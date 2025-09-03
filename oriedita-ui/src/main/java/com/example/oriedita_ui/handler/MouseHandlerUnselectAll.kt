package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point

/**
 * Обработчик отмены выбора всех линий
 * Адаптированная версия MouseHandlerUnselectAll для Android
 */
class MouseHandlerUnselectAll : BaseMouseHandler() {
    
    private var selectedLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        // Отменить выбор всех линий при нажатии
        unselectAllLines()
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // При перетаскивании также отменяем выбор всех линий
        unselectAllLines()
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        // При отпускании подтверждаем отмену выбора всех линий
        confirmUnselection()
        return true
    }
    
    private fun unselectAllLines() {
        // Здесь должна быть логика отмены выбора всех линий на холсте
        // Пока просто логируем
        println("Отменен выбор всех линий (${selectedLines.size} линий)")
    }
    
    private fun confirmUnselection() {
        // Здесь должна быть логика подтверждения отмены выбора
        println("Подтверждена отмена выбора всех линий")
    }
    
    fun setSelectedLines(lines: List<LineSegment>) {
        selectedLines.clear()
        selectedLines.addAll(lines)
    }
    
    fun getSelectedLines(): List<LineSegment> = selectedLines.toList()
    
    fun getSelectedCount(): Int = selectedLines.size
    
    override fun getName(): String = "Отмена выбора всех"
    
    override fun getDescription(): String = "Отмените выбор всех линий на холсте"
} 