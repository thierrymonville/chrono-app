package com.chrono.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String URL_RPI = "http://192.168.1.X/chrono/index.php"; 
    private WebView webView;
    private boolean isChronoRunning = false;

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
    }

    // Intercepte les signaux clavier/volume envoyés par les boutons Bluetooth Amathings
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            
            // Mode bascule automatique : 1er appui = Départ, appui suivant = Fin
            if (!isChronoRunning) {
                isChronoRunning = true;
                notifyWebPage("start");
            } else {
                isChronoRunning = false;
                notifyWebPage("stop");
            }
            
            return true; // Empêche le volume du téléphone de varier
        }
        return super.onKeyDown(keyCode, event);
    }

    private void notifyWebPage(String action) {
        if (webView != null) {
            webView.evaluateJavascript("javascript:if(typeof onChronoTrigger === 'function'){ onChronoTrigger('" + action + "'); }", null);
            Toast.makeText(this, "Chrono -> " + action.toUpperCase(), Toast.LENGTH_SHORT).show();
        }
    }
}
