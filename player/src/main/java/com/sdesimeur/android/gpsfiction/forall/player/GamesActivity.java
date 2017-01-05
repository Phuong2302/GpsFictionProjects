package com.sdesimeur.android.gpsfiction.forall.player;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;

import com.sdesimeur.android.gpsfiction.intent.GpsFictionIntent;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

//import android.support.multidex.MultiDex;

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
                action(re.activityInfo);
            }
        }
        public abstract void action(ActivityInfo ai);
    }

    private void parseExtras (Bundle extras) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = settings.edit();
        String tmp = extras.getString(GpsFictionIntent.LOCALE,null);
        if (tmp != null) {
            ed.putString(GpsFictionIntent.LOCALE,tmp);
            ed.commit();
        }
        Boolean resetAll = extras.getBoolean(GpsFictionIntent.RESETGAMES,false);
        if (resetAll) {
            AllGpsFictionActivityHelper temp = new AllGpsFictionActivityHelper() {
                @Override
                public void action (ActivityInfo ai) {
                    ComponentName theComponentName = new ComponentName(ai.applicationInfo.packageName, ai.name);
                    Intent intent1 = new Intent();
                    intent1.setAction(GpsFictionIntent.RESETGAMES);
                    intent1.setComponent(theComponentName);
                    startActivity(intent1);
                }
            };
            temp.doForAllGpsFictionActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiDex.install(this);
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
    public void onListFragmentInteraction(GameFragment.GameItem item) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        // TODO  put the locale in string
        String locale = settings.getString(GpsFictionIntent.LOCALE,GpsFictionIntent.DEFAULTPLAYERLOCALE);
        Intent intent = new Intent();
        ComponentName theComponentName = new ComponentName(item.activityInfo.applicationInfo.packageName, item.activityInfo.name);
        intent.setComponent(theComponentName);
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
