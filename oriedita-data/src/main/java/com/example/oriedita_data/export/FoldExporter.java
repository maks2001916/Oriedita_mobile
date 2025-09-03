package com.example.oriedita_data.export;

import android.util.Log;
import fold.io.CustomFoldWriter;
import fold.model.FoldFile;
import fold.model.FoldEdgeAssignment;
import fold.model.Edge;
import fold.model.Face;
import fold.model.Vertex;
import com.example.oriedita_data.export.api.FileExporter;
import com.example.oriedita_data.save.OrieditaFoldFile;
import com.example.oriedita_data.save.Save;
import com.example.oriedita_common.editor.tools.ResourceUtil;
import com.example.oriedita_core.origami.crease_pattern.LineSegmentSet;
import com.example.oriedita_core.origami.crease_pattern.worker.PointSet;
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;
import com.example.oriedita_core.origami.crease_pattern.worker.WireFrame_Worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FoldExporter - экспортер файлов .fold для Android
 * 
 * Этот класс отвечает за экспорт файлов оригами в формате .fold.
 * Поддерживает сохранение граней, цветов линий, окружностей и текста
 * в файлы формата FOLD, используемого для представления складных структур.
 */
public class FoldExporter implements FileExporter {
    
    private static final String TAG = "FoldExporter";

    /**
     * Конструктор по умолчанию
     */
    public FoldExporter() {
    }

    /**
     * Преобразует цвет линии оригами в тип сгиба FOLD
     * 
     * @param lineColor цвет линии оригами
     * @return соответствующий тип сгиба FOLD
     */
    private FoldEdgeAssignment getAssignment(LineColor lineColor) {
        switch (lineColor) {
            case BLACK_0:
                return FoldEdgeAssignment.BORDER; // Граница
            case RED_1:
                return FoldEdgeAssignment.MOUNTAIN_FOLD; // Горная линия
            case BLUE_2:
                return FoldEdgeAssignment.VALLEY_FOLD;  // Долинная линия
            case CYAN_3:
            case ORANGE_4:
            case MAGENTA_5:
            case GREEN_6:
            case YELLOW_7:
            case PURPLE_8:
            case OTHER_9:
                return FoldEdgeAssignment.FLAT_FOLD; // Плоская линия
            default:
                return FoldEdgeAssignment.UNASSIGNED;  // Не назначен
        }
    }

