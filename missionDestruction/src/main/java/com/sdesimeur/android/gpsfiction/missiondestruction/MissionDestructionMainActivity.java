package com.sdesimeur.android.gpsfiction.missiondestruction;

import android.os.Bundle;

import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;
import com.sdesimeur.android.gpsfiction.activities.MyDialogFragment;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.geopoint.GeoPoint;

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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(this.getGpsFictionData().isAllreadyConfigured())) {
            this.getGpsFictionData().setRules(R.string.rulesDef);
            this.getGpsFictionData().setTitle(R.string.rulesName);
            MyDialogFragment df = new MyDialogFragment();
            df.init(this, R.string.dialogFirstTaskTitle, R.string.dialogFirstTaskText);
            df.getButtonsListIds().add(R.string.dialogButtonYes);
            df.getButtonsListIds().add(R.string.dialogButtonNo);
            df.show(this.fragmentManager);
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void getReponseFromMyDialogFragment(int why, int reponse) {
        if (why == R.string.dialogFirstTaskTitle) {
            if (reponse == R.string.dialogButtonYes) {
                this.angle = ((int) Math.round(360 * Math.random()));
                this.createAllZoneAmi();
                this.createAllZoneEnnemi();
                this.getMyLocationListener().firePlayerLocationListener();
                this.getGpsFictionData().setAllreadyConfigured(true);
            }
            if (reponse == R.string.dialogButtonNo) {
                this.gpsFictionData = null;
                this.finish();
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
            zm.init(this);
            //zm.setMainActivity(this);
            this.zoneMultiples.add(zm);
            zce = new ZoneCibleEnnemie();
            zce.setIdAdverseNum(i);
            zce.init(this);
            //zce.setMainActivity(this);
            this.zoneMultiples.add(zce);
            ze = new ZoneEnnemie();
            ze.setIdAdverseNum(i);
            ze.init(this);
            //ze.setMainActivity(this);
            this.zoneMultiples.add(ze);
        }

    }

    public void createZoneAmi(ZoneAmie zone, float radius) {
        GeoPoint newZp = this.findNewCenterZoneAmie(radius);
//		zone.getShape().setCenterPoint(newZp);
//		zone.setCircularShape();
        zone.setShape(newZp, radius);
        this.zoneMultiples.add(zone);
    }

    private GeoPoint findNewCenterZoneAmie(float radiusNewZone) {
        int n;
        GeoPoint newZp;
        Zone zn;

        do
            n = (int) Math.floor(4 * Math.random());
        while (this.zone_occupee[n]);
        this.zone_occupee[n] = true;
        double distance;
        boolean valide = true;
        boolean newvalide;
        do {
            valide = true;
            distance = (radius_zone_globale - radiusNewZone - dist_min) * Math.random() + dist_min;
            newZp = this.zoneGlobale.getCenterPoint().project((double) (this.angle + (n * 90)), distance);
            Iterator<Zone> it = this.zoneMultiples.iterator();
            while (it.hasNext()) {
                zn = it.next();
                newvalide = ((zn.getCenterPoint().distanceTo(newZp) - (zn.getRadius() + radiusNewZone + ZoneAmie.distStdEntreZones)) > 0);
                valide = valide && newvalide;
            }
        } while (!(valide));
        return newZp;
    }

    public void createAllZoneAmi() {
        GeoPoint newZp;
        int nameId;
        float radius;
        this.zoneGlobale = new ZoneGlobale();
        this.zoneGlobale.init(this);
        this.zoneGlobale.setAttrs(false, false);
        this.zoneGlobale.setId(R.string.zoneGlobale);
        newZp = this.getMyLocationListener().getPlayerGeoPoint();
        this.zoneGlobale.setShape(newZp, radius_zone_globale);

        //Autres Zones
        //  Zone Arme
        nameId = R.string.zoneArme;
        radius = radius_zone_arme;
        this.zoneArme = new ZoneArme();
        this.zoneArme.init(this);
        this.zoneArme.setId(nameId);
        this.createZoneAmi(this.zoneArme, radius);

        //  Zone Explosifs
        nameId = R.string.zoneExplosifs;
        radius = radius_zone_explosifs;
        this.zoneExplosifs = new ZoneExplosifs();
        this.zoneExplosifs.init(this);
        this.zoneExplosifs.setId(nameId);
        this.createZoneAmi(this.zoneExplosifs, radius);

        //  Zone Munitions
        nameId = R.string.zoneMunitions;
        radius = radius_zone_munitions;
        this.zoneMunitions = new ZoneMunitions();
        this.zoneMunitions.init(this);
        this.zoneMunitions.setId(nameId);
        this.createZoneAmi(this.zoneMunitions, radius);

        //  Zone Clef
        nameId = R.string.zoneClef;
        radius = radius_zone_clef;
        this.zoneClef = new ZoneClef();
        this.zoneClef.init(this);
        this.zoneClef.setId(nameId);
        this.createZoneAmi(this.zoneClef, radius);

        //  Zone Prendre Clef
        ZonePrendreClef zpc;
        nameId = R.string.zonePrendreClef;
        zpc = new ZonePrendreClef();
        zpc.init(this);
        zpc.setId(nameId);
        zpc.setVisible(false);
        double distance = (radius_zone_clef - radius_zone_prendre_clef) * Math.random();
        int angle = (int) Math.floor(360 * Math.random());
        newZp = this.zoneClef.getCenterPoint().project(angle, distance);
        zpc.setShape(newZp, radius_zone_prendre_clef);
        this.zoneClef.addThingToContainer(zpc);
    }

}
