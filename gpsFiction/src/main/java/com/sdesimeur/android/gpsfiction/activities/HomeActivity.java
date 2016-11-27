package com.sdesimeur.android.gpsfiction.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity {
    private ComponentName appLauncherDefault(final String myPackageName ) {
        ComponentName toRet = null;
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);

        List<IntentFilter> filters = new ArrayList<IntentFilter>();
        filters.add(filter);

        List<ComponentName> activities = new ArrayList<ComponentName>();
        final PackageManager packageManager = (PackageManager) getPackageManager();

        packageManager.getPreferredActivities(filters, activities, null);

        for (ComponentName activity : activities) {
            if (myPackageName.equals(activity.getPackageName())) {
                toRet = new ComponentName(this,AdminActivity.class);
            }
        }
        if (toRet == null ) {

        }
        return toRet;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        ResolveInfo resolveinfo = (ResolveInfo) getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String packageName = resolveinfo.activityInfo.applicationInfo.packageName;
        String name = resolveinfo.activityInfo.name;
        super.onCreate(savedInstanceState);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String homeDefaultPackageName = settings.getString("loadHomeDefaultPackageName",packageName);
        String homeDefaultActivityName = settings.getString("loadHomeDefaultActivityName",name);
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.setComponent(new ComponentName(homeDefaultPackageName,homeDefaultActivityName));
        startActivity(homeIntent);
    }
}
