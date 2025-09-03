package com.example.oriedita_data.canvas;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.example.oriedita_common.editor.canvas.MouseMode;
import com.example.oriedita_core.origami.crease_pattern.FlatFoldabilityViolation;
import com.example.oriedita_data.drawing.DrawingUtil;

import com.example.oriedita_common.editor.service.TaskExecutorService;
import com.example.oriedita_core.origami.Epsilon;
import com.example.oriedita_core.origami.crease_pattern.FoldLineSet;
import com.example.oriedita_core.origami.crease_pattern.LineSegmentSet;
import com.example.oriedita_core.origami.crease_pattern.OritaCalc;
import com.example.oriedita_core.origami.crease_pattern.elements.Circle;
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;
import com.example.oriedita_core.origami.crease_pattern.elements.Polygon;
import com.example.oriedita_core.origami.crease_pattern.elements.Rectangle;
import com.example.oriedita_core.origami.crease_pattern.elements.StraightLine;
import com.example.oriedita_core.origami.crease_pattern.worker.foldlineset.Check1;
import com.example.oriedita_core.origami.crease_pattern.worker.foldlineset.Check2;
import com.example.oriedita_core.origami.crease_pattern.worker.foldlineset.Check3;
import com.example.oriedita_core.origami.crease_pattern.worker.foldlineset.Fix1;
import com.example.oriedita_core.origami.crease_pattern.worker.foldlineset.Fix2;
import com.example.oriedita_core.origami.crease_pattern.worker.foldlineset.InsideToAux;
import com.example.oriedita_core.origami.crease_pattern.worker.foldlineset.OrganizeCircles;
import com.example.oriedita_core.origami.crease_pattern.worker.foldlineset.BranchTrim;
import com.example.oriedita_data.databinding.ApplicationModel;
import com.example.oriedita_data.databinding.CanvasModel;
import com.example.oriedita_data.databinding.FileModel;
import com.example.oriedita_data.databinding.FoldedFigureModel;
import com.example.oriedita_data.databinding.GridModel;
import com.example.oriedita_data.databinding.SelectedTextModel;
import com.example.oriedita_data.drawing.Grid;
import com.example.oriedita_common.editor.canvas.FoldLineAdditionalInputMode;
import com.example.oriedita_common.editor.canvas.LineStyle;
import com.example.oriedita_common.editor.drawing.tools.Camera;
import com.example.oriedita_data.save.Save;
import com.example.oriedita_data.service.HistoryState;
import com.example.oriedita_data.service.impl.DequeHistoryState;
import com.example.oriedita_data.save.SaveProvider;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Реализация CreasePattern_Worker
 */
public abstract class CreasePattern_Worker_Impl implements CreasePattern_Worker {
    
    private static final String TAG = "CreasePattern_Worker_Impl";
    private static final int CHECK4_COLOR_TRANSPARENCY_INCREMENT = 10;

    // ------------
    private final int check4ColorTransparencyIncrement = 10;
    private final LineSegmentSet lineSegmentSet = new LineSegmentSet();    // Создание базовой структуры ветвей
    private Camera creasePatternCamera;
    private TaskExecutorService camvTaskExecutor;
    private CanvasModel canvasModel;
    private ApplicationModel applicationModel;
    private GridModel gridModel;
    private FoldedFigureModel foldedFigureModel;

    private TextWorker textWorker;
    private FileModel fileModel;
    private FoldLineSet foldLineSet;    // Хранение полигональных линий
    private Grid grid = new Grid();
    private HistoryState historyState;
    private HistoryState auxHistoryState;
    /**
     * Временные сегменты линий при рисовании.
     */
    private final List<LineSegment> lineStep = new ArrayList<>();
    /**
     * Временный Android Path при рисовании.
     */
    private final Path linePath = new Path();
    /**
     * Временные круги при рисовании.
     */
    private final List<Circle> circleStep = new ArrayList<>();
    /**
     * Кандидаты сегментов линий.
     */
    private final List<LineSegment> lineCandidate = new ArrayList<>();
    private Camera camera = new Camera();
    // mouseMode==61 - используется для прямоугольного выбора (функция выбора, похожая на paint)
    private SelectedTextModel textModel;
    private double selectionDistance = 50.0;//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Значение для определения, близка ли входная точка к существующей точке или сегменту линии
    private int pointSize = 1;
    private LineColor lineColor;// Цвет сегмента линии
    private LineColor auxLineColor = LineColor.ORANGE_4;// Цвет вспомогательной линии
    private boolean gridInputAssist = false;// 1 если используется функция помощи ввода для отображения мелкой сетки, 0 если не используется
    private int customCircleColor;// Хранит пользовательские цвета для кругов и вспомогательных горячих линий
    private FoldLineAdditionalInputMode i_foldLine_additional = FoldLineAdditionalInputMode.POLY_LINE_0;// = 0 - ввод полигональной линии = 1 - режим ввода вспомогательной линии (при вводе сегмента линии эти два). При удалении сегмента линии значение становится следующим. = 0 - удаление полигональной линии, = 1 - удаление вспомогательной картинной линии, = 2 - удаление черной линии, = 3 - удаление вспомогательной живой линии, = 4 - линия сгиба, вспомогательная живая линия и вспомогательная картинная линия.
    private FoldLineSet auxLines;    // Хранение вспомогательных линий
    private int foldLineDividingNumber = 1;
    private int numPolygonCorners = 5;
    private String text_cp_setumei;
    private String s_title; // Используется для хранения заголовка, который появляется в верхней части рамки
    private boolean check1 = false;// =0 не выполнять check1, 1=выполнять
    private boolean check2 = false;// =0 не выполнять check2, 1=выполнять
    private boolean check3 = false;// =0 не выполнять check3, 1=выполнять // TODO: intellij говорит, что это поле никогда не записывается, дважды проверьте, можно ли удалить check3
    private boolean check4 = false;// =0 не выполнять check4, 1=выполнять
    private boolean isSelectionEmpty = false;
    //---------------------------------
    // ****************************************************************************************************************************************
    // ************** Определение переменных до сих пор ****************************************************************************************************
    // ****************************************************************************************************************************************
    // ------------------------------------------------------------------------------------------------------------
    // Подрежим операции для MouseMode.FOLDABLE_LINE_DRAW_71, либо DRAW_CREASE_FREE_1, либо VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE_38
    //--------------------------------------------
    private CanvasModel.SelectionOperationMode i_select_mode = CanvasModel.SelectionOperationMode.NORMAL_0;// =0 - обычная операция выбора
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private OperationFrame operationFrame;

    public CreasePattern_Worker_Impl() {
        this.foldLineSet = new FoldLineSet();
        this.auxLines = new FoldLineSet();
        this.camera = new Camera();
        this.grid = new Grid();
        this.pcs = new PropertyChangeSupport(this);
        
        this.historyState = new DequeHistoryState();
        this.auxHistoryState = new DequeHistoryState();
        this.textWorker = new TextWorker();
        this.operationFrame = new OperationFrame();
        
        // Инициализация недостающих компонентов
        this.creasePatternCamera = new Camera();
        this.camvTaskExecutor = null;
        this.canvasModel = null;
        this.applicationModel = null;
        this.gridModel = null;
        this.foldedFigureModel = null;
        this.fileModel = null;
        this.textModel = null;
        
        // Инициализация значений по умолчанию
        this.lineColor = LineColor.BLACK_0;
        this.text_cp_setumei = "1/";
        this.s_title = "no title";
        
        initialize();
    }
    
