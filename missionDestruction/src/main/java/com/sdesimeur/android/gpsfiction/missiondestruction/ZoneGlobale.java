package com.sdesimeur.android.gpsfiction.missiondestruction;

import android.os.Bundle;

public class ZoneGlobale extends ZoneAmie {
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

    public void onEnter() {
        // TODO Auto-generated method stub
    }

    public void onExit() {
        // TODO Auto-generated method stub
    }
}
