package com.sdesimeur.android.gpsfiction.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;

public class ZoneDistance4ListView extends TextView implements PlayerLocationListener {

    public ZoneDistance4ListView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ZoneDistance4ListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoneDistance4ListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onLocationPlayerChanged(MyGeoPoint playerLocation) {
        String distanceText = ((Zone)getTag()).getStringDistance2Player();
        this.setText(distanceText);
        if (this.isShown())
            this.invalidate();
    }

    public void setText(String text) {
        super.setText(text);
        if (this.isShown())
            this.invalidate();
    }
}
