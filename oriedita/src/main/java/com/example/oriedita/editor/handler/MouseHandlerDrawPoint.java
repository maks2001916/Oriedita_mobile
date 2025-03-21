package com.example.oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.canvas.MouseMode;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.elements.LineSegment;
import origami.crease_pattern.elements.Point;

@ApplicationScoped
@Handles(MouseMode.DRAW_POINT_14)
public class MouseHandlerDrawPoint extends BaseMouseHandler {
    @Inject
    public MouseHandlerDrawPoint() {
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Point p = d.getCamera().TV2object(p0);
        LineSegment mtsLineSegment = d.getFoldLineSet().closestLineSegmentSearch(p);
        LineSegment mts = new LineSegment(mtsLineSegment.getA(), mtsLineSegment.getB());//mtsは点pに最も近い線分

        if (OritaCalc.determineLineSegmentDistance(p, mts) < d.getSelectionDistance()) {
            //直線t上の点pの影の位置（点pと最も近い直線t上の位置）を求める。public Ten oc.kage_motome(Tyokusen t,Ten p){}
            //線分を含む直線を得る public Tyokusen oc.Senbun2Tyokusen(Senbun s){}
            Point pk = OritaCalc.findProjection(OritaCalc.lineSegmentToStraightLine(mts), p);//pkは点pの（線分を含む直線上の）影

            //点paが、二点p1,p2を端点とする線分に点p1と点p2で直行する、2つの線分を含む長方形内にある場合は2を返す関数	public int oc.hakononaka(Ten p1,Ten pa,Ten p2){}

            if (OritaCalc.isInside(mts.getA(), pk, mts.getB()) == 2) {
                //線分の分割-----------------------------------------
                d.getFoldLineSet().applyLineSegmentDivide(mtsLineSegment, pk);  //i番目の線分(端点aとb)を点pで分割する。i番目の線分abをapに変え、線分pbを加える。
                d.record();
            }
        }
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
    }
}
