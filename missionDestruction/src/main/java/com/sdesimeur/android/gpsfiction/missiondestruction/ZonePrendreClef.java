package com.sdesimeur.android.gpsfiction.missiondestruction;

import android.os.Bundle;

public class ZonePrendreClef extends ZoneAmie {
    public Bundle getByBundle() {
        Bundle dest = new Bundle();
        Bundle toPass = super.getByBundle();
        dest.putBundle("Parent", toPass);
        return dest;
    }

    public void setByBundle(Bundle in) {
        Bundle toPass = in.getBundle("Parent");
        super.setByBundle(toPass);
    }

    @Override
    public void onEnter() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onExit() {
        // TODO Auto-generated method stub
    }
}
