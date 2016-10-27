package com.sdesimeur.android.gpsfiction.activities;

import android.app.Fragment;
import android.view.View;

import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListener;


public abstract class MyTabFragment extends Fragment implements MyTabFragmentImpl {
    private View rootView = null;
    private GpsFictionData mGpsFictionData = null;

    public MyTabFragment() {
        super();
    }


    public GpsFictionData getmGpsFictionData() {
        return mGpsFictionData;
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

    public void register(GpsFictionData gpsFictionData) {
        mGpsFictionData = gpsFictionData;
    }
    public GpsFictionActivity getmGpsFictionActivity() {
        return (GpsFictionActivity) getActivity();
    }
    public MyLocationListener getmMyLocationListener() {
        return getmGpsFictionData().getmMyLocationListener();
    }
}
