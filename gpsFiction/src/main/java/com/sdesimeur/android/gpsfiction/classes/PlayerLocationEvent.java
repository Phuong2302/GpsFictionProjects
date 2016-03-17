package com.sdesimeur.android.gpsfiction.classes;

import com.sdesimeur.android.gpsfiction.geopoint.GeoPoint;

public class PlayerLocationEvent {
    private GeoPoint locationOfPlayer = null;

    PlayerLocationEvent(GeoPoint gp) {
        this.setLocationOfPlayer(gp);
    }

    public PlayerLocationEvent() {
        // TODO Auto-generated constructor stub
        this.locationOfPlayer = new GeoPoint();
    }

    public GeoPoint getLocationOfPlayer() {
        return this.locationOfPlayer;
    }

    public void setLocationOfPlayer(GeoPoint locationOfPlayer) {
        this.locationOfPlayer = locationOfPlayer;
    }
}
