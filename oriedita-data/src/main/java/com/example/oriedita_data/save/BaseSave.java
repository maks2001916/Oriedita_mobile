package com.example.oriedita_data.save;

import com.example.oriedita_data.databinding.ApplicationModel;
import com.example.oriedita_data.databinding.CanvasModel;
import com.example.oriedita_data.databinding.FoldedFigureModel;
import com.example.oriedita_data.databinding.GridModel;
import com.example.oriedita_common.editor.drawing.tools.Camera;
import com.example.oriedita_common.editor.text.Text;
import com.example.oriedita_core.origami.crease_pattern.elements.Circle;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Базовая реализация сохранения состояния оригами
 * Этот класс не должен сериализоваться в JSON, поэтому не имеет аннотации типа
 * Содержит все основные элементы оригами: линии, круги, точки, текст и модели
 */
public class BaseSave implements Save {
    // Основные элементы оригами
    private List<LineSegment> lineSegments;      // Линии сгибов
    private List<Circle> circles;                // Круги (вспомогательные элементы)
    private List<Text> texts;                    // Текстовые элементы
    private String title;                        // Заголовок сохранения
    private List<Point> points;                  // Точки
    private List<LineSegment> auxLineSegments;   // Вспомогательные линии

    // Модели состояния приложения
    private Camera creasePatternCamera;          // Камера для отображения
    private CanvasModel canvasModel;             // Модель холста
    private GridModel gridModel;                 // Модель сетки
    private FoldedFigureModel foldedFigureModel; // Модель сложенной фигуры
    private ApplicationModel applicationModel;   // Модель приложения

    /**
     * Получает модель приложения
     * @return модель приложения
     */
    public ApplicationModel getApplicationModel() {
        return applicationModel;
    }

    /**
     * Устанавливает модель приложения с созданием копии
     * @param applicationModel модель приложения для копирования
     */
    public void setApplicationModel(ApplicationModel applicationModel) {
        if (applicationModel != null) {
            this.applicationModel = new ApplicationModel();
            this.applicationModel.set(applicationModel);
        }
    }

    /**
     * Защищенный конструктор для инициализации коллекций
     */
    protected BaseSave() {
        lineSegments = new ArrayList<>();
        circles = new ArrayList<>();
        points = new ArrayList<>();
        auxLineSegments = new ArrayList<>();
        texts = new ArrayList<>();
    }

    /**
     * Добавляет точку в сохранение
     * @param p точка для добавления
     */
    public void addPoint(Point p) {
        points.add(p);
    }

    /**
     * Получает список точек
     * @return список точек
     */
    public List<Point> getPoints() {
        return points;
    }

    /**
     * Устанавливает список точек
     * @param points список точек для установки
     */
    public void setPoints(List<Point> points) {
        this.points = points;
    }

    /**
     * Получает камеру для отображения
     * @return камера
     */
    public Camera getCreasePatternCamera() {
        return creasePatternCamera;
    }

    /**
     * Устанавливает камеру для отображения
     * @param creasePatternCamera камера для установки
     */
    public void setCreasePatternCamera(Camera creasePatternCamera) {
        this.creasePatternCamera = creasePatternCamera;
    }

    /**
     * Получает заголовок сохранения
     * @return заголовок
     */
    public String getTitle() {
        return title;
    }

    /**
     * Устанавливает заголовок сохранения
     * @param title заголовок для установки
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Добавляет линию сгиба
     * @param lineSegment линия для добавления
     */
    public void addLineSegment(LineSegment lineSegment) {
        this.lineSegments.add(lineSegment);
    }

    /**
     * Добавляет круг
     * @param circle круг для добавления
     */
    public void addCircle(Circle circle) {
        this.circles.add(circle);
    }

    /**
     * Получает список линий сгибов
     * @return список линий
     */
    public List<LineSegment> getLineSegments() {
        return lineSegments;
    }

    /**
     * Устанавливает список линий сгибов
     * @param lineSegments список линий для установки
     */
    public void setLineSegments(List<LineSegment> lineSegments) {
        this.lineSegments = lineSegments;
    }

    /**
     * Получает список кругов
     * @return список кругов
     */
    public List<Circle> getCircles() {
        return circles;
    }

    /**
     * Устанавливает список кругов
     * @param circles список кругов для установки
     */
    public void setCircles(List<Circle> circles) {
        this.circles = circles;
    }

    /**
     * Копирует данные из другого сохранения
     * Копирует линии, круги, вспомогательные линии и заголовок
     * @param save сохранение для копирования
     */
    public void set(Save save) {
        for (LineSegment s : save.getLineSegments()) {
            addLineSegment(s);
        }
        for (Circle c : save.getCircles()) {
            addCircle(c);
        }
        for (LineSegment s : save.getAuxLineSegments()) {
            addAuxLineSegment(s);
        }
        setTitle(save.getTitle());
    }

