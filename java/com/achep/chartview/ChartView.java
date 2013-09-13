package com.achep.chartview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.achep.chartview.basics.Path;
import com.achep.chartview.basics.Viewport;

import java.util.ArrayList;

/**
 * Created by Artem on 01.09.13.
 */
public abstract class ChartView extends View {

    private ArrayList<ChartSeries> mChartSeriesList = new ArrayList<ChartSeries>();
    private Viewport mViewport = onCreateViewport();
    private Paint mPaint = new Paint();
    private Path mPath = new Path();

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate();
        return mViewport.onTouch(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mViewport.onSizeChanged(w, h, oldw, oldh);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void onDraw(Canvas canvas) {
        resetPaints();
        mViewport.onDebug(canvas, mPaint);
    }

    public Paint getPaint() {
        return mPaint;
    }

    public Path getPath() {
        return mPath;
    }

    public void resetPaints() {
        mPath.reset();
        mPaint.reset();
        mPaint.setAntiAlias(true);
    }

    public abstract Viewport onCreateViewport();

    public Viewport getViewport() {
        return mViewport;
    }

    // //////////////////////////////////////////
    // ///////// -- GRAPH SERIES -- /////////////
    // //////////////////////////////////////////

    public void notifyDataChanged() { /* unused */ }

    /**
     * @param series
     * @see #notifyDataChanged()
     */
    public void addChartSeries(ChartSeries series) {
        mChartSeriesList.add(series);
    }

    /**
     * Remove series from the graph
     *
     * @param series series to remove
     * @return index of removed series
     */
    public int removeChartSeries(ChartSeries series) {
        int i = mChartSeriesList.indexOf(series);
        mChartSeriesList.remove(i);
        return i;
    }

    public ArrayList<ChartSeries> getChartSeries() {
        return mChartSeriesList;
    }
}
