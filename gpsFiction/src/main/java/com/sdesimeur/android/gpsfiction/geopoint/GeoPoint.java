package com.sdesimeur.android.gpsfiction.geopoint;

import android.location.Location;

import com.sdesimeur.android.gpsfiction.gpx.beans.Waypoint;

import org.mapsforge.core.model.LatLong;

/**
 * Abstraction of geographic point.
 */
public class GeoPoint extends LatLong {
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
     * Creates new GeoPoint with given latitude and longitude (both degree).
     *
     * @param lat latitude
     * @param lon longitude
     */
    public GeoPoint(double lat, double lon) {
        super(lat, lon);
    }

    public GeoPoint(Waypoint wpt) {
        super(wpt.getLatitude(), wpt.getLongitude());
    }

    /**
     * Creates new GeoPoint with given latitude and longitude (both microdegree).
     *
     * @param lat latitude
     * @param lon longitude
     * @throws MalformedCoordinateException if any coordinate is incorrect
     */
    public GeoPoint(int lat, int lon) {
        super(((double) lat) / 1e6, ((double) lon) / 1e6);
    }

    public GeoPoint(GeoPoint gp) {
        super(gp.getLatitude(), gp.getLongitude());
    }

    /**
     * Creates new GeoPoint with latitude and longitude parsed from string.
     *
     * @param text string to parse
     * @throws GeoPointParser.ParseException if the string cannot be parsed
     * @throws MalformedCoordinateException  if any coordinate is incorrect
     * @see GeoPointParser.parse()
     */
    public GeoPoint(String text) {
        super(GeoPointParser.parseLatitude(text), GeoPointParser.parseLongitude(text));
    }

    /**
     * Creates new GeoPoint with latitude and longitude parsed from strings.
     *
     * @param latText latitude string to parse
     * @param lonText longitude string to parse
     * @throws GeoPointParser.ParseException if any argument string cannot be parsed
     * @throws MalformedCoordinateException  if any coordinate is incorrect
     * @see GeoPointParser.parse()
     */
    public GeoPoint(String latText, String lonText) {
        super(GeoPointParser.parseLatitude(latText), GeoPointParser.parseLongitude(lonText));
    }

    /**
     * Creates new GeoPoint with given Location.
     *
     * @param gp the Location to clone
     */
    public GeoPoint(Location loc) {
        super(loc.getLatitude(), loc.getLongitude());
    }

    public GeoPoint() {
        this(0, 0);
    }
    
 /*   
    public void setGeoPoint(Location loc) {
        this.setGeoPoint(loc.getLatitude(), loc.getLongitude());
    }
    public void setGeoPoint(GeoPoint gp) {
        this.setGeoPoint(gp.getLatitude(), gp.getLongitude());
    }
*/
 /*   public void setGeoPoint( double lat, double lon) {
        this.latitude = lat;
    	this.longitude = lon;
    }
    public void setGeoPointE6(int latE6, int lonE6) {
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
     * Get latitude in degree.
     *
     * @return latitude
     */
    public Double getLatitude() {
        return this.latitude;
    }

    /**
     * Get latitude in microdegree.
     *
     * @return latitude
     */
    public int getLatitudeE6() {
        return ((int) (Math.round(this.latitude * 1e6)));
    }

    /**
     * Get longitude in degree.
     *
     * @return longitude
     */
    public Double getLongitude() {
        return this.longitude;
    }

    /**
     * Get longitude in microdegree.
     *
     * @return longitude
     */
    public int getLongitudeE6() {
        return ((int) (Math.round(this.longitude * 1e6)));
    }

    /**
     * Get distance and bearing from the current point to a target.
     *
     * @param target The target
     * @return An array of floats: the distance in meters, then the bearing in degrees
     */
    private float[] pathTo(final GeoPoint target) {
        float[] results = new float[2];
        android.location.Location.distanceBetween(getLatitude(), getLongitude(), target.getLatitude(), target.getLongitude(), results);
        return results;
    }

