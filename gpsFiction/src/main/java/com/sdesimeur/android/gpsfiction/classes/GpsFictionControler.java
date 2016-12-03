package com.sdesimeur.android.gpsfiction.classes;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;

import com.google.gson.Gson;
import com.sdesimeur.android.gpsfiction.activities.CalcRouteAndSpeakService;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;
import com.sdesimeur.android.gpsfiction.gpx.GPXParser;
import com.sdesimeur.android.gpsfiction.gpx.beans.GPX;
import com.sdesimeur.android.gpsfiction.gpx.beans.Track;
import com.sdesimeur.android.gpsfiction.gpx.beans.Waypoint;
import com.sdesimeur.android.gpsfiction.helpers.BindToMyLocationListenerHelper;

import org.oscim.layers.PathLayer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by sam on 03/12/16.
 */

public class GpsFictionControler {
    private final Activity activity;

    private final BindToMyLocationListenerHelper mBindToMyLocationListenerHelper;
    protected GpsFictionData mGpsFictionData = null;
    protected MyLocationListenerService mMyLocationListenerService = null;
    private HashMap<REGISTER, HashSet<PlayerBearingListener>> playerBearingListener = null;
    private HashMap<REGISTER, HashSet<PlayerLocationListener>> playerLocationListener = null;
    private transient HashSet<VehiculeSelectedIdListener> vehiculeSelectedIdListener = new HashSet<>();
    private transient HashMap<REGISTER, HashSet<ZoneSelectListener>> zoneSelectListener = new HashMap<>();
    private transient HashSet<ZoneChangeListener> zoneChangeListener = new HashSet<> ();
    private Resources resources;


    public Resources getResources() {
        return resources;
    }

    public Activity getActivity() {
        return activity;
    }

    public PathLayer getRoutePathLayer() {
        return getmGpsFictionData().getRoutePathLayer();
    }

    public boolean isAllreadyConfigured() {
        return getmGpsFictionData().isAllreadyConfigured();
    }

    public void setAllreadyConfigured(boolean a) {
        mGpsFictionData.setAllreadyConfigured(a);
    }

    public Zone getSelectedZone() {
        return mGpsFictionData.getSelectedZone();
    }

    public MyGeoPoint getPlayerGeoPoint() {
        return (mMyLocationListenerService != null )?mMyLocationListenerService.getPlayerGeoPoint():new MyGeoPoint(0,0);
    }

    public float getBearingOfPlayer() {
        return (mMyLocationListenerService != null )?mMyLocationListenerService.getBearingOfPlayer():0;
    }

    public void setSelectedZone(Zone selectedZone) {
        mGpsFictionData.setSelectedZone(selectedZone);
    }

    static public enum REGISTER {
        SERVICE,
        MARKER,
        ZONE,
        HOLDERVIEW,
        ADAPTERVIEW,
        VIEW,
        LAYOUT,
        FRAGMENT
    }
    public HashSet<PlayerBearingListener> getPlayerBearingListener(REGISTER i) {
        return playerBearingListener.get(i);
    }

    public HashSet<PlayerLocationListener> getPlayerLocationListener(REGISTER i) {
        return playerLocationListener.get(i);
    }

    public GpsFictionData getmGpsFictionData() {
        return mGpsFictionData;
    }
    public void setmGpsFictionData(GpsFictionData mGpsFictionData) {
        this.mGpsFictionData = mGpsFictionData;
    }
    public MyLocationListenerService getmMyLocationListenerService() {
        // TODO Auto-generated method stub
        return mMyLocationListenerService;
    }
    public void addPlayerLocationListener(REGISTER type, PlayerLocationListener listener) {
        playerLocationListener.get(type).add(listener);
        if (mMyLocationListenerService != null ) {
            MyGeoPoint gp = mMyLocationListenerService.getPlayerGeoPoint();
            if (gp != null) listener.onLocationPlayerChanged(gp);
        }
    }

    public void removePlayerLocationListener(REGISTER type, PlayerLocationListener listener) {
        playerLocationListener.get(type).remove(listener);
    }

