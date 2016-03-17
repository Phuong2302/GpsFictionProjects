package com.sdesimeur.android.gpsfiction.classes;

public class PlayerBearingEvent {
    private float bearingOfPlayer;

    PlayerBearingEvent(float bearing) {
        this.setBearingOfPlayer(bearing);
    }

    public PlayerBearingEvent() {
        // TODO Auto-generated constructor stub
        this.bearingOfPlayer = 0;
    }

    public float getBearing() {
        return this.bearingOfPlayer;
    }

    public void setBearingOfPlayer(float bearing) {
        this.bearingOfPlayer = bearing;
    }
}
