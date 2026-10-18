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

    private static final String URL_RPI = "http://192.168.0.151:8888/index.php"; 
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PLAY_PAUSE) {
            
            if (!isChronoRunning) {
                isChronoRunning = true;
                notifyWebPage("start");
            } else {
                isChronoRunning = false;
                notifyWebPage("stop");
            }
            
            return true; // Bloque l'action native du volume sur le téléphone
        }
        return super.onKeyDown(keyCode, event);
    }

    private void notifyWebPage(String action) {
        if (webView != null) {
            runOnUiThread(() -> {
                webView.evaluateJavascript("javascript:if(typeof window.onChronoTrigger === 'function'){ window.onChronoTrigger('" + action + "'); }", null);
                Toast.makeText(this, "Chrono -> " + action.toUpperCase(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
