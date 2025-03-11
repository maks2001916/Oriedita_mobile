package com.example.oriedita_core.origami.data.quadTree.adapter;

import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;
import com.example.oriedita_core.origami.data.quadTree.QuadTreeItem;

import java.util.List;

public class LineSegmentListAdapter implements QuadTreeAdapter {

    private final List<LineSegment> list;
    private final int offset;

    public LineSegmentListAdapter(List<LineSegment> list) {
        this(list, 1);
    }

    public LineSegmentListAdapter(List<LineSegment> list, int offset) {
        this.list = list;
        this.offset = offset;
    }

    @Override
    public int getCount() {
        return list.size() - offset;
    }

    @Override
    public QuadTreeItem getItem(int index) {
        LineSegment l = list.get(index + offset);
        return new QuadTreeItem(l.getA(), l.getB());
    }

    @Override
    public int getPointCount() {
        return (list.size() - offset) * 2;
    }

    @Override
    public Point getPoint(int index) {
        LineSegment l = list.get(index / 2 + offset);
        return index % 2 == 0 ? l.getA() : l.getB();
    }

    @Override
    public int getOffset() {
        return offset;
    }
}

