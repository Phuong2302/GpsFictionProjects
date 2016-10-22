package com.sdesimeur.android.gpsfiction.classes;

import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;

public class PlayerLocationEvent {
    private MyGeoPoint locationOfPlayer = null;

    PlayerLocationEvent(MyGeoPoint gp) {
        locationOfPlayer=gp;
    }

    public PlayerLocationEvent() {
        // TODO Auto-generated constructor stub
        locationOfPlayer = new MyGeoPoint();
    }

    public MyGeoPoint getLocationOfPlayer() {
        return locationOfPlayer;
    }

    public void setLocationOfPlayer(MyGeoPoint location) {
        locationOfPlayer = location;
    }

}
