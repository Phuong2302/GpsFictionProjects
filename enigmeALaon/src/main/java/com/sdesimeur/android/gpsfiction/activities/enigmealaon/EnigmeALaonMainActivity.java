package com.sdesimeur.android.gpsfiction.activities.enigmealaon;

import android.os.Bundle;

import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;

/**
 * Created by sam on 06/09/15.
 */
public class EnigmeALaonMainActivity extends GpsFictionActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( ! ( getmGpsFictionControler().isAllreadyConfigured() ) ) {
            getmGpsFictionControler().setResourcedZones(R.raw.enigmealaon);
//            getmMyLocationListenerService().firePlayerLocationListener();
            getmGpsFictionControler().setAllreadyConfigured(true);
        }
    }
}
