package com.example.oriedita.editor.service.impl;

import oriedita.editor.action.ActionType;
import oriedita.editor.action.OrieditaAction;
import oriedita.editor.action.ActionService;

import java.util.HashMap;
import java.util.Map;

public class ActionServiceImpl implements ActionService {
    private final Map<ActionType, OrieditaAction> registeredActions;

    public ActionServiceImpl() {
        registeredActions = new HashMap<>();
    }

    @Override
    public void registerAction(ActionType actionType, OrieditaAction orieditaAction) {
        registeredActions.put(actionType, orieditaAction);
    }

    @Override
    public Map<ActionType, OrieditaAction> getAllRegisteredActions() {
        return registeredActions;
    }
}
