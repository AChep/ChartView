package com.achep.chartview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.achep.fifteenpuzzle.R;

import java.util.ArrayList;

/**
 * Created by Artem on 13.09.13.
 */
public class ChartComplexView extends RelativeLayout {

    private TextView mTitleText;
    private ChartView mChartView;

    private LinearLayout mLegendLayout;
    private RelativeLayout.LayoutParams mLegendLayoutParams;
    private boolean mLegendTouched;
    private float[] mLegendTouchOffset = new float[2];
    private boolean mLegendEnabled = true;

    public ChartComplexView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTitleText = (TextView) findViewById(R.id.title_text);
        mChartView = (ChartView) findViewById(R.id.chart_view);
        mLegendLayout = (LinearLayout) findViewById(R.id.legend_layout);
        mLegendLayoutParams = (RelativeLayout.LayoutParams) mLegendLayout.getLayoutParams();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!mLegendEnabled) return super.dispatchTouchEvent(event);

        final float x = event.getX(), y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                final float transX;
                final float transY;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    transX = mLegendLayout.getTranslationX();
                    transY = mLegendLayout.getTranslationY();
                } else {
                    transX = mLegendLayoutParams.leftMargin;
                    transY = mLegendLayoutParams.topMargin;
                }

                if ((mLegendTouched = transX < x && transX + mLegendLayout.getWidth() > x &&
                        transY < y && transY + mLegendLayout.getHeight() > y)) {
                    mLegendTouchOffset[0] = transX - x;
                    mLegendTouchOffset[1] = transY - y;
                    return true;
                }
            case MotionEvent.ACTION_MOVE:
                if (mLegendTouched) {
                    float newX = mLegendTouchOffset[0] + x;
                    int legendWidth = mLegendLayout.getWidth() + getPaddingLeft() + getPaddingRight();
                    if (newX > getWidth() - legendWidth) {
                        newX = getWidth() - legendWidth;
                    } else if (newX < 0)
                        newX = 0;

                    float newY = mLegendTouchOffset[1] + y;
                    int legendHeight = mLegendLayout.getHeight() + getPaddingTop() + getPaddingBottom();
                    if (newY > getHeight() - legendHeight) {
                        newY = getHeight() - legendHeight;
                    } else if (newY < 0)
                        newY = 0;


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        mLegendLayout.setTranslationX(newX);
                        mLegendLayout.setTranslationY(newY);
                    } else {
                        mLegendLayoutParams.leftMargin = (int) newX;
                        mLegendLayoutParams.topMargin = (int) newY;
                        mLegendLayout.setLayoutParams(mLegendLayoutParams);
                    }
                    return true;
                }
        }

        return super.dispatchTouchEvent(event);
    }

    public TextView onCreateLegendItemTitle(ChartSeries series) {
        TextView textView = new TextView(getContext());
        textView.setText(series.getTitle());
        textView.setTextColor(series.getStyle().color);
        return textView;
    }

    public void setTitleText(String titleText) {
        mTitleText.setText(titleText);
    }

    public void setLegendEnabled(boolean enabled) {
        mLegendEnabled = enabled;
        mLegendLayout.setVisibility(enabled ? VISIBLE : INVISIBLE);
    }

    // //////////////////////////////////////////
    // //////////// -- WRAPPER -- ///////////////
    // //////////////////////////////////////////

    public ChartView getChartView() {
        return mChartView;
    }

    public void notifyDataChanged() {
        mChartView.notifyDataChanged();
    }

    /**
     * @param series
     * @see #notifyDataChanged()
     */
    public void addChartSeries(ChartSeries series) {
        mChartView.addChartSeries(series);
        mLegendLayout.addView(onCreateLegendItemTitle(series));
    }

    /**
     * Remove series from the graph
     *
     * @param series series to remove
     * @return index of removed series
     */
    public int removeChartSeries(ChartSeries series) {
        int i = mChartView.removeChartSeries(series);
        mLegendLayout.removeViewAt(i);
        return i;
    }

    public void removeAllChartSeries() {
        mChartView.removeAllChartSeries();
        mLegendLayout.removeAllViews();
    }

    public ArrayList<ChartSeries> getChartSeries() {
        return mChartView.getChartSeries();
    }

}
