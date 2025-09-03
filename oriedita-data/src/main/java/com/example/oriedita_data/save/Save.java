package com.example.oriedita_data.save;

import com.example.oriedita_data.databinding.ApplicationModel;
import com.example.oriedita_data.databinding.CanvasModel;
import com.example.oriedita_data.databinding.FoldedFigureModel;
import com.example.oriedita_data.databinding.GridModel;
import com.example.oriedita_common.editor.drawing.tools.Camera;
import com.example.oriedita_core.origami.data.save.LineSegmentSave;
import com.example.oriedita_core.origami.data.save.PointSave;

import java.io.Serializable;

/**
 * Интерфейс для сохранения состояния оригами
 * Определяет методы для работы с различными элементами оригами и моделями приложения
 * Поддерживает версионирование через наследование от BaseSave
 */
public interface Save extends PointSave, LineSegmentSave, TextSave, Serializable {
    
    /**
     * Получает модель приложения
     * @return модель приложения с настройками
     */
    ApplicationModel getApplicationModel();

    /**
     * Устанавливает модель приложения
     * @param applicationModel модель приложения для установки
     */
    void setApplicationModel(ApplicationModel applicationModel);

    /**
     * Получает камеру для отображения оригами
     * @return камера с настройками масштабирования и позиционирования
     */
    Camera getCreasePatternCamera();

    /**
     * Устанавливает камеру для отображения оригами
     * @param creasePatternCamera камера для установки
     */
    void setCreasePatternCamera(Camera creasePatternCamera);

    /**
     * Получает модель холста
     * @return модель холста с настройками отображения
     */
    CanvasModel getCanvasModel();

    /**
     * Устанавливает модель холста
     * @param canvasModel модель холста для установки
     */
    void setCanvasModel(CanvasModel canvasModel);

    /**
     * Получает модель сетки
     * @return модель сетки с настройками отображения
     */
    GridModel getGridModel();

    /**
     * Устанавливает модель сетки
     * @param gridModel модель сетки для установки
     */
    void setGridModel(GridModel gridModel);

    /**
     * Получает модель сложенной фигуры
     * @return модель сложенной фигуры с настройками
     */
    FoldedFigureModel getFoldedFigureModel();

    /**
     * Устанавливает модель сложенной фигуры
     * @param foldedFigureModel модель сложенной фигуры для установки
     */
    void setFoldedFigureModel(FoldedFigureModel foldedFigureModel);

    /**
     * Копирует данные из другого сохранения
     * Заменяет все текущие данные данными из указанного сохранения
     * @param save сохранение для копирования
     */
    void set(Save save);

    /**
     * Добавляет данные из другого сохранения к текущему
     * Объединяет данные, не заменяя существующие
     * @param save сохранение для добавления
     */
    void add(Save save);

    /**
     * Проверяет, можно ли сохранить в формате .cp без потери информации
     * .cp формат поддерживает только линии сгибов, но не круги, вспомогательные линии и текст
     * @return true если можно сохранить в .cp без потери данных, false в противном случае
     */
    boolean canSaveAsCp();
}
