package com.sdesimeur.android.gpsfiction.classes;

import android.os.Bundle;

@SuppressWarnings("unused")
public class Character extends Container {

    public Character() {
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


}
