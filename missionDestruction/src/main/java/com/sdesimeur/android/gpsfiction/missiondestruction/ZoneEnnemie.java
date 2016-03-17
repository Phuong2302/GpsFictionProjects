package com.sdesimeur.android.gpsfiction.missiondestruction;

import android.os.Bundle;


public class ZoneEnnemie extends ZoneAdverse {
    protected final static float radiusStdZone = MissionDestructionMainActivity.COEF * 3f;
    protected final static float distStdEntreZones = MissionDestructionMainActivity.COEF * 20f;
    private final static int displayNameId = R.string.zoneEnnemi;

    public ZoneEnnemie() {
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

    @Override
    public void setIdAdverseNum(int numInTable4LongId) {
        this.setIdAdverse(displayNameId, numInTable4LongId);
        this.findCenterAndSetShapeIfAllIsSet();
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
