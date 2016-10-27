package com.sdesimeur.android.gpsfiction.classes;

import android.location.Location;
import android.os.Bundle;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;
import com.sdesimeur.android.gpsfiction.gpx.beans.Waypoint;
import com.sdesimeur.android.gpsfiction.helpers.DistanceToTextHelper;
import com.sdesimeur.android.gpsfiction.polygon.MyPolygon;

import org.oscim.layers.PathLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class Zone extends Container implements ZoneEnterOrExitInterface, PlayerLocationListener, PlayerBearingListener, Comparable<Zone> {
    private final static double RAPPORT = 2 * Math.PI / 0.005;
    private final static int NB_MIN_DE_COTES = 8;
    private final boolean transportable = false;
    private MyGeoPoint nearestPoint2Player = new MyGeoPoint(0,0);
    private DistanceToTextHelper distance2Player = new DistanceToTextHelper(0);
    private float bearingPlayer = 0;
    private float bearing2Zone = 0;
    private float anglePlayer2Zone = 0;
    private boolean playerIsInThisZone = false;
//    private Polyline zonePolyline = null;
    private MarkerItem zoneMarkerItem = null;
    private float radius = 0; // distance max entre points de zone et centre de zone ou rayon pour une zone circulaire.
    private MyGeoPoint centerPoint = null; // moyenne des points ou centre d'une zone circulaire,
    private MyPolygon shape = new MyPolygon(); // contour de zone
    private MarkerSymbol zoneMarkerSymbol = null;
    private PathLayer zonePathLayer = null;

    /*    static final Parcelable.Creator<Zone> CREATOR = new Parcelable.Creator<Zone>() {
            public Zone createFromParcel(Parcel in) {
                return new Zone(in);
            }
            public Zone[] newArray(int size) {
                return new Zone[size];
            }
        };
    */
    public static final Comparator<Zone> DISTANCE2PLAYERINCREASING =
                                        new Comparator<Zone>() {
            public int compare(Zone z1, Zone z2) {
                return ((Float)(z1.getDistance2Player())).compareTo(((Float)z2.getDistance2Player()));
            }
    };
    public Zone() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Bundle getByBundle() {
        Bundle toPass = super.getByBundle();
        Bundle dest = new Bundle();
        dest.putBundle("Parent", toPass);
        double[] coord = null;
        boolean val = (this == getmGpsFictionData().getSelectedZone());
        dest.putBoolean("selectedZone", val);
//    	boolean val [] = { this.isSelectedZone , this.circularZone };
//		dest.putBooleanArray("selectedZone_circularZone",val);
//		dest.putFloat("radius",this.radius);
//		coord = {this.centerPoint.getLatitude() , this.centerPoint.getLongitude()};
//		dest.putDoubleArray("centerPoint",coord);
        Iterator<MyGeoPoint> it = this.shape.iterator();
        MyGeoPoint gp = null;
        dest.putInt("shapeNbPoints", this.shape.size());
        int index = 0;
        while (it.hasNext()) {
            gp = it.next();
            coord = new double[]{gp.getLatitude(), gp.getLongitude()};
            dest.putDoubleArray("shapePoint" + index, coord);
            index++;
        }
        return dest;
    }

    public void setByBundle(Bundle in) {
        Bundle toPass = in.getBundle("Parent");
        super.setByBundle(toPass);
        boolean isSelectedZone = in.getBoolean("selectedZone");
        double[] coord = null;
//		this.circularZone = val[1];
//		this.radius = in.getFloat("radius");
//		coord = in.getDoubleArray("centerPoint");
//		this.centerPoint = new MyGeoPoint(coord[0],coord[1]);
        int nbGeoGointInShape = in.getInt("shapeNbPoints");
        for (int index = 0; index < nbGeoGointInShape; index++) {
            coord = in.getDoubleArray("shapePoint" + index);
            this.shape.add(new MyGeoPoint(coord[0], coord[1]));
        }
        this.validate();
        if (isSelectedZone) getmGpsFictionData().setSelectedZone(this);
    }

    /*public boolean isThisZoneSelected() {
        return thisZoneSelected;
	}*/

    public void onLocationPlayerChanged(PlayerLocationEvent playerLocationEvent) {
        this.playerIsInThisZone = this.isInThisZone(playerLocationEvent.getLocationOfPlayer());
        if (!(this.playerIsInThisZone)) {
            this.setNearestPoint2Player(playerLocationEvent);
        }
    }

    private void setNearestPoint2Player(PlayerLocationEvent playerLocationEvent) {
        nearestPoint2Player = this.shape.pointDistanceMin(playerLocationEvent.getLocationOfPlayer());
        distance2Player.setDistanceInKM(this.nearestPoint2Player.distanceTo(playerLocationEvent.getLocationOfPlayer()));
        bearing2Zone = playerLocationEvent.getLocationOfPlayer().bearingTo(this.nearestPoint2Player);
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
        getmGpsFictionData().fireZoneChangeListener(this);
    }

    public void setShape(float latitude, float longitude, float radius) {
        this.setShape(new MyGeoPoint(latitude, longitude), radius);
    }

    public void setShape(float[] point, float radius) {
        this.setShape(new MyGeoPoint(point[0], point[1]), radius);
    }

    public void setShape(MyGeoPoint point, float radius) {
//		this.setCenterPoint(point);
//		this.setRadius(radius);
        this.setCircularShape(point, radius);
    }

    public void setCircularShape(MyGeoPoint centerPoint, float radius) {
//		this.setCircularZone(true);
        this.centerPoint = centerPoint;
        this.radius = radius;
        int nbDePas = Math.max(NB_MIN_DE_COTES, (int) Math.floor(RAPPORT * radius));
        for (int i = 0; i < nbDePas; i++) {
            shape.addMyGeoPoint(centerPoint.project((float) (i * 360) / nbDePas, radius));
        }
        getmGpsFictionData().fireZoneChangeListener(this);
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

    /*	public void setCenterPoint (GeoPoint point) {
            this.centerPoint = point;
        }
        */
    public float getRadius() {
        return this.radius;
    }

    /*	public void setCircularZone (boolean circular) {
            this.circularZone=circular;
        }
    */
    public void setRadius(float radius) {
        this.radius = radius;
        getmGpsFictionData().fireZoneChangeListener(this);
    }

    /*
        public boolean isCircularZone() {
            return this.circularZone;
        }
    */
    private float distanceMaxToShape(MyGeoPoint point) {
        float distance = 0;
        for (int i = 0; i < this.shape.size(); i++)
            distance = Math.max(distance, point.distanceTo(this.shape.get(i)));
        return distance;
    }

    public boolean isInThisZone(MyGeoPoint point) {
		/* TODO*/
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
        getmGpsFictionData().fireZoneChangeListener(this);
    }

    public String getStringDistance2Player() {
        if (playerIsInThisZone) {
            return getmGpsFictionActivity().getResources().getString(R.string.distanceNull);
        } else {
            return distance2Player.getDistanceInText();
        }
    }

    public String getStringBearing2Go() {
        // TODO Auto-generated method stub
        String directionText;
        if (playerIsInThisZone) {
            directionText = getmGpsFictionActivity().getResources().getString(R.string.noZoneBearing);
        } else {
            float direction = this.getAnglePlayer2Zone();
            directionText = Integer.toString((int) Math.round(direction)) + " °";
        }
        return directionText;
    }

    @Override
    public void onBearingPlayerChanged(PlayerBearingEvent playerBearingEvent) {
        // TODO Auto-generated method stub
        float a = playerBearingEvent.getBearing();
        bearingPlayer = a;
        anglePlayer2Zone = this.getBearing2Zone() - a;
    }

    public float getAnglePlayer2Zone() {
        // TODO Auto-generated method stub
        return anglePlayer2Zone;
    }

    public float getBearingPlayer() {
        // TODO Auto-generated method stub
        return bearingPlayer;
    }

    public void validate() {
        super.validate();
       // getGpsFictionActivity().getGpsFictionData().addZoneSelectListener(GpsFictionData.REGISTER.ZONE, this);
        getmMyLocationListener().addPlayerLocationListener(MyLocationListener.REGISTER.ZONE, this);
        getmMyLocationListener().addPlayerBearingListener(MyLocationListener.REGISTER.ZONE, this);
        getmGpsFictionData().fireZoneChangeListener(this);
    }



    public void setVisible(boolean visible) {
        super.setVisible(visible);
        getmGpsFictionData().fireZoneChangeListener(this);
    }

    @Override
    public void onEnter() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onExit() {
        // TODO Auto-generated method stub

    }

    public boolean isSelectedZone() {
        return (this == getmGpsFictionData().getSelectedZone());
    }

    @Override
    public int compareTo(Zone zone) {
        return ((Float)getDistance2Player()).compareTo(zone.getDistance2Player());
    }
}

