package com.example.oriedita_ui.resource

import android.content.Context
import java.util.Properties

/**
 * Заглушка для ResourceManager
 * Используется для тестирования без полной реализации
 */
class ResourceManagerStub(private val context: Context) {
    
    fun resourceExists(fileName: String): Boolean {
        return false // Заглушка
    }
    
    fun getIcon(iconName: String): Any? {
        return null // Заглушка
    }
    
    fun getAvailableIcons(): List<String> {
        return emptyList() // Заглушка
    }
    
    fun getHelpKeys(): List<String> {
        return emptyList() // Заглушка
    }
    
    fun getHelpText(key: String): String? {
        return null // Заглушка
    }
    
    fun getIconKeys(): List<String> {
        return emptyList() // Заглушка
    }
    
    fun getIconName(key: String): String? {
        return null // Заглушка
    }
    
    fun getNameKeys(): List<String> {
        return emptyList() // Заглушка
    }
    
    fun getElementName(key: String): String? {
        return null // Заглушка
    }
    
    fun getAvailableHelpImages(): List<String> {
        return emptyList() // Заглушка
    }
    
    fun getHelpImage(imageName: String): Any? {
        return null // Заглушка
    }
    
    fun loadProperties(fileName: String): Properties? {
        return null // Заглушка
    }
    
    fun getResourceInfo(): String {
        return "ResourceManagerStub - заглушка для тестирования"
    }
} 