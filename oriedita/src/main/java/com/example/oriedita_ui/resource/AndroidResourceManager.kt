package com.example.oriedita_ui.resource

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.StringRes
import java.io.IOException

/**
 * Android-совместимый менеджер ресурсов для Oriedita
 * Использует Android Resource System вместо properties файлов
 */
class AndroidResourceManager(private val context: Context) {
    
    companion object {
        private const val TAG = "AndroidResourceManager"
        
        // Поддерживаемые языки
        const val LANGUAGE_RUSSIAN = "ru"
        const val LANGUAGE_JAPANESE = "jp"
        const val LANGUAGE_ENGLISH = "en"
        
                       // Текущий язык по умолчанию
               private var currentLanguage = LANGUAGE_RUSSIAN
    }
    
    private val resources: Resources = context.resources
    private val iconCache = mutableMapOf<String, Bitmap>()
    private val darkIconCache = mutableMapOf<String, Bitmap>()
    
    /**
     * Устанавливает текущий язык
     * @param language код языка
     */
    fun setLanguage(language: String) {
        currentLanguage = when (language) {
            LANGUAGE_JAPANESE -> LANGUAGE_JAPANESE
            LANGUAGE_ENGLISH -> LANGUAGE_ENGLISH
            LANGUAGE_RUSSIAN -> LANGUAGE_RUSSIAN
            else -> LANGUAGE_RUSSIAN
        }
        Log.i(TAG, "Установлен язык: $currentLanguage")
    }
    
    /**
     * Получает текущий язык
     * @return код текущего языка
     */
    fun getCurrentLanguage(): String = currentLanguage
    
    /**
     * Получает строковый ресурс по ID
     * @param stringResId ID строкового ресурса
     * @return строка или null если не найдена
     */
    fun getString(@StringRes stringResId: Int): String? {
        return try {
            resources.getString(stringResId)
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Строковый ресурс не найден: $stringResId", e)
            null
        }
    }
    
    /**
     * Получает название иконки
     * @param iconKey ключ иконки
     * @return название иконки или null если не найдено
     */
    fun getIconName(iconKey: String): String? {
        val resourceName = "icon_$iconKey"
        val resourceId = getStringResourceId(resourceName)
        return if (resourceId != 0) getString(resourceId) else null
    }
    
    /**
     * Получает подсказку для элемента
     * @param tooltipKey ключ подсказки
     * @return подсказка или null если не найдена
     */
    fun getTooltip(tooltipKey: String): String? {
        val resourceName = "tooltip_$tooltipKey"
        val resourceId = getStringResourceId(resourceName)
        return if (resourceId != 0) getString(resourceId) else null
    }
    
    /**
     * Получает горячую клавишу для действия
     * @param hotkeyKey ключ горячей клавиши
     * @return горячая клавиша или null если не найдена
     */
    fun getHotkey(hotkeyKey: String): String? {
        val resourceName = "hotkey_$hotkeyKey"
        val resourceId = getStringResourceId(resourceName)
        return if (resourceId != 0) getString(resourceId) else null
    }
    
    /**
     * Получает текст помощи по ключу
     * @param helpKey ключ помощи
     * @return текст помощи или null если не найден
     */
    fun getHelpText(helpKey: String): String? {
        val resourceName = "help_$helpKey"
        val resourceId = getStringResourceId(resourceName)
        return if (resourceId != 0) getString(resourceId) else null
    }
    
    /**
     * Загружает иконку по имени
     * @param iconName имя иконки (без расширения)
     * @param useDarkTheme использовать темную тему
     * @return Bitmap иконки или null если не найдена
     */
    fun getIcon(iconName: String, useDarkTheme: Boolean = false): Bitmap? {
        val cache = if (useDarkTheme) darkIconCache else iconCache
        
        return cache.getOrPut(iconName) {
            loadIconFromAssets(iconName, useDarkTheme) ?: createDefaultIcon()
        }
    }
    
