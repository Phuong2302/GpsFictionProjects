package com.sdesimeur.android.gpsfiction.classes;

public class DistancesZones {
    protected Float distance;
    protected Zone zone;

    public DistancesZones(Zone zone, Float distance) {
        this.zone = zone;
        this.distance = distance;
    }

    public Float getDistance() {
        return this.distance;
    }

    public Zone getZone() {
        return this.zone;
    }

}