    public void addPlayerBearingListener(REGISTER type, PlayerBearingListener listener) {
        playerBearingListener.get(type).add(listener);
        if (mMyLocationListenerService != null ) {
            float a = mMyLocationListenerService.getBearingOfPlayer();
            listener.onBearingPlayerChanged(a);
        }
    }

    public void removePlayerBearingListener(REGISTER type, PlayerBearingListener listener) {
        playerBearingListener.get(type).remove(listener);
    }

    public void firePlayerLocationListener() {
        if (mMyLocationListenerService != null ) {
            MyGeoPoint gp = mMyLocationListenerService.getPlayerGeoPoint();
            if (gp != null) {
                for (REGISTER i : REGISTER.values()) {
                    for (PlayerLocationListener listener : playerLocationListener.get(i)) {
                        listener.onLocationPlayerChanged(gp);
                    }
                }
            }
        }
    }

    public void firePlayerBearingListener() {
        if (mMyLocationListenerService != null ) {
            float a = mMyLocationListenerService.getBearingOfPlayer();
            for (REGISTER i : REGISTER.values()) {
                for (PlayerBearingListener listener : playerBearingListener.get(i)) {
                    listener.onBearingPlayerChanged(a);
                }
            }
        }
    }

    public void clearFragmentListener() {
        getPlayerBearingListener(REGISTER.FRAGMENT).clear();
        getPlayerLocationListener(REGISTER.FRAGMENT).clear();
    }

    public void clearZoneListener() {
        getPlayerBearingListener(REGISTER.ZONE).clear();
        getPlayerLocationListener(REGISTER.ZONE).clear();
    }

    public void clearViewListener() {
        getPlayerBearingListener(REGISTER.VIEW).clear();
        getPlayerLocationListener(REGISTER.VIEW).clear();
    }

    public void clearAllListener() {
        clearFragmentListener();
        clearViewListener();
        //clearZoneListener();
    }
    public void addZoneChangeListener(ZoneChangeListener listener) {
        Zone sz = mGpsFictionData.getSelectedZone();
        this.zoneChangeListener.add(listener);
        if (sz != null) listener.onZoneChanged(sz);
    }

    public void removeZoneChangeListener(ZoneChangeListener listener) {
        this.zoneChangeListener.remove(listener);
    }
    public void fireZoneChangeListener(Zone zn) {
        for (ZoneChangeListener listener : this.zoneChangeListener) {
            listener.onZoneChanged(zn);
        }
        if (zn.isSelectedZone()) {
            /////TODO send this to CalcRouteAndSpeakService
            mGpsFictionData.setSelectedZone(null);
            fireZoneSelectListener();
        }
    }
    public void addVehiculeSelectedIdListener(VehiculeSelectedIdListener listener) {
        int vid = mGpsFictionData.getVehiculeSelectedId();
        this.vehiculeSelectedIdListener.add(listener);
        listener.onVehiculeSelectedId(vid);
    }

    public void removeVehiculeSelectedIdListener(VehiculeSelectedIdListener listener) {
        this.vehiculeSelectedIdListener.remove(listener);
    }
    public void fireVehiculeSelectedIdListener() {
        int vid = mGpsFictionData.getVehiculeSelectedId();
        for (VehiculeSelectedIdListener listener : this.vehiculeSelectedIdListener) {
            listener.onVehiculeSelectedId(vid);
        }
    }
    public void addZoneSelectListener(REGISTER type, ZoneSelectListener listener) {
        Zone sz = mGpsFictionData.getSelectedZone();
        Zone usz = mGpsFictionData.getUnSelectedZone();
        this.zoneSelectListener.get(type).add(listener);
        if (sz != null) listener.onZoneSelectChanged(sz,usz);
    }

    public void removeZoneSelectListener(REGISTER type, ZoneSelectListener listener) {
        this.zoneSelectListener.get(type).remove(listener);
    }

    public void fireZoneSelectListener() {
        Zone sz = mGpsFictionData.getSelectedZone();
        Zone usz = mGpsFictionData.getUnSelectedZone();
        if (sz != usz) {
            for (REGISTER i : REGISTER.values()) {
                for (ZoneSelectListener listener : this.zoneSelectListener.get(i)) {
                    listener.onZoneSelectChanged(sz, usz);
                }
            }
        }
    }



