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
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
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
import java.util.Locale;


public class GpsFictionActivity extends Activity implements TextToSpeech.OnInitListener {
    private static final String TAGFONT = "FONT";

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
    private TextToSpeech mTts;
    private boolean mTtsOK = false;
    private void testTTS() {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, 0x01);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    		if (requestCode == 0x01) {
		        if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
		            // Succès, au moins un moteur de TTS à été trouvé, on l'instancie
		            mTts = new TextToSpeech(this, this);
                    if (mTts.isLanguageAvailable(Locale.getDefault()) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    	mTts.setLanguage(Locale.getDefault());
                    } else if (mTts.isLanguageAvailable(Locale.ENGLISH) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                        mTts.setLanguage(Locale.ENGLISH);
                    }
                    mTts.setSpeechRate(1); // 1 est la valeur par défaut. Une valeur inférieure rendra l'énonciation plus lente, une valeur supérieure la rendra plus rapide.
                    mTts.setPitch(1); // 1 est la valeur par défaut. Une valeur inférieure rendra l'énonciation plus grave, une valeur supérieure la rendra plus aigue.
		        //} else {
		            // Echec, aucun moteur n'a été trouvé, on propose à l'utilisateur d'en installer un depuis le Market
		        //    Intent installIntent = new Intent();
		        //    installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
		        //    startActivity(installIntent);
		        }
    		}
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
        mtf.register(mGpsFictionData);
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
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void floatingAction(View view) {
        drawerLayout.openDrawer(Gravity.LEFT);
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
        testTTS();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        this.setContentView(R.layout.main_view);
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

    @Override
    public void onDestroy() {
        if (mTts != null) mTts.shutdown();
        mTtsOK=false;
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        mTtsOK=(status == TextToSpeech.SUCCESS);
    }
    public void speak (String txt) {
        if (mTtsOK) {
            //String utteranceId=this.hashCode() + "";
            if (!mTts.isSpeaking()) mTts.speak(txt, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
