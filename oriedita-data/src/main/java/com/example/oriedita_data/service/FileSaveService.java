package com.example.oriedita_data.service;

import com.example.oriedita_common.editor.exception.FileReadingException;
import com.example.oriedita_data.save.Save;
import java.io.File;

public interface FileSaveService {
    void openFile(File file) throws FileReadingException;

    void openFile();

    void importPref();

    void exportPref();

    void importFile();

    void exportFile();

    File selectSaveFile();

    File selectImportFile();

    File selectExportFile();

    Save readImportFile(File file) throws FileReadingException;

    Save readImportFile(File file, boolean askOnUnknownFormat) throws FileReadingException;

    void saveFile();

    void saveAsFile();

    boolean readBackgroundImageFromFile();

    void initAutoSave();

    void openFileInFE();
    void openFileInFE(File file);
}
