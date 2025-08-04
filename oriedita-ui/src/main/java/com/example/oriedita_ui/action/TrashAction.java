package com.example.oriedita_ui.action;

import oriedita.editor.databinding.FoldedFiguresList;

import java.awt.event.ActionEvent;

@ActionHandler(ActionType.trashAction)
public class TrashAction extends AbstractOrieditaAction {
    FoldedFiguresList foldedFiguresList;

    public TrashAction() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (foldedFiguresList.getSize() == 0) {
            return;
        }

        Object selectedItem = foldedFiguresList.getSelectedItem();

        if (selectedItem == null) {
            selectedItem = foldedFiguresList.getElementAt(0);
        }

        foldedFiguresList.removeElement(selectedItem);
    }
}
