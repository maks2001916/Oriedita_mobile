package com.example.oriedita_core.origami.crease_pattern.worker.linesegmentset;

import com.example.oriedita_core.origami.Epsilon;
import com.example.oriedita_core.origami.crease_pattern.LineSegmentSet;
import com.example.oriedita_core.origami.crease_pattern.OritaCalc;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;
import com.example.oriedita_core.origami.data.quadTree.QuadTree;
import com.example.oriedita_core.origami.data.quadTree.adapter.LineSegmentEndPointAdapter;
import com.example.oriedita_core.origami.data.quadTree.collector.EndPointCollector;
import com.example.oriedita_core.origami.data.quadTree.collector.QuadTreeCollector;

import java.util.ArrayList;
import java.util.List;

public class OverlappingLineRemoval {
    public static void apply(LineSegmentSet input) {
        overlapping_line_removal(input, Epsilon.UNKNOWN_001);
    }

    public static void overlapping_line_removal(LineSegmentSet lineSegmentSet, double r) {
        QuadTree qtA = new QuadTree(new LineSegmentEndPointAdapter(lineSegmentSet, LineSegmentSet::getA));
        QuadTree qtB = new QuadTree(new LineSegmentEndPointAdapter(lineSegmentSet, LineSegmentSet::getB));

        boolean[] removal_flg = new boolean[lineSegmentSet.getNumLineSegments()];
        List<LineSegment> snew = new ArrayList<>();

        for (int i = 0; i < lineSegmentSet.getNumLineSegments(); i++) {
            LineSegment si = lineSegmentSet.get(i);
            Point p1 = si.getA();
            Point p2 = si.getB();
            QuadTreeCollector c = new EndPointCollector(p1, i);
            for (int j : qtA.collect(c)) {
                LineSegment sj = lineSegmentSet.get(j);
                Point p3 = sj.getA();
                Point p4 = sj.getB();
                if (OritaCalc.equal(p1, p3, r) && OritaCalc.equal(p2, p4, r)) {
                    removal_flg[j] = true;
                }
            }
            for (int j : qtB.collect(c)) {
                LineSegment sj = lineSegmentSet.get(j);
                Point p3 = sj.getA();
                Point p4 = sj.getB();
                if (OritaCalc.equal(p1, p4, r) && OritaCalc.equal(p2, p3, r)) {
                    removal_flg[j] = true;
                }
            }
        }

        for (int i = 0; i < lineSegmentSet.getNumLineSegments(); i++) {
            if (!removal_flg[i]) {
                snew.add(lineSegmentSet.get(i));
            }
        }

        lineSegmentSet.reset(snew.size());

        for (int i = 0; i < snew.size(); i++) {
            lineSegmentSet.set(i, new LineSegment(snew.get(i)));
        }
    }
}

