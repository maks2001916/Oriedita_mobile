package com.example.oriedita_core.origami.crease_pattern.worker.foldlineset;

import com.example.oriedita_core.origami.crease_pattern.Epsilon;
import com.example.oriedita_core.origami.crease_pattern.FoldLineSet;
import com.example.oriedita_core.origami.crease_pattern.OritaCalc;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;

public class BranchTrim {
    public static void apply(FoldLineSet foldLineSet) {
        double r = Epsilon.UNKNOWN_1EN6;
        boolean iflga;
        boolean iflgb;
        for (int i = 1; i <= foldLineSet.getTotal(); i++) {
            iflga = false;
            iflgb = false;
            LineSegment si;
            si = foldLineSet.get(i);
            for (int j = 1; j <= foldLineSet.getTotal(); j++) {
                if (i != j) {
                    LineSegment sj;
                    sj = foldLineSet.get(j);
                    if (OritaCalc.distance(si.getA(), sj.getA()) < r) {
                        iflga = true;
                    }
                    if (OritaCalc.distance(si.getA(), sj.getB()) < r) {
                        iflga = true;
                    }
                    if (OritaCalc.distance(si.getB(), sj.getA()) < r) {
                        iflgb = true;
                    }
                    if (OritaCalc.distance(si.getB(), sj.getB()) < r) {
                        iflgb = true;
                    }
                }
            }

            if (!iflga || !iflgb) {
                deleteLineSegment_vertex(foldLineSet, i);
                i = 1;
            }
        }
    }

    public static void deleteLineSegment_vertex(FoldLineSet foldLineSet, int i) {//When erasing the i-th fold line, if the end point of the fold line can also be erased, erase it.
        LineSegment s = foldLineSet.get(i);

        Point pa = s.getA();
        Point pb = s.getB();
        foldLineSet.deleteLine(i);

        foldLineSet.del_V(pa, Epsilon.UNKNOWN_1EN6, Epsilon.UNKNOWN_1EN6);
        foldLineSet.del_V(pb, Epsilon.UNKNOWN_1EN6, Epsilon.UNKNOWN_1EN6);
    }
}

