package com.example.oriedita_common.editor;

import com.example.oriedita_common.editor.drawing.tools.Camera;
import com.example.oriedita_core.origami.crease_pattern.FoldingException;
import com.example.oriedita_core.origami.crease_pattern.LineSegmentSet;
import com.example.oriedita_core.origami.folding.FoldedFigure;

/**
 * Интерфейс для работы со складыванием оригами в Android
 * Адаптирован для работы с Jetpack Compose и Android-специфичными компонентами
 */
public interface Foldable {
    
    /**
     * Выполняет оценку складывания оригами
     * @param creasePatternCamera камера для отображения паттерна складок
     * @param lineSegmentSet набор сегментов линий для складывания
     * @throws InterruptedException если операция была прервана
     * @throws FoldingException если произошла ошибка при складывании
     */
    void folding_estimated(Camera creasePatternCamera, LineSegmentSet lineSegmentSet) throws InterruptedException, FoldingException;

    /**
     * Устанавливает порядок оценки складывания
     * @param estimationOrder порядок оценки
     */
    void setEstimationOrder(FoldedFigure.EstimationOrder estimationOrder);

    /**
     * Инициализирует оценку складывания
     */
    void estimated_initialize();

    /**
     * Получает текстовый результат складывания
     * @return текстовый результат
     */
    String getTextResult();

    /**
     * Устанавливает текстовый результат складывания
     * @param textResult текстовый результат
     */
    void setTextResult(String textResult);

    /**
     * Создает двухцветный паттерн складок
     * @param camera_of_foldLine_diagram камера для диаграммы линий складывания
     * @param Ss0 набор сегментов линий
     * @throws InterruptedException если операция была прервана
     */
    void createTwoColorCreasePattern(Camera camera_of_foldLine_diagram, LineSegmentSet Ss0) throws InterruptedException;
}
