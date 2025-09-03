package com.example.oriedita_data.export;

import android.util.Log;
import com.example.oriedita_data.export.api.FileExporter;
import com.example.oriedita_data.save.Save;
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * DxfExporter - экспортер файлов .dxf для Android
 * 
 * Этот класс отвечает за экспорт файлов оригами в формате DXF (Drawing Exchange Format).
 * Поддерживает сохранение линий сгиба с различными цветами и типами в файлы DXF,
 * которые могут быть открыты в CAD-программах.
 */
public class DxfExporter implements FileExporter {
    
    private static final String TAG = "DxfExporter";

    /**
     * Конструктор по умолчанию
     */
    public DxfExporter() {
    }

    /**
     * Экспортирует объект Save в файл DXF
     * 
     * @param save объект Save с данными оригами
     * @param file файл для экспорта
     * @throws IOException при ошибках записи файла
     */
    @Override
    public void doExport(Save save, File file) throws IOException {
        Log.i(TAG, "Начинаем экспорт в формат DXF: " + file.getName());
        
        double scale = 3.0;
        double center = 4.0;
        double x1, y1, x2, y2;
        LineColor lineColor;

        try (FileWriter fw = new FileWriter(file); 
             BufferedWriter bw = new BufferedWriter(fw); 
             PrintWriter pw = new PrintWriter(bw)) {
            
            // Записываем заголовок DXF файла
            pw.println("  0");
            pw.println("SECTION");
            pw.println("  2");
            pw.println("HEADER");
            pw.println("  9");
            pw.println("$ACADVER");
            pw.println("  1");
            pw.println("AC1009");
            pw.println("  0");
            pw.println("ENDSEC");
            pw.println("  0");
            pw.println("SECTION");
            pw.println("  2");
            pw.println("ENTITIES");

            Log.i(TAG, "Обрабатываем " + save.getLineSegments().size() + " сегментов линий");

            // Обрабатываем все сегменты линий
            for (LineSegment lineSegment : save.getLineSegments()) {
                pw.println("  0");
                pw.println("LINE");
                pw.println("  8");

                lineColor = lineSegment.getColor();
                String layerName = "noname";
                int colorNumber = 0;

                // Определяем имя слоя и номер цвета в зависимости от типа линии
                switch (lineColor) {
                    case BLACK_0:
                        layerName = "CutLine";        // Линия разреза
                        colorNumber = 250; // серый
                        break;
                    case RED_1:
                        layerName = "MountainLine";   // Горная линия
                        colorNumber = 1; // красный
                        break;
                    case BLUE_2:
                        layerName = "ValleyLine";     // Долинная линия
                        colorNumber = 5; // синий
                        break;
                    case CYAN_3:
                        layerName = "AuxiliaryLine";  // Вспомогательная линия
                        colorNumber = 4; // голубой
                        break;
                    default:
                        layerName = "OtherLine";      // Другие линии
                        colorNumber = 7; // белый
                        break;
                }
                
                // Вычисляем координаты с смещением
                x1 = lineSegment.determineAX() + 200;
                y1 = lineSegment.determineAY() - 200;
                x2 = lineSegment.determineBX() + 200;
                y2 = lineSegment.determineBY() - 200;

                // Записываем свойства линии
                pw.println(layerName);
                pw.println("  6");
                pw.println("CONTINUOUS");
                pw.println("  62");
                pw.println(colorNumber);

                // Записываем координаты начала линии
                pw.println("  10");
                pw.println(scale(x1, scale, center));
                pw.println("  20");
                pw.println(scale(y1, -scale, center));

                // Записываем координаты конца линии
                pw.println("  11");
                pw.println(scale(x2, scale, center));
                pw.println("  21");
                pw.println(scale(y2, -scale, center));
            }

            // Завершаем DXF файл
            pw.println("  0");
            pw.println("ENDSEC");
            pw.println("  0");
            pw.println("EOF");
            
            Log.i(TAG, "Файл DXF успешно экспортирован");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при экспорте файла DXF", e);
            throw e;
        }
    }

    /**
     * Масштабирует координату с учетом масштаба и центра
     * 
     * @param d координата для масштабирования
     * @param scale коэффициент масштабирования
     * @param center смещение центра
     * @return масштабированная координата
     */
    private static double scale(double d, double scale, double center) {
        return d * scale + center;
    }

    /**
     * Возвращает название формата экспорта
     * @return название формата
     */
    @Override
    public String getName() {
        return "DXF";
    }

    /**
     * Возвращает расширение файла
     * @return расширение файла
     */
    @Override
    public String getExtension() {
        return ".dxf";
    }
}
