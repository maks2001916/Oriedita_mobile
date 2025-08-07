package com.example.oriedita_data.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;

import android.graphics.Color;
import java.io.File;
import java.lang.reflect.Type;

/**
 * Настроенный Gson ObjectMapper для Android
 * Предоставляет кастомные сериализаторы и десериализаторы для специфичных типов
 * Заменяет Jackson ObjectMapper на Gson для лучшей совместимости с Android
 */
public class DefaultObjectMapper {
    
    private static final Gson gson;
    
    static {
        // Создаем GsonBuilder с настройками
        GsonBuilder builder = new GsonBuilder()
                .setPrettyPrinting()                    // Красивое форматирование JSON
                .serializeNulls()                       // Включаем null значения
                .setLenient()                           // Либеральный режим парсинга
                .disableHtmlEscaping();                 // Отключаем экранирование HTML
                
        // Регистрируем кастомные адаптеры
        builder.registerTypeAdapter(Integer.class, new ColorTypeAdapter());
        builder.registerTypeAdapter(Point.class, new PointTypeAdapter());
        builder.registerTypeAdapter(File.class, new FileTypeAdapter());
        
        gson = builder.create();
    }
    
    /**
     * Получает настроенный экземпляр Gson
     * @return экземпляр Gson с кастомными адаптерами
     */
    public static Gson getInstance() {
        return gson;
    }
    
    /**
     * Создает новый экземпляр Gson с теми же настройками
     * @return новый экземпляр Gson
     */
    public static Gson createInstance() {
        GsonBuilder builder = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .setLenient()
                .disableHtmlEscaping();
                
        builder.registerTypeAdapter(Integer.class, new ColorTypeAdapter());
        builder.registerTypeAdapter(Point.class, new PointTypeAdapter());
        builder.registerTypeAdapter(File.class, new FileTypeAdapter());
        
        return builder.create();
    }
    
    /**
     * Адаптер для работы с Android Color (как int)
     * Сериализует Color в hex строку и десериализует из hex строки
     */
    private static class ColorTypeAdapter implements JsonSerializer<Integer>, JsonDeserializer<Integer> {
        
        @Override
        public JsonElement serialize(Integer src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return JsonNull.INSTANCE;
            }
            // Сериализуем цвет в hex формат (например, "#FF0000")
            return new JsonPrimitive(String.format("#%06X", (0xFFFFFF & src)));
        }
        
        @Override
        public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
                throws JsonParseException {
            if (json.isJsonNull()) {
                return null;
            }
            
            String hexColor = json.getAsString();
            
            // Убираем # если есть
            if (hexColor.startsWith("#")) {
                hexColor = hexColor.substring(1);
            }
            
            try {
                // Парсим hex цвет
                int color = Integer.parseInt(hexColor, 16);
                return color;
            } catch (NumberFormatException e) {
                throw new JsonParseException("Неверный формат цвета: " + hexColor, e);
            }
        }
    }
    
    /**
     * Адаптер для работы с точками оригами
     * Сериализует Point в строку "x,y" и десериализует из такой строки
     */
    private static class PointTypeAdapter implements JsonSerializer<Point>, JsonDeserializer<Point> {
        
        @Override
        public JsonElement serialize(Point src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return JsonNull.INSTANCE;
            }
            // Сериализуем точку в формат "x,y"
            return new JsonPrimitive(src.getX() + "," + src.getY());
        }
        
        @Override
        public Point deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
                throws JsonParseException {
            if (json.isJsonNull()) {
                return null;
            }
            
            String pointString = json.getAsString();
            String[] values = pointString.split(",");
            
            if (values.length != 2) {
                throw new JsonParseException("Неверный формат точки: " + pointString + 
                                           ". Ожидается формат 'x,y'");
            }
            
            try {
                double x = Double.parseDouble(values[0].trim());
                double y = Double.parseDouble(values[1].trim());
                return new Point(x, y);
            } catch (NumberFormatException e) {
                throw new JsonParseException("Неверные координаты точки: " + pointString, e);
            }
        }
    }
    
    /**
     * Адаптер для работы с файлами
     * Сериализует File в строку пути и десериализует из строки пути
     */
    private static class FileTypeAdapter implements JsonSerializer<File>, JsonDeserializer<File> {
        
        @Override
        public JsonElement serialize(File src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return JsonNull.INSTANCE;
            }
            // Сериализуем файл в строку пути
            return new JsonPrimitive(src.getAbsolutePath());
        }
        
        @Override
        public File deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
                throws JsonParseException {
            if (json.isJsonNull()) {
                return null;
            }
            
            String filePath = json.getAsString();
            return new File(filePath);
        }
    }
    
    /**
     * Сериализует объект в JSON строку
     * @param obj объект для сериализации
     * @return JSON строка
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }
    
    /**
     * Сериализует объект в JSON строку с указанным типом
     * @param obj объект для сериализации
     * @param typeOfSrc тип объекта
     * @return JSON строка
     */
    public static String toJson(Object obj, Type typeOfSrc) {
        return gson.toJson(obj, typeOfSrc);
    }
    
    /**
     * Десериализует JSON строку в объект
     * @param json JSON строка
     * @param classOfT класс объекта
     * @param <T> тип объекта
     * @return десериализованный объект
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
    
    /**
     * Десериализует JSON строку в объект с указанным типом
     * @param json JSON строка
     * @param typeOfT тип объекта
     * @param <T> тип объекта
     * @return десериализованный объект
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }
    
    /**
     * Десериализует JSON строку в объект с TypeToken
     * @param json JSON строка
     * @param typeToken токен типа
     * @param <T> тип объекта
     * @return десериализованный объект
     */
    public static <T> T fromJson(String json, TypeToken<T> typeToken) {
        return gson.fromJson(json, typeToken.getType());
    }
    
    /**
     * Проверяет, является ли строка валидным JSON
     * @param json строка для проверки
     * @return true если строка является валидным JSON, false в противном случае
     */
    public static boolean isValidJson(String json) {
        try {
            gson.fromJson(json, Object.class);
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }
    
    /**
     * Создает GsonBuilder с предустановленными настройками
     * @return настроенный GsonBuilder
     */
    public static GsonBuilder createBuilder() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .setLenient()
                .disableHtmlEscaping()
                .registerTypeAdapter(Color.class, new ColorTypeAdapter())
                .registerTypeAdapter(Point.class, new PointTypeAdapter())
                .registerTypeAdapter(File.class, new FileTypeAdapter());
    }
}
