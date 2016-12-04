package com.sdesimeur.android.gpsfiction.activities;

import android.database.DataSetObserver;
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
    private Adapter4TabZones adapter = new Adapter4TabZones();
    private DataSetObserver mDataSetObserver = null;

    //TODO add tmpZonesToOrder
    public ZonesFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.setRootView(inflater.inflate(R.layout.zones_view, container, false));
        this.listZones = (RecyclerView) this.getRootView().findViewById(R.id.listZones);
        listZones.setAdapter(adapter);
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
        mDataSetObserver = new DataSetObserver() {
            public void onChanged () {
                if (listZones != null) listZones.invalidateViews();
            }
        };
        adapter.registerDataSetObserver(mDataSetObserver);
        adapter.register(this);
    }
    @Override
    public void onPause () {
        adapter.unregisterDataSetObserver(mDataSetObserver);
        adapter.unregister();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
