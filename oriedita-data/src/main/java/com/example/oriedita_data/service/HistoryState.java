package com.example.oriedita_data.service;

import com.example.oriedita_data.save.Save;

import java.beans.PropertyChangeListener;

public interface HistoryState {
    void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    void reset();

    boolean isEmpty();

    void record(Save s0);

    boolean canUndo();

    boolean canRedo();

    Save undo();

    Save redo();
}
