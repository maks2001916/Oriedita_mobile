package com.example.oriedita_core.origami.data.quadTree.collector;

import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;
import com.example.oriedita_core.origami.data.quadTree.QuadTree.Node;
import com.example.oriedita_core.origami.data.quadTree.QuadTreeItem;
import com.example.oriedita_core.origami.data.quadTree.adapter.QuadTreeAdapter;

/**
 * Get all items that might partially contains the given line.
 */
public class LineSegmentCollector extends RecursiveCollector {

    private QuadTreeItem item;

    public LineSegmentCollector(LineSegment line) {
        this(line.getA(), line.getB());
    }

    public LineSegmentCollector(Point p, Point q) {
        this.item = new QuadTreeItem(p, q);
    }

    @Override
    public boolean shouldGoDown() {
        return true;
    }

    @Override
    public boolean shouldCollect(int cursor, QuadTreeAdapter adapter) {
        return adapter.getItem(cursor).mightOverlap(item);
    }

    @Override
    public boolean contains(Node node) {
        return node.contains(item);
    }
}
