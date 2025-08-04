package com.example.oriedita_ui.action;

import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;
import oriedita.editor.service.FoldingService;

import java.awt.event.ActionEvent;

@ActionHandler(ActionType.anotherSolutionAction)
public class AnotherSolutionAction extends AbstractOrieditaAction {
    FoldedFiguresList foldedFiguresList;

    FoldingService foldingService;

    public AnotherSolutionAction() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FoldedFigure_Drawer selectedItem = foldedFiguresList.getActiveItem();
        if (selectedItem != null) {
            foldingService.foldAnother(selectedItem);
        }
    }
}
