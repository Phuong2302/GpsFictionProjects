package com.sdesimeur.android.gpsfiction.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;

/**
 * Created by sam on 10/12/16.
 */

public class ZoneName4ListView extends TextView  implements ZoneSelectListener{
    public ZoneName4ListView(Context context) {
        super(context);
    }

    public ZoneName4ListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoneName4ListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        int id = R.id.gpsFictionControlerId;
        GpsFictionControler gfc = (GpsFictionControler) getTag(id);
        gfc.addZoneSelectListener(GpsFictionControler.REGISTER.VIEW, this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        int id = R.id.gpsFictionControlerId;
        GpsFictionControler gfc = (GpsFictionControler) getTag(id);
        gfc.removeZoneSelectListener(GpsFictionControler.REGISTER.VIEW, this);
        super.onDetachedFromWindow();
    }


    @Override
    public void onZoneSelectChanged(Zone selectedZone, Zone uSZ) {
        int id = R.id.attachedZoneId;
        setSelected(selectedZone == ((Zone)getTag(id)));
        if (isShown()) invalidate();
    }
}
