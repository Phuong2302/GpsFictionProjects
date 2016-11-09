package com.sdesimeur.android.gpsfiction.activities;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

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


public class GpsFictionActivity extends Activity {
    private static final String TAGFONT = "FONT";
    private FloatingActionButton fabCreate;
    private int lastFabAction;
    private float dYFab;
    private float dXFab;
    private float dX;
    private float dY;
    private static final float MINMOVE = 20;

    public GpsFictionData getmGpsFictionData() {
        return mGpsFictionData;
    }

    public void setmGpsFictionData(GpsFictionData mGpsFictionData) {
        this.mGpsFictionData = mGpsFictionData;
    }

    protected GpsFictionData mGpsFictionData = null;
    protected FragmentManager fragmentManager;
    protected HashSet<MyDialogFragment> dialogFragments = new HashSet<>();
    private MyLocationListener mMyLocationListener = null;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private HashMap<Integer, MyTabFragmentImpl> menuItem2Fragments;
    private int selectedFragmentId = R.id.Zones;
    private void defineFragments() {
        menuItem2Fragments = new HashMap<>();
        String[] fragmentNames = getResources().getStringArray(R.array.fragmentsNames);
        TypedArray menuItems = getResources().obtainTypedArray(R.array.menuItems);
        for (int i = 0; i < fragmentNames.length; i++) {
            String s = fragmentNames[i];
            try {
                Class myclass = Class.forName("com.sdesimeur.android.gpsfiction.activities." + s + "Fragment");
                MyTabFragmentImpl myTabFragment = (MyTabFragmentImpl) (myclass.newInstance());
                Integer id = menuItems.getResourceId(i,0);
                menuItem2Fragments.put(id, myTabFragment);
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Resources.NotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        menuItems.recycle();
    }

    public void setFragmentInContainer() {
        FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
        MyTabFragmentImpl mtf = menuItem2Fragments.get(selectedFragmentId);
        fragTransaction.replace(R.id.container, (Fragment)mtf);
        fragTransaction.commit();
        //	navigationView.getMenu().findItem(selectedFragmentId).setChecked(true);
    }

    public void getReponseFromMyDialogFragment(int why, int reponse) {
        if (why == R.string.dialogCloseTaskTitle) {
            if (reponse == R.string.dialogButtonNo) {
                mGpsFictionData.toSave = false;
                this.finish();
            }
        }
    }

    public MyLocationListener getmMyLocationListener() {
        // TODO Auto-generated method stub
        return mMyLocationListener;
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
                zn.init(mGpsFictionData);
                zn.setId(res);
                zn.setShape(wpts);
                zn.validate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Typeface getFontFromRes(int resource) {
        Typeface tf = null;
        InputStream is = null;
        try {
            is = getResources().openRawResource(resource);
        } catch (Resources.NotFoundException e) {
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Checking for the "menu" key
        //if (keyCode == KeyEvent.KEYCODE_BACK) {
        //}
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    public void floatingAction(View view) {
   //     drawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    public void onBackPressed() {
        if (this.dialogFragments.isEmpty()) {
            MyDialogFragment df = new MyDialogFragment();
            df.init(R.string.dialogCloseTaskTitle, R.string.dialogCloseTaskText);
            df.getButtonsListIds().add(R.string.dialogButtonYes);
            df.getButtonsListIds().add(R.string.dialogButtonNo);
            df.show(this.fragmentManager);
        }
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.LEFT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MultiDex.install(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LOW_PROFILE);
//        getWindow().addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
        if (mMyLocationListener == null) mMyLocationListener = new MyLocationListener();
        mMyLocationListener.init(this);
        if (mGpsFictionData == null) {
            mGpsFictionData = new GpsFictionData();
            mGpsFictionData.setmGpsFictionActivity(this);
        }
        if (savedInstanceState != null) {
            Bundle toPass = savedInstanceState.getBundle("GpsFictionData");
            mGpsFictionData.setByBundle(toPass);
            toPass = savedInstanceState.getBundle("MyLocationListener");
            mMyLocationListener.setByBundle(toPass);
            mMyLocationListener.firePlayerLocationListener();
            mMyLocationListener.firePlayerBearingListener();
            selectedFragmentId = savedInstanceState.getInt("lastSelectedFragmentId",R.id.Zones);
        } else {
            mGpsFictionData.init();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.getMenu().findItem(selectedFragmentId).setChecked(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                selectedFragmentId = menuItem.getItemId();
                setFragmentInContainer();
                drawerLayout.closeDrawer(Gravity.LEFT);
                return true;
            }
        });
        fabCreate = (FloatingActionButton)findViewById(R.id.actionbutton);
        fabCreate.setOnTouchListener(new View.OnTouchListener() {
    	      @Override
    	      public boolean onTouch(View view, MotionEvent event) {
    	          switch (event.getActionMasked()) {
    	              case MotionEvent.ACTION_DOWN:
    	                  dXFab = view.getX() - event.getRawX();
                          dX = event.getRawX();
    	                  dYFab = view.getY() - event.getRawY();
                          dY = event.getRawY();
    	                  lastFabAction = MotionEvent.ACTION_DOWN;
    	                  break;
    	              case MotionEvent.ACTION_UP:
                          if ( ((lastFabAction == MotionEvent.ACTION_MOVE) && (Math.abs(dX-event.getRawX())<MINMOVE) && (Math.abs(dY - event.getRawY())<MINMOVE)) ||
                            (lastFabAction == MotionEvent.ACTION_DOWN) )
                              drawerLayout.openDrawer(Gravity.LEFT);
    	                      //Toast.makeText(getActivity(), "Clicked!", Toast.LENGTH_SHORT).show();
    	                  break;
                      case MotionEvent.ACTION_MOVE:
                          view.setY(event.getRawY() + dYFab);
                          view.setX(event.getRawX() + dXFab);
                          lastFabAction = MotionEvent.ACTION_MOVE;
                          break;
    	              case MotionEvent.ACTION_BUTTON_PRESS:
                          drawerLayout.openDrawer(Gravity.LEFT);
    	              default:
    	                  return false;
    	          }
    	          return true;
    	      }
  	    });
        fragmentManager = getFragmentManager();
        defineFragments();
        setFragmentInContainer();
        testTTS();
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (mGpsFictionData.toSave) {
            Bundle toPass = mGpsFictionData.getByBundle();
            savedInstanceState.putBundle("GpsFictionData", toPass);
            toPass = mMyLocationListener.getByBundle();
            savedInstanceState.putBundle("MyLocationListener", toPass);
            savedInstanceState.putInt("lastSelectedFragmentId", selectedFragmentId);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void testTTS() {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, 0x01);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x01) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                ///// TODO send result to CalcRouteAndSpeakService which "startTts"
            }
        }
    }

}
