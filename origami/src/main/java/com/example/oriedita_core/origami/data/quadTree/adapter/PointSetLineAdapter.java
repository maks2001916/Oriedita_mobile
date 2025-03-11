package com.example.oriedita_core.origami.data.quadTree.adapter;

import com.example.oriedita_core.origami.crease_pattern.worker.PointSet;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;
import com.example.oriedita_core.origami.data.quadTree.QuadTreeItem;

public class PointSetLineAdapter extends PointSetAdapter {

    public PointSetLineAdapter(PointSet set) {
        super(set);
    }

    @Override
    public int getCount() {
        return set.getNumLines();
    }

    @Override
    public QuadTreeItem getItem(int index) {
        // Lines in PointSet are 1-based
        Point A = set.getBeginPointFromLineId(index + 1);
        Point B = set.getEndPointFromLineId(index + 1);
        double ax = A.getX(), ay = A.getY();
        double bx = B.getX(), by = B.getY();
        return new QuadTreeItem(Math.min(ax, bx), Math.max(ax, bx), Math.min(ay, by), Math.max(ay, by));
    }
}

