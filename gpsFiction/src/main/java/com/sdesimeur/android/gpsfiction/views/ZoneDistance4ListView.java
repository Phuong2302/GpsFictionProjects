package com.sdesimeur.android.gpsfiction.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;
import com.sdesimeur.android.gpsfiction.activities.MyTabFragment;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;

public class ZoneDistance4ListView extends TextView implements PlayerLocationListener {

    private GpsFictionActivity mGpsFictionActivity = null;
    private Zone attachedZone = null;
    private MyTabFragment myTabFragment;

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

    public void init(MyTabFragment mtf, Zone zone) {
        myTabFragment = mtf;
        attachedZone = zone;
//        mGpsFictionActivity.getmMyLocationListenerService().addPlayerLocationListener(MyLocationListenerService.REGISTER.VIEW, this);
    }

    @Override
    public void onLocationPlayerChanged(MyGeoPoint playerLocation) {
        String distanceText = attachedZone.getStringDistance2Player();
        this.setText(distanceText);
        if (this.isShown())
            this.invalidate();
    }
    @Override
    protected void onAttachedToWindow() {
        myTabFragment.getmGpsFictionControler().addPlayerLocationListener(GpsFictionControler.REGISTER.VIEW, this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        myTabFragment.getmGpsFictionControler().removePlayerLocationListener(GpsFictionControler.REGISTER.VIEW, this);
        super.onDetachedFromWindow();
    }

    public void setText(String text) {
        super.setText(text);
        if (this.isShown())
            this.invalidate();
    }
}
