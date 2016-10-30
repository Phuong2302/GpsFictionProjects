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
    private ZoneDistance4ListView distanceToZoneView = null;
    //	private TextView directionOfZone;
    private MiniCompassView miniCompassView = null;
    private Zone attachedZone = null;
    private GpsFictionData mGpsFictionData = null;

    public Zone getAttachedZone() {
        return attachedZone;
    }

    private void updateZoneTitleView() {
        // TODO Auto-generated method stub
        Resources res = mGpsFictionData.getmGpsFictionActivity().getResources();
        int titlebackgroundcolor = 0;
        if (mGpsFictionData.getSelectedZone() == attachedZone) {
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

    public void init(GpsFictionData gpsFictionData, Zone aZone) {
        // TODO Auto-generated method stub
        mGpsFictionData = gpsFictionData;
        attachedZone = aZone;
        zoneTitleView.setText(attachedZone.getName());
        updateZoneTitleView();
        mGpsFictionData.addZoneSelectListener(GpsFictionData.REGISTER.HOLDERVIEW, this);
        miniCompassView.init(mGpsFictionData, attachedZone);
        distanceToZoneView.init(mGpsFictionData, attachedZone);
    }

    @Override
    public void onZoneSelectChanged(Zone selectedZone, Zone uSZ) {
        // TODO Auto-generated method stub
        updateZoneTitleView();
    }


}