    /**
     * Загружает иконку инструмента
     * @param toolName имя инструмента
     * @param useDarkTheme использовать темную тему
     * @return Bitmap иконки или null если не найдена
     */
    fun getToolIcon(toolName: String, useDarkTheme: Boolean = false): Bitmap? {
        return getIcon(toolName, useDarkTheme)
    }
    
    /**
     * Загружает иконку типа линии
     * @param lineType тип линии
     * @param useDarkTheme использовать темную тему
     * @return Bitmap иконки или null если не найдена
     */
    fun getLineTypeIcon(lineType: String, useDarkTheme: Boolean = false): Bitmap? {
        return getIcon(lineType, useDarkTheme)
    }
    
    /**
     * Загружает иконку геометрического построения
     * @param constructionName имя построения
     * @param useDarkTheme использовать темную тему
     * @return Bitmap иконки или null если не найдена
     */
    fun getConstructionIcon(constructionName: String, useDarkTheme: Boolean = false): Bitmap? {
        return getIcon(constructionName, useDarkTheme)
    }
    
    /**
     * Загружает иконку проверки
     * @param checkName имя проверки
     * @param useDarkTheme использовать темную тему
     * @return Bitmap иконки или null если не найдена
     */
    fun getCheckIcon(checkName: String, useDarkTheme: Boolean = false): Bitmap? {
        return getIcon(checkName, useDarkTheme)
    }
    
    /**
     * Загружает иконку настроек
     * @param settingName имя настройки
     * @param useDarkTheme использовать темную тему
     * @return Bitmap иконки или null если не найдена
     */
    fun getSettingIcon(settingName: String, useDarkTheme: Boolean = false): Bitmap? {
        return getIcon(settingName, useDarkTheme)
    }
    
