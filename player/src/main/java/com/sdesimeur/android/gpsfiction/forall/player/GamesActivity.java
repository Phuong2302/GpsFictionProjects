package com.sdesimeur.android.gpsfiction.forall.player;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
//import android.support.multidex.MultiDex;


import com.sdesimeur.android.gpsfiction.intent.GpsFictionIntent;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class GamesActivity extends Activity implements GameFragment.OnListFragmentInteractionListener {

    //private boolean isStopped;
    public abstract class AllGpsFictionActivityHelper {
        PackageManager pm ;
        public void doForAllGpsFictionActivity () {
            Intent intent = new Intent(GpsFictionIntent.STARTGAME);
            //intent.setAction(GpsFictionActivity.STARTGAME);
            //intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(GpsFictionIntent.ALLGPSFICTIONCATEGORY);
            pm = getPackageManager();
            List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                ResolveInfo re = (ResolveInfo) iterator.next();
                ComponentName theComponentName = new ComponentName(re.activityInfo.applicationInfo.packageName, re.activityInfo.name);
                action(re,theComponentName);
            }
        }
        public abstract void action(ResolveInfo re, ComponentName theComponentName);
    }

    private void parseExtras (Bundle extras) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = settings.edit();
        String tmp = extras.getString(GpsFictionIntent.LOCALE,null);
        if (tmp != null) ed.putString(GpsFictionIntent.LOCALE,tmp);
        Boolean resetAll = extras.getBoolean(GpsFictionIntent.RESETGAMES,false);
        if (resetAll) {
            AllGpsFictionActivityHelper temp = new AllGpsFictionActivityHelper() {
                @Override
                public void action(ResolveInfo re, ComponentName theComponentName) {
                    Intent intent1 = new Intent();
                    intent1.setAction(GpsFictionIntent.RESETGAMES);
                    intent1.setComponent(theComponentName);
                    startActivity(intent1);
                }
            };
            temp.doForAllGpsFictionActivity();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //MultiDex.install(this);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) parseExtras(extras);
        String localeString = settings.getString(GpsFictionIntent.LOCALE,GpsFictionIntent.DEFAULTPLAYERLOCALE);
        Locale locale = new Locale(localeString);
        //if (!Locale.getDefault().equals(locale)) {
        if (!getResources().getConfiguration().locale.equals(locale)) {
            //Locale.setDefault(locale);
            Configuration cfg = getResources().getConfiguration();
            cfg.setLocale(locale);
            getResources().updateConfiguration(cfg,null) ;
            recreate();
        }
        //Intent myIntent2 = new Intent(this, MyLocationListenerService.class);
        //myIntent2.setAction(MyLocationListenerService.ACTION.STARTFOREGROUND);
        //startService(myIntent2);
        //isStopped = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        super.onConfigurationChanged(newConfig);
        recreate();
    }

    @Override
    public void onListFragmentInteraction(GameFragment.GameItem item) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        // TODO  put the locale in string
        String locale = settings.getString(GpsFictionIntent.LOCALE,GpsFictionIntent.DEFAULTPLAYERLOCALE);
        Intent intent = new Intent();
        intent.setComponent(item.theComponentName);
        intent.setAction(GpsFictionIntent.STARTGAME);
        intent.putExtra(GpsFictionIntent.LOCALE,locale);
        startActivity(intent);
        //isStopped = false;
    }
    @Override
    public void onDestroy () {
        //if (isStopped) {
        //    Intent myIntent2 = new Intent(this, MyLocationListenerService.class);
        //    myIntent2.setAction(MyLocationListenerService.ACTION.STOPFOREGROUND);
        //    startService(myIntent2);
        //}
        super.onDestroy();
    }
}
