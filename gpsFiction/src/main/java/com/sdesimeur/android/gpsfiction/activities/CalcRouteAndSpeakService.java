package com.sdesimeur.android.gpsfiction.activities;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.util.BikeFlagEncoder;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.FootFlagEncoder;
import com.graphhopper.util.DistanceCalc;
import com.graphhopper.util.Helper;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;
import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListenerService;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.VehiculeSelectedIdListener;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;
import helpers.DistanceToTextHelper;
import com.sdesimeur.android.gpsfiction.polygon.MyPolygon;

import org.oscim.layers.PathLayer;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class CalcRouteAndSpeakService extends Service implements TextToSpeech.OnInitListener, PlayerLocationListener, ZoneSelectListener, VehiculeSelectedIdListener {
    private static int NOTIFICATIONID = 1024;
    private GpsFictionControler gpsFictionControler;

    public PathLayer getRoutePathLayer() {
        return gpsFictionControler.getRoutePathLayer();
    }


    private MyLocationListenerService mMyLocationListenerService;
    @Override
    public void onLocationPlayerChanged(MyGeoPoint pl) {
        playerLocation = pl;
        calcPathIfNecessar();
        cleanListOfPoints();
    }

    @Override
    public void onZoneSelectChanged(Zone selectedZone, Zone unSelectedZone) {
        destLocation = selectedZone.getCenterPoint();
        if (selectedZone != unSelectedZone) {
            clearAndCalc();
        }
    }

    @Override
    public void onVehiculeSelectedId(int id) {
        if (vehiculeSelectedId != id) {
            vehiculeSelectedId = id;
            clearAndCalc();
        }
    }
    public void clearAndCalc () {
        listOfPoints.clear();
        calcPathIfNecessar();
    }

    private void setDistanceToEnd(float distanceToEnd) {
        this.distanceToEnd = distanceToEnd;
        gpsFictionControler.fireDistanceByRouteChangeListener(distanceToEnd);
    }

    public void setGpsFictionControler(GpsFictionControler gpsFictionControler) {
        this.gpsFictionControler = gpsFictionControler;
        this.gpsFictionControler.addPlayerLocationListener(GpsFictionControler.REGISTER.SERVICE, this);
        this.gpsFictionControler.addZoneSelectListener(GpsFictionControler.REGISTER.SERVICE,this);
        this.gpsFictionControler.addVehiculeSelectedIdListener(this);
        startTts();
    }

    public interface ACTION {
        public static String STARTFOREGROUND = "com.sdesimeur.android.gpsfiction.action.startforeground";
        public static String STOPFOREGROUND = "com.sdesimeur.android.gpsfiction.action.stopforeground";
        public static String CHANGEVEHICULESELECTEDID = "com.sdesimeur.android.gpsfiction.action.vehiculeselectedidchange";
        public static String CHANGEGEOPOINT4ZONESELECTED = "com.sdesimeur.android.gpsfiction.action.changegeopoint4zoneselected";
        public static String CHANGEGEOPOINT4PLAYER = "com.sdesimeur.android.gpsfiction.action.changegeopoint4player";
        public static String SUPRESSONEPOINT = "com.sdesimeur.android.gpsfiction.action.supressonepoint";
        public static String CHANGEROUTEPATH = "com.sdesimeur.android.gpsfiction.action.changeroutepath";
    }
    MyPolygon listOfPoints = new MyPolygon();
    private MyGeoPoint playerLocation = null;
    private MyGeoPoint destLocation = null;
    private TextToSpeech mTts;
    private boolean mTtsOK = false;
    private File ghFolder;
    private GraphHopper hopper;
    static final private String CURRENTAREA = "jeu";
    private volatile boolean shortestPathRunning = false;
    private volatile boolean prepareInProgress = false;
    private PathWrapper routePath = null;
    private HashMap<Integer, FlagEncoder> vehiculeGHEncoding = new HashMap<Integer, FlagEncoder>() {{
        put(R.drawable.compass, null);
        put(R.drawable.pieton, new FootFlagEncoder());
        put(R.drawable.cycle, new BikeFlagEncoder());
        put(R.drawable.auto, new CarFlagEncoder());
    }};
    private int vehiculeSelectedId = R.drawable.compass;
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
    private void finishPrepare() {
        prepareInProgress = false;
    }
    private IBinder myBinder = new MyBinder();
    public class MyBinder extends Binder {
        public CalcRouteAndSpeakService getService() {
            return CalcRouteAndSpeakService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        gpsFictionControler.removeZoneSelectListener(GpsFictionControler.REGISTER.SERVICE,this);
        gpsFictionControler.removeVehiculeSelectedIdListener(this);
        gpsFictionControler.removePlayerLocationListener(GpsFictionControler.REGISTER.SERVICE,this);
        return false;
    }

    boolean isReady() {
        if (hopper != null) return true;
        if (prepareInProgress) return false;
        return false;
    }

    public boolean isShortestPathRunning() {
        return shortestPathRunning;
    }

    public CalcRouteAndSpeakService() {
    }


    @Override
    public void onCreate() {
        prepareInProgress = true;
        shortestPathRunning = false;
        boolean greaterOrEqKitkat = Build.VERSION.SDK_INT >= 19;
        File dir = null;
        if (greaterOrEqKitkat) {
            //dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            dir = Environment.getExternalStorageDirectory();
        } else {
            dir = Environment.getExternalStorageDirectory();
        }
        dir = new File(dir, "/sdesimeur/");
        this.ghFolder = new File(dir, "/graphhopper/");
        loadGraphStorage();
        TranslationMap trm = new TranslationMap();
        trm.doImport();
        mTranslation = trm.getWithFallBack(Locale.getDefault());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bd = null;
        switch (intent.getAction()) {
            case ACTION.STARTFOREGROUND:
                Intent notificationIntent = new Intent(this, GpsFictionActivity.class);
                notificationIntent.setAction(ACTION.STARTFOREGROUND);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    notification = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.bearing)
                        .setContentTitle("GpsFictionService")
                        .setContentText("Started")
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(pendingIntent)
                        .build();
                }
                this.startForeground(NOTIFICATIONID, notification);
                break;
            case ACTION.STOPFOREGROUND:
                this.stopForeground(true);
                this.stopSelf();
                break;
            default:
                break;
        }

        return Service.START_REDELIVER_INTENT;
        //return super.onStartCommand(intent, flags, startId);
    }

    private void turnResponseToListOfPoints(GHResponse resp) {
        routePath = resp.getBest();
        PointList pl = routePath.getPoints();
        for (int i = 0; i < pl.size(); i++) {
            listOfPoints.add(new MyGeoPoint(pl.toGHPoint(i).getLat(), pl.toGHPoint(i).getLon()));
        }
        fillRoutePathLayer();
    }

    private void calcLinePath (){
        listOfPoints.clear();
        listOfPoints.add(playerLocation);
        listOfPoints.add(destLocation);
        fillRoutePathLayer();
        DistanceCalc distCalc = Helper.DIST_EARTH;
        setDistanceToEnd(((float) distCalc.calcDist(playerLocation.getLatitude(),playerLocation.getLongitude(),destLocation.getLatitude(),destLocation.getLongitude()))/1000);
    }
    public void calcPathIfNecessar() {
        if ((playerLocation != null) && (destLocation != null)) {
            if (vehiculeSelectedId == R.drawable.compass) {
                calcLinePath();
            } else {
                if (listOfPoints.size() == 0) {
                    calcRoutePath();
                } else {
                    fillRoutePathLayer();
                }
            }
        } else {
            if (getRoutePathLayer()!=null) getRoutePathLayer().clearPath();
        }
    }
    public void calcRoutePath() {
        routePath = null;
        final double fromLat = playerLocation.getLatitude();
        final double fromLon = playerLocation.getLongitude();
        final double toLat = destLocation.getLatitude();
        final double toLon = destLocation.getLongitude();
        shortestPathRunning = true;
        new AsyncTask<Void, Void, GHResponse>() {
            float time;

            protected GHResponse doInBackground(Void... v) {
                while (!(isReady())) {
                }
                StopWatch sw = new StopWatch().start();
                GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon);
                req.setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI);
                req.getHints().put("instructions", true);
                req.getHints().put("calc_points", true);
                req.setLocale(Locale.getDefault());
                req.setVehicle(vehiculeGHEncoding.get(vehiculeSelectedId).toString());
                //req.setWeighting("fastest");
                //hopper.getGraphHopperStorage();
                GHResponse resp = hopper.route(req);
                time = sw.stop().getSeconds();
                return resp;
            }

            protected void onPostExecute(GHResponse resp) {
                if (!resp.hasErrors()) {
                    turnResponseToListOfPoints(resp);
                }
                shortestPathRunning = false;
                cleanListOfPoints();
            }
        }.execute();
    }

    @Override
    public void onDestroy() {
        if (mTts != null) mTts.shutdown();
        mTtsOK = false;
        super.onDestroy();
    }

    void loadGraphStorage() {
        new AsyncTask<Void, Void, Path>() {
            String error = "Pas d'erreur";

            protected Path saveDoInBackground(Void... v) {
                GraphHopper tmpHopp = new GraphHopper().forMobile();
                hopper = tmpHopp;
//                hopper.getCHFactoryDecorator().addWeighting("fastest");
//                hopper.getCHFactoryDecorator().addWeighting("shortest");
                hopper.setCHEnabled(false);
                hopper.setAllowWrites(false);
                hopper.setEnableInstructions(true);
                hopper.setEnableCalcPoints(true);
                hopper.load(new File(ghFolder, CURRENTAREA).getAbsolutePath());
                return null;
            }

            @Override
            protected Path doInBackground(Void... params) {
                try {
                    return saveDoInBackground(params);
                } catch (Throwable t) {
                    error = t.getMessage();
                    Log.e("AsynTask GraphHopper", error);
                    return null;
                }
            }

            protected void onPostExecute(Path o) {
                finishPrepare();
            }
        }.execute();
    }

    @Override
    public void onInit(int status) {
        mTtsOK = (status == TextToSpeech.SUCCESS);
    }

    public void speak(String txt) {
        if (mTtsOK) {
            //String utteranceId=this.hashCode() + "";
            if (!mTts.isSpeaking()) mTts.speak(txt, TextToSpeech.QUEUE_FLUSH, null);
            Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
        }
    }

    protected void startTts() {
        mTts = new TextToSpeech(this, this);
        if (mTts.isLanguageAvailable(Locale.getDefault()) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
            mTts.setLanguage(Locale.getDefault());
        } else if (mTts.isLanguageAvailable(Locale.ENGLISH) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
            mTts.setLanguage(Locale.ENGLISH);
        }
        mTts.setSpeechRate(1); // 1 est la valeur par défaut. Une valeur inférieure rendra l'énonciation plus lente, une valeur supérieure la rendra plus rapide.
        mTts.setPitch(1); // 1 est la valeur par défaut. Une valeur inférieure rendra l'énonciation plus grave, une valeur supérieure la rendra plus aigue.
        //} else {
        // Echec, aucun moteur n'a été trouvé, on propose à l'utilisateur d'en installer un depuis le Market
        //    Intent installIntent = new Intent();
        //    installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        //    startActivity(installIntent);
    }

    public PathWrapper getRoutePath() {
        return routePath;
    }

    public void fillRoutePathLayer () {
        if (getRoutePathLayer()!=null) {
            getRoutePathLayer().setPoints(listOfPoints.getAllGeoPoints());
        }
    }

    public void cleanListOfPoints() {
        if ((listOfPoints.size() !=0) && (vehiculeSelectedId!=R.drawable.compass))
            if (listOfPoints.pointDistanceMin(playerLocation).distanceTo(playerLocation) > deltaDistMax) {
                listOfPoints.clear();
                calcPathIfNecessar();
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
                treatNextInstruction();
            }
    }

    private void treatNextInstruction () {
        if (getRoutePath() != null && !isShortestPathRunning()) {
            Instruction nextInst = getRoutePath().getInstructions().find(playerLocation.getLatitude(), playerLocation.getLongitude(), 2000);
            int lastIndex = nextInst.getPoints().size()-1;
            MyGeoPoint gp = new MyGeoPoint(nextInst.getPoints().getLat(lastIndex),nextInst.getPoints().getLon(lastIndex));
            calcDistancePlayerToPoints(playerLocation,gp);
            DistanceToTextHelper dst = new DistanceToTextHelper(distanceToNxtPoint);
            nextInstructionString = mTranslation.tr("web.to_hint", new Object[0]) + " " + dst.getDistanceInText() + ", " + nextInst.getTurnDescription(mTranslation);
            speak(nextInstructionString);
        }

    }

    private void calcDistancePlayerToPoints(MyGeoPoint locationOfPlayer, MyGeoPoint gp) {
        DistanceCalc distCalc = Helper.DIST_EARTH;
        Iterator<MyGeoPoint> it = listOfPoints.iterator();
        MyGeoPoint p1 = it.next();
        p1 = locationOfPlayer;
        MyGeoPoint p2;
        float dtmp = 0;
        while (it.hasNext()) {
            p2 = it.next();
            dtmp += distCalc.calcDist(p1.getLatitude(), p1.getLongitude(), p2.getLatitude(), p2.getLongitude());
            //    dtmp += p1.distanceTo(p2);
            p1 = p2;
            if ((Math.abs(p2.getLatitude() - gp.getLatitude()) < 0.00005) && (Math.abs(p2.getLongitude() - gp.getLongitude()) < 0.00005))
                distanceToNxtPoint = dtmp / 1000;
        }
        setDistanceToEnd(dtmp / 1000);
    }
}
