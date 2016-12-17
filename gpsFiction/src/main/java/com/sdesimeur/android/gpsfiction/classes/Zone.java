package com.sdesimeur.android.gpsfiction.classes;

import android.location.Location;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;
import com.sdesimeur.android.gpsfiction.gpx.beans.Waypoint;
import com.sdesimeur.android.gpsfiction.helpers.DistanceToTextHelper;
import com.sdesimeur.android.gpsfiction.polygon.MyPolygon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class Zone extends Container implements ZoneEnterOrExitInterface, PlayerLocationListener, PlayerBearingListener, Comparable<Zone> {
    private final static double RAPPORT = 2 * Math.PI / 0.005;
    private final static int NB_MIN_DE_COTES = 8;
    private transient MyGeoPoint nearestPoint2Player = new MyGeoPoint(0,0);
    private transient DistanceToTextHelper distance2Player = new DistanceToTextHelper(0);
    private transient float bearingPlayer = 0;
    private transient float bearing2Zone = 0;
    private transient float anglePlayer2Zone = 0;
    private transient boolean playerIsInThisZone = false;
    private transient float radius = 0; // distance max entre points de zone et centre de zone ou rayon pour une zone circulaire.
    private transient MyGeoPoint centerPoint = null; // moyenne des points ou centre d'une zone circulaire,

    private MyPolygon shape = new MyPolygon(); // contour de zone


    public void setShape(MyPolygon shape) {
        this.shape = shape;
    }

    public static final Comparator<Zone> DISTANCE2PLAYERINCREASING = new Comparator<Zone>() {
            public int compare(Zone z1, Zone z2) {
                return ((Float)(z1.getDistance2Player())).compareTo(((Float)z2.getDistance2Player()));
            }
    };
    public Zone() {
        super();
    }

/*
    public Bundle getByBundle() throws JSONException {
        Bundle toPass = super.getByBundle();
        Bundle dest = new Bundle();
        dest.putBundle("Parent", toPass);
        dest.putBundle("shape",this.shape.getByBundle());
        return dest;
    }

    public void setByBundle(Bundle in) throws JSONException {
        Bundle toPass = in.getBundle("Parent");
        super.setByBundle(toPass);
        shape = new MyPolygon();
        toPass = in.getBundle("shape");
        shape.setByBundle(toPass);
        this.validate();
    }
*/
public JSONObject getJson() throws JSONException {
        JSONObject objsuper = super.getJson();
        JSONObject obj  = new JSONObject();
        obj.put(JSonStrings.PARENTJSON,objsuper);
        obj.put(JSonStrings.MYPOLYGON,shape.getJsonArray());
        return  obj;
    }

    public void setJson (JSONObject obj) throws JSONException {
        super.setJson(obj.getJSONObject(JSonStrings.PARENTJSON));
        shape = new MyPolygon();
        shape.setJsonArray(obj.getJSONArray(JSonStrings.MYPOLYGON));
        validate();
    }

    public void onLocationPlayerChanged(MyGeoPoint playerLocation) {
        this.playerIsInThisZone = this.isInThisZone(playerLocation);
        if (!(this.playerIsInThisZone)) {
            this.setNearestPoint2Player(playerLocation);
        }
    }

    private void setNearestPoint2Player(MyGeoPoint playerLocation) {
        nearestPoint2Player = this.shape.pointDistanceMin(playerLocation);
        distance2Player.setDistanceInKM(this.nearestPoint2Player.distanceTo(playerLocation));
        bearing2Zone = playerLocation.bearingTo(this.nearestPoint2Player);
    }

    public float getDistance2Player() {
        return (this.distance2Player.getDistanceInKM());
    }

    public float getBearing2Zone() {
        return (this.bearing2Zone);
    }

    public void setTransportable(boolean transportable) {
    }

    public void setShape(ArrayList<Waypoint> wpts) {
        Iterator<Waypoint> it = wpts.iterator();
        Waypoint wpt = null;
        while (it.hasNext()) {
            wpt = it.next();
            shape.addMyGeoPoint(new MyGeoPoint(wpt));
        }
        mGpsFictionData.fireZoneChangeListener(this);
    }

    public void setShape(float latitude, float longitude, float radius) {
        this.setShape(new MyGeoPoint(latitude, longitude), radius);
    }

    public void setShape(float[] point, float radius) {
        this.setShape(new MyGeoPoint(point[0], point[1]), radius);
    }

    public void setShape(MyGeoPoint point, float radius) {
        this.setCircularShape(point, radius);
    }

    public void setCircularShape(MyGeoPoint centerPoint, float radius) {
        this.centerPoint = centerPoint;
        this.radius = radius;
        int nbDePas = Math.max(NB_MIN_DE_COTES, (int) Math.floor(RAPPORT * radius));
        for (int i = 0; i < nbDePas; i++) {
            shape.addMyGeoPoint(centerPoint.project((float) (i * 360) / nbDePas, radius));
        }
        mGpsFictionData.fireZoneChangeListener(this);
    }

    public MyGeoPoint getCenterPoint() {
        if (centerPoint == null) {
            double sumLatitude = 0;
            double sumLongitude = 0;
            Iterator<MyGeoPoint> it = this.getShape().iterator();
            while (it.hasNext()) {
                MyGeoPoint gp = it.next();
                sumLatitude += gp.getLatitude();
                sumLongitude += gp.getLongitude();
            }
            int nb = this.getShape().size();
            centerPoint = new MyGeoPoint(sumLatitude / nb, sumLongitude / nb);
        }
        return centerPoint;
    }

    public float getRadius() {
        return this.radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        mGpsFictionData.fireZoneChangeListener(this);
    }

    private float distanceMaxToShape(MyGeoPoint point) {
        float distance = 0;
        for (int i = 0; i < this.shape.size(); i++)
            distance = Math.max(distance, point.distanceTo(this.shape.get(i)));
        return distance;
    }

    public boolean isInThisZone(MyGeoPoint point) {
        return this.shape.contains(point);
    }

    public boolean isInThisZone(Location location) {
        MyGeoPoint geoPoint = new MyGeoPoint(location);
        return (this.isInThisZone(geoPoint));
    }

    public boolean isPlayerInThisZone() {
        return this.playerIsInThisZone;
    }

    public MyPolygon getShape() {
        return this.shape;
    }

    public void setShape(float[][] points) {
        for (int i = 0; i < points.length; i++) {
            shape.addMyGeoPoint(new MyGeoPoint(points[i][0], points[i][1]));
        }
        mGpsFictionData.fireZoneChangeListener(this);
    }

    public String getStringDistance2Player() {
        if (playerIsInThisZone) {
            return mGpsFictionData.getResources().getString(R.string.distanceNull);
        } else {
            return distance2Player.getDistanceInText();
        }
    }

    public String getStringBearing2Go() {
        String directionText;
        if (playerIsInThisZone) {
            directionText = mGpsFictionData.getResources().getString(R.string.noZoneBearing);
        } else {
            float direction = this.getAnglePlayer2Zone();
            directionText = Integer.toString((int) Math.round(direction)) + " °";
        }
        return directionText;
    }

    @Override
    public void onBearingPlayerChanged(float angle) {
        bearingPlayer = angle;
        anglePlayer2Zone = this.getBearing2Zone() - angle;
    }

    public float getAnglePlayer2Zone() {
        return anglePlayer2Zone;
    }

    public float getBearingPlayer() {
        return bearingPlayer;
    }

    public void validate() {
        mGpsFictionData.getmGpsFictionControler().addPlayerLocationListener(GpsFictionControler.REGISTER.ZONE, this);
        mGpsFictionData.getmGpsFictionControler().addPlayerBearingListener(GpsFictionControler.REGISTER.ZONE, this);
        mGpsFictionData.fireZoneChangeListener(this);
    }



    public void setVisible(boolean visible) {
        super.setVisible(visible);
        mGpsFictionData.fireZoneChangeListener(this);
    }

    @Override
    public void onEnter() {
    }

    @Override
    public void onExit() {
    }
    public void init (GpsFictionData gfd) {
        super.init(gfd);
        transportable = false;
    }
    public boolean isSelectedZone() {
        return (this == mGpsFictionData.getSelectedZone());
    }

    @Override
    public int compareTo(Zone zone) {
        return ((Float)getDistance2Player()).compareTo(zone.getDistance2Player());
    }
}

