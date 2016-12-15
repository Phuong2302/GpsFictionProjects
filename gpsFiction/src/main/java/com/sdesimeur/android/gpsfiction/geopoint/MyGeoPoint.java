package com.sdesimeur.android.gpsfiction.geopoint;

import android.location.Location;
import android.os.Bundle;

import com.sdesimeur.android.gpsfiction.classes.JSonStrings;
import com.sdesimeur.android.gpsfiction.gpx.beans.Waypoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oscim.core.GeoPoint;


/**
 * Abstraction of geographic point.
 */
public class MyGeoPoint extends GeoPoint {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final double deg2rad = Math.PI / 180;
    private static final double rad2deg = 180 / Math.PI;
    private static final float erad = 6371.0f;

    private static final int BEARING = 1;
    private static final int DISTANCE = 0;

    //private int latitudeE6;
    //private int longitudeE6;

    /**
     * Creates new MyGeoPoint with given latitude and longitude (both degree).
     *
     * @param lat latitude
     * @param lon longitude
     */
    public MyGeoPoint(double lat, double lon) {
        super(lat, lon);
    }

    public MyGeoPoint(Waypoint wpt) {
        super(wpt.getLatitude(), wpt.getLongitude());
    }

    /**
     * Creates new MyGeoPoint with given latitude and longitude (both microdegree).
     *
     * @param lat latitude
     * @param lon longitude
     * @throws MalformedCoordinateException if any coordinate is incorrect
     */
    public MyGeoPoint(int lat, int lon) {
        super(((double) lat) / 1e6, ((double) lon) / 1e6);
    }

    public MyGeoPoint(MyGeoPoint gp) {
        super(gp.getLatitude(), gp.getLongitude());
    }

    /**
     * Creates new MyGeoPoint with latitude and longitude parsed from string.
     *
     * @param text string to parse
     * @throws MyGeoPointParser.ParseException if the string cannot be parsed
     * @throws MalformedCoordinateException  if any coordinate is incorrect
     * @see MyGeoPointParser.parse()
     */
    public MyGeoPoint(String text) {
        super(MyGeoPointParser.parseLatitude(text), MyGeoPointParser.parseLongitude(text));
    }

    /**
     * Creates new MyGeoPoint with latitude and longitude parsed from strings.
     *
     * @param latText latitude string to parse
     * @param lonText longitude string to parse
     * @throws MyGeoPointParser.ParseException if any argument string cannot be parsed
     * @throws MalformedCoordinateException  if any coordinate is incorrect
     * @see MyGeoPointParser.parse()
     */
    public MyGeoPoint(String latText, String lonText) {
        super(MyGeoPointParser.parseLatitude(latText), MyGeoPointParser.parseLongitude(lonText));
    }

    /**
     * Creates new MyGeoPoint with given Location.
     *
     * @param gp the Location to clone
     */
    public MyGeoPoint(Location loc) {
        super(loc.getLatitude(), loc.getLongitude());
    }

    public MyGeoPoint() {
        this(0, 0);
    }

    public MyGeoPoint(GeoPoint gp) {
        this(gp.getLatitude(),gp.getLongitude());
    }
    public Bundle getByBundle() {
        Bundle dest = new Bundle();
        double[] coord = null;
        coord = new double[]{getLatitude(), getLongitude()};
        dest.putDoubleArray("GeoPoint", coord);
        return dest;
    }
    public static MyGeoPoint setByBundle(Bundle in) {
            double[] coord = null;
            coord = in.getDoubleArray("GeoPoint");
            return new MyGeoPoint(coord[0],coord[1]);
    }
    public JSONArray getJson() throws JSONException {
        JSONArray obj  = new JSONArray();
        obj.put(JSonStrings.MYGEOPOINT.LATITUDE,getLatitudeE6());
        obj.put(JSonStrings.MYGEOPOINT.LONGITUDE,getLongitudeE6());
        return  obj;
    }

    public MyGeoPoint newFromJson (JSONObject obj) throws JSONException {
        return new MyGeoPoint(obj.getInt(String.valueOf(JSonStrings.MYGEOPOINT.LATITUDE)),obj.getInt(String.valueOf(JSonStrings.MYGEOPOINT.LONGITUDE)));
    }

 /*
    public void setMyGeoPoint(Location loc) {
        this.setMyGeoPoint(loc.getLatitude(), loc.getLongitude());
    }
    public void setMyGeoPoint(MyGeoPoint gp) {
        this.setMyGeoPoint(gp.getLatitude(), gp.getLongitude());
    }
*/
 /*   public void setMyGeoPoint( double lat, double lon) {
        this.latitude = lat;
    	this.longitude = lon;
    }
    public void setMyGeoPointE6(int latE6, int lonE6) {
		// TODO Auto-generated method stub
		if (latE6 <= (90000000) && latE6 >= (-90000000)) {
            this.setLatitudeE6(latE6);
        } else {
            throw new MalformedCoordinateException("malformed latitude: " + latE6);
        }
        if (lonE6 <= (180000000) && lonE6 >= (-180000000)) {
            // Prefer 180 degrees rather than the equivalent -180.
            this.setLongitudeE6((lonE6 == (-180000000 )  ? 180000000 : lonE6));
        } else {
            throw new MalformedCoordinateException("malformed longitude: " + lonE6);
        }
	}
*/

