package com.sdesimeur.android.gpsfiction.classes;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class Character extends Container {

    public Character() {
        super();
        // TODO Auto-generated constructor stub
    }

/*
    public Bundle getByBundle() throws JSONException {
        Bundle toPass = super.getByBundle();
        Bundle dest = new Bundle();
        dest.putBundle(JSonStrings.PARENTJSON, toPass);
        return dest;
    }

    public void setByBundle(Bundle in) throws JSONException {
        Bundle toPass = in.getBundle(JSonStrings.PARENTJSON);
        super.setByBundle(toPass);
    }
*/
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
