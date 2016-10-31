package com.sdesimeur.android.gpsfiction.classes;

import org.oscim.layers.PathLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.vector.VectorLayer;
import org.oscim.layers.vector.geometries.PolygonDrawable;

/**
 * Created by sam on 22/10/16.
 */

public class ZoneViewHelper {
    public MarkerItem markerItem = null;
    public PathLayer pathLayer = null;
    public Zone zone = null;
    public PolygonDrawable polygon = null;
    public boolean isPolygonVisible;
    public VectorLayer vectorLayer = null;
    public ZoneViewHelper(Zone zn) {
        zone = zn;
    }
}
