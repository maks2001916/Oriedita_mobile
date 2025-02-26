package com.example.oriedita_core.origami.crease_pattern.worker.foldlineset;

import com.example.oriedita_core.origami.crease_pattern.FoldLineSet;
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Polygon;

public class InsideToAux {
    public static boolean apply(FoldLineSet foldLineSet, Polygon b) {
        boolean i_r = false;

        int numChangedLines = 0;
        for (int i = 1; i <= foldLineSet.getTotal(); i++) {
            LineSegment s = foldLineSet.get(i);
            if (s.getColor().isFoldingLine()) {
                if (b.totu_boundary_inside(s)) {
                    numChangedLines = numChangedLines + 1;

                    LineSegment add_sen = new LineSegment(s);
                    add_sen.setColor(LineColor.CYAN_3);

                    foldLineSet.deleteLine(i);
                    foldLineSet.addLine(add_sen);
                    i = i - 1;

                    i_r = true;
                }
            }
        }
        if (numChangedLines >= 1) {
            foldLineSet.divideLineSegmentWithNewLines(foldLineSet.getTotal() - numChangedLines, foldLineSet.getTotal());
        }
        //上２行の場合わけが必要な理由は、kousabunkatu()をやってしまうと折線と補助活線との交点で折線が分割されるから。kousabunkatu(1,sousuu-numChangedLines,sousuu-numChangedLines+1,sousuu)だと折線は分割されない。

        return i_r;
    }
}

