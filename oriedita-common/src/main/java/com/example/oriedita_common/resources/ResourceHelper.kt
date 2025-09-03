package com.example.oriedita_common.resources

import android.content.Context
import androidx.annotation.DrawableRes

/**
 * Вспомогательный класс для работы с ресурсами Oriedita
 * Предоставляет статические методы для получения ресурсов из других модулей
 */
object ResourceHelper {
    
    /**
     * Получить ID drawable ресурса по имени
     */
    @DrawableRes
    fun getDrawableId(context: Context, name: String): Int {
        return try {
            context.resources.getIdentifier(name, "drawable", context.packageName)
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Получить ID строкового ресурса по имени
     */
    fun getStringId(context: Context, name: String): Int {
        return try {
            context.resources.getIdentifier(name, "string", context.packageName)
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Получить ID цветового ресурса по имени
     */
    fun getColorId(context: Context, name: String): Int {
        return try {
            context.resources.getIdentifier(name, "color", context.packageName)
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Получить ID размерного ресурса по имени
     */
    fun getDimensionId(context: Context, name: String): Int {
        return try {
            context.resources.getIdentifier(name, "dimen", context.packageName)
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Получить ID массива ресурса по имени
     */
    fun getArrayId(context: Context, name: String): Int {
        return try {
            context.resources.getIdentifier(name, "array", context.packageName)
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Получить ID drawable ресурса с fallback
     */
    @DrawableRes
    fun getDrawableIdWithFallback(context: Context, name: String, fallbackName: String = "ic_launcher_foreground"): Int {
        val id = getDrawableId(context, name)
        return if (id != 0) id else getDrawableId(context, fallbackName)
    }
    
    /**
     * Получить ID строкового ресурса с fallback
     */
    fun getStringIdWithFallback(context: Context, name: String, fallbackName: String = "app_name"): Int {
        val id = getStringId(context, name)
        return if (id != 0) id else getStringId(context, fallbackName)
    }
    
    /**
     * Проверить, существует ли ресурс
     */
    fun hasResource(context: Context, name: String, defType: String): Boolean {
        return try {
            val id = context.resources.getIdentifier(name, defType, context.packageName)
            id != 0
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Получить список всех доступных ресурсов определенного типа
     */
    fun getAvailableResources(context: Context, defType: String): List<String> {
        val resources = mutableListOf<String>()
        
        // Попробуем получить ресурсы из разных модулей
        val packages = listOf(
            "com.example.oriedita_common",
            "com.example.oriedita_ui",
            "com.example.oriedita",
            "com.example.oriedita_data"
        )
        
        for (packageName in packages) {
            try {
                val fields = Class.forName("$packageName.R").declaredFields
                for (field in fields) {
                    if (field.name == defType) {
                        val resourceClass = field.get(null)?.javaClass
                        if (resourceClass != null) {
                            val resourceFields = resourceClass.declaredFields
                            for (resourceField in resourceFields) {
                                try {
                                    val resourceId = resourceField.getInt(null)
                                    val resourceName = context.resources.getResourceEntryName(resourceId)
                                    if (resourceName.isNotEmpty()) {
                                        resources.add(resourceName)
                                    }
                                } catch (e: Exception) {
                                    // Игнорируем ошибки
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Игнорируем ошибки
            }
        }
        
        return resources.distinct()
    }
} 