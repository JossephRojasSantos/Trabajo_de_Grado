package example.josseph.com.notificacionsms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

public class EnvioSMS extends AppCompatActivity {

    TextView url;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        bundle = getIntent().getExtras();
        String s= bundle.getString("DATO");
        String t = bundle.getString("DATO2");

        url= (TextView)findViewById(R.id.textView3);

        WebView myWebView = (WebView) this.findViewById(R.id.webView);
        myWebView.loadUrl("https://sistemasmasivos.com/itcloud/api/sendsms/send.php?user="+"josseph_14@hotmail.com"+
                "&password="+"IYeUUUabC0"+"&GSM="+s+"&SMSText="+t);

        url.setText("https://sistemasmasivos.com/itcloud/api/sendsms/send.php?user="+"josseph_14@hotmail.com"+
                "&password="+"IYeUUUabC0"+"&GSM="+s+"&SMSText="+t);
    }
}
