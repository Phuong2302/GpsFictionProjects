package com.sdesimeur.android.gpsfiction.utils;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

/**
 * Created by sam on 28/10/16.
 */

public class MyDrawable {
    public Drawable getmDrawable() {
        return mDrawable;
    }

    public void setmDrawable(Drawable d) {
        this.mDrawable = d;
    }

    private Drawable mDrawable;

    public void setAngle(float a) {
        this.angle = a;
    }

    private float angle=0;

    public MyDrawable (Drawable d) {
        mDrawable = d;
    }
    public Drawable getRotated (float a) {
        angle=a;
        return getRotated();
    }
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
}
