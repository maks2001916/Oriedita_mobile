package com.example.oriedita.editor.handler;

import org.tinylog.Logger;
import oriedita.editor.databinding.AngleSystemModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.drawing.tools.DrawingUtil;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import origami.crease_pattern.FoldLineSet;
import origami.crease_pattern.elements.LineSegment;
import origami.crease_pattern.elements.Point;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class BaseMouseHandlerLineTransform extends BaseMouseHandlerLineSelect {

    protected CanvasModel canvasModel;
    protected FoldLineSet lines;
    protected Point delta;

    private BufferedImage image;
    private boolean cacheTooBig;
    private boolean needsRerender = false;
    protected boolean active = false;
    private double lastZoomX;
    private double lastZoomY;
    private double lastAngle;
    private Point bottomLeft, topRight;

    protected BaseMouseHandlerLineTransform(CanvasModel canvasModel, AngleSystemModel angleSystemModel) {
        super(angleSystemModel);
        this.canvasModel = canvasModel;
    }

    @Override
    public void mousePressed(Point p0) {
        super.mousePressed(p0);

        delta = new Point(0, 0);
        FoldLineSet ori_s_temp = new FoldLineSet();    //セレクトされた折線だけ取り出すために使う
        Save save = SaveProvider.createInstance();
        d.getFoldLineSet().getMemoSelectOption(save, 2);
        ori_s_temp.setSave(save);
        lines = ori_s_temp;
        active = true;
        needsRerender = true;
        bottomLeft = null;
        topRight = null;
    }

    @Override
    public void mouseDragged(Point p0) {
        super.mouseDragged(p0);
        delta = new Point(
                -selectionLine.determineBX() + selectionLine.determineAX(),
                -selectionLine.determineBY() + selectionLine.determineAY()
        );
    }

    @Override
    public void mouseReleased(Point p0) {
        //  <-------20180919この行はセレクトした線の端点を選ぶと、移動とかコピー等をさせると判断するが、その操作が終わったときに必要だから追加した。

        delta = new Point(
                -selectionLine.determineBX() + selectionLine.determineAX(),
                -selectionLine.determineBY() + selectionLine.determineAY()
        );
        d.getLineStep().clear();
        active = false;
        image = null;
        cacheTooBig = false;
    }

    @Override
    public void drawPreview(Graphics2D g2, Camera camera, DrawingSettings settings) {
        super.drawPreview(g2, camera, settings);
        if (!active) {
            return;
        }
        if (lines.getTotal() < 1000) { // no need to cache with so few lines
            drawDirect(g2, camera, settings);
            return;
        }
        if (determineCameraChanged(camera)) {
            cacheTooBig = false;
            needsRerender = true;
        }
        lastZoomX = camera.getCameraZoomX();
        lastZoomY = camera.getCameraZoomY();
        lastAngle = camera.getCameraAngle();

        if (needsRerender && lines != null && !cacheTooBig) {
            initCacheImage(g2, camera, settings);
            if (image != null) { // image won't be created if it would be too big
                rerender(camera, settings);
                needsRerender = false;
            }
        }

        if (image != null) {
            Point origin = camera.object2TV(new Point(0, 0));
            Point deltaTransformed = camera.object2TV(delta);
            Logger.info(delta);
            g2.drawImage(image,
                    (int) (bottomLeft.getX() + deltaTransformed.getX() - origin.getX()),
                    (int) (bottomLeft.getY() + deltaTransformed.getY() - origin.getY()),
                    image.getWidth(), image.getHeight(), null);
        }
        if (image == null && lines != null) {
            drawDirect(g2, camera, settings);
        }
    }

    private void initCacheImage(Graphics2D g2, Camera camera, DrawingSettings settings) {
        if (bottomLeft == null) {
            double minX = lines.getMinX();
            double maxX = lines.getMaxX();
            double minY = lines.getMinY();
            double maxY = lines.getMaxY();

            bottomLeft = camera.object2TV(new Point(minX, minY));
            topRight = camera.object2TV(new Point(maxX, maxY));
        }

        int width = (int) (topRight.getX() - bottomLeft.getX()) + 3;
        int height = (int) (topRight.getY() - bottomLeft.getY()) + 3;
        image = null;
        if (width * height < settings.getWidth() * settings.getHeight() * 1.5) {
            image = g2.getDeviceConfiguration().createCompatibleImage(width, height, BufferedImage.BITMASK);
            bottomLeft = bottomLeft.move(new Point(-1, -1));
        }
    }

    protected void drawDirect(Graphics2D g2, Camera camera, DrawingSettings settings) {
        Point origin = camera.object2TV(new Point(0, 0));
        Point deltaTransformed = camera.object2TV(delta);
        int minx = (int) -(deltaTransformed.getX() - origin.getX());
        int miny = (int) -(deltaTransformed.getY() - origin.getY());
        int maxx = minx + settings.getWidth();
        int maxy = miny + settings.getHeight();

        // do cohen-sutherland clipping
        for (var s : lines.getLineSegmentsIterable()) {
            Point a = camera.object2TV(s.getA());
            Point b = camera.object2TV(s.getB());
            int regionA = DrawingUtil.cohenSutherlandRegion(minx, miny, maxx, maxy, a);
            int regionB = DrawingUtil.cohenSutherlandRegion(minx, miny, maxx, maxy, b);
            if ((regionA & regionB) == DrawingUtil.CENTER) {
                Point pa = s.getA().move(delta);
                Point pb = s.getB().move(delta);
                LineSegment s2 = new LineSegment(pa, pb);
                DrawingUtil.drawCpLine(g2, s2, camera, settings.getLineStyle(), settings.getLineWidth(),
                        d.getPointSize(), settings.getWidth(), settings.getHeight(), settings.useRoundedEnds());
            }
        }
    }

    protected void rerender(Camera camera, DrawingSettings settings) {
        Point zero = camera.TV2object(new Point(0, 0));
        Point boObject = camera.TV2object(bottomLeft);
        FoldLineSet ori_s_temp = new FoldLineSet();
        ori_s_temp.set(lines);
        ori_s_temp.move(zero.getX() - boObject.getX(), zero.getY() - boObject.getY());
        Graphics2D g = image.createGraphics();
        g.setBackground(new Color(0f, 0, 0, 0));
        for (var ls : ori_s_temp.getLineSegmentsIterable()) {
            DrawingUtil.drawCpLine(g, ls, camera, settings.getLineStyle(),
                    settings.getLineWidth(), d.getPointSize(), image.getWidth(), image.getHeight(), settings.useRoundedEnds());
        }
    }

    private boolean determineCameraChanged(Camera camera) {
        return camera.getCameraZoomX() != lastZoomX || camera.getCameraZoomY() != lastZoomY || camera.getCameraAngle() != lastAngle;
    }

    @Override
    public void reset() {
        super.reset();
        image = null;
        needsRerender = false;
        bottomLeft = null;
        cacheTooBig = false;
        active = false;
        lastAngle = 100000;
        lastZoomX = 0;
        lastZoomY = 0;
    }
}
