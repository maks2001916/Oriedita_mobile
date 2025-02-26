package com.example.oriedita_core.origami.crease_pattern.worker.foldlineset;

import com.example.oriedita_core.origami.crease_pattern.Epsilon;
import com.example.oriedita_core.origami.crease_pattern.FoldLineSet;
import com.example.oriedita_core.origami.crease_pattern.OritaCalc;
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;

public class Check2 {
    public static void apply(FoldLineSet foldLineSet) {
        foldLineSet.getCheck2LineSegment().clear();

        foldLineSet.unselect_all();
        for (var si : foldLineSet.getLineSegmentsIterable()) {
            for (var sj : foldLineSet.getLineSegmentsIterable()) {
                if (sj == si) break;
                if (si.getColor() == LineColor.CYAN_3) continue;
                if (sj.getColor() == LineColor.CYAN_3) continue;

                LineSegment si1 = new LineSegment(si);
                LineSegment sj1 = new LineSegment(sj);

                //T-shaped intersection
                LineSegment.Intersection intersection = OritaCalc.determineLineSegmentIntersectionSweet(si, sj, Epsilon.UNKNOWN_0001, Epsilon.PARALLEL_FOR_FIX);
                switch (intersection) {
                    case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25:
                    case INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26:
                    case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27:
                    case INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28:
                        foldLineSet.getCheck2LineSegment().add(si1);
                        foldLineSet.getCheck2LineSegment().add(sj1);   /* set_select(i,2);set_select(j,2); */
                        break;
                    default:
                        break;
                }
            }
        }
    }
}

