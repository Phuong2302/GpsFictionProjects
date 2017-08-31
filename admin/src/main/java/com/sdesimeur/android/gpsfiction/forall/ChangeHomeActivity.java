package com.sdesimeur.android.gpsfiction.forall;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ChangeHomeActivity extends Activity {
    final HashMap<String, ActivityInfo> string2activityinfo = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changehomeactivity);
        ArrayList<String> homeActivities = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinnerhomeactivityselect, homeActivities);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
        Iterator<ResolveInfo> it = list.iterator();
        while (it.hasNext()) {
            ActivityInfo act = it.next().activityInfo;
            //if (!act.name.equals(HOMEACTIVITYCLASSNAME)) {
            String st = (String) act.loadLabel(pm);
            adapter.add(st);
            string2activityinfo.put(st, act);
            //}
        }
        Spinner sp = findViewById(R.id.HomeActivityListSpinner);
        sp.setAdapter(adapter);

    }

    public void changeHomeActivity(View view) {
        Spinner spinner = findViewById(R.id.HomeActivityListSpinner);
        ActivityInfo resolveInfo = string2activityinfo.get(spinner.getSelectedItem());
        String homePackageName = resolveInfo.applicationInfo.packageName;
        String homeActivityName = resolveInfo.name;
        String packageName = getPackageName();
        String activityName = HomeActivity.class.getName();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        homeIntent.setComponent(new ComponentName(packageName,activityName));
        homeIntent.putExtra(HomeActivity.HOMEDEFAULTACTIVITY,homeActivityName);
        homeIntent.putExtra(HomeActivity.HOMEDEFAULTPACKAGE,homePackageName);
        startActivity(homeIntent);
        this.finish();
    }
}
