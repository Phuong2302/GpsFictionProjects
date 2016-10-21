package com.sdesimeur.android.gpsfiction.classes;

import com.sdesimeur.android.gpsfiction.geopoint.GeoPoint;

public class PlayerLocationEvent {
    private GeoPoint locationOfPlayer = null;

    PlayerLocationEvent(GeoPoint gp) {
        locationOfPlayer=gp;
    }

    public PlayerLocationEvent() {
        // TODO Auto-generated constructor stub
        locationOfPlayer = new GeoPoint();
    }

    public GeoPoint getLocationOfPlayer() {
        return locationOfPlayer;
    }

    public void setLocationOfPlayer(GeoPoint location) {
        locationOfPlayer = location;
    }

}
