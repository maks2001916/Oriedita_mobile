package com.example.oriedita_core.origami.crease_pattern.data.quadTree.adapter;

import origami.crease_pattern.element.Point;
import origami.data.quadTree.QuadTree;
import origami.data.quadTree.QuadTreeItem;

/**
 * QuadTreeAdapter interface adapts different types of collection to
 * {@link QuadTree}.
 *
 * @author Mu-Tsun Tsai
 */
public interface QuadTreeAdapter {

    /**
     * Get the item count in the collection.
     */
    public int getCount();

    /**
     * Get the item from the collection and convert it to QuadTreeItem.
     */
    public QuadTreeItem getItem(int index);

    /**
     * Get the number of points for initialization.
     */
    public int getPointCount();

    /**
     * Get a point for initialization.
     */
    public Point getPoint(int index);

    /**
     * Get index offset.
     */
    public int getOffset();
}

