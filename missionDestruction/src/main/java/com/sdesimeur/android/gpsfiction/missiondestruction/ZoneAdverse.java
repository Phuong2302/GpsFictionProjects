package com.sdesimeur.android.gpsfiction.missiondestruction;

import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;

import java.util.Iterator;

public abstract class ZoneAdverse extends Zone {

    private final static float dist_min = MissionDestructionMainActivity.COEF * 3f;
    private final static int replacementTableId = R.array.numeros;
    private transient static float radiusStdZone;
    private transient static float distStdEntreZonesAdverses = MissionDestructionMainActivity.COEF * 3f;
    private transient static float distStdEntreZones;
    private transient MissionDestructionMainActivity mainActivity = null;

    public ZoneAdverse() {
        super();
        // TODO Auto-generated constructor stub
    }
/*
    public Bundle getByBundle() throws JSONException {
        Bundle toPass = super.getByBundle();
        Bundle dest = new Bundle();
        dest.putBundle("Parent", toPass);
        return dest;
    }

    public void setByBundle(Bundle in) throws JSONException {
        Bundle toPass = in.getBundle("Parent");
        super.setByBundle(toPass);
    }
*/
    public void initnew(GpsFictionData gpsFictionData) {
        super.init(gpsFictionData);
        this.findCenterAndSetShapeIfAllIsSet();
        this.validate();
    }

    public void setIdAdverse(int longId, int numInTable4LongId) {
        this.setId(longId, ZoneAdverse.replacementTableId, numInTable4LongId);
        this.findCenterAndSetShapeIfAllIsSet();
    }

    protected void findCenterAndSetShapeIfAllIsSet() {
        if ((mGpsFictionData != null) && (getId() != 0)) findCenterAndSetShape();
    }

    public void findCenterAndSetShape() {
        float radiusNewZone = ZoneAdverse.radiusStdZone;
        try {
            radiusNewZone = this.getClass().getDeclaredField("radiusStdZone").getFloat(null);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        double distance;
        MyGeoPoint newZp;
        Zone zn;
        double angle;
        boolean valideZoneMultiple = true;
//		boolean valideZoneAmie=true;
        float d1 = 0;
        float d = ZoneAdverse.distStdEntreZones;
        do {
            valideZoneMultiple = true;
//			valideZoneAmie=true;
            distance = (MissionDestructionMainActivity.radius_zone_globale - radiusNewZone - ZoneAdverse.dist_min) * Math.random() + ZoneAdverse.dist_min;
            angle = Math.random() * 360;
            newZp = this.getMissionDestructionActivity().zoneGlobale.getCenterPoint().project(angle, distance);
            Iterator<Zone> it = this.getMissionDestructionActivity().zoneMultiples.iterator();
            while (it.hasNext()) {
                zn = it.next();
                if (zn != this) {
                    //this.getClass().cast(zn);
                    try {
                        d = (this.getClass().isInstance(zn)) ? zn.getClass().getDeclaredField("distStdEntreZones").getFloat(zn) : ZoneAdverse.distStdEntreZonesAdverses;
                        d = ZoneAdverse.distStdEntreZonesAdverses;
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    d1 = newZp.distanceTo(zn.getCenterPoint());
                    valideZoneMultiple = valideZoneMultiple && ((d1 - (zn.getRadius() + radiusNewZone + d)) > 0);
                }
            }
//    		it = ZonesAmies.zonesAmies.iterator();
//    		while(it.hasNext()) {
//    			type=it.next();
//    			zn = (Zone) Zone.getById(type);
//				valideZoneAmie = valideZoneAmie && ((newZp.distanceTo(zn.getCenterPoint()) - ( zn.getRadiusMax() + radiusNewZone + ZoneAdverse.distStdEntreZones)) > 0);
//    		}

//    	} while (!(valideZoneMultiple  && valideZoneAmie));
        } while (!(valideZoneMultiple));
        this.setShape(newZp, radiusNewZone);
    }

    public abstract void setIdAdverseNum(int numInTable4LongId);

    public MissionDestructionMainActivity getMissionDestructionActivity() {
        return (MissionDestructionMainActivity) mGpsFictionData.getmGpsFictionControler().getActivity();
    }
}
