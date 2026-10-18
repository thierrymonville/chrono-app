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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final String MAC_BLEU = "EE:ED:68:E1:35:89"; // Départ
    private static final String MAC_ROUGE = "ED:ED:4D:E5:2B";   // Fin
    private static final String URL_RPI = "http://192.168.1.X/chrono/index.php";

    private TextView txtStatus, txtChrono;
    private WebView webView;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt gattBleu, gattRouge;
    private long startTime = 0;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtStatus = findViewById(R.id.txtStatus);
        txtChrono = findViewById(R.id.txtChrono);
        Button btnConnect = findViewById(R.id.btnConnect);
        webView = findViewById(R.id.webView);

        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(URL_RPI);

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        btnConnect.setOnClickListener(v -> checkPermissionsAndConnect());
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
        connectDevices();
    }

    private void connectDevices() {
        if (bluetoothAdapter == null) return;

        txtStatus.setText("Statut : Connexion en cours...");

        BluetoothDevice deviceBleu = bluetoothAdapter.getRemoteDevice(MAC_BLEU);
        BluetoothDevice deviceRouge = bluetoothAdapter.getRemoteDevice(MAC_ROUGE);

        @SuppressLint("MissingPermission")
        BluetoothGattCallback callbackBleu = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    runOnUiThread(() -> txtStatus.setText("Bleu (Départ) Connecté"));
                    startChrono();
                }
            }
        };

        @SuppressLint("MissingPermission")
        BluetoothGattCallback callbackRouge = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    runOnUiThread(() -> txtStatus.setText("Rouge (Fin) Connecté"));
                    stopChrono();
                }
            }
        };

        gattBleu = deviceBleu.connectGatt(this, false, callbackBleu);
        gattRouge = deviceRouge.connectGatt(this, false, callbackRouge);
    }

    private void startChrono() {
        startTime = System.currentTimeMillis();
        runOnUiThread(() -> txtChrono.setText("CHRONO LANCÉ !"));
    }

    private void stopChrono() {
        long elapsed = System.currentTimeMillis() - startTime;
        long seconds = (elapsed / 1000) % 60;
        long millis = (elapsed % 1000) / 10;
        long minutes = (elapsed / (1000 * 60)) % 60;
        String timeStr = String.format("%02d:%02d.%02d", minutes, seconds, millis);
        runOnUiThread(() -> txtChrono.setText(timeStr));
    }
}
