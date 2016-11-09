package com.sdesimeur.android.gpsfiction.activities;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
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

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

public class CalcRouteAndSpeakService extends Service implements TextToSpeech.OnInitListener  {
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
    private HashMap<Integer,FlagEncoder> vehiculeGHEncoding = new HashMap <Integer, FlagEncoder> () {{
        put(R.drawable.compass, null);
        put(R.drawable.pieton, new FootFlagEncoder());
        put(R.drawable.cycle, new BikeFlagEncoder());
        put(R.drawable.auto, new CarFlagEncoder());
    }};


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
    public void onCreate () {
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
        dir = new File (dir, "/sdesimeur/");
        this.ghFolder = new File (dir , "/graphhopper/");
        loadGraphStorage();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
          // Du code
        return super.onStartCommand(intent, flags, startId);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createRouteGeoPointListHelper(GHResponse resp) {
        routePath = resp.getBest();
        if (mRouteGeoPointListHelper == null) mRouteGeoPointListHelper = new RouteGeoPointListHelper(this);
        mRouteGeoPointListHelper.startListenning();
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

            protected GHResponse doInBackground(Void... v ) {
                while ( ! (isReady()))  {}
                StopWatch sw = new StopWatch().start();
                GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon);
                req.setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI);
                req.getHints().put("instructions", true);
                req.getHints().put("calc_points", true);
                req.setLocale(Locale.getDefault());
                req.setVehicle(vehiculeGHEncoding.get(getVehiculeSelectedId()).toString());
                //req.setWeighting("fastest");
                //hopper.getGraphHopperStorage();
                GHResponse resp = hopper.route(req);
                time = sw.stop().getSeconds();
                return resp;
            }

            protected void onPostExecute( GHResponse resp ) {
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
        mTtsOK=false;
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
        mTtsOK=(status == TextToSpeech.SUCCESS);
    }
    public void speak (String txt) {
        if (mTtsOK) {
            //String utteranceId=this.hashCode() + "";
            if (!mTts.isSpeaking()) mTts.speak(txt, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    protected void startTts () {
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
    }
