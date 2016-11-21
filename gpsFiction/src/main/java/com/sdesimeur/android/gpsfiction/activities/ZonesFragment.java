package com.sdesimeur.android.gpsfiction.activities;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListenerService;
import com.sdesimeur.android.gpsfiction.helpers.BindToMyLocationListenerHelper;


public class ZonesFragment extends MyTabFragment
{
    private ListView listZones = null;
    private Adapter4TabZones adapter = new Adapter4TabZones();
    private DataSetObserver mDataSetObserver = null;
    private BindToMyLocationListenerHelper mBindToMyLocationListenerHelper;
    private MyLocationListenerService mMyLocationListenerService;

    //TODO add tmpZonesToOrder
    public ZonesFragment() {
        super();
    }

    private void onBindWithMyLocationListener() {
        adapter.setMyLocationListenerService(mMyLocationListenerService);
        mMyLocationListenerService.addPlayerLocationListener(MyLocationListenerService.REGISTER.ADAPTERVIEW, adapter);
        for ( View v : adapter.getZone2View().values()) {
            ViewHolder4Zones v1 = (ViewHolder4Zones)v.getTag();
            mMyLocationListenerService.addPlayerLocationListener(MyLocationListenerService.REGISTER.VIEW,v1.getDistanceToZoneView());
            mMyLocationListenerService.addPlayerBearingListener(MyLocationListenerService.REGISTER.VIEW,v1.getMiniCompassView());
        }
    }

    private void onUnBindWithMyLocationListener() {
        mMyLocationListenerService.removePlayerLocationListener(MyLocationListenerService.REGISTER.ADAPTERVIEW, adapter);
        for ( View v : adapter.getZone2View().values()) {
            ViewHolder4Zones v1 = (ViewHolder4Zones)v.getTag();
            mMyLocationListenerService.removePlayerLocationListener(MyLocationListenerService.REGISTER.VIEW,v1.getDistanceToZoneView());
            mMyLocationListenerService.removePlayerBearingListener(MyLocationListenerService.REGISTER.VIEW,v1.getMiniCompassView());
        }
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

    @Override
    public void onCreate(Bundle saveInstanceState) {
        mBindToMyLocationListenerHelper = new BindToMyLocationListenerHelper(getActivity()) {
            @Override
            protected void onBindWithMyLocationListener(MyLocationListenerService mlls) {
                mMyLocationListenerService = mlls;
                ZonesFragment.this.onBindWithMyLocationListener();
            }

            @Override
            public void onUnBindWithMyLocationListener() {
                ZonesFragment.this.onUnBindWithMyLocationListener();
                super.onUnBindWithMyLocationListener();
            }
        };
        super.onCreate(saveInstanceState);
    }
    @Override
    public void onResume () {
        super.onResume();
        listZones.setAdapter(adapter);
        mDataSetObserver = new DataSetObserver() {
            public void onChanged () {
                listZones.invalidateViews();
            }
        };
        adapter.registerDataSetObserver(mDataSetObserver);
        adapter.register(this);
        getmGpsFictionData().addZoneChangeListener(adapter);
    }
    @Override
    public void onPause () {
        adapter.unregisterDataSetObserver(mDataSetObserver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        GpsFictionData gfd =  getmGpsFictionData();
        if (gfd != null) gfd.removeZoneChangeListener(adapter);
        mBindToMyLocationListenerHelper.onUnBindWithMyLocationListener();
        super.onDestroy();
    }
}
