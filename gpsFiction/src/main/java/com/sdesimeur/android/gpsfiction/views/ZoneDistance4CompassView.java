package com.sdesimeur.android.gpsfiction.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;

public class ZoneDistance4CompassView extends TextView implements PlayerLocationListener, ZoneSelectListener {

    private GpsFictionActivity mGpsFictionActivity = null;

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
        mGpsFictionActivity = gpsFictionActivity;
//        mGpsFictionActivity.getmMyLocationListenerService().addPlayerLocationListener(MyLocationListenerService.REGISTER.VIEW, this);
        mGpsFictionActivity.getmGpsFictionControler().addZoneSelectListener(GpsFictionControler.REGISTER.VIEW, this);
//		gpsFictionActivity.getGpsFictionData().addZoneSelectListener(GpsFictionData.REGISTER.VIEW, this);
    }

    @Override
    public void onLocationPlayerChanged(MyGeoPoint playerLocation) {

        String distanceText = (mGpsFictionActivity.getmGpsFictionControler().getSelectedZone() == null) ?
                getResources().getString(R.string.noZoneDistance) :
                mGpsFictionActivity.getmGpsFictionControler().getSelectedZone().getStringDistance2Player();
        setText(distanceText);
        if (isShown()) invalidate();
    }


    @Override
    public void onZoneSelectChanged(Zone sZ, Zone uSZ) {
        if (isShown()) invalidate();
    }

}
