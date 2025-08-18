package com.example.oriedita_data.save;

import com.google.gson.annotations.SerializedName;

/**
 * Версия 1.1 сохранения оригами
 * Расширенная версия формата сохранения с дополнительными возможностями
 * Использует Gson для сериализации/десериализации
 */

public class SaveV1_1 extends BaseSave {
    
    /**
     * Защищенный конструктор для Gson
     * Создает экземпляр версии 1.1 сохранения
     */
    protected SaveV1_1() {
        super();
    }
    
    /**
     * Создает новый экземпляр версии 1.1 сохранения
     * @return новый экземпляр SaveV1_1
     */
    public static SaveV1_1 createInstance() {
        return new SaveV1_1();
    }
    
    /**
     * Получает версию формата сохранения
     * @return строку с версией "v1.1"
     */
    public String getVersion() {
        return "v1.1";
    }
    
    /**
     * Проверяет, является ли это последней версией формата
     * @return true для версии 1.1 (последняя стабильная версия)
     */
    public boolean isLatestVersion() {
        return true;
    }
}
