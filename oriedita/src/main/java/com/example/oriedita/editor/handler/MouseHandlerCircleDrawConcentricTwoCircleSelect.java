package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.elements.Circle;
import origami.crease_pattern.elements.LineColor;
import origami.crease_pattern.elements.Point;

@ApplicationScoped
@Handles(MouseMode.CIRCLE_DRAW_CONCENTRIC_TWO_CIRCLE_SELECT_50)
public class MouseHandlerCircleDrawConcentricTwoCircleSelect extends BaseMouseHandler {
    @Inject
    public MouseHandlerCircleDrawConcentricTwoCircleSelect() {
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);

        Circle closest_circumference = new Circle(); //Circle with the circumference closest to the mouse
        closest_circumference.set(d.getClosestCircleMidpoint(p));
        // Point closest_point = d.getClosestPoint(p);

        if ((d.getLineStep().size() == 0) && (d.getCircleStep().size() == 0)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.getSelectionDistance()) {
                return;
            }

            d.getLineStep().clear();
            d.getCircleStep().add(new Circle(closest_circumference.determineCenter(), closest_circumference.getR(), LineColor.GREEN_6));
        } else if ((d.getLineStep().size() == 0) && (d.getCircleStep().size() == 1)) {
            if (OritaCalc.distance_circumference(p, closest_circumference) > d.getSelectionDistance()) {
                return;
            }

            d.getLineStep().clear();
            d.getCircleStep().add(new Circle(closest_circumference.determineCenter(), closest_circumference.getR(), LineColor.GREEN_6));
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if ((d.getLineStep().size() == 0) && (d.getCircleStep().size() == 2)) {
            Circle circle1 = d.getCircleStep().get(0);
            Circle circle2 = d.getCircleStep().get(1);
            d.getCircleStep().clear();
            double add_r = (OritaCalc.distance(circle1.determineCenter(), circle2.determineCenter()) - circle1.getR() - circle2.getR()) / 2;

            if (!Epsilon.high.eq0(add_r)) {
                double new_r1 = add_r + circle1.getR();
                double new_r2 = add_r + circle2.getR();

                if (Epsilon.high.gt0(new_r1) && Epsilon.high.gt0(new_r2)) {
                    circle1.setR(new_r1);
                    circle1.setColor(LineColor.CYAN_3);
                    d.addCircle(circle1);
                    circle2.setR(new_r2);
                    circle2.setColor(LineColor.CYAN_3);
                    d.addCircle(circle2);
                    d.record();
                }
            }
        }

    }
}
