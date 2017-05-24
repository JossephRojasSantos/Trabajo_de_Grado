package example.josseph.com.servidor_android;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class MainActivity extends AppCompatActivity {

    private EditText textPuerto;
    private TextView textIp;
    private static final int DEFAULT_PORT = 8080; // Definimos puerto por defecto
    private Pagina_Web pagina_web;
    private BroadcastReceiver estado_red;
    private static boolean estado=false;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            textPuerto = (EditText) findViewById(R.id.textPuerto);
            textIp= (TextView) findViewById(R.id.textIp);
            acceso_Ip();
            initBroadcastReceiverNetworkStateChanged();

        }

    public void inicio (View view){
       final String TAG2 = "Servidor: ";

        if (conectado_wifi()){
            if (!estado && iniciar_servidor()){
                estado=true;
                textPuerto.setEnabled(false);
                Toast toast = Toast.makeText(MainActivity.this,"Servidor iniciado",Toast.LENGTH_SHORT);
                 toast.show();
                Log.d(TAG2, "Iniciado");


            } else if (parar_servidor()){
                textPuerto.setEnabled(true);
                estado=false;
                Toast toast = Toast.makeText(MainActivity.this,"Servidor parado",Toast.LENGTH_SHORT);
                toast.show();
                Log.d(TAG2, "parado");

            }
        }else{
            Toast toast = Toast.makeText(MainActivity.this,"Debe conectarse a una Red Wi-Fi",Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void acceso_Ip (){
        textIp.setText(capturar_Ip());
    }

    private String capturar_Ip(){

        /*InetAddress inetAddress = getInetAddress();
        String formato_ip= inetAddress.getHostAddress();
        return "http://"+ formato_Ip +":";*/

        WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        int direccion_Ip = wifiManager.getConnectionInfo().getIpAddress();
        final String formato_Ip= String.format("%d.%d.%d.%d", (direccion_Ip & 0xff), (direccion_Ip >> 8 & 0xff),
                (direccion_Ip >> 16 & 0xff), (direccion_Ip >> 24 & 0xff));
        return "http://"+ formato_Ip +":";
    }
/*
    private static InetAddress getInetAddress(){
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                // Enumeration : llamadas sucesivas a una serie de elementos
                //getNetworkInterfaces: retorna todas las interfaces del dispositivo
                // hasMoreElements : comprueba si existen mas elementos
                NetworkInterface networkInterface = (NetworkInterface) en.nextElement();
                //nextElement: retorna el siguiente elemento
                //NetworkInterface: Proporciona información de configuración y estadística para una interfaz de red.
                for (Enumeration enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    //getInetAddresses: Método de conveniencia para volver una Enumeration con todos o un subconjunto de los InetAddresses unidos a esta interfaz de red.
                    //InetAddress: representa una direccion de protocolo de internet
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress;
                        // isLoopbackAddress : rutina de utilidad para comprobar si el InetAddress es una dirección de enlace local.
                        //Inet4Address: representa el protocolo de internet en version 4
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            Log.e(TAG, "Error al obtener la información de interfaz de red");
        }

        return null;
    }
*/
    public boolean conectado_wifi(){
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()
                && wifiManager.isWifiEnabled() && networkInfo.getTypeName().equals("WIFI")) {
            return true;
        }
        return false;
    }

    private int capturar_puerto (){
        String valor = textPuerto.getText().toString();
        return (valor.length() > 0) ? Integer.parseInt(valor) : DEFAULT_PORT;
    }

    private boolean iniciar_servidor(){
        if (!estado){
            Thread t= new Thread(){
                public void run () {
                    int puerto = 5125;
                    String TAG2 = "Servidor: ";
                    Log.d(TAG2, "Puerto: " + puerto);

                    try{
                        if (puerto==0){throw new Exception();}
                        pagina_web= new Pagina_Web(puerto);
                        pagina_web.start();
                       } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            };
            t.start();
            Thread x= new Thread(){
                public void run () {
                    int puerto = 1512;
                    String TAG2 = "Servidor: ";
                    Log.d(TAG2, "Puerto: " + puerto);
                    try{
                        if (puerto==0){throw new Exception();}
                        pagina_web= new Pagina_Web(puerto);
                        pagina_web.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            x.start();

            int puerto = capturar_puerto();
            String TAG2 = "Servidor: ";
            Log.d(TAG2, "Puerto: " + puerto);
            try{
                if (puerto==0){throw new Exception();}
                pagina_web= new Pagina_Web(puerto);
                pagina_web.start();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "No se tiene acceso al puerto "+puerto+
                        " ó el Servidor ya se encuentra en servicio",Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    private boolean parar_servidor(){
        if (estado && pagina_web!=null){
            pagina_web.stop();
            return true;
        }
        return false;
    }

    private void initBroadcastReceiverNetworkStateChanged() {
        final IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filters.addAction("android.net.wifi.STATE_CHANGE");
        estado_red = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                acceso_Ip();
            }
        };
        super.registerReceiver(estado_red, filters);
    }

    protected void onDestroy() {

        parar_servidor();
        estado = false;
        if (estado_red != null) {
            unregisterReceiver(estado_red);
        }
        super.onDestroy();
    }

}
