package com.achep.chartview.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.achep.chartview.ChartSeries;
import com.achep.chartview.ChartView;
import com.achep.chartview.basics.Path;
import com.achep.fifteenpuzzle.utils.LogUtils;

/**
 * Created by Artem on 07.09.13.
 */
public class LineChartView extends ChartView {

    private boolean isDataInitialized;
    private SurfaceMode mSurfaceMode = SurfaceMode.DYNAMIC;

    public static enum SurfaceMode {
        DYNAMIC, MANUAL, DYNAMIC_MAX, DYNAMIC_MIN
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public com.achep.chartview.basics.Viewport onCreateViewport() {
        return new Viewport();
    }

    @Override
    public void onDraw(Canvas canvas) {
        final Viewport viewport = (Viewport) getViewport();
        final int height = getHeight();
        final int width = getWidth();

        final int startIndex = (int) viewport.getViewportX();
        final float offsetX = (float) viewport.getViewportX() - startIndex;
        float stepX = (float) width / ((int) viewport.getViewportWidth() - 1);
        final float startX = -offsetX * stepX;
        int endIndex = startIndex + (int) viewport.getViewportWidth();
        if (offsetX != 0) endIndex++;

        double viewportHeight = viewport.getViewportHeight();
        double viewportY = viewport.getSurfaceHeight() - viewportHeight - viewport.getViewportY()
                + viewport.getHeightOffset();

        if (getDebugEnabled()) {
            resetPaints();
            canvas.drawText("view_y=" + viewportY + " viw_height=" + viewportHeight, 20, height - 20, getPaint());
        }

        final int size = getChartSeries().size();
        for (int i = 0; i < size; i++) {
            final ChartSeries series = getChartSeries().get(i);
            final int seriesSize = series.size();
            if (seriesSize < 2 || seriesSize < startIndex) continue;

            resetPaints();
            onDrawSeries(canvas, series, startX, stepX, startIndex,
                    seriesSize < endIndex ? seriesSize : endIndex,
                    viewportY, viewportHeight, height);
        }

        resetPaints();
        Paint paint = getPaint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xA0808080);
        paint.setStrokeWidth(2);
        Path path = getPath();

        float padding = paint.getStrokeWidth() / 2;
        path.moveTo(padding, padding);
        path.lineTo(width - padding, padding);
        path.lineTo(width - padding, height - padding);
        path.lineTo(padding, height - padding);
        path.close();
        canvas.drawPath(path, paint);

      /*  int n = 40;

        int end = (int) (viewportY + viewportHeight);
        for (int a = (int) Math.floor(viewportY / n) * n; a <= end; a += n) {
            float y = (float) ((1f - (float) (a - viewportY) / viewportHeight) * height);
            canvas.drawLine(0, y, width, y, paint);
        }

        end = (int) (viewport.getViewportX() + viewport.getViewportWidth());
        for (int a = (int) Math.floor(viewport.getViewportX() / n) * n; a <= end; a += n) {
            float x = (float) ((float) (a - viewport.getViewportX()) / viewport.getViewportWidth() * width);
            canvas.drawLine(x, 0, x, height, paint);
        }*/


        // Draw some basics.
        super.onDraw(canvas);
    }

    /**
     * Called on draw series.
     *
     * @param canvas
     * @param series         chart series to draw
     * @param x              start x coordinate
     * @param stepX
     * @param i              start index of visible values
     * @param length         end index of visible values
     * @param viewportY
     * @param viewportHeight
     * @param height         {@link android.graphics.Canvas#getHeight()}
     */
    public void onDrawSeries(Canvas canvas, ChartSeries series,
                             float x, float stepX, int i, int length,
                             double viewportY, double viewportHeight,
                             int height) {
        Paint paint = getPaint();
        Path path = getPath();
        path.setLimits(20, height - 20, true);

        boolean first = true;
        for (; i < length; i++, x += stepX) {
            float y = (float) (1.0 - (series.get(i) - viewportY) / viewportHeight) * height;
            if (first) {
                first = false;
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        paint.setColor(series.getStyle().color);
        paint.setStrokeWidth(series.getStyle().strokeWidth);

        // Draw the graph line
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(getPath(), getPaint());

        if (series.getStyle().backgroundColor != Color.TRANSPARENT) {
            paint.setColor(series.getStyle().backgroundColor);
            paint.setStyle(Paint.Style.FILL);

            path.lineTo(x - stepX, height);
            path.lineTo(0, height);
            path.close();
            canvas.drawPath(path, paint);
        }
    }

    @Override
    public void notifyDataChanged() {
        super.notifyDataChanged();

        Viewport viewport = (Viewport) getViewport();
        double min = isDataInitialized ? viewport.getHeightOffset() : Double.MAX_VALUE;
        double max = isDataInitialized ? viewport.getSurfaceHeight() + min : Double.MIN_VALUE;
        int index = 0;

        final int size = getChartSeries().size();
        for (int i = 0; i < size; i++) {
            double value;

            final ChartSeries series = getChartSeries().get(i);
            if (series.isEmpty()) continue;

            if ((value = series.getMax()) > max)
                max = value;
            if ((value = series.getMin()) < min)
                min = value;
            if ((value = series.size()) > index)
                index = (int) value;
        }
        LogUtils.d("min=" + min + " max=" + max);

        if (index == 0) return; // there's no active (non-empty) series
        viewport.setHeightOffset(min);
        viewport.setSurfaceSize(index, max - min, true);

        isDataInitialized = true;
        invalidate();
    }

    /*public void setSurfaceParams(SurfaceMode surfaceMode, double min, double max) {
        mSurfaceMode = surfaceMode;

        Viewport viewport = (Viewport) getViewport();
        if (surfaceMode == SurfaceMode.MANUAL) {
            viewport.setSurfaceHeight(max - min);
            viewport.setHeightOffset(min);
        }
    }
*/
    public static class Viewport extends com.achep.chartview.basics.Viewport {

        private double mHeightOffset;

        public void setHeightOffset(double offset) {
            mHeightOffset = offset;
        }

        public double getHeightOffset() {
            return mHeightOffset;
        }

    }

}
