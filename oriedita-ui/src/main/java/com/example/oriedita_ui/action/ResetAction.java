package com.example.oriedita_ui.action;

import com.example.oriedita_data.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.FoldedFiguresList;

import java.awt.event.ActionEvent;

@ActionHandler(ActionType.resetAction)
public class ResetAction extends AbstractOrieditaAction{
    FoldedFiguresList foldedFiguresList;
    CanvasModel canvasModel;
    CreasePattern_Worker mainCreasePatternWorker;
    CameraModel creasePatternCameraModel;

    public ResetAction() {

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        mainCreasePatternWorker.clearCreasePattern();
        creasePatternCameraModel.reset();
        foldedFiguresList.removeAllElements();

        canvasModel.setMouseMode(MouseMode.FOLDABLE_LINE_DRAW_71);

        mainCreasePatternWorker.record();
        mainCreasePatternWorker.auxRecord();
    }
}