    public GpsFictionControler(Activity activity) {
        playerBearingListener = new HashMap<>();
        playerLocationListener = new HashMap<>();
        resources = activity.getResources();
        for (REGISTER i : REGISTER.values()) {
            playerBearingListener.put(i, new HashSet<PlayerBearingListener>());
            playerLocationListener.put(i, new HashSet<PlayerLocationListener>());
            this.zoneSelectListener.put(i, new HashSet<ZoneSelectListener>());
        }
        this.activity = activity;
        if (mGpsFictionData == null) {
            mGpsFictionData = new GpsFictionData();
            mGpsFictionData.setmGpsFictionControler(this);
        }
	    Intent myIntent1 = new Intent(activity, CalcRouteAndSpeakService.class);
	    myIntent1.setAction(CalcRouteAndSpeakService.ACTION.STARTFOREGROUND);
	    activity.bindService(myIntent1, serviceConnectionToCalcRouteAndSpeakService, Context.BIND_AUTO_CREATE);
	    mBindToMyLocationListenerHelper = new BindToMyLocationListenerHelper(activity) {
	        @Override
	        protected void onBindWithMyLocationListener(MyLocationListenerService mlls) {
	            mMyLocationListenerService = mlls;
	            GpsFictionControler.this.onBindWithMyLocationListener();
	        }
	    };
    }

    private void onBindWithMyLocationListener() {
        mMyLocationListenerService.setGpsFictionControler(this);
    }

    public CalcRouteAndSpeakService getmCalcRouteAndSpeakService() {
        return mCalcRouteAndSpeakService;
    }

    private CalcRouteAndSpeakService mCalcRouteAndSpeakService;
    private boolean isBoundToCalcRouteAndSpeakService;
    private ServiceConnection serviceConnectionToCalcRouteAndSpeakService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CalcRouteAndSpeakService.MyBinder binder = (CalcRouteAndSpeakService.MyBinder) service;
            mCalcRouteAndSpeakService = binder.getService();
            mCalcRouteAndSpeakService.setGpsFictionControler(this);
            isBoundToCalcRouteAndSpeakService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCalcRouteAndSpeakService = null;
            isBoundToCalcRouteAndSpeakService = false;
        }
    };
    public void onDestroy() {
        activity.unbindService(serviceConnectionToCalcRouteAndSpeakService);
        mBindToMyLocationListenerHelper.onUnBindWithMyLocationListener();
        Intent myIntent1 = new Intent(activity, CalcRouteAndSpeakService.class);
        myIntent1.setAction(CalcRouteAndSpeakService.ACTION.STOPFOREGROUND);
        activity.startService(myIntent1);
        Intent myIntent2 = new Intent(activity, MyLocationListenerService.class);
        myIntent2.setAction(MyLocationListenerService.ACTION.STOPFOREGROUND);
        activity.startService(myIntent2);
    }
    public void setResourcedZones(int gpxRes) {
        InputStream in = activity.getResources().openRawResource(gpxRes);
        GPXParser p = new GPXParser();
        Zone zn = null;
        try {
            GPX gpx = p.parseGPX(in);
            Iterator<Track> it = gpx.getTracks().iterator();
            Track tr;
            while (it.hasNext()) {
                tr = it.next();
                ArrayList<Waypoint> wpts = tr.getTrackPoints();
                wpts.remove(0);
                String name = "zone" + tr.getName();
                int res = activity.getResources().getIdentifier(name, "string", activity.getPackageName());
                zn = new Zone();
                zn.init(mGpsFictionData);
                zn.setId(res);
                zn.setShape(wpts);
                zn.validate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Bundle toPass = savedInstanceState.getBundle("GpsFictionData");
            mGpsFictionData.setByBundle(toPass);
        } else {
            mGpsFictionData.init();
        }
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //if (mGpsFictionData.toSave) {
        Gson gson = new Gson();
        String test = gson.toJson(mGpsFictionData);
        Bundle toPass = mGpsFictionData.getByBundle();
        savedInstanceState.putBundle("GpsFictionData", toPass);
        //savedInstanceState.putString("GpsFictionDataAsJson",test);
        //}
    }

}
