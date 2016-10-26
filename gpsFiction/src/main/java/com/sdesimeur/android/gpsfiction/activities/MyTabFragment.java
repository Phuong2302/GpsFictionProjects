package com.sdesimeur.android.gpsfiction.activities;

import android.app.Fragment;
import android.view.View;


public abstract class MyTabFragment extends Fragment implements MyTabFragmentImpl {
    private View rootView = null;
    private GpsFictionActivity gpsFictionActivity = null;

    public MyTabFragment() {
        super();
    }


    public GpsFictionActivity getGpsFictionActivity() {
        return this.gpsFictionActivity;
    }

    public View getRootView() {
        return this.rootView;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
    }

    public void register(GpsFictionActivity gpsFictionActivity) {
        this.gpsFictionActivity = gpsFictionActivity;
    }
}
