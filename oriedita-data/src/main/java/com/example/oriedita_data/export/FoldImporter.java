package com.example.oriedita_data.export;

import android.util.Log;
import fold.io.CustomFoldReader;
import fold.model.Edge;
import fold.model.FoldEdgeAssignment;
import fold.model.FoldFrame;
import com.example.oriedita_common.editor.exception.FileReadingException;
import com.example.oriedita_data.export.api.FileImporter;
import com.example.oriedita_data.save.OrieditaFoldFile;
import com.example.oriedita_data.save.Save;
import com.example.oriedita_data.save.SaveProvider;
import com.example.oriedita_core.origami.crease_pattern.FoldLineSet;
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * FoldImporter - импортер файлов .fold для Android
 * 
 * Этот класс отвечает за импорт файлов оригами в формате .fold.
 * Поддерживает чтение граней, цветов линий, окружностей и текста
 * из файлов формата FOLD, используемого для представления складных структур.
 */
public class FoldImporter implements FileImporter {
    
    private static final String TAG = "FoldImporter";

    /**
     * Проверяет, поддерживается ли файл для импорта
     * @param filename файл для проверки
     * @return true если файл имеет расширение .fold
     */
    @Override
    public boolean supports(File filename) {
        return filename.getName().endsWith(".fold");
    }

    /**
     * Преобразует объект OrieditaFoldFile в объект Save
     * 
     * @param foldFile файл FOLD для преобразования
     * @return объект Save с данными оригами
     */
    public Save toSave(OrieditaFoldFile foldFile) {
        Save save = SaveProvider.createInstance();

        // Границы для нормализации координат
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        FoldFrame rootFrame = foldFile.getRootFrame();

        Log.i(TAG, "Обрабатываем " + rootFrame.getEdges().size() + " граней");

        // Обрабатываем все грани из корневого фрейма
        for (int i = 0; i < rootFrame.getEdges().size(); i++) {
            Edge edge = rootFrame.getEdges().get(i);

            // Извлекаем координаты начала и конца грани
            double ax = edge.getStart().getX();
            double ay = edge.getStart().getY();
            double bx = edge.getEnd().getX();
            double by = edge.getEnd().getY();
            
            // Создаем сегмент линии с соответствующим цветом
            LineSegment ls = new LineSegment(
                    new Point(ax, ay),
                    new Point(bx, by),
                    getColor(edge.getAssignment()));

            // Обновляем границы модели
            minX = Math.min(Math.min(minX, ax), bx);
            minY = Math.min(Math.min(minY, ay), by);
            maxX = Math.max(Math.max(maxX, ax), bx);
            maxY = Math.max(Math.max(maxY, ay), by);

            save.addLineSegment(ls);
        }

        // Добавляем окружности из файла FOLD
        save.setCircles(new ArrayList<>(foldFile.getCircles()));
        Log.i(TAG, "Добавлено " + foldFile.getCircles().size() + " окружностей");

        // Создаем временный набор линий для перемещения модели
        FoldLineSet ori_s_temp = new FoldLineSet();    // Используется для извлечения выбранных линий сгиба
        ori_s_temp.setSave(save); // Извлекаем выбранные линии сгиба и создаем ori_s_temp
        
        // Перемещаем модель в центр координат
        ori_s_temp.move(
                new Point(minX, minY),
                new Point(minX, maxY),
                new Point(-200, -200),
                new Point(-200, 200)
        );

        // Создаем новый объект Save с перемещенной моделью
        Save save1 = SaveProvider.createInstance();
        ori_s_temp.getSave(save1);

        // Добавляем тексты из файла FOLD
        save1.setTexts(new ArrayList<>(foldFile.getTexts()));
        Log.i(TAG, "Добавлено " + foldFile.getTexts().size() + " текстовых элементов");

        Log.i(TAG, "Преобразование FOLD файла завершено успешно");
        return save1;
    }

    /**
     * Преобразует тип сгиба FOLD в цвет линии оригами
     * 
     * @param edgeAssignment тип сгиба из файла FOLD
     * @return соответствующий цвет линии
     */
    public static LineColor getColor(FoldEdgeAssignment edgeAssignment) {
        switch (edgeAssignment) {
            case MOUNTAIN_FOLD:
                return LineColor.RED_1;    // Горная линия (красная)
            case VALLEY_FOLD:
                return LineColor.BLUE_2;     // Долинная линия (синяя)
            case FLAT_FOLD:
                return LineColor.CYAN_3;       // Плоская линия (голубая)
            default:
                return LineColor.BLACK_0;             // По умолчанию черная линия
        }
    }

    /**
     * Импортирует файл .fold с обработкой исключений
     * 
     * @param file файл .fold для импорта
     * @return объект Save с данными оригами
     * @throws FileReadingException при ошибках чтения файла
     * @throws IOException при ошибках ввода-вывода
     */
    public Save importFile(File file) throws FileReadingException, IOException {
        Log.i(TAG, "Начинаем импорт файла .fold: " + file.getName());
        
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            CustomFoldReader<OrieditaFoldFile> orieditaFoldFileCustomFoldReader = 
                new CustomFoldReader<>(OrieditaFoldFile.class, fileInputStream);
            OrieditaFoldFile foldFile = orieditaFoldFileCustomFoldReader.read();
            Log.i(TAG, "Файл .fold успешно прочитан");
            return toSave(foldFile);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при чтении файла .fold", e);
            throw e;
        }
    }

    /**
     * Импортирует файл .fold (основной метод интерфейса)
     * 
     * @param file файл .fold для импорта
     * @return объект Save с данными оригами
     * @throws IOException при ошибках чтения файла
     */
    @Override
    public Save doImport(File file) throws IOException {
        try {
            return importFile(file);
        } catch (FileReadingException e) {
            Log.e(TAG, "Ошибка чтения файла .fold", e);
            throw new RuntimeException("Ошибка чтения файла .fold: " + e.getMessage(), e);
        }
    }
}
