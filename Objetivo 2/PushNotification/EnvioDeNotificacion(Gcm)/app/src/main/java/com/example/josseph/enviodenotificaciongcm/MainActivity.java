package com.example.josseph.enviodenotificaciongcm;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private class mWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("http://basededatosuv.gzpot.com/gcm/sendNotification.php")) {
                return false;  //Don't override since it's the same host
            } else {
                return true; //Stop the navigation since it's a different site
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webview = new WebView(this);
        setContentView(webview);
        webview.loadUrl("http://basededatosuv.gzpot.com/gcm/sendNotification.php");
        webview.setWebViewClient(new WebViewClient());

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //webSettings.setBuiltInZoomControls(true);
    }
}
