package com.sdesimeur.android.gpsfiction.activities;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sdesimeur.android.gpsfiction.R;


public class ZonesFragment extends MyTabFragment
//        implements PlayerLocationListener
{
    private ListView listZones = null;
    private Adapter4TabZones adapter = null;
    private DataSetObserver mDataSetObserver = null;

    //private GpsFiction gpsFiction;
    //TODO add tmpZonesToOrder
    public ZonesFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.setRootView(inflater.inflate(R.layout.zones_view, container, false));
        this.listZones = (ListView) this.getRootView().findViewById(R.id.listZones);
        if (adapter == null) {
            adapter = new Adapter4TabZones();
        }
        listZones.setAdapter(adapter);
        mDataSetObserver = new DataSetObserver() {
            public void onChanged () {
                listZones.invalidateViews();
            }
        };
        adapter.registerDataSetObserver(mDataSetObserver);
        return this.getRootView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.register(getGpsFictionActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.unregisterDataSetObserver(mDataSetObserver);
        adapter.destroy();
        adapter=null;
    }
/*
    public void register(GpsFictionActivity gpsFictionActivity) {
        super.register(gpsFictionActivity);
    }
    */
}
