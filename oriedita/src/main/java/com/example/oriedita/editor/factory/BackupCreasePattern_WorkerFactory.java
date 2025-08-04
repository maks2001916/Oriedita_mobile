package com.example.oriedita.editor.factory;

import com.example.oriedita_data.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.TextWorker;
import oriedita.editor.canvas.impl.CreasePattern_Worker_Impl;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FileModel;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.databinding.SelectedTextModel;
import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.service.HistoryState;
import oriedita.editor.service.TaskExecutorService;
import origami.crease_pattern.FoldLineSet;

public class BackupCreasePattern_WorkerFactory {
                                                                  CanvasModel canvasModel,
                                                                  ApplicationModel applicationModel,
                                                                  GridModel gridModel,
                                                                  FoldedFigureModel foldedFigureModel,
                                                                  FileModel fileModel,
                                                                  TextWorker textWorker,
                                                                  SelectedTextModel textModel) {
        return new CreasePattern_Worker_Impl(creasePatternCamera, historyState, auxHistoryState, auxLines, foldLineSet, camvTaskExecutor, canvasModel, applicationModel, gridModel, foldedFigureModel, fileModel, textWorker, textModel);
    }

                                                                CanvasModel canvasModel,
                                                                ApplicationModel applicationModel,
                                                                GridModel gridModel,
                                                                FoldedFigureModel foldedFigureModel,
                                                                FileModel fileModel,
                                                                TextWorker textWorker,
                                                                SelectedTextModel textModel) {

        return new CreasePattern_Worker_Impl(creasePatternCamera, historyState, auxHistoryState, auxLines, foldLineSet, camvTaskExecutor, canvasModel, applicationModel, gridModel, foldedFigureModel, fileModel, textWorker, textModel);
    }
}
