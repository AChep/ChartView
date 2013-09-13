package com.achep.chartview.basics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.achep.chartview.DebugViaCanvas;

/**
 * Created by Artem on 07.09.13.
 */
// TODO: make it scalable
// TODO: fill descriptions
public class Viewport implements DebugViaCanvas {

    private int realWidth;
    private int realHeight;

    private double mWidth;
    private double mHeight;

    private double mViewportX;
    private double mViewportWidth;
    private double mViewportY;
    private double mViewportHeight;

    private float mTouchX;
    private float mTouchY;

    @Override
    public void onDebug(Canvas canvas, Paint paint) {
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);

        paint.setTextSize(18);
        canvas.drawText("x=" + getViewportX() + " width=" + getViewportWidth() + " surface_w=" + getSurfaceWidth(), 10, 20, paint);
        canvas.drawText("y=" + getViewportY() + " height=" + getViewportHeight() + " surface_h=" + getSurfaceHeight(), 10, 40, paint);
    }

    public void onSizeChanged(int width, int height, int oldw, int oldh) {
        this.realWidth = width;
        this.realHeight = height;
    }

    public boolean onTouch(MotionEvent event) {
        final float x = event.getX(), y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                setViewportX(mViewportX - (x - mTouchX) * mViewportWidth / realWidth);
                setViewportY(mViewportY - (y - mTouchY) * mViewportHeight / realHeight);
            case MotionEvent.ACTION_DOWN:
                mTouchX = x;
                mTouchY = y;
                break;
        }
        return true;
    }

    private void fixHorizontalScrolling() {
        if (mViewportX + mViewportWidth > mWidth)
            mViewportX = mWidth - mViewportWidth;

        // Do not put it inside "else" statement!
        // Otherwise you'll get lil "jumps" while scrolling
        // when viewport size is bigger then surface size.
        if (mViewportX < 0) mViewportX = 0;
    }

    private void fixVerticalScrolling() {
        if (mViewportY + mViewportHeight > mHeight)
            mViewportY = mHeight - mViewportHeight;
        if (mViewportY < 0) mViewportY = 0;
    }

    // //////////////////////////////////////////
    // ////////////// -- INPUT -- ///////////////
    // //////////////////////////////////////////

    public void setSurfaceHeight(double height) {
        mHeight = height;
    }

    public void setSurfaceWidth(double width) {
        mWidth = width;
    }

    /**
     * Sets viewport size.
     *
     * @param width  must be >= {@link #getSurfaceWidth()}
     * @param height must be >= {@link #getSurfaceHeight()}
     * @see #getViewportWidth()
     * @see #getViewportHeight()
     */
    public void setViewport(int width, int height) {
        mViewportWidth = width;
        fixHorizontalScrolling();

        mViewportHeight = height;
        fixVerticalScrolling();
    }

    public void setViewportX(double x) {
        mViewportX = x;
        fixHorizontalScrolling();
    }

    public void setViewportY(double y) {
        mViewportY = y;
        fixVerticalScrolling();
    }

    public void resetScroll() {
        mViewportX = 0;
        mViewportY = 0;
    }

    // //////////////////////////////////////////
    // ///////////// -- OUTPUT -- ///////////////
    // //////////////////////////////////////////

    public final double getSurfaceWidth() {
        return mWidth;
    }

    public final double getSurfaceHeight() {
        return mHeight;
    }

    public final double getViewportX() {
        return mViewportX;
    }

    public final double getViewportY() {
        return mViewportY;
    }

    public final double getViewportWidth() {
        return mViewportWidth;
    }

    public final double getViewportHeight() {
        return mViewportHeight;
    }

}
