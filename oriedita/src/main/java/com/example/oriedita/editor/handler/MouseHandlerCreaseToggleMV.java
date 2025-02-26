package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.elements.LineColor;
import origami.crease_pattern.elements.LineSegment;
import origami.crease_pattern.elements.Point;

@ApplicationScoped
@Handles(MouseMode.CREASE_TOGGLE_MV_58)
public class MouseHandlerCreaseToggleMV extends BaseMouseHandlerBoxSelect {
    @Inject
    public MouseHandlerCreaseToggleMV() {
    }

    /**
     * マウス操作(mouseMode==58線_変換 でボタンを離したとき)を行う関数
     * <p>
     * Toggling M/V creases cannot possibly create new T-intersections, so we dont' need {@link CreasePattern_Worker#fix2()} here.
     */
    public void mouseReleased(Point p0) {
        super.mouseReleased(p0);
        d.getLineStep().clear();

        if (selectionStart.distance(p0) > Epsilon.UNKNOWN_1EN6) {//
            if (d.MV_change(selectionStart, p0) != 0) {
                d.record();
            }
        }

        if (selectionStart.distance(p0) <= Epsilon.UNKNOWN_1EN6) {//
            Point p = d.getCamera().TV2object(p0);
            if (d.getFoldLineSet().closestLineSegmentDistance(p) < d.getSelectionDistance()) {//点pに最も近い線分の番号での、その距離を返す	public double mottomo_tikai_senbun_kyori(Ten p)
                LineSegment s = d.getFoldLineSet().closestLineSegmentSearch(p);
                LineColor ic_temp = s.getColor();
                if (ic_temp == LineColor.RED_1) {
                    s.setColor(LineColor.BLUE_2);
                } else if (ic_temp == LineColor.BLUE_2) {
                    s.setColor(LineColor.RED_1);
                }

                d.record();
            }

        }
    }
}
