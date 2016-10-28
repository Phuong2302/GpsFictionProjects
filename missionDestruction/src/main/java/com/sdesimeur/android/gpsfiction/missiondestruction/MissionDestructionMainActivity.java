package com.sdesimeur.android.gpsfiction.missiondestruction;

import android.os.Bundle;

import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;
import com.sdesimeur.android.gpsfiction.activities.MyDialogFragment;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;

import java.util.HashSet;
import java.util.Iterator;

public class MissionDestructionMainActivity extends GpsFictionActivity {
    public final static float COEF = 10f / 1000f;
    public final static float dist_min = MissionDestructionMainActivity.COEF * 15f;
    public final static float radius_zone_globale = MissionDestructionMainActivity.COEF * 50f;
    public final static float radiusStdZone = MissionDestructionMainActivity.COEF * 4f;
    public final static float radius_zone_clef = MissionDestructionMainActivity.COEF * 10f;
    public final static float radius_zone_prendre_clef = MissionDestructionMainActivity.COEF * 3f;
    public final static float radius_zone_arme = radiusStdZone;
    public final static float radius_zone_munitions = radiusStdZone;
    public final static float radius_zone_explosifs = radiusStdZone;
    private static final int NBZONESENNEMIES = 2;
    private final boolean[] zone_occupee = {false, false, false, false};
    public ZoneGlobale zoneGlobale = null;
    public ZoneClef zoneClef = null;
    public ZoneArme zoneArme = null;
    public ZoneMunitions zoneMunitions = null;
    public ZoneExplosifs zoneExplosifs = null;
    public HashSet<Zone> zoneMultiples = new HashSet<Zone>();
    private int angle = 0;
    private boolean firstDialogBoxNotClose = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getmGpsFictionData().isAllreadyConfigured())) {
            getmGpsFictionData().setRules(R.string.rulesDef);
            getmGpsFictionData().setTitle(R.string.rulesName);
            if ((savedInstanceState==null) || (savedInstanceState.getInt("firstDialogBoxNotClose",0)==0)) {
                MyDialogFragment df = new MyDialogFragment();
                df.init(R.string.dialogFirstTaskTitle, R.string.dialogFirstTaskText);
                df.getButtonsListIds().add(R.string.dialogButtonYes);
                df.getButtonsListIds().add(R.string.dialogButtonNo);
                firstDialogBoxNotClose = true;
                df.show(fragmentManager);
            }
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (firstDialogBoxNotClose) savedInstanceState.putInt("firstDialogBoxNotClose", 1);
    }
    public void onResume() {
        super.onResume();
    }

    public void getReponseFromMyDialogFragment(int why, int reponse) {
        if (why == R.string.dialogFirstTaskTitle) {
            firstDialogBoxNotClose = false;
            if (reponse == R.string.dialogButtonYes) {
                angle = ((int) Math.round(360 * Math.random()));
                createAllZoneAmi();
                createAllZoneEnnemi();
                getmMyLocationListener().firePlayerLocationListener();
                getmGpsFictionData().setAllreadyConfigured(true);
            }
            if (reponse == R.string.dialogButtonNo) {
                setmGpsFictionData(null);
                finish();
            }
        }
        super.getReponseFromMyDialogFragment(why, reponse);
    }

    public void createAllZoneEnnemi() {
        ZoneMine zm = null;
        ZoneCibleEnnemie zce = null;
        ZoneEnnemie ze = null;
        for (int i = 0; i < MissionDestructionMainActivity.NBZONESENNEMIES; i++) {
            zm = new ZoneMine();
            zm.setIdAdverseNum(i);
            zm.initnew(getmGpsFictionData());
            zoneMultiples.add(zm);
            zce = new ZoneCibleEnnemie();
            zce.setIdAdverseNum(i);
            zce.initnew(getmGpsFictionData());
            zoneMultiples.add(zce);
            ze = new ZoneEnnemie();
            ze.setIdAdverseNum(i);
            ze.initnew(getmGpsFictionData());
            zoneMultiples.add(ze);
        }

    }

    public void createZoneAmi(ZoneAmie zone, float radius) {
        MyGeoPoint newZp = findNewCenterZoneAmie(radius);
//		zone.getShape().setCenterPoint(newZp);
//		zone.setCircularShape();
        zone.setShape(newZp, radius);
        zone.validate();
        zoneMultiples.add(zone);
    }

    private MyGeoPoint findNewCenterZoneAmie(float radiusNewZone) {
        int n;
        MyGeoPoint newZp;
        Zone zn;

        do
            n = (int) Math.floor(4 * Math.random());
        while (zone_occupee[n]);
        zone_occupee[n] = true;
        double distance;
        boolean valide = true;
        boolean newvalide;
        do {
            valide = true;
            distance = (radius_zone_globale - radiusNewZone - dist_min) * Math.random() + dist_min;
            newZp = zoneGlobale.getCenterPoint().project((double) (angle + (n * 90)), distance);
            Iterator<Zone> it = zoneMultiples.iterator();
            while (it.hasNext()) {
                zn = it.next();
                newvalide = ((zn.getCenterPoint().distanceTo(newZp) - (zn.getRadius() + radiusNewZone + ZoneAmie.distStdEntreZones)) > 0);
                valide = valide && newvalide;
            }
        } while (!(valide));
        return newZp;
    }

    public void createAllZoneAmi() {
        MyGeoPoint newZp;
        int nameId;
        float radius;
        zoneGlobale = new ZoneGlobale();
        zoneGlobale.init(getmGpsFictionData());
        zoneGlobale.setVisible(false);
        zoneGlobale.setActive(false);
        zoneGlobale.setId(R.string.zoneGlobale);
        newZp = getmMyLocationListener().getPlayerGeoPoint();
        zoneGlobale.setShape(newZp, radius_zone_globale);
        zoneGlobale.validate();

        //Autres Zones
        //  Zone Arme
        nameId = R.string.zoneArme;
        radius = radius_zone_arme;
        zoneArme = new ZoneArme();
        zoneArme.init(getmGpsFictionData());
        zoneArme.setId(nameId);
        createZoneAmi(zoneArme, radius);

        //  Zone Explosifs
        nameId = R.string.zoneExplosifs;
        radius = radius_zone_explosifs;
        zoneExplosifs = new ZoneExplosifs();
        zoneExplosifs.init(getmGpsFictionData());
        zoneExplosifs.setId(nameId);
        createZoneAmi(zoneExplosifs, radius);

        //  Zone Munitions
        nameId = R.string.zoneMunitions;
        radius = radius_zone_munitions;
        zoneMunitions = new ZoneMunitions();
        zoneMunitions.init(getmGpsFictionData());
        zoneMunitions.setId(nameId);
        createZoneAmi(zoneMunitions, radius);

        //  Zone Clef
        nameId = R.string.zoneClef;
        radius = radius_zone_clef;
        zoneClef = new ZoneClef();
        zoneClef.init(getmGpsFictionData());
        zoneClef.setId(nameId);
        createZoneAmi(zoneClef, radius);

        //  Zone Prendre Clef
        ZonePrendreClef zpc;
        nameId = R.string.zonePrendreClef;
        zpc = new ZonePrendreClef();
        zpc.init(getmGpsFictionData());
        zpc.setId(nameId);
        zpc.setVisible(false);
        double distance = (radius_zone_clef - radius_zone_prendre_clef) * Math.random();
        int angle = (int) Math.floor(360 * Math.random());
        newZp = zoneClef.getCenterPoint().project(angle, distance);
        zpc.setShape(newZp, radius_zone_prendre_clef);
        zoneClef.addThingToContainer(zpc);
        zpc.validate();
    }

}