    /**
     * Получает изображение помощи
     * @param imageName имя изображения
     * @return Bitmap изображения или null если не найдено
     */
    fun getHelpImage(imageName: String): Bitmap? {
        return try {
            val resourceName = "help_img_${imageName.lowercase()}"
            val resourceId = getDrawableResourceId(resourceName)
            if (resourceId != 0) {
                BitmapFactory.decodeResource(resources, resourceId)
            } else {
                loadHelpImageFromAssets(imageName)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки изображения помощи: $imageName", e)
            null
        }
    }
    
    /**
     * Получает список доступных иконок
     * @param useDarkTheme использовать темную тему
     * @return список имен иконок
     */
    fun getAvailableIcons(useDarkTheme: Boolean = false): List<String> {
        return try {
            val path = if (useDarkTheme) "ppp_dark" else "ppp"
            context.assets.list(path)?.filter { it.endsWith(".png") }?.map { it.removeSuffix(".png") } ?: emptyList()
        } catch (e: IOException) {
            Log.e(TAG, "Ошибка получения списка иконок", e)
            emptyList()
        }
    }
    
    /**
     * Получает все ключи иконок
     * @return список ключей
     */
    fun getIconKeys(): List<String> {
        return listOf(
            "line_input", "circle_input", "select", "delete", "undo", "redo",
            "zoom_in", "zoom_out", "pan", "rotate",
            "mountain", "valley", "edge", "aux", "black", "red",
            "parallel", "perpendicular", "angle_bisector", "square_bisector", "fish_bone", "voronoi",
            "check1", "check2", "check3", "check4",
            "settings", "mouse_settings", "line_width", "point_size"
        )
    }
    
    /**
     * Получает все ключи подсказок
     * @return список ключей
     */
    fun getTooltipKeys(): List<String> {
        return listOf(
            "line_input", "circle_input", "select", "delete", "undo", "redo",
            "mountain", "valley", "edge", "aux",
            "parallel", "perpendicular", "angle_bisector", "square_bisector",
            "check1", "check2", "check3", "check4"
        )
    }
    
    /**
     * Получает все ключи горячих клавиш
     * @return список ключей
     */
    fun getHotkeyKeys(): List<String> {
        return listOf(
            "line_input", "circle_input", "select", "delete", "undo", "redo",
            "zoom_in", "zoom_out", "pan", "rotate",
            "mountain", "valley", "edge", "aux",
            "parallel", "perpendicular", "angle_bisector", "square_bisector",
            "check1", "check2", "check3", "check4",
            "settings", "mouse_settings"
        )
    }
    
    /**
     * Получает все ключи помощи
     * @return список ключей
     */
    fun getHelpKeys(): List<String> {
        return listOf(
            "welcome", "quick_start", "tool_selection",
            "line_tool", "circle_tool", "select_tool", "delete_tool",
            "mountain_fold", "valley_fold", "edge_line", "auxiliary_line",
            "parallel_lines", "perpendicular_lines", "angle_bisector", "square_bisector",
            "check1", "check2", "check3", "check4",
            "zoom", "pan", "rotate",
            "save", "load", "export"
        )
    }
    
    /**
     * Очищает кэш иконок
     */
    fun clearCache() {
        iconCache.clear()
        darkIconCache.clear()
        Log.i(TAG, "Кэш иконок очищен")
    }
    
    /**
     * Получает информацию о ресурсах
     * @return строка с информацией о ресурсах
     */
    fun getResourceInfo(): String {
        val iconCount = getAvailableIcons().size
        val helpKeyCount = getHelpKeys().size
        val iconKeyCount = getIconKeys().size
        val tooltipKeyCount = getTooltipKeys().size
        val hotkeyKeyCount = getHotkeyKeys().size
        
        return """
            Информация о ресурсах (Android):
            - Иконки: $iconCount
            - Ключи помощи: $helpKeyCount
            - Ключи иконок: $iconKeyCount
            - Ключи подсказок: $tooltipKeyCount
            - Ключи горячих клавиш: $hotkeyKeyCount
            - Текущий язык: $currentLanguage
            - Использует Android Resource System
        """.trimIndent()
    }
    
    /**
     * Загружает иконку из assets
     */
    private fun loadIconFromAssets(iconName: String, useDarkTheme: Boolean): Bitmap? {
        return try {
            val path = if (useDarkTheme) "ppp_dark/$iconName.png" else "ppp/$iconName.png"
            val inputStream = context.assets.open(path)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            bitmap
        } catch (e: IOException) {
            Log.e(TAG, "Ошибка загрузки иконки: $iconName", e)
            null
        }
    }
    
    /**
     * Загружает изображение помощи из assets
     */
    private fun loadHelpImageFromAssets(imageName: String): Bitmap? {
        return try {
            val path = "help-img/$imageName.png"
            val inputStream = context.assets.open(path)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            bitmap
        } catch (e: IOException) {
            Log.e(TAG, "Ошибка загрузки изображения помощи: $imageName", e)
            null
        }
    }
    
    /**
     * Получает ID строкового ресурса по имени
     */
    private fun getStringResourceId(resourceName: String): Int {
        return try {
            val packageName = context.packageName
            resources.getIdentifier(resourceName, "string", packageName)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка получения ID строкового ресурса: $resourceName", e)
            0
        }
    }
    
    /**
     * Получает ID drawable ресурса по имени
     */
    private fun getDrawableResourceId(resourceName: String): Int {
        return try {
            val packageName = context.packageName
            resources.getIdentifier(resourceName, "drawable", packageName)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка получения ID drawable ресурса: $resourceName", e)
            0
        }
    }
    
    /**
     * Создает простую иконку по умолчанию
     */
    private fun createDefaultIcon(): Bitmap {
        return Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888).apply {
            val pixels = IntArray(32 * 32) { 0xFF808080.toInt() }
            setPixels(pixels, 0, 32, 0, 0, 32, 32)
        }
    }
} 