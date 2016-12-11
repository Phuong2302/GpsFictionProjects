package com.sdesimeur.android.gpsfiction.classes;

import android.os.Bundle;

public class Task extends GpsFictionThing {
    private boolean complete = false;

    public Task() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Bundle getByBundle() {
        Bundle toPass = super.getByBundle();
        Bundle dest = new Bundle();
        dest.putBundle("Parent", toPass);
        boolean val[] = {this.complete};
        dest.putBooleanArray("Complete", val);
        return dest;
    }

    public void setByBundle(Bundle in) {
        Bundle toPass = in.getBundle("Parent");
        super.setByBundle(toPass);
        boolean val[] = new boolean[1];
        val = in.getBooleanArray("Complete");
        this.complete = val[0];
    }

    @Override
    public void validate() {

    }

    public void setAttrs(boolean visible, boolean active, boolean complete) {
        this.setComplete(complete);
        this.setAttrs(visible, active);
    }

    /**
     * @return the complete
     */
    public boolean isComplete() {
        return this.complete;
    }

    /**
     * @param complete the complete to set
     */
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

}
