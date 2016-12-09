package com.sdesimeur.android.gpsfiction.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.R;
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
    @Override
    protected void onAttachedToWindow() {
        GpsFictionControler gfc = (GpsFictionControler) getTag();
        gfc.addZoneSelectListener(GpsFictionControler.REGISTER.VIEW, this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        GpsFictionControler gfc = (GpsFictionControler) getTag();
        gfc.removeZoneSelectListener(GpsFictionControler.REGISTER.VIEW, this);
        super.onDetachedFromWindow();
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
