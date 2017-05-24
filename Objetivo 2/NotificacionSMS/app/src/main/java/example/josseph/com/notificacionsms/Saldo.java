package example.josseph.com.notificacionsms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class Saldo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        WebView myWebView = (WebView) this.findViewById(R.id.webView2);
        myWebView.loadUrl("https://sistemasmasivos.com/itcloud/api/sendsms/query.php?user="+"josseph_14@hotmail.com"+
                "&password="+"IYeUUUabC0");
    }
}
