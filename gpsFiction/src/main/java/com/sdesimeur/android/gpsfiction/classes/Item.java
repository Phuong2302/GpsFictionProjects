package com.sdesimeur.android.gpsfiction.classes;

import android.os.Bundle;

public class Item extends Container {

    public Item() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Bundle getByBundle() {
        Bundle dest = new Bundle();
        Bundle toPass = super.getByBundle();
        dest.putBundle("Parent", toPass);
        return dest;
    }

    public void setByBundle(Bundle in) {
        Bundle toPass = in.getBundle("Parent");
        Bundle dest = new Bundle();
        super.setByBundle(toPass);
    }

    @Override
    public void validate() {

    }

}