    /**
     * Экспортирует файл .fold с использованием набора сегментов линий
     * 
     * @param save объект Save с данными оригами
     * @param lineSegmentSet набор сегментов линий
     * @param file файл для экспорта
     * @throws InterruptedException при прерывании операции
     * @throws IOException при ошибках записи файла
     */
    private void exportFile(Save save, LineSegmentSet lineSegmentSet, File file) throws InterruptedException, IOException {
        Log.i(TAG, "Начинаем экспорт файла .fold: " + file.getName());
        
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            CustomFoldWriter<FoldFile> foldFileCustomFoldWriter = new CustomFoldWriter<>(fileOutputStream);
            OrieditaFoldFile foldFile = toFoldSave(save, lineSegmentSet);
            foldFileCustomFoldWriter.write(foldFile);
            Log.i(TAG, "Файл .fold успешно экспортирован");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при экспорте файла .fold", e);
            throw e;
        }
    }

    /**
     * Преобразует объект Save в объект OrieditaFoldFile
     * 
     * @param save объект Save с данными оригами
     * @return объект OrieditaFoldFile
     * @throws InterruptedException при прерывании операции
     */
    public OrieditaFoldFile toFoldSave(Save save) throws InterruptedException {
        LineSegmentSet s = new LineSegmentSet();
        s.setSave(save);
        return toFoldSave(save, s);
    }

    /**
     * Преобразует объект Save в объект OrieditaFoldFile с использованием набора сегментов линий
     * 
     * @param save объект Save с данными оригами
     * @param lineSegmentSet набор сегментов линий
     * @return объект OrieditaFoldFile
     * @throws InterruptedException при прерывании операции
     */
    public OrieditaFoldFile toFoldSave(Save save, LineSegmentSet lineSegmentSet) throws InterruptedException {
        Log.i(TAG, "Преобразуем Save в FOLD формат");
        
        // Создаем рабочий объект для обработки проволочной модели
        WireFrame_Worker wireFrame_worker = new WireFrame_Worker(3.0);
        wireFrame_worker.setLineSegmentSetWithoutFaceOccurence(lineSegmentSet);
        PointSet pointSet = wireFrame_worker.get();
        boolean includeFaces = pointSet.calculateFaces();

        OrieditaFoldFile foldFile = new OrieditaFoldFile();
        foldFile.setCreator("oriedita");
        fold.model.FoldFrame rootFrame = foldFile.getRootFrame();

        Log.i(TAG, "Обрабатываем " + pointSet.getNumPoints() + " точек");

        // Добавляем все вершины
        for (int i = 1; i <= pointSet.getNumPoints(); i++) {
            Vertex vertex = new Vertex();
            vertex.setX(pointSet.getPoint(i).getX());
            vertex.setY(pointSet.getPoint(i).getY());
            rootFrame.getVertices().add(vertex);
        }

        Log.i(TAG, "Обрабатываем " + pointSet.getNumLines() + " линий");

        // Добавляем все грани
        for (int i = 1; i <= pointSet.getNumLines(); i++) {
            Edge edge = new Edge();
            edge.setAssignment(getAssignment(pointSet.getColor(i)));
            edge.setFoldAngle(getFoldAngle(pointSet.getColor(i)));
            Vertex startVertex = rootFrame.getVertices().get(pointSet.getBegin(i) - 1);
            Vertex endVertex = rootFrame.getVertices().get(pointSet.getEnd(i) - 1);

            edge.setStart(startVertex);
            edge.setEnd(endVertex);

            rootFrame.getEdges().add(edge);
        }

        // Добавляем грани, если они были вычислены
        if (includeFaces) {
            Log.i(TAG, "Обрабатываем " + pointSet.getNumFaces() + " граней");
            
            for (int i = 1; i <= pointSet.getNumFaces(); i++) {
                var pface = pointSet.getFace(i);
                var face = new Face();

                var faceVertices = new ArrayList<Vertex>();
                var faceEdges = new ArrayList<Edge>();
                var vertexFirst = rootFrame.getVertices().get(pface.getPointId(1) - 1);
                var vertexLast = rootFrame.getVertices().get(pface.getPointId(pface.getNumPoints()) - 1);
                faceVertices.add(vertexFirst);
                faceEdges.add(findEdge(vertexFirst, vertexLast, rootFrame.getEdges()));
                
                for (var j = 2; j <= pface.getNumPoints(); j++) {
                    var currentVertex = rootFrame.getVertices().get(pface.getPointId(j) - 1);
                    var previousVertex = rootFrame.getVertices().get(pface.getPointId(j - 1) - 1);
                    faceVertices.add(currentVertex);
                    faceEdges.add(findEdge(currentVertex, previousVertex, rootFrame.getEdges()));
                }
                face.setVertices(faceVertices);
                face.setEdges(faceEdges);

                rootFrame.getFaces().add(face);
            }
        }

        // Добавляем окружности и тексты из исходного Save
        foldFile.setCircles(save.getCircles());
        foldFile.setTexts(save.getTexts());
        foldFile.setVersion(ResourceUtil.getVersionFromManifest());

        Log.i(TAG, "Добавлено " + save.getCircles().size() + " окружностей и " + save.getTexts().size() + " текстовых элементов");

        return foldFile;
    }

    /**
     * Находит грань между двумя вершинами
     * 
     * @param v1 первая вершина
     * @param v2 вторая вершина
     * @param edges список всех граней
     * @return найденная грань
     * @throws IllegalStateException если грань не найдена
     */
    private Edge findEdge(Vertex v1, Vertex v2, List<Edge> edges) {
        var foundEdge = edges.stream().filter(e -> (e.getStart() == v1 && e.getEnd() == v2) || e.getStart() == v2 && e.getEnd() == v1).findFirst();

        if (foundEdge.isPresent()) {
            return foundEdge.get();
        }

        throw new IllegalStateException("Грань в лице не найдена");
    }

    /**
     * Получает угол сгиба для цвета линии
     * 
     * @param color цвет линии
     * @return угол сгиба в градусах
     */
    private double getFoldAngle(LineColor color) {
        switch (color) {
            case BLUE_2:
                return 180;   // Долинная линия - 180 градусов
            case RED_1:
                return -180;   // Горная линия - -180 градусов
            default:
                return 0;         // По умолчанию - 0 градусов
        }
    }

    /**
     * Проверяет, поддерживается ли файл для экспорта
     * @param filename файл для проверки
     * @return true если файл имеет расширение .fold
     */
    @Override
    public boolean supports(File filename) {
        return filename.getName().endsWith(".fold");
    }

    /**
     * Экспортирует объект Save в файл .fold
     * 
     * @param save объект Save с данными оригами
     * @param file файл для экспорта
     * @throws IOException при ошибках записи файла
     */
    @Override
    public void doExport(Save save, File file) throws IOException {
        Log.i(TAG, "Начинаем экспорт в формат FOLD");
        
        try {
            LineSegmentSet s = new LineSegmentSet();
            s.setSave(save);
            
            // Добавляем пустую линию, если нет сегментов линий
            if (s.getNumLineSegments() == 0) {
                Log.w(TAG, "Нет сегментов линий, добавляем пустую линию");
                s.addLine(new Point(0, 0), new Point(0, 0), LineColor.BLACK_0);
            }
            
            exportFile(save, s, file);
        } catch (InterruptedException e) {
            Log.e(TAG, "Операция экспорта была прервана", e);
            throw new IOException("Операция экспорта была прервана", e);
        }
    }

    /**
     * Возвращает название формата экспорта
     * @return название формата
     */
    @Override
    public String getName() {
        return "FOLD";
    }

    /**
     * Возвращает расширение файла
     * @return расширение файла
     */
    @Override
    public String getExtension() {
        return ".fold";
    }
}
