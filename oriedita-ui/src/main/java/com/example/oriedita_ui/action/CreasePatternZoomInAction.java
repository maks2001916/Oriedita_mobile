package com.example.oriedita_ui.action;


import java.awt.event.ActionEvent;

@ActionHandler(ActionType.creasePatternZoomInAction)
public class CreasePatternZoomInAction extends AbstractCreasePatternZoomAction{
    @Override
    public void actionPerformed(ActionEvent e) {
        zoom(-1);
    }
}
