package example.josseph.com.wsserver;

import android.app.Fragment;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class MainActivity extends AppCompatActivity {

    TextView textIp,textPuerto ,mensajes;
    EditText intro;
    private static final String TAG = "Websocket";
    static final int SERVER_PORT = 8080;
    private ServidorSocket mServidor;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        iniciarServidor();

        textIp=(TextView)findViewById(R.id.textIp);
        textPuerto= (TextView)findViewById(R.id.textPuerto);
        mensajes=(TextView)findViewById(R.id.messages);
        intro = (EditText)findViewById(R.id.editText);
        acceso_Ip();
        textPuerto.setText("8080");
    }


    private void iniciarServidor(){
        InetAddress inetAddress = getInetAddress();
        //InetAddress nos sirve para representar una dirección IP
        if (inetAddress == null) {
            // Toast.makeText(this,"No se encuentra direccion IP",Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No se encuentra direccion IP");
            return;
        }

      /* Thread t= new Thread(){
            public void run () {
                int SERVER_PORT =5125 ;
                InetAddress inetAddress = getInetAddress();
                try{
                    mServidor = new ServidorSocket(new InetSocketAddress(inetAddress.getHostAddress(), SERVER_PORT));
                    //getHostAddress(): retorna en cadena de texto la direccion IP
                    mServidor.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();*/
        mServidor = new ServidorSocket(new InetSocketAddress(inetAddress.getHostAddress(), SERVER_PORT));
        // InetSocketAddress : genera una Uri con inetAddress.getHostAddress() + SERVER_PORT
        //getHostAddress(): retorna en cadena de texto la direccion IP
        mServidor.start();
    }

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
                    //getInetAddresses: Método de conveniencia para volver una Enumeration con todos o un subconjunto de los InetAddresses
                    // unidos a esta interfaz de red.
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


    private void acceso_Ip (){
        textIp.setText(capturar_Ip());
    }

    private String capturar_Ip(){

       /* InetAddress inetAddress = getInetAddress();
        String formato_ip= inetAddress.getHostAddress();
        return formato_ip;*/

        WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        int direccion_Ip = wifiManager.getConnectionInfo().getIpAddress();
        final String formato_Ip= String.format("%d.%d.%d.%d", (direccion_Ip & 0xff), (direccion_Ip >> 8 & 0xff),
                (direccion_Ip >> 16 & 0xff), (direccion_Ip >> 24 & 0xff));
        return "http://"+ formato_Ip +":";
    }

    public void mostrarMensajes (String s){
        //Log.d("WebSocket", "Yo: "+ s);
      mensajes.setText(mensajes.getText() + "\n" + s);
    }

    public void sendMessage(View view) {
       EditText editText = (EditText)findViewById(R.id.editText);
       String s = editText.getText().toString();
        mServidor.enviarMensaje("Servidor: " + s);
        mostrarMensajes("Servidor: " + s);
        editText.setText("");
    }



}
