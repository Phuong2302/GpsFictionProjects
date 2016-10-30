package com.sdesimeur.android.gpsfiction.classes;

import java.util.EventListener;

/**
 * Created by sam on 20/10/16.
 */
public interface ZoneChangeListener extends EventListener {
    void onZoneChanged(Zone z1);
}
