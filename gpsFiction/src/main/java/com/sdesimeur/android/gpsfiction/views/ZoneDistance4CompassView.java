package com.sdesimeur.android.gpsfiction.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;
import com.sdesimeur.android.gpsfiction.geopoint.MyGeoPoint;

public class ZoneDistance4CompassView extends TextView implements PlayerLocationListener, ZoneSelectListener {

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

    @Override
    protected void onAttachedToWindow() {
        GpsFictionControler gfc = (GpsFictionControler) getTag(R.id.gpsFictionControlerId);
        gfc.addPlayerLocationListener(GpsFictionControler.REGISTER.VIEW, this);
        gfc.addZoneSelectListener(GpsFictionControler.REGISTER.VIEW, this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        GpsFictionControler gfc = (GpsFictionControler) getTag(R.id.gpsFictionControlerId);
        gfc.removePlayerLocationListener(GpsFictionControler.REGISTER.VIEW, this);
        gfc.removeZoneSelectListener(GpsFictionControler.REGISTER.VIEW, this);
        super.onDetachedFromWindow();
    }


    @Override
    public void onLocationPlayerChanged(MyGeoPoint playerLocation) {
        GpsFictionControler gfc = (GpsFictionControler) getTag(R.id.gpsFictionControlerId);
        String distanceText ;
        if (gfc.getSelectedZone() == null) {
            distanceText = getResources().getString(R.string.noZoneDistance);
        } else {
            distanceText = gfc.getSelectedZone().getStringDistance2Player();
        }
        setText(distanceText);
        if (isShown()) invalidate();
    }


    @Override
    public void onZoneSelectChanged(Zone sZ, Zone uSZ) {
        if (isShown()) invalidate();
    }

}
