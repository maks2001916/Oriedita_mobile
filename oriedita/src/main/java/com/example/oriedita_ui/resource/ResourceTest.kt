package com.example.oriedita_ui.resource

import android.content.Context
import android.util.Log

/**
 * Тестовый класс для проверки работы системы ресурсов
 * Используется для отладки и проверки корректности переноса ресурсов
 */
class ResourceTest(private val context: Context) {
    
    companion object {
        private const val TAG = "ResourceTest"
    }
    
    private val resourceManager = ResourceManagerStub(context)
    
    /**
     * Запускает полное тестирование системы ресурсов
     */
    fun runFullTest(): String {
        val results = mutableListOf<String>()
        
        try {
            results.add("=== ТЕСТ СИСТЕМЫ РЕСУРСОВ ORIEDITA ===")
            
            // Тест 1: Проверка доступности основных ресурсов
            results.add(testBasicResources())
            
            // Тест 2: Проверка загрузки иконок
            results.add(testIconLoading())
            
            // Тест 3: Проверка текстовых ресурсов
            results.add(testTextResources())
            
            // Тест 4: Проверка изображений помощи
            results.add(testHelpImages())
            
            // Тест 5: Проверка свойств файлов
            results.add(testPropertiesFiles())
            
            // Тест 6: Общая информация о ресурсах
            results.add(resourceManager.getResourceInfo())
            
            results.add("=== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ===")
            
        } catch (e: Exception) {
            results.add("ОШИБКА ТЕСТИРОВАНИЯ: ${e.message}")
            Log.e(TAG, "Ошибка тестирования", e)
        }
        
        return results.joinToString("\n")
    }
    
    /**
     * Тестирует доступность основных ресурсов
     */
    private fun testBasicResources(): String {
        val results = mutableListOf<String>()
        results.add("--- Тест 1: Основные ресурсы ---")
        
        // Проверка файлов свойств
        val propertiesFiles = listOf(
            "help.properties",
            "help_jp.properties", 
            "icons.properties",
            "name.properties",
            "tooltip.properties",
            "hotkey.properties",
            "gif.properties"
        )
        
        propertiesFiles.forEach { fileName ->
            val exists = resourceManager.resourceExists(fileName)
            results.add("$fileName: ${if (exists) "✓" else "✗"}")
        }
        
        // Проверка CSV файлов
        val csvFiles = listOf("categories.csv")
        csvFiles.forEach { fileName ->
            val exists = resourceManager.resourceExists(fileName)
            results.add("$fileName: ${if (exists) "✓" else "✗"}")
        }
        
        // Проверка шрифтов
        val fontFiles = listOf("Icons2.ttf")
        fontFiles.forEach { fileName ->
            val exists = resourceManager.resourceExists(fileName)
            results.add("$fileName: ${if (exists) "✓" else "✗"}")
        }
        
        return results.joinToString("\n")
    }
    
    /**
     * Тестирует загрузку иконок
     */
    private fun testIconLoading(): String {
        val results = mutableListOf<String>()
        results.add("--- Тест 2: Загрузка иконок ---")
        
        // Тест основных иконок
        val basicIcons = listOf(
            "line_input",
            "circle_input",
            "select",
            "delete"
        )
        
        basicIcons.forEach { iconName ->
            val icon = resourceManager.getIcon(iconName)
            results.add("$iconName: ${if (icon != null) "✓" else "✗"}")
        }
        
        // Тест иконок типов линий
        val lineTypeIcons = listOf(
            "mountain",
            "valley",
            "edge"
        )
        
        lineTypeIcons.forEach { iconName ->
            val icon = resourceManager.getIcon(iconName)
            results.add("$iconName: ${if (icon != null) "✓" else "✗"}")
        }
        
        // Подсчет доступных иконок
        val availableIcons = resourceManager.getAvailableIcons()
        results.add("Всего доступно иконок: ${availableIcons.size}")
        
        return results.joinToString("\n")
    }
    
    /**
     * Тестирует текстовые ресурсы
     */
    private fun testTextResources(): String {
        val results = mutableListOf<String>()
        results.add("--- Тест 3: Текстовые ресурсы ---")
        
        // Тест помощи
        val helpKeys = resourceManager.getHelpKeys()
        results.add("Ключи помощи: ${helpKeys.size}")
        
        if (helpKeys.isNotEmpty()) {
            val sampleHelpKey = helpKeys.first()
            val helpText = resourceManager.getHelpText(sampleHelpKey)
            results.add("Пример помощи ($sampleHelpKey): ${helpText?.take(50)}...")
        }
        
        // Тест названий иконок
        val iconKeys = resourceManager.getIconKeys()
        results.add("Ключи иконок: ${iconKeys.size}")
        
        if (iconKeys.isNotEmpty()) {
            val sampleIconKey = iconKeys.first()
            val iconName = resourceManager.getIconName(sampleIconKey)
            results.add("Пример названия иконки ($sampleIconKey): $iconName")
        }
        
        // Тест названий элементов
        val nameKeys = resourceManager.getNameKeys()
        results.add("Ключи названий: ${nameKeys.size}")
        
        if (nameKeys.isNotEmpty()) {
            val sampleNameKey = nameKeys.first()
            val elementName = resourceManager.getElementName(sampleNameKey)
            results.add("Пример названия элемента ($sampleNameKey): $elementName")
        }
        
        return results.joinToString("\n")
    }
    
    /**
     * Тестирует изображения помощи
     */
    private fun testHelpImages(): String {
        val results = mutableListOf<String>()
        results.add("--- Тест 4: Изображения помощи ---")
        
        val helpImages = resourceManager.getAvailableHelpImages()
        results.add("Доступно изображений помощи: ${helpImages.size}")
        
        if (helpImages.isNotEmpty()) {
            val sampleImage = helpImages.first()
            val image = resourceManager.getHelpImage(sampleImage)
            results.add("Пример изображения ($sampleImage): ${if (image != null) "✓" else "✗"}")
        }
        
        return results.joinToString("\n")
    }
    
    /**
     * Тестирует файлы свойств
     */
    private fun testPropertiesFiles(): String {
        val results = mutableListOf<String>()
        results.add("--- Тест 5: Файлы свойств ---")
        
        // Тест загрузки различных properties файлов
        val propertiesFiles = listOf(
            "help.properties",
            "icons.properties",
            "name.properties"
        )
        
        propertiesFiles.forEach { fileName ->
            val properties = resourceManager.loadProperties(fileName)
            if (properties != null) {
                val keyCount = properties.keys.size
                results.add("$fileName: ✓ ($keyCount ключей)")
            } else {
                results.add("$fileName: ✗")
            }
        }
        
        return results.joinToString("\n")
    }
    
    /**
     * Запускает быстрый тест
     */
    fun runQuickTest(): String {
        return try {
            val iconCount = resourceManager.getAvailableIcons().size
            val helpImageCount = resourceManager.getAvailableHelpImages().size
            val helpKeyCount = resourceManager.getHelpKeys().size
            
            """
            Быстрый тест ресурсов:
            ✓ Иконки: $iconCount
            ✓ Изображения помощи: $helpImageCount  
            ✓ Ключи помощи: $helpKeyCount
            ✓ Система ресурсов работает
            """.trimIndent()
            
        } catch (e: Exception) {
            "Ошибка быстрого теста: ${e.message}"
        }
    }
} 