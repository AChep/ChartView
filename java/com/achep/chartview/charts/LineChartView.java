package com.achep.chartview.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.achep.chartview.ChartSeries;
import com.achep.chartview.ChartView;
import com.achep.chartview.basics.Path;
import com.achep.chartview.basics.Viewport;

/**
 * Created by Artem on 07.09.13.
 */
public class LineChartView extends ChartView {

    private boolean isDataInitialized;

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Viewport onCreateViewport() {
        return new MyViewport();
    }

    @Override
    public void onDraw(Canvas canvas) {
        final MyViewport viewport = (MyViewport) getViewport();
        final int height = canvas.getHeight();
        final int width = canvas.getWidth();

        final int startIndex = (int) viewport.getViewportX();
        final float offsetX = (float) viewport.getViewportX() - startIndex;
        final float stepX = (float) width / ((int) viewport.getViewportWidth() - 1);
        final float startX = -offsetX * stepX;
        int endIndex = startIndex + (int) viewport.getViewportWidth();
        if (offsetX != 0) endIndex++;

        double viewportHeight = viewport.getViewportHeight();
        double viewportY = viewport.getSurfaceHeight() - viewportHeight - viewport.getViewportY()
                + viewport.getHeightOffset();

        canvas.drawText("view_y=" + viewportY + " viw_height=" + viewportHeight, 20, height -  20, getPaint());

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


      /*  final int canvasHeight = canvas.getHeight();
        final int canvasWidth = canvas.getWidth();

        MyViewport viewport = (MyViewport) getViewport();
        final double viewportMinY = viewport.getViewportY() + viewport.getHeightOffset();
        final double viewportMaxY = viewport.getViewportHeight() + viewportMinY;
        final int viewportMinX = (int) viewport.getViewportX();
        final int viewportMaxX = viewportMinX + (int) viewport.getViewportWidth();
        final float viewportOffsetX = (float) viewport.getViewportX() - viewportMinX;

        final int size = getChartSeries().size();
        for (int i = 0; i < size; i++) {
            resetPaints();
            GraphSeries series = getChartSeries().get(i);
            final float step = (float) canvasWidth / ((int) viewport.getViewportWidth() - 1);
            float x = -viewportOffsetX * step;

            int left = viewportMinX;
            int right = viewportMaxX;
            if (viewportOffsetX > 0)
                right += 1;
            if (right > series.getSize())
            right = series.getSize();

            final GraphSeriesData[] values = series.getValues();
            boolean first = true;
            for (int j = left; j < right; j++, x += step) {
                double y = canvasHeight - (values[j].getValue() - viewportMinY) * canvasHeight / (viewportMaxY - viewportMinY);
                if (first) {
                    first = false;
                    getPath().moveTo(x, (float) y);
                } else {
                    getPath().lineTo(x, (float) y);
                }
            }

            getPaint().setColor(series.getStyle().color);
            getPaint().setStrokeWidth(series.getStyle().width);

            // Draw the graph line
            getPaint().setStyle(Paint.Style.STROKE);
            canvas.drawPath(getPath(), getPaint());

            if (series.getStyle().background != Color.TRANSPARENT) {
                getPaint().setColor(series.getStyle().background);
                getPaint().setStyle(Paint.Style.FILL);

                final int height = canvas.getHeight();
                getPath().lineTo(x, height);
                getPath().lineTo(0, height);
                getPath().close();
                canvas.drawPath(getPath(), getPaint());
            }
        }
*/
        // Draw some basics.
        super.onDraw(canvas);
    }

    /**
     * Called on draw series.
     *
     * @param canvas
     * @param series chart series to draw
     * @param x start x coordinate
     * @param stepX
     * @param i start index of visible values
     * @param length end index of visible values
     * @param viewportY
     * @param viewportHeight
     * @param height {@link android.graphics.Canvas#getHeight()}
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

            path.lineTo(x, height);
            path.lineTo(0, height);
            path.close();
            canvas.drawPath(path, paint);
        }
    }

    @Override
    public void notifyDataChanged() {
        super.notifyDataChanged();

        MyViewport viewport = (MyViewport) getViewport();
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

        if (index == 0) return; // there's no active (non-empty) series
        viewport.setSurfaceWidth(index);
        viewport.setSurfaceHeight(max - min);
        viewport.setViewport((int) viewport.getSurfaceWidth() / 4, (int) viewport.getSurfaceHeight() / 4);
        viewport.setHeightOffset(min);

        isDataInitialized = true;
    }

    private class MyViewport extends Viewport {

        private double mHeightOffset;

        public void setHeightOffset(double offset) {
            mHeightOffset = offset;
        }

        public double getHeightOffset() {
            return mHeightOffset;
        }

    }

}
