package com.example.oriedita_data.export.api;

import com.example.oriedita_data.save.Save;

import java.io.File;
import java.io.IOException;

public interface FileImporter {
    boolean supports(File filename);

    Save doImport(File file) throws IOException;
}
