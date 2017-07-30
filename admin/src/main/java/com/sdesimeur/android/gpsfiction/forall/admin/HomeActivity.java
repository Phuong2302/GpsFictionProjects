package com.sdesimeur.android.gpsfiction.forall.admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SearchEvent;
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
import java.util.List;
import java.util.Locale;
import java.util.Set;

//import android.support.multidex.MultiDex;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomeActivity extends Activity {
    public static final String HOMEDEFAULTACTIVITY = "loadHomeDefaultActivityName";
    public static final String HOMEDEFAULTPACKAGE = "loadHomeDefaultPackageName";
    public static final String PASSWORD = "com.sdesimeur.android.gpsfiction.forall.admin.password";
    public static final String PLAYERAPPALLREADYSTARTED = "playerAppAllreadyStarted";
    private static final String PACKAGE4GPSFICTIONPLAYERACTIVITY = "com.sdesimeur.android.gpsfiction.forall.player";
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
        Boolean test = false;
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);
        List<IntentFilter> filters = new ArrayList<>();
        filters.add(filter);
        List<ComponentName> activities = new ArrayList<ComponentName>();
        final PackageManager packageManager = (PackageManager) getPackageManager();
        packageManager.getPreferredActivities(filters, activities, null);
        //Intent[] others = new Intent[activities.size()];
        //int i = 0;
        for (ComponentName activity : activities) {
            //Intent other = new Intent(Intent.ACTION_MAIN);
            //other.addCategory(Intent.CATEGORY_HOME);
            //other.addCategory(Intent.CATEGORY_DEFAULT);
            //other.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            //others[i]=other;
            //startActivity(other);
            //i++;
            if (activity.getClassName().equals(HomeActivity.class.getName())) {
                test = true;
            }
        }
        //startActivities(others);

        return test;
    }

    public void changeHomeActivity(View view) {
        Intent changeHomeIntent = new Intent(Intent.ACTION_MAIN);
        //changeHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        changeHomeIntent.setComponent(new ComponentName(getPackageName(),ChangeHomeActivity.class.getName()));
        startActivity(changeHomeIntent);
    }
    @Override
    protected void onResume() {
        String activityName = this.getIntent().getStringExtra(HomeActivity.HOMEDEFAULTACTIVITY);
        String packageName = this.getIntent().getStringExtra(HomeActivity.HOMEDEFAULTPACKAGE);
        if ( activityName!=null ) if ((!activityName.isEmpty()) && (!activityName.contains(HomeActivity.class.getName()))) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            homeIntent.setComponent(new ComponentName(packageName,activityName));
            startActivity(homeIntent);
            this.finish();
        }
        super.onResume();
    }

    @Override
    protected void onStart() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean temp = settings.getBoolean(PLAYERAPPALLREADYSTARTED, false);
        if ( temp ) {
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
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                    String passsave = settings.getString(HomeActivity.PASSWORD, new String(Hex.encodeHex(DigestUtils.sha(""))));
                    String passsha = new String(Hex.encodeHex(DigestUtils.sha(input.getText().toString())));
                    if (passsha.equals(passsave)) {
                        SharedPreferences.Editor ed = settings.edit();
                        ed.putBoolean(HomeActivity.PLAYERAPPALLREADYSTARTED, false);
                        ed.commit();
                    } else {
                        Toast.makeText(HomeActivity.this, R.string.passwd_different, Toast.LENGTH_LONG).show();
                        startGamesActivity(true);
                    }
                }
            });
            dialogBox.setNegativeButton(R.string.dialogButtonCancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startGamesActivity(true);
                }
            });
            dialogBox.show();
        }
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      //  MultiDex.install(this);
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, 0x01);
        setDefaultKeyMode(DEFAULT_KEYS_DISABLE);
        launchAppChooser();
        testLocation();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LOW_PROFILE);
        super.onCreate(savedInstanceState);
        // mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LOW_PROFILE|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.homeactivity);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinnerlanguageselect, langs);
        languageLocaleSpinner.setAdapter(adapter);
        languageLocaleSpinner.setSelection(i);
    }

    public void startMockGps (View v) {
        ComponentName cn = new ComponentName("com.sdesimeur.android.mockgps", "com.sdesimeur.android.mockgps.MockGpsProviderActivity");
        Intent intent = new Intent();
        intent.setComponent(cn);
        startActivity(intent);
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
                    ed.putBoolean(PLAYERAPPALLREADYSTARTED, true);
                    ed.commit();
                    startGamesActivity(false);
                } else {
                    Toast.makeText(HomeActivity.this, R.string.passwd_different, Toast.LENGTH_LONG).show();
                }
            }
        });
        dialogBox.setNegativeButton(R.string.dialogButtonCancel, null);
        dialogBox.show();
    }

    public void startGamesActivity(Boolean noReset) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Bundle extras = new Bundle();
        extras.putBoolean(GpsFictionIntent.RESETGAMES, (noReset)?false:settings.getBoolean(GpsFictionIntent.RESETGAMES, false));
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
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onBackPressed() {
    }
    @Override
    public boolean dispatchKeyEvent ( KeyEvent event) {
        boolean handled = super.dispatchKeyEvent(event);
            if (event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
                return true;
            }
            //if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            //    return true;
            //}
        if (event.getKeyCode()==KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return handled;
    }
    @Override
    public boolean onSearchRequested () {
        return true;
    }
    @Override
    public boolean onSearchRequested (SearchEvent searchEvent) {
        return true;
    }
}

