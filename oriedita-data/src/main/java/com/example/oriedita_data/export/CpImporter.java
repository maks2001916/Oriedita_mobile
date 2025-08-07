package com.example.oriedita_data.export;

import fold.io.CreasePatternReader;
import fold.model.Edge;
import fold.model.FoldFile;
import com.example.oriedita_data.export.api.FileImporter;
import com.example.oriedita_data.save.Save;
import com.example.oriedita_data.save.SaveProvider;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CpImporter implements FileImporter {
    @Override
    public boolean supports(File filename) {
        return filename.getName().endsWith(".cp");
    }

    public Save doImport(InputStream is) throws IOException {
        Save save = SaveProvider.createInstance();

        CreasePatternReader creasePatternReader = new CreasePatternReader(is);

        FoldFile foldFile = creasePatternReader.read();

        for (Edge edge : foldFile.getRootFrame().getEdges()) {
            save.addLineSegment(new LineSegment(new Point(edge.getStart().getX(), edge.getStart().getY()), new Point(edge.getEnd().getX(), edge.getEnd().getY()), FoldImporter.getColor(edge.getAssignment())));
        }

        return save;
    }

    @Override
    public Save doImport(File file) throws IOException {
        try (FileInputStream is = new FileInputStream(file)) {
            return doImport(is);
        }
    }
}
