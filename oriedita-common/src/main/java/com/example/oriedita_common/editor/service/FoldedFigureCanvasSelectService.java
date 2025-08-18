package com.example.oriedita_common.editor.service;

import com.example.oriedita_common.editor.canvas.MouseWheelTarget;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;

/**
 * Сервис для выбора элементов на холсте сложенной фигуры в Android приложении
 * Предоставляет методы для определения, находится ли точка в паттерне складок
 * или в сложенной фигуре
 */
public interface FoldedFigureCanvasSelectService {
    
    /**
     * Определяет, находится ли точка в паттерне складок или в сложенной фигуре
     * 
     * @param p точка для проверки
     * @return целевой объект для колесика мыши (паттерн складок или сложенная фигура)
     */
    MouseWheelTarget pointInCreasePatternOrFoldedFigure(Point p);
}
