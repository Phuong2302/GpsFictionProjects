package com.sdesimeur.android.gpsfiction.activities;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;


public abstract class MyTabFragment extends Fragment {
    private int nameId;
    private View rootView = null;
    private GpsFictionActivity gpsFictionActivity = null;

    public MyTabFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.gpsFictionActivity = (GpsFictionActivity) activity;
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

    /*public int getNameId () {
        return this.nameId;
    }
    public void setNameId (int nameId) {
        this.nameId=nameId;
    }*/
    @Override
    public void onDestroyView() {
        super.onDestroy();
        //this.unRegister();
    }

    public void register(GpsFictionActivity gpsFictionActivity) {
        this.gpsFictionActivity = gpsFictionActivity;
        //this.getGpsFictionActivity().getGpsFictionData().addZoneSelectListener(GpsFictionData.REGISTER.FRAGMENT,this);
        //this.getGpsFictionActivity().getMyLocationListener().addPlayerLocationListener(MyLocationListener.REGISTER.FRAGMENT,this);
        //this.getGpsFictionActivity().getMyLocationListener().addPlayerBearingListener(MyLocationListener.REGISTER.FRAGMENT,this);
    }
    //public void unRegister() {
    //this.getGpsFictionActivity().getGpsFictionData().removeZoneSelectListener(GpsFictionData.REGISTER.FRAGMENT,this);
    //this.getGpsFictionActivity().getMyLocationListener().removePlayerLocationListener(MyLocationListener.REGISTER.FRAGMENT,this);
    //this.getGpsFictionActivity().getMyLocationListener().removePlayerBearingListener(MyLocationListener.REGISTER.FRAGMENT,this);
    //}
}
