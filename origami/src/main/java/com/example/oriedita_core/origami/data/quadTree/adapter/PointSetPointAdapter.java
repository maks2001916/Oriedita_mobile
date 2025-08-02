package com.example.oriedita_core.origami.data.quadTree.adapter;


import com.example.oriedita_core.origami.crease_pattern.worker.PointSet;
import com.example.oriedita_core.origami.data.quadTree.QuadTreeItem;

public class PointSetPointAdapter extends PointSetAdapter {

    public PointSetPointAdapter(PointSet set) {
        super(set);
    }

    @Override
    public int getCount() {
        return set.getNumPoints();
    }

    @Override
    public QuadTreeItem getItem(int index) {
        // PointSet is 1-based
        double x = set.getPointX(index + 1);
        double y = set.getPointY(index + 1);
        return new QuadTreeItem(x, x, y, y);
    }
}
