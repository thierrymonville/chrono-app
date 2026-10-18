package com.chrono.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final String MAC_BLEU = "EE:ED:68:E1:35:89";  // Départ
    private static final String MAC_ROUGE = "ED:ED:4D:E5:2B";    // Fin
    private static final String URL_RPI = "http://192.168.1.X/chrono/index.php"; 

    private WebView webView;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt gattBleu, gattRouge;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(URL_RPI);

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        // Vérifie les permissions et lance la connexion aux adresses MAC
        checkPermissionsAndConnect();
    }

    private void checkPermissionsAndConnect() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 1);
            return;
        }
        connectBleDevices();
    }

    @SuppressLint("MissingPermission")
    private void connectBleDevices() {
        if (bluetoothAdapter == null) return;

        // Récupération des appareils par leur adresse MAC respective
        if (BluetoothAdapter.checkBluetoothAddress(MAC_BLEU)) {
            BluetoothDevice deviceBleu = bluetoothAdapter.getRemoteDevice(MAC_BLEU);
            gattBleu = deviceBleu.connectGatt(this, false, new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        notifyWebPage("start"); // Bouton Bleu -> Départ
                    }
                }
            });
        }

        if (BluetoothAdapter.checkBluetoothAddress(MAC_ROUGE)) {
            BluetoothDevice deviceRouge = bluetoothAdapter.getRemoteDevice(MAC_ROUGE);
            gattRouge = deviceRouge.connectGatt(this, false, new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        notifyWebPage("stop"); // Bouton Rouge -> Fin
                    }
                }
            });
        }
    }

    private void notifyWebPage(String action) {
        if (webView != null) {
            runOnUiThread(() -> {
                webView.evaluateJavascript("javascript:if(typeof onChronoTrigger === 'function'){ onChronoTrigger('" + action + "'); }", null);
                Toast.makeText(this, "MAC Trigger -> " + action.toUpperCase(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        @SuppressLint("MissingPermission")
        if (gattBleu != null) { gattBleu.close(); gattBleu = null; }
        @SuppressLint("MissingPermission")
        if (gattRouge != null) { gattRouge.close(); gattRouge = null; }
    }
}
