package com.sdesimeur.android.gpsfiction.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdesimeur.android.gpsfiction.R;


public class ZonesFragment extends MyTabFragment
{
    private RecyclerView listZones = null;
    private Adapter4TabZones adapter;
    //private DataSetObserver mDataSetObserver = null;

    //TODO add tmpZonesToOrder
    public ZonesFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.setRootView(inflater.inflate(R.layout.zones_view, container, false));
        this.listZones = (RecyclerView) this.getRootView().findViewById(R.id.listZones);
        int id = R.id.gpsFictionControlerId;
        listZones.setTag(id,getmGpsFictionControler());
        adapter = new Adapter4TabZones();
        listZones.setAdapter(adapter);
        listZones.setLayoutManager(new LinearLayoutManager(getActivity()));
        return this.getRootView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }
    @Override
    public void onResume () {
        super.onResume();
    }
    @Override
    public void onPause () {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
