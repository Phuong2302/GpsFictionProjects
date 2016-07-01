package com.sdesimeur.android.gpsfiction.activities;


import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListener;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.gpx.GPXParser;
import com.sdesimeur.android.gpsfiction.gpx.beans.GPX;
import com.sdesimeur.android.gpsfiction.gpx.beans.Track;
import com.sdesimeur.android.gpsfiction.gpx.beans.Waypoint;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


public class GpsFictionActivity extends AppCompatActivity /*implements TabListener*/ {
    private static final String TAGFONT = "FONT";
    protected GpsFictionData gpsFictionData = null;
    protected FragmentManager fragmentManager;
    protected HashSet<MyDialogFragment> dialogFragments = new HashSet<MyDialogFragment>();
    private MyLocationListener myLocationListener = null;
    private ViewPager mViewPager = null;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private HashMap<Integer, MyTabFragmentImpl> menuItem2Fragments;
    private int selectedFragmentId = R.id.Zones;

    private void defineFragments(String lastSelectedFragmentName) {
        menuItem2Fragments = new HashMap<Integer, MyTabFragmentImpl>();
        String[] fragmentNames = getResources().getStringArray(R.array.fragmentsNames);
        TypedArray menuItems = getResources().obtainTypedArray(R.array.menuItems);
        for (int i = 0; i < fragmentNames.length; i++) {
            String s = fragmentNames[i];
            try {
                Class myclass = Class.forName("com.sdesimeur.android.gpsfiction.activities." + s + "Fragment");
                MyTabFragmentImpl myTabFragment = (MyTabFragmentImpl) (myclass.newInstance());
                Integer id = menuItems.getResourceId(i, 0);
                menuItem2Fragments.put(id, myTabFragment);
                myTabFragment.register(this);
                selectedFragmentId = (myTabFragment.getClass().getName() == lastSelectedFragmentName) ? id : selectedFragmentId;
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        menuItems.recycle();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (this.myLocationListener == null) this.myLocationListener = new MyLocationListener();
        this.myLocationListener.init(this);
        if (this.gpsFictionData == null) {
            this.gpsFictionData = new GpsFictionData();
            this.gpsFictionData.setGpsFictionActivity(this);
        }
        String lastSelectedFragmentName = null;
        if (savedInstanceState != null) {
            Bundle toPass = savedInstanceState.getBundle("GpsFictionData");
            this.gpsFictionData.setByBundle(toPass);
            toPass = savedInstanceState.getBundle("MyLocationListener");
            this.myLocationListener.setByBundle(toPass);
            this.myLocationListener.firePlayerLocationListener();
            this.myLocationListener.firePlayerBearingListener();
            lastSelectedFragmentName = savedInstanceState.getString("lastSelectedFragment");
        } else {
            this.gpsFictionData.init();
        }
        this.defineFragments(lastSelectedFragmentName);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_view);
        this.fragmentManager = getSupportFragmentManager();
        //	this.createFragmentTabs();
		/*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
		final ActionBar supportAB = getSupportActionBar();
        supportAB.setHomeAsUpIndicator(R.drawable.abc_menu_hardkey_panel_mtrl_mult);
        supportAB.setDisplayHomeAsUpEnabled(true);*/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.getMenu().findItem(selectedFragmentId).setChecked(true);
        setFragmentInContainer();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                //	navigationView.getMenu().findItem(selectedFragmentId).setChecked(false);
                selectedFragmentId = menuItem.getItemId();
                setFragmentInContainer();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Checking for the "menu" key
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void setFragmentInContainer() {
        FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
        fragTransaction.replace(R.id.container, (Fragment)menuItem2Fragments.get(selectedFragmentId));
        fragTransaction.commit();
        //	navigationView.getMenu().findItem(selectedFragmentId).setChecked(true);
    }

    public void floatingAction(View view) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        Bundle toPass = this.gpsFictionData.getByBundle();
        savedInstanceState.putBundle("GpsFictionData", toPass);
        toPass = this.myLocationListener.getByBundle();
        savedInstanceState.putBundle("MyLocationListener", toPass);
        savedInstanceState.putString("lastSelectedFragment", menuItem2Fragments.get(selectedFragmentId).getClass().getName());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (this.dialogFragments.isEmpty()) {
            MyDialogFragment df = new MyDialogFragment();
            df.init(this, R.string.dialogCloseTaskTitle, R.string.dialogCloseTaskText);
            df.getButtonsListIds().add(R.string.dialogButtonYes);
            df.getButtonsListIds().add(R.string.dialogButtonNo);
            df.show(this.fragmentManager);
        } else {
            //	this.getGpsFictionFragment().getMyLocationListener().removeGpsFictionUpdates();
        }
        return;
    }

    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
    */
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPostResume() {
        super.onPostResume();
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

    public void displayToast(String txt) {
        Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
    }

    @Override
    public void finish() {
        super.finish();
    }

    public void getReponseFromMyDialogFragment(int why, int reponse) {
        if (why == R.string.dialogCloseTaskTitle) {
//			this.gpsFictionFragment.setRetainInstance(false);
            if (reponse == R.string.dialogButtonNo) {
                this.gpsFictionData = null;
                this.finish();
            }
        }
    }

    public Typeface getFontFromRes(int resource) {
        Typeface tf = null;
        InputStream is = null;
        try {
            is = getResources().openRawResource(resource);
        } catch (NotFoundException e) {
            Log.e(TAGFONT, "Could not find font in resources!");
        }
        String outPath = getCacheDir() + "/tmp" + System.currentTimeMillis() + ".raw";
        try {
            byte[] buffer = new byte[is.available()];
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outPath));
            int l = 0;
            while ((l = is.read(buffer)) > 0)
                bos.write(buffer, 0, l);
            bos.close();
            tf = Typeface.createFromFile(outPath);
            // clean up
            new File(outPath).delete();
        } catch (IOException e) {
            Log.e(TAGFONT, "Error reading in font!");
            return null;
        }
        Log.d(TAGFONT, "Successfully loaded font.");
        return tf;
    }

    public MyLocationListener getMyLocationListener() {
        // TODO Auto-generated method stub
        return this.myLocationListener;
    }

    public void setResourcedZones(int gpxRes) {
        InputStream in = getResources().openRawResource(gpxRes);
        GPXParser p = new GPXParser();
        Zone zn = null;
        try {
            GPX gpx = p.parseGPX(in);
            Iterator<Track> it = gpx.getTracks().iterator();
            Track tr;
            while (it.hasNext()) {
                tr = it.next();
                ArrayList<Waypoint> wpts = tr.getTrackPoints();
                wpts.remove(0);
                String name = "zone" + tr.getName();
                int res = getResources().getIdentifier(name, "string", getPackageName());
                zn = new Zone();
                zn.init(this);
                zn.setId(res);
                zn.setShape(wpts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GpsFictionData getGpsFictionData() {
        return gpsFictionData;
    }

}
