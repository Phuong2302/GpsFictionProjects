package com.sdesimeur.android.gpsfiction.classes;

import android.content.res.Resources;
import android.os.Bundle;

import com.google.gson.Gson;
import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;

import org.json.JSONException;
import org.oscim.layers.PathLayer;

import java.util.ArrayList;
import java.util.HashMap;
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

    public void setByBundle(Bundle in) throws JSONException {
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
            try {
                myclass = Class.forName(name);
                gft = (GpsFictionThing) (myclass.newInstance());
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
    
 
/*    static final Parcelable.Creator<GpsFictionData> CREATOR
            = new Parcelable.Creator<GpsFictionData>() {
 
    	public GpsFictionData createFromParcel(Parcel in) {
            return new GpsFictionData(in);
        }
 
    	public GpsFictionData[] newArray(int size) {
            return new GpsFictionData[size];
        }
    };*/

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

    /**
     * @param the name of a thing, the thing
     */
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
        // TODO Auto-generated method stub
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


//    public MyLocationListener getmMyLocationListener() {
//        return mMyLocationListener;
//    }

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
