package com.sdesimeur.android.gpsfiction.fragments;

import android.app.Fragment;
import android.view.View;

import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;


public abstract class MyTabFragment extends Fragment implements MyTabFragmentImpl {
    private View rootView = null;

    public MyTabFragment() {
        super();
    }


    public GpsFictionData getmGpsFictionData() {
        if (getmGpsFictionControler()==null)
            return null;
        else
            return getmGpsFictionControler().getmGpsFictionData();
    }
    public GpsFictionControler getmGpsFictionControler() {
        if (getmGpsFictionActivity()==null)
            return null;
        else
            return getmGpsFictionActivity().getmGpsFictionControler();
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

    public GpsFictionActivity getmGpsFictionActivity() {
        return (GpsFictionActivity) getActivity();
    }
}
