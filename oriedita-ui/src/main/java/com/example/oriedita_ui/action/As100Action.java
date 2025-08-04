package com.example.oriedita_ui.action;

import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.service.TaskService;
import origami.folding.FoldedFigure;

import java.awt.event.ActionEvent;

@ActionHandler(ActionType.As100Action)
public class As100Action extends AbstractOrieditaAction {
    TaskService taskService;
    FoldedFiguresList foldedFiguresList;

    public As100Action() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FoldedFigure_Drawer selectedFigure = foldedFiguresList.getActiveItem();
        if (selectedFigure != null && selectedFigure.getFoldedFigure().findAnotherOverlapValid) {
            selectedFigure.getFoldedFigure().estimationOrder = FoldedFigure.EstimationOrder.ORDER_6;

            taskService.executeFoldingEstimateSave100Task();
        }
    }
}
