package com.example.oriedita_data.save;


import oriedita.editor.text.Text;

import java.util.List;

public interface TextSave {

    void addText(Text text);

    List<Text> getTexts();

    void setTexts(List<Text> texts);
}
