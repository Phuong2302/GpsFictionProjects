package com.sdesimeur.android.gpsfiction.classes;

import android.widget.Toast;

import com.graphhopper.util.DistanceCalc;
import com.graphhopper.util.Helper;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.PointList;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;
import com.sdesimeur.android.gpsfiction.activities.MapFragment;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;
import com.sdesimeur.android.gpsfiction.helpers.DistanceToTextHelper;
import com.sdesimeur.android.gpsfiction.polygon.MyPolygon;

import java.util.Iterator;
import java.util.Locale;

/**
 * Created by sam on 25/10/16.
 */
public class RouteGeoPointListHelper implements PlayerLocationListener {
    private MapFragment mapFragment = null;
    private MyPolygon listOfPoints = null;
    private float deltaDistMax = 0.050f;
    private float distanceToEnd;
    private float distanceToNxtPoint;
    private String nextInstructionString = "";
    private Translation mTranslation = null;

    public String getNextInstructionString() {
        return nextInstructionString;
    }


    public float getDistanceToEnd() {
        return distanceToEnd;
    }

    public float getDistanceToNxtPoint() {
        return distanceToNxtPoint;
    }

    public RouteGeoPointListHelper(MapFragment mf) {
        mapFragment = mf;
        TranslationMap trm = new TranslationMap();
        trm.doImport();
        mTranslation = trm.getWithFallBack(Locale.getDefault());
//        deltaDistMax = (float) (0.015+(mapFragment.getVehiculeSelectedId()!= R.drawable.pieton?0.015:0));
        listOfPoints = new MyPolygon();
        PointList pl = mapFragment.getRoutePath().getPoints();
        for (int i = 0; i < pl.size(); i++) {
            listOfPoints.add(new MyGeoPoint(pl.toGHPoint(i).getLat(), pl.toGHPoint(i).getLon()));
        }
        fillRoutePathLayer();
        mapFragment.getmMyLocationListener().addPlayerLocationListener(MyLocationListener.REGISTER.MARKER,this);
    }

    public void fillRoutePathLayer () {
        if (mapFragment.getRoutePathLayer()!=null) mapFragment.getRoutePathLayer().setPoints(listOfPoints.getAllGeoPoints());
    }

    @Override
    public void onLocationPlayerChanged(PlayerLocationEvent playerLocationEvent) {
        if (listOfPoints.size()>2) {
            MyGeoPoint p = playerLocationEvent.getLocationOfPlayer();
            MyGeoPoint g0 = new MyGeoPoint(listOfPoints.get(0));
            MyGeoPoint g1 = new MyGeoPoint(listOfPoints.get(1));
            boolean toFillRoutePathLayer = false;
            float dPts0Pts1 = g0.distanceTo(g1);
            float dPts0Pl = g0.distanceTo(p);
            if (listOfPoints.pointDistanceMin(p).distanceTo(p) > deltaDistMax) {
                destroy();
                mapFragment.calcRoutePath();
            }
            while (dPts0Pts1 < dPts0Pl) {
                toFillRoutePathLayer = true;
                listOfPoints.poll();
                g0 = g1;
                g1 = new MyGeoPoint(listOfPoints.get(1));
                dPts0Pts1 = g0.distanceTo(g1);
                dPts0Pl = g0.distanceTo(p);
                if (listOfPoints.size() == 2) break;
            }
            if (toFillRoutePathLayer) {
                fillRoutePathLayer();
            }
        }
        if (mapFragment.getRoutePath() != null && !mapFragment.isShortestPathRunning()) {
            Instruction nextInst = mapFragment.getRoutePath().getInstructions().find(playerLocationEvent.getLocationOfPlayer().getLatitude(), playerLocationEvent.getLocationOfPlayer().getLongitude(), 50);
            DistanceToTextHelper dst = new DistanceToTextHelper(getDistanceToNxtPoint());
            calcDistancePlayerToPoints(playerLocationEvent.getLocationOfPlayer());
            nextInstructionString = mTranslation.tr("web.to_hint", new Object[0]) + " " + dst.getDistanceInText() + ", " + nextInst.getTurnDescription(mTranslation);
            Toast.makeText(mapFragment.getmGpsFictionActivity(), nextInstructionString, Toast.LENGTH_LONG).show();
            mapFragment.getmGpsFictionActivity().speak(nextInstructionString);
        }
    }

    private void calcDistancePlayerToPoints(MyGeoPoint locationOfPlayer) {
        DistanceCalc distCalc = Helper.DIST_EARTH;
        Iterator <MyGeoPoint> it = listOfPoints.iterator();
        MyGeoPoint p1 = it.next();
        p1 = locationOfPlayer;
        MyGeoPoint p2;
        float dtmp = 0;
        while (it.hasNext()) {
            p2 = it.next();
            dtmp += distCalc.calcNormalizedDist(p1.getLatitude(),p1.getLongitude(),p2.getLatitude(),p2.getLongitude());
            p1=p2;
            if ((Math.abs(p1.getLatitude()-p2.getLatitude())<0.00005) && (Math.abs(p1.getLongitude()-p2.getLongitude())<0.00005)) distanceToNxtPoint = dtmp;
        }
        distanceToEnd = dtmp;
    }

    public void destroy () {
        mapFragment.getmMyLocationListener().removePlayerLocationListener(MyLocationListener.REGISTER.MARKER,this);
    }
}
