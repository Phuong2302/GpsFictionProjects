package com.sdesimeur.android.gpsfiction.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;

public class ZoneNameView extends TextView implements ZoneSelectListener {
    private GpsFictionActivity mGpsFictionActivity = null;
    private Zone selectedZone = null;

    public ZoneNameView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ZoneNameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoneNameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(GpsFictionActivity gpsFictionActivity) {
        mGpsFictionActivity = gpsFictionActivity;
        mGpsFictionActivity.getmGpsFictionData().addZoneSelectListener(GpsFictionData.REGISTER.VIEW, this);
    }

    @Override
    public void onZoneSelectChanged(Zone selectedZone) {
        // TODO Auto-generated method stub
        this.selectedZone = selectedZone;
        String nameText = (this.selectedZone == null) ?
                getResources().getString(R.string.noZoneTitle) :
                this.selectedZone.getName();
        this.setText(nameText);
        this.invalidate();
    }
}
