package com.sdesimeur.android.gpsfiction.classes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.format.Time;

import com.sdesimeur.android.gpsfiction.activities.CalcRouteAndSpeakService;
import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;

import java.util.HashMap;
import java.util.HashSet;

public class MyLocationListener implements LocationListener, SensorEventListener {
    private static final double LATITUDE = 49.59266;
    private static final double LONGITUDE = 3.65649;
    private final static float MINBEARINGCHANGED = 5;
    private final static float SPEEDLIMIT = 0.003f;
    private HashMap<MyLocationListener.REGISTER, HashSet<PlayerBearingListener>> playerBearingListener = null;
    private HashMap<MyLocationListener.REGISTER, HashSet<PlayerLocationListener>> playerLocationListener = null;
    //private static final HashSet <PlayerLocationListener> playerLocationListener = new HashSet <PlayerLocationListener> ();
    //private static final HashSet <PlayerLocationListener> playerLocationListenerZone = new HashSet <PlayerLocationListener> ();
    private MyGeoPoint lastPlayerGeoPoint = null;
    private Time lastTimePlayerGeoPoint = null;
    private MyGeoPoint playerGeoPoint = null;
    private Time timePlayerGeoPoint = null;
    //	private static final MyLocationListener staticLocationListener=new MyLocationListener();
//	private static final SensorEventListener staticCompassListener = staticLocationListener;
    private boolean compassActive;
    private float bearingOfPlayer;
    private float compassBearing;
    private float locationBearing;
    private Sensor sensorsOrientation = null;
    private GpsFictionActivity gpsFictionActivity = null;

    public MyLocationListener() {
        playerBearingListener = new HashMap<>();
        playerLocationListener = new HashMap<>();
        for (REGISTER i : REGISTER.values()) {
            playerBearingListener.put(i, new HashSet<PlayerBearingListener>());
            playerLocationListener.put(i, new HashSet<PlayerLocationListener>());
        }
        timePlayerGeoPoint = new Time();
        lastTimePlayerGeoPoint = new Time();
        compassActive = false;
        bearingOfPlayer = 0;
        compassBearing = 0;
        locationBearing = 0;
    }

    public HashSet<PlayerBearingListener> getPlayerBearingListener(REGISTER i) {
        return playerBearingListener.get(i);
    }

    public HashSet<PlayerLocationListener> getPlayerLocationListener(REGISTER i) {
        return playerLocationListener.get(i);
    }

    public Bundle getByBundle() {
        Bundle dest = new Bundle();
        dest.putFloat("bop", bearingOfPlayer);
        dest.putFloat("cb", compassBearing);
        dest.putFloat("lb", locationBearing);
        /*
        double[] coord = {playerGeoPoint.getLatitude(), playerGeoPoint.getLongitude()};
        dest.putDoubleArray("pgp", coord);
        coord = new double[]{lastPlayerGeoPoint.getLatitude(), lastPlayerGeoPoint.getLongitude()};
        dest.putDoubleArray("lpgp", coord);
        dest.putLong("tpgp", timePlayerGeoPoint.toMillis(true));
        dest.putLong("ltpgp", lastTimePlayerGeoPoint.toMillis(true));
        */
        return dest;
    }

    public void setByBundle(Bundle in) {
        bearingOfPlayer = in.getFloat("bop");
        compassBearing = in.getFloat("cb");
        locationBearing = in.getFloat("lb");
        /*
        double[] coord = in.getDoubleArray("pgp");
        playerGeoPoint = new MyGeoPoint(coord[0], coord[1]);
        coord = in.getDoubleArray("lpgp");
        lastPlayerGeoPoint = new MyGeoPoint(coord[0], coord[1]);
        timePlayerGeoPoint.set(in.getLong("tpgp"));
        lastTimePlayerGeoPoint.set(in.getLong("ltpgp"));
        */
    }

    public void init(GpsFictionActivity gfa) {
        gpsFictionActivity = gfa;
        startLocationListener();

    }

    /*static public enum SENSORTOREGISTER  {
        BEARING,
        LOCATION
    }*/
    public void addPlayerLocationListener(REGISTER type, PlayerLocationListener listener) {
        playerLocationListener.get(type).add(listener);
        if (playerGeoPoint != null) listener.onLocationPlayerChanged(playerGeoPoint);
    }

    public void removePlayerLocationListener(REGISTER type, PlayerLocationListener listener) {
        playerLocationListener.get(type).remove(listener);
    }

    public void addPlayerBearingListener(REGISTER type, PlayerBearingListener listener) {
        playerBearingListener.get(type).add(listener);
            listener.onBearingPlayerChanged(bearingOfPlayer);
    }

    public void removePlayerBearingListener(REGISTER type, PlayerBearingListener listener) {
        playerBearingListener.get(type).remove(listener);
    }

    public void firePlayerLocationListener() {
        if (playerGeoPoint != null) {
            Bundle bd = playerGeoPoint.getByBundle();
            Intent myIntent = new Intent (getGpsFictionActivity(), CalcRouteAndSpeakService.class);
            myIntent.setAction(CalcRouteAndSpeakService.ACTION.CHANGEGEOPOINT4PLAYER);
            myIntent.putExtras(bd);
            getGpsFictionActivity().startService(myIntent);
            for (REGISTER i : REGISTER.values()) {
                for (PlayerLocationListener listener : playerLocationListener.get(i)) {
                    listener.onLocationPlayerChanged(playerGeoPoint);
                }
            }
        }
    }

    public void firePlayerBearingListener() {
        for (REGISTER i : REGISTER.values()) {
            for (PlayerBearingListener listener : playerBearingListener.get(i)) {
                listener.onBearingPlayerChanged(bearingOfPlayer);
            }
        }
    }

    public void onLocationChanged(Location location) {
        setNewPlayerGeopoint(location);
        firePlayerLocationListener();
    }

    @Override
    public void onProviderDisabled(String provider) {
//TODO 	onProviderDisabled
    }

    @Override
    public void onProviderEnabled(String provider) {
//TODO onProviderEnabled
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//TODO onStatusChanged
    }

    private void setNewPlayerGeopoint(Location location) {
        if (playerGeoPoint != null) {
            lastTimePlayerGeoPoint.set(timePlayerGeoPoint);
            lastPlayerGeoPoint = new MyGeoPoint(playerGeoPoint);
        }
        timePlayerGeoPoint.setToNow();
        //playerGeoPoint.setGeoPoint(location);
        playerGeoPoint = new MyGeoPoint(location);
        if (lastPlayerGeoPoint != null) {
            locationBearing = lastPlayerGeoPoint.bearingTo(playerGeoPoint);
        }
        setCompassActive();
    }

    private void calculateNewPlayerBearing() {
        // TODO Auto-generated method stub
        if (compassActive) {
            bearingOfPlayer = compassBearing;
        } else {
            bearingOfPlayer = locationBearing;
        }
        firePlayerBearingListener();
    }

    public void startLocationListener() {
        LocationManager locationManager = (LocationManager) getGpsFictionActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getGpsFictionActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getGpsFictionActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0.5f, this);
        //lastPlayerGeoPoint = playerGeoPoint;
    }

    public void removeGpsFictionUpdates() {
        LocationManager locationManager = (LocationManager) getGpsFictionActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getGpsFictionActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getGpsFictionActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
    }

    public GpsFictionActivity getGpsFictionActivity() {
        return gpsFictionActivity;
    }

    public MyGeoPoint getPlayerGeoPoint() {
        return playerGeoPoint;
    }

    public boolean isCompassActive() {
        return compassActive;
    }

    public void setCompassActive() {
        SensorManager sensorManager = (SensorManager) getGpsFictionActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            float speed = 0f;
            if ((lastPlayerGeoPoint != null) && (playerGeoPoint != null)) {
                float deltaTimeInMilliSeconds = (float) (timePlayerGeoPoint.toMillis(true) - lastTimePlayerGeoPoint.toMillis(true));
                speed = 3600 * lastPlayerGeoPoint.distanceTo(playerGeoPoint) / deltaTimeInMilliSeconds;
            }
            compassActive = (speed < SPEEDLIMIT);
        } else {
            compassActive = false;
        }
        if (compassActive) {
            sensorsOrientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            sensorManager.registerListener(this, sensorsOrientation, SensorManager.SENSOR_DELAY_GAME);
        } else {
            if (sensorManager != null) {
                sensorManager.unregisterListener(this, sensorsOrientation);
            }
        }
        calculateNewPlayerBearing();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        float newCompassBearing = event.values[0];
        float diffBearing = Math.abs(newCompassBearing - compassBearing);
        if (!((diffBearing < MINBEARINGCHANGED) || (diffBearing > (360 - MINBEARINGCHANGED)))) {
            compassBearing = newCompassBearing;
            calculateNewPlayerBearing();
        }
    }

    public float getBearingOfPlayer() {
        return bearingOfPlayer;
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

    static public enum REGISTER {
        MARKER,
        ZONE,
        HOLDERVIEW,
        ADAPTERVIEW,
        VIEW,
        LAYOUT,
        FRAGMENT
    }
}
