package com.example.oriedita_ui.action;

import oriedita.editor.service.TaskExecutorService;

import java.awt.event.ActionEvent;

@ActionHandler(ActionType.haltAction)
public class HaltAction extends AbstractOrieditaAction {
    TaskExecutorService camvTaskExecutor;
    TaskExecutorService foldingTaskExecutor;

    public HaltAction() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        camvTaskExecutor.stopTask();
        foldingTaskExecutor.stopTask();
    }
}
