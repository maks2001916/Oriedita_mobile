package com.example.oriedita_data;

import android.graphics.Color;
import java.util.HashMap;
import java.util.Map;

public class Colors {
    public static final int GRID_LINE_DARK = Color.rgb(50, 50, 50);
    public static final int GRID_SCALE_DARK = Color.rgb(17, 75, 10);

    public static final int GRID_LINE = Color.rgb(230, 230, 230);
    public static final int GRID_SCALE = Color.rgb(180, 200, 180);

    public static final int FIGURE_FRONT_DARK = Color.rgb(129, 15, 94);
    public static final int FIGURE_BACK_DARK = Color.rgb(89, 89, 89);

    public static final int FIGURE_FRONT = Color.rgb(255, 255, 50);
    public static final int FIGURE_BACK = Color.rgb(233, 233, 233);

    public static final int INVALID_INPUT = Color.rgb(255, 153, 153);
    public static final int INVALID_INPUT_DARK = Color.rgb(160, 60, 80);

    private static final Map<Integer, Integer> colorMap;
    private static final Map<Integer, Integer> darkColorMap;

    static {
        colorMap = new HashMap<>();
        darkColorMap = new HashMap<>();

        add(Color.BLACK, Color.BLACK, Color.rgb(210, 210, 210));
        add(Color.WHITE, Color.WHITE, Color.rgb(37, 37, 37));
        add(Color.RED, Color.RED, Color.rgb(229, 115, 115));
        add(Color.BLUE, Color.BLUE, Color.rgb(33, 150, 243));
        add(Color.CYAN, Color.CYAN, Color.rgb(0, 100, 100));
        add(Color.MAGENTA, Color.MAGENTA, Color.rgb(100, 0, 100));
        add(Color.GREEN, Color.GREEN, Color.rgb(0, 100, 0));
        add(Color.rgb(150, 150, 150), Color.rgb(150, 150, 150), Color.rgb(50, 50, 50));
        add(Color.argb(0, 0, 75, 255), Color.argb(0, 0, 75, 255), Color.argb(0, 0, 75, 255));
        add(Color.rgb(230, 230, 230), Color.rgb(230, 230, 230), Color.rgb(54, 54, 54));
        add(Color.rgb(162, 162, 162), Color.rgb(162, 162, 162), Color.rgb(120, 120, 120)); //placeholder
        add(Color.GRAY, Color.argb(128,128,128,128), Color.argb(128,128,128,128));
        add(Color.YELLOW, Color.MAGENTA, Color.YELLOW);
        add(INVALID_INPUT, INVALID_INPUT, INVALID_INPUT_DARK);
    }

    private static Map<Integer, Integer> activeColorMap = colorMap;

    private static void add(int color, int lightColor, int darkColor) {
        colorMap.put(color, lightColor);
        darkColorMap.put(color, darkColor);
    }

    public static int get(int color) {
        return activeColorMap.getOrDefault(color, color);
    }

    public static void update(boolean isDark) {
        if (isDark) {
            activeColorMap = darkColorMap;
        } else {
            activeColorMap = colorMap;
        }
    }

    /**
     * Reverse lookup of colors. Find key in darkColorMap when going from light to dark, find value in darkColormap when going from dark to light.
     *
     * @param color  Current color
     * @param isDark The desired mode for this color to be in.
     * @return Restored color
     */
    public static int restore(int color, boolean isDark) {
        if (color == Color.TRANSPARENT) {
            return Color.TRANSPARENT;
        }

        if (isDark) {
            return darkColorMap.getOrDefault(color, color);
        }

        for (Map.Entry<Integer, Integer> entry : darkColorMap.entrySet()) {
            if (entry.getValue() == color) {
                return entry.getKey();
            }
        }
        return color;
    }
}
