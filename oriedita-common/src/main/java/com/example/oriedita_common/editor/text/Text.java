package com.example.oriedita_common.editor.text;

import com.example.oriedita_core.origami.crease_pattern.elements.Point;

import android.graphics.Paint;
import android.graphics.Rect;
import java.io.Serializable;

public class Text implements Serializable {
    private double x, y;
    private String text;
    private static Paint paint;

    @SuppressWarnings("unused") // Used for unit test
	private Text() {
        this(0, 0, "");
    }

    public Text(double x, double y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public Text(Text t) {
        this.x = t.getX();
        this.y = t.getY();
        this.text = t.getText();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public static void setPaint(Paint paint) {
        Text.paint = paint;
    }

    public Point getPos() {
        return new Point(getX(), getY());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Rect calculateBounds() {
        if (paint == null) {
            return new Rect(0, 0, 25, 3);
        }
        int width = 0;
        String[] lines = text.split("\n");
        for (String line : lines) {
            float newWidth = paint.measureText(line);
            if (newWidth > width) {
                width = (int) newWidth;
            }
        }
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        int height = (int) (fontMetrics.bottom - fontMetrics.top);
        return new Rect(0, 0, width, height * (int) text.chars().filter(c -> c == '\n').count() + 1);
    }
}
