package com.sdesimeur.android.gpsfiction.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdesimeur.android.gpsfiction.activities.R;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;
import com.sdesimeur.android.gpsfiction.views.CompassView;
import com.sdesimeur.android.gpsfiction.views.ZoneDistance4CompassView;
import com.sdesimeur.android.gpsfiction.views.ZoneName4CompassView;


@SuppressWarnings("unused")
public class CompassFragment extends MyTabFragment {
    private ZoneDistance4CompassView textviewDistance = null;
    private ZoneName4CompassView textviewName = null;
    private CompassView compassView = null;

    public CompassFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRootView(inflater.inflate(R.layout.compass_view, container, false));
        textviewName = (ZoneName4CompassView) (getRootView().findViewById(R.id.textNameOfZone));
        textviewName.setTag(R.id.gpsFictionControlerId,getmGpsFictionControler());
        textviewDistance = (ZoneDistance4CompassView) (getRootView().findViewById(R.id.textDistance));
        textviewDistance.setTag(R.id.gpsFictionControlerId,getmGpsFictionControler());
        compassView = (CompassView) (getRootView().findViewById(R.id.compassDirection));
        compassView.setTag(R.id.gpsFictionControlerId,getmGpsFictionControler());
        compassView.setTypeface(getmGpsFictionActivity().getFontFromRes(R.raw.font_dancing));
        GpsFictionControler gfc = getmGpsFictionActivity().getmGpsFictionControler();
        gfc.addPlayerLocationListener(GpsFictionControler.REGISTER.VIEW,CompassFragment.this.textviewDistance);
        return getRootView();
    }
    @Override
    public void onDestroy() {
        GpsFictionControler gfc = getmGpsFictionActivity().getmGpsFictionControler();
        gfc.removePlayerLocationListener(GpsFictionControler.REGISTER.VIEW,CompassFragment.this.textviewDistance);
        super.onDestroy();
    }
}