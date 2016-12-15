package com.sdesimeur.android.gpsfiction.classes;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class Item extends Container {

    public Item() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Bundle getByBundle() throws JSONException {
        Bundle dest = new Bundle();
        Bundle toPass = super.getByBundle();
        dest.putBundle(JSonStrings.PARENTJSON, toPass);
        return dest;
    }

    public void setByBundle(Bundle in) throws JSONException {
        Bundle toPass = in.getBundle(JSonStrings.PARENTJSON);
        Bundle dest = new Bundle();
        super.setByBundle(toPass);
    }


    public JSONObject getJson() throws JSONException {
        JSONObject objsuper = super.getJson();
        JSONObject obj  = new JSONObject();
        obj.put(JSonStrings.PARENTJSON,objsuper);
        return  obj;
    }

    public void setJson (JSONObject obj) throws JSONException {
        super.setJson(obj.getJSONObject(JSonStrings.PARENTJSON));
    }

    @Override
    public void validate() {

    }

}
