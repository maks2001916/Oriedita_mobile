package com.example.oriedita_data.databinding;

import android.util.Log;
import com.example.oriedita_common.editor.text.Text;
import com.google.gson.annotations.Expose;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SelectedTextModel {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private Text selectedText;

    private boolean selected;

    private boolean dirty;

    @Inject
    public SelectedTextModel() {
        reset();
    }

    public Text getSelectedText() {
        return selectedText;
    }

    public void setSelectedText(Text selectedText) {
        Text oldText = this.selectedText;
        this.selectedText = selectedText;
        pcs.firePropertyChange("selectedText", oldText, selectedText);
    }

    public void markDirty() {
        setDirty(true);
    }

    public void markClean() {
        setDirty(false);
    }

    @Expose(serialize = false, deserialize = false)
    public void setDirty(boolean dirty) {
        boolean oldDirty = this.dirty;
        if (dirty && !oldDirty) {
            Log.i("TAG","text dirty");
        }
        if (!dirty && oldDirty) {
            Log.i("TAG","text clean");
        }
        this.dirty = dirty;
        pcs.firePropertyChange("dirty", !dirty, dirty); // should always trigger a PropertyChangeEvent
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isSelected(Text t) {
        return isSelected() && getSelectedText() == t;
    }

    public void setSelected(boolean selected) {
        boolean oldSelected = this.selected;
        this.selected = selected;
        pcs.firePropertyChange("selected", oldSelected, selected);
    }

    public void reset() {
        setSelected(false);
        setSelectedText(null);
        dirty = false;
        this.pcs.firePropertyChange(null, null, null);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    @Expose(serialize = false, deserialize = false)
    public boolean isDirty() {
        return dirty;
    }
}
