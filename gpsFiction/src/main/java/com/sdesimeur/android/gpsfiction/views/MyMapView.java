package com.sdesimeur.android.gpsfiction.views;

import android.content.Context;
import android.graphics.Canvas;
import android.view.DragEvent;
import android.view.View;

import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListener;
import com.sdesimeur.android.gpsfiction.classes.PlayerBearingEvent;
import com.sdesimeur.android.gpsfiction.classes.PlayerBearingListener;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationEvent;
import com.sdesimeur.android.gpsfiction.classes.PlayerLocationListener;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;
import com.sdesimeur.android.gpsfiction.geopoint.GeoPoint;
import org.oscim.android.MapView;

public class MyMapView extends MapView implements PlayerLocationListener, ZoneSelectListener, PlayerBearingListener {

    private static final float SQ2 = 1.414213562373095f;
    private float playerBearing = 0;
    private boolean centerOnPlayer = true;
    private GeoPoint centerMap = null;
    private byte zoomLevel = 15;
    private GpsFictionActivity gpsFictionActivity;

    public MyMapView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public boolean isCenterOnPlayer() {
        return centerOnPlayer;
    }

    public Layers getLayers() {
        return this.getLayerManager().getLayers();
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        this.centerOnPlayer = false;
        super.onDragEvent(event);
        return true;
    }

    public void init(GpsFictionActivity gpsFictionActivity) {
        this.setClickable(true);
        this.getMapScaleBar().setVisible(false);
        this.setBuiltInZoomControls(false);
        this.getMapZoomControls().setZoomLevelMin((byte) 3);
        this.getMapZoomControls().setZoomLevelMax((byte) 20);
        this.gpsFictionActivity = gpsFictionActivity;
        MyLocationListener myLocationListener = this.gpsFictionActivity.getMyLocationListener();
        myLocationListener.addPlayerLocationListener(MyLocationListener.REGISTER.VIEW, this);
        myLocationListener.addPlayerBearingListener(MyLocationListener.REGISTER.LAYOUT, this);
        this.gpsFictionActivity.getGpsFictionData().addZoneSelectListener(GpsFictionData.REGISTER.VIEW, this);
        GeoPoint playerLocation = this.gpsFictionActivity.getMyLocationListener().getPlayerGeoPoint();
        this.centerMap = new LatLong(playerLocation.getLatitude(), playerLocation.getLongitude());
        this.getModel().mapViewPosition.setCenter(this.centerMap);
        this.getModel().mapViewPosition.setZoomLevel(this.zoomLevel);
    }

    @Override
    public void onZoneSelectChanged(Zone selectedZone) {
        // TODO Auto-generated method stub
        this.invalidate();
    }

    @Override
    public void onLocationPlayerChanged(PlayerLocationEvent playerLocationEvent) {
        // TODO Auto-generated method stub
        //if (this.centerMap == this.playerPosition) {
        if (this.centerOnPlayer) {
            this.centerMap = playerLocationEvent.getLocationOfPlayer();
            this.set.getModel().mapViewPosition.setCenter(this.centerMap);
        }
        //this.getLayers().get(0).requestRedraw();
        this.invalidate();
    }

/*
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        final int width = getWidth();
        final int height = getHeight();
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View view = getChildAt(i);
            final int childWidth = view.getMeasuredWidth();
            final int childHeight = view.getMeasuredHeight();
            final int childLeft = (width - childWidth) / 2;
            final int childTop = (height - childHeight) / 2;
            view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        }
    }
*/
    /*	@Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            int sizeSpec;
            if (w > h) {
                sizeSpec = MeasureSpec.makeMeasureSpec((int) (w * MyMapView.SQ2), MeasureSpec.EXACTLY);
            } else {
                sizeSpec = MeasureSpec.makeMeasureSpec((int) (h * MyMapView.SQ2), MeasureSpec.EXACTLY);
            }
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                getChildAt(i).measure(sizeSpec, sizeSpec);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }*/
    @Override
    public void onBearingPlayerChanged(PlayerBearingEvent playerBearingEvent) {
        // TODO Auto-generated method stub
        this.playerBearing = playerBearingEvent.getBearing();
        this.invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(-this.playerBearing, this.getWidth() / 2, this.getHeight() / 2);
        //this.mCanvas.delegate = canvas;
        //super.dispatchDraw(this.mCanvas);
        super.dispatchDraw(canvas);
        canvas.restore();
    }
}
