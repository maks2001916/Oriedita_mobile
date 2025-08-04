package com.example.oriedita.editor.factory;

import oriedita.editor.service.TaskExecutorService;
import oriedita.editor.service.impl.SingleTaskExecutorServiceImpl;

public class TaskFactory {
    public TaskExecutorService camvTaskExecutorService() {
        return new SingleTaskExecutorServiceImpl();
    }

    public TaskExecutorService foldingTaskExecutorService() {
        return new SingleTaskExecutorServiceImpl();
    }
}
