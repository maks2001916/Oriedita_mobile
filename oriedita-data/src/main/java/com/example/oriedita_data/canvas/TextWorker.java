package com.example.oriedita_data.canvas;


import com.example.oriedita_common.editor.drawing.tools.Camera;
import com.example.oriedita_data.save.TextSave;
import com.example.oriedita_common.editor.text.Text;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows displaying text on the canvas
 */
public class TextWorker {
    private final List<Text> texts;
    private final Paint textPaint;

    public TextWorker() {
        this.texts = new ArrayList<>();
        this.textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(16f);
        textPaint.setTypeface(Typeface.DEFAULT);
    }

    public void draw(Canvas canvas, Camera camera) {
        Text.setPaint(textPaint);
        for (Text text : texts) {
            Point textPos = camera.object2TV(text.getPos());
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float height = fontMetrics.bottom - fontMetrics.top;
            float textY = (float) textPos.getY();

            for (String line : text.getText().split("\n")) {
                canvas.drawText(line, (float) textPos.getX(), textY, textPaint);
                textY += height;
            }
        }
    }

    public void getSave(TextSave save) {
        texts.forEach(t -> save.addText(new Text(t)));
    }

    public void setSave(TextSave memo1) {
        texts.clear();
        texts.addAll(memo1.getTexts());
    }

    public void addText(Text text) {
        this.texts.add(text);
    }

    public List<Text> getTexts() {
        return this.texts;
    }

    public void removeText(Text text) {
        this.getTexts().remove(text);
    }

    /**
     * Deletes all texts whose bounding boxes are (at least partially) contained inside the Rectangle spanned by pa and pb.
     *
     * @param pa     one corner of the deletion box (in canvas coordinates)
     * @param pb     opposite corner to pa of the deletion box (in canvas coordinates)
     * @param camera current camera, to account for zoom level
     * @return true if any text was deleted, false otherwise
     */
    public boolean deleteInsideRectangle(Point pa, Point pb, Camera camera) {
        boolean changed = false;

        // Text does not rotate with the camera, so we can always assume both the bounding boxes of the text, and the
        // Selection box are rectangles whose sides are parallel to the x/y axes
        if (pa.getX() > pb.getX()) {
            double tmp = pa.getX();
            pa = pa.withX(pb.getX());
            pb = pb.withX(tmp);
        }
        if (pa.getY() > pb.getY()) {
            double tmp = pa.getY();
            pa = pa.withY(pb.getY());
            pb = pb.withY(tmp);
        }
        List<Text> toRemove = new ArrayList<>();

        for (Text text : texts) {
            Rect r = text.calculateBounds();
            Point p1 = camera.object2TV(text.getPos());
            r.offset((int) p1.getX(), (int) p1.getY());
            Rect selection = new Rect((int) pa.getX(), (int) pa.getY(), (int) pb.getX(), (int) pb.getY());

            if (Rect.intersects(selection, r)) {
                changed = true;
                toRemove.add(text);
            }
        }
        texts.removeAll(toRemove);

        return changed;
    }
}
