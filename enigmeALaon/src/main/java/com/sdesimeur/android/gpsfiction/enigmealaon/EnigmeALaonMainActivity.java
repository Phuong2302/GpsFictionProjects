package com.sdesimeur.android.gpsfiction.enigmealaon;

import com.sdesimeur.android.gpsfiction.enigmealaon.R;
import android.os.Bundle;

import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;

/**
 * Created by sam on 06/09/15.
 */
public class EnigmeALaonMainActivity extends GpsFictionActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( ! ( this.getGpsFictionData().isAllreadyConfigured() ) ) {
            setResourcedZones(R.raw.enigmealaon);
            this.getMyLocationListener().firePlayerLocationListener();
            this.getGpsFictionData().setAllreadyConfigured(true);
        }
    }
}
