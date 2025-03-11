package com.example.oriedita_core.origami.data.quadTree.adapter;

import com.example.oriedita_core.origami.crease_pattern.LineSegmentSet;
import com.example.oriedita_core.origami.data.quadTree.QuadTreeItem;

/**
 * LineSegmentSetLineAdapter is a {@link LineSegmentSetAdapter} of which items
 * are the lines.
 *
 * @author Mu-Tsun Tsai
 */
public class LineSegmentSetLineAdapter extends LineSegmentSetAdapter {

    public LineSegmentSetLineAdapter(LineSegmentSet set) {
        super(set);
    }

    @Override
    public int getCount() {
        return set.getNumLineSegments();
    }

    @Override
    public QuadTreeItem getItem(int index) {
        return new QuadTreeItem(set.getA(index), set.getB(index));
    }
}

