package com.sdesimeur.android.gpsfiction.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListener;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationEvent;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.Zone;

public class ZoneDistance4ListView extends TextView implements PlayerLocationListener {

    private GpsFictionActivity gpsFictionActivity = null;
    private Zone attachedZone = null;

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

    public void init(GpsFictionActivity gpsFictionActivity, Zone zone) {
        this.gpsFictionActivity = gpsFictionActivity;
        this.attachedZone = zone;
        this.gpsFictionActivity.getMyLocationListener().addPlayerLocationListener(MyLocationListener.REGISTER.VIEW, this);
//		this.gpsFictionActivity.getGpsFictionData().addZoneSelectListener(GpsFictionData.REGISTER.VIEW, this);
    }

    @Override
    public void onLocationPlayerChanged(PlayerLocationEvent playerLocationEvent) {
        String distanceText = this.attachedZone.getStringDistance2Player();
        this.setText(distanceText);
        if (this.isShown())
            this.invalidate();
    }

    public void setText(String text) {
        super.setText(text);
        if (this.isShown())
            this.invalidate();
    }

	/*
    @Override
	public void onZoneSelectChanged(Zone selectedZone) {
		this.selectedZone = selectedZone;
		this.invalidate();
	}*/

}
