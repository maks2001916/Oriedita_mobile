package com.example.oriedita_ui.action;

import oriedita.editor.AnimationDurations;
import oriedita.editor.Animations;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.databinding.CameraModel;
import oriedita.editor.service.AnimationService;

public abstract class AbstractCreasePatternZoomAction extends AbstractOrieditaAction{
    CameraModel creasePatternCameraModel;

    AnimationService animationService;

    ApplicationModel applicationModel;

    protected void zoom(double value) {
        animationService.animate(Animations.ZOOM_CP, creasePatternCameraModel::setScale,
                creasePatternCameraModel::getScale, s -> creasePatternCameraModel.getScaleForZoomBy(value, applicationModel.getZoomSpeed(), s),
                AnimationDurations.ZOOM);
    }
}
