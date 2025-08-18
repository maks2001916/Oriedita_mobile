package com.example.oriedita_common.resources

import android.content.Context
import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.annotation.DrawableRes

/**
 * Менеджер ресурсов для Android-совместимой архитектуры Oriedita
 * Заменяет устаревшие .properties файлы на нативные Android ресурсы
 */
class AndroidResourceManager(private val context: Context) {
    
    companion object {
        const val LANGUAGE_RUSSIAN = "ru"
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_JAPANESE = "jp"
    }
    
    private var currentLanguage: String = LANGUAGE_RUSSIAN
    
    /**
     * Установить язык приложения
     */
    fun setLanguage(language: String) {
        currentLanguage = language
        // Здесь можно добавить логику переключения языка
    }
    
    /**
     * Получить текущий язык
     */
    fun getCurrentLanguage(): String = currentLanguage
    
    /**
     * Получить название иконки
     */
    fun getIconName(key: String): String {
        return getStringResource("icon_$key")
    }
    
    /**
     * Получить подсказку для инструмента
     */
    fun getTooltip(key: String): String {
        return getStringResource("tooltip_$key")
    }
    
    /**
     * Получить горячую клавишу
     */
    fun getHotkey(key: String): String {
        return getStringResource("hotkey_$key")
    }
    
    /**
     * Получить текст помощи
     */
    fun getHelpText(key: String): String {
        return getStringResource("help_$key")
    }
    
    /**
     * Получить код иконки (Unicode символ)
     */
    fun getIconCode(key: String): String {
        return getStringResource("icon_code_$key")
    }
    
    /**
     * Получить общую строку
     */
    fun getString(key: String): String {
        return getStringResource(key)
    }
    
    /**
     * Получить ID drawable ресурса по имени
     */
    fun getDrawableId(name: String): Int {
        return getResourceId(name, "drawable")
    }
    
    /**
     * Получить ID drawable ресурса по имени с fallback
     */
    fun getDrawableIdWithFallback(name: String, fallbackName: String = "ic_launcher_foreground"): Int {
        val id = getDrawableId(name)
        return if (id != 0) id else getDrawableId(fallbackName)
    }
    
    /**
     * Проверить, существует ли drawable ресурс
     */
    fun hasDrawable(name: String): Boolean {
        return getDrawableId(name) != 0
    }
    
    /**
     * Получить список всех доступных drawable ресурсов
     */
    fun getAvailableDrawables(): List<String> {
        val drawables = mutableListOf<String>()
        val fields = DrawableResources::class.java.declaredFields
        
        for (field in fields) {
            if (field.type == String::class.java) {
                field.isAccessible = true
                val value = field.get(null) as? String
                if (value != null && hasDrawable(value)) {
                    drawables.add(value)
                }
            }
        }
        
        return drawables
    }
    
    /**
     * Получить строковый ресурс по ключу
     */
    private fun getStringResource(key: String): String {
        return try {
            val resourceId = getResourceId(key, "string")
            if (resourceId != 0) {
                context.getString(resourceId)
            } else {
                // Fallback на русский язык
                getStringResourceFallback(key)
            }
        } catch (e: Exception) {
            // Fallback на русский язык
            getStringResourceFallback(key)
        }
    }
    
    /**
     * Fallback метод для получения строки на русском языке
     */
    private fun getStringResourceFallback(key: String): String {
        return try {
            val resourceId = getResourceId(key, "string")
            if (resourceId != 0) {
                context.getString(resourceId)
            } else {
                key // Возвращаем ключ, если ресурс не найден
            }
        } catch (e: Exception) {
            key // Возвращаем ключ, если ресурс не найден
        }
    }
    
    /**
     * Получить ID ресурса по имени и типу
     */
    private fun getResourceId(name: String, defType: String): Int {
        return try {
            context.resources.getIdentifier(name, defType, context.packageName)
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Получить ресурс по ID
     */
    fun getString(@StringRes resourceId: Int): String {
        return context.getString(resourceId)
    }
    
    /**
     * Получить ресурс по ID с форматированием
     */
    fun getString(@StringRes resourceId: Int, vararg formatArgs: Any): String {
        return context.getString(resourceId, *formatArgs)
    }
    
    /**
     * Проверить, существует ли ресурс
     */
    fun hasResource(key: String, defType: String = "string"): Boolean {
        return getResourceId(key, defType) != 0
    }
    
    /**
     * Получить все доступные языки
     */
    fun getAvailableLanguages(): List<String> {
        return listOf(LANGUAGE_RUSSIAN, LANGUAGE_ENGLISH, LANGUAGE_JAPANESE)
    }
    
    /**
     * Получить название языка
     */
    fun getLanguageName(languageCode: String): String {
        return when (languageCode) {
            LANGUAGE_RUSSIAN -> "Русский"
            LANGUAGE_ENGLISH -> "English"
            LANGUAGE_JAPANESE -> "日本語"
            else -> languageCode
        }
    }
} 