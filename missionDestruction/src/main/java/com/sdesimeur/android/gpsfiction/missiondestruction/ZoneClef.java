package com.sdesimeur.android.gpsfiction.missiondestruction;

import android.os.Bundle;

public class ZoneClef extends ZoneAmie {
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

    @Override
    public void onEnter() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onExit() {
        // TODO Auto-generated method stub
    }
}
