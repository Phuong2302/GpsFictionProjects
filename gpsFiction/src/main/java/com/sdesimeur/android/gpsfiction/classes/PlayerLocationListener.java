package com.sdesimeur.android.gpsfiction.classes;

import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;

import java.util.EventListener;

public interface PlayerLocationListener extends EventListener {
    void onLocationPlayerChanged(MyGeoPoint playerLocation);
}
