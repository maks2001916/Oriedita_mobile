package com.example.oriedita_data.export;

import android.graphics.Color;
import android.util.Log;
import com.example.oriedita_common.editor.canvas.LineStyle;
import com.example.oriedita_data.databinding.ApplicationModel;
import com.example.oriedita_data.databinding.CanvasModel;
import com.example.oriedita_data.databinding.FoldedFigureModel;
import com.example.oriedita_data.databinding.GridModel;
import com.example.oriedita_common.editor.drawing.tools.Camera;
import com.example.oriedita_data.export.api.FileImporter;
import com.example.oriedita_data.save.Save;
import com.example.oriedita_data.save.SaveProvider;
import com.example.oriedita_common.editor.tools.StringOp;
import com.example.oriedita_core.origami.crease_pattern.elements.Circle;
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OrhImporter - импортер файлов .orh (Orihime) для Android
 * 
 * Этот класс отвечает за импорт файлов оригами в формате .orh (Orihime).
 * Поддерживает чтение настроек камеры, сетки, цветов, линий и окружностей
 * из файлов, созданных в программе Orihime.
 */
public class OrhImporter implements FileImporter {
    
    private static final String TAG = "OrhImporter";

    /**
     * Проверяет, поддерживается ли файл для импорта
     * @param filename файл для проверки
     * @return true если файл имеет расширение .orh
     */
    @Override
    public boolean supports(File filename) {
        return filename.getName().endsWith(".orh");
    }

