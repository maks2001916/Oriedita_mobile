package com.example.oriedita_data.save;

import com.google.gson.annotations.SerializedName;

/**
 * Версия 1.0 сохранения оригами
 * Базовая версия формата сохранения с поддержкой основных элементов
 * Использует Gson для сериализации/десериализации
 */
@SerializedName("v1")
public class SaveV1_0 extends BaseSave {
    
    /**
     * Защищенный конструктор для Gson
     * Создает экземпляр версии 1.0 сохранения
     */
    protected SaveV1_0() {
        super();
    }
    
    /**
     * Создает новый экземпляр версии 1.0 сохранения
     * @return новый экземпляр SaveV1_0
     */
    public static SaveV1_0 createInstance() {
        return new SaveV1_0();
    }
    
    /**
     * Получает версию формата сохранения
     * @return строку с версией "v1"
     */
    public String getVersion() {
        return "v1";
    }
}