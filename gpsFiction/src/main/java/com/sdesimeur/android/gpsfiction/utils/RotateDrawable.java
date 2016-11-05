package com.sdesimeur.android.gpsfiction.utils;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

/**
 * Created by sam on 28/10/16.
 */

public class RotateDrawable extends LayerDrawable {
    private float xOffset=0;
    private float yOffset=0;
    private Paint mPaint;
    private float angle=0;
    private Drawable mDrawable;

    public Drawable getmDrawable() {
        return mDrawable;
    }
/*
    public void setmDrawable(Drawable d) {
        this.mDrawable = d;
    }
*/
    public void setAngle(float a) {
        this.angle = a;
    }


    public RotateDrawable (Drawable d) {
        super(new Drawable[]{d});
        mDrawable = d;
    }
/*
    public Drawable getRotated (float a) {
        angle=a;
        return getRotated();
    }
*/
    public void setOffset (float x, float y) {
        xOffset = x;
        yOffset = y;
    }
/*
    public Drawable getRotated() {
        final Drawable[] arD = { mDrawable };
        return new LayerDrawable(arD) {
            @Override
            public void draw(final Canvas canvas) {
                canvas.save();
                canvas.rotate(angle, mDrawable.getBounds().width() / 2, mDrawable.getBounds().height() / 2);
                super.draw(canvas);
                canvas.restore();
            }
        };
    }
*/
    @Override
    public void draw(Canvas canvas) {
        int saveCount =  canvas.save();
        canvas.rotate(angle, mDrawable.getBounds().width() / 2 , mDrawable.getBounds().height() / 2 );
        super.draw(canvas);
        float dxSize =  mDrawable.getBounds().width()*xOffset;
        float dySize =  mDrawable.getBounds().height()*yOffset;
        float dx = (float) (dxSize * Math.cos(angle) - dySize * Math.sin(angle));
        float dy = (float) (dxSize * Math.sin(angle) + dySize * Math.cos(angle));
        canvas.translate(dx, dy);
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
        return mDrawable.getIntrinsicWidth();
    }
    @Override
    public int getIntrinsicHeight() {
        return mDrawable.getIntrinsicHeight();
    }
}
