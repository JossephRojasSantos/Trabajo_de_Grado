package com.example.josseph.gcmpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            // check type of intent filter
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().endsWith(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //registration success
                    String token = intent.getStringExtra("token");
                    Toast.makeText(getApplicationContext(), "GCM token: " +token , Toast.LENGTH_LONG).show();
                }else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    //registration error
                    Toast.makeText(getApplicationContext(), "GCM  regitration error" , Toast.LENGTH_LONG).show();
                }else {
                    // to be define
                }
            }
        };
        //check status of google play service in device

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(ConnectionResult.SUCCESS!= resultCode){
            //check type of error
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                Toast.makeText(getApplicationContext(), "Google Play Services is not install/enabled in this device!" , Toast.LENGTH_LONG).show();
                //so notification
                GooglePlayServicesUtil.showErrorNotification(resultCode,getApplication());
            }else{
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!" , Toast.LENGTH_LONG).show();
            }
        }else {
            //star service
            Intent i = new Intent(this , GCMRegistrationIntentService.class);
            startService(i);
        }
    }
    protected void onResume(){
        super.onResume();
        Log.v("MainActivity","onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity","onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);

    }

}
