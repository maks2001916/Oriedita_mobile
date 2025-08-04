package com.example.oriedita_ui.action;

import com.example.oriedita_data.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

@ActionHandler(ActionType.operationFrameSelectAction)
public class OperationFrameSelectAction extends AbstractOrieditaAction{
    CanvasModel canvasModel;

    CreasePattern_Worker mainCreasePatternWorker;

    public OperationFrameSelectAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.POLY_LINE_0);
        canvasModel.setMouseMode(MouseMode.OPERATION_FRAME_CREATE_61);
        canvasModel.setMouseModeAfterColorSelection(MouseMode.DRAW_CREASE_FREE_1);

        mainCreasePatternWorker.unselect_all(false);
    }
}
