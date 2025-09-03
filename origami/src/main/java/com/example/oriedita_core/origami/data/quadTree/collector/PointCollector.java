package com.example.oriedita_core.origami.data.quadTree.collector;

import com.example.oriedita_core.origami.crease_pattern.elements.Point;
import com.example.oriedita_core.origami.data.quadTree.QuadTree.Node;
import com.example.oriedita_core.origami.data.quadTree.adapter.QuadTreeAdapter;
import com.example.oriedita_core.origami.data.quadTree.collector.*;

/**
 * Get all items that might contains the given point.
 */
public class PointCollector extends RecursiveCollector {

    private final Point p;

    public PointCollector(Point p) {
        this.p = p;
    }

    @Override
    public boolean shouldGoDown() {
        return false;
    }

    @Override
    public boolean shouldCollect(int cursor, QuadTreeAdapter adapter) {
        return adapter.getItem(cursor).mightContain(p);
    }

    @Override
    public boolean contains(Node node) {
        return node.contains(p);
    }
}

