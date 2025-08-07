package com.example.oriedita_data.export;

import com.example.oriedita_data.json.DefaultObjectMapper;
import com.example.oriedita_data.save.Save;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * OriExporter - экспортер файлов .ori для Android
 * 
 * Этот класс отвечает за экспорт данных оригами в файлы формата .ori.
 * Использует Gson для сериализации объектов Save в JSON формат.
 * Поддерживает сохранение проектов оригами в стандартном формате Oriedita.
 */
public class OriExporter implements FileExporter {
    
    private final Gson gson;

    /**
     * Конструктор по умолчанию
     * Инициализирует Gson экземпляр для работы с JSON
     */
    public OriExporter() {
        this.gson = DefaultObjectMapper.getInstance();
    }

    /**
     * Конструктор с пользовательским Gson экземпляром
     * @param gson экземпляр Gson для сериализации
     */
    public OriExporter(Gson gson) {
        this.gson = gson;
    }

    /**
     * Выполняет экспорт объекта Save в файл .ori
     * Сериализует данные оригами в JSON формат и сохраняет в указанный файл
     * 
     * @param save объект с данными оригами для экспорта
     * @param file файл для сохранения данных
     * @throws IOException при ошибках записи файла
     */
    @Override
    public void doExport(Save save, File file) throws IOException {
        // Создаем FileWriter для записи в файл
        try (FileWriter writer = new FileWriter(file)) {
            // Сериализуем объект Save в JSON и записываем в файл
            gson.toJson(save, writer);
            // Принудительно записываем данные на диск
            writer.flush();
        }
    }

    /**
     * Возвращает название формата экспорта
     * @return название формата "Ori"
     */
    @Override
    public String getName() {
        return "Ori";
    }

    /**
     * Возвращает расширение файла для данного формата
     * @return расширение ".ori"
     */
    @Override
    public String getExtension() {
        return ".ori";
    }

    /**
     * Проверяет, поддерживается ли файл для экспорта
     * @param filename файл для проверки
     * @return true если файл имеет расширение .ori
     */
    @Override
    public boolean supports(File filename) {
        return filename.getName().endsWith(".ori");
    }

    /**
     * Возвращает приоритет экспортера
     * Используется для определения порядка экспортеров при наличии нескольких
     * @return приоритет (0 - высший приоритет)
     */
    @Override
    public int getPriority() {
        return 0;
    }

    /**
     * Получает Gson экземпляр, используемый для сериализации
     * @return экземпляр Gson
     */
    public Gson getGson() {
        return gson;
    }

    /**
     * Экспортирует объект Save в строку JSON
     * Удобный метод для получения JSON представления без записи в файл
     * 
     * @param save объект с данными оригами
     * @return JSON строка с данными оригами
     */
    public String exportToString(Save save) {
        return gson.toJson(save);
    }

    /**
     * Проверяет, является ли файл валидным .ori файлом
     * Пытается десериализовать файл для проверки его корректности
     * 
     * @param file файл для проверки
     * @return true если файл является валидным .ori файлом
     */
    public boolean isValidOriFile(File file) {
        if (!supports(file)) {
            return false;
        }
        
        try {
            // Пытаемся десериализовать файл
            gson.fromJson(new java.io.FileReader(file), Save.class);
            return true;
        } catch (Exception e) {
            // Файл не является валидным JSON или не содержит объект Save
            return false;
        }
    }
}
