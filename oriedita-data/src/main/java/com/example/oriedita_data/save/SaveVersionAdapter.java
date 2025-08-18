package com.example.oriedita_data.save;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Адаптер для работы с версиями сохранений в Gson
 * Обеспечивает правильную сериализацию и десериализацию различных версий Save
 */
public class SaveVersionAdapter implements JsonSerializer<Save>, JsonDeserializer<Save> {
    
    private static final String VERSION_PROPERTY = "@version";
    private static final Map<String, Class<? extends Save>> VERSION_MAP = new HashMap<>();
    
    static {
        // Регистрируем все поддерживаемые версии
        VERSION_MAP.put("v1", SaveV1_0.class);
        VERSION_MAP.put("v1.1", SaveV1_1.class);
        VERSION_MAP.put("base", BaseSave.class);
    }
    
    /**
     * Сериализует объект Save в JSON с указанием версии
     * @param src объект для сериализации
     * @param typeOfSrc тип объекта
     * @param context контекст сериализации
     * @return JsonElement с данными и версией
     */
    @Override
    public JsonElement serialize(Save src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return JsonNull.INSTANCE;
        }
        
        // Получаем версию из класса
        String version = getVersionFromClass(src.getClass());
        
        // Сериализуем объект
        JsonElement element = context.serialize(src, src.getClass());
        
        // Добавляем информацию о версии
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            jsonObject.addProperty(VERSION_PROPERTY, version);
            return jsonObject;
        }
        
        return element;
    }
    
    /**
     * Десериализует JSON в объект Save с учетом версии
     * @param json JSON элемент для десериализации
     * @param typeOfT тип объекта
     * @param context контекст десериализации
     * @return объект Save соответствующей версии
     * @throws JsonParseException если версия не поддерживается
     */
    @Override
    public Save deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        
        if (json.isJsonNull()) {
            return null;
        }
        
        if (!json.isJsonObject()) {
            throw new JsonParseException("Ожидается JSON объект");
        }
        
        JsonObject jsonObject = json.getAsJsonObject();
        
        // Получаем версию из JSON
        String version = null;
        if (jsonObject.has(VERSION_PROPERTY)) {
            version = jsonObject.get(VERSION_PROPERTY).getAsString();
        }
        
        // Определяем класс для десериализации
        Class<? extends Save> targetClass = VERSION_MAP.get(version);
        if (targetClass == null) {
            // Если версия не указана или не поддерживается, используем последнюю версию
            targetClass = SaveV1_1.class;
        }
        
        // Удаляем информацию о версии перед десериализацией
        jsonObject.remove(VERSION_PROPERTY);
        
        // Десериализуем в соответствующий класс
        return context.deserialize(jsonObject, targetClass);
    }
    
    /**
     * Получает версию из класса Save
     * @param saveClass класс Save
     * @return строку с версией
     */
    private String getVersionFromClass(Class<? extends Save> saveClass) {
        if (SaveV1_0.class.isAssignableFrom(saveClass)) {
            return "v1";
        } else if (SaveV1_1.class.isAssignableFrom(saveClass)) {
            return "v1.1";
        } else if (BaseSave.class.isAssignableFrom(saveClass)) {
            return "base";
        }
        return "v1.1"; // По умолчанию используем последнюю версию
    }
    
    /**
     * Создает Gson с поддержкой версионирования Save
     * @return настроенный экземпляр Gson
     */
    public static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(Save.class, new SaveVersionAdapter())
                .create();
    }
    
    /**
     * Проверяет, поддерживается ли указанная версия
     * @param version версия для проверки
     * @return true если версия поддерживается, false в противном случае
     */
    public static boolean isVersionSupported(String version) {
        return VERSION_MAP.containsKey(version);
    }
    
    /**
     * Получает список поддерживаемых версий
     * @return массив строк с поддерживаемыми версиями
     */
    public static String[] getSupportedVersions() {
        return VERSION_MAP.keySet().toArray(new String[0]);
    }
    
    /**
     * Получает класс для указанной версии
     * @param version версия
     * @return класс Save для указанной версии или null если версия не поддерживается
     */
    public static Class<? extends Save> getClassForVersion(String version) {
        return VERSION_MAP.get(version);
    }
    
    /**
     * Получает последнюю поддерживаемую версию
     * @return строку с последней версией
     */
    public static String getLatestVersion() {
        return "v1.1";
    }
    
    /**
     * Создает экземпляр Save последней версии
     * @return новый экземпляр SaveV1_1
     */
    public static Save createLatestVersion() {
        return new SaveV1_1();
    }
} 