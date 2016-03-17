package com.sdesimeur.android.gpsfiction.classes;

import android.content.res.Resources;

import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;

import org.mapsforge.core.model.LatLong;

public class PlayerRotatingMarker extends RotatingMarker implements PlayerLocationListener {

    public PlayerRotatingMarker(LatLong latLong) {
        super(latLong);
        // TODO Auto-generated constructor stub
    }

    public PlayerRotatingMarker(LatLong latLong, Resources resources, int resource) {
        super(latLong, resources, resource);
    }

    @Override
    public void onLocationPlayerChanged(PlayerLocationEvent playerLocationEvent) {
        // TODO Auto-generated method stub
        LatLong playerPosition = new LatLong(playerLocationEvent.getLocationOfPlayer().getLatitude(), playerLocationEvent.getLocationOfPlayer().getLongitude());
        this.setLatLong(playerPosition);
    }

    public void register(GpsFictionActivity gpsFictionActivity) {
        super.register(gpsFictionActivity);
        this.gpsFictionActivity.getMyLocationListener().addPlayerLocationListener(MyLocationListener.REGISTER.MARKER, this);
    }

}
