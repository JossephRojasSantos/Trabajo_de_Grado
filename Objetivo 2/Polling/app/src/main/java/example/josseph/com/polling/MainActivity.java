package example.josseph.com.polling;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


     ListView listado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listado = (ListView) findViewById(R.id.listView1);
        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe);
        refreshLayout.setColorScheme(android.R.color.holo_blue_dark);

        //widget para refrescar la actividad principal
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("Polling","Accion refresh ejecutada");
                refreshLayout.setRefreshing(true);
              // la funcion refresh contiene el ciclo el cual se refresca cada t segundos
                refresh();
                refreshLayout.setRefreshing(false);
            }
        });

        Thread tr = new Thread(){
            @Override
            public void run(){
                runOnUiThread(new Runnable() {
                    String Resultado = leer();
                    @Override
                    public void run() {
                        cargaListado(obtDatosJSON(Resultado));
                    }
                });
            }
        };
        // Busca la informacion apenas inicia la apk
        tr.start();
    }
    // Ciclo
   private void refresh() {
        Timer waitingTimer = new Timer();
        waitingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Thread tr = new Thread() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            String Resultado = leer();
                            public void run() {
                                cargaListado(obtDatosJSON(Resultado));
                                listado.invalidateViews();

                            }
                        });
                    }
                };
                tr.start();
            }
        },0, 8000);
    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listado, menu);
        return true;
    }

    public void cargaListado(ArrayList<String> datos){
        Log.d("Polling","Cargando datos en interfaz");
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,datos);
        listado.setAdapter(adaptador);
    }

    public String leer(){
        Log.d("Polling","En busqueda de datos");
        HttpClient cliente =new DefaultHttpClient();
        HttpContext contexto = new BasicHttpContext();

        HttpGet httpget = new HttpGet("https://josseph-14.000webhostapp.com/soldist.php");
        String resultado=null;
        try {
            HttpResponse response = cliente.execute(httpget,contexto);
            HttpEntity entity = response.getEntity();
            resultado = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            // TODO: handle exception
        }
        return resultado;
    }

    public ArrayList<String> obtDatosJSON(String response){
        ArrayList<String> listado= new ArrayList<String>();
        Log.d("Polling","Obtuvo datos");
        try {
            JSONArray json= new JSONArray(response);
            String texto="";
            for (int i=0; i<json.length();i++){
                texto = json.getJSONObject(i).getString("nombre") +" - "+
                        json.getJSONObject(i).getString("apellido") +" - "+
                        json.getJSONObject(i).getString("edad") ;
                listado.add(texto);
            }
        } catch (Exception e) {
            }

        return listado;
    }
}
