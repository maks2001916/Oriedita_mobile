package com.example.oriedita_core.origami.crease_pattern.data.quadTree.adapter;

import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;
import com.example.oriedita_core.origami.crease_pattern.data.quadTree.QuadTreeItem;

import java.util.List;
import java.util.function.Function;

public class LineSegmentListEndPointAdapter implements QuadTreeAdapter {

    private final List<LineSegment> list;
    private final Function<LineSegment, Point> factory;

    public LineSegmentListEndPointAdapter(List<LineSegment> list, Function<LineSegment, Point> factory) {
        this.list = list;
        this.factory = factory;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public QuadTreeItem getItem(int index) {
        Point p = factory.apply(list.get(index));
        double x = p.getX(), y = p.getY();
        return new QuadTreeItem(x, x, y, y);
    }

    @Override
    public int getPointCount() {
        return list.size();
    }

    @Override
    public Point getPoint(int index) {
        return factory.apply(list.get(index));
    }

    @Override
    public int getOffset() {
        return 0;
    }
}

