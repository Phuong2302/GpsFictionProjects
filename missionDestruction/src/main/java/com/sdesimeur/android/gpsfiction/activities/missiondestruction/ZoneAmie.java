package com.sdesimeur.android.gpsfiction.activities.missiondestruction;

import com.sdesimeur.android.gpsfiction.classes.Zone;

public abstract class ZoneAmie extends Zone {
    public final static float distStdEntreZones = MissionDestructionMainActivity.COEF * 10f;

    public ZoneAmie() {
        super();
        // TODO Auto-generated constructor stub
    }

/*
    public Bundle getByBundle() throws JSONException {
        Bundle toPass = super.getByBundle();
        Bundle dest = new Bundle();
        dest.putBundle("Parent", toPass);
        return dest;
    }

    public void setByBundle(Bundle in) throws JSONException {
        Bundle toPass = in.getBundle("Parent");
        super.setByBundle(toPass);
    }
*/
}
