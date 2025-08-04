package com.example.oriedita.editor.factory;

import oriedita.editor.service.HistoryState;
import oriedita.editor.service.impl.DequeHistoryState;

public class HistoryStateFactory {
    public HistoryState normalHistoryState() {
        return new DequeHistoryState();
    }

    public HistoryState auxHistoryState() {
        return new DequeHistoryState();
    }
}
