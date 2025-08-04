package com.example.oriedita_ui.databinding;

import oriedita.editor.drawing.FoldedFigure_Drawer;

import javax.swing.DefaultComboBoxModel;

public class FoldedFiguresList extends DefaultComboBoxModel<FoldedFigure_Drawer> {
    public FoldedFiguresList() {
    }

    public FoldedFigure_Drawer getActiveItem() {
        Object selectedItem = getSelectedItem();

        if (selectedItem != null) {
            return (FoldedFigure_Drawer) selectedItem;
        }

        return null;
    }

    public FoldedFigure_Drawer[] getItems() {
        FoldedFigure_Drawer[] list = new FoldedFigure_Drawer[getSize()];
        for (int i = 0; i < getSize(); i++) {
            list[i] = getElementAt(i);
        }

        return list;
    }
}
