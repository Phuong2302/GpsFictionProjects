package com.sdesimeur.android.gpsfiction.classes;

import android.location.Location;
import android.os.Bundle;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.geopoint.GeoPoint;
import com.sdesimeur.android.gpsfiction.gpx.beans.Waypoint;
import com.sdesimeur.android.gpsfiction.polygon.MyPolygon;

import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Zone extends Container implements ZoneEnterOrExitInterface, PlayerLocationListener, PlayerBearingListener, ZoneSelectListener {
    private final static double RAPPORT = 2 * Math.PI / 0.005;
    private final static int NB_MIN_DE_COTES = 8;
    private final boolean transportable = false;
    private Paint paintStroke = null;
    private GeoPoint nearestPoint2Player = new GeoPoint();
    private float distance2Player = 0;
    private float bearingPlayer = 0;
    private float bearing2Zone = 0;
    private float anglePlayer2Zone = 0;
    private boolean playerIsInThisZone = false;
    private Polyline zonePolyline = null;
    private RotatingMarker zoneMarker = null;
    private boolean isSelectedZone = false;
    //	private boolean circularZone = false;
    private float radius = 0; // distance max entre points de zone et centre de zone ou rayon pour une zone circulaire.
    private GeoPoint centerPoint = null; // moyenne des points ou centre d'une zone circulaire,
    private MyPolygon shape = new MyPolygon(); // contour de zone

    /*    static final Parcelable.Creator<Zone> CREATOR = new Parcelable.Creator<Zone>() {
            public Zone createFromParcel(Parcel in) {
                return new Zone(in);
            }
            public Zone[] newArray(int size) {
                return new Zone[size];
            }
        };
    */
    public Zone() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Bundle getByBundle() {
        Bundle toPass = super.getByBundle();
        Bundle dest = new Bundle();
        dest.putBundle("Parent", toPass);
        double[] coord = null;
        boolean val = this.isSelectedZone;
        dest.putBoolean("selectedZone", val);
//    	boolean val [] = { this.isSelectedZone , this.circularZone };
//		dest.putBooleanArray("selectedZone_circularZone",val);
//		dest.putFloat("radius",this.radius);
//		coord = {this.centerPoint.getLatitude() , this.centerPoint.getLongitude()};
//		dest.putDoubleArray("centerPoint",coord);
        Iterator<GeoPoint> it = this.shape.iterator();
        GeoPoint gp = null;
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
        boolean val = in.getBoolean("selectedZone");
        this.isSelectedZone = val;
        double[] coord = null;
//		this.circularZone = val[1];
//		this.radius = in.getFloat("radius");
//		coord = in.getDoubleArray("centerPoint");
//		this.centerPoint = new GeoPoint(coord[0],coord[1]);
        int nbGeoGointInShape = in.getInt("shapeNbPoints");
        for (int index = 0; index < nbGeoGointInShape; index++) {
            coord = in.getDoubleArray("shapePoint" + index);
            this.shape.add(new GeoPoint(coord[0], coord[1]));
        }
        this.registerInListeners();
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
        this.nearestPoint2Player = this.shape.pointDistanceMin(playerLocationEvent.getLocationOfPlayer());
        this.distance2Player = this.nearestPoint2Player.distanceTo(playerLocationEvent.getLocationOfPlayer());
        this.bearing2Zone = playerLocationEvent.getLocationOfPlayer().bearingTo(this.nearestPoint2Player);
    }

    public float getDistance2Player() {
        return (this.distance2Player);
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
            shape.addGeoPoint(new GeoPoint(wpt));
        }
        this.registerInListeners();
    }

    public void setShape(float latitude, float longitude, float radius) {
        this.setShape(new GeoPoint(latitude, longitude), radius);
    }

    public void setShape(float[] point, float radius) {
        this.setShape(new GeoPoint(point[0], point[1]), radius);
    }

    public void setShape(GeoPoint point, float radius) {
//		this.setCenterPoint(point);
//		this.setRadius(radius);
        this.setCircularShape(point, radius);
    }

    public void setCircularShape(GeoPoint centerPoint, float radius) {
//		this.setCircularZone(true);
        this.centerPoint = centerPoint;
        this.radius = radius;
        int nbDePas = Math.max(NB_MIN_DE_COTES, (int) Math.floor(RAPPORT * radius));
        for (int i = 0; i < nbDePas; i++) {
            shape.addGeoPoint(centerPoint.project((float) (i * 360) / nbDePas, radius));
        }
        this.registerInListeners();
    }

    public GeoPoint getCenterPoint() {
        if (centerPoint == null) {
            double sumLatitude = 0;
            double sumLongitude = 0;
            Iterator<GeoPoint> it = this.getShape().iterator();
            while (it.hasNext()) {
                GeoPoint gp = it.next();
                sumLatitude += gp.getLatitude();
                sumLongitude += gp.getLongitude();
            }
            int nb = this.getShape().size();
            centerPoint = new GeoPoint(sumLatitude / nb, sumLongitude / nb);
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
    }

    /*
        public boolean isCircularZone() {
            return this.circularZone;
        }
    */
    private float distanceMaxToShape(GeoPoint point) {
        float distance = 0;
        for (int i = 0; i < this.shape.size(); i++)
            distance = Math.max(distance, point.distanceTo(this.shape.get(i)));
        return distance;
    }

    public boolean isInThisZone(GeoPoint point) {
		/* TODO*/
        return this.shape.contains(point);
    }

    public boolean isInThisZone(Location location) {
        GeoPoint geoPoint = new GeoPoint(location);
        return (this.isInThisZone(geoPoint));
    }

    public boolean isPlayerInThisZone() {
        return this.playerIsInThisZone;
    }

    /*	public GeoPoint getCenterPoint() {
            return this.centerPoint;
        }
    */
    public MyPolygon getShape() {
        return this.shape;
    }

    /*	public void setShape(ArrayList<GeoPoint> gpts){
            this.shape.addAll(gpts);
            this.registerInListeners();
        }
        */
    public void setShape(float[][] points) {
//		double sumLatitude=0;
//		double sumLongitude=0;
        for (int i = 0; i < points.length; i++) {
            shape.addGeoPoint(new GeoPoint(points[i][0], points[i][1]));
//			sumLatitude+=points[i][0];
//			sumLongitude+=points[i][1];
        }
//		this.setCenterPoint(new GeoPoint(sumLatitude/points.length,sumLongitude/points.length));
//		this.setRadius(distanceMaxToShape(this.getCenterPoint()));
        this.registerInListeners();
    }

    public String getStringDistance2Player() {
        float distance = distance2Player;
        String distanceText;
        if (playerIsInThisZone) {
            distanceText = getGpsFictionActivity().getResources().getString(R.string.distanceNull);
        } else if (distance >= 10) {
            distance = (float) ((Math.ceil(distance * 10)) / 10);
            distanceText = Float.toString(distance) + " km";
        } else if ((distance >= 0.800) && (distance < 10)) {
            distance = (float) ((Math.ceil(distance * 100)) / 100);
            distanceText = Float.toString(distance) + " km";
        } else if ((distance >= 0.060) && (distance < 0.800)) {
            distance = (float) Math.ceil(distance * 1000);
            distanceText = Float.toString(distance) + " m";
        } else {
            distance = (float) (Math.ceil(distance * 10000) / 10);
            distanceText = Float.toString(distance) + " m";
        }

//		distanceText = Float.toString(distance) + " km"; 
        return distanceText.replaceFirst("[\\.,]00* ", " ");
    }

    public String getStringBearing2Go() {
        // TODO Auto-generated method stub
        String directionText;
        if (playerIsInThisZone) {
            directionText = this.getGpsFictionActivity().getResources().getString(R.string.noZoneBearing);
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

    public void registerInListeners() {
        this.createMarker();
        this.createPolyline();
        getGpsFictionActivity().getGpsFictionData().addZoneSelectListener(GpsFictionData.REGISTER.ZONE, this);
        getGpsFictionActivity().getMyLocationListener().addPlayerLocationListener(MyLocationListener.REGISTER.ZONE, this);
        getGpsFictionActivity().getMyLocationListener().addPlayerBearingListener(MyLocationListener.REGISTER.ZONE, this);
    }

    private void createMarker() {
        //Drawable drawable = getResources().getDrawable(this.getIconId());
        //Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);
        //this.zoneMarker =  new Marker(this.getCenterPoint(), bitmap, -bitmap.getWidth()/2+1, -bitmap.getHeight()/2+1);
        zoneMarker = new RotatingMarker(getCenterPoint(), getResources(), this.getIconId());
        //this.zoneMarker = new RotatingMarker(this.centerPoint);
        //this.zoneMarker.setResource(getResources(), this.getIconId());
        zoneMarker.setVisible(this.isVisible());
        zoneMarker.register(this.getGpsFictionActivity());
    }

    private void createPolyline() {
        this.paintStroke = AndroidGraphicFactory.INSTANCE.createPaint();
        this.paintStroke.setStyle(Style.FILL);
        this.onZoneSelectChanged(null);
        //paintStroke.setDashPathEffect(new float[] { 25, 15 });
        this.paintStroke.setStrokeWidth(getResources().getDimension(R.dimen.mapzoneborderwidth));
        //paintStroke.setStrokeWidth(8);
        // TODO: new mapsforge version wants an mapsforge-paint, not an android paint.
        // This doesn't seem to support transparceny
        //paintStroke.setAlpha(128);
        Polyline line = new Polyline(this.paintStroke, AndroidGraphicFactory.INSTANCE);
        List<LatLong> geoPoints = line.getLatLongs();
        geoPoints.addAll(this.getShape());
        this.zonePolyline = line;
        this.zonePolyline.setVisible(this.isVisible());
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (this.zoneMarker != null) this.zoneMarker.setVisible(visible);
        if (this.zonePolyline != null) this.zonePolyline.setVisible(visible);
    }

    public Polyline getZonePolyline() {
        return this.zonePolyline;
    }

    public Marker getZoneMarker() {
        return this.zoneMarker;
    }

    public boolean isSelectedZone() {
        return this.isSelectedZone;
    }

    public void setSelectedZone(boolean isSelectedZone) {
        this.isSelectedZone = isSelectedZone;
        if (this.isSelectedZone) {
            this.paintStroke.setColor(getResources().getColor(R.color.mapzoneselectedborder));
        } else {
            this.paintStroke.setColor(getResources().getColor(R.color.mapzoneborder));
        }
    }

    public void onZoneSelectChanged(Zone selectedZone) {
        this.setSelectedZone(this == selectedZone);
    }

    @Override
    public void onEnter() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onExit() {
        // TODO Auto-generated method stub

    }

}

