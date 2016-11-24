package com.sdesimeur.android.gpsfiction.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

        return locationMode != Settings.Secure.LOCATION_MODE_OFF;

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
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    testLocation();
                    //get gps
                }
            });
            /*
            dialog.setNegativeButton(getString(R.string.dialogButtonCancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                }
            });
            */
            dialog.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        testLocation();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LOW_PROFILE);
        super.onCreate(savedInstanceState);
       // mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LOW_PROFILE|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.adminactivity);
        languageLocaleSpinner = (Spinner) findViewById(R.id.LanguageListSpinner);
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
        TextView tv = (TextView) findViewById(R.id.mytext);
        tv.setText(getResources().getString(R.string.titleCompass));
        tv.invalidate();
    }

    public void changeAdminPassword(View v) {
        EditText ed1 = (EditText) findViewById(R.id.pass1);
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            String shaHex= new String(Hex.encodeHex(DigestUtils.sha(ed1.getText().toString())));
            settings.edit().putString("PassWord",shaHex);
            ed1.setText("");
            Toast.makeText(this, R.string.passwd_saved,Toast.LENGTH_LONG).show();

    }
    public void startGames (View v) {
        Locale l = string2locale.get(languageLocaleSpinner.getSelectedItem());
        Locale.setDefault(l);
        Configuration cfg = getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cfg.setLocale(l);
            }
        getResources().updateConfiguration(cfg,null);
        recreate();

        /// ask passwd in dialogbox
        Toast.makeText(this, R.string.passwd_different,Toast.LENGTH_LONG).show();
    }
}
