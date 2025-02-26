package com.example.oriedita_core.origami.crease_pattern.data.quadTree.adapter;

import com.example.oriedita_core.origami.crease_pattern.LineSegmentSet;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;
import com.example.oriedita_core.origami.crease_pattern.data.quadTree.QuadTreeItem;

import java.util.function.BiFunction;

/**
 * LineSegmentEndPointAdapter adapts of of the end points of the lines in a
 * {@link LineSegmentSet}.
 *
 * @author Mu-Tsun Tsai
 */
public class LineSegmentEndPointAdapter implements QuadTreeAdapter {

    private final LineSegmentSet set;
    private final BiFunction<LineSegmentSet, Integer, Point> factory;

    public LineSegmentEndPointAdapter(LineSegmentSet set, BiFunction<LineSegmentSet, Integer, Point> factory) {
        this.set = set;
        this.factory = factory;
    }

    @Override
    public int getCount() {
        return set.getNumLineSegments();
    }

    @Override
    public QuadTreeItem getItem(int index) {
        Point p = factory.apply(set, index);
        double x = p.getX(), y = p.getY();
        return new QuadTreeItem(x, x, y, y);
    }

    @Override
    public int getPointCount() {
        return set.getNumLineSegments();
    }

    @Override
    public Point getPoint(int index) {
        return factory.apply(set, index);
    }

    @Override
    public int getOffset() {
        return 0;
    }
}

