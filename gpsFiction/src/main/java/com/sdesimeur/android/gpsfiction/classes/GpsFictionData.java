package com.sdesimeur.android.gpsfiction.classes;

import android.os.Bundle;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public class GpsFictionData {
    private static final int INITZOOMLEVEL = 14;
    public static final String BUNDLE = "GpsFictionData";
    public static final String BUNDLEBOOLEANARRAY = "BooleanArray";
    private static final String GFTNARRAY = "GFTNArray";
    private static final String NBGFT = "nbGpsFictionThings";
    private static final String PASSASBUNDLE = "PassAsBundle";
    private static final String GFTINCLUDED = "GFTIncluded";
    protected boolean allreadyConfigured = false; // Parcel
    protected Item inventory = null; // Parcel
    private Zone selectedZone = null;
    private Zone unSelectedZone = null;
    private int rules;
    private int title;
    private GpsFictionActivity mGpsFictionActivity = null;

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

    private int zoomLevel = INITZOOMLEVEL;
//    private MyLocationListener mMyLocationListener = null;
    private HashMap<GpsFictionData.REGISTER, HashSet<ZoneSelectListener>> zoneSelectListener = new HashMap<>();
    private HashSet<GpsFictionThing> gpsFictionThings = new HashSet<>();
    private HashSet<ZoneChangeListener> zoneChangeListener = new HashSet<> ();
    public boolean toSave=true;
    private int vehiculeSelectedId = R.drawable.compass;

    public int getVehiculeSelectedId() {
        return vehiculeSelectedId;
    }

    public void setVehiculeSelectedId(int vehiculeSelectedId) {
        this.vehiculeSelectedId = vehiculeSelectedId;
    }


    public GpsFictionData() {
        for (GpsFictionData.REGISTER i : GpsFictionData.REGISTER.values()) {
            this.zoneSelectListener.put(i, new HashSet<ZoneSelectListener>());
        }
    }

    public Bundle getByBundle() {
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
        return dest;
    }

    public void setByBundle(Bundle in) {
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

    /**
     * @return the rules
     */
    public int getRules() {
        return rules;
    }

    /**
     * @param rules the rules to set
     */
    public void setRules(int rules) {
        this.rules = rules;
    }

    /**
     * @param the name of a thing
     * @return the thing with this name
     */
    /*public GpsFictionThing getThing(String name){
		return this.things.get(name);
	}*/

    /**
     * @return the title
     */
    public int getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(int title) {
        this.title = title;
    }

    public Zone getSelectedZone() {
        return this.selectedZone;
    }

    public void setSelectedZone(Zone sZone) {
        unSelectedZone = selectedZone;
        selectedZone = sZone;
        this.fireZoneSelectListener();
    }

    public void addZoneSelectListener(GpsFictionData.REGISTER type, ZoneSelectListener listener) {
        this.zoneSelectListener.get(type).add(listener);
        if (selectedZone != null) listener.onZoneSelectChanged(selectedZone,unSelectedZone);
    }

    public void removeZoneSelectListener(GpsFictionData.REGISTER type, ZoneSelectListener listener) {
        this.zoneSelectListener.get(type).remove(listener);
    }

    public void fireZoneSelectListener() {
        if (selectedZone != unSelectedZone)
        for (GpsFictionData.REGISTER i : GpsFictionData.REGISTER.values()) {
            for (ZoneSelectListener listener : this.zoneSelectListener.get(i)) {
                listener.onZoneSelectChanged(selectedZone,unSelectedZone);
            }
        }
    }

    public GpsFictionActivity getmGpsFictionActivity() {
        return mGpsFictionActivity;
    }


//    public MyLocationListener getmMyLocationListener() {
//        return mMyLocationListener;
//    }

    public void setmGpsFictionActivity(GpsFictionActivity gpsFictionActivity) {
        mGpsFictionActivity = gpsFictionActivity;
//        mMyLocationListener = mGpsFictionActivity.getmMyLocationListener();
    }
/*
	public String getName() {
		String name;
		if (this.getId() == 0 ) {
			name = this.getGpsFiction().getContext().getResources().getString(this.getId());
		} else {
			if (this.replacementIsATableInLongId) {
				name = this.getGpsFiction().getContext().getResources().getStringArray(this.replacementInNameInLongId)[this.numInTable4LongId];
			} else {
				name = this.getGpsFiction().getContext().getResources().getString(this.replacementInNameInLongId);
			}
			name=this.getGpsFiction().getContext().getResources().getString(this.longId).replaceFirst("%%%%", name);
		}
		return name;
	}
*/

    public static enum REGISTER {
        ZONE,
        HOLDERVIEW,
        ADAPTERVIEW,
        VIEW,
        LAYOUT,
        FRAGMENT
    }
    public void addZoneChangeListener(ZoneChangeListener listener) {
        this.zoneChangeListener.add(listener);
        if (selectedZone != null) listener.onZoneChanged(selectedZone);
    }

    public void removeZoneChangeListener(ZoneChangeListener listener) {
        this.zoneChangeListener.remove(listener);
    }
    public void fireZoneChangeListener(Zone zn) {
            for (ZoneChangeListener listener : this.zoneChangeListener) {
                listener.onZoneChanged(zn);
            }
        if (zn.isSelectedZone()) {
            setSelectedZone(null);
            fireZoneSelectListener();
        }
    }

}
