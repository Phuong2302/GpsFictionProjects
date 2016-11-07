package com.sdesimeur.android.gpsfiction.utils;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

/**
 * Created by sam on 28/10/16.
 */

public class RotateDrawable extends LayerDrawable {
    private float angle = 0;
    private Drawable mDrawable;
    private int mIntrinsicWidth;
    private int mIntrinsicHeight;
    private float xRotateOffset = 0.5f;
    private float yRotateOffset = 0.5f;
  //  private Matrix mMatrix = new Matrix();
    private float xTranslate = 0;
    private float yTranslate = 0;
    private float xRotateCenter;
    private float yRotateCenter;

    public void setAngle(float a) {
        angle = a;
        applyMatrix();
    }

    private void applyMatrix() {
    /*
        Drawable dtemp = getConstantState().newDrawable();
        mIntrinsicHeight = dtemp.getIntrinsicHeight();
        mIntrinsicWidth = dtemp.getIntrinsicWidth();
    */
        mIntrinsicHeight = mDrawable.getIntrinsicHeight();
        mIntrinsicWidth = mDrawable.getIntrinsicWidth();
        xRotateCenter = xRotateOffset*mIntrinsicWidth;
        yRotateCenter = yRotateOffset*mIntrinsicHeight;
    /*
        mMatrix.setRotate(angle,xRotateCenter,yRotateCenter);
        RectF rf = new RectF(dtemp.getBounds());
        final boolean b = mMatrix.mapRect(rf);
        Rect rn = new Rect();
        rf.round(rn);
        mIntrinsicWidth = rn.width();
        mIntrinsicHeight = rn.height();
    */
    }

    public RotateDrawable (Drawable[] d) {
        super(d);
        mDrawable = d[0];
        mDrawable.getBounds().offsetTo(0,0);
        applyMatrix();
    }
    public void setRotateOffset (float x, float y) {
        xRotateOffset = x;
        yRotateOffset = y;
        applyMatrix();
    }
    public void setTranslate (float x, float y) {
        xTranslate = x;
        yTranslate = y;
    }
    @Override
    public void draw(Canvas canvas) {
        int saveCount =  canvas.save();
        canvas.drawARGB(125, 255, 0, 0);
        float tx = (float) (xTranslate * Math.cos(angle) - yTranslate * Math.sin(angle));
        float ty = (float) (xTranslate * Math.sin(angle) + yTranslate * Math.cos(angle));
        canvas.translate(tx, ty);
        canvas.rotate(angle, xRotateCenter , yRotateCenter);
        canvas.drawARGB(125, 0, 255, 0);
        canvas.getClipBounds().offsetTo(0,0);
        //Paint mPaint = new Paint();
        //mPaint.setColor(Color.BLUE);
        //mPaint.setStrokeCap(Paint.Cap.BUTT);
        //mPaint.setStyle(Style.FILL);
        //canvas.drawCircle(xRotateCenter,yRotateCenter, (float) Math.sqrt(xRotateCenter*xRotateCenter+yRotateCenter*yRotateCenter),mPaint);
        super.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    public void setAlpha(int alpha) {
        mDrawable.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter filter) {
        mDrawable.setColorFilter(filter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
    @Override
    public int getIntrinsicWidth() {
        return mIntrinsicWidth;
    }
    @Override
    public int getIntrinsicHeight() {
        return mIntrinsicHeight;
    }
}
