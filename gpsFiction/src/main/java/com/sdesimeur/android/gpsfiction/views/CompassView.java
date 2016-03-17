package com.sdesimeur.android.gpsfiction.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.sdesimeur.android.gpsfiction.R;
import com.sdesimeur.android.gpsfiction.activities.GpsFictionActivity;
import com.sdesimeur.android.gpsfiction.classes.GpsFictionData;
import com.sdesimeur.android.gpsfiction.classes.MyLocationListener;
import com.sdesimeur.android.gpsfiction.classes.PlayerBearingEvent;
import com.sdesimeur.android.gpsfiction.classes.PlayerBearingListener;
import com.sdesimeur.android.gpsfiction.classes.Zone;
import com.sdesimeur.android.gpsfiction.classes.ZoneSelectListener;

import java.util.ArrayList;

public class CompassView extends View implements PlayerBearingListener, ZoneSelectListener {
    private static final float SCALEROSE = (float) 0.8;
    private static final float FACTORTEXT = (float) 0.8;
    private static final float SCALEARROW = (float) 0.6;
    private static final int TEXTWIDTH = 300;
    private Paint arrowPaint = new Paint();
    private Path arrowPath = new Path();
    private Paint circlePaint = new Paint();
    private Path circlePath = new Path();
    private Paint roseOneBranchPaint = new Paint();
    private Path roseOneBranchPath = new Path();
    private Path centerPath = new Path();
    private TextPaint textPaint = new TextPaint();
    private ArrayList<StaticLayout> cardinalStaticLayout = new ArrayList<StaticLayout>();
    private Zone selectedZone = null;
    private float bearingOfPlayer = 0;
    private Typeface typeface = null;
    //private Canvas savedCanvas;
    //private GpsFictionActivity gpsFictionActivity = null;
    @SuppressWarnings("unused")
    private boolean mAnimate;
    @SuppressWarnings("unused")
    private long mNextTime;
    private boolean firstTimePathsDefined = true;
    private GpsFictionActivity gpsFictionActivity;
    public CompassView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    private float getRoseHalfSize() {
        float h = (float) this.getSize();
        return (h * SCALEROSE / 2);
    }

    private float getTextSize() {
        float h = (float) this.getSize();
        return ((h / 2 - this.getRoseHalfSize()) * FACTORTEXT);
    }

    private float getMarginSize() {
        float h = (float) this.getSize();
        return ((h / 2 - this.getRoseHalfSize() - this.getTextSize()) / 2);
    }

    private void setTextStaticLayout() {
//    	AssetManager myassetmgr = MyLocationListener.getContext().getAssets();
//    	typeface = Typeface.createFromAsset(myassetmgr, "/fonts/fally.ttf");
//    	Typeface typeface = Typeface.createFromFile("/mnt/sdcard/fonts/fally.ttf");
        this.textPaint.setFlags(TextPaint.FAKE_BOLD_TEXT_FLAG);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setColor(this.getResources().getColor(R.color.compasstext));
        this.textPaint.setTextSize(this.getTextSize());
        this.textPaint.setFakeBoldText(true);
        this.textPaint.setTypeface(this.typeface);
        String[] pointsCardinauxTexts = this.getResources().getStringArray(R.array.pointscardinaux);
        for (String txt : pointsCardinauxTexts) {
            this.cardinalStaticLayout.add(new StaticLayout(txt, this.textPaint, TEXTWIDTH, Layout.Alignment.ALIGN_CENTER, 0, 0, false));
        }
    }

    private void setPathsAndSavedCanvas(Canvas canvas) {
        /*
		this.savedCanvas = new Canvas();
    	this.savedCanvas.translate(height/2, width/2);
    	this.savedCanvas.drawColor(this.getResources().getColor(R.color.backgroundcolorminicompass));
	    */
        this.firstTimePathsDefined = false;
		/*
		int tx = canvas.getWidth() / 2;
    	int ty = canvas.getHeight() / 2;
    	canvas.translate(tx, ty);
    	canvas.drawColor(this.getResources().getColor(R.color.backgroundcolorminicompass));
    	*/
        this.setArrowPaths();
        this.setRoseOneBranchPath();
        this.setCirclePath();
        this.setTextStaticLayout();
    }

    private void setCirclePath() {
        float h = this.getRoseHalfSize();
        this.circlePath.addCircle(0, 0, h, Path.Direction.CW);

        this.circlePaint.setColor(this.getResources().getColor(R.color.compasscircle));
        this.circlePaint.setStyle(Paint.Style.FILL);
        this.circlePaint.setAntiAlias(true);
    }

    private void setRoseOneBranchPath() {
        float h = this.getRoseHalfSize();
        this.roseOneBranchPath.moveTo(0, -h);
        this.roseOneBranchPath.lineTo(-h / 5, 0);
        this.roseOneBranchPath.lineTo(0, h);
        this.roseOneBranchPath.lineTo(h / 5, 0);
        this.roseOneBranchPath.close();

        this.roseOneBranchPaint.setAntiAlias(true);
        this.roseOneBranchPaint.setColor(this.getResources().getColor(R.color.compassrose));
        this.roseOneBranchPaint.setStyle(Paint.Style.FILL);
    }

    private void setArrowPaths() {
        float c = ((float) this.getSize()) / 2;
        float h = c * SCALEARROW;
        float m = 2 * (c - h) / 3;
        this.arrowPath.moveTo(0, -h - m);
        this.arrowPath.lineTo(-h * 2 / 5, h - m);
        this.arrowPath.lineTo(0, h * 3 / 5 - m);
        this.arrowPath.lineTo(h * 2 / 5, h - m);
        this.arrowPath.close();

        h = h / 20;
        this.centerPath.addCircle(0, 0, h, Path.Direction.CW);
    }

