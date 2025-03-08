package com.example.oriedita_common.editor.service;

import oriedita.editor.canvas.MouseWheelTarget;
import origami.crease_pattern.elements.Point;

public interface FoldedFigureCanvasSelectService {
    MouseWheelTarget pointInCreasePatternOrFoldedFigure(Point p);
}
