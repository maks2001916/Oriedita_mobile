package com.example.oriedita_ui.action;

import com.formdev.flatlaf.FlatLaf;
import oriedita.editor.Colors;
import oriedita.editor.FrameProvider;
import oriedita.editor.databinding.FoldedFigureModel;

import javax.swing.JColorChooser;
import java.awt.Color;
import java.awt.event.ActionEvent;

@ActionHandler(ActionType.backColorAction)
public class BackColorAction extends AbstractOrieditaAction {
    FrameProvider frameProvider;
    FoldedFigureModel foldedFigureModel;

    public BackColorAction() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //以下にやりたいことを書く
        Color backColor = JColorChooser.showDialog(frameProvider.get(), "B_col", FlatLaf.isLafDark() ? Colors.FIGURE_BACK_DARK : Colors.FIGURE_BACK);

        if (backColor != null) {
            foldedFigureModel.setBackColor(backColor);
        }
    }
}
