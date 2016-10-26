package com.sdesimeur.android.gpsfiction.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.views.CompassView;
import com.sdesimeur.android.gpsfiction.views.ZoneDistance4CompassView;
import com.sdesimeur.android.gpsfiction.views.ZoneNameView;

@SuppressWarnings("unused")
public class CompassFragment extends MyTabFragment {
    private ZoneDistance4CompassView textviewDistance = null;
    private ZoneNameView textviewName = null;
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
        this.setRootView(inflater.inflate(R.layout.compass_view, container, false));
        this.textviewName = (ZoneNameView) (this.getRootView().findViewById(R.id.textNameOfZone));
        this.textviewName.init(this.getGpsFictionActivity());
        this.textviewDistance = (ZoneDistance4CompassView) (this.getRootView().findViewById(R.id.textDistance));
        this.textviewDistance.init(this.getGpsFictionActivity());
        this.compassView = (CompassView) (this.getRootView().findViewById(R.id.compassDirection));
        this.compassView.init(this.getGpsFictionActivity());
        this.compassView.setTypeface(this.getGpsFictionActivity().getFontFromRes(R.raw.font_dancing));
        return this.getRootView();
    }

}
