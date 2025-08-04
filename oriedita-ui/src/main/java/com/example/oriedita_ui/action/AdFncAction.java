package com.example.oriedita_ui.action;

import oriedita.editor.FrameProvider;
import oriedita.editor.databinding.CanvasModel;
import oriedita.editor.service.ButtonService;
import oriedita.editor.swing.dialog.OpenFrame;

import java.awt.event.ActionEvent;

@ActionHandler(ActionType.ad_fncAction)
public class AdFncAction extends AbstractOrieditaAction{
    OpenFrame openFrame;

    CanvasModel canvasModel;

    FrameProvider frameProvider;

    ButtonService buttonService;

    @Override
    public void actionPerformed(ActionEvent e) {
        openFrame = new OpenFrame("additionalFrame", frameProvider.get(), buttonService);

        openFrame.setData(null, canvasModel);
        openFrame.setLocationRelativeTo(frameProvider.get());
        openFrame.setVisible(true);
    }
}
