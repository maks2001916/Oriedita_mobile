package com.example.oriedita_data.service.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.example.oriedita_data.databinding.ApplicationModel;
import com.example.oriedita_common.editor.service.ApplicationModelPersistenceService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipInputStream;

/**
 * Сервис для сохранения и восстановления модели приложения
 * Адаптирован для Android с использованием SharedPreferences и внутреннего хранилища
 * Использует Gson для JSON сериализации/десериализации
 */
public class ApplicationModelPersistenceServiceImpl implements ApplicationModelPersistenceService {

    public static final String CONFIG_JSON = "config.json";
    public static final String PREFS_NAME = "OrieditaPrefs";
    public static final String CONFIG_KEY = "application_config";
    
    private final Context context; // Android контекст для доступа к файловой системе
    private final ApplicationModel applicationModel;
    private final SharedPreferences sharedPreferences;
    private final Gson gson; // Gson для JSON сериализации

    /**
     * Конструктор для Android версии
     * @param context Android контекст для доступа к файловой системе
     * @param applicationModel модель приложения для сохранения/восстановления
     */
    public ApplicationModelPersistenceServiceImpl(Context context, ApplicationModel applicationModel) {
        this.context = context;
        this.applicationModel = applicationModel;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Инициализируем Gson с красивым форматированием
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    @Override
    public void init() {
        restoreApplicationModel();
        // Добавляем слушатель изменений для автоматического сохранения
        applicationModel.addPropertyChangeListener(e -> persistApplicationModel());
    }

    /**
     * Восстанавливает модель приложения из файла или SharedPreferences
     * Если файл не существует или поврежден, загружает настройки по умолчанию
     */
    public void restoreApplicationModel() {
        // Сначала пытаемся загрузить из SharedPreferences (быстрее)
        String configJson = sharedPreferences.getString(CONFIG_KEY, null);
        
        if (configJson != null) {
            try {
                ApplicationModel loadedApplicationModel = gson.fromJson(configJson, ApplicationModel.class);
                applicationModel.set(loadedApplicationModel);
                Log.i("ApplicationModelPersistence", "Настройки успешно загружены из SharedPreferences");
                return;
            } catch (JsonSyntaxException e) {
                Log.w("ApplicationModelPersistence", "Ошибка загрузки из SharedPreferences, пробуем файл", e);
            }
        }

        // Если SharedPreferences пустые, пробуем загрузить из файла
        File configFile = new File(context.getFilesDir(), CONFIG_JSON);

        if (!configFile.exists()) {
            applicationModel.reset();
            Log.i("ApplicationModelPersistence", "Файл конфигурации не найден, загружены настройки по умолчанию");
            return;
        }

        try (FileReader reader = new FileReader(configFile)) {
            ApplicationModel loadedApplicationModel = gson.fromJson(reader, ApplicationModel.class);
            applicationModel.set(loadedApplicationModel);
            
            // Сохраняем в SharedPreferences для быстрого доступа в следующий раз
            String jsonString = gson.toJson(loadedApplicationModel);
            sharedPreferences.edit().putString(CONFIG_KEY, jsonString).apply();
            
            Log.i("ApplicationModelPersistence", "Настройки успешно загружены из файла");
        } catch (IOException | JsonSyntaxException e) {
            // Найдено состояние приложения, но оно недействительно
            Log.e("ApplicationModelPersistence", "Ошибка загрузки состояния приложения", e);
            
            // Показываем уведомление пользователю
            showToast("Не удалось загрузить настройки приложения.\nЗагружена конфигурация по умолчанию.", false);

            // Переименовываем поврежденный файл
            File backupFile = new File(context.getFilesDir(), CONFIG_JSON + ".old");
            if (!configFile.renameTo(backupFile)) {
                Log.e("ApplicationModelPersistence", "Не удалось переименовать config.json");
            }

            applicationModel.reset();
        }
    }

    /**
     * Импортирует модель приложения из ZIP архива
     * @param zis поток ZIP архива с конфигурацией
     */
    public void importApplicationModel(ZipInputStream zis) {
        try {
            StringBuilder s = new StringBuilder();
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = zis.read(buffer, 0, 1024)) >= 0) {
                s.append(new String(buffer, 0, read));
            }

            ApplicationModel loadedApplicationModel = gson.fromJson(s.toString(), ApplicationModel.class);
            applicationModel.set(loadedApplicationModel);
            
            // Сохраняем импортированные настройки
            persistApplicationModel();
            
            Log.i("ApplicationModelPersistence", "Настройки успешно импортированы");
            showToast("Настройки успешно импортированы", true);
        } catch (JsonSyntaxException e) {
            // Не удается сопоставить импортированное состояние приложения
            Log.e("ApplicationModelPersistence", "Ошибка сопоставления импортированного состояния", e);
            showToast("Не удалось сопоставить импортированные настройки", false);
        } catch (IOException e) {
            // Импортированное состояние приложения недоступно
            Log.e("ApplicationModelPersistence", "Ошибка импорта состояния приложения", e);
            showToast("Не удалось импортировать настройки", false);
        } catch (Exception e) {
            Log.e("ApplicationModelPersistence", "Неожиданная ошибка при импорте", e);
            showToast("Произошла ошибка при импорте настроек", false);
        }
    }

    /**
     * Сохраняет модель приложения в файл и SharedPreferences
     * Использует двойное сохранение для надежности
     */
    public void persistApplicationModel() {
        try {
            // Создаем временную копию для сериализации
            ApplicationModel tempApplicationModel = new ApplicationModel();
            tempApplicationModel.set(applicationModel);
            
            // Сохраняем в JSON строку
            String jsonString = gson.toJson(tempApplicationModel);
            
            // Сохраняем в SharedPreferences (быстрое сохранение)
            sharedPreferences.edit().putString(CONFIG_KEY, jsonString).apply();
            
            // Сохраняем в файл (долгосрочное хранение)
            File configFile = new File(context.getFilesDir(), CONFIG_JSON);
            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(tempApplicationModel, writer);
            }
            
            Log.d("ApplicationModelPersistence", "Настройки успешно сохранены");
        } catch (IOException e) {
            Log.e("ApplicationModelPersistence", "Не удалось записать модель приложения на диск", e);
            showToast("Ошибка сохранения настроек", false);
        }
    }

    /**
     * Показывает Toast уведомление пользователю
     * @param message сообщение для отображения
     * @param isSuccess true для успешного уведомления, false для ошибки
     */
    private void showToast(String message, boolean isSuccess) {
        // Используем Handler для показа Toast в главном потоке
        android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
        handler.post(() -> {
            Toast.makeText(context, message, isSuccess ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
        });
    }

    /**
     * Очищает все сохраненные настройки
     * Используется для сброса приложения к настройкам по умолчанию
     */
    public void clearAllSettings() {
        // Очищаем SharedPreferences
        sharedPreferences.edit().clear().apply();
        
        // Удаляем файл конфигурации
        File configFile = new File(context.getFilesDir(), CONFIG_JSON);
        if (configFile.exists()) {
            configFile.delete();
        }
        
        // Сбрасываем модель к настройкам по умолчанию
        applicationModel.reset();
        
        Log.i("ApplicationModelPersistence", "Все настройки очищены");
        showToast("Настройки сброшены к значениям по умолчанию", true);
    }

    /**
     * Экспортирует текущие настройки в JSON файл
     * @param outputFile файл для сохранения настроек
     * @return true если экспорт успешен, false в противном случае
     */
    public boolean exportSettings(File outputFile) {
        try {
            // Создаем временную копию для сериализации
            ApplicationModel tempApplicationModel = new ApplicationModel();
            tempApplicationModel.set(applicationModel);
            
            // Сохраняем в файл с красивым форматированием
            try (FileWriter writer = new FileWriter(outputFile)) {
                gson.toJson(tempApplicationModel, writer);
            }
            
            Log.i("ApplicationModelPersistence", "Настройки успешно экспортированы в " + outputFile.getPath());
            showToast("Настройки успешно экспортированы", true);
            return true;
        } catch (IOException e) {
            Log.e("ApplicationModelPersistence", "Ошибка экспорта настроек", e);
            showToast("Ошибка экспорта настроек", false);
            return false;
        }
    }

    /**
     * Получает JSON строку текущих настроек
     * @return JSON строка с настройками или null в случае ошибки
     */
    public String getSettingsAsJson() {
        try {
            ApplicationModel tempApplicationModel = new ApplicationModel();
            tempApplicationModel.set(applicationModel);
            return gson.toJson(tempApplicationModel);
        } catch (Exception e) {
            Log.e("ApplicationModelPersistence", "Ошибка получения настроек в JSON формате", e);
            return null;
        }
    }

    /**
     * Загружает настройки из JSON строки
     * @param jsonString JSON строка с настройками
     * @return true если загрузка успешна, false в противном случае
     */
    public boolean loadSettingsFromJson(String jsonString) {
        try {
            ApplicationModel loadedApplicationModel = gson.fromJson(jsonString, ApplicationModel.class);
            applicationModel.set(loadedApplicationModel);
            
            // Сохраняем загруженные настройки
            persistApplicationModel();
            
            Log.i("ApplicationModelPersistence", "Настройки успешно загружены из JSON строки");
            showToast("Настройки успешно загружены", true);
            return true;
        } catch (JsonSyntaxException e) {
            Log.e("ApplicationModelPersistence", "Ошибка парсинга JSON строки", e);
            showToast("Ошибка загрузки настроек из JSON", false);
            return false;
        }
    }
}
