package com.example.oriedita_data.save;

import com.example.oriedita_data.save.fold.MapCircleAdapter;
import com.example.oriedita_data.save.fold.MapTextAdapter;
import com.example.oriedita_common.editor.text.Text;
import com.example.oriedita_core.origami.crease_pattern.elements.Circle;
import com.example.oriedita_core.origami.folding.fold.custom.CustomListField;
import com.example.oriedita_core.origami.folding.fold.model.FoldFile;

import java.util.List;

public class OrieditaFoldFile extends FoldFile {
    private static final String NS = "oriedita";
    private final CustomListField<Text, MapTextAdapter.TextFields> textFieldsCustomListField;
    private final CustomListField<Circle, MapCircleAdapter.CircleFields> circleFieldsCustomListField;

    public OrieditaFoldFile() {
        circleFieldsCustomListField = new CustomListField<>(NS, MapCircleAdapter.CircleFields.class, new MapCircleAdapter());
        textFieldsCustomListField = new CustomListField<>(NS, MapTextAdapter.TextFields.class, new MapTextAdapter());
    }

    /**
     * Retrieve circles from custom properties in embedded FoldFile.
     */
    public List<Circle> getCircles() {
        return circleFieldsCustomListField.getValue(getCustomPropertyMap());
    }

    /**
     * Set circles in custom properties of embedded FoldFile
     */
    public void setCircles(List<Circle> circles) {
        circleFieldsCustomListField.setValue(getCustomPropertyMap(), circles);
    }

    public List<Text> getTexts() {
        return textFieldsCustomListField.getValue(getCustomPropertyMap());
    }

    public void setTexts(List<Text> texts) {
        textFieldsCustomListField.setValue(getCustomPropertyMap(), texts);
    }

    public String getVersion() {
        return (String) getCustomProperty(NS, "version");
    }

    public void setVersion(String version) {
        setCustomProperty(NS, "version", version);
    }
}
