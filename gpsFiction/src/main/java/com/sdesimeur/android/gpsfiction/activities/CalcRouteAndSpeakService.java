package com.sdesimeur.android.gpsfiction.activities;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.util.BikeFlagEncoder;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.FootFlagEncoder;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.StopWatch;
import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.RouteGeoPointListHelper;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;
import com.sdesimeur.android.gpsfiction.polygon.MyPolygon;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

public class CalcRouteAndSpeakService extends Service implements TextToSpeech.OnInitListener {
    private static int NOTIFICATIONID = 1024;
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
    private RouteGeoPointListHelper mRouteGeoPointListHelper = null;
    private HashMap<Integer, FlagEncoder> vehiculeGHEncoding = new HashMap<Integer, FlagEncoder>() {{
        put(R.drawable.compass, null);
        put(R.drawable.pieton, new FootFlagEncoder());
        put(R.drawable.cycle, new BikeFlagEncoder());
        put(R.drawable.auto, new CarFlagEncoder());
    }};
    private int vehiculeSelectedId = R.drawable.compass;
    private void finishPrepare() {
        prepareInProgress = false;
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
            case ACTION.CHANGEVEHICULESELECTEDID:
                /////// TODO prendre en compte les valeurs nulles
                int temp = intent.getIntExtra("vehiculeSelectedId",R.drawable.compass);
                if (vehiculeSelectedId != temp) calcPathIfNecessar();
                vehiculeSelectedId = temp;
                break;
            case ACTION.CHANGEGEOPOINT4ZONESELECTED:
                /////// TODO prendre en compte les valeurs nulles
                bd = intent.getExtras();
                destLocation = MyGeoPoint.setByBundle(bd);
                calcPathIfNecessar();
                break;
            case ACTION.CHANGEGEOPOINT4PLAYER:
                /////// TODO prendre en compte les valeurs nulles
                bd = intent.getExtras();
                playerLocation = MyGeoPoint.setByBundle(bd);
                calcPathIfNecessar();
                break;
            default:

        }

        return Service.START_REDELIVER_INTENT;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createRouteGeoPointListHelper(GHResponse resp) {
        routePath = resp.getBest();
        if (mRouteGeoPointListHelper == null)
            mRouteGeoPointListHelper = new RouteGeoPointListHelper(this);
        mRouteGeoPointListHelper.startListenning();
    }

    private void calcLinePath (){
        listOfPoints.clear();
        listOfPoints.add(playerLocation);
        listOfPoints.add(destLocation);
    }
    private void calcPathIfNecessar() {
        if ((playerLocation != null) && (destLocation != null)) {
            if (vehiculeSelectedId == R.drawable.compass) {
                calcLinePath();
            } else {
                if (listOfPoints.size() == 0) calcRoutePath();
            }
        } else {
            if (routePathLayer!=null) routePathLayer.clearPath();
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
                    createRouteGeoPointListHelper(resp);
                }
                shortestPathRunning = false;
            }
        }.execute();
    }

    @Override
    public void onDestroy() {
        if (mTts != null) mTts.shutdown();
        mTtsOK = false;
        if (mRouteGeoPointListHelper != null) mRouteGeoPointListHelper.stopListenning();
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

    public void startLocationListener() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0.5f, this);
        //lastPlayerGeoPoint = playerGeoPoint;
    }

    public void removeGpsFictionUpdates() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
    }
}
