package com.sdesimeur.android.gpsfiction.activities;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
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
import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;
import com.sdesimeur.android.gpsfiction.classes.JSonStrings;
import com.sdesimeur.android.gpsfiction.gpsfictionprojects.admin.AdminActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;


public class GpsFictionActivity extends Activity {
    public static final String RESETGAMES = "com.sdesimeur.android.gpsfiction.intent.action.RESETGAMES";
    public static final String ALLGPSFICTIONCATEGORY = "com.sdesimeur.android.gpsfiction.intent.category.GPSFICTIONACTIVITY";
    private static final String TAGFONT = "FONT";
    private static final String BUNDLEASJSON = "BundleAsJson";
    private static final String LASTSELECTEDFRAGMENTID  = "LastSelectedFragmentId";
    private FloatingActionButton fabCreate;
    private int lastFabAction;
    private float dYFab;
    private float dXFab;
    private float dX;
    private float dY;
    private static final float MINMOVE = 20;
    
    
    protected FragmentManager fragmentManager;
    protected HashSet<MyDialogFragment> dialogFragments = new HashSet<>();
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private HashMap<Integer, MyTabFragmentImpl> menuItem2Fragments;

    private int selectedFragmentId = R.id.Zones;

    protected GpsFictionControler mGpsFictionControler;

    public GpsFictionControler getmGpsFictionControler() {
        return mGpsFictionControler;
    }

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
            //    mGpsFictionData.toSave = false;
                finish();
            }
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

        /*
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
        */

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        super.onConfigurationChanged(newConfig);
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
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MultiDex.install(this);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (getIntent().getAction().equals(RESETGAMES)) {
            SharedPreferences.Editor ed = settings.edit();
            ed.putString(JSonStrings.ALLDATA,"");
            finish();
        }
        String loc = getIntent().getStringExtra(AdminActivity.LOCALE);
        if (loc != null) {
            SharedPreferences.Editor ed = settings.edit();
            ed.putString(AdminActivity.LOCALE,loc);
            ed.commit();
        }
        String localeString = settings.getString(AdminActivity.LOCALE,"fr_FR");
        Locale locale = new Locale(localeString);
        //if (!Locale.getDefault().equals(locale)) {
        if (!getResources().getConfiguration().locale.equals(locale)) {
                Locale.setDefault(locale);
                Configuration cfg = getResources().getConfiguration();
                cfg.setLocale(locale);
                getResources().updateConfiguration(cfg,null) ;
                recreate();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LOW_PROFILE);
        mGpsFictionControler = new GpsFictionControler(this);
        if (savedInstanceState != null) {
            selectedFragmentId = savedInstanceState.getInt(LASTSELECTEDFRAGMENTID, R.id.Zones);
        }
        String tmp = settings.getString(JSonStrings.ALLDATA, null);
        if (tmp != null) {
            try {
                mGpsFictionControler.getmGpsFictionData().setJson(new JSONObject(tmp));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } else mGpsFictionControler.getmGpsFictionData().init();

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
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = settings.edit();
        try {
            ed.putString(JSonStrings.ALLDATA,getmGpsFictionControler().getmGpsFictionData().getJson().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ed.commit();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(LASTSELECTEDFRAGMENTID, selectedFragmentId);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences.Editor ed = settings.edit();
        //ed.commit();
        mGpsFictionControler.onDestroy();
        super.onDestroy();
    }

}
