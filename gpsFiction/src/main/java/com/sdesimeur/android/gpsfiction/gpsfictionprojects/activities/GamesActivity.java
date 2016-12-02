package com.sdesimeur.android.gpsfiction.gpsfictionprojects.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListenerService;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Locale;

public class GamesActivity extends Activity implements GameFragment.OnListFragmentInteractionListener {

    private boolean isStopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String localeString = settings.getString(AdminActivity.LOCALE,"fr_FR");
        Locale locale = new Locale(localeString);
        Locale.setDefault(locale);
        Configuration cfg = getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            cfg.setLocale(locale);
        }
        getResources().updateConfiguration(cfg,null);
        Intent myIntent2 = new Intent(this, MyLocationListenerService.class);
        myIntent2.setAction(MyLocationListenerService.ACTION.STARTFOREGROUND);
        startService(myIntent2);
        isStopped = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
    }


    @Override
    public void onBackPressed() {
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
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(GamesActivity.this);
                String passsave = settings.getString(AdminActivity.PASSWORD,"&é(-è_çà)=");
                String passsha = new String (Hex.encodeHex(DigestUtils.sha(input.getText().toString())));
                if (passsha.equals(passsave)) {
                    SharedPreferences.Editor ed = settings.edit();
                    ed.putBoolean(AdminActivity.ALLREADYSTARTED,false);
                    ed.commit();
                    GamesActivity.this.finish();
                } else {
                    Toast.makeText(GamesActivity.this, R.string.passwd_different,Toast.LENGTH_LONG).show();
                }
            }
        });
        dialogBox.setNegativeButton(R.string.dialogButtonCancel,null);
        dialogBox.show();
        return;
    }
    @Override
    public void onListFragmentInteraction(GameFragment.GameItem item) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        // TODO  put the locale in string
        String locale = settings.getString(AdminActivity.LOCALE,"fr_FR");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_RUN);
        intent.setComponent(item.theComponentName);
        intent.putExtra(AdminActivity.LOCALE,locale);
        startActivity(intent);
        isStopped = false;
    }
    @Override
    public void onDestroy () {
        if (isStopped) {
            Intent myIntent2 = new Intent(this, MyLocationListenerService.class);
            myIntent2.setAction(MyLocationListenerService.ACTION.STOPFOREGROUND);
            startService(myIntent2);
        }
        super.onDestroy();
    }
}
