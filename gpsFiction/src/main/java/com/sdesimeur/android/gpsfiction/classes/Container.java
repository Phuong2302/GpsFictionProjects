package com.sdesimeur.android.gpsfiction.classes;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;


public abstract class Container extends GpsFictionThing {
    protected Integer maxIncludedThings = 0;
    protected Boolean transportable = true;
    protected transient HashSet<GpsFictionThing> includedThings = new HashSet<GpsFictionThing>();

    public Container() {
        super();
        // TODO Auto-generated constructor stub
    }

    public HashSet<GpsFictionThing> getIncludedThings() {
        return includedThings;
    }

    public int[][] getIncludedThingsByUUID() {
        int nbOfIncludedThings = this.includedThings.size();
        int[][] includedThingsByUUID = new int[nbOfIncludedThings][3];
        GpsFictionThing gft = null;
        Iterator<GpsFictionThing> it = this.includedThings.iterator();
        int index = 0;
        while (it.hasNext()) {
            gft = it.next();
            includedThingsByUUID[index] = new int[3];
            includedThingsByUUID[index] = gft.getUUID();
            index++;
        }
        return includedThingsByUUID;
    }

    public Bundle getByBundle() throws JSONException {
        Bundle toPass = super.getByBundle();
        Bundle dest = new Bundle();
        dest.putBundle(JSonStrings.PARENTJSON, toPass);
        boolean[] val = {this.transportable};
        dest.putBooleanArray(JSonStrings.TRANSPORTABLE, val);
        dest.putInt(JSonStrings.MAXINCLUDEDTHINGS, this.maxIncludedThings);
        return dest;
    }

    public void setByBundle(Bundle in) throws JSONException {
        Bundle toPass = in.getBundle(JSonStrings.PARENTJSON);
        super.setByBundle(toPass);
        boolean[] val = new boolean[1];
        val = in.getBooleanArray(JSonStrings.TRANSPORTABLE);
        this.transportable = val[0];
        this.maxIncludedThings = in.getInt(JSonStrings.MAXINCLUDEDTHINGS);
    }

    public JSONObject getJson() throws JSONException {
        JSONObject objsuper = super.getJson();
        JSONObject obj  = new JSONObject();
        obj.put(JSonStrings.PARENTJSON,objsuper);
        obj.put(JSonStrings.TRANSPORTABLE,transportable);
        obj.put(JSonStrings.MAXINCLUDEDTHINGS, maxIncludedThings);
        return  obj;
    }

    public void setJson (JSONObject obj) throws JSONException {
        super.setJson(obj.getJSONObject(JSonStrings.PARENTJSON));
        transportable = obj.getBoolean(JSonStrings.TRANSPORTABLE);
        maxIncludedThings = obj.getInt(JSonStrings.MAXINCLUDEDTHINGS);
    }

    public void setAttrs(boolean visible, boolean active, boolean transportable) {
        this.setTransportable(transportable);
        this.setAttrs(visible, active);
    }

    public boolean getTransportable() {
        return this.transportable;
    }

    /**
     * @param transportable the transportable to set
     */
    public void setTransportable(boolean transportable) {
        this.transportable = transportable;
    }

    /**
     * @param maxThings the maxThings to set
     */
    public void setMaxIncludedThings(int maxThings) {
        this.maxIncludedThings = maxThings;
    }

    public int getMaxIncludedThings() {
        return this.maxIncludedThings;
    }

    public boolean addThingToContainer(GpsFictionThing thing) {
        this.includedThings.add(thing);
        return false;
    }

    void removeThingToContainer(GpsFictionThing thing) {
        this.includedThings.remove(thing);
    }

}

