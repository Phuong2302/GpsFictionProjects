package com.sdesimeur.android.gpsfiction.forall.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.multidex.MultiDex;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.sdesimeur.android.gpsfiction.intent.GpsFictionIntent;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

//import android.support.multidex.MultiDex;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AdminActivity extends Activity {
    public static final String HOMEDEFAULTACTIVITY = "loadHomeDefaultActivityName";
    public static final String PASSWORD = "com.sdesimeur.android.gpsfiction.forall.admin.password";
    public static final String HOMEDEFAULTPACKAGE = "loadHomeDefaultPackageName";
    public static final String ALLREADYSTARTED = "allreadyStarted";
    private static final String PACKAGE4GPSFICTIONPLAYERACTIVITY = "com.sdesimeur.android.gpsfiction.forall.player";
    public static final String ADMINACTIVITYCLASSNAME = AdminActivity.class.getName();
    public static final String HOMEACTIVITYCLASSNAME = HomeActivity.class.getName();
    private HashMap<String, Locale> string2locale = new HashMap<>();
    private Spinner languageLocaleSpinner;
    private Switch sw;

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return ((locationMode != Settings.Secure.LOCATION_MODE_OFF) && (locationMode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY));
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void testLocation() {
//        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        if(!lm.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
        if (!isLocationEnabled(this)) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    testLocation();
                }
            });
            dialog.show();
        }
    }

    public void changeHomeActivityInPref(View v) {
        final HashMap<String, ActivityInfo> string2activityinfo = new HashMap<>();
        ArrayList<String> homeActivities = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinnerhomeactivityselect, homeActivities);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
        Iterator<ResolveInfo> it = list.iterator();
        while (it.hasNext()) {
            ActivityInfo act = it.next().activityInfo;
            if (!act.name.equals(HOMEACTIVITYCLASSNAME)) {
                String st = (String) act.loadLabel(pm);
                adapter.add(st);
                string2activityinfo.put(st, act);
            }
        }
        LayoutInflater inflater = getLayoutInflater();
        final Spinner spinner = (Spinner) inflater.inflate(R.layout.homeactivitychooser, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(spinner);
        spinner.setAdapter(adapter);

        dialog.setTitle(R.string.dialoghomeactivitychoosertitle);
        dialog.setMessage(R.string.dialoghomeactivitychoosermessage);
        dialog.setPositiveButton(R.string.dialogButtonValidate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AdminActivity.this);
                SharedPreferences.Editor ed = settings.edit();
                ActivityInfo resolveInfo = string2activityinfo.get(spinner.getSelectedItem());
                String homePackageName = resolveInfo.applicationInfo.packageName;
                String homeActivityName = resolveInfo.name;
                if (!homePackageName.equals(getPackageName())) {
                    ed.putString(HOMEDEFAULTPACKAGE, homePackageName);
                    ed.putString(HOMEDEFAULTACTIVITY, homeActivityName);
                }
                ed.commit();
            }
        });
        dialog.show();
    }

    private void launchAppChooser() {
        if (!isMyAppLauncherDefault()) {
            AlertDialog.Builder dialogBox = new AlertDialog.Builder(this);
            dialogBox.setTitle(R.string.homechoosertittle);
            dialogBox.setMessage(R.string.homechoosermessage);
            dialogBox.setPositiveButton(R.string.dialogButtonValidate, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            dialogBox.show();

            //startActivity(Intent.createChooser(intent, getString(R.string.changeToMyHomeActivity)));
        }
    }

    private boolean isMyAppLauncherDefault() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);
        List<IntentFilter> filters = new ArrayList<>();
        filters.add(filter);
        List<ComponentName> activities = new ArrayList<ComponentName>();
        final PackageManager packageManager = (PackageManager) getPackageManager();
        packageManager.getPreferredActivities(filters, activities, null);
        for (ComponentName activity : activities) {
            if (activity.getClassName().equals(HOMEACTIVITYCLASSNAME)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = settings.edit();
        ed.putString(HOMEDEFAULTPACKAGE, getPackageName());
        ed.putString(HOMEDEFAULTACTIVITY, ADMINACTIVITYCLASSNAME);
        ed.commit();
        Boolean temp = settings.getBoolean(ALLREADYSTARTED, false);
        if (temp) {
            AlertDialog.Builder dialogBox = new AlertDialog.Builder(this);
            dialogBox.setTitle(R.string.askpasstitlereturn);
            dialogBox.setMessage(R.string.askpassmessagereturn);
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            //input.setTransformationMethod(PasswordTransformationMethod.getInstance());
            dialogBox.setView(input);
            dialogBox.setPositiveButton(R.string.dialogButtonValidate, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AdminActivity.this);
                    String passsave = settings.getString(AdminActivity.PASSWORD, new String(Hex.encodeHex(DigestUtils.sha(""))));
                    String passsha = new String(Hex.encodeHex(DigestUtils.sha(input.getText().toString())));
                    if (passsha.equals(passsave)) {
                        SharedPreferences.Editor ed = settings.edit();
                        ed.putBoolean(AdminActivity.ALLREADYSTARTED, false);
                        ed.commit();
                    } else {
                        Toast.makeText(AdminActivity.this, R.string.passwd_different, Toast.LENGTH_LONG).show();
                        startGamesActivity();
                    }
                }
            });
            dialogBox.setNegativeButton(R.string.dialogButtonCancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startGamesActivity();
                }
            });
            dialogBox.show();
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MultiDex.install(this);
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, 0x01);

        launchAppChooser();
        testLocation();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LOW_PROFILE);
        super.onCreate(savedInstanceState);
        // mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LOW_PROFILE|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.adminactivity);
        languageLocaleSpinner = (Spinner) findViewById(R.id.LanguageListSpinner);
        languageLocaleSpinner.setFocusable(true);
        languageLocaleSpinner.setFocusableInTouchMode(true);
        languageLocaleSpinner.requestFocus();
        sw = (Switch) findViewById(R.id.ResetGames);
        Set<String> codeCountryArray = new HashSet<>();
        Collections.addAll(codeCountryArray, getResources().getStringArray(R.array.countryCodeArray));
        /*
        for ( String s : getResources().getStringArray(R.array.countryCodeArray)) {
            codeCountryArray.add(s);
        }
        */
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> langs = new ArrayList<>();
        String lang;
        int i = 0;
        for (Locale loc : locale) {
            lang = loc.getDisplayLanguage();
            if (codeCountryArray.contains(loc.toString()) && !langs.contains(lang)) {
                if (loc.toString().equals(Locale.getDefault().toString())) {
                    i = langs.size();
                }
                langs.add(lang);
                string2locale.put(lang, loc);
            }
        }
        Collections.sort(langs, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinnerlanguageselect, langs);
        languageLocaleSpinner.setAdapter(adapter);
        languageLocaleSpinner.setSelection(i);
    }

    public void changeAdminPassword(View v) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor ed = settings.edit();
        AlertDialog.Builder dialogBox = new AlertDialog.Builder(this);
        dialogBox.setTitle(R.string.askchangepasstitle);
        dialogBox.setMessage(R.string.askchangepassmessage);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        dialogBox.setView(input);
        dialogBox.setPositiveButton(R.string.dialogButtonValidate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String shaHex = new String(Hex.encodeHex(DigestUtils.sha(input.getText().toString())));
                ed.putString(PASSWORD, shaHex);
                ed.commit();
                Toast.makeText(getApplicationContext(), R.string.passwd_saved, Toast.LENGTH_LONG).show();
            }
        });
        dialogBox.setNegativeButton(R.string.dialogButtonCancel, null);
        dialogBox.show();
    }

    public void startGames(View v) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor ed = settings.edit();
        Locale locale = string2locale.get(languageLocaleSpinner.getSelectedItem());
        String localeString = locale.toString();
        ed.putString(GpsFictionIntent.LOCALE, localeString);
        ed.putBoolean(GpsFictionIntent.RESETGAMES, sw.isChecked());
        ed.commit();
        AlertDialog.Builder dialogBox = new AlertDialog.Builder(this);
        dialogBox.setTitle(R.string.askpasstitle);
        dialogBox.setMessage(R.string.askpassmessage);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        dialogBox.setView(input);
        dialogBox.setPositiveButton(R.string.dialogButtonValidate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String passsave = settings.getString(PASSWORD, "&é(-è_çà)=");
                String passsha = new String(Hex.encodeHex(DigestUtils.sha(input.getText().toString())));
                if (passsha.equals(passsave)) {
                    ed.putBoolean(ALLREADYSTARTED, true);
                    ed.commit();
                    startGamesActivity();
                } else {
                    Toast.makeText(AdminActivity.this, R.string.passwd_different, Toast.LENGTH_LONG).show();
                }
            }
        });
        dialogBox.setNegativeButton(R.string.dialogButtonCancel, null);
        dialogBox.show();
    }

    public void startGamesActivity() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Bundle extras = new Bundle();
        extras.putBoolean(GpsFictionIntent.RESETGAMES, settings.getBoolean(GpsFictionIntent.RESETGAMES, false));
        extras.putString(GpsFictionIntent.LOCALE, settings.getString(GpsFictionIntent.LOCALE, GpsFictionIntent.DEFAULTPLAYERLOCALE));
        ComponentName cn = new ComponentName(PACKAGE4GPSFICTIONPLAYERACTIVITY, PACKAGE4GPSFICTIONPLAYERACTIVITY + ".GamesActivity");
        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.setComponent(cn);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x01) {
            if (resultCode != TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                ///// TODO send result to CalcRouteAndSpeakService which "startTts"
                Toast.makeText(this, R.string.nottsengine, Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }
}