    public CreasePattern_Worker_Impl(HistoryState historyState, HistoryState auxHistoryState, 
                                   TextWorker textWorker, OperationFrame operationFrame, 
                                   TaskExecutorService camvTaskExecutor) {
        this.foldLineSet = new FoldLineSet();
        this.auxLines = new FoldLineSet();
        this.camera = new Camera();
        this.grid = new Grid();
        this.pcs = new PropertyChangeSupport(this);
        
        this.historyState = historyState;
        this.auxHistoryState = auxHistoryState;
        this.textWorker = textWorker;
        this.operationFrame = operationFrame;
        this.camvTaskExecutor = camvTaskExecutor;
        
        // Инициализация недостающих компонентов
        this.creasePatternCamera = new Camera();
        this.canvasModel = null;
        this.applicationModel = null;
        this.gridModel = null;
        this.foldedFigureModel = null;
        this.fileModel = null;
        this.textModel = null;
        
        // Инициализация значений по умолчанию
        this.lineColor = LineColor.BLACK_0;
        this.text_cp_setumei = "1/";
        this.s_title = "no title";
        
        initialize();
    }
    
    public CreasePattern_Worker_Impl(Camera creasePatternCamera,
                                     HistoryState normalHistoryState,
                                     HistoryState auxHistoryState,
                                     FoldLineSet auxLines,
                                     FoldLineSet foldLineSet,
                                     TaskExecutorService camvTaskExecutor,
                                     CanvasModel canvasModel,
                                     ApplicationModel applicationModel,
                                     GridModel gridModel,
                                     FoldedFigureModel foldedFigureModel,
                                     FileModel fileModel,
                                     TextWorker textWorker,
                                     SelectedTextModel textModel) {
        this.creasePatternCamera = creasePatternCamera;
        this.historyState = normalHistoryState;
        this.auxHistoryState = auxHistoryState;
        this.camvTaskExecutor = camvTaskExecutor;
        this.canvasModel = canvasModel;
        this.applicationModel = applicationModel;
        this.gridModel = gridModel;
        this.foldedFigureModel = foldedFigureModel;
        this.fileModel = fileModel;
        this.textWorker = textWorker;
        this.textModel = textModel;

        this.auxLines = auxLines;
        this.foldLineSet = foldLineSet;

        this.lineColor = LineColor.BLACK_0;

        this.operationFrame = new OperationFrame();
        this.camera = new Camera();
        this.grid = new Grid();
        this.pcs = new PropertyChangeSupport(this);

        this.text_cp_setumei = "1/";
        this.s_title = "no title";
        
        initialize();
    }
    
    @Override
    public void lineStepAdd(LineSegment s) {
        Log.d(TAG, "Добавление линии в шаги: от (" + s.getA().getX() + ", " + s.getA().getY() + ") до (" + s.getB().getX() + ", " + s.getB().getY() + ")");
        LineSegment s0 = s.clone();
        s0.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
        lineStep.add(s0);
        Log.d(TAG, "Линия добавлена в шаги. Всего шагов: " + lineStep.size());
    }

    public void setGridConfigurationData(GridModel gridModel) {
        Log.d(TAG, "Установка данных конфигурации сетки");
        grid.setGridConfigurationData(gridModel);
        text_cp_setumei = "1/" + grid.getGridSize();
        calculateDecisionWidth();
        Log.d(TAG, "Данные конфигурации сетки установлены");
    }

    @Override
    public void clearCreasePattern() {
        foldLineSet.reset();
        auxLines.reset();
        initialize();

        camera.reset();
        lineStep.clear();
        circleStep.clear();
        lineCandidate.clear();
    }
    
    @Override
    public void reset() {
        foldLineSet.reset();
        auxLines.reset();
        
        historyState.reset();
        auxHistoryState.reset();
        
        camera.reset();
        lineStep.clear();
        circleStep.clear();
        linePath.reset();
    }
    
    @Override
    public void initialize() {
        // Добавляем границы бумаги (квадрат)
        foldLineSet.addLine(-200.0, -200.0, -200.0, 200.0, LineColor.BLACK_0);
        foldLineSet.addLine(-200.0, -200.0, 200.0, -200.0, LineColor.BLACK_0);
        foldLineSet.addLine(200.0, 200.0, -200.0, 200.0, LineColor.BLACK_0);
        foldLineSet.addLine(200.0, 200.0, 200.0, -200.0, LineColor.BLACK_0);
    }

    public void Memo_jyouhou_toridasi(Save memo1) {
        if (memo1.getCreasePatternCamera() != null) {
            creasePatternCamera.setCamera(memo1.getCreasePatternCamera());
        }

        if (memo1.getApplicationModel() != null) {
            applicationModel.set(memo1.getApplicationModel());
        }

        if (memo1.getCanvasModel() != null) {
            canvasModel.set(memo1.getCanvasModel());
        }

        if (memo1.getGridModel() != null) {
            gridModel.set(memo1.getGridModel());
        }

        if (memo1.getFoldedFigureModel() != null) {
            foldedFigureModel.setFrontColor(memo1.getFoldedFigureModel().getFrontColor());
            foldedFigureModel.setBackColor(memo1.getFoldedFigureModel().getBackColor());
            foldedFigureModel.setLineColor(memo1.getFoldedFigureModel().getLineColor());
        }

        textModel.reset();
    }

