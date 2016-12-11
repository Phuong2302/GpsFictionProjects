package com.sdesimeur.android.gpsfiction.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.classes.DistanceByRouteListener;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;
import com.sdesimeur.android.gpsfiction.helpers.DistanceToTextHelper;

import org.oscim.layers.PathLayer;

/**
 * Created by sam on 11/12/16.
 */

public class ZoneDistance4MapFragment extends TextView implements DistanceByRouteListener {
    public ZoneDistance4MapFragment(Context context) {
        super(context);
    }

    public ZoneDistance4MapFragment(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoneDistance4MapFragment(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onAttachedToWindow() {
        GpsFictionControler gfc = (GpsFictionControler) getTag(R.id.gpsFictionControlerId);
        gfc.addDistanceByRouteChangeListener(this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        GpsFictionControler gfc = (GpsFictionControler) getTag(R.id.gpsFictionControlerId);
        gfc.removeDistanceByRouteChangeListener(this);
        super.onDetachedFromWindow();
    }
    public void setViewDistanceToDest (float distanceToEnd) {
        GpsFictionControler gfc = (GpsFictionControler) getTag(R.id.gpsFictionControlerId);
        PathLayer routePathLayer = gfc.getRoutePathLayer();
        if ((routePathLayer!=null) && (routePathLayer.getPoints().size() > 1)) {
            DistanceToTextHelper d = new DistanceToTextHelper(distanceToEnd);
            ((ViewGroup)getParent()).setVisibility(View.VISIBLE);
            setText(d.getDistanceInText());
            invalidate();
        } else {
            ((ViewGroup)getParent()).setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onDistanceByRouteChanged(float distance) {
        setViewDistanceToDest(distance);
    }


}
