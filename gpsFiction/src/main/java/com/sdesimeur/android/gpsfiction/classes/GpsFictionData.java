package com.sdesimeur.android.gpsfiction.classes;

import android.content.res.Resources;
import android.os.Bundle;

import com.sdesimeur.android.gpsfiction.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscim.layers.PathLayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class GpsFictionData {
    private static final int INITZOOMLEVEL = 14;
    public static final String BUNDLE = "GpsFictionData";
    public static final String BUNDLEBOOLEANARRAY = "BooleanArray";
    private static final String GFTNARRAY = "GFTNArray";
    private static final String NBGFT = "nbGpsFictionThings";
    private static final String PASSASBUNDLE = "PassAsBundle";
    private static final String GFTINCLUDED = "GFTIncluded";
    protected transient Item inventory = null; // Parcel
    private transient Zone selectedZone = null;
    private transient Zone unSelectedZone = null;

    private transient PathLayer routePathLayer = null;
    private transient GpsFictionControler mGpsFictionControler;
    private transient Resources resources;

    protected boolean allreadyConfigured = false; // Parcel
    private int vehiculeSelectedId = R.drawable.compass;
    private HashSet<GpsFictionThing> gpsFictionThings = new HashSet<>();
    private int zoomLevel = INITZOOMLEVEL;

    public int getZoomLevel() {
        return zoomLevel;
    }
    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public int getZoomLevelIncr() {
        zoomLevel++;
        return zoomLevel;
    }
    public int getZoomLevelDecr() {
        zoomLevel--;
        return zoomLevel;
    }
    public int getVehiculeSelectedId() {
        return vehiculeSelectedId;
    }

    public void setVehiculeSelectedId(int id) {
        if (id != vehiculeSelectedId) {
            vehiculeSelectedId = id;
            mGpsFictionControler.fireVehiculeSelectedIdListener();
        }
    }

    public GpsFictionData() {
    }


    public PathLayer getRoutePathLayer() {
        return routePathLayer;
    }

    public Bundle getByBundle() throws JSONException {
        Bundle dest = new Bundle();
        int index = 0;
        boolean[] valbool = {this.allreadyConfigured};
        dest.putInt("zoomLevel",zoomLevel);
        dest.putInt("lastVehiculeSelectedID",vehiculeSelectedId);
        dest.putBooleanArray("BundleBoolArray", valbool);
        ArrayList<String> gftn = new ArrayList<String>();
        Iterator<GpsFictionThing> it = this.gpsFictionThings.iterator();
        GpsFictionThing gft = null;
        while (it.hasNext()) {
            gft = it.next();
            gftn.add(gft.getClass().getCanonicalName());
        }
        dest.putStringArrayList("GFTNameArray", gftn);
        it = this.gpsFictionThings.iterator();
        Bundle toPass;
        while (it.hasNext()) {
            gft = it.next();
            toPass = gft.getByBundle();
            dest.putBundle("PassAsBundle" + index, toPass);
            index++;
        }
        // sauvegarde des objets contenus dans les autres objets.
        int nbGpsFictionThings = this.gpsFictionThings.size();
        int nbGpsFictionThingsIncluded = 0;
        int indexGpsFictionThings = 0;
        int indexGpsFictionThingsIncluded = 0;
        dest.putInt("NbGFT", nbGpsFictionThings);
        it = this.gpsFictionThings.iterator();
        while (it.hasNext()) {
            gft = it.next();
            dest.putIntArray("GFT" + indexGpsFictionThings, gft.getUUID());
            int[][] gftContains = ((Container) gft).getIncludedThingsByUUID();
            nbGpsFictionThingsIncluded = gftContains.length;
            dest.putInt("NbIncludedGFT" + indexGpsFictionThings, nbGpsFictionThingsIncluded);
            for (indexGpsFictionThingsIncluded = 0; indexGpsFictionThingsIncluded < nbGpsFictionThingsIncluded; indexGpsFictionThingsIncluded++) {
                dest.putIntArray("GFTIncludedArray" + indexGpsFictionThings + "#" + indexGpsFictionThingsIncluded, gftContains[indexGpsFictionThingsIncluded]);
            }
            indexGpsFictionThings++;
        }
        dest.putIntArray("selectedZone", (getSelectedZone()!=null)?getSelectedZone().getUUID(): new int[]{});
        return dest;
    }
    public JSONObject getJson() throws JSONException {
        JSONObject obj  = new JSONObject();
        obj.put(JSonStrings.ALLREADYCONFIGURED,allreadyConfigured);
        obj.put(JSonStrings.ZOOMLEVEL,zoomLevel);
        obj.put(JSonStrings.VEHICULESELECTEDID,vehiculeSelectedId);
        JSONArray arr = new JSONArray();
        Iterator <GpsFictionThing> it = gpsFictionThings.iterator();
        GpsFictionThing gft = null;
        while (it.hasNext()) {
            gft = it.next();
            JSONObject obj1 = new JSONObject();
            obj1.put(JSonStrings.GFTCLASS,gft.getClass().getCanonicalName());
            obj1.put(JSonStrings.GFTDEFINE,gft.getJson());
            int nbgfts = 0;
            if ( gft instanceof Container) {
                nbgfts = ((Container) gft).getMaxIncludedThings());
                JSONArray arr1 = new JSONArray();
                Iterator<GpsFictionThing> it1 = ((Container) gft).getIncludedThings().iterator();
                while (it1.hasNext()) {
                    arr1.put(it.next().getIdx().getJsonArray());
                }
                obj1.put(JSonStrings.INCLUDEDTHINGS, arr1);
            }
            obj1.put(JSonStrings.NBMAXINCLUDEDTHINGS, nbgfts);
            arr.put(obj1);
        }
        obj.put(JSonStrings.ALLGFT,arr);
        obj.put(JSonStrings.SELECTEDZONEID,(getSelectedZone()!=null)?getSelectedZone().getIdx().getJsonArray(): new JSONArray());
        return  obj;
    }

    public void setJson (JSONObject obj) throws JSONException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        allreadyConfigured = obj.getBoolean(JSonStrings.ALLREADYCONFIGURED);
        zoomLevel = obj.getInt(JSonStrings.ZOOMLEVEL);
        vehiculeSelectedId = obj.getInt(JSonStrings.VEHICULESELECTEDID);
        JSONArray arr = obj.getJSONArray(JSonStrings.ALLGFT);
        Class myclass = null;
        GpsFictionThing gft = null;
        for (int index = 0; index < arr.length() ;index++) {
            JSONObject obj1 = arr.getJSONObject(index);
            myclass = Class.forName(obj1.getString(JSonStrings.GFTCLASS));
            gft = (GpsFictionThing) myclass.newInstance();
            gft.init(this);
            gft.setJson(obj1.getJSONObject(JSonStrings.GFTDEFINE));
            gpsFictionThings.add(gft);
        }
        for (int index = 0; index < arr.length() ;index++) {
            JSONObject obj1 = arr.getJSONObject(index);
            int nb = obj1.getInt(JSonStrings.NBMAXINCLUDEDTHINGS);
            if (nb != 0) {
                Container gft1 = ((Container) findGpsFictionThingByJSONArray(arr));
                JSONArray arr1 = obj1.getJSONArray(JSonStrings.INCLUDEDTHINGS);
                for (int index1 = 0 ; index1 < arr1.length() ; index1++) {
                    GpsFictionThing gft2 = findGpsFictionThingByJSONArray(arr1.getJSONArray(index1));
                    gft1.addThingToContainer(gft2);
                }
            }
        }
        JSONArray arr3 = obj.getJSONArray(JSonStrings.SELECTEDZONEID);
        if (arr3.length() != 0) {
            selectedZone = (Zone) findGpsFictionThingByJSONArray(arr3);
        }
    }

    public void setByBundle(Bundle in) throws JSONException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        boolean[] val = new boolean[1];
        Class myclass = null;
        GpsFictionThing gft = null;
        Bundle toPass = null;
        zoomLevel = in.getInt("zoomLevel",zoomLevel);
        vehiculeSelectedId = in.getInt("lastVehiculeSelectedID",vehiculeSelectedId);
        val = in.getBooleanArray("BundleBoolArray");
        this.allreadyConfigured = val[0];
        ArrayList<String> gftn = new ArrayList<String>();
        gftn = in.getStringArrayList("GFTNameArray");
        Iterator<String> it = gftn.iterator();
        int index = 0;
        while (it.hasNext()) {
            String name = it.next();
                myclass = Class.forName(name);
                gft = (GpsFictionThing) (myclass.newInstance());
            gft.init(this);
            toPass = in.getBundle("PassAsBundle" + index);
            gft.setByBundle(toPass);
            index++;
            this.gpsFictionThings.add(gft);
        }
        // restauration des objets contenus dans les autres objets.
        int nbGpsFictionThings = in.getInt("NbGFT");
        int nbGpsFictionThingsIncluded = 0;
        int indexGpsFictionThings = 0;
        int indexGpsFictionThingsIncluded = 0;
        int[] uuid = new int[3];
        for (indexGpsFictionThings = 0; indexGpsFictionThings < nbGpsFictionThings; indexGpsFictionThings++) {
            uuid = in.getIntArray("GFT" + indexGpsFictionThings);
            gft = this.findGpsFictionThingByUUID(uuid);
            nbGpsFictionThingsIncluded = in.getInt("NbIncludedGFT" + indexGpsFictionThings);
            for (indexGpsFictionThingsIncluded = 0; indexGpsFictionThingsIncluded < nbGpsFictionThingsIncluded; indexGpsFictionThingsIncluded++) {
                uuid = in.getIntArray("GFTIncludedArray" + indexGpsFictionThings + "#" + indexGpsFictionThingsIncluded);
                ((Container) gft).addThingToContainer(this.findGpsFictionThingByUUID(uuid));
            }
        }
        int[] tmp = in.getIntArray("selectedZone");
        if (tmp.length != 0) setSelectedZone((Zone) findGpsFictionThingByUUID(tmp));
        indexGpsFictionThings++;
    }


    private GpsFictionThing findGpsFictionThingByUUID(int[] uuid) {
        GpsFictionThing gft = null;
        Iterator<GpsFictionThing> it = this.gpsFictionThings.iterator();
        while (it.hasNext()) {
            gft = it.next();
            if (gft.compareUUID(uuid)) break;
        }
        return gft;
    }

    private GpsFictionThing findGpsFictionThingByJSONArray(JSONArray arr) throws JSONException {
        GpsFictionThing gft = null;
        Iterator<GpsFictionThing> it = this.gpsFictionThings.iterator();
        while (it.hasNext()) {
            gft = it.next();
            if (gft.getIdx().getJsonArray().equals(arr)) break;
        }
        return gft;
    }

    public boolean isAllreadyConfigured() {
        return this.allreadyConfigured;
    }

    public void setAllreadyConfigured(boolean allreadyConfigured) {
        this.allreadyConfigured = allreadyConfigured;
    }

    public void init() {
        this.inventory = new Item();
        this.inventory.init(this);
        this.inventory.setId(R.string.inventory);
    }

    public void addGpsFictionThing(GpsFictionThing thing) {
        this.gpsFictionThings.add(thing);
    }

    @SuppressWarnings("rawtypes")
    public HashSet<GpsFictionThing> getGpsFictionThing(final Class cl) {
        HashSet<GpsFictionThing> ret = new HashSet<GpsFictionThing>();
        Iterator<GpsFictionThing> it = this.gpsFictionThings.iterator();
        while (it.hasNext()) {
            GpsFictionThing gft = it.next();
            if (cl.isInstance(gft)) {
                ret.add(gft);
            }
        }
        return ret;
    }

    public HashSet<GpsFictionThing> getGpsFictionThing() {
        return this.gpsFictionThings;
    }

    public Zone getSelectedZone() {
        return this.selectedZone;
    }

    public void setSelectedZone(Zone sZone) {
        unSelectedZone = selectedZone;
        selectedZone = sZone;
        mGpsFictionControler.fireZoneSelectListener();
    }

    public GpsFictionControler getmGpsFictionControler() {
        return mGpsFictionControler;
    }

    public void setRoutePathLayer(PathLayer layer) {
        routePathLayer = layer;
        mGpsFictionControler.clearAndCalc();
    }

    public void setmGpsFictionControler(GpsFictionControler mGpsFictionControler) {
        this.mGpsFictionControler = mGpsFictionControler;
        resources = mGpsFictionControler.getResources();
    }

    public Resources getResources() {
        return resources;
    }

    public Zone getUnSelectedZone() {
        return unSelectedZone;
    }

    public void fireZoneChangeListener(Zone zone) {
        mGpsFictionControler.fireZoneChangeListener(zone);
    }
}
