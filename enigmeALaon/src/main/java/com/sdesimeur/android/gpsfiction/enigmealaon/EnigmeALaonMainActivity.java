package com.sdesimeur.android.gpsfiction.enigmealaon;

import android.os.Bundle;

import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;

/**
 * Created by sam on 06/09/15.
 */
public class EnigmeALaonMainActivity extends GpsFictionActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( ! ( getmGpsFictionData().isAllreadyConfigured() ) ) {
            setResourcedZones(R.raw.enigmealaon);
//            getmMyLocationListenerService().firePlayerLocationListener();
            getmGpsFictionData().setAllreadyConfigured(true);
        }
    }
}
