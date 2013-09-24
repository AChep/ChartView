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

    private int mRealWidth;
    private int mRealHeight;

    private double mSurfaceWidth;
    private double mSurfaceHeight;

    private double mViewportX;
    private double mViewportWidth;
    private double mViewportY;
    private double mViewportHeight;

    private float mTouchX;
    private float mTouchY;

    private OnSurfaceSizeChangedListener mOnSurfaceSizeChangedListener;

    public interface OnSurfaceSizeChangedListener {

        public void onSurfaceSizeChanged(Viewport vp, double surfaceWidth, double surfaceHeight,
                                         double surfaceOldWidth, double surfaceOldHeight);
    }

    public void setOnSurfaceSizeChangedListener(OnSurfaceSizeChangedListener listener) {
        mOnSurfaceSizeChangedListener = listener;
    }

    @Override
    public void onDebug(Canvas canvas, Paint paint) {
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);

        paint.setTextSize(18);
        canvas.drawText("x=" + getViewportX() + " width=" + getViewportWidth(), 10, 20, paint);
        canvas.drawText("surface_w=" + getSurfaceWidth() + " viewport_w=" + getViewportWidth(), 10, 40, paint);
        canvas.drawText("y=" + getViewportY() + " height=" + getViewportHeight() + " surface_h=" + getSurfaceHeight(), 10, 60, paint);
    }

    public void onSizeChanged(int width, int height, int oldw, int oldh) {
        mRealWidth = width;
        mRealHeight = height;

        if (isSurfaceReady() && mOnSurfaceSizeChangedListener != null) {
            mOnSurfaceSizeChangedListener.onSurfaceSizeChanged(this, mSurfaceWidth, mSurfaceHeight, mSurfaceWidth, mSurfaceHeight);
        }
    }

    public boolean onTouch(MotionEvent event) {
        final float x = event.getX(), y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                setViewportX(mViewportX - (x - mTouchX) * mViewportWidth / mRealWidth);
                setViewportY(mViewportY - (y - mTouchY) * mViewportHeight / mRealHeight);
            case MotionEvent.ACTION_DOWN:
                mTouchX = x;
                mTouchY = y;
                break;
        }
        return true;
    }

    private void fixHorizontalScrolling() {
        if (mViewportX + mViewportWidth > mSurfaceWidth)
            mViewportX = mSurfaceWidth - mViewportWidth;

        // Do not put it inside "else" statement!
        // Otherwise you'll get lil "jumps" while scrolling
        // when viewport size is bigger then surface size.
        if (mViewportX < 0) mViewportX = 0;
    }

    private void fixVerticalScrolling() {
        if (mViewportY + mViewportHeight > mSurfaceHeight)
            mViewportY = mSurfaceHeight - mViewportHeight;
        if (mViewportY < 0) mViewportY = 0;
    }

    // //////////////////////////////////////////
    // ////////////// -- INPUT -- ///////////////
    // //////////////////////////////////////////

    /**
     * Sets new width and height of the surface.
     *
     * @param width  new width of the surface
     * @param height new height of the surface
     * @param handle calls {@link OnSurfaceSizeChangedListener#onSurfaceSizeChanged(Viewport, double, double, double, double)} if true
     */
    public void setSurfaceSize(double width, double height, boolean handle) {
        double oldWidth = mSurfaceWidth;
        double oldHeight = mSurfaceHeight;
        mSurfaceWidth = width;
        mSurfaceHeight = height;

        if (handle && mOnSurfaceSizeChangedListener != null) {
            mOnSurfaceSizeChangedListener.onSurfaceSizeChanged(this, width, height, oldWidth, oldHeight);
        }
    }

    /**
     * Sets viewport size.
     *
     * @param width  must be >= {@link #getSurfaceWidth()}
     * @param height must be >= {@link #getSurfaceHeight()}
     * @see #getViewportWidth()
     * @see #getViewportHeight()
     */
    public void setViewportSize(double width, double height) {
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
    // //////////// -- SCROLLBAR -- /////////////
    // //////////////////////////////////////////

    public final double getHorizontalScrollBarWidth() {
        return mViewportWidth / mSurfaceWidth * mRealWidth;
    }

    public final double getHorizontalScrollBarX() {
        return mViewportX / mSurfaceWidth * mRealWidth;
    }

    public final double getVerticalScrollBarHeight() {
        return mViewportHeight / mSurfaceHeight * mRealHeight;
    }

    public final double getVerticalScrollBarY() {
        return mViewportY / mSurfaceHeight * mRealHeight;
    }

    // //////////////////////////////////////////
    // //////////// -- SURFACE -- ///////////////
    // //////////////////////////////////////////

    public boolean isSurfaceReady() {
        return mSurfaceWidth > 0 && mSurfaceHeight > 0;
    }

    public final double getSurfaceWidth() {
        return mSurfaceWidth;
    }

    public final double getSurfaceHeight() {
        return mSurfaceHeight;
    }

    // //////////////////////////////////////////
    // //////////// -- VIEWPORT -- //////////////
    // //////////////////////////////////////////

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

    // //////////////////////////////////////////
    // //////////// -- REALITY -- ///////////////
    // //////////////////////////////////////////

    public final int getRealWidth() {
        return mRealWidth;
    }

    public final int getRealHeight() {
        return mRealHeight;
    }

}
