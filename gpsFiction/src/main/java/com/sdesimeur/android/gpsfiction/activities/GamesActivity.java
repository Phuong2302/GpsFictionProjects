package com.sdesimeur.android.gpsfiction.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListenerService;

public class GamesActivity extends Activity implements GameFragment.OnListFragmentInteractionListener {

    private boolean isStopped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent myIntent2 = new Intent(this, MyLocationListenerService.class);
        myIntent2.setAction(MyLocationListenerService.ACTION.STARTFOREGROUND);
        startService(myIntent2);
        isStopped = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
    }


    @Override
    public void onListFragmentInteraction(GameFragment.GameItem item) {
        // TODO  put the locale in string
        String locale = "";
        Intent intent = new Intent(this, item.theClass);
        intent.setAction(Intent.ACTION_RUN);
        intent.putExtra("Locale",locale);
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
