package com.sdesimeur.android.gpsfiction.classes;

public interface ZoneEnterOrExitInterface {
    // creer un evenement en fonction de playerIsInZone
    abstract void onEnter();

    abstract void onExit();
}
