package com.sdesimeur.android.gpsfiction.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    }

    public void changeAdminPassword(View v) {

    }
    public void startGames ( View v) {
        Locale l = string2locale.get(languageLocaleSpinner.getSelectedItem());
        Locale.setDefault(l);
        Configuration cfg = getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cfg.setLocale(l);
        }
        TextView tv = (TextView) findViewById(R.id.mytext);
        tv.setText(getResources().getString(R.string.titleCompass));
        tv.invalidate();
    }
}
