package com.sdesimeur.android.gpsfiction.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity {
    private void launchAppChooser() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
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
        super.onCreate(savedInstanceState);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String homeDefaultPackageName = settings.getString("loadHomeDefaultPackageName","com.sdesimeur.android.gpsfiction.activities");
        String homeDefaultActivityName = settings.getString("loadHomeDefaultActivityName","AdminActivity");
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.setComponent(new ComponentName(homeDefaultPackageName,homeDefaultActivityName));
        startActivity(homeIntent);
    }
}
