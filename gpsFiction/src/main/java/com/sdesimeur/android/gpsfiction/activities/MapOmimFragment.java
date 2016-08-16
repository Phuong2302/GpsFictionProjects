package com.sdesimeur.android.gpsfiction.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import com.mapswithme.maps.BuildConfig;
import com.mapswithme.maps.base.MapFragment;
import com.mapswithme.util.UiUtils;
import com.mapswithme.maps.R;

public class MapOmimFragment extends com.mapswithme.maps.base.MapFragment
        implements MyTabFragmentImpl
{
    private GpsFictionActivity gpsFictionActivity;
    private View rootView;

    @Override
    public View getRootView() {
        return this.rootView;
    }

    @Override
    public void setRootView(View rootView) {
        this.rootView = rootView;
    }


    @Override
    public void register(GpsFictionActivity gpsFictionActivity) {
        this.gpsFictionActivity = gpsFictionActivity;
    }

    public GpsFictionActivity getGpsFictionActivity() {
        return gpsFictionActivity;
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        final Surface surface = surfaceHolder.getSurface();
        final Rect rect = surfaceHolder.getSurfaceFrame();
        final DisplayMetrics metrics = new DisplayMetrics();
        final float exactDensityDpi = metrics.densityDpi;

        //mFirstStart = ((MwmActivity) getMwmActivity()).isFirstStart();
        super.surfaceCreated(surfaceHolder);
    }

    @Override
    boolean isFirstStart()
    {
        return false;
    }
}
