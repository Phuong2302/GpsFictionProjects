package com.sdesimeur.android.gpsfiction.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewWithId extends ImageView {

    private int drawableId = 0;

    public ImageViewWithId(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public ImageViewWithId(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public ImageViewWithId(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int id) {
        this.drawableId = id;
        this.setImageDrawable(getResources().getDrawable(id));
    }

}
