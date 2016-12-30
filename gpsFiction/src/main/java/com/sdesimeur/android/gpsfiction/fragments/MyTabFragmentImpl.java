package com.sdesimeur.android.gpsfiction.fragments;

import android.view.View;

import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;

/**
 * Created by sam on 30/06/16.
 */
public interface MyTabFragmentImpl {
    public View getRootView();
    public void setRootView(View rootView);
    public GpsFictionData getmGpsFictionData();
    public GpsFictionControler getmGpsFictionControler();
    public GpsFictionActivity getmGpsFictionActivity();
}
