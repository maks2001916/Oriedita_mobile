package com.example.oriedita_data.export;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.oriedita_data.export.api.FileExporter;
import com.example.oriedita_data.save.Save;
import com.example.oriedita_data.databinding.ApplicationModel;

import fold.io.CreasePatternWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * CpExporter - экспортер файлов .cp (Crease Pattern) для Android
 * 
 * Этот класс отвечает за экспорт файлов оригами в формате CP (Crease Pattern).
 * Формат CP поддерживает только основные линии сгиба, но не включает круги,
 * текст и желтые вспомогательные линии. Для полного сохранения рекомендуется
 * использовать формат .ori.
 */
public class CpExporter implements FileExporter {
    
    private static final String TAG = "CpExporter";
    
    private final Context context;
    private final ApplicationModel applicationModel;

    /**
     * Конструктор CpExporter
     * 
     * @param context Android контекст для показа уведомлений
     * @param applicationModel модель приложения для управления настройками
     */
    public CpExporter(Context context, ApplicationModel applicationModel) {
        this.context = context;
        this.applicationModel = applicationModel;
    }

    /**
     * Экспортирует объект Save в файл CP
     * 
     * @param save объект Save с данными оригами
     * @param file файл для экспорта
     * @throws IOException при ошибках записи файла
     */
    @Override
    public void doExport(Save save, File file) throws IOException {
        Log.i(TAG, "Начинаем экспорт в формат CP: " + file.getName());
        
        // Проверяем, можно ли сохранить как CP и показывали ли уже предупреждение
        if (!save.canSaveAsCp() && !applicationModel.getCpExportWarning()) {
            Log.w(TAG, "Показываем предупреждение о ограничениях формата CP");
            
            // Показываем предупреждение пользователю
            String warningMessage = "Сохраненный .cp файл не содержит круги, текст и желтые вспомогательные линии. " +
                    "Сохраните как .ori файл, чтобы также сохранить эти элементы.";
            
            Toast.makeText(context, warningMessage, Toast.LENGTH_LONG).show();
            
            // Устанавливаем флаг, чтобы больше не показывать предупреждение
            applicationModel.setCpExportWarning(true);
        }

        try (OutputStream os = new FileOutputStream(file)) {
            // Создаем писатель для формата CP
            CreasePatternWriter creasePatternWriter = new CreasePatternWriter(os);
            
            // Конвертируем Save в формат FOLD и записываем
            FoldExporter foldExporter = new FoldExporter();
            creasePatternWriter.write(foldExporter.toFoldSave(save));
            
            Log.i(TAG, "Файл CP успешно экспортирован");
            
        } catch (InterruptedException e) {
            Log.e(TAG, "Ошибка при экспорте файла CP", e);
            throw new IOException("Ошибка при экспорте CP файла", e);
        } catch (Exception e) {
            Log.e(TAG, "Неожиданная ошибка при экспорте файла CP", e);
            throw new IOException("Неожиданная ошибка при экспорте CP файла", e);
        }
    }

    /**
     * Возвращает название формата экспорта
     * @return название формата
     */
    @Override
    public String getName() {
        return "Crease Pattern";
    }

    /**
     * Возвращает расширение файла
     * @return расширение файла
     */
    @Override
    public String getExtension() {
        return ".cp";
    }

    /**
     * Возвращает приоритет экспортера
     * @return приоритет (0 - стандартный)
     */
    @Override
    public int getPriority() {
        return 0;
    }
}
