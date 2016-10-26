package com.sdesimeur.android.gpsfiction.classes;

import com.graphhopper.util.PointList;
import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.activities.MapFragment;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;

import org.oscim.core.GeoPoint;

import java.util.LinkedList;

/**
 * Created by sam on 25/10/16.
 */
public class RouteGeoPointListAutoClean implements PlayerLocationListener {
    private MapFragment mapFragment = null;
    public LinkedList<GeoPoint> listOfPoints = null;
    private float d1Old = 10000000;
    private float d2Old = 10000000;
    private float deltaDistMax;
    public RouteGeoPointListAutoClean(MapFragment mf) {
        mapFragment = mf;
        d1Old = 10000000;
        d2Old = 10000000;
        deltaDistMax = (float) (0.015+(mapFragment.getVehiculeSelectedId()!= R.drawable.pieton?0.015:0));
        listOfPoints = new LinkedList<>();
        PointList pl = mapFragment.getRoutePath().getPoints();
        for (int i = 0; i < pl.size(); i++) {
            listOfPoints.add(new GeoPoint(pl.toGHPoint(i).getLat(), pl.toGHPoint(i).getLon()));
        }
        fillRoutePathLayer();
        mapFragment.getGpsFictionActivity().getMyLocationListener().addPlayerLocationListener(MyLocationListener.REGISTER.MARKER,this);
    }

    public void fillRoutePathLayer () {
        mapFragment.getRoutePathLayer().setPoints(listOfPoints);
    }

    @Override
    public void onLocationPlayerChanged(PlayerLocationEvent playerLocationEvent) {
        if (listOfPoints.size()>2) {
            MyGeoPoint p = playerLocationEvent.getLocationOfPlayer();
            MyGeoPoint g0 = new MyGeoPoint(listOfPoints.get(0));
            MyGeoPoint g1 = new MyGeoPoint(listOfPoints.get(1));
            MyGeoPoint g2 = new MyGeoPoint(listOfPoints.get(2));
            float d1 = p.distanceTo(g1);
            float d2 = p.distanceTo(g2);
            if (((d1-d1Old)<deltaDistMax) && ((d2-d2Old)<deltaDistMax)) {
                d1Old=Math.min(d1,d1Old);
                d2Old=Math.min(d2,d2Old);
                boolean toFillRoutePathLayer = false;
                float dPts0Pts1 = g0.distanceTo(g1);
                float dPts0Pl = g0.distanceTo(p);
                while (dPts0Pts1 < dPts0Pl) {
                    toFillRoutePathLayer = true;
                    listOfPoints.poll();
                    g0 = g1;
                    g1 = new MyGeoPoint(listOfPoints.get(1));
                    dPts0Pts1 = g0.distanceTo(g1);
                    dPts0Pl = g0.distanceTo(p);
                    if (listOfPoints.size() == 2) break;
                }
                if (toFillRoutePathLayer) fillRoutePathLayer();
            } else {
                mapFragment.calcRoutePath();
            }
        }

    }
}
