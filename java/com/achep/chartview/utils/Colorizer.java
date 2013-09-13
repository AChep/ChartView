package com.achep.chartview.utils;

import android.graphics.Color;

import java.util.Random;

/**
* Created by Artem on 06.09.13.
*/
public class Colorizer {
    public static final int RED = 0;
    public static final int GREEN = 1;
    public static final int BLUE = 2;

    private static int i = 0;

    public static int getColor() {
        if (i == 3) i = 0;
        return getColor(i++);
    }

    public static int getColor(int colorType) {
        final Random random = new Random();
        if (colorType == RED)
            return Color.argb(255,
                    random.nextInt(101) + 155,
                    random.nextInt(156),
                    random.nextInt(156));

        if (colorType == GREEN)
            return Color.argb(255,
                    random.nextInt(156),
                    random.nextInt(101) + 155,
                    random.nextInt(156));

        if (colorType == BLUE)
            return Color.argb(255,
                    random.nextInt(156),
                    random.nextInt(156),
                    random.nextInt(101) + 155);

        throw new IllegalArgumentException("Passed argument \"color\" is not in range [0;2] (see constants list)!");
    }
}
