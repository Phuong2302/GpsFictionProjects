package com.sdesimeur.android.gpsfiction.activities;

import android.view.View;

/**
 * Created by sam on 30/06/16.
 */
public interface MyTabFragmentImpl {
    public View getRootView();
    public GpsFictionActivity getGpsFictionActivity();
    public void setRootView(View rootView);
    public void register(GpsFictionActivity gpsFictionActivity);
}
