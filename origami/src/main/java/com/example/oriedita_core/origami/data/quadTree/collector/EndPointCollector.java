package com.example.oriedita_core.origami.crease_pattern.data.quadTree.collector;

import com.example.oriedita_core.origami.crease_pattern.elements.Point;
import com.example.oriedita_core.origami.crease_pattern.data.quadTree.adapter.QuadTreeAdapter;

public class EndPointCollector extends PointCollector {

    private final int min;

    public EndPointCollector(Point p, int min) {
        super(p);
        this.min = min;
    }

    @Override
    public boolean shouldCollect(int cursor, QuadTreeAdapter adapter) {
        return super.shouldCollect(cursor, adapter) && cursor > min;
    }
}
