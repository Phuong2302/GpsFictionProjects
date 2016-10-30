package com.sdesimeur.android.gpsfiction.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdesimeur.android.gpsfiction.R;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRootView(inflater.inflate(R.layout.compass_view, container, false));
        textviewName = (ZoneName4CompassView) (getRootView().findViewById(R.id.textNameOfZone));
        textviewName.init(getmGpsFictionActivity());
        textviewDistance = (ZoneDistance4CompassView) (getRootView().findViewById(R.id.textDistance));
        textviewDistance.init(getmGpsFictionActivity());
        compassView = (CompassView) (getRootView().findViewById(R.id.compassDirection));
        compassView.init(getmGpsFictionActivity());
        compassView.setTypeface(getmGpsFictionActivity().getFontFromRes(R.raw.font_dancing));
        return getRootView();
    }

}
