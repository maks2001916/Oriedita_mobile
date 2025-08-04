package com.example.oriedita_ui.action;

import com.example.oriedita_data.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.databinding.InternalDivisionRatioModel;

import java.awt.event.ActionEvent;

@ActionHandler(ActionType.drawLineSegmentInternalDivisionRatioAction)
public class DrawLineSegmentInternalDivisionRatioAction extends AbstractOrieditaAction{
    CanvasModel canvasModel;
    InternalDivisionRatioModel internalDivisionRatioModel;

    CreasePattern_Worker mainCreasePatternWorker;

    public DrawLineSegmentInternalDivisionRatioAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        internalDivisionRatioModel.commit();

        canvasModel.setMouseMode(MouseMode.LINE_SEGMENT_RATIO_SET_28);
        canvasModel.setMouseModeAfterColorSelection(MouseMode.LINE_SEGMENT_RATIO_SET_28);

        mainCreasePatternWorker.unselect_all(false);
    }
}
