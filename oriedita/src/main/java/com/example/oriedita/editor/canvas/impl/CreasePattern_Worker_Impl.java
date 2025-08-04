package com.example.oriedita.editor.canvas.impl;

import android.util.Log;

import com.example.oriedita_data.canvas.CreasePattern_Worker;
import com.example.oriedita_data.canvas.OperationFrame;
import com.example.oriedita_data.canvas.TextWorker;
import com.example.oriedita_data.Colors;
import com.example.oriedita_data.databinding.AngleSystemModel;
import com.example.oriedita_data.databinding.ApplicationModel;
import com.example.oriedita_data.databinding.CanvasModel;
import com.example.oriedita_data.databinding.FileModel;
import com.example.oriedita_data.databinding.FoldedFigureModel;
import com.example.oriedita_data.databinding.GridModel;
import com.example.oriedita_data.databinding.SelectedTextModel;
import com.example.oriedita_data.drawing.Grid;
import com.example.oriedita_data.save.Save;
import com.example.oriedita_data.save.SaveProvider;
import com.example.oriedita_data.service.HistoryState;
import com.example.oriedita_common.editor.canvas.FoldLineAdditionalInputMode;
import com.example.oriedita_common.editor.canvas.LineStyle;
import com.example.oriedita_common.editor.canvas.MouseMode;
import com.example.oriedita_common.editor.drawing.tools.Camera;
import com.example.oriedita_common.editor.service.TaskExecutorService;
import com.example.oriedita_ui.drawing.tools.DrawingUtil;
import com.example.oriedita_core.origami.Epsilon;
import com.example.oriedita_core.origami.crease_pattern.CustomLineTypes;
import com.example.oriedita_core.origami.crease_pattern.FlatFoldabilityViolation;
import com.example.oriedita_core.origami.crease_pattern.FoldLineSet;
import com.example.oriedita_core.origami.crease_pattern.LineSegmentSet;
import com.example.oriedita_core.origami.crease_pattern.OritaCalc;
import com.example.oriedita_core.origami.crease_pattern.elements.Circle;
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;
import com.example.oriedita_core.origami.crease_pattern.elements.Rectangle;
import com.example.oriedita_core.origami.crease_pattern.elements.StraightLine;
import com.example.oriedita_core.origami.crease_pattern.worker.foldlineset.BranchTrim;
import com.example.oriedita_core.origami.crease_pattern.worker.foldlineset.Check1;
import com.example.oriedita_core.origami.crease_pattern.worker.foldlineset.Check2;
import com.example.oriedita_core.origami.crease_pattern.worker.foldlineset.Check3;
import com.example.oriedita_core.origami.crease_pattern.worker.foldlineset.Fix1;
import com.example.oriedita_core.origami.crease_pattern.worker.foldlineset.Fix2;
import com.example.oriedita_core.origami.crease_pattern.worker.foldlineset.InsideToAux;
import com.example.oriedita_core.origami.crease_pattern.worker.foldlineset.OrganizeCircles;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for holding the current creasepattern and drawing it.
 */
public class CreasePattern_Worker_Impl implements CreasePattern_Worker {
    // ------------
    private final int check4ColorTransparencyIncrement = 10;
    private final LineSegmentSet lineSegmentSet = new LineSegmentSet();
    private final Camera creasePatternCamera;
    private final TaskExecutorService camvTaskExecutor;
    private final CanvasModel canvasModel;
    private final ApplicationModel applicationModel;
    private final GridModel gridModel;
    private final FoldedFigureModel foldedFigureModel;

    private final TextWorker textWorker;
    private final FileModel fileModel;
    private final FoldLineSet foldLineSet;
    private final Grid grid = new Grid();
    private final HistoryState historyState;
    private final HistoryState auxHistoryState;
    private final FoldLineSet auxLines;
    private final List<LineSegment> lineStep = new ArrayList<>();
    private final Path linePath = new Path();
    private final List<Circle> circleStep = new ArrayList<>();
    private final List<LineSegment> lineCandidate = new ArrayList<>();
    private final Camera camera = new Camera();
    private final SelectedTextModel textModel;
    private final OperationFrame operationFrame = new OperationFrame();
    
    private double selectionDistance = 50.0;
    private int pointSize = 1;
    private LineColor lineColor = LineColor.BLACK_0;
    private LineColor auxLineColor = LineColor.ORANGE_4;
    private boolean gridInputAssist = false;
    private int customCircleColor;
    private FoldLineAdditionalInputMode i_foldLine_additional = FoldLineAdditionalInputMode.POLY_LINE_0;
    private int foldLineDividingNumber = 1;
    private int numPolygonCorners = 5;
    private String text_cp_setumei = "1/";
    private String s_title = "no title";
    private boolean check1 = false;
    private boolean check2 = false;
    private boolean check3 = false;
    private boolean check4 = false;
    private boolean isSelectionEmpty = true;
    private CanvasModel.SelectionOperationMode i_select_mode = CanvasModel.SelectionOperationMode.NORMAL_0;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

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

        lineColor = LineColor.BLACK_0;
        text_cp_setumei = "1/";
        s_title = "no title";
    }

    // TODO: Добавить остальные методы реализации
}