    private void drawStaticPaths(Canvas canvas) {
        canvas.save();
        canvas.drawPath(this.circlePath, this.circlePaint);
        canvas.drawPath(this.roseOneBranchPath, this.roseOneBranchPaint);
        canvas.rotate((float) 90.0);
        canvas.drawPath(this.roseOneBranchPath, this.roseOneBranchPaint);
        canvas.restore();
        canvas.scale((float) 0.8, (float) 0.8);
        canvas.rotate((float) 45.0);
        canvas.drawPath(this.roseOneBranchPath, this.roseOneBranchPaint);
        canvas.rotate((float) -90.0);
        canvas.drawPath(this.roseOneBranchPath, this.roseOneBranchPaint);
        canvas.restore();
    }

    private void drawArrowPaths(Canvas canvas) {
        if ((this.selectedZone == null) || (this.selectedZone.isPlayerInThisZone())) {
            this.arrowPaint.setAntiAlias(true);
            this.arrowPaint.setColor(this.getResources().getColor(R.color.compasscenter));
            this.arrowPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(this.centerPath, this.arrowPaint);
        } else {
            canvas.save();
            canvas.rotate(this.selectedZone.getBearing2Zone());
            this.arrowPaint.setAntiAlias(true);
            this.arrowPaint.setColor(this.getResources().getColor(R.color.compassarrow));
            this.arrowPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(this.arrowPath, this.arrowPaint);
            this.arrowPaint.setColor(this.getResources().getColor(R.color.compasscenter));
//            this.arrowPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(this.centerPath, this.arrowPaint);
            canvas.restore();
        }
    }

    private void drawTexts(Canvas canvas) {
        canvas.save();
        // calculate x and y position where your text will be placed
        float textY = this.getRoseHalfSize() + this.getMarginSize() + this.getTextSize();
        float textX = 0;
        int n = this.cardinalStaticLayout.size();
        StaticLayout sl = null;
        for (int i = 0; i < n; i++) {
            sl = this.cardinalStaticLayout.get(i);
            textX = (float) (-sl.getWidth()) / 2;
            canvas.save();
            canvas.rotate((float) (360 * i / n));
            canvas.translate(textX, -textY);
            sl.draw(canvas);
            canvas.restore();
        }
        canvas.restore();
    }

    //public void register(GpsFictionActivity gpsFictionActivity) {
    //	this.setGpsFictionActivity(gpsFictionActivity);
    //	this.getGpsFictionActivity().getGpsFictionData().getMyLocationListener().addPlayerBearingListener(MyLocationListener.REGISTER.VIEW,this);
    //	this.getGpsFictionActivity().getGpsFictionData().addZoneSelectListener(this);
    //}
    //public void onSizeChanged (int w, int h, int oldw, int oldh) {
    //	super.onSizeChanged(w, h, oldw, oldh);
    //	this.firstTimePathsDefined=true;
    //	this.invalidate();
    //}
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //int size = this.getSize();
        this.setMeasuredDimension(this.getSize(), this.getSize());
        //int size =Math.min(this.getHeight(),this.getWidth());
        //this.setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //this.setBackgroundResource(R.color.backgroundcolorminicompass);
        //canvas.drawColor(Color.WHITE);
        int tx = this.getSize() / 2;
        canvas.translate(tx, tx);
        canvas.rotate(-this.bearingOfPlayer);
        canvas.save();
        //canvas.drawColor(this.getResources().getColor(R.color.backgroundcolorminicompass));
        if (this.firstTimePathsDefined) {
            this.setPathsAndSavedCanvas(canvas);
        }
        this.drawStaticPaths(canvas);
        this.drawTexts(canvas);
        this.drawArrowPaths(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        mAnimate = true;
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        mAnimate = false;
        super.onDetachedFromWindow();
    }

    //private Zone getZoneSelected() {
    // TODO Auto-generated method stub
    //	return this.getGpsFictionActivity().getGpsFictionData().getSelectedZone();
    //}
    //public void setGpsFictionActivity (GpsFictionActivity gpsFictionActivity) {
    //	this.gpsFictionActivity = gpsFictionActivity;
    //}
    //public GpsFictionActivity getGpsFictionActivity () {
    //	return this.gpsFictionActivity;
    //}
    private int getSize() {
        int size = getResources().getDimensionPixelSize(R.dimen.compassSize);
        return size;
        //return this.getHeight();
    }

    @Override
    public void onZoneSelectChanged(Zone selectedZone) {
        // TODO Auto-generated method stub
        this.selectedZone = selectedZone;
        this.invalidate();
    }

    @Override
    public void onBearingPlayerChanged(PlayerBearingEvent playerBearingEvent) {
        // TODO Auto-generated method stub
        this.bearingOfPlayer = playerBearingEvent.getBearing();
        this.invalidate();
    }

    public void init(GpsFictionActivity gpsFictionActivity) {
        this.gpsFictionActivity = gpsFictionActivity;
        this.gpsFictionActivity.getMyLocationListener().addPlayerBearingListener(MyLocationListener.REGISTER.VIEW, this);
        this.gpsFictionActivity.getGpsFictionData().addZoneSelectListener(GpsFictionData.REGISTER.VIEW, this);
    }
}
