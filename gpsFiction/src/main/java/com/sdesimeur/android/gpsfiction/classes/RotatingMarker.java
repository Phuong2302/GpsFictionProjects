package com.sdesimeur.android.gpsfiction.classes;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;

import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.overlay.Marker;

public class RotatingMarker extends Marker implements PlayerBearingListener {

    protected GpsFictionActivity gpsFictionActivity;
    private float playerBearing = 0;
    private Resources resources = null;
    private int resource = 0;

    public RotatingMarker(LatLong latLong, Bitmap bitmap, int horizontalOffset, int verticalOffset) {
        super(latLong, bitmap, horizontalOffset, verticalOffset);
        // TODO Auto-generated constructor stub
    }

    public RotatingMarker(LatLong latLong) {
        super(latLong, null, 0, 0);
    }

    public RotatingMarker(LatLong latLong, Resources resources, int resource) {
        super(latLong, null, 0, 0);
        this.setResource(resources, resource);
    }

    private void updateMarker() {
        android.graphics.Bitmap bitmap = BitmapFactory.decodeResource(this.resources, this.resource);
        Matrix matrix = new Matrix();
        int bx = bitmap.getWidth();
        int by = bitmap.getHeight();
        float ax = bx / 2 + 1;
        float ay = by / 2 + 1;
        matrix.setRotate(this.playerBearing, ax, ay);
        android.graphics.Bitmap androidBitmapRotated = android.graphics.Bitmap.createBitmap(bitmap, 0, 0, bx, by, matrix, true);
        BitmapDrawable bmd = new BitmapDrawable(this.resources, androidBitmapRotated);
        Bitmap bitmapRotated = AndroidGraphicFactory.convertToBitmap(bmd);
        this.setBitmap(bitmapRotated);
        //int cx = bitmapRotated.getWidth() / 2 + 1;
        //int cy = bitmapRotated.getHeight() / 2 + 1;
        //this.setHorizontalOffset(cx);
        //this.setVerticalOffset(cy);
    }

    public void setResource(Resources resources, int resource) {
        this.resources = resources;
        this.resource = resource;
        this.updateMarker();
    }

    public void register(GpsFictionActivity gpsFictionActivity) {
        this.gpsFictionActivity = gpsFictionActivity;
        this.gpsFictionActivity.getMyLocationListener().addPlayerBearingListener(MyLocationListener.REGISTER.MARKER, this);
    }

    @Override
    public void onBearingPlayerChanged(PlayerBearingEvent playerBearingEvent) {
        // TODO Auto-generated method stub
        this.playerBearing = playerBearingEvent.getBearing();
        this.updateMarker();
    }

}
