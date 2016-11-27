package com.sdesimeur.android.gpsfiction.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.sdesimeur.android.gpsfiction.R;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AdminActivity extends Activity {
    private HashMap <String, Locale> string2locale = new HashMap<>();
    private Spinner languageLocaleSpinner;
    private Switch sw;

    public static boolean isLocationEnabled(Context context) {
	    int locationMode = 0;
	    String locationProviders;
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
	        try {
	            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
	        } catch (Settings.SettingNotFoundException e) {
	            e.printStackTrace();
	            return false;
	        }
	        return ((locationMode != Settings.Secure.LOCATION_MODE_OFF) && (locationMode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY));
	    }else{
	        locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	        return !TextUtils.isEmpty(locationProviders);
	    }
    }
    private void testLocation () {
//        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        if(!lm.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
        if (!isLocationEnabled(this)) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    testLocation();
                }
            });
            dialog.show();
        }
    }

    private void changeHomeActivityInPref () {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        ResolveInfo resolveinfo = (ResolveInfo) getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = settings.edit();
        String packageName = resolveinfo.activityInfo.applicationInfo.packageName;
        String name = resolveinfo.activityInfo.name;
        if ( ! packageName.equals(getPackageName())) {
            ed.putString("loadHomeDefaultPackageName", packageName);
            ed.putString("loadHomeDefaultActivityName", name);
        } else {
            // dialogbox to choose prefered homeactivity
        }
        ed.commit();
    }

    private void launchAppChooser() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = settings.edit();
        ed.putString("loadHomeDefaultPackageName", getPackageName());
        ed.putString("loadHomeDefaultActivityName", com.sdesimeur.android.gpsfiction.activities.AdminActivity.class.toString());
        testLocation();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LOW_PROFILE);
        super.onCreate(savedInstanceState);
       // mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LOW_PROFILE|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.adminactivity);
        languageLocaleSpinner = (Spinner) findViewById(R.id.LanguageListSpinner);
        sw = (Switch) findViewById(R.id.ResetGames);
        Set<String> codeCountryArray = new HashSet<>();
        Collections.addAll(codeCountryArray,getResources().getStringArray(R.array.countryCodeArray));
        /*
        for ( String s : getResources().getStringArray(R.array.countryCodeArray)) {
            codeCountryArray.add(s);
        }
        */
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> langs = new ArrayList<>();
        String lang;
        int i=0;
        for( Locale loc : locale ){
            lang = loc.getDisplayLanguage();
            if( codeCountryArray.contains(loc.toString()) && !langs.contains(lang) ){
                if (loc.toString().equals(Locale.getDefault().toString())) {
                    i=langs.size();
                }
                langs.add( lang );
                string2locale.put(lang,loc);
            }
        }
        Collections.sort(langs, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinnerlanguageselect, langs);
        languageLocaleSpinner.setAdapter(adapter);
        languageLocaleSpinner.setSelection(i);
    }

    public void changeAdminPassword(View v) {
        EditText ed1 = (EditText) findViewById(R.id.pass1);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String shaHex= new String(Hex.encodeHex(DigestUtils.sha(ed1.getText().toString())));
        SharedPreferences.Editor ed = settings.edit();
        ed.putString("PassWord",shaHex);
        ed.commit();
        ed1.setText("");
        Toast.makeText(this, R.string.passwd_saved,Toast.LENGTH_LONG).show();
    }
    public void startGames (View v) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = settings.edit();
        Locale locale = string2locale.get(languageLocaleSpinner.getSelectedItem());
        String localeString = locale.toString();
        ed.putString("Locale",localeString);
        ed.putBoolean("ResetGames",sw.isChecked());
        ed.commit();
        AlertDialog.Builder dialogBox = new AlertDialog.Builder(this);
        dialogBox.setTitle(R.string.askpasstitle);
        dialogBox.setMessage(R.string.askpassmessage);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        dialogBox.setView(input);
        dialogBox.setPositiveButton(R.string.dialogButtonValidate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AdminActivity.this);
                String passsave = settings.getString("PassWord","&é(-è_çà)=");
                String passsha = new String (Hex.encodeHex(DigestUtils.sha(input.getText().toString())));
                if (passsha.equals(passsave)) {
                    Intent intent = new Intent(AdminActivity.this,GamesActivity.class);
                    intent.setAction(Intent.ACTION_RUN);
                    startActivity(intent);
                } else {
                    Toast.makeText(AdminActivity.this, R.string.passwd_different,Toast.LENGTH_LONG).show();
                }
            }
        });
        dialogBox.setNegativeButton(R.string.dialogButtonCancel,null);
        dialogBox.show();
    }
}
