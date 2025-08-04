package com.example.oriedita.editor.export;

import oriedita.editor.Canvas;
import oriedita.editor.save.Save;
import oriedita.editor.export.api.FileExporter;

import java.io.File;
import java.io.IOException;

public class PngExporter implements FileExporter {
    private final Canvas canvas;

    public PngExporter(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public boolean supports(File file) {
        return file.getName().endsWith(".png");
    }

    @Override
    public void doExport(Save save, File file) throws IOException {
        canvas.writeImageFile(file);
    }

    @Override
    public String getName() {
        return "Portable Network Graphics";
    }

    @Override
    public String getExtension() {
        return ".png";
    }
}
