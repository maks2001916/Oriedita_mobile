package com.example.oriedita_core.origami.crease_pattern;

import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;

import java.util.LinkedHashMap;
import java.util.Map;

public class LittleBigLittleViolation extends FlatFoldabilityViolation {
    private final LineSegment[] lineSegments;
    private final boolean[] violatingLBL;

    public LittleBigLittleViolation(Point p, LinkedHashMap<LineSegment, Boolean> littleBigLittleViolations) {
        super(p, Rule.LITTLE_BIG_LITTLE, Color.CORRECT);
        lineSegments = new LineSegment[littleBigLittleViolations.size()];
        violatingLBL = new boolean[littleBigLittleViolations.size()];

        int i = 0;
        for (Map.Entry<LineSegment, Boolean> entry : littleBigLittleViolations.entrySet()) {
            lineSegments[i] = entry.getKey();
            violatingLBL[i] = entry.getValue();
            i++;
        }
    }

    public LineSegment[] getLineSegments() {
        return lineSegments;
    }

    public boolean[] getViolatingLBL() {
        return violatingLBL;
    }
}

