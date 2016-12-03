package com.sdesimeur.android.gpsfiction.activities;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;
import com.sdesimeur.android.gpsfiction.views.MiniCompassView;
import com.sdesimeur.android.gpsfiction.views.ZoneDistance4ListView;

public class ViewHolder4Zones implements ZoneSelectListener {
    private TextView zoneTitleView = null;
    private MyTabFragment myTabFragment;

    private ZoneDistance4ListView distanceToZoneView = null;
    //	private TextView directionOfZone;
    private MiniCompassView miniCompassView = null;
    private Zone attachedZone = null;

    public Zone getAttachedZone() {
        return attachedZone;
    }

    private void updateZoneTitleView() {
        Resources res = myTabFragment.getResources();
        int titlebackgroundcolor = 0;
        if (myTabFragment.getmGpsFictionControler().getSelectedZone() == attachedZone) {
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

    public void init(MyTabFragment mtf, Zone aZone) {
        myTabFragment= mtf;
        attachedZone = aZone;
        zoneTitleView.setText(attachedZone.getName());
        zoneTitleView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                myTabFragment.getmGpsFictionControler().addZoneSelectListener(GpsFictionControler.REGISTER.HOLDERVIEW, ViewHolder4Zones.this);
            }
            @Override
            public void onViewDetachedFromWindow(View view) {
                myTabFragment.getmGpsFictionControler().removeZoneSelectListener(GpsFictionControler.REGISTER.HOLDERVIEW, ViewHolder4Zones.this);
            }
        });
        updateZoneTitleView();
        miniCompassView.init(myTabFragment, attachedZone);
        distanceToZoneView.init(myTabFragment, attachedZone);
    }

    @Override
    public void onZoneSelectChanged(Zone selectedZone, Zone uSZ) {
        // TODO Auto-generated method stub
        updateZoneTitleView();
    }


}
