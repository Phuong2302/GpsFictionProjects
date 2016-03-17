package com.sdesimeur.android.gpsfiction.classes;

import java.util.EventListener;

public interface ZoneSelectListener extends EventListener {
    public void onZoneSelectChanged(Zone selectedZone);
}
