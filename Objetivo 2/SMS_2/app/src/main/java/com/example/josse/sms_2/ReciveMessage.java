package com.example.josse.sms_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by josse on 22/05/2017.
 */

public class ReciveMessage extends BroadcastReceiver {


    final SmsManager mysms= SmsManager.getDefault();
    static int id=1;

    public void onReceive(Context context, Intent intent) {
        Bundle mybundle= intent.getExtras();
        try {
            if (mybundle!= null){
                final Object [] meseaageContent= (Object[])mybundle.get("pdus");
                for (int i = 0; i<meseaageContent.length ; i++){
                    SmsMessage mynewsms = SmsMessage.createFromPdu((byte[])  meseaageContent[i]);

                    String numero_Servidor= "3166235026";
                    String numero_Entrante= mynewsms.getOriginatingAddress();
                    Log.d("BroadcastReceiver","Numero del servidor: "+numero_Servidor);
                    Log.d("BroadcastReceiver","Numero entrante: "+ numero_Entrante);


                    if(numero_Entrante.equals(numero_Servidor)){
                        Log.d("BroadcastReceiver","Se ejecuta la notificacion");
                        NewMessageNotification nome = new NewMessageNotification();
                        nome.notify(context, mynewsms.getDisplayOriginatingAddress(),mynewsms.getDisplayMessageBody(),id);
                        id++;}
                    else {
                        Log.d("BroadcastReceiver","No es el servidor");
                    }
                }
            }
        }catch (Exception ex){

        }
    }
}
