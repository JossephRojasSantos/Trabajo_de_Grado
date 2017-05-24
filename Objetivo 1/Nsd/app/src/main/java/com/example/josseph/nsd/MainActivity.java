package com.example.josseph.nsd;

import android.app.Activity;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    Nsd mNsd;
    Connection mConnection;

    TextView texto;
    private Handler mUpdateHandler;
    public static final String TAG = "Nsd";



    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        texto = (TextView) findViewById(R.id.status);

        mUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String chat = msg.getData().getString("msg");
                texto.setText(texto.getText() + "\n" + chat);


            }
        };

    }

    public void Registrar(View v) {
        // Register service
        if(mConnection.getLocalPort() > -1) {
            mNsd.registerService(mConnection.getLocalPort());
        } else {
            Log.d(TAG, "ServerSocket no esta vinculado");
        }
    }

    public void Descubrir(View v) {
        mNsd.discoverServices();
    }

    public void Conectar(View v) {
        NsdServiceInfo service = mNsd.getChosenServiceInfo();
        if (service != null) {
            Log.d(TAG, "Conectando");
            mConnection.connectToServer(service.getHost(),service.getPort());
        } else {
            Log.d(TAG, "Servicio no conectado!");
        }
    }

    public void Enviar(View v) {
        EditText messageView = (EditText) this.findViewById(R.id.chatInput);
        if (messageView != null) {
            String messageString = messageView.getText().toString();
            if (!messageString.isEmpty()) {
                mConnection.sendMessage(messageString);
            }
            messageView.setText("");
        }
    }
    @Override
    protected void onStart() {
        Log.d(TAG, "Starting.");
        mConnection = new Connection(mUpdateHandler);

        mNsd = new Nsd(this);
        mNsd.initializeNsd();
        super.onStart();
    }
    @Override
    protected void onPause() {
        Log.d(TAG, "Pausing.");
        if (mNsd != null) {
            mNsd.stopDiscovery();
        }
        super.onPause();
    }
    @Override
    protected void onResume() {
        Log.d(TAG, "Resuming.");
        super.onResume();
        if (mNsd != null) {
            mNsd.discoverServices();
        }
    }
    @Override
    protected void onStop() {
        Log.d(TAG, "Being stopped.");
        mNsd.tearDown();
        mConnection.tearDown();
        mNsd = null;
        mConnection = null;
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        Log.d(TAG, "Being destroyed.");
        super.onDestroy();
    }
}

