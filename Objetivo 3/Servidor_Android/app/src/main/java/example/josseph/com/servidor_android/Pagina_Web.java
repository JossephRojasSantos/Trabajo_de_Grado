package example.josseph.com.servidor_android;

import android.util.Log;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by josse on 23/09/2016.
 */

public class Pagina_Web extends NanoHTTPD {

    public Pagina_Web(int puerto) {
        super(puerto);
    }


    public Response serve(IHTTPSession session) {
        String msg = "<html><body><h1>Hola! (Servidor Android) </h1>\n";
        final String TAG2 = "Datos del Cliente";

        Map<String, String> parms = session.getParms();
        if (parms.get("nombre") == null && parms.get("apellido") == null) {
            msg += "<form action='?' method='get'>" +
                    "<p>Su nombre: <input type='text' name='nombre'></p>"+
                    "<p>Su apellido: <input type='text' name='apellido'></p>"+
                    "<input type=\"submit\" value=\"Submit\">"+
                    "</form>";

        } else {

            msg += "<p>Hola, " + parms.get("nombre") + " " +parms.get("apellido")+ "!</p>";
            Log.d(TAG2, "Hola, "+ parms.get("nombre") + " " +parms.get("apellido"));
        }
        return newFixedLengthResponse( msg + "</body></html>\n" );

    }
}
