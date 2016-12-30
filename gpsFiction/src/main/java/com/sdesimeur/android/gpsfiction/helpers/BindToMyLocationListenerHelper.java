package com.sdesimeur.android.gpsfiction.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.sdesimeur.android.gpsfiction.activities.MyLocationListenerService;

/**
 * Created by sam on 20/11/16.
 */

public abstract class BindToMyLocationListenerHelper {

    public boolean isBoundToMyLocationListenerService() {
        return isBoundToMyLocationListenerService;
    }

    private boolean isBoundToMyLocationListenerService = false;
    private MyLocationListenerService mMyLocationListenerService;
    private ServiceConnection serviceConnectionToMyLocationListenerService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyLocationListenerService.MyBinder binder = (MyLocationListenerService.MyBinder) service;
            mMyLocationListenerService = binder.getService();
            isBoundToMyLocationListenerService = true;
            onBindWithMyLocationListener(mMyLocationListenerService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            onUnBindWithMyLocationListener();
            mMyLocationListenerService = null;
            isBoundToMyLocationListenerService = false;
        }
    };
    private Context context;
    public BindToMyLocationListenerHelper (Context ct) {
        if (!isBoundToMyLocationListenerService) {
            context = ct;
            Intent myIntent2 = new Intent(context, MyLocationListenerService.class);
            myIntent2.setAction(MyLocationListenerService.ACTION.STARTFOREGROUND);
            context.bindService(myIntent2, serviceConnectionToMyLocationListenerService, Context.BIND_AUTO_CREATE);
        }
    }
    protected abstract void onBindWithMyLocationListener(MyLocationListenerService mlls);
    public void onUnBindWithMyLocationListener() {
        if (isBoundToMyLocationListenerService)
            context.unbindService(serviceConnectionToMyLocationListenerService);
    }


}
