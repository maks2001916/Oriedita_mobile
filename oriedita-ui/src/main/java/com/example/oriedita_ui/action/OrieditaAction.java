package com.example.oriedita_ui.action;

public interface OrieditaAction extends javax.swing.Action {
    default boolean resetLineStep() {
        return true;
    }
}
