package com.example.oriedita_ui.action;

import com.example.oriedita_data.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.FoldLineAdditionalInputMode;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;

import java.awt.event.ActionEvent;

@ActionHandler(ActionType.voronoiAction)
public class VoronoiAction extends AbstractOrieditaAction {

    CanvasModel canvasModel;

    CreasePattern_Worker mainCreasePatternWorker;

    public VoronoiAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setFoldLineAdditionalInputMode(FoldLineAdditionalInputMode.POLY_LINE_0);
        canvasModel.setMouseMode(MouseMode.VORONOI_CREATE_62);
        canvasModel.setMouseModeAfterColorSelection(MouseMode.VORONOI_CREATE_62);

        mainCreasePatternWorker.unselect_all(false);
    }
}
