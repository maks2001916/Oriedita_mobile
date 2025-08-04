package com.example.oriedita.editor.handler;

import org.tinylog.Logger;
import oriedita.editor.canvas.MouseMode;

@Handles(MouseMode.SELECT_LASSO_74)
public class MouseHandlerSelectLasso extends BaseMouseHandlerLasso{
    public MouseHandlerSelectLasso(){}

    @Override
    protected void performAction() {
        Logger.debug("selected");
        d.getFoldLineSet().select_lasso(d.getLinePath(), "select");
    }
}
