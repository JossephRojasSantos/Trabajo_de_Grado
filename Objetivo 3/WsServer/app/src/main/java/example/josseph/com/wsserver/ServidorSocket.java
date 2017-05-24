package example.josseph.com.wsserver;

import android.util.Log;
import android.widget.TextView;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;



import static android.content.ContentValues.TAG;

/**
 * Created by josse on 6/10/2016.
 */
public class ServidorSocket extends WebSocketServer{

    private WebSocket mSocket;
    MainActivity mainActivity;

    public ServidorSocket(InetSocketAddress direccion){
        super(direccion);
    }

    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        mSocket = conn;
        Log.i("WebSocket", "onOpen "+ handshake);
    }
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    }

    @Override
    public void onMessage(WebSocket conn, final String message) {
        Log.d("WebSocket", "Cliente: " + message);
/*
       Thread t = new Thread() {
           public void run() {
             final   String s = message;
               mainActivity.runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       mainActivity.mostrarMensajes(s);
                   }
               });
               //mainActivity.mostrarMensajes("Hola");
           }
       }; t.start();
    */
        }

    @Override
    public void onError(WebSocket conn, Exception ex) {
    }

    public void enviarMensaje(final String mensaje) {
        Log.d("WebSocket", "Yo: " + mensaje);
        // Log.i(TAG, "Saliendo evento "+ mensaje);
        mSocket.send(mensaje);
    }
}
