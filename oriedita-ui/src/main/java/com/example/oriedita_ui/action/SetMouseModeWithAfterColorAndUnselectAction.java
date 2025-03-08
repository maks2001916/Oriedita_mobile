package com.example.oriedita_ui.action;

import jakarta.inject.Named;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.service.ButtonService;

import java.awt.event.ActionEvent;

public class SetMouseModeWithAfterColorAndUnselectAction extends AbstractOrieditaAction {
    private final CanvasModel canvasModel;
    private final CreasePattern_Worker mainCreasePatternWorker;
    private final ButtonService buttonService;
    private final MouseMode mouseMode;

    public SetMouseModeWithAfterColorAndUnselectAction(CanvasModel canvasModel,
                                                       @Named("mainCreasePattern_Worker") CreasePattern_Worker mainCreasePatternWorker,
                                                       ButtonService buttonService,
                                                       MouseMode mouseMode){
        this.canvasModel = canvasModel;
        this.mainCreasePatternWorker = mainCreasePatternWorker;
        this.buttonService = buttonService;
        this.mouseMode = mouseMode;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        canvasModel.setMouseMode(mouseMode);
        canvasModel.setMouseModeAfterColorSelection(mouseMode);
        mainCreasePatternWorker.unselect_all();
        buttonService.Button_shared_operation();
    }
}