    /**
     * Calculates distance to given GeoPoint in m.
     *
     * @param gp target
     * @return distance in km
     * @throws GeoPointException if there is an error in distance calculation
     */
    public float distanceTo(final GeoPoint gp) {
        return (pathTo(gp)[DISTANCE] / 1000);
    }

    /**
     * Calculates bearing to given GeoPoint in degree.
     *
     * @param gp target
     * @return bearing in degree, in the [0,360[ range
     */
    public float bearingTo(final GeoPoint gp) {
        // Android library returns a bearing in the [-180;180] range
        final float bearing = pathTo(gp)[BEARING];
        return bearing < 0 ? bearing + 360 : bearing;
    }

    /**
     * Calculates GeoPoint from given bearing and distance.
     *
     * @param bearing  bearing in degree
     * @param distance distance in km
     * @return the projected GeoPoint
     */
    public GeoPoint project(final double bearing, final double distance) {
        final double rlat1 = getLatitude() * deg2rad;
        final double rlon1 = getLongitude() * deg2rad;
        final double rbearing = bearing * deg2rad;
        final double rdistance = distance / erad;

        final double rlat = Math.asin(Math.sin(rlat1) * Math.cos(rdistance) + Math.cos(rlat1) * Math.sin(rdistance) * Math.cos(rbearing));
        final double rlon = rlon1 + Math.atan2(Math.sin(rbearing) * Math.sin(rdistance) * Math.cos(rlat1), Math.cos(rdistance) - Math.sin(rlat1) * Math.sin(rlat));

        return new GeoPoint((int) Math.round(rlat * rad2deg * 1E6), (int) Math.round(rlon * rad2deg * 1E6));
    }

    /**
     * Checks if given GeoPoint is identical with this GeoPoint.
     *
     * @param gp GeoPoint to check
     * @return true if identical, false otherwise
     */
    public boolean isEqualTo(GeoPoint gp) {
        return null != gp && gp.getLatitudeE6() == this.getLatitudeE6() && gp.getLongitudeE6() == this.getLongitudeE6();
    }

    /**
     * Checks if given GeoPoint is similar to this GeoPoint with tolerance.
     *
     * @param gp        GeoPoint to check
     * @param tolerance tolerance in km
     * @return true if similar, false otherwise
     */
    public boolean isEqualTo(GeoPoint gp, double tolerance) {
        return null != gp && distanceTo(gp) <= tolerance;
    }

    /**
     * Returns formatted coordinates.
     *
     * @param format the desired format
     * @return formatted coordinates
     * @see GeoPointFormatter
     */
    public String format(GeoPointFormatter format) {
        return format.format(this);
    }

    /**
     * Returns formatted coordinates.
     *
     * @param format the desired format
     * @return formatted coordinates
     * @see GeoPointFormatter
     */
    public String format(String format) {
        return GeoPointFormatter.format(format, this);
    }

    /**
     * Returns formatted coordinates.
     *
     * @param format the desired format
     * @return formatted coordinates
     * @see GeoPointFormatter
     */
    public String format(GeoPointFormatter.Format format) {
        return GeoPointFormatter.format(format, this);
    }

    /**
     * Returns formatted coordinates with default format.
     * Default format is decimalminutes, e.g. N 52° 36.123 E 010° 03.456
     *
     * @return formatted coordinates
     */
    public String toString() {
        return format(GeoPointFormatter.Format.LAT_LON_DECMINUTE);
    }

    public int getX() {
        // TODO Auto-generated method stub
        return this.getLongitudeE6();
    }

    public int getY() {
        // TODO Auto-generated method stub
        return this.getLatitudeE6();
    }

    abstract public static class GeoPointException
            extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public GeoPointException(String msg) {
            super(msg);
        }
    }

    public static class MalformedCoordinateException
            extends GeoPointException {
        private static final long serialVersionUID = 1L;

        public MalformedCoordinateException(String msg) {
            super(msg);
        }
    }

}
