package com.sdesimeur.android.gpsfiction.classes;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;


public class Task extends GpsFictionThing {
    private Boolean complete = false;

    public Task() {
        super();
    }

    public Bundle getByBundle() throws JSONException {
        Bundle toPass = super.getByBundle();
        Bundle dest = new Bundle();
        dest.putBundle(JSonStrings.PARENTJSON, toPass);
        boolean val[] = {this.complete};
        dest.putBooleanArray(complete.getClass().getCanonicalName(), val);
        return dest;
    }

    public void setByBundle(Bundle in) throws JSONException {
        Bundle toPass = in.getBundle(JSonStrings.PARENTJSON);
        super.setByBundle(toPass);
        boolean val[] = new boolean[1];
        val = in.getBooleanArray(complete.getClass().getCanonicalName());
        this.complete = val[0];
    }

    public JSONObject getJson() throws JSONException {
        JSONObject objsuper = super.getJson();
        JSONObject obj  = new JSONObject();
        obj.put(JSonStrings.PARENTJSON,objsuper);
        obj.put(JSonStrings.COMPLETE,complete);
        return  obj;
    }

    public void setJson (JSONObject obj) throws JSONException {
        super.setJson(obj.getJSONObject(JSonStrings.PARENTJSON));
        complete = obj.getBoolean(JSonStrings.COMPLETE);
    }

    @Override
    public void validate() {

    }

    public void setAttrs(boolean visible, boolean active, boolean complete) {
        this.setComplete(complete);
        this.setAttrs(visible, active);
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

}
