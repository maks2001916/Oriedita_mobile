package oriedita.editor.export;

import fold.io.CreasePatternReader;
import fold.model.Edge;
import fold.model.FoldFile;
import jakarta.enterprise.context.ApplicationScoped;
import oriedita.editor.export.api.FileImporter;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import origami.crease_pattern.elements.LineSegment;
import origami.crease_pattern.elements.Point;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@ApplicationScoped
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
