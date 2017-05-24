package com.example.josseph.gcmpush;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by josseph on 6/06/16.
 */
public class GCMTokenRefreshListenerService extends InstanceIDListenerService{
    public void onTokenRefresh(){
        Intent intent = new Intent(this,GCMRegistrationIntentService.class);
        startActivity(intent);
    }
}
