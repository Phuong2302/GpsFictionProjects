package com.sdesimeur.android.gpsfiction.activities;

import android.content.res.Resources;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;
import com.sdesimeur.android.gpsfiction.views.MiniCompassView;
import com.sdesimeur.android.gpsfiction.views.ZoneDistance4ListView;

public class ViewHolder4Zones implements ZoneSelectListener {
    private TextView zoneTitleView = null;
    private ZoneDistance4ListView distanceToZoneView = null;
    //	private TextView directionOfZone;
    private MiniCompassView miniCompassView = null;
    private Zone attachedZone = null;
    private GpsFictionActivity gpsFictionActivity = null;
    private Zone selectedZone = null;


    public Zone getAttachedZone() {
        return attachedZone;
    }

    private void updateZoneTitleView() {
        // TODO Auto-generated method stub
        Resources res = this.gpsFictionActivity.getResources();
        int titlebackgroundcolor = 0;
        if (this.selectedZone == this.attachedZone) {
            titlebackgroundcolor = res.getColor(R.color.tabnameofzoneselected);
        } else {
            titlebackgroundcolor = res.getColor(R.color.tabnameofzone);
        }
        this.zoneTitleView.setBackgroundColor(titlebackgroundcolor);
        if (this.zoneTitleView.isShown())
            this.zoneTitleView.invalidate();
    }

    public void setZoneTitleView(TextView textView) {
        this.zoneTitleView = textView;
    }

    public void setDistanceToZoneView(ZoneDistance4ListView distanceToZoneView) {
        this.distanceToZoneView = distanceToZoneView;
    }

    public void setMiniCompassView(MiniCompassView miniCompassView) {
        this.miniCompassView = miniCompassView;
    }

    public void init(GpsFictionActivity gpsFictionActivity, Zone attachedZone) {
        // TODO Auto-generated method stub
        this.gpsFictionActivity = gpsFictionActivity;
        this.attachedZone = attachedZone;
        this.zoneTitleView.setText(this.attachedZone.getName());
        this.updateZoneTitleView();
        this.gpsFictionActivity.getGpsFictionData().addZoneSelectListener(GpsFictionData.REGISTER.HOLDERVIEW, this);
        this.miniCompassView.init(this.gpsFictionActivity, this.attachedZone);
        this.distanceToZoneView.init(this.gpsFictionActivity, this.attachedZone);
    }

    @Override
    public void onZoneSelectChanged(Zone selectedZone) {
        // TODO Auto-generated method stub
        this.selectedZone = selectedZone;
        this.updateZoneTitleView();
    }


}
