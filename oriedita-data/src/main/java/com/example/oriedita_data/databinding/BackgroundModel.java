package com.example.oriedita_data.databinding;

import com.example.oriedita_core.origami.crease_pattern.elements.Rectangle;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;
import com.example.oriedita_core.origami.crease_pattern.elements.Polygon;

import android.graphics.drawable.Drawable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.inject.Singleton;
import javax.inject.Inject;

@Singleton
public class BackgroundModel {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean displayBackground;
    private boolean lockBackground;
    private Drawable backgroundImage;
    private Rectangle backgroundPosition;

    @Inject
    public BackgroundModel() {
        reset();
    }

    public Drawable getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Drawable backgroundImage) {
        Drawable oldBackgroundImage = this.backgroundImage;
        this.backgroundImage = backgroundImage;
        this.pcs.firePropertyChange("backgroundImage", oldBackgroundImage, backgroundImage);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public void reset() {
        backgroundImage = null;
        displayBackground = false;
        lockBackground = false;

        backgroundPosition = new Rectangle(new Point(0.0, 0.0),
                new Point(1.0, 1.0),
                new Point(120.0, 120.0),
                new Point(121.0, 121.0));

        this.pcs.firePropertyChange(null, null, null);
    }

    public boolean isDisplayBackground() {
        return displayBackground;
    }

    public void setDisplayBackground(boolean displayBackground) {
        boolean oldDisplayBackground = this.displayBackground;
        this.displayBackground = displayBackground;
        this.pcs.firePropertyChange("displayBackground", oldDisplayBackground, displayBackground);
    }

    public boolean isLockBackground() {
        return lockBackground;
    }

    public void setLockBackground(boolean lockBackground) {
        boolean oldLockBackground = this.lockBackground;
        this.lockBackground = lockBackground;
        this.pcs.firePropertyChange("lockBackground", oldLockBackground, lockBackground);
    }

    public Rectangle getBackgroundPosition() {
        return backgroundPosition;
    }

    public void setBackgroundPosition(Rectangle backgroundPosition) {
        Polygon oldBackgroundPosition = this.backgroundPosition;
        this.backgroundPosition = backgroundPosition;
        this.pcs.firePropertyChange("backgroundPosition", oldBackgroundPosition, backgroundPosition);
    }
}
