package com.sdesimeur.android.gpsfiction.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListener;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationEvent;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;


public class ZonesFragment extends MyTabFragment implements PlayerLocationListener {
    private ListView listZones = null;
    private Adapter4TabZones adapter = null;

    //private GpsFiction gpsFiction;
    //TODO add tmpZonesToOrder
    public ZonesFragment() {
        super();
    }

    @Override
    public void onLocationPlayerChanged(PlayerLocationEvent playerLocationEvent) {
    //    if (this.listZones != null) {
            /*
            if (this.adapter != null) {
                this.listZones.setAdapter(this.adapter);
                this.adapter.notifyDataSetChanged();
            }
            */
            this.listZones.invalidateViews();
    //    }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.setRootView(inflater.inflate(R.layout.zones_view, container, false));
        this.listZones = (ListView) this.getRootView().findViewById(R.id.listZones);
        return this.getRootView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onResume() {
        super.onResume();
        if (adapter == null) {
            adapter = new Adapter4TabZones();
        }
        adapter.init(getGpsFictionActivity());
        listZones.setAdapter(adapter);
        this.getGpsFictionActivity().getMyLocationListener().addPlayerLocationListener(MyLocationListener.REGISTER.FRAGMENT, this);
    }
/*
    public void register(GpsFictionActivity gpsFictionActivity) {
        super.register(gpsFictionActivity);
    }
    */
}
