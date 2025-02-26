package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.elements.Circle;
import origami.crease_pattern.elements.LineColor;
import origami.crease_pattern.elements.LineSegment;
import origami.crease_pattern.elements.Point;

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_FREE_47)
public class MouseHandlerCircleDrawFree extends BaseMouseHandler {
    @Inject
    public MouseHandlerCircleDrawFree() {
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(mouseMode==47 円入力(フリー　)　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        d.getCircleStep().clear();
        d.getLineStep().clear();

        Point p = d.getCamera().TV2object(p0);
        Point closestPoint = d.getClosestPoint(p);
        if (p.distance(closestPoint) > d.getSelectionDistance()) {
            d.lineStepAdd(new LineSegment(p, p, LineColor.CYAN_3));

            Circle stepCircle = new Circle();
            stepCircle.set(p, 0.0, LineColor.CYAN_3);
            d.getCircleStep().add(stepCircle);
        } else {
            d.lineStepAdd(new LineSegment(p, closestPoint, LineColor.CYAN_3));

            Circle stepCircle = new Circle();
            stepCircle.set(closestPoint, 0.0, LineColor.CYAN_3);
            d.getCircleStep().add(stepCircle);
        }
    }

    //マウス操作(mouseMode==47 円入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        d.getLineStep().set(0, d.getLineStep().get(0).withA(p));
        d.getCircleStep().get(0).setR(OritaCalc.distance(d.getLineStep().get(0).getA(), d.getLineStep().get(0).getB()));
    }

    //マウス操作(mouseMode==47 円入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 1) {

            Point p = d.getCamera().TV2object(p0);
            Point closestPoint = d.getClosestPoint(p);

            if (p.distance(closestPoint) <= d.getSelectionDistance()) {
                d.getLineStep().set(0, d.getLineStep().get(0).withA(closestPoint));
            } else {
                d.getLineStep().set(0, d.getLineStep().get(0).withA(p));
            }

            if (Epsilon.high.gt0(d.getLineStep().get(0).determineLength())) {
                d.addCircle(d.getLineStep().get(0).determineBX(), d.getLineStep().get(0).determineBY(), d.getLineStep().get(0).determineLength(), LineColor.CYAN_3);
                d.record();
            }

            d.getCircleStep().clear();
            d.getLineStep().clear();
        }
    }
}
