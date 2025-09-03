package com.example.oriedita_common.editor.service;

import com.example.oriedita_common.editor.Foldable;
import com.example.oriedita_core.origami.crease_pattern.FoldingException;
import com.example.oriedita_core.origami.crease_pattern.LineSegmentSet;
import com.example.oriedita_core.origami.folding.FoldedFigure;

/**
 * Сервис для работы со складыванием оригами в Android приложении
 * Предоставляет методы для оценки складывания, создания сложенных фигур
 * и управления процессом складывания
 */
public interface FoldingService {
    /**
     * Выполняет оценку складывания для выбранной фигуры
     * 
     * @param selectedFigure выбранная фигура для оценки складывания
     * @throws InterruptedException если операция была прервана
     * @throws FoldingException если произошла ошибка при складывании
     */
    void folding_estimated(Foldable selectedFigure) throws InterruptedException, FoldingException;

    /**
     * Выполняет складывание с указанным порядком оценки
     * 
     * @param estimationOrder порядок оценки для складывания
     */
    void fold(FoldedFigure.EstimationOrder estimationOrder);

    /**
     * Получает текущий тип складывания
     * 
     * @return тип складывания
     */
    FoldType getFoldType();

    /**
     * Инициализирует новую сложенную фигуру
     * 
     * @return инициализированная сложенная фигура
     */
    Foldable initFoldedFigure();

    /**
     * Создает двухцветный паттерн складок
     */
    void createTwoColoredCp();

    /**
     * Складывает другую фигуру
     * 
     * @param selectedItem выбранный элемент для складывания
     */
    void foldAnother(Foldable selectedItem);

    /**
     * Получает набор сегментов линий для складывания
     * 
     * @return набор сегментов линий
     */
    LineSegmentSet getLineSegmentsForFolding();

    /**
     * Дублирует сложенную фигуру
     * 
     * @param figureToDuplicate фигура для дублирования
     */
    void duplicate(FoldedFigure figureToDuplicate);

    /**
     * Типы складывания оригами
     */
    enum FoldType {
        /**
         * Складывание для всех связанных линий
         */
        FOR_ALL_CONNECTED_LINES_1,
        
        /**
         * Складывание для выбранных линий
         */
        FOR_SELECTED_LINES_2,
        
        /**
         * Складывание для существующей сложенной фигуры
         */
        FOR_EXISTING_FOLDED_FIGURE_3,
    }
}
