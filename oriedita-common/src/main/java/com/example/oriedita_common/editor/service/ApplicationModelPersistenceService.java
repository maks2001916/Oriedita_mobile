package com.example.oriedita_common.editor.service;

import java.util.zip.ZipInputStream;

public interface ApplicationModelPersistenceService {
    void init();

    void importApplicationModel(ZipInputStream zis);
}
