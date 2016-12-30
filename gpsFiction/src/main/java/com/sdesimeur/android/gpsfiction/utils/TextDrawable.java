package com.sdesimeur.android.gpsfiction.utils;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.sdesimeur.android.gpsfiction.activities.R;


/**
 * Created by sam on 05/11/16.
 */

public class TextDrawable extends Drawable {
    private static final int DEFAULT_COLOR = Color.BLACK;
    private static final int DEFAULT_TEXTSIZE = 15;
    private Paint mPaint;
    private String mText;
    private int mIntrinsicWidth;
    private int mIntrinsicHeight;
    private Resources resources;
    private float textSize;
    private int mIntrinsicWidthAndHeight;

    public TextDrawable(Resources res, String text) {
        mText = text;
        resources = res;
        textSize = resources.getDimension(R.dimen.normalTextSize);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setTextAlign(Paint.Align.LEFT);
    }
    private void setPaintAndMesure () {
        mPaint.setTextSize(textSize);
        //mIntrinsicWidth = bounds.width();
        //mIntrinsicHeight = bounds.height();
        mIntrinsicWidth = (int) (mPaint.measureText(mText) + 0.5);
        mIntrinsicHeight = mPaint.getFontMetricsInt(null);
        mIntrinsicWidthAndHeight = (int) (Math.sqrt(mIntrinsicHeight*mIntrinsicHeight + mIntrinsicWidth * mIntrinsicWidth) + 0.5);
    }
    public void setTextSize (float i) {
        textSize = i;
        setPaintAndMesure();
    }
    public void setColor (int i) {
        mPaint.setColor(i);
    }
    @Override
    public void draw(Canvas canvas) {
        Rect bds = new Rect();
        mPaint.getTextBounds(mText,0,mText.length(),bds);
        //Rect bds = getBounds();
        //bounds.offsetTo(0,0);
        canvas.drawARGB(255, 0, 255, 0);
        canvas.drawText(mText, 0, mText.length(), -bds.left, -bds.top, mPaint);
        //canvas.drawText(mText, 0, mText.length(), 0, 0, mPaint);
    }
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
    @Override
    public int getIntrinsicWidth() {
        return 2*mIntrinsicWidthAndHeight;
        //return mIntrinsicWidth;
    }
    @Override
    public int getIntrinsicHeight() {
        return 2*mIntrinsicWidthAndHeight;
        //return mIntrinsicHeight;
    }
    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }
    @Override
    public void setColorFilter(ColorFilter filter) {
        mPaint.setColorFilter(filter);
    }
}
