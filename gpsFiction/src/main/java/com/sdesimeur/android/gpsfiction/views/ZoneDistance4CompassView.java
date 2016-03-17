package com.sdesimeur.android.gpsfiction.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListener;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationEvent;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;

public class ZoneDistance4CompassView extends TextView implements PlayerLocationListener, ZoneSelectListener {

    private GpsFictionActivity gpsFictionActivity = null;
    private Zone selectedZone = null;

    public ZoneDistance4CompassView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ZoneDistance4CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoneDistance4CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(GpsFictionActivity gpsFictionActivity) {
        this.gpsFictionActivity = gpsFictionActivity;
        this.gpsFictionActivity.getMyLocationListener().addPlayerLocationListener(MyLocationListener.REGISTER.VIEW, this);
        this.gpsFictionActivity.getGpsFictionData().addZoneSelectListener(GpsFictionData.REGISTER.VIEW, this);
//		this.gpsFictionActivity.getGpsFictionData().addZoneSelectListener(GpsFictionData.REGISTER.VIEW, this);
    }

    @Override
    public void onLocationPlayerChanged(PlayerLocationEvent playerLocationEvent) {
        String distanceText = (this.selectedZone == null) ?
                getResources().getString(R.string.noZoneDistance) :
                this.selectedZone.getStringDistance2Player();
        this.setText(distanceText);
    }

    public void setText(String text) {
        super.setText(text);
        if (this.isShown())
            this.invalidate();
    }

    @Override
    public void onZoneSelectChanged(Zone selectedZone) {
        this.selectedZone = selectedZone;
        this.invalidate();
    }

	/*
    @Override
	public void onZoneSelectChanged(Zone selectedZone) {
		this.selectedZone = selectedZone;
		this.invalidate();
	}*/

}