    public String setMemo_for_redo_undo(Save save) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<undo,redoでのkiroku復元用
        textWorker.setSave(save);
        textModel.setSelected(false);
        return foldLineSet.setSave(save);
    }

    public void setSave_for_reading(Save memo1) {//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<For reading data
        Memo_jyouhou_toridasi(memo1);
        foldLineSet.setSave(memo1);
        auxLines.setAuxSave(memo1);
        textWorker.setSave(memo1);
    }

    @Override
    public void setSave_for_reading_tuika(Save memo1) {
        Log.d(TAG, "Установка сохранения для чтения (tuika)");
        if (memo1 == null) return;

        // Извлекаем только добавляемые линии
        FoldLineSet temp = new FoldLineSet();
        temp.setSave(memo1);

        double addx = foldLineSet.getMaxX() + 100.0 - temp.getMinX();
        double addy = foldLineSet.getMaxY() - temp.getMaxY();
        temp.move(addx, addy);

        int total_old = foldLineSet.getTotal();
        Save save = SaveProvider.createInstance();
        temp.getSave(save);
        foldLineSet.addSave(save);
        int total_new = foldLineSet.getTotal();
        foldLineSet.divideLineSegmentWithNewLines(total_old, total_new);
        foldLineSet.unselect_all();
        record();
    }

    @Override
    public void setSaveForPaste(Save save1) {
        Log.d(TAG, "Установка сохранения для вставки");
        int total_old = foldLineSet.getTotal();
        foldLineSet.addSave(save1);
        int total_new = foldLineSet.getTotal();
        foldLineSet.divideLineSegmentWithNewLines(total_old, total_new);
        foldLineSet.unselect_all();
        record();
    }

    @Override
    public void setAuxMemo(Save memo1) {
        Log.d(TAG, "Установка вспомогательного мемо");
        auxLines.setAuxSave(memo1);
        Log.d(TAG, "Вспомогательное мемо установлено");
    }
    @Override
    public void allMountainValleyChange() {
        Log.d(TAG, "Изменение всех гор/долин");
        foldLineSet.allMountainValleyChange();
        checkIfNecessary();
        record();
        Log.d(TAG, "Все горы/долины изменены");
    }

    @Override
    public void branch_trim() {
        Log.d(TAG, "Обрезка ветвей");
        BranchTrim.apply(foldLineSet);
        Log.d(TAG, "Ветви обрезаны");
    }
    @Override
    public LineSegmentSet get() {
        Log.d(TAG, "Получение набора сегментов линий");
        Save save = SaveProvider.createInstance();
        foldLineSet.getSave(save);
        lineSegmentSet.setSave(save);
        Log.d(TAG, "Набор сегментов линий получен");
        return lineSegmentSet;
    }

    //折畳み推定用にselectされた線分集合の折線数を intとして出力する。//icolが3(cyan＝水色)以上の補助線はカウントしない
    @Override
    public int getFoldLineTotalForSelectFolding() {
        int total = foldLineSet.getFoldLineTotalForSelectFolding();
        Log.d(TAG, "Получение общего количества линий сгиба для выбора складывания: " + total);
        return total;
    }

    @Override
    public LineSegmentSet getForSelectFolding() {//selectした折線で折り畳み推定をする。
        Log.d(TAG, "Получение набора сегментов линий для выбора складывания");
        Save save = SaveProvider.createInstance();
        foldLineSet.getSaveForSelectFolding(save);
        LineSegmentSet ls = new LineSegmentSet();
        ls.setSave(save);
        Log.d(TAG, "Набор сегментов линий для выбора складывания получен");
        return ls;
    }

    public void calculateDecisionWidth() {
        selectionDistance = applicationModel.getMouseRadius();
        if (camera.getCameraZoomX() > 1) {
            selectionDistance = applicationModel.getMouseRadius() / camera.getCameraZoomX();
        }
    }

    @Override
    public int getTotal() {
        return foldLineSet.getTotal();
    }

    @Override
    public Save getSave(String title) {
        Log.d(TAG, "Получение сохранения с заголовком: " + title);
        Save save_temp = SaveProvider.createInstance();
        foldLineSet.getSave(save_temp, title);
        saveAdditionalInformation(save_temp);
        Log.d(TAG, "Сохранение с заголовком получено");
        return save_temp;
    }

    public Save h_getSave() {
        Log.d(TAG, "Получение вспомогательного сохранения");
        Save save = SaveProvider.createInstance();
        auxLines.h_getSave(save);
        Log.d(TAG, "Вспомогательное сохранение получено");
        return save;
    }

    @Override
    public Save getSave_for_export() {
        Log.d(TAG, "Получение сохранения для экспорта");
        Save save = SaveProvider.createInstance();
        foldLineSet.getSave(save);
        auxLines.h_getSave(save);
        saveAdditionalInformation(save);
        Log.d(TAG, "Сохранение для экспорта получено");

        return save;
    }

    @Override
    public Save getSave_for_export_with_applicationModel() {
        Log.d(TAG, "Получение сохранения для экспорта с моделью приложения");
        Save save = getSave_for_export();

        save.setApplicationModel(applicationModel);
        Log.d(TAG, "Сохранение для экспорта с моделью приложения получено");
        return save;
    }

    @Override
    public void saveAdditionalInformation(Save memo1) {
        Log.d(TAG, "Сохранение дополнительной информации");
        // Сохраняем камеру
        Camera camera = new Camera();
        camera.setCamera(this.camera);
        memo1.setCreasePatternCamera(camera);

        // Сохраняем текст
        textWorker.getSave(memo1);

        // Сохраняем модели
        memo1.setCanvasModel(canvasModel);
        memo1.setGridModel(gridModel);

        memo1.setFoldedFigureModel(foldedFigureModel);
    }

    public void setColor(LineColor i) {
        lineColor = i;
    }

    @Override
    public void point_removal() {
        foldLineSet.removePoints();
    }

    @Override
    public void overlapping_line_removal() {
        foldLineSet.removeOverlappingLines();
    }

    @Override
    public String undo() {
        Log.d(TAG, "Выполнение отмены");
        s_title = setMemo_for_redo_undo(historyState.undo());
        checkIfNecessary();
        refreshIsSelectionEmpty();
        Log.d(TAG, "Отмена выполнена успешно");
        return s_title;
    }

    @Override
    public String redo() {
        s_title = setMemo_for_redo_undo(historyState.redo());
        checkIfNecessary();
        refreshIsSelectionEmpty();
        return s_title;
    }

    @Override
    public void setTitle(String s_title0) {
        Log.d(TAG, "Установка заголовка: " + s_title0);
        s_title = s_title0;
    }

    @Override
    public String getS_title() {
        return s_title;
    }

    @Override
    public void record() {
        Log.d(TAG, "Запись состояния");
        checkIfNecessary();
        // Сохраняем линии с заголовком и доп. данными (камера/текст/модели)
        if (!historyState.isEmpty()) {
            fileModel.setSaved(false);
        }
        historyState.record(getSave(s_title));
        Log.d(TAG, "Состояние записано в историю");
    }

    @Override
    public void auxUndo() {
        Log.d(TAG, "Выполнение вспомогательной отмены");
        if (auxHistoryState.canUndo()) {
            Save save = auxHistoryState.undo();
            setAuxMemo(save);
            Log.d(TAG, "Вспомогательная отмена выполнена успешно");
        } else {
            Log.d(TAG, "Вспомогательная отмена невозможна - история пуста");
        }
    }

    @Override
    public void auxRedo() {
        Log.d(TAG, "Выполнение вспомогательного повтора");
        if (auxHistoryState.canRedo()) {
            Save save = auxHistoryState.redo();
            setAuxMemo(save);
            Log.d(TAG, "Вспомогательный повтор выполнен успешно");
        } else {
            Log.d(TAG, "Вспомогательный повтор невозможен - нет будущих состояний");
        }
    }

    @Override
    public void auxRecord() {
        auxHistoryState.record(h_getSave());
    }

    @Override
    public void drawGrid(Canvas g, int p0x_max, int p0y_max) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1.0f);
        // g - Canvas для рисования
        // paint - Paint объект для настройки стиля рисования
        // camera - камера для преобразования координат
        // p0x_max - максимальная ширина экрана
        // p0y_max - максимальная высота экрана
        // gridInputAssist - флаг помощи ввода сетки
        // minGridUnitSize - минимальный размер единицы сетки
        grid.draw(
                g,
                paint,
                camera,
                p0x_max,
                p0y_max,
                gridInputAssist,
                applicationModel != null ? applicationModel.getMinGridUnitSize() : 1);
    }

    @Override
    public void drawWithCamera(android.graphics.Canvas g, boolean displayComments, boolean displayCpLines, boolean displayAuxLines, boolean displayAuxLiveLines, float lineWidth, LineStyle lineStyle, float f_h_WireframeLineWidth, int p0x_max, int p0y_max, boolean i_mejirusi_display, boolean hideOperationFrame) {// Аргументы: настройки камеры, ширина линии, ширина экрана X, высота экрана Y
        // Установка толщины и формы конца линии
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setStrokeWidth(1.0f);
        paint.setStrokeCap(android.graphics.Paint.Cap.BUTT);
        paint.setStrokeJoin(android.graphics.Paint.Join.MITER);

        // Рисование вспомогательных штрихов (не мешающих полигональным линиям)
        if (displayAuxLiveLines) {
            paint.setStrokeWidth(f_h_WireframeLineWidth);
            paint.setStrokeCap(android.graphics.Paint.Cap.BUTT);
            paint.setStrokeJoin(android.graphics.Paint.Join.MITER);
            for (LineSegment as : auxLines.getLineSegmentsIterable()) {
                DrawingUtil.drawAuxLiveLine(g, paint, as, camera, lineWidth, pointSize, f_h_WireframeLineWidth);
            }
        }

        // Отображение результатов проверки

        paint.setStrokeWidth(15.0f);
        paint.setStrokeCap(android.graphics.Paint.Cap.ROUND);
        paint.setStrokeJoin(android.graphics.Paint.Join.MITER);

        // Check1Senb содержит данные от 0-го до size()-1 элемента
        if (check1) {
            for (LineSegment s_temp : foldLineSet.getCheck1LineSegments()) {
                DrawingUtil.pointingAt1(g, paint, camera.object2TV(s_temp));
            }
        }

        if (check2) {
            for (LineSegment s_temp : foldLineSet.getCheck2LineSegments()) {
                DrawingUtil.pointingAt2(g, paint, camera.object2TV(s_temp));
            }
        }

        paint.setStrokeWidth(2.0f);
        paint.setStrokeCap(android.graphics.Paint.Cap.ROUND);
        paint.setStrokeJoin(android.graphics.Paint.Join.MITER);

        // Check4Senb содержит данные от 0-го до size()-1 элемента
        if (check4) {
            for (FlatFoldabilityViolation violation : foldLineSet.getViolations()) {
                DrawingUtil.drawViolation(g, paint, camera.object2TV(violation.getPoint()), violation,
                        applicationModel != null ? applicationModel.getCheck4ColorTransparency() : 100, 
                        applicationModel != null ? applicationModel.getAdvancedCheck4Display() : false);
            }

            if (displayComments) {
                if (camvTaskExecutor != null && camvTaskExecutor.isTaskRunning()) {
                    paint.setColor(Color.MAGENTA);//orange
                    paint.setTextSize(16f);
                    g.drawText("... cAMV Errors", p0x_max - 100, 10, paint);
                } else {
                    int numErrors = foldLineSet.getViolations().size();
                    if (numErrors == 0) {
                        paint.setColor(Color.GREEN);
                    } else {
                        paint.setColor(Color.RED);
                    }
                    paint.setTextSize(16f);
                    g.drawText(numErrors + " cAMV Errors", p0x_max - 100, 10, paint);
                }
            }
        }

        // Check3Senb содержит данные от 0-го до size()-1 элемента
        if (check3) {
            for (LineSegment s_temp : foldLineSet.getCheck3LineSegments()) {
                DrawingUtil.pointingAt3(g, paint, camera.object2TV(s_temp));
            }
        }

        // Рисование центра камеры крестом
        if (i_mejirusi_display) {
            DrawingUtil.cross(g, paint, camera.object2TV(camera.getCameraPosition()), 5.0, 2.0, LineColor.BLUE_2);
        }

        // Рисование кругов
        if (displayAuxLines) {
            for (Circle circle : foldLineSet.getCircles()) {
                DrawingUtil.drawCircle(g, paint, circle, camera, lineWidth, pointSize);
            }
        }

        Collection<LineSegment> lines = foldLineSet.getLineSegmentsCollection();
        // Рисование выделения
        paint.setStrokeWidth(lineWidth * 2.0f + 2.0f);
        paint.setStrokeCap(android.graphics.Paint.Cap.BUTT);
        paint.setStrokeJoin(android.graphics.Paint.Join.MITER);
        
        for (LineSegment s : lines) {
            if (s.getSelected() == 2) {
                DrawingUtil.drawSelectLine(g, paint, s, camera);
            }
        }

        boolean useRounded = applicationModel != null ? applicationModel.getRoundedEnds() : false;
        // Рисование развертки - только вспомогательные активные линии
        if (displayAuxLines) {
            for (LineSegment s : lines) {
                if (s.getColor() == LineColor.CYAN_3) {
                    DrawingUtil.drawAuxLine(g, paint, s, camera, lineWidth, pointSize, useRounded);
                }
            }
        }

        // Рисование развертки - линии сгиба кроме вспомогательных активных линий
        if (displayCpLines) {
            paint.setColor(Color.BLACK);
            for (LineSegment s : lines) {
                if (s.getColor() != LineColor.CYAN_3 && s.getColor() != LineColor.RED_1 && s.getColor() != LineColor.BLACK_0) {
                    DrawingUtil.drawCpLine(g, paint, s, camera, lineStyle, lineWidth, pointSize, p0x_max, p0y_max, useRounded);
                }
            }
            for (LineSegment s : lines) {
                if (s.getColor() == LineColor.RED_1) {
                    DrawingUtil.drawCpLine(g, paint, s, camera, lineStyle, lineWidth, pointSize, p0x_max, p0y_max, useRounded);
                }
            }
            for (LineSegment s : lines) {
                if (s.getColor() == LineColor.BLACK_0) {
                    DrawingUtil.drawCpLine(g, paint, s, camera, lineStyle, lineWidth, pointSize, p0x_max, p0y_max, useRounded);
                }
            }
        }

        // mouseMode==61 - используется для прямоугольного выбора (функция выбора, похожая на paint)
        if (!hideOperationFrame && canvasModel != null && canvasModel.getMouseMode() == MouseMode.OPERATION_FRAME_CREATE_61 && lineStep.size() == 4) {
            Point p1 = camera.TV2object(operationFrame.getP1());
            Point p2 = camera.TV2object(operationFrame.getP2());
            Point p3 = camera.TV2object(operationFrame.getP3());
            Point p4 = camera.TV2object(operationFrame.getP4());

            lineStep.set(0, new LineSegment(p1, p2, LineColor.GREEN_6));
            lineStep.set(1, new LineSegment(p2, p3, LineColor.GREEN_6));
            lineStep.set(2, new LineSegment(p3, p4, LineColor.GREEN_6));
            lineStep.set(3, new LineSegment(p4, p1, LineColor.GREEN_6));
        }

        // Рисование временных s_step сегментов линий при вводе линии

        if (!hideOperationFrame && (canvasModel == null || canvasModel.getMouseMode() != MouseMode.OPERATION_FRAME_CREATE_61 || lineStep.size() == 4)) {
            for (LineSegment s : lineStep) {
                DrawingUtil.drawLineStep(g, paint, s, camera, lineWidth, gridInputAssist);
            }
        }

        paint.setColor(Color.MAGENTA);
        DrawingUtil.drawCurve(g, paint, linePath, lineWidth);

        // Рисование кандидатов при вводе кандидатов
        paint.setStrokeWidth(lineWidth + 0.0f);
        paint.setStrokeCap(android.graphics.Paint.Cap.BUTT);
        paint.setStrokeJoin(android.graphics.Paint.Join.MITER);

        for (LineSegment s : lineCandidate) {
            DrawingUtil.drawLineCandidate(g, paint, s, camera, pointSize);
        }

        paint.setColor(Color.BLACK);

        for (Circle c : circleStep) {
            DrawingUtil.drawCircleStep(g, paint, c, camera);
        }

        paint.setColor(Color.BLACK);

        if (displayComments) {
            paint.setTextSize(16f);
            g.drawText(text_cp_setumei, 10, 55, paint);
            textWorker.draw(g, camera);
        }
    }

    @Override
    public void resetCircleStep() {
        circleStep.clear();
    }

    //--------------------------------------------------------------------------------------
    //Mouse operation----------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------

    @Override
    public void addCircle(Circle e0) {
        addCircle(e0.getX(), e0.getY(), e0.getR(), e0.getColor());
    }


    //動作モデル00a--------------------------------------------------------------------------------------------------------
    //マウスクリック（マウスの近くの既成点を選択）、マウスドラッグ（選択した点とマウス間の線が表示される）、マウスリリース（マウスの近くの既成点を選択）してから目的の処理をする雛形セット

    @Override
    public void addCircle(Point t0, double dr, LineColor ic) {
        addCircle(t0.getX(), t0.getY(), dr, ic);
    }

    @Override
    public void addCircle(double dx, double dy, double dr, LineColor ic) {
        foldLineSet.addCircle(dx, dy, dr, ic);

        int imin = 0;
        int imax = foldLineSet.numCircles() - 2;
        int jmin = foldLineSet.numCircles() - 1;
        int jmax = foldLineSet.numCircles() - 1;

        foldLineSet.applyCircleCircleIntersection(imin, imax, jmin, jmax);
        foldLineSet.applyLineSegmentCircleIntersection(1, foldLineSet.getTotal(), jmin, jmax);

    }

    @Override
    public FoldLineSet getAuxFoldLineSet() {
        return auxLines;
    }

    @Override
    public void addLineSegment_auxiliary(LineSegment s0) {
        Log.d(TAG, "Добавление вспомогательной линии: от (" + s0.getA().getX() + ", " + s0.getA().getY() + ") до (" + s0.getB().getX() + ", " + s0.getB().getY() + ")");
        auxLines.addLine(s0);
        auxRecord();
    }

    //動作モデル00b--------------------------------------------------------------------------------------------------------
    //マウスクリック（近くの既成点かマウス位置を選択）、マウスドラッグ（選択した点とマウス間の線が表示される）、マウスリリース（近くの既成点かマウス位置を選択）してから目的の処理をする雛形セット


    @Override
    public void addLineSegment(LineSegment s0) {//0 = Без изменений, 1 = Только изменение цвета, 2 = Сегмент линии добавлен
        Log.d(TAG, "Добавление линии в FoldLineSet: от (" + s0.getA().getX() + ", " + s0.getA().getY() + ") до (" + s0.getB().getX() + ", " + s0.getB().getY() + ")");
        
        foldLineSet.addLine(s0);//Просто добавляет информацию s0 в конец senbun foldLineSet
        int total_old = foldLineSet.getTotal();
        foldLineSet.applyLineSegmentCircleIntersection(foldLineSet.getTotal(), foldLineSet.getTotal(), 0, foldLineSet.numCircles() - 1);

        foldLineSet.divideLineSegmentWithNewLines(total_old - 1, total_old);
        
        Log.d(TAG, "Линия успешно добавлена. Всего линий: " + foldLineSet.getTotal());
    }

    @Override
    public Point getClosestPoint(Point t0) {
        // When dividing paper 1/1 Only the end point of the folding line is the reference point. The grid point never becomes the reference point.
        // When dividing paper from 1/2 to 1/512 The end point of the polygonal line and the grid point in the paper frame (-200.0, -200.0 _ 200.0, 200.0) are the reference points.

        //End point of the polygonal line
        Point t1 = foldLineSet.closestPoint(t0); // foldLineSet.closestPoint returns (100000.0,100000.0) if there is no close point

        //Center of circle
        Point t3 = foldLineSet.closestCenter(t0); // foldLineSet.closestCenter returns (100000.0,100000.0) if there is no close point

        if (t0.distanceSquared(t1) > t0.distanceSquared(t3)) {
            t1 = t3;
        }

        if (grid.getBaseState() == GridModel.State.HIDDEN) {
            return t1;
        }

        if (t0.distanceSquared(t1) > t0.distanceSquared(grid.closestGridPoint(t0))) {
            return grid.closestGridPoint(t0);
        }

        return t1;
    }

    @Override
    public LineSegment getClosestLineStepSegment(Point t0, int imin, int imax) {
        int minrid = -100;
        double minr = 100000;
        for (int i = imin; i <= imax; i++) {
            double sk = OritaCalc.determineLineSegmentDistance(t0, lineStep.get(i - 1));
            if (minr > sk) {
                minr = sk;
                minrid = i;
            }//柄の部分に近いかどうか
        }
        return lineStep.get(minrid - 1);
    }

    //------------------------------
    @Override
    public Circle getClosestCircleMidpoint(Point t0) {
        Log.d(TAG, "Поиск ближайшего круга к точке (" + t0.getX() + ", " + t0.getY() + ")");
        return foldLineSet.closestCircleMidpoint(t0);
    }

    //-----------------------------------------------62ここまで　//20181121　iactiveをtppに置き換える
    @Override
    public Point getGridPosition(Point p0) {
        Point p = camera.TV2object(p0);
        Point closestPoint = getClosestPoint(p);
        return grid.getPosition(closestPoint);
    }

    /**
     * Used when OperationFrame is temporarily hidden.
     *
     * @param i New number of steps.
     */

    @Override
    public void resetLineStep(int i) {
        lineStep.clear();

        for (int j = 0; j < i; j++) {
            lineStepAdd(new LineSegment());
        }
    }

    public void resetLinePath(){
        linePath.reset();
    }


    //動作概要　
    //マウスボタン押されたとき　
    //用紙1/1分割時 		折線の端点のみが基準点。格子点が基準点になることはない。
    //用紙1/2から1/512分割時	折線の端点と用紙枠内（-200.0,-200.0 _ 200.0,200.0)）の格子点とが基準点
    //入力点Pが基準点から格子幅kus.d_haba()の1/4より遠いときは折線集合への入力なし
    //線分が長さがなく1点状のときは折線集合への入力なし

    @Override
    public int getCandidateSize() {
        return lineCandidate.size();
    }

    @Override
    public void setIsSelectionEmpty(boolean isSelectionEmpty){
        boolean oldIsSelectionEmpty = this.isSelectionEmpty;
        this.isSelectionEmpty = isSelectionEmpty;
        this.pcs.firePropertyChange("isSelectionEmpty", oldIsSelectionEmpty, isSelectionEmpty);
    }

    @Override
    public boolean getIsSelectionEmpty(){
        return isSelectionEmpty;
    }

    @Override
    public void refreshIsSelectionEmpty(){
        boolean latestState = foldLineSet.isSelectionEmpty();
        if(latestState != this.isSelectionEmpty){
            setIsSelectionEmpty(latestState);
        }
    }

    @Override
    public void select_all() {
        int beforeSelectNum = foldLineSet.getFoldLineTotalForSelectFolding();
        foldLineSet.select_all();
        refreshIsSelectionEmpty();
        canvasModel.markDirty();
        int afterSelectNum = foldLineSet.getFoldLineTotalForSelectFolding();
        if (beforeSelectNum != afterSelectNum) record();
    }

    @Override
    public void unselect_all(boolean ignorePersistent) {
        if (!applicationModel.getSelectPersistent() || ignorePersistent) {
            boolean beforeSelection = getIsSelectionEmpty();
            foldLineSet.unselect_all();
            setIsSelectionEmpty(true);
            canvasModel.markDirty();
            boolean afterSelection = getIsSelectionEmpty();
            if (beforeSelection != afterSelection) record();
        }
    }

    public void unselect_all() {
        unselect_all(true);
    }

    @Override
    public void select(Point p0a, Point p0b) {
        boolean anyLinesSelected = foldLineSet.select(createBox(p0a, p0b));
        if(anyLinesSelected) {
            setIsSelectionEmpty(false);
        }
    }

    @Override
    public void unselect(Point p0a, Point p0b) {
        foldLineSet.unselect(createBox(p0a, p0b));
        refreshIsSelectionEmpty();
    }

    @Override
    public boolean deleteInside_foldingLine(Point p0a, Point p0b) {
        Log.d(TAG, "Удаление линий сгиба в прямоугольнике: от (" + p0a.getX() + ", " + p0a.getY() + ") до (" + p0b.getX() + ", " + p0b.getY() + ")");
        boolean result = foldLineSet.deleteInside_foldingLine(createBox(p0a, p0b));
        Log.d(TAG, "Удаление линий сгиба: " + (result ? "успешно" : "не выполнено"));
        return result;
    }

    @Override
    public boolean deleteInside_edge(Point p0a, Point p0b) {
        Log.d(TAG, "Удаление краевых линий в прямоугольнике: от (" + p0a.getX() + ", " + p0a.getY() + ") до (" + p0b.getX() + ", " + p0b.getY() + ")");
        boolean result = foldLineSet.deleteInside_edge(createBox(p0a, p0b));
        Log.d(TAG, "Удаление краевых линий: " + (result ? "успешно" : "не выполнено"));
        return result;
    }

    @Override
    public boolean deleteInside_aux(Point p0a, Point p0b) {
        Log.d(TAG, "Удаление вспомогательных линий в прямоугольнике: от (" + p0a.getX() + ", " + p0a.getY() + ") до (" + p0b.getX() + ", " + p0b.getY() + ")");
        boolean result = foldLineSet.deleteInside_aux(createBox(p0a, p0b));
        Log.d(TAG, "Удаление вспомогательных линий: " + (result ? "успешно" : "не выполнено"));
        return result;
    }

    @Override
    public boolean insideToDeleteType(Point p0a, Point p0b, com.example.oriedita_core.origami.crease_pattern.CustomLineTypes del) {
        Log.d(TAG, "Удаление типа линий в прямоугольнике: " + del);
        boolean result = foldLineSet.insideToDeleteType(createBox(p0a, p0b), del);
        Log.d(TAG, "Удаление типа линий: " + (result ? "выполнено" : "не выполнено"));
        return result;
    }

    @Override
    public boolean deleteInside_text(Point p1, Point p2) {
        Log.d(TAG, "Удаление текста в прямоугольнике: от (" + p1.getX() + ", " + p1.getY() + ") до (" + p2.getX() + ", " + p2.getY() + ")");
        if (textWorker.deleteInsideRectangle(p1, p2, camera)) {
            textModel.markDirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean change_property_in_4kakukei(Point p0a, Point p0b) {
        return foldLineSet.change_property_in_4kakukei(createBox(p0a, p0b), customCircleColor);
    }

    @Override
    public boolean deleteInside(Point p0a, Point p0b) {
        Log.d(TAG, "Удаление всех элементов в прямоугольнике: от (" + p0a.getX() + ", " + p0a.getY() + ") до (" + p0b.getX() + ", " + p0b.getY() + ")");
        return auxLines.deleteInside(createBox(p0a, p0b));
    }

    @Override
    public int MV_change(Point p0a, Point p0b) {
        Log.d(TAG, "Изменение гор/долин в прямоугольнике: от (" + p0a.getX() + ", " + p0a.getY() + ") до (" + p0b.getX() + ", " + p0b.getY() + ")");
        int result = foldLineSet.MV_change(createBox(p0a, p0b));
        Log.d(TAG, "Изменение гор/долин: изменено " + result + " элементов");
        return result;
    }

    @Override
    public LineSegment extendToIntersectionPoint(LineSegment s0) {
        Log.d(TAG, "Расширение линии до точки пересечения: от (" + s0.getA().getX() + ", " + s0.getA().getY() + ") до (" + s0.getB().getX() + ", " + s0.getB().getY() + ")");

        LineSegment add_sen = new LineSegment(s0);
        Point kousa_point = new Point(1000000.0, 1000000.0);
        double kousa_ten_kyori = kousa_point.distance(add_sen.getA());

        StraightLine tyoku1 =
                new StraightLine(add_sen.getA(), add_sen.getB());

        for (LineSegment s : foldLineSet.getLineSegmentsIterable()) {
            StraightLine.Intersection i_kousa_flg =
                    tyoku1.lineSegment_intersect_reverse_detail(s);

            if (i_kousa_flg.isIntersecting()) {
                kousa_point = OritaCalc.findIntersection(tyoku1, s);
                if (kousa_point.distance(add_sen.getA()) > Epsilon.UNKNOWN_1EN5) {
                    if (kousa_point.distance(add_sen.getA()) < kousa_ten_kyori) {
                        double d_kakudo = OritaCalc.angle(add_sen.getA(), add_sen.getB(), add_sen.getA(), kousa_point);
                        if (d_kakudo < 1.0 || d_kakudo > 359.0) {
                            kousa_ten_kyori = kousa_point.distance(add_sen.getA());
                            add_sen = new LineSegment(add_sen.getA(), kousa_point);
                        }
                    }
                }
            }
        }

        Log.d(TAG, "Расширение линии завершено");
        return add_sen;
    }

    //-------------------------
    @Override
    public void del_selected_senbun() {
        foldLineSet.delSelectedLineSegmentFast();
        setIsSelectionEmpty(true);
    }

    @Override
    public void v_del_all() {
        Log.d(TAG, "Удаление всех вершин");
        try {
            int sousuu_old = foldLineSet.getTotal();
            foldLineSet.del_V_all();
            if (sousuu_old != foldLineSet.getTotal()) {
                record();
                Log.d(TAG, "Все вершины удалены. Изменено линий: " + (sousuu_old - foldLineSet.getTotal()));
            } else {
                Log.d(TAG, "Удаление вершин не изменило количество линий");
            }
        } catch (InterruptedException e) {
            Log.i(TAG, "v_del_all прервано");
        }
    }

    @Override
    public void v_del_all_cc() {
        Log.d(TAG, "Удаление всех вершин (cc)");
        try {
            int sousuu_old = foldLineSet.getTotal();
            foldLineSet.del_V_all_cc();
            if (sousuu_old != foldLineSet.getTotal()) {
                record();
                Log.d(TAG, "Все вершины удалены (cc). Изменено линий: " + (sousuu_old - foldLineSet.getTotal()));
            } else {
                Log.d(TAG, "Удаление вершин (cc) не изменило количество линий");
            }
        } catch (InterruptedException e) {
            Log.i(TAG, "v_del_all_cc прервано");
        }
    }

    @Override
    public void addPreviewLinesToCp() {//20181014
        Log.d(TAG, "Добавление предварительных линий в CP");
        for (LineSegment s : lineStep) {
            if (Epsilon.high.gt0(s.determineLength())) {
                LineSegment add_sen = s.withColor(lineColor);
                addLineSegment(add_sen);
                Log.d(TAG, "Добавлена линия: длина=" + s.determineLength() + ", цвет=" + lineColor);
            } else {
                addCircle(s.determineAX(), s.determineAY(), 5.0, LineColor.CYAN_3);
                Log.d(TAG, "Добавлен круг в точке: (" + s.determineAX() + ", " + s.determineAY() + ")");
            }
        }
        record();
        Log.d(TAG, "Предварительные линии добавлены в CP");
    }

    @Override
    public boolean insideToMountain(Point p0a, Point p0b) {
        Log.d(TAG, "Преобразование в горы в прямоугольнике: от (" + p0a.getX() + ", " + p0a.getY() + ") до (" + p0b.getX() + ", " + p0b.getY() + ")");
        boolean result = foldLineSet.insideToMountain(createBox(p0a, p0b));
        Log.d(TAG, "Преобразование в горы: " + (result ? "выполнено" : "не выполнено"));
        return result;
    }

    @Override
    public boolean insideToValley(Point p0a, Point p0b) {
        Log.d(TAG, "Преобразование в долины в прямоугольнике: от (" + p0a.getX() + ", " + p0a.getY() + ") до (" + p0b.getX() + ", " + p0b.getY() + ")");
        boolean result = foldLineSet.insideToValley(createBox(p0a, p0b));
        Log.d(TAG, "Преобразование в долины: " + (result ? "выполнено" : "не выполнено"));
        return result;
    }

    @Override
    public boolean insideToEdge(Point p0a, Point p0b) {
        Log.d(TAG, "Преобразование в края в прямоугольнике: от (" + p0a.getX() + ", " + p0a.getY() + ") до (" + p0b.getX() + ", " + p0b.getY() + ")");
        boolean result = foldLineSet.insideToEdge(createBox(p0a, p0b));
        Log.d(TAG, "Преобразование в края: " + (result ? "выполнено" : "не выполнено"));
        return result;
    }

    @Override
    public boolean insideToAux(Point p0a, Point p0b) {
        Log.d(TAG, "Преобразование во вспомогательные линии (CYAN_3) внутри прямоугольника");
        return InsideToAux.apply(foldLineSet, createBox(p0a, p0b));
    }

    @Override
    public boolean insideToReplaceType(Point p0a, Point p0b, com.example.oriedita_core.origami.crease_pattern.CustomLineTypes from, com.example.oriedita_core.origami.crease_pattern.CustomLineTypes to) {
        Log.d(TAG, "Замена типа линий в прямоугольнике: от " + from + " к " + to);
        boolean result = foldLineSet.insideToReplaceType(createBox(p0a, p0b), from, to);
        Log.d(TAG, "Замена типа линий: " + (result ? "выполнена" : "не выполнена"));
        return result;
    }

    @Override
    public void setFoldLineAdditional(FoldLineAdditionalInputMode i) {
        Log.d(TAG, "Установка дополнительного режима линии сгиба: " + i);
        i_foldLine_additional = i;
    }


    @Override
    public void check1() {
        Log.d(TAG, "Выполнение проверки check1");
        Check1.apply(foldLineSet);
        Log.d(TAG, "Проверка check1 завершена");
        //In foldLineSet, check and set the funny fold line to the selected state.
    }

    @Override
    public void fix1() {
        Log.d(TAG, "Выполнение исправления fix1");
        while (true) {
            if (!Fix1.apply(foldLineSet)) break;
        }
        //foldLineSet.addsenbun  delsenbunを実施しているところでcheckを実施
        checkIfNecessary();
        Log.d(TAG, "Исправление fix1 завершено");
    }

    @Override
    public void set_i_check1(boolean i) {
        Log.d(TAG, "Установка check1: " + i);
        check1 = i;
        Log.d(TAG, "Check1 установлен: " + check1);
    }

    @Override
    public void check2() {
        Log.d(TAG, "Выполнение проверки check2");
        Check2.apply(foldLineSet);
        Log.d(TAG, "Проверка check2 завершена");
    }

    @Override
    public void fix2() {
        Log.d(TAG, "Выполнение исправления fix2");
        Fix2.apply(foldLineSet);
        //foldLineSet.addsenbun  delsenbunを実施しているところでcheckを実施
        checkIfNecessary();
        Log.d(TAG, "Исправление fix2 завершено");
    }

    private void checkIfNecessary() {
        Log.d(TAG, "Проверка необходимости выполнения проверок");
        if (check1) check1();
        if (check2) check2();
        if (check3) check3();
        if (check4) check4();
    }

    @Override
    public void check3() {
        Log.d(TAG, "Выполнение проверки check3");
        Check3.apply(foldLineSet);
        Log.d(TAG, "Проверка check3 завершена");
    }

    @Override
    public void check4() {
        Log.d(TAG, "Выполнение проверки check4");
        foldLineSet.check4();
        Log.d(TAG, "Проверка check4 завершена");
    }


    @Override
    public void lightenCheck4Color() {
        Log.d(TAG, "Осветление цвета check4 (уменьшение прозрачности)");
        if (applicationModel != null) {
            int newVal = Math.max(50, applicationModel.getCheck4ColorTransparency() - CHECK4_COLOR_TRANSPARENCY_INCREMENT);
            applicationModel.setCheck4ColorTransparency(newVal);
        }
    }

    @Override
    public void darkenCheck4Color() {
        Log.d(TAG, "Затемнение цвета check4 (увеличение прозрачности)");
        if (applicationModel != null) {
            int newVal = Math.min(250, applicationModel.getCheck4ColorTransparency() + CHECK4_COLOR_TRANSPARENCY_INCREMENT);
            applicationModel.setCheck4ColorTransparency(newVal);
        }
    }

    @Override
    public void organizeCircles() {
        Log.d(TAG, "Организация всех кругов");
        OrganizeCircles.apply(foldLineSet);
        Log.d(TAG, "Круги организованы");
    }

    @Override
    public double getSelectionDistance() {
        Log.d(TAG, "Получение расстояния выбора: " + selectionDistance);
        return selectionDistance;
    }

    @Override
    public void setData(PropertyChangeEvent e, ApplicationModel data) {
        Log.d(TAG, "Установка данных приложения");
        this.applicationModel = data;

        setGridInputAssist(data.getDisplayGridInputAssist());
        this.pointSize = data.getPointSize();
        setFoldLineDividingNumber(data.getFoldLineDividingNumber());
        setNumPolygonCorners(data.getNumPolygonCorners());
        setCheck4(data.getCheck4Enabled());
        setCustomCircleColor(data.getCircleCustomizedColor());

        if (e == null || "check4Enabled".equals(e.getPropertyName())) {
            if (data.getCheck4Enabled()) {
                check4();
            }
        } else if (camvTaskExecutor.isTaskRunning()) {
            camvTaskExecutor.stopTask();
        }

        grid.setData(data);
        Log.d(TAG, "Данные приложения установлены");
    }

    @Override
    public void setData(CanvasModel data) {
        Log.d(TAG, "Установка данных холста");
        this.canvasModel = data;
        setColor(data.calculateLineColor());
        setAuxLineColor(data.calculateAuxColor());
        setFoldLineAdditional(data.getFoldLineAdditionalInputMode());
        this.i_select_mode = data.getSelectionOperationMode();
        Log.d(TAG, "Данные холста установлены");
    }

    @Override
    public Point getCameraPosition() {
        Point position = camera.getCameraPosition();
        Log.d(TAG, "Получение позиции камеры: (" + position.getX() + ", " + position.getY() + ")");
        return position;
    }

    @Override
    public void selectConnected(Point p) {
        Log.d(TAG, "Выбор связанных элементов с точкой (" + p.getX() + ", " + p.getY() + ")");
        foldLineSet.selectProbablyConnected(p);
        Log.d(TAG, "Связанные элементы выбраны");
    }

    @Override
    public List<LineSegment> getLineStep() {
        return lineStep;
    }

    @Override
    public void setLineStepColor(LineSegment s, LineColor icol) {
        Log.d(TAG, "Установка цвета для сегмента линии в шаге");
        int index = lineStep.indexOf(s);
        lineStep.set(index, s.withColor(icol));
    }

    @Override
    public Path getLinePath() {
        return linePath;
    }

    @Override
    public Camera getCamera() {
        Log.d(TAG, "Получение камеры");
        return camera;
    }

    @Override
    public void setCamera(Camera cam0) {
        Log.d(TAG, "Установка камеры");
        camera.setCamera(cam0);
        calculateDecisionWidth();
        Log.d(TAG, "Камера установлена");
    }

    @Override
    public boolean getGridInputAssist() {
        return gridInputAssist;
    }

    // ------------------------------------
    @Override
    public void setGridInputAssist(boolean i) {
        Log.d(TAG, "Установка помощи ввода сетки: " + i);
        this.gridInputAssist = i;
        if (!gridInputAssist) {
            for (LineSegment candidate : lineCandidate) {
                candidate.deactivate();
            }
            Log.d(TAG, "Кандидаты линий деактивированы");
        }
        Log.d(TAG, "Помощь ввода сетки установлена");
    }

    private Polygon createBox(Point p0a, Point p0b) {
        Point p_a = camera.TV2object(new Point(p0a.getX(), p0a.getY()));
        Point p_b = camera.TV2object(new Point(p0a.getX(), p0b.getY()));
        Point p_c = camera.TV2object(new Point(p0b.getX(), p0b.getY()));
        Point p_d = camera.TV2object(new Point(p0b.getX(), p0a.getY()));

        return new Rectangle(p_a, p_b, p_c, p_d);
    }

    @Override
    public LineColor getLineColor() {
        Log.d(TAG, "Получение цвета линии: " + lineColor);
        return lineColor;
    }

    @Override
    public List<LineSegment> getLineCandidate() {
        List<LineSegment> result = new ArrayList<>(lineCandidate);
        Log.d(TAG, "Получение кандидатов линий: " + result.size() + " элементов");
        return result;
    }

    @Override
    public FoldLineSet getFoldLineSet() {
        Log.d(TAG, "Получение набора линий сгиба");
        return foldLineSet;
    }

    @Override
    public int getPointSize() {
        Log.d(TAG, "Получение размера точки: " + pointSize);
        return pointSize;
    }

    @Override
    public List<Circle> getCircleStep() {
        List<Circle> result = new ArrayList<>(circleStep);
        Log.d(TAG, "Получение шагов кругов: " + result.size() + " элементов");
        return result;
    }

    @Override
    public Grid getGrid() {
        Log.d(TAG, "Получение сетки");
        return grid;
    }

    @Override
    public FoldLineAdditionalInputMode getI_foldLine_additional() {
        Log.d(TAG, "Получение дополнительного режима линии сгиба: " + i_foldLine_additional);
        return i_foldLine_additional;
    }

    @Override
    public LineColor getAuxLineColor() {
        Log.d(TAG, "Получение цвета вспомогательных линий: " + auxLineColor);
        return auxLineColor;
    }

    @Override
    public void setAuxLineColor(LineColor i) {
        auxLineColor = i;
    }

    @Override
    public FoldLineSet getAuxLines() {
        Log.d(TAG, "Получение вспомогательных линий");
        return auxLines;
    }

    @Override
    public boolean isCheck1() {
        Log.d(TAG, "Проверка состояния check1: " + check1);
        return check1;
    }

    @Override
    public boolean isCheck2() {
        Log.d(TAG, "Проверка состояния check2: " + check2);
        return check2;
    }

    public void setCheck2(boolean i) {
        check2 = i;
    }

    @Override
    public boolean isCheck3() {
        Log.d(TAG, "Проверка состояния check3: " + check3);
        return check3;
    }

    @Override
    public boolean isCheck4() {
        Log.d(TAG, "Проверка состояния check4: " + check4);
        return check4;
    }

    @Override
    public void setCheck4(boolean i) {
        Log.d(TAG, "Установка check4: " + i);
        check4 = i;
        Log.d(TAG, "Check4 установлен: " + check4);
    }

    @Override
    public OperationFrame getOperationFrame() {
        Log.d(TAG, "Получение рамки операции");
        return this.operationFrame;
    }

    @Override
    public int getNumPolygonCorners() {
        Log.d(TAG, "Получение количества углов многоугольника: " + numPolygonCorners);
        return numPolygonCorners;
    }

    @Override
    public void setNumPolygonCorners(int i) {
        Log.d(TAG, "Установка количества углов многоугольника: " + i);
        numPolygonCorners = i;
        if (numPolygonCorners < 3) {
            foldLineDividingNumber = 3;
        }
        Log.d(TAG, "Количество углов многоугольника установлено: " + numPolygonCorners);
    }

    @Override
    public int getCustomCircleColor() {
        return customCircleColor;
    }

    public void setCustomCircleColor(int c0) {
        customCircleColor = c0;
    }

    @Override
    public CanvasModel.SelectionOperationMode getI_select_mode() {
        return i_select_mode;
    }

    @Override
    public int getFoldLineDividingNumber() {
        return foldLineDividingNumber;
    }

    @Override
    public void setFoldLineDividingNumber(int i) {
        Log.d(TAG, "Установка номера деления линии сгиба: " + i);
        foldLineDividingNumber = i;
        if (foldLineDividingNumber < 1) {
            foldLineDividingNumber = 1;
        }
        Log.d(TAG, "Номер деления линии сгиба установлен: " + foldLineDividingNumber);
    }

    @Override
    public TextWorker getTextWorker() {
        return textWorker;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
}