package com.example.oriedita.editor.handler;

import oriedita.editor.canvas.MouseMode;
import origami.crease_pattern.elements.LineSegment;
import origami.crease_pattern.elements.Polygon;

@Handles(MouseMode.UNSELECT_POLYGON_67)
public class MouseHandlerUnselectPolygon extends BaseMouseHandlerPolygon {
    public MouseHandlerUnselectPolygon() {}

    @Override
    protected void performAction() {
        Polygon polygon = new Polygon(d.getLineStep().stream().map(LineSegment::getA).toList());
        d.getFoldLineSet().select_Takakukei(polygon, "unselectAction");
    }
}