    /**
     * Читает файл Orihime (.orh) и создает объект Save с данными оригами
     * 
     * @param file файл .orh для импорта
     * @return объект Save с данными оригами
     * @throws IOException при ошибках чтения файла
     */
    @Override
    public Save doImport(File file) throws IOException {
        Save save = SaveProvider.createInstance();
        Pattern p = Pattern.compile("<(.+)>(.+)</(.+)>");

        boolean reading;

        // Загрузка настроек камеры для вида разработки
        reading = false;

        List<String> fileLines = loadFile(file);

        if (fileLines == null) {
            throw new IOException("Кодировка не определена");
        }

        // Чтение настроек камеры
        for (String str : fileLines) {
            if (str.equals("<camera_of_orisen_nyuuryokuzu>")) {
                reading = true;
            } else if (str.equals("</camera_of_orisen_nyuuryokuzu>")) {
                reading = false;
            } else {
                if (!reading) {
                    continue;
                }

                Matcher m = p.matcher(str);

                if (!m.matches()) {
                    continue;
                }

                Camera creasePatternCamera = new Camera();
                save.setCreasePatternCamera(creasePatternCamera);
                switch (m.group(1)) {
                    case "camera_ichi_x":
                        creasePatternCamera.setCameraPositionX(Double.parseDouble(m.group(2)));
                        break;
                    case "camera_ichi_y":
                        creasePatternCamera.setCameraPositionY(Double.parseDouble(m.group(2)));
                        break;
                    case "camera_kakudo":
                        creasePatternCamera.setCameraAngle(Double.parseDouble(m.group(2)));
                        break;
                    case "camera_kagami":
                        creasePatternCamera.setCameraMirror(Double.parseDouble(m.group(2)));
                        break;
                    case "camera_bairitsu_x":
                        creasePatternCamera.setCameraZoomX(Double.parseDouble(m.group(2)));
                        break;
                    case "camera_bairitsu_y":
                        creasePatternCamera.setCameraZoomY(Double.parseDouble(m.group(2)));
                        break;
                    case "hyouji_ichi_x":
                        creasePatternCamera.setDisplayPositionX(Double.parseDouble(m.group(2)));
                        break;
                    case "hyouji_ichi_y":
                        creasePatternCamera.setDisplayPositionY(Double.parseDouble(m.group(2)));
                        break;
                }
            }
        }

        CanvasModel canvasModel = new CanvasModel();
        save.setCanvasModel(canvasModel);

        ApplicationModel applicationModel = new ApplicationModel();
        save.setApplicationModel(applicationModel);

        // ----------------------------------------- Чтение настроек чекбоксов и других параметров
        reading = false;
        for (String str : fileLines) {
            if (str.equals("<settei>")) {
                reading = true;
            } else if (str.equals("</settei>")) {
                reading = false;
            } else {
                if (!reading) {
                    continue;
                }
                Matcher m = p.matcher(str);
                if (!m.matches()) {
                    continue;
                }

                String value = m.group(2).trim();
                switch (m.group(1)) {
                    case "mouseSettingsAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setMouseWheelMovesCreasePattern(selected);
                        break;
                    }
                    case "showPointRangeAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayPointSpotlight(selected);
                        break;
                    }
                    case "pointOffsetAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayPointOffset(selected);
                        break;
                    }
                    case "gridInputAssistAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayGridInputAssist(selected);
                        break;
                    }
                    case "displayCommentsAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayComments(selected);
                        break;
                    }
                    case "displayCpLinesAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayCpLines(selected);
                        break;
                    }
                    case "displayAuxLinesAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayAuxLines(selected);
                        break;
                    }
                    case "displayLiveAuxLinesAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayLiveAuxLines(selected);
                        break;
                    }
                    case "displayStandardFaceMarksAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayMarkings(selected);
                        break;
                    }
                    case "cpOnTopAction": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayCreasePatternOnTop(selected);
                        break;
                    }
                    case "ckbox_oritatami_keika": {
                        boolean selected = Boolean.parseBoolean(value);
                        applicationModel.setDisplayFoldingProgress(selected);
                        break;
                    }
                    case "iTenkaizuSenhaba":
                        applicationModel.setLineWidth(Integer.parseInt(value));
                        break;
                    case "ir_ten":
                        applicationModel.setPointSize(Integer.parseInt(value));
                        break;
                    case "i_orisen_hyougen":
                        applicationModel.setLineStyle(LineStyle.from(value));
                        break;
                    case "i_anti_alias":
                        applicationModel.setAntiAlias(Boolean.parseBoolean(value));
                        break;
                }
            }
        }

        // ----------------------------------------- Чтение настроек сетки
        reading = false;
        GridModel gridModel = new GridModel();
        save.setGridModel(gridModel);
        double gridXA = 0.0;
        double gridXB = 1.0;
        double gridXC = 1.0;
        double gridYA = 0.0;
        double gridYB = 1.0;
        double gridYC = 1.0;
        for (String str : fileLines) {
            if (str.equals("<Kousi>")) {
                reading = true;
            } else if (str.equals("</Kousi>")) {
                reading = false;
            } else {
                if (!reading) {
                    continue;
                }

                Matcher m = p.matcher(str);
                if (!m.matches()) {
                    continue;
                }

                switch (m.group(1)) {
                    case "i_kitei_jyoutai":
                        gridModel.setBaseState(GridModel.State.from(m.group(2)));
                        break;
                    case "nyuuryoku_kitei":
                        gridModel.setGridSize(StringOp.String2int(m.group(2), gridModel.getGridSize()));
                        break;
                    case "memori_kankaku":
                        int scale_interval = Integer.parseInt(m.group(2));
                        gridModel.setIntervalGridSize(scale_interval);
                        break;
                    case "a_to_heikouna_memori_iti":
                        gridModel.setHorizontalScalePosition(Integer.parseInt(m.group(2)));
                        break;
                    case "b_to_heikouna_memori_iti":
                        gridModel.setVerticalScalePosition(Integer.parseInt(m.group(2)));
                        break;
                    case "kousi_senhaba":
                        applicationModel.setGridLineWidth(Integer.parseInt(m.group(2)));
                        break;
                    case "d_kousi_x_a":
                        gridXA = StringOp.String2double(m.group(2), gridModel.getGridXA());
                        break;
                    case "d_kousi_x_b":
                        gridXB = StringOp.String2double(m.group(2), gridModel.getGridXB());
                        break;
                    case "d_kousi_x_c":
                        gridXC = StringOp.String2double(m.group(2), gridModel.getGridXC());
                        break;
                    case "d_kousi_y_a":
                        gridYA = StringOp.String2double(m.group(2), gridModel.getGridYA());
                        break;
                    case "d_kousi_y_b":
                        gridYB = StringOp.String2double(m.group(2), gridModel.getGridYB());
                        break;
                    case "d_kousi_y_c":
                        gridYC = StringOp.String2double(m.group(2), gridModel.getGridYC());
                        break;
                    case "d_kousi_kakudo":
                        gridModel.setGridAngle(StringOp.String2double(m.group(2), gridModel.getGridAngle()));
                        break;
                }
            }
        }

        gridModel.applyGridX(gridXA, gridXB, gridXC);
        gridModel.applyGridY(gridYA, gridYB, gridYC);

        // ----------------------------------------- Чтение настроек цветов сетки
        int i_grid_color_R = 0;
        int i_grid_color_G = 0;
        int i_grid_color_B = 0;
        int i_grid_memori_color_R = 0;
        int i_grid_memori_color_G = 0;
        int i_grid_memori_color_B = 0;

        boolean i_Grid_iro_yomikomi = false; // Флаг чтения цветов сетки
        reading = false;
        for (String str : fileLines) {
            if (str.equals("<Kousi_iro>")) {
                reading = true;
                i_Grid_iro_yomikomi = true;
            } else if (str.equals("</Kousi_iro>")) {
                reading = false;
            } else {
                if (!reading) {
                    continue;
                }
                Matcher m = p.matcher(str);

                if (!m.matches()) {
                    continue;
                }

                switch (m.group(1)) {
                    case "kousi_color_R":
                        i_grid_color_R = (Integer.parseInt(m.group(2)));
                        break;
                    case "kousi_color_G":
                        i_grid_color_G = (Integer.parseInt(m.group(2)));
                        break;
                    case "kousi_color_B":
                        i_grid_color_B = (Integer.parseInt(m.group(2)));
                        break;
                    case "kousi_memori_color_R":
                        i_grid_memori_color_R = (Integer.parseInt(m.group(2)));
                        break;
                    case "kousi_memori_color_G":
                        i_grid_memori_color_G = (Integer.parseInt(m.group(2)));
                        break;
                    case "kousi_memori_color_B":
                        i_grid_memori_color_B = (Integer.parseInt(m.group(2)));
                        break;
                }
            }
        }

        if (i_Grid_iro_yomikomi) {
            // Создаем Android Color из RGB компонентов
            int gridColor = Color.rgb(
                    i_grid_color_R,
                    i_grid_color_G,
                    i_grid_color_B
            );
            applicationModel.setGridColor(gridColor);

            Log.i(TAG, "i_kousi_memori_color_R= " + i_grid_memori_color_R);
            Log.i(TAG, "i_kousi_memori_color_G= " + i_grid_memori_color_G);
            Log.i(TAG, "i_kousi_memori_color_B= " + i_grid_memori_color_B);
            
            int gridScaleColor = Color.rgb(i_grid_memori_color_R, i_grid_memori_color_G, i_grid_memori_color_B);
            applicationModel.setGridScaleColor(gridScaleColor);
        }

        // Чтение настроек сложенной фигуры -------------------------------------------------------------------------
        int i_oriagarizu_F_color_R = 0;
        int i_oriagarizu_F_color_G = 0;
        int i_oriagarizu_F_color_B = 0;

        int i_oriagarizu_B_color_R = 0;
        int i_oriagarizu_B_color_G = 0;
        int i_oriagarizu_B_color_B = 0;

        int i_oriagarizu_L_color_R = 0;
        int i_oriagarizu_L_color_G = 0;
        int i_oriagarizu_L_color_B = 0;

        boolean i_oriagarizu_yomikomi = false; // Флаг чтения настроек сложенной фигуры
        reading = false;
        for (String str : fileLines) {
            if (str.equals("<oriagarizu>")) {
                reading = true;
                i_oriagarizu_yomikomi = true;
            } else if (str.equals("</oriagarizu>")) {
                reading = false;
            } else {
                if (!reading) {
                    continue;
                }
                Matcher m = p.matcher(str);
                if (!m.matches()) {
                    continue;
                }

                switch (m.group(1)) {
                    case "oriagarizu_F_color_R":
                        i_oriagarizu_F_color_R = (Integer.parseInt(m.group(2)));
                        break;
                    case "oriagarizu_F_color_G":
                        i_oriagarizu_F_color_G = (Integer.parseInt(m.group(2)));
                        break;
                    case "oriagarizu_F_color_B":
                        i_oriagarizu_F_color_B = (Integer.parseInt(m.group(2)));
                        break;
                    case "oriagarizu_B_color_R":
                        i_oriagarizu_B_color_R = (Integer.parseInt(m.group(2)));
                        break;
                    case "oriagarizu_B_color_G":
                        i_oriagarizu_B_color_G = (Integer.parseInt(m.group(2)));
                        break;
                    case "oriagarizu_B_color_B":
                        i_oriagarizu_B_color_B = (Integer.parseInt(m.group(2)));
                        break;
                    case "oriagarizu_L_color_R":
                        i_oriagarizu_L_color_R = (Integer.parseInt(m.group(2)));
                        break;
                    case "oriagarizu_L_color_G":
                        i_oriagarizu_L_color_G = (Integer.parseInt(m.group(2)));
                        break;
                    case "oriagarizu_L_color_B":
                        i_oriagarizu_L_color_B = (Integer.parseInt(m.group(2)));
                        break;
                }
            }
        }

        FoldedFigureModel foldedFigureModel = new FoldedFigureModel();
        save.setFoldedFigureModel(foldedFigureModel);
        if (i_oriagarizu_yomikomi) {
            // Создаем Android Color из RGB компонентов
            int frontColor = Color.rgb(
                    i_oriagarizu_F_color_R,
                    i_oriagarizu_F_color_G,
                    i_oriagarizu_F_color_B
            );
            int backColor = Color.rgb(
                    i_oriagarizu_B_color_R,
                    i_oriagarizu_B_color_G,
                    i_oriagarizu_B_color_B
            );
            int lineColor = Color.rgb(
                    i_oriagarizu_L_color_R,
                    i_oriagarizu_L_color_G,
                    i_oriagarizu_L_color_B
            );
            
            foldedFigureModel.setFrontColor(frontColor);
            foldedFigureModel.setBackColor(backColor);
            foldedFigureModel.setLineColor(lineColor);
        }

        int reading_flag = 0; // Флаг чтения: 0 - не читаем, 1 - читаем линии, 2 - читаем заголовок, 3 - читаем окружности
        int number = 0;
        LineColor ic;
        LineSegment.ActiveState is;

        String r_title = "_";

        double ax, ay, bx, by;
        double dx, dy, dr;

        String str;

        // Чтение файла .orh для Orihime

        // Сначала находим общее количество сегментов линий
        int numLines = 0;
        for (String line : fileLines) {
            StringTokenizer tk = new StringTokenizer(line, ",");

            str = tk.nextToken();
            if (str.equals("<線分集合>")) {
                reading_flag = 1;
            }
            if (str.equals("<円集合>")) {
                reading_flag = 3;
            }
            if ((reading_flag == 1) && (str.equals("番号"))) {
                numLines++;
            }
        }

        while (save.getLineSegments().size() <= numLines) {
            save.addLineSegment(new LineSegment());
            save.addCircle(new Circle());
        }
        // Общее количество сегментов линий было подсчитано

        Circle e_temp = new Circle();

        int i_customized_color_R = 0;
        int i_customized_color_G = 0;
        int i_customized_color_B = 0;

        List<Circle> circles = save.getCircles();
        for (String str_i : fileLines) {
            // Старомодный метод чтения
            StringTokenizer tk = new StringTokenizer(str_i, ",");
            str = tk.nextToken();

            if (str.equals("<タイトル>")) {
                reading_flag = 2;
            }
            if ((reading_flag == 2) && (str.equals("タイトル"))) {
                str = tk.nextToken();
                r_title = str;
            }

            if (str.equals("<線分集合>")) {
                reading_flag = 1;
            }
            if ((reading_flag == 1) && (str.equals("番号"))) {
                str = tk.nextToken();
                number = Integer.parseInt(str) - 1;
            }
            LineSegment s = save.getLineSegments().get(number);
            if ((reading_flag == 1) && (str.equals("色"))) {
                str = tk.nextToken();
                ic = LineColor.from(str);
                s.setColor(ic);
            }

            if (reading_flag == 1) {
                String[] st_new = str_i.split(">", 2); // Разделяем на 2 части
                if (st_new[0].equals("<tpp")) {
                    String[] s_new = st_new[1].split("<", 2);
                    int i_customized = (Integer.parseInt(s_new[0]));
                    s.setCustomized(i_customized);
                }

                if (st_new[0].equals("<tpp_color_R")) {
                    String[] s_new = st_new[1].split("<", 2);
                    i_customized_color_R = (Integer.parseInt(s_new[0]));
                    int customizedColor = Color.rgb(i_customized_color_R, i_customized_color_G, i_customized_color_B);
                    s.setCustomizedColor(customizedColor);
                }

                if (st_new[0].equals("<tpp_color_G")) {
                    String[] s_new = st_new[1].split("<", 2);
                    i_customized_color_G = (Integer.parseInt(s_new[0]));
                    int customizedColor = Color.rgb(i_customized_color_R, i_customized_color_G, i_customized_color_B);
                    s.setCustomizedColor(customizedColor);
                }
                if (st_new[0].equals("<tpp_color_B")) {
                    String[] s_new = st_new[1].split("<", 2);
                    i_customized_color_B = (Integer.parseInt(s_new[0]));
                    int customizedColor = Color.rgb(i_customized_color_R, i_customized_color_G, i_customized_color_B);
                    s.setCustomizedColor(customizedColor);
                }
            }

            if ((reading_flag == 1) && (str.equals("iactive"))) { // Добавлено 20181110
                str = tk.nextToken();
                is = LineSegment.ActiveState.valueOf(str);
                s.setActive(is);
            }

            if ((reading_flag == 1) && (str.equals("選択"))) {
                str = tk.nextToken();
                int isel = Integer.parseInt(str);
                s.setSelected(isel);
            }
            if ((reading_flag == 1) && (str.equals("座標"))) {
                str = tk.nextToken();
                ax = Double.parseDouble(str);
                str = tk.nextToken();
                ay = Double.parseDouble(str);
                str = tk.nextToken();
                bx = Double.parseDouble(str);
                str = tk.nextToken();
                by = Double.parseDouble(str);

                s = s.withCoordinates(ax, ay, bx, by);
            }
            // TODO: протестировать производительность, реализовать и использовать LineSegmentBuilder если слишком медленно
            save.getLineSegments().set(number, s);
            if (str.equals("<円集合>")) {
                reading_flag = 3;
            }

            if ((reading_flag == 3) && (str.equals("番号"))) {
                str = tk.nextToken();
                number = Integer.parseInt(str) - 1;

                save.getCircles().get(number).set(e_temp);
            }

            if ((reading_flag == 3) && (str.equals("中心と半径と色"))) {
                str = tk.nextToken();
                dx = Double.parseDouble(str);
                str = tk.nextToken();
                dy = Double.parseDouble(str);
                str = tk.nextToken();
                dr = Double.parseDouble(str);

                str = tk.nextToken();
                ic = LineColor.from(str);

                circles.get(number).set(dx, dy, dr, ic);
            }

            if (reading_flag == 3) {
                String[] st_new = str_i.split(">", 2); // Разделяем на 2 части
                if (st_new[0].equals("<tpp")) {
                    String[] s_new = st_new[1].split("<", 2);
                    int i_customized = (Integer.parseInt(s_new[0]));
                    circles.get(number).setCustomized(i_customized);
                }

                if (st_new[0].equals("<tpp_color_R")) {
                    String[] s_new = st_new[1].split("<", 2);
                    i_customized_color_R = (Integer.parseInt(s_new[0]));
                    int customizedColor = Color.rgb(
                            i_customized_color_R,
                            i_customized_color_G,
                            i_customized_color_B
                    );
                    circles.get(number).setCustomizedColor(customizedColor);
                }

                if (st_new[0].equals("<tpp_color_G")) {
                    String[] s_new = st_new[1].split("<", 2);
                    i_customized_color_G = (Integer.parseInt(s_new[0]));
                    int customizedColor = Color.rgb(
                            i_customized_color_R,
                            i_customized_color_G,
                            i_customized_color_B
                    );
                    circles.get(number).setCustomizedColor(customizedColor);
                }
                if (st_new[0].equals("<tpp_color_B")) {
                    String[] s_new = st_new[1].split("<", 2);
                    i_customized_color_B = (Integer.parseInt(s_new[0]));
                    int customizedColor = Color.rgb(
                            i_customized_color_R,
                            i_customized_color_G,
                            i_customized_color_B
                    );
                    circles.get(number).setCustomizedColor(customizedColor);
                }
            }
        }

        save.setTitle(r_title);

        return save;
    }

    /**
     * Загружает файл с автоматическим определением кодировки
     * Поддерживает различные японские и китайские кодировки
     * 
     * @param file файл для загрузки
     * @return список строк файла или null если кодировка не определена
     * @throws IOException при ошибках чтения файла
     */
    private static List<String> loadFile(File file) throws IOException {
        // Возможные кодировки
        List<Charset> possibleCharsets = List.of(
                StandardCharsets.UTF_8,
                Charset.forName("EUC-JP"),
                Charset.forName("EUC-CN"),
                Charset.forName("Shift_JIS"),
                Charset.forName("GBK")
        );

        for (Charset charset : possibleCharsets) {
            try {
                List<String> lines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                }
                return lines;
            } catch (CharacterCodingException exception) {
                Log.i(TAG, "Файл не в кодировке " + charset.displayName());
                // игнорируем
            }
        }

        return null;
    }
}
