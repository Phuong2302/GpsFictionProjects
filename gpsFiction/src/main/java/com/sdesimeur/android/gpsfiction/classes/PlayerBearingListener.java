package com.sdesimeur.android.gpsfiction.classes;

import java.util.EventListener;

public interface PlayerBearingListener extends EventListener {
    void onBearingPlayerChanged( float playerBearing );
}
