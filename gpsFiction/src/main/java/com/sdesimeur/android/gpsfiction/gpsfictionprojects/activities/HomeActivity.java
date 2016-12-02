package com.sdesimeur.android.gpsfiction.gpsfictionprojects.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class HomeActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onResume () {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        ResolveInfo resolveinfo = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).get(0);
        String packageName = resolveinfo.activityInfo.applicationInfo.packageName;
        String name = resolveinfo.activityInfo.name;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String homeDefaultPackageName = settings.getString(AdminActivity.HOMEDEFAULTPACKAGE,packageName);
        String homeDefaultActivityName = settings.getString(AdminActivity.HOMEDEFAULTACTIVITY,name);
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        homeIntent.setComponent(new ComponentName(homeDefaultPackageName,homeDefaultActivityName));
        startActivity(homeIntent);
        super.onResume();
    }

}
