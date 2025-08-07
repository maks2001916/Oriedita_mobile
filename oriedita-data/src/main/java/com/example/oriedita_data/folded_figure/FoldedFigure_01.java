package com.example.oriedita_data.folded_figure;

import com.example.oriedita_data.databinding.FoldedFigureModel;
import com.example.oriedita_core.origami.folding.FoldedFigure;
import com.example.oriedita_core.origami.folding.util.IBulletinBoard;

/**
 * FoldedFigure_01 - алгоритм предсказания складывания на основе FoldedFigure
 * 
 * Этот класс расширяет базовый класс FoldedFigure и предоставляет методы
 * для работы с моделью данных сложенной фигуры (FoldedFigureModel).
 * Используется для управления состоянием алгоритма складывания оригами.
 */
public class FoldedFigure_01 extends FoldedFigure {
    
    /**
     * Конструктор класса
     * @param bb доска объявлений для алгоритма складывания
     */
    public FoldedFigure_01(IBulletinBoard bb) {
        super(bb);
    }

    /**
     * Устанавливает данные из модели сложенной фигуры
     * Копирует состояние и флаг валидности поиска перекрытий из модели в алгоритм
     * 
     * @param foldedFigureModel модель данных сложенной фигуры
     */
    public void setData(FoldedFigureModel foldedFigureModel) {
        // Копируем состояние алгоритма из модели
        ip4 = foldedFigureModel.getState();
        // Копируем флаг валидности поиска других перекрытий
        findAnotherOverlapValid = foldedFigureModel.isFindAnotherOverlapValid();
    }

    /**
     * Получает данные из алгоритма и сохраняет в модель сложенной фигуры
     * Копирует состояние и флаг валидности поиска перекрытий из алгоритма в модель
     * 
     * @param foldedFigureModel модель данных сложенной фигуры для обновления
     */
    public void getData(FoldedFigureModel foldedFigureModel) {
        // Сохраняем текущее состояние алгоритма в модель
        foldedFigureModel.setState(ip4);
        // Сохраняем флаг валидности поиска других перекрытий в модель
        foldedFigureModel.setFindAnotherOverlapValid(findAnotherOverlapValid);
    }
}
