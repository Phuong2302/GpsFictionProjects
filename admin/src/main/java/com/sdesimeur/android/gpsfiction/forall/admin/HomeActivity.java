package com.sdesimeur.android.gpsfiction.forall.admin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

public class HomeActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //MultiDex.install(this);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onStart () {
        /*
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveinfo = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).get(0);
        String packageName = resolveinfo.activityInfo.applicationInfo.packageName;
        String name = resolveinfo.activityInfo.name;
        */
        String packageName = getPackageName();
        String name = AdminActivity.ADMINACTIVITYCLASSNAME;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String homeDefaultPackageName = settings.getString(AdminActivity.HOMEDEFAULTPACKAGE,packageName);
        String homeDefaultActivityName = settings.getString(AdminActivity.HOMEDEFAULTACTIVITY,name);
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        //homeIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.setComponent(new ComponentName(homeDefaultPackageName,homeDefaultActivityName));
        startActivity(homeIntent);
        this.finish();
        super.onStart();
    }
    @Override
    public void onBackPressed() {
    }
    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        boolean handled = super.onKeyDown(keyCode,event);
        if ((keyCode==KeyEvent.KEYCODE_HOME) && (event.isLongPress())) {
            return true;
        }
        if ((keyCode==KeyEvent.KEYCODE_MENU) && (event.isLongPress())) {
            return true;
        }
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return handled;
    }

}
