package com.sdesimeur.android.gpsfiction.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;

public class ZoneName4CompassView extends TextView implements ZoneSelectListener {

    public ZoneName4CompassView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ZoneName4CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoneName4CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(GpsFictionActivity gpsFictionActivity) {
        gpsFictionActivity.getmGpsFictionControler().addZoneSelectListener(GpsFictionControler.REGISTER.VIEW, this);
    }

    @Override
    public void onZoneSelectChanged(Zone selectedZone, Zone uSZ) {
        // TODO Auto-generated method stub
        String nameText = (selectedZone == null) ?
                getResources().getString(R.string.noZoneTitle) :
                selectedZone.getName();
        this.setText(nameText);
        this.invalidate();
    }
}
