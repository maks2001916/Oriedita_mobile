package com.example.oriedita_core.origami.crease_pattern.worker;

import android.util.Log;
import com.example.oriedita_core.origami.crease_pattern.LineSegmentSet;
import com.example.oriedita_core.origami.crease_pattern.worker.linesegmentset.IntersectDivide;
import com.example.oriedita_core.origami.crease_pattern.worker.linesegmentset.OverlappingLineRemoval;

public class LineSegmentSetWorker {
    LineSegmentSet lineSegmentSet = new LineSegmentSet();    //Instantiation of basic branch structure

    public void overlapping_line_removal() {
        OverlappingLineRemoval.apply(lineSegmentSet);
    }

    public void intersect_divide() throws InterruptedException {
        IntersectDivide.apply(lineSegmentSet);
    }

    public void reset() {
        lineSegmentSet.reset();
    }

    public void set(LineSegmentSet ss) {
        lineSegmentSet.set(ss);
    }

    public LineSegmentSet get() {
        return lineSegmentSet;
    }

    public LineSegmentSet split_arrangement_for_SubFace_generation() throws InterruptedException {
        Log.i("TAG","　　Senbunsyuugouの中で、Smenを発生させるための線分集合の整理");
        Log.i("TAG","Divide and organize　1. Before deleting points	getNumLineSegments() = " + lineSegmentSet.getNumLineSegments());
        lineSegmentSet.point_removal();          //Just in case, remove the dotted line segment
        Log.i("TAG","Divide and organize　2. Before deleting overlapping line segments	getNumLineSegments() = " + lineSegmentSet.getNumLineSegments());
        overlapping_line_removal();//念のため、全く一致する線分が２つあれば１つを除く
        Log.i("TAG","Divide and organize　3. Before intersection division	getNumLineSegments() = " + lineSegmentSet.getNumLineSegments());
        intersect_divide();
        Log.i("TAG","Divide and organize　4. Before deleting points	getNumLineSegments() = " + lineSegmentSet.getNumLineSegments());
        lineSegmentSet.point_removal();             //折り畳み推定の針金図の整理のため、点状の線分を除く
        Log.i("TAG","Divide and organize　5. Before deleting overlapping line segments	getNumLineSegments() = " + lineSegmentSet.getNumLineSegments());
        overlapping_line_removal(); //折り畳み推定の針金図の整理のため、全く一致する線分が２つあれば１つを除く
        Log.i("TAG","Divide and organize　5, After deleting overlapping line segments	getNumLineSegments() = " + lineSegmentSet.getNumLineSegments());

        return lineSegmentSet;
    }//k is a set of line segments, LineSegmentSet k = new LineSegmentSet ();
}

