package com.example.oriedita_data.databinding;


import android.util.Log;
import javax.inject.Inject;
import javax.inject.Singleton;
import com.example.oriedita_core.origami.crease_pattern.OritaCalc;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


@Singleton
public class CameraModel {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private double rotation;
    private double scale;

    @Inject
    public CameraModel() {
        reset();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public void reset() {
        scale = 1.0;
        rotation = 0.0;

        this.pcs.firePropertyChange(null, null, null);
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        double oldRotation = this.rotation;
        this.rotation = OritaCalc.angle_between_m180_180(rotation);
        this.pcs.firePropertyChange("rotation", oldRotation, this.rotation);
    }

    public void increaseRotation() {
        setRotation(rotation + 11.25);
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        double oldScale = this.scale;
        this.scale = Math.max(scale, 0.00001);
        this.pcs.firePropertyChange("scale", oldScale, scale);
    }

    public void zoomIn(double zoomSpeed) {
        zoomBy(-1, zoomSpeed);
        Log.i("TAG", "zoom in");
        Log.i("TAG", "зум +");
    }

    public void zoomBy(double value, double zoomSpeed) {
        setScale(getScaleForZoomBy(value, zoomSpeed));
    }

    public double getScaleForZoomBy(double value, double zoomSpeed) {
        return getScaleForZoomBy(value, zoomSpeed, scale);
    }

    public double getScaleForZoomBy(double value, double zoomSpeed, double initialScale) {
        double zoomBase = 1 + zoomSpeed/10;
        if (value > 0) {
            return (initialScale / Math.pow(zoomBase, value));
        } else if (value < 0) {
            return (initialScale * Math.pow(zoomBase, Math.abs(value)));
        }
        return initialScale;
    }

    public void zoomOut(double zoomSpeed) {
        zoomBy(1, zoomSpeed);
    }

    public void decreaseRotation() {
        setRotation(rotation - 11.25);
    }
}
