package com.sdesimeur.android.gpsfiction.classes;

import java.util.EventListener;

public interface PlayerLocationListener extends EventListener {
    void onLocationPlayerChanged(PlayerLocationEvent playerLocationEvent);
}
