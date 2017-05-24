package com.example.josseph.wifi_direct;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        ChannelListener,OnClickListener,PeerListListener,ConnectionInfoListener{

    private WifiP2pManager manager;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private Button buscar;
    private Button conectar;
    private WifiP2pDevice device;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);


        this.conectar = (Button) this.findViewById(R.id.conectar);
        this.conectar.setOnClickListener(this);

        this.buscar = (Button)this.findViewById(R.id.buscar);
        this.buscar.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onChannelDisconnected() {

    }

    @Override
    public void onClick(View v) {
        if(v == conectar)
        {
            Toast.makeText(MainActivity.this, "conectando a " + device.deviceAddress + device.deviceName ,
                    Toast.LENGTH_SHORT).show();
            connect(this.device);
        }
        else if(v == buscar)
        {
            find();
        }

    }

    public void connect( WifiP2pDevice device)    {
        WifiP2pConfig config = new WifiP2pConfig();
        if(device != null)
        {
            Log.d("connect", "conectando a " + device.deviceAddress + device.deviceName);

            config.deviceAddress = device.deviceAddress;
            manager.connect(channel, config, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Log.d("connect", "conectado");
                }

                @Override
                public void onFailure(int reason) {
                    //fail
                }
            });
        }
        else
        {
            Toast.makeText(MainActivity.this, "No se pudo conectar, no se encuentra el dispositivo",
                    Toast.LENGTH_SHORT).show();
            Log.d("connect", "No se pudo conectar, no se encuentra el dispositivo");
        }
    }

    public void find() {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            Toast.makeText(MainActivity.this, "Buscando Dispositivos",Toast.LENGTH_SHORT).show();
                Log.d("discoverPeers", "Buscando Dispositivos");
            }
            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(MainActivity.this, "No se encuentran Dispositivos ",Toast.LENGTH_SHORT).show();
                Log.d("discoverPeers", "No se encuentran Dispositivos");
            }
        });
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {

        for (WifiP2pDevice device : peerList.getDeviceList()) {

            Log.d("Direccion: ", device.deviceAddress);
            Log.d("Direccion: ", device.deviceName);
            this.device = device;
            break;

        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        String infoname = info.groupOwnerAddress.toString();

    }
}
