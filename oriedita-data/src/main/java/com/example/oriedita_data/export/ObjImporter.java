package com.example.oriedita_data.export;

import android.util.Log;
import com.example.oriedita_data.export.api.FileImporter;
import com.example.oriedita_data.save.Save;
import com.example.oriedita_data.save.SaveProvider;
import com.example.oriedita_core.origami.crease_pattern.elements.Line;
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * ObjImporter - импортер файлов .obj для Android
 * 
 * Этот класс отвечает за импорт файлов оригами в формате .obj (Wavefront OBJ).
 * Поддерживает чтение вершин (v), граней (f) и цветов линий (#e) из файлов OBJ.
 * Преобразует 3D геометрию в 2D паттерн сгибов для оригами.
 */
public class ObjImporter implements FileImporter {
    
    private static final String TAG = "ObjImporter";

    /**
     * Импортирует файл .obj и создает объект Save с данными оригами
     * 
     * @param file файл .obj для импорта
     * @return объект Save с данными оригами
     * @throws IOException при ошибках чтения файла
     */
    @Override
    public Save doImport(File file) throws IOException {
        int jtok;

        Save save = SaveProvider.createInstance();

        // Список точек (вершин)
        ArrayList<Point> tL = new ArrayList<>();
        tL.add(new Point()); // Добавляем пустую точку с индексом 0

        int pointMax = 0;

        // Список линий
        ArrayList<Line> lineList = new ArrayList<>();
        lineList.add(new Line()); // Добавляем пустую линию с индексом 0

        int lineMax = 0;

        // Временный список для хранения индексов граней
        ArrayList<Integer> itempL = new ArrayList<>();
        itempL.add(0);

        int ia;
        int ib;
        LineColor ic;
        // int id;
        double d1, d2; // d3, d4;

        // Границы для нормализации координат
        double xmax = -10000.0;
        double xmin = 10000.0;
        double ymax = -10000.0;
        double ymin = 10000.0;

        String str;

        Log.i(TAG, "Начинаем импорт файла .obj: " + file.getName());

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String fileLine;

            while ((fileLine = reader.readLine()) != null) {
                StringTokenizer tk = new StringTokenizer(fileLine, " ");
                jtok = tk.countTokens();

                if (jtok == 0) {
                    continue; // Пропускаем пустые строки
                }

                str = tk.nextToken();

                // Обработка вершин (v x y z)
                if (str.equals("v")) {
                    d1 = Double.parseDouble(tk.nextToken()); // x координата
                    d2 = Double.parseDouble(tk.nextToken()); // y координата
                    tk.nextToken(); // z координата (игнорируем для 2D)

                    // Обновляем границы
                    if (d1 > xmax) {
                        xmax = d1;
                    }
                    if (d1 < xmin) {
                        xmin = d1;
                    }
                    if (d2 > ymax) {
                        ymax = d2;
                    }
                    if (d2 < ymin) {
                        ymin = d2;
                    }

                    pointMax = pointMax + 1;
                    tL.add(new Point(d1, d2));
                }
                
                // Обработка граней (f v1 v2 v3 ...)
                if (str.equals("f")) {
                    itempL.clear();
                    itempL.add(0);
                    
                    // Читаем индексы вершин грани
                    for (int i = 1; i < jtok; i++) {
                        int ite = Integer.parseInt(tk.nextToken());
                        itempL.add(ite);
                    }
                    
                    // Замыкаем грань (последняя точка = первой)
                    itempL.set(0, itempL.get(jtok - 1));
                    
                    // Создаем линии между соседними вершинами грани
                    for (int i = 0; i < jtok - 1; i++) {
                        int iflg = 0;
                        Integer I_itempL = itempL.get(i + 1);
                        Integer Im1_itempL = itempL.get(i);
                        
                        // Проверяем, не существует ли уже такая линия
                        for (Line line : lineList) {
                            if ((line.getBegin() == Im1_itempL) && (line.getEnd() == I_itempL)) {
                                iflg = iflg + 1;
                            }
                            if ((line.getBegin() == I_itempL) && (line.getEnd() == Im1_itempL)) {
                                iflg = iflg + 1;
                            }
                        }
                        
                        // Если линия не существует, создаем новую
                        if (iflg == 0) {
                            lineMax = lineMax + 1;
                            lineList.add(new Line(Im1_itempL, I_itempL, LineColor.BLACK_0));
                        }
                    }
                }

                // Обработка цветов линий (#e v1 v2 color id)
                if (str.equals("#e")) {
                    ia = Integer.parseInt(tk.nextToken()); // первая вершина
                    ib = Integer.parseInt(tk.nextToken()); // вторая вершина
                    ic = LineColor.from(tk.nextToken()); // цвет линии
                    tk.nextToken(); // id (игнорируем)
                    
                    // Устанавливаем цвет для соответствующей линии
                    for (Line line : lineList) {
                        if ((line.getBegin() == ia) && (line.getEnd() == ib)) {
                            line.setColor(ic);
                        }
                        if ((line.getBegin() == ib) && (line.getEnd() == ia)) {
                            line.setColor(ic);
                        }
                    }
                }
            }
        }

        Log.i(TAG, "Обработано точек: " + pointMax + ", линий: " + lineMax);

        // Преобразуем линии в сегменты линий для сохранения
        for (Line line : lineList) {
            // Корректируем цвета линий
            LineColor icol = LineColor.fromNumber(line.getColor().getNumber() - 1);
            line.setColor(icol);
            
            // Инвертируем горные/долинные линии
            if (line.getColor() == LineColor.RED_1) {
                icol = LineColor.BLUE_2;
            }
            if (line.getColor() == LineColor.BLUE_2) {
                icol = LineColor.RED_1;
            }

            if (icol != LineColor.BLACK_0) {
                line.setColor(icol);
            }

            // Создаем сегмент линии из двух точек
            LineSegment s = new LineSegment(
                    tL.get(line.getBegin()),
                    tL.get(line.getEnd()),
                    line.getColor()
            );

            save.addLineSegment(s.clone());
        }

        Log.i(TAG, "Импорт файла .obj завершен успешно");
        return save;
    }

    /**
     * Проверяет, поддерживается ли файл для импорта
     * @param filename файл для проверки
     * @return true если файл имеет расширение .obj
     */
    @Override
    public boolean supports(File filename) {
        return filename.getName().endsWith(".obj");
    }
}
