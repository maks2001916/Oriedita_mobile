package com.example.oriedita_ui.action;

import oriedita.editor.FrameProvider;
import oriedita.editor.databinding.FoldedFigureModel;

import javax.swing.JColorChooser;
import java.awt.Color;
import java.awt.event.ActionEvent;

@ActionHandler(ActionType.lineColorAction)
public class LineColorAction extends AbstractOrieditaAction {
    FrameProvider frameProvider;
    FoldedFigureModel foldedFigureModel;

    public LineColorAction() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //以下にやりたいことを書く

        Color lineColor = JColorChooser.showDialog(frameProvider.get(), "L_col", Color.black);
        if (lineColor != null) {
            foldedFigureModel.setLineColor(lineColor);
        }
    }
}
