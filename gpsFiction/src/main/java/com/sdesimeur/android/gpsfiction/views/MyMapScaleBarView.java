package com.sdesimeur.android.gpsfiction.views;

import android.content.Context;
import android.graphics.Canvas;
import android.view.ViewGroup;

import org.mapsforge.core.graphics.GraphicContext;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.scalebar.DefaultMapScaleBar;
import org.mapsforge.map.scalebar.MapScaleBar;
import org.mapsforge.map.view.MapView;

/**
 * Created by sam on 12/08/15.
 */
public class MyMapScaleBarView extends ViewGroup {
    private MapScaleBar mapScaleBar;

    public MyMapScaleBarView(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    public void init(MapView mapView) {
        this.mapScaleBar = new DefaultMapScaleBar(mapView.getModel().mapViewPosition, mapView.getModel().mapViewDimension,
                AndroidGraphicFactory.INSTANCE, mapView.getModel().displayModel);
        ((DefaultMapScaleBar) this.mapScaleBar).setScaleBarMode(DefaultMapScaleBar.ScaleBarMode.BOTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        org.mapsforge.core.graphics.Canvas graphicContext = AndroidGraphicFactory.createGraphicContext(canvas);
        //this.setBackgroundResource(R.color.backgroundcolorminicompass);
        //canvas.drawColor(Color.WHITE);
        this.mapScaleBar.draw((GraphicContext) canvas);
        super.onDraw(canvas);
        graphicContext.destroy();
    }
}
