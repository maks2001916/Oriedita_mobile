package com.example.oriedita_core.origami.crease_pattern.elements;

import android.graphics.Color;

public interface MutableLineSegment {
    Point getA();
    void setA(Point a);
    Point getB();
    void setB(Point b);
    void setAB(Point a, Point b);

    LineColor getColor();
    void setColor(LineColor color);
    LineSegment.ActiveState getActiveState();
    void setActiveState(LineSegment.ActiveState activeState);
    Color getCustomColor();
    void setCustomColor();
    int getCustomized();
    void setCustomized(int customized);
    int getSelected();
    void setSelected(int selected);

    LineSegment asImmutable();
}

