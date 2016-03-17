package com.sdesimeur.android.gpsfiction.missiondestruction;

import android.os.Bundle;

import com.sdesimeur.android.gpsfiction.classes.Zone;

public abstract class ZoneAmie extends Zone {
    public final static float distStdEntreZones = MissionDestructionMainActivity.COEF * 10f;

    public ZoneAmie() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Bundle getByBundle() {
        Bundle toPass = super.getByBundle();
        Bundle dest = new Bundle();
        dest.putBundle("Parent", toPass);
        return dest;
    }

    public void setByBundle(Bundle in) {
        Bundle toPass = in.getBundle("Parent");
        super.setByBundle(toPass);
    }
}
