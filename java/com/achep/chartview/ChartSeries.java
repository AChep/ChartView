package com.achep.chartview;

import android.graphics.Color;

import com.achep.chartview.utils.Colorizer;

import java.util.LinkedList;

/**
 * Created by Artem on 08.09.13.
 */
public class ChartSeries {

    private LinkedList<Double> mDatas;
    private int mMaxSize;

    private double mMax = Double.MIN_VALUE;
    private double mMin = Double.MAX_VALUE;

    private final Style mStyle;
    private final String mTitle;

    public ChartSeries(Style style, String title, int maxSize) {
        mStyle = style;
        mTitle = title;

        mDatas = new LinkedList<Double>();
        mMaxSize = maxSize;
    }

    public boolean isEmpty() {
        return mDatas.isEmpty();
    }

    public void removeValue(int i) {
        mDatas.remove(i);
    }

    public void appendValue(Double value) {
        if (mDatas.size() == mMaxSize) {
            Double d = mDatas.pollFirst();
            if (d == mMax || d == mMin) {
                final int size = mDatas.size();
                for (int i = 0; i < size; i++) {
                    d = mDatas.get(i);
                    if (d > mMax) mMax = d;
                    if (d < mMin) mMin = d;
                }
            }
        }

        mDatas.addLast(value);
        if (value > mMax) mMax = value;
        if (value < mMin) mMin = value;
        //LogUtils.d(this + "]:" + value.doubleValue() + " max=" + mMax + " min=" + mMin);
    }

    public Double get(int i) {
        return mDatas.get(i);
    }

    /**
     * Get maximum value of this series.
     *
     * @return maximum value of this series.
     */
    public double getMax() {
        return mMax;
    }

    /**
     * Get minimum value of this series.
     *
     * @return minimum value of this series.
     */
    public double getMin() {
        return mMin;
    }

    public int size() {
        return mDatas.size();
    }

    /**
     * The style of chart series.
     */
    public static class Style {

        public static final int DEFAULT_WIDTH = 2; // px.

        public float strokeWidth;
        public int backgroundColor;
        public int color;

        /**
         * Defines the style of {@link com.achep.graphs.ChartSeries} (the color is picked by {@link com.achep.graphs.Colorizer}).
         *
         * @see #Style(int, float)
         * @see #Style(int, float, int)
         */
        public Style() {
            this(Colorizer.getColor(), DEFAULT_WIDTH, Color.TRANSPARENT);
        }

        /**
         * Defines the style of {@link com.achep.graphs.ChartSeries}.
         *
         * @param color       stroke color
         * @param strokeWidth stroke width (px.)
         * @see #Style()
         * @see #Style(int, float, int)
         */
        public Style(int color, float strokeWidth) {
            this(color, strokeWidth, Color.TRANSPARENT);
        }

        public Style(int color, float strokeWidth, int backgroundColor) {
            this.color = color;
            this.strokeWidth = strokeWidth;
            this.backgroundColor = backgroundColor;
        }
    }

    public Style getStyle() {
        return mStyle;
    }

    public String getTitle() {
        return mTitle;
    }

}
