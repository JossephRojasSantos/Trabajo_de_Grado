package example.josseph.com.notificacionsms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText numero, sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numero=(EditText)findViewById(R.id.editText);
        sms=(EditText)findViewById(R.id.editText2);

    }
    public void enviar(View v){
        Intent i= new Intent (this,EnvioSMS.class);
        String dato = numero.getText().toString();
        String dato2= sms.getText().toString();
        i.putExtra("DATO",dato);
        i.putExtra("DATO2",dato2);
        startActivity(i);

    }
    public void consultar(View v){
        Intent i= new Intent (this,Saldo.class);
        startActivity(i);

    }
}
