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
import com.sdesimeur.android.gpsfiction.classes.GpsFictionControler;
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
    private boolean firstTimePathsDefined = true;
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
        typeface = typeface;
    }

    private float getRoseHalfSize() {
        float h = (float) getSize();
        return (h * SCALEROSE / 2);
    }

    private float getTextSize() {
        float h = (float) getSize();
        return ((h / 2 - getRoseHalfSize()) * FACTORTEXT);
    }

    private float getMarginSize() {
        float h = (float) getSize();
        return ((h / 2 - getRoseHalfSize() - getTextSize()) / 2);
    }

    private void setTextStaticLayout() {
        textPaint.setFlags(TextPaint.FAKE_BOLD_TEXT_FLAG);
        textPaint.setAntiAlias(true);
        textPaint.setColor(getResources().getColor(R.color.compasstext));
        textPaint.setTextSize(getTextSize());
        textPaint.setFakeBoldText(true);
        textPaint.setTypeface(typeface);
        String[] pointsCardinauxTexts = getResources().getStringArray(R.array.pointscardinaux);
        for (String txt : pointsCardinauxTexts) {
            cardinalStaticLayout.add(new StaticLayout(txt, textPaint, TEXTWIDTH, Layout.Alignment.ALIGN_CENTER, 0, 0, false));
        }
    }

    private void setPathsAndSavedCanvas(Canvas canvas) {
        firstTimePathsDefined = false;
        setArrowPaths();
        setRoseOneBranchPath();
        setCirclePath();
        setTextStaticLayout();
    }

    private void setCirclePath() {
        float h = getRoseHalfSize();
        circlePath.addCircle(0, 0, h, Path.Direction.CW);

        circlePaint.setColor(getResources().getColor(R.color.compasscircle));
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);
    }

    private void setRoseOneBranchPath() {
        float h = getRoseHalfSize();
        roseOneBranchPath.moveTo(0, -h);
        roseOneBranchPath.lineTo(-h / 5, 0);
        roseOneBranchPath.lineTo(0, h);
        roseOneBranchPath.lineTo(h / 5, 0);
        roseOneBranchPath.close();

        roseOneBranchPaint.setAntiAlias(true);
        roseOneBranchPaint.setColor(getResources().getColor(R.color.compassrose));
        roseOneBranchPaint.setStyle(Paint.Style.FILL);
    }

    private void setArrowPaths() {
        float c = ((float) getSize()) / 2;
        float h = c * SCALEARROW;
        float m = 2 * (c - h) / 3;
        arrowPath.moveTo(0, -h - m);
        arrowPath.lineTo(-h * 2 / 5, h - m);
        arrowPath.lineTo(0, h * 3 / 5 - m);
        arrowPath.lineTo(h * 2 / 5, h - m);
        arrowPath.close();

        h = h / 20;
        centerPath.addCircle(0, 0, h, Path.Direction.CW);
    }

    private void drawStaticPaths(Canvas canvas) {
        canvas.save();
        canvas.drawPath(circlePath, circlePaint);
        canvas.drawPath(roseOneBranchPath, roseOneBranchPaint);
        canvas.rotate((float) 90.0);
        canvas.drawPath(roseOneBranchPath, roseOneBranchPaint);
        canvas.restore();
        canvas.scale((float) 0.8, (float) 0.8);
        canvas.rotate((float) 45.0);
        canvas.drawPath(roseOneBranchPath, roseOneBranchPaint);
        canvas.rotate((float) -90.0);
        canvas.drawPath(roseOneBranchPath, roseOneBranchPaint);
        canvas.restore();
    }

    private void drawArrowPaths(Canvas canvas) {
        if ((selectedZone == null) || (selectedZone.isPlayerInThisZone())) {
            arrowPaint.setAntiAlias(true);
            arrowPaint.setColor(getResources().getColor(R.color.compasscenter));
            arrowPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(centerPath, arrowPaint);
        } else {
            canvas.save();
            canvas.rotate(selectedZone.getBearing2Zone());
            arrowPaint.setAntiAlias(true);
            arrowPaint.setColor(getResources().getColor(R.color.compassarrow));
            arrowPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(arrowPath, arrowPaint);
            arrowPaint.setColor(getResources().getColor(R.color.compasscenter));
//            arrowPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(centerPath, arrowPaint);
            canvas.restore();
        }
    }

    private void drawTexts(Canvas canvas) {
        canvas.save();
        // calculate x and y position where your text will be placed
        float textY = getRoseHalfSize() + getMarginSize() + getTextSize();
        float textX = 0;
        int n = cardinalStaticLayout.size();
        StaticLayout sl = null;
        for (int i = 0; i < n; i++) {
            sl = cardinalStaticLayout.get(i);
            textX = (float) (-sl.getWidth()) / 2;
            canvas.save();
            canvas.rotate((float) (360 * i / n));
            canvas.translate(textX, -textY);
            sl.draw(canvas);
            canvas.restore();
        }
        canvas.restore();
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getSize(), getSize());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int tx = getSize() / 2;
        canvas.translate(tx, tx);
        canvas.rotate(-bearingOfPlayer);
        canvas.save();
        if (firstTimePathsDefined) {
            setPathsAndSavedCanvas(canvas);
        }
        drawStaticPaths(canvas);
        drawTexts(canvas);
        drawArrowPaths(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        GpsFictionControler gfc = (GpsFictionControler) getTag();
        gfc.addPlayerBearingListener(GpsFictionControler.REGISTER.VIEW, this);
        gfc.addZoneSelectListener(GpsFictionControler.REGISTER.VIEW, this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        GpsFictionControler gfc = (GpsFictionControler) getTag();
        gfc.removePlayerBearingListener(GpsFictionControler.REGISTER.VIEW, this);
        gfc.removeZoneSelectListener(GpsFictionControler.REGISTER.VIEW, this);
        super.onDetachedFromWindow();
    }

    private int getSize() {
        int size = getResources().getDimensionPixelSize(R.dimen.compassSize);
        return size;
    }

    @Override
    public void onZoneSelectChanged(Zone sZ, Zone uSZ) {
        // TODO Auto-generated method stub
        selectedZone = sZ;
        invalidate();
    }

    @Override
    public void onBearingPlayerChanged(float angle) {
        // TODO Auto-generated method stub
        bearingOfPlayer = angle;
        invalidate();
    }
}
