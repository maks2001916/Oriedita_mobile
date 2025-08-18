package com.example.oriedita_ui.ui.constants

import androidx.compose.ui.unit.dp

/**
 * Константы для пользовательского интерфейса
 */
object UiConstants {
    
    // Размеры панели инструментов
    object DockBar {
        val WIDTH_HORIZONTAL = 300.dp
        val HEIGHT_HORIZONTAL = 60.dp
        val WIDTH_VERTICAL = 60.dp
        val HEIGHT_VERTICAL = 300.dp
        
        val CORNER_RADIUS = 16.dp
        val MAGNET_THRESHOLD = 32.dp
        val BUTTON_SIZE = 48.dp
        val PADDING = 8.dp
    }
    
    // Цвета
    object Colors {
        val DOCK_BAR_BACKGROUND = androidx.compose.ui.graphics.Color(0xFF2C3E50)
        val BUTTON_BACKGROUND = androidx.compose.ui.graphics.Color.LightGray
        val BUTTON_ICON_TINT = androidx.compose.ui.graphics.Color.White
        val CANVAS_BACKGROUND = androidx.compose.ui.graphics.Color.White
    }
    
    // Количество инструментов в панели
    const val TOOL_COUNT = 5
    
    // Названия инструментов
    val TOOL_NAMES = listOf(
        "Выбор",
        "Линия",
        "Окружность", 
        "Текст",
        "Стирание"
    )
} 