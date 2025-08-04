package com.example.oriedita.editor.factory;

import oriedita.editor.databinding.CameraModel;
import oriedita.editor.drawing.tools.Camera;

public class CameraFactory {
    public static Camera creasePatternCamera(CameraModel cameraModel) {
        Camera creasePatternCamera = new Camera();

        cameraModel.addPropertyChangeListener(e -> {
            creasePatternCamera.setCameraAngle(cameraModel.getRotation());
            creasePatternCamera.setCameraZoomX(cameraModel.getScale());
            creasePatternCamera.setCameraZoomY(cameraModel.getScale());
        });


        return creasePatternCamera;
    }

}
