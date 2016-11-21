package com.sdesimeur.android.gpsfiction.activities;

import android.content.res.Resources;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;
import com.sdesimeur.android.gpsfiction.views.MiniCompassView;
import com.sdesimeur.android.gpsfiction.views.ZoneDistance4ListView;

public class ViewHolder4Zones implements ZoneSelectListener {
    private TextView zoneTitleView = null;

    public ZoneDistance4ListView getDistanceToZoneView() {
        return distanceToZoneView;
    }

    public MiniCompassView getMiniCompassView() {
        return miniCompassView;
    }

    private ZoneDistance4ListView distanceToZoneView = null;
    //	private TextView directionOfZone;
    private MiniCompassView miniCompassView = null;
    private Zone attachedZone = null;
    private GpsFictionActivity mGpsFictionActivity = null;

    public Zone getAttachedZone() {
        return attachedZone;
    }

    private void updateZoneTitleView() {
        // TODO Auto-generated method stub
        Resources res = mGpsFictionActivity.getResources();
        int titlebackgroundcolor = 0;
        if (mGpsFictionActivity.getmGpsFictionData().getSelectedZone() == attachedZone) {
            titlebackgroundcolor = res.getColor(R.color.tabnameofzoneselected);
        } else {
            titlebackgroundcolor = res.getColor(R.color.tabnameofzone);
        }
        zoneTitleView.setBackgroundColor(titlebackgroundcolor);
        if (zoneTitleView.isShown())
            zoneTitleView.invalidate();
    }

    public void setZoneTitleView(TextView textView) {
        zoneTitleView = textView;
    }

    public void setDistanceToZoneView(ZoneDistance4ListView dTZV) {
        distanceToZoneView = dTZV;
    }

    public void setMiniCompassView(MiniCompassView miniCV) {
        miniCompassView = miniCV;
    }

    public void init(GpsFictionActivity gpsFictionActivity, Zone aZone) {
        // TODO Auto-generated method stub
        mGpsFictionActivity = gpsFictionActivity;
        attachedZone = aZone;
        zoneTitleView.setText(attachedZone.getName());
        updateZoneTitleView();
        mGpsFictionActivity.getmGpsFictionData().addZoneSelectListener(GpsFictionData.REGISTER.HOLDERVIEW, this);
        miniCompassView.init(mGpsFictionActivity, attachedZone);
        distanceToZoneView.init(mGpsFictionActivity, attachedZone);
    }

    @Override
    public void onZoneSelectChanged(Zone selectedZone, Zone uSZ) {
        // TODO Auto-generated method stub
        updateZoneTitleView();
    }


}