    /**
     * Get latitude in microdegree.
     *
     * @return latitude
     */
    public int getLatitudeE6() {
        return (this.latitudeE6);
    }

    /**
     * Get longitude in microdegree.
     *
     * @return longitude
     */
    public int getLongitudeE6() {
        return (this.longitudeE6);
    }

    /**
     * Get distance and bearing from the current point to a target.
     *
     * @param target The target
     * @return An array of floats: the distance in meters, then the bearing in degrees
     */
    private float[] pathTo(final MyGeoPoint target) {
        float[] results = new float[2];
        android.location.Location.distanceBetween(getLatitude(), getLongitude(), target.getLatitude(), target.getLongitude(), results);
        return results;
    }

    /**
     * Calculates distance to given MyGeoPoint in m.
     *
     * @param gp target
     * @return distance in km
     * @throws MyGeoPointException if there is an error in distance calculation
     */
    public float distanceTo(final MyGeoPoint gp) {
        return (pathTo(gp)[DISTANCE] / 1000);
    }

    /**
     * Calculates bearing to given MyGeoPoint in degree.
     *
     * @param gp target
     * @return bearing in degree, in the [0,360[ range
     */
    public float bearingTo(final MyGeoPoint gp) {
        // Android library returns a bearing in the [-180;180] range
        final float bearing = pathTo(gp)[BEARING];
        return bearing < 0 ? bearing + 360 : bearing;
    }

    /**
     * Calculates MyGeoPoint from given bearing and distance.
     *
     * @param bearing  bearing in degree
     * @param distance distance in km
     * @return the projected MyGeoPoint
     */
    public MyGeoPoint project(final double bearing, final double distance) {
        final double rlat1 = getLatitude() * deg2rad;
        final double rlon1 = getLongitude() * deg2rad;
        final double rbearing = bearing * deg2rad;
        final double rdistance = distance / erad;

        final double rlat = Math.asin(Math.sin(rlat1) * Math.cos(rdistance) + Math.cos(rlat1) * Math.sin(rdistance) * Math.cos(rbearing));
        final double rlon = rlon1 + Math.atan2(Math.sin(rbearing) * Math.sin(rdistance) * Math.cos(rlat1), Math.cos(rdistance) - Math.sin(rlat1) * Math.sin(rlat));

        return new MyGeoPoint((int) Math.round(rlat * rad2deg * 1E6), (int) Math.round(rlon * rad2deg * 1E6));
    }

    /**
     * Checks if given MyGeoPoint is identical with this MyGeoPoint.
     *
     * @param gp MyGeoPoint to check
     * @return true if identical, false otherwise
     */
    public boolean isEqualTo(MyGeoPoint gp) {
        return null != gp && gp.getLatitudeE6() == this.getLatitudeE6() && gp.getLongitudeE6() == this.getLongitudeE6();
    }

    /**
     * Checks if given MyGeoPoint is similar to this MyGeoPoint with tolerance.
     *
     * @param gp        MyGeoPoint to check
     * @param tolerance tolerance in km
     * @return true if similar, false otherwise
     */
    public boolean isEqualTo(MyGeoPoint gp, double tolerance) {
        return null != gp && distanceTo(gp) <= tolerance;
    }

    /**
     * Returns formatted coordinates.
     *
     * @param format the desired format
     * @return formatted coordinates
     * @see MyGeoPointFormatter
     */
    public String format(MyGeoPointFormatter format) {
        return format.format(this);
    }

    /**
     * Returns formatted coordinates.
     *
     * @param format the desired format
     * @return formatted coordinates
     * @see MyGeoPointFormatter
     */
    public String format(String format) {
        return MyGeoPointFormatter.format(format, this);
    }

    /**
     * Returns formatted coordinates.
     *
     * @param format the desired format
     * @return formatted coordinates
     * @see MyGeoPointFormatter
     */
    public String format(MyGeoPointFormatter.Format format) {
        return MyGeoPointFormatter.format(format, this);
    }

    /**
     * Returns formatted coordinates with default format.
     * Default format is decimalminutes, e.g. N 52° 36.123 E 010° 03.456
     *
     * @return formatted coordinates
     */
    public String toString() {
        return format(MyGeoPointFormatter.Format.LAT_LON_DECMINUTE);
    }

    public int getX() {
        // TODO Auto-generated method stub
        return this.getLongitudeE6();
    }

    public int getY() {
        // TODO Auto-generated method stub
        return this.getLatitudeE6();
    }

    abstract public static class MyGeoPointException
            extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public MyGeoPointException(String msg) {
            super(msg);
        }
    }

    public static class MalformedCoordinateException
            extends MyGeoPointException {
        private static final long serialVersionUID = 1L;

        public MalformedCoordinateException(String msg) {
            super(msg);
        }
    }

}
