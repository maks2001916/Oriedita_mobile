package com.example.oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import origami.Epsilon;
import origami.crease_pattern.elements.Circle;
import origami.crease_pattern.elements.LineColor;
import origami.crease_pattern.elements.LineSegment;
import origami.crease_pattern.elements.Point;

import java.awt.Graphics2D;

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_42)
public class MouseHandlerCircleDraw extends BaseMouseHandler {
    private Circle previewCircle;
    private LineSegment previewLine;

    @Inject
    public MouseHandlerCircleDraw() {
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==42 円入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        Point closestPoint = d.getClosestPoint(p);
        d.getCircleStep().clear();
        d.getLineStep().clear();
        if (p.distance(closestPoint) <= d.getSelectionDistance()) {
            this.previewCircle = new Circle(closestPoint, 0, LineColor.CYAN_3);
            this.previewLine = new LineSegment(p, closestPoint, LineColor.CYAN_3);
        }
    }

    //マウス操作(mouseMode==42 円入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        if (previewLine != null) {
            previewLine = previewLine.withA(p);
            if (previewCircle != null) {
                previewCircle.setR(previewLine.determineLength());
            }
        }
    }

    //マウス操作(mouseMode==42 円入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (previewLine != null) {
            Point p = d.getCamera().TV2object(p0);
            Point closestPoint = d.getClosestPoint(p);
            previewLine = previewLine.withA(closestPoint);
            if (p.distance(closestPoint) <= d.getSelectionDistance()) {
                if (Epsilon.high.gt0(previewLine.determineLength())) {
                    d.addCircle(previewLine.determineBX(), previewLine.determineBY(), previewLine.determineLength(), LineColor.CYAN_3);
                    d.record();
                }
            }

            previewLine = null;
            previewCircle = null;
        }
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        if (previewCircle != null) {
            DrawingUtil.drawCircleStep(g2, previewCircle, camera);
        }
        if (previewLine != null) {
            DrawingUtil.drawLineStep(g2, previewLine, camera, settings.getLineWidth(), d.getGridInputAssist());
        }
    }
}
