package com.example.oriedita_data.export;

import android.graphics.Color;
import android.util.Log;
import com.example.oriedita_data.databinding.ApplicationModel;
import com.example.oriedita_data.databinding.FoldedFigureModel;
import com.example.oriedita_data.databinding.GridModel;
import com.example.oriedita_common.editor.drawing.tools.Camera;
import com.example.oriedita_data.export.api.FileExporter;
import com.example.oriedita_data.save.Save;
import com.example.oriedita_core.origami.crease_pattern.elements.Circle;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * OrhExporter - экспортер файлов .orh (Orihime) для Android
 * 
 * Этот класс отвечает за экспорт файлов оригами в формате .orh (Orihime).
 * Сохраняет настройки камеры, сетки, цветов, линий и окружностей
 * в файлы, совместимые с программой Orihime.
 */
public class OrhExporter implements FileExporter {
    
    private static final String TAG = "OrhExporter";

    /**
     * Экспортирует объект Save в файл .orh
     * 
     * @param save объект Save с данными оригами
     * @param file файл для экспорта
     * @throws IOException при ошибках записи файла
     */
    @Override
    public void doExport(Save save, File file) throws IOException {
        try (FileWriter fw = new FileWriter(file); 
             BufferedWriter bw = new BufferedWriter(fw); 
             PrintWriter pw = new PrintWriter(bw)) {
            
            // Записываем заголовок
            pw.println("<タイトル>");
            pw.println("タイトル," + save.getTitle());

            // Записываем сегменты линий
            pw.println("<線分集合>");
            int index = 1;
            for (LineSegment s : save.getLineSegments()) {
                pw.println("番号," + index++);
                pw.println("色," + s.getColor());

                pw.println("<tpp>" + s.getCustomized() + "</tpp>");
                
                // Извлекаем RGB компоненты из Android Color (int)
                int customizedColor = s.getCustomizedColor();
                pw.println("<tpp_color_R>" + Color.red(customizedColor) + "</tpp_color_R>");
                pw.println("<tpp_color_G>" + Color.green(customizedColor) + "</tpp_color_G>");
                pw.println("<tpp_color_B>" + Color.blue(customizedColor) + "</tpp_color_B>");

                pw.println("座標," + s.determineAX() + "," + s.determineAY() + "," + s.determineBX() + "," + s.determineBY());
            }

            // Записываем окружности
            pw.println("<円集合>");
            index = 1;
            for (Circle circle : save.getCircles()) {
                pw.println("番号," + index++);
                Circle e_temp = new Circle();
                e_temp.set(circle);
                pw.println("中心と半径と色," + e_temp.getX() + "," + e_temp.getY() + "," + e_temp.getR() + "," + e_temp.getColor());

                pw.println("<tpp>" + e_temp.getCustomized() + "</tpp>");
                
                // Извлекаем RGB компоненты из Android Color (int)
                int customizedColor = e_temp.getCustomizedColor();
                pw.println("<tpp_color_R>" + Color.red(customizedColor) + "</tpp_color_R>");
                pw.println("<tpp_color_G>" + Color.green(customizedColor) + "</tpp_color_G>");
                pw.println("<tpp_color_B>" + Color.blue(customizedColor) + "</tpp_color_B>");
            }

            // Записываем вспомогательные сегменты линий
            pw.println("<補助線分集合>");
            index = 1;
            for (LineSegment s : save.getAuxLineSegments()) {
                pw.println("補助番号," + index++);
                pw.println("補助色," + s.getColor());

                pw.println("<tpp>" + s.getCustomized() + "</tpp>");
                
                // Извлекаем RGB компоненты из Android Color (int)
                int customizedColor = s.getCustomizedColor();
                pw.println("<tpp_color_R>" + Color.red(customizedColor) + "</tpp_color_R>");
                pw.println("<tpp_color_G>" + Color.green(customizedColor) + "</tpp_color_G>");
                pw.println("<tpp_color_B>" + Color.blue(customizedColor) + "</tpp_color_B>");

                pw.println("補助座標," + s.determineAX() + "," + s.determineAY() + "," + s.determineBX() + "," + s.determineBY());
            }

            // Записываем настройки камеры
            Camera camera = save.getCreasePatternCamera() != null ? save.getCreasePatternCamera() : new Camera();
            pw.println("<camera_of_orisen_nyuuryokuzu>");
            pw.println("<camera_ichi_x>" + camera.getCameraPositionX() + "</camera_ichi_x>");
            pw.println("<camera_ichi_y>" + camera.getCameraPositionY() + "</camera_ichi_y>");
            pw.println("<camera_kakudo>" + camera.getCameraAngle() + "</camera_kakudo>");
            pw.println("<camera_kagami>" + camera.getCameraMirror() + "</camera_kagami>");
            pw.println("<camera_bairitsu_x>" + camera.getCameraZoomX() + "</camera_bairitsu_x>");
            pw.println("<camera_bairitsu_y>" + camera.getCameraZoomY() + "</camera_bairitsu_y>");
            pw.println("<hyouji_ichi_x>" + camera.getDisplayPositionX() + "</hyouji_ichi_x>");
            pw.println("<hyouji_ichi_y>" + camera.getDisplayPositionY() + "</hyouji_ichi_y>");
            pw.println("</camera_of_orisen_nyuuryokuzu>");

            // Записываем настройки приложения
            pw.println("<settei>");
            ApplicationModel applicationModel = save.getApplicationModel() != null ? save.getApplicationModel() : new ApplicationModel();
            pw.println("<ckbox_mouse_settei>" + applicationModel.getMouseWheelMovesCreasePattern() + "</ckbox_mouse_settei>");
            pw.println("<ckbox_ten_sagasi>" + applicationModel.getDisplayPointSpotlight() + "</ckbox_ten_sagasi>");
            pw.println("<ckbox_ten_hanasi>" + applicationModel.getDisplayPointOffset() + "</ckbox_ten_hanasi>");
            pw.println("<ckbox_kou_mitudo_nyuuryoku>" + applicationModel.getDisplayGridInputAssist() + "</ckbox_kou_mitudo_nyuuryoku>");
            pw.println("<ckbox_bun>" + applicationModel.getDisplayComments() + "</ckbox_bun>");
            pw.println("<ckbox_cp>" + applicationModel.getDisplayCpLines() + "</ckbox_cp>");
            pw.println("<ckbox_a0>" + applicationModel.getDisplayAuxLines() + "</ckbox_a0>");
            pw.println("<ckbox_a1>" + applicationModel.getDisplayLiveAuxLines() + "</ckbox_a1>");
            pw.println("<ckbox_mejirusi>" + applicationModel.getDisplayMarkings() + "</ckbox_mejirusi>");
            pw.println("<ckbox_cp_ue>" + applicationModel.getDisplayCreasePatternOnTop() + "</ckbox_cp_ue>");
            pw.println("<ckbox_oritatami_keika>" + applicationModel.getDisplayFoldingProgress() + "</ckbox_oritatami_keika>");
            
            // Толщина линии в виде разработки
            pw.println("<iTenkaizuSenhaba>" + applicationModel.getLineWidth() + "</iTenkaizuSenhaba>");
            // Ширина знака вершины
            pw.println("<ir_ten>" + applicationModel.getPointSize() + "</ir_ten>");
            // Выражение полигональной линии с цветом
            pw.println("<i_orisen_hyougen>" + applicationModel.getLineStyle() + "</i_orisen_hyougen>");
            pw.println("<i_anti_alias>" + applicationModel.getAntiAlias() + "</i_anti_alias>");
            pw.println("</settei>");

            // Записываем настройки сетки
            GridModel gridModel = save.getGridModel() != null ? save.getGridModel() : new GridModel();
            pw.println("<Kousi>");
            pw.println("<i_kitei_jyoutai>" + gridModel.getBaseState() + "</i_kitei_jyoutai>");
            pw.println("<nyuuryoku_kitei>" + gridModel.getGridSize() + "</nyuuryoku_kitei>");

            pw.println("<memori_kankaku>" + gridModel.getIntervalGridSize() + "</memori_kankaku>");
            pw.println("<a_to_heikouna_memori_iti>" + gridModel.getHorizontalScalePosition() + "</a_to_heikouna_memori_iti>");
            pw.println("<b_to_heikouna_memori_iti>" + gridModel.getVerticalScalePosition() + "</b_to_heikouna_memori_iti>");
            pw.println("<kousi_senhaba>" + applicationModel.getGridLineWidth() + "</kousi_senhaba>");

            pw.println("<d_kousi_x_a>" + gridModel.getGridXA() + "</d_kousi_x_a>");
            pw.println("<d_kousi_x_b>" + gridModel.getGridXB() + "</d_kousi_x_b>");
            pw.println("<d_kousi_x_c>" + gridModel.getGridXC() + "</d_kousi_x_c>");
            pw.println("<d_kousi_y_a>" + gridModel.getGridYA() + "</d_kousi_y_a>");
            pw.println("<d_kousi_y_b>" + gridModel.getGridYB() + "</d_kousi_y_b>");
            pw.println("<d_kousi_y_c>" + gridModel.getGridYC() + "</d_kousi_y_c>");
            pw.println("<d_kousi_kakudo>" + gridModel.getGridAngle() + "</d_kousi_kakudo>");
            pw.println("</Kousi>");

            // Записываем цвета сетки
            pw.println("<Kousi_iro>");
            int gridColor = applicationModel.getGridColor();
            pw.println("<kousi_color_R>" + Color.red(gridColor) + "</kousi_color_R>");
            pw.println("<kousi_color_G>" + Color.green(gridColor) + "</kousi_color_G>");
            pw.println("<kousi_color_B>" + Color.blue(gridColor) + "</kousi_color_B>");

            int gridScaleColor = applicationModel.getGridScaleColor();
            pw.println("<kousi_memori_color_R>" + Color.red(gridScaleColor) + "</kousi_memori_color_R>");
            pw.println("<kousi_memori_color_G>" + Color.green(gridScaleColor) + "</kousi_memori_color_G>");
            pw.println("<kousi_memori_color_B>" + Color.blue(gridScaleColor) + "</kousi_memori_color_B>");
            pw.println("</Kousi_iro>");

            // Записываем настройки сложенной фигуры
            pw.println("<oriagarizu>");
            FoldedFigureModel foldedFigureModel = save.getFoldedFigureModel() != null ? save.getFoldedFigureModel() : new FoldedFigureModel();
            
            int frontColor = foldedFigureModel.getFrontColor();
            pw.println("<oriagarizu_F_color_R>" + Color.red(frontColor) + "</oriagarizu_F_color_R>");
            pw.println("<oriagarizu_F_color_G>" + Color.green(frontColor) + "</oriagarizu_F_color_G>");
            pw.println("<oriagarizu_F_color_B>" + Color.blue(frontColor) + "</oriagarizu_F_color_B>");

            int backColor = foldedFigureModel.getBackColor();
            pw.println("<oriagarizu_B_color_R>" + Color.red(backColor) + "</oriagarizu_B_color_R>");
            pw.println("<oriagarizu_B_color_G>" + Color.green(backColor) + "</oriagarizu_B_color_G>");
            pw.println("<oriagarizu_B_color_B>" + Color.blue(backColor) + "</oriagarizu_B_color_B>");

            int lineColor = foldedFigureModel.getLineColor();
            pw.println("<oriagarizu_L_color_R>" + Color.red(lineColor) + "</oriagarizu_L_color_R>");
            pw.println("<oriagarizu_L_color_G>" + Color.green(lineColor) + "</oriagarizu_L_color_G>");
            pw.println("<oriagarizu_L_color_B>" + Color.blue(lineColor) + "</oriagarizu_L_color_B>");

            pw.println("</oriagarizu>");
            
            Log.i(TAG, "Файл .orh успешно экспортирован: " + file.getAbsolutePath());
            
        } catch (IOException e) {
            Log.e(TAG, "Ошибка при экспорте .orh файла", e);
            throw e;
        }
    }

    /**
     * Проверяет, поддерживается ли файл для экспорта
     * @param filename файл для проверки
     * @return true если файл имеет расширение .orh
     */
    @Override
    public boolean supports(File filename) {
        return filename.getName().endsWith(".orh");
    }

    /**
     * Возвращает название формата экспорта
     * @return название формата
     */
    @Override
    public String getName() {
        return "Orihime save";
    }

    /**
     * Возвращает расширение файла
     * @return расширение файла
     */
    @Override
    public String getExtension() {
        return ".orh";
    }
}
