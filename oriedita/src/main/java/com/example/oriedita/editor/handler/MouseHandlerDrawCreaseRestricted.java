package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.elements.LineSegment;
import origami.crease_pattern.elements.Point;

@ApplicationScoped
@Handles(MouseMode.DRAW_CREASE_RESTRICTED_11)
public class MouseHandlerDrawCreaseRestricted extends BaseMouseHandlerInputRestricted {
    @Inject
    public MouseHandlerDrawCreaseRestricted() {
    }

    //マウス操作(mouseMode==11線分入力　でボタンを押したとき)時の作業----------------------------------------------------
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        Point closest_point = d.getClosestPoint(p);
        if (p.distance(closest_point) > d.getSelectionDistance()) {
            return;
        }
        LineSegment s = new LineSegment(p, closest_point, d.getLineColor(), LineSegment.ActiveState.ACTIVE_B_2);

        d.lineStepAdd(s);
    }

    //マウス操作(mouseMode==11線分入力　でドラッグしたとき)を行う関数----------------------------------------------------
    public void mouseDragged(Point p0) {
        //近い既存点のみ表示

        if (d.getLineStep().size() == 0) {
            return;
        }

        Point p = d.getCamera().TV2object(p0);
        d.getLineStep().set(0, d.getLineStep().get(0).withA(p));
        d.getLineStep().get(0).setColor(d.getLineColor());

        if (d.getGridInputAssist()) {
            d.getLineCandidate().clear();

            Point closestPoint = d.getClosestPoint(p);
            if (p.distance(closestPoint) < d.getSelectionDistance()) {
                d.getLineCandidate().add(new LineSegment(closestPoint, closestPoint, d.getLineColor()));
                d.getLineStep().set(0, d.getLineStep().get(0).withA(d.getLineCandidate().get(0).getA()));
            }
        }
    }//近い既存点のみ表示

    //マウス操作(mouseMode==11線分入力　でボタンを離したとき)を行う関数----------------------------------------------------
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 1) {
            Point p = d.getCamera().TV2object(p0);
            Point closestPoint = d.getClosestPoint(p);
            d.getLineStep().set(0, d.getLineStep().get(0).withA(closestPoint));
            if (p.distance(closestPoint) <= d.getSelectionDistance()) {
                if (Epsilon.high.gt0(d.getLineStep().get(0).determineLength())) {
                    d.addLineSegment(d.getLineStep().get(0));
                    d.record();
                }
            }

            d.getLineStep().clear();
        }
    }
}
