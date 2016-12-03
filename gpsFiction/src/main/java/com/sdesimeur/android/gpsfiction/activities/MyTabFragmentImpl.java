package com.sdesimeur.android.gpsfiction.activities;

import android.view.View;

import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;

/**
 * Created by sam on 30/06/16.
 */
public interface MyTabFragmentImpl {
    public View getRootView();
    public void setRootView(View rootView);
    public GpsFictionControler getmGpsFictionControler();
    public GpsFictionActivity getmGpsFictionActivity();
}
