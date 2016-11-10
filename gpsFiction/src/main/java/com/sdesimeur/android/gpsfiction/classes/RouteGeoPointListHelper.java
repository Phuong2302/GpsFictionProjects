package com.sdesimeur.android.gpsfiction.classes;

import android.widget.Toast;

import com.graphhopper.util.DistanceCalc;
import com.graphhopper.util.Helper;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.PointList;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;
import com.sdesimeur.android.gpsfiction.activities.CalcRouteAndSpeakService;
import com.sdesimeur.android.gpsfiction.activities.CalcRouteAndSpeakService;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;
import com.sdesimeur.android.gpsfiction.helpers.DistanceToTextHelper;
import com.sdesimeur.android.gpsfiction.polygon.MyPolygon;

import java.util.Iterator;
import java.util.Locale;

/**
 * Created by sam on 25/10/16.
 */
public class RouteGeoPointListHelper implements PlayerLocationListener {
    private CalcRouteAndSpeakService mCalcRouteAndSpeakService = null;
    private CalcRouteAndSpeakService mCalcRouteAndSpeakService = null;
    private MyPolygon listOfPoints = null;
    private float deltaDistMax = 0.050f;


    public float getDistanceToEnd() {
        return distanceToEnd;
    }

    private float distanceToEnd;
    private float distanceToNxtPoint;
    private String nextInstructionString = "";
    private Translation mTranslation = null;

    public String getNextInstructionString() {
        return nextInstructionString;
    }


    public RouteGeoPointListHelper(CalcRouteAndSpeakService calcRouteAndSpeakService) {
        mCalcRouteAndSpeakService = calcRouteAndSpeakService;
        TranslationMap trm = new TranslationMap();
        trm.doImport();
        mTranslation = trm.getWithFallBack(Locale.getDefault());
//        deltaDistMax = (float) (0.015+(mCalcRouteAndSpeakService.getVehiculeSelectedId()!= R.drawable.pieton?0.015:0));
    }

    public void fillRoutePathLayer () {
        if (mCalcRouteAndSpeakService.getRoutePathLayer()!=null) mCalcRouteAndSpeakService.getRoutePathLayer().setPoints(listOfPoints.getAllGeoPoints());
    }

    @Override
    public void onLocationPlayerChanged(MyGeoPoint playerLocation) {
        if (listOfPoints.pointDistanceMin(playerLocation).distanceTo(playerLocation) > deltaDistMax) {
            stopListenning();
            mCalcRouteAndSpeakService.calcRoutePath();
        } else {
            if (listOfPoints.size() > 2) {
                MyGeoPoint g0 = new MyGeoPoint(listOfPoints.get(0));
                MyGeoPoint g1 = new MyGeoPoint(listOfPoints.get(1));
                boolean toFillRoutePathLayer = false;
                float dPts0Pts1 = g0.distanceTo(g1);
                float dPts0Pl = g0.distanceTo(playerLocation);
                while (dPts0Pts1 < dPts0Pl) {
                    toFillRoutePathLayer = true;
                    listOfPoints.poll();
                    g0 = g1;
                    g1 = new MyGeoPoint(listOfPoints.get(1));
                    dPts0Pts1 = g0.distanceTo(g1);
                    dPts0Pl = g0.distanceTo(playerLocation);
                    if (listOfPoints.size() == 2) break;
                }
                if (toFillRoutePathLayer) {
                    fillRoutePathLayer();
                }
            }
            if (mCalcRouteAndSpeakService.getRoutePath() != null && !mCalcRouteAndSpeakService.isShortestPathRunning()) {
                Instruction nextInst = mCalcRouteAndSpeakService.getRoutePath().getInstructions().find(playerLocation.getLatitude(), playerLocation.getLongitude(), 2000);
                int lastIndex = nextInst.getPoints().size()-1;
                MyGeoPoint gp = new MyGeoPoint(nextInst.getPoints().getLat(lastIndex),nextInst.getPoints().getLon(lastIndex));
                calcDistancePlayerToPoints(playerLocation,gp);
                DistanceToTextHelper dst = new DistanceToTextHelper(distanceToNxtPoint);
                nextInstructionString = mTranslation.tr("web.to_hint", new Object[0]) + " " + dst.getDistanceInText() + ", " + nextInst.getTurnDescription(mTranslation);
                Toast.makeText(mCalcRouteAndSpeakService, nextInstructionString, Toast.LENGTH_LONG).show();
                mCalcRouteAndSpeakService.speak(nextInstructionString);
            }
        }
    }

    private void calcDistancePlayerToPoints(MyGeoPoint locationOfPlayer, MyGeoPoint gp) {
        DistanceCalc distCalc = Helper.DIST_EARTH;
        Iterator <MyGeoPoint> it = listOfPoints.iterator();
        MyGeoPoint p1 = it.next();
        p1 = locationOfPlayer;
        MyGeoPoint p2;
        float dtmp = 0;
        while (it.hasNext()) {
            p2 = it.next();
            dtmp += distCalc.calcDist(p1.getLatitude(),p1.getLongitude(),p2.getLatitude(),p2.getLongitude());
        //    dtmp += p1.distanceTo(p2);
            p1=p2;
            if ((Math.abs(p2.getLatitude()-gp.getLatitude())<0.00005) && (Math.abs(p2.getLongitude()-gp.getLongitude())<0.00005)) distanceToNxtPoint = dtmp/1000;
        }
        distanceToEnd = dtmp/1000;
    }

    public void stopListenning () {
        mCalcRouteAndSpeakService.getmMyLocationListener().removePlayerLocationListener(MyLocationListener.REGISTER.MARKER,this);
    }

    public void startListenning() {
        listOfPoints = new MyPolygon();
        PointList pl = mCalcRouteAndSpeakService.getRoutePath().getPoints();
        for (int i = 0; i < pl.size(); i++) {
            listOfPoints.add(new MyGeoPoint(pl.toGHPoint(i).getLat(), pl.toGHPoint(i).getLon()));
        }
        fillRoutePathLayer();
        mCalcRouteAndSpeakService.getmMyLocationListener().addPlayerLocationListener(MyLocationListener.REGISTER.MARKER,this);
    }
}
