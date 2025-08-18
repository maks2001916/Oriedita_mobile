package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point

/**
 * Обработчик выбора всех линий
 * Адаптированная версия MouseHandlerSelectAll для Android
 */
class MouseHandlerSelectAll : BaseMouseHandler() {
    
    private var allLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        // Выбрать все линии при нажатии
        selectAllLines()
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // При перетаскивании также выбираем все линии
        selectAllLines()
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        // При отпускании подтверждаем выбор всех линий
        confirmSelection()
        return true
    }
    
    private fun selectAllLines() {
        // Здесь должна быть логика выбора всех линий на холсте
        // Пока просто логируем
        println("Выбраны все линии (${allLines.size} линий)")
    }
    
    private fun confirmSelection() {
        // Здесь должна быть логика подтверждения выбора
        println("Подтвержден выбор всех линий")
    }
    
    fun setAllLines(lines: List<LineSegment>) {
        allLines.clear()
        allLines.addAll(lines)
    }
    
    fun getAllLines(): List<LineSegment> = allLines.toList()
    
    fun getSelectedCount(): Int = allLines.size
    
    override fun getName(): String = "Выбор всех"
    
    override fun getDescription(): String = "Выберите все линии на холсте"
} 