package com.sdesimeur.android.gpsfiction.activities;

import android.view.View;

import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListener;

/**
 * Created by sam on 30/06/16.
 */
public interface MyTabFragmentImpl {
    public View getRootView();
    public void setRootView(View rootView);
    public GpsFictionData getmGpsFictionData();
    public GpsFictionActivity getmGpsFictionActivity();
    public MyLocationListener getmMyLocationListener();
}
