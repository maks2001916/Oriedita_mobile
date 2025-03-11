package com.example.oriedita_core.origami.data.save;

import com.example.oriedita_core.origami.crease_pattern.elements.Point;

import java.util.List;

/**
 * Savefile containing points
 *
 * @see origami.crease_pattern.PointSet
 */
public interface PointSave {
    void addPoint(Point p);

    List<Point> getPoints();

    void setPoints(List<Point> points);
}
