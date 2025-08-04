package com.example.oriedita.editor.handler;


import com.example.oriedita_data.canvas.CreasePattern_Worker;

import java.util.EnumSet;

public abstract class BaseMouseHandler implements MouseModeHandler {
    protected CreasePattern_Worker d;

    public BaseMouseHandler() {
    }

    @Override
    public EnumSet<Feature> getSubscribedFeatures() {
        return EnumSet.of(Feature.BUTTON_1);
    }
}
