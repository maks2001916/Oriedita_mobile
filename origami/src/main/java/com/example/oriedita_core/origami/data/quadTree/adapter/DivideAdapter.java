package com.example.oriedita_core.origami.crease_pattern.data.quadTree.adapter;

import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;

import java.util.List;

public class DivideAdapter extends LineSegmentListAdapter {

    private final int max;

    public DivideAdapter(List<LineSegment> lineSegments, int max) {
        super(lineSegments);
        this.max = max;
    }

    @Override
    public int getCount() {
        return max;
    }
}