    /**
     * Получает модель холста
     * @return модель холста
     */
    public CanvasModel getCanvasModel() {
        return canvasModel;
    }

    /**
     * Устанавливает модель холста с созданием копии
     * @param canvasModel модель холста для копирования
     */
    public void setCanvasModel(CanvasModel canvasModel) {
        this.canvasModel = new CanvasModel();
        if (canvasModel != null) {
            this.canvasModel.set(canvasModel);
        }
    }

    /**
     * Получает модель сетки
     * @return модель сетки
     */
    public GridModel getGridModel() {
        return gridModel;
    }

    /**
     * Устанавливает модель сетки с созданием копии
     * @param gridModel модель сетки для копирования
     */
    public void setGridModel(GridModel gridModel) {
        this.gridModel = new GridModel();
        if (gridModel != null) {
            this.gridModel.set(gridModel);
        }
    }

    /**
     * Получает модель сложенной фигуры
     * @return модель сложенной фигуры
     */
    public FoldedFigureModel getFoldedFigureModel() {
        return foldedFigureModel;
    }

    /**
     * Устанавливает модель сложенной фигуры с созданием копии
     * @param foldedFigureModel модель сложенной фигуры для копирования
     */
    public void setFoldedFigureModel(FoldedFigureModel foldedFigureModel) {
        this.foldedFigureModel = new FoldedFigureModel();
        if (foldedFigureModel != null) {
            this.foldedFigureModel.set(foldedFigureModel);
        }
    }

    /**
     * Получает список вспомогательных линий
     * @return список вспомогательных линий
     */
    public List<LineSegment> getAuxLineSegments() {
        return auxLineSegments;
    }

    /**
     * Устанавливает список вспомогательных линий
     * @param auxLineSegments список вспомогательных линий для установки
     */
    public void setAuxLineSegments(List<LineSegment> auxLineSegments) {
        this.auxLineSegments = auxLineSegments;
    }

    /**
     * Добавляет вспомогательную линию
     * @param lineSegment вспомогательная линия для добавления
     */
    public void addAuxLineSegment(LineSegment lineSegment) {
        this.auxLineSegments.add(lineSegment);
    }

    /**
     * Добавляет данные из другого сохранения к текущему
     * Добавляет линии, круги, вспомогательные линии и текст
     * @param save сохранение для добавления
     */
    public void add(Save save) {
        for (LineSegment s : save.getLineSegments()) {
            addLineSegment(s);
        }
        for (Circle c : save.getCircles()) {
            addCircle(c);
        }
        for (LineSegment s : save.getAuxLineSegments()) {
            addAuxLineSegment(s);
        }
        for (Text t : save.getTexts()) {
            addText(t);
        }
    }

    /**
     * Проверяет, можно ли сохранить в формате .cp без потери информации
     * .cp формат поддерживает только линии сгибов, но не круги, вспомогательные линии и текст
     * @return true если можно сохранить в .cp без потери данных, false в противном случае
     */
    public boolean canSaveAsCp() {
        return circles.isEmpty() && auxLineSegments.isEmpty() && texts.isEmpty();
    }

    /**
     * Добавляет текстовый элемент
     * @param text текстовый элемент для добавления
     */
    @Override
    public void addText(Text text) {
        texts.add(text);
    }

    /**
     * Получает список текстовых элементов
     * @return список текстовых элементов
     */
    @Override
    public List<Text> getTexts() {
        return texts;
    }

    /**
     * Устанавливает список текстовых элементов
     * @param texts список текстовых элементов для установки
     */
    @Override
    public void setTexts(List<Text> texts) {
        this.texts = texts;
    }

    /**
     * Очищает все данные сохранения
     * Удаляет все линии, круги, точки, текст и сбрасывает модели
     */
    public void clear() {
        lineSegments.clear();
        circles.clear();
        points.clear();
        auxLineSegments.clear();
        texts.clear();
        title = null;
        creasePatternCamera = null;
        canvasModel = null;
        gridModel = null;
        foldedFigureModel = null;
        applicationModel = null;
    }

    /**
     * Проверяет, пусто ли сохранение
     * @return true если сохранение не содержит элементов, false в противном случае
     */
    public boolean isEmpty() {
        return lineSegments.isEmpty() && 
               circles.isEmpty() && 
               points.isEmpty() && 
               auxLineSegments.isEmpty() && 
               texts.isEmpty() &&
               title == null;
    }

    /**
     * Получает общее количество элементов в сохранении
     * @return общее количество элементов
     */
    public int getTotalElements() {
        return lineSegments.size() + 
               circles.size() + 
               points.size() + 
               auxLineSegments.size() + 
               texts.size();
    }
}
