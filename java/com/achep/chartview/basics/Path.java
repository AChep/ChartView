package com.achep.chartview.basics;

/**
 * Created by Artem on 13.09.13.
 */
public class Path extends android.graphics.Path {

    private float mTop;
    private float mBottom;
    private boolean mEnabled;

    public void reset() {
        super.reset();
        setLimits(0, 0, false);
    }

    public void setLimits(float top, float bottom, boolean enabled) {
        mTop = top;
        mBottom = bottom;
        mEnabled = enabled;
    }

    private boolean peakTop;

    public void moveTo(float x, float y) {
        if (mEnabled) {
            super.moveTo(x, y);
        } else {
            super.moveTo(x, y);
        }
    }

    public void lineTo(float x, float y){
        if (mEnabled) {
            super.lineTo(x, y);
        } else {
            super.lineTo(x, y);
        }
    }


    /*

                if (negativePeak) {
                    negativePeak = y <= minY;
                    if (negativePeak) {
                        path.lineTo(x, minY);
                    } else {
                        float h1 = minY - yprev;
                        float h2 = y - minY;
                        float w = x - xprev;
                        float x0 = xprev + w * h1 / (h1 + h2);
                        path.lineTo(x0, minY);
                        path.lineTo(x, y);
                    }
                } else {
                    negativePeak = y <= minY;
                    if (negativePeak) {
                        float h1 = minY - y;
                        float h2 = yprev - minY;
                        float w = x - xprev;
                        float x0 = xprev + w * h2 / (h1 + h2);
                        path.lineTo(x0, minY);
                        path.lineTo(x, minY);
                    } else {
                        path.lineTo(x, y);
                    }
                }
                xprev = x;
                yprev = y;
     */
}
