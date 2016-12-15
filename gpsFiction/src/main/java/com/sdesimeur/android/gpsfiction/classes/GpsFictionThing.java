package com.sdesimeur.android.gpsfiction.classes;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.CallSuper;

import com.sdesimeur.android.gpsfiction.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public abstract class GpsFictionThing {
    protected transient GpsFictionData mGpsFictionData = null;

    protected class Identification {
        public int nameId = 0;
        public int replacementNameInLongId = 0;
        public int numInTable4LongId = -1;
        public int definitionId = 0;
        public int iconId = R.drawable.flag_green;
        public void setJson (JSONArray arr) throws JSONException {
                nameId = arr.getInt(0);
                replacementNameInLongId = arr.getInt(1);
                numInTable4LongId = arr.getInt(2);
                definitionId = arr.getInt(3);
                iconId = arr.getInt(4);
        }
        public JSONArray getJson () throws JSONException {
            JSONArray arr = new JSONArray();
                arr.put(0,nameId);
                arr.put(1,replacementNameInLongId);
                arr.put(2,numInTable4LongId);
                arr.put(3,definitionId);
                arr.put(4,iconId);
            return arr;
        }

    }
    protected Identification idx = new Identification();
    protected Boolean visible = true;
    protected Boolean active = true;

    public GpsFictionThing() {
    }

    @CallSuper
    public Bundle getByBundle() throws JSONException {
        Bundle dest = new Bundle();
        dest.putBoolean("active", active);
        dest.putBoolean("visible",visible);
            dest.putString(JSonStrings.IDENTIFICATION,idx.getJson().toString(0));
        return dest;
    }
    @CallSuper
    public JSONObject getJson() throws JSONException {
        JSONObject obj  = new JSONObject();
        obj.put(JSonStrings.ACTIVE,active);
        obj.put(JSonStrings.VISIBLE,visible);
        obj.put(JSonStrings.IDENTIFICATION,idx.getJson());
        return  obj;
    }

    @CallSuper
    public void setByBundle(Bundle in) throws JSONException {
        active = in.getBoolean("active");
        visible = in.getBoolean("visible");
        idx.setJson(new JSONArray(in.getString(JSonStrings.IDENTIFICATION)));
    }
    @CallSuper
    public void setJson (JSONObject obj) throws JSONException {
        active = obj.getBoolean(JSonStrings.ACTIVE);
        visible = obj.getBoolean(JSonStrings.VISIBLE);
        idx.setJson(obj.getJSONArray(JSonStrings.IDENTIFICATION));
    }
    public void init(GpsFictionData gpsFictionData) {
        mGpsFictionData = gpsFictionData;
        mGpsFictionData.addGpsFictionThing(this);
    }

    public void setAttrs(boolean visible, boolean active) {
        setVisible(visible);
        setActive(active);
    }

    public int[] getUUID() {
        int[] ret = new int[3];
        ret[0] = this.getId();
        //ret[1] = this.longId;
        ret[1] = this.getReplacementNameInLongId();
        ret[2] = this.getNumInTable4LongId();
        return ret;

    }

    public boolean compareUUID(int[] uuidToCompare) {
        boolean egal = true;
        for (int i = 0; i < this.getUUID().length; i++)
            egal = (egal && ((this.getUUID()[i]) == (uuidToCompare[i])));
        return egal;
    }

    public void setId(int longId, int replacementNameInLongId) {
        idx.nameId = longId;
        //this.longId = longId;
        idx.replacementNameInLongId = replacementNameInLongId;
        idx.numInTable4LongId = -1;
    }

    public void setId(int longId, int replacementNameInLongId, int numInTable4LongId) {
        this.setId(longId, replacementNameInLongId);
        //this.replacementIsATableInLongId=true;
        idx.numInTable4LongId = numInTable4LongId;
    }

    public int getDefinitionId() {
        return idx.definitionId;
    }

    public void setDefinitionId(int definitionId) {
        idx.definitionId = definitionId;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean v) {
        visible = v;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean a) {
        active = a;
    }

    public int getNameId() {
        return idx.nameId;
    }

    public int getId() {
        return idx.nameId;
    }

    public void setId(int id) {
        idx.nameId = id;
    }

    public int getReplacementNameInLongId() {
        return idx.replacementNameInLongId;
    }

    public int getNumInTable4LongId() {
        return idx.numInTable4LongId;
    }

    public String getName() {
        String name;
        if (this.getReplacementNameInLongId() == 0) {
            name = this.getResources().getString(this.getId());
        } else {
            if (this.getNumInTable4LongId() == -1) {
                name = this.getResources().getString(this.getReplacementNameInLongId());
            } else {
                name = this.getResources().getStringArray(this.getReplacementNameInLongId())[this.getNumInTable4LongId()];
            }
            name = this.getResources().getString(this.getId()).replaceFirst("%%%%", name);
        }
        return name;
    }

    public Resources getResources() {
        return mGpsFictionData.getResources();
    }

    public int getIconId() {
        return idx.iconId;
    }

    public void setIconId(int iconId) {
        idx.iconId = iconId;
    }

    public abstract void validate();
}
