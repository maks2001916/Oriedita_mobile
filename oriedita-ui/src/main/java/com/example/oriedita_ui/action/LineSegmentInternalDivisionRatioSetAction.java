package com.example.oriedita_ui.action;

import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.InternalDivisionRatioModel;

import java.awt.event.ActionEvent;

@ActionHandler(ActionType.lineSegmentInternalDivisionRatioSetAction)
public class LineSegmentInternalDivisionRatioSetAction extends AbstractOrieditaAction{
    InternalDivisionRatioModel internalDivisionRatioModel;
    CanvasModel canvasModel;

    public LineSegmentInternalDivisionRatioSetAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        internalDivisionRatioModel.commit();

        canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_RATIO_SET_28);
        canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_RATIO_SET_28);
    }
}
