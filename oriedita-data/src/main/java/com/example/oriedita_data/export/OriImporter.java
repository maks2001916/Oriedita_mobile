package com.example.oriedita_data.export;

import android.content.Context;
import android.widget.Toast;

import com.example.oriedita_data.export.api.FileImporter;
import com.example.oriedita_data.json.DefaultObjectMapper;
import com.example.oriedita_data.save.BaseSave;
import com.example.oriedita_data.save.FileVersionTester;
import com.example.oriedita_data.save.Save;
import com.example.oriedita_data.save.SaveConverter;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * OriImporter - импортер файлов .ori для Android
 * 
 * Этот класс отвечает за импорт файлов оригами (.ori) в Android приложении.
 * Обрабатывает версионность файлов и предупреждает пользователя о возможных
 * проблемах совместимости при открытии файлов, созданных в более новых версиях.
 */
public class OriImporter implements FileImporter {
    
    private final Context context;
    private boolean askOnUnknownFormat = true;
    private final Gson gson;

    /**
     * Конструктор с контекстом Android
     * @param context Android контекст для доступа к ресурсам и UI
     */
    public OriImporter(Context context) {
        this.context = context;
        this.gson = DefaultObjectMapper.getInstance();
    }

    /**
     * Конструктор с дополнительным параметром для управления диалогами
     * @param context Android контекст для доступа к ресурсам и UI
     * @param askOnUnknownFormat флаг для показа диалогов при неизвестном формате
     */
    public OriImporter(Context context, boolean askOnUnknownFormat) {
        this(context);
        this.askOnUnknownFormat = askOnUnknownFormat;
    }

    /**
     * Проверяет, поддерживается ли файл для импорта
     * @param filename файл для проверки
     * @return true если файл имеет расширение .ori
     */
    @Override
    public boolean supports(File filename) {
        return filename.getName().endsWith(".ori");
    }

    /**
     * Выполняет импорт файла .ori
     * Обрабатывает версионность и предупреждает о возможных проблемах совместимости
     * 
     * @param file файл для импорта
     * @return объект Save с данными оригами или null если импорт отменен
     * @throws IOException при ошибках чтения файла
     */
    @Override
    public Save doImport(File file) throws IOException {
        // Читаем файл с помощью Gson
        Save readSave;
        FileVersionTester versionTester;
        
        try (FileReader reader = new FileReader(file)) {
            // Читаем основной объект Save
            readSave = gson.fromJson(reader, Save.class);
            
            // Сбрасываем позицию в файле для чтения версии
            reader.close();
        }
        
        try (FileReader reader = new FileReader(file)) {
            // Читаем информацию о версии файла
            versionTester = gson.fromJson(reader, FileVersionTester.class);
        }

        // Проверяем, является ли файл созданным в более новой версии
        if (readSave.getClass() == BaseSave.class && versionTester.getVersion() == null) {
            // Файл создан в более новой версии, которая не распознается
            if (askOnUnknownFormat) {
                // Показываем предупреждение пользователю
                showNewerVersionWarning();
                // В Android версии автоматически открываем файл с предупреждением
                // Вместо диалога используем Toast для уведомления
                Toast.makeText(context, 
                    "Файл создан в более новой версии Oriedita. " +
                    "Некоторые функции могут быть недоступны.", 
                    Toast.LENGTH_LONG).show();
            }
            
            // Всегда конвертируем в новейшую версию для Android
            return SaveConverter.convertToNewestSave(readSave);
        }
        
        // Обычный случай - конвертируем в новейшую версию
        return SaveConverter.convertToNewestSave(readSave);
    }

    /**
     * Показывает предупреждение о файле, созданном в более новой версии
     * В Android версии использует Toast вместо диалога
     */
    private void showNewerVersionWarning() {
        Toast.makeText(context, 
            "Внимание: Файл создан в более новой версии Oriedita.\n" +
            "Использование с текущей версией может привести к потере данных.", 
            Toast.LENGTH_LONG).show();
    }

    /**
     * Получает Gson экземпляр для работы с JSON
     * @return настроенный экземпляр Gson
     */
    public Gson getGson() {
        return gson;
    }

    /**
     * Устанавливает флаг показа диалогов при неизвестном формате
     * @param askOnUnknownFormat новый флаг
     */
    public void setAskOnUnknownFormat(boolean askOnUnknownFormat) {
        this.askOnUnknownFormat = askOnUnknownFormat;
    }

    /**
     * Получает текущий флаг показа диалогов
     * @return текущее значение флага
     */
    public boolean isAskOnUnknownFormat() {
        return askOnUnknownFormat;
    }
}
