package com.example.oriedita.editor.handler;

import org.tinylog.Logger;
import oriedita.editor.canvas.MouseMode;

@Handles(MouseMode.UNSELECT_LASSO_75)
public class MouseHandlerUnselectLasso extends BaseMouseHandlerLasso{
    public MouseHandlerUnselectLasso(){}

    @Override
    protected void performAction() {
        Logger.debug("unselected");
        d.getFoldLineSet().select_lasso(d.getLinePath(), "unselect");
    }
}
