package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик удаления пересекающихся линий
 * Адаптированная версия MouseHandlerCreaseDeleteIntersecting для Android
 */
class MouseHandlerCreaseDeleteIntersecting : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var selectionLine: LineSegment? = null
    private var isSelecting = false
    private var deletedLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        isSelecting = true
        
        println("Начало выбора линии для удаления пересекающихся в точке: (${point.x}, ${point.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isSelecting) return false
        
        val currentPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать линию выбора
        selectionLine = LineSegment(start, currentPoint)
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isSelecting) return false
        
        val endPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать финальную линию выбора
        selectionLine = LineSegment(start, endPoint)
        
        // Выполнить удаление пересекающихся линий
        if (selectionLine?.determineLength() ?: 0.0 > 0.001) {
            performDeleteIntersecting()
        }
        
        // Сбросить состояние
        startPoint = null
        selectionLine = null
        isSelecting = false
        
        return true
    }
    
    private fun performDeleteIntersecting() {
        selectionLine?.let { line ->
            // Удалить линии, пересекающиеся с выбранной линией
            val deletedCount = deleteIntersectingLines(line)
            
            println("Удалено пересекающихся линий: $deletedCount")
        }
    }
    
    private fun deleteIntersectingLines(selectionLine: LineSegment): Int {
        deletedLines.clear()
        
        // Получить все линии
        val allLines = getAllLines()
        var deletedCount = 0
        
        for (line in allLines) {
            if (isLineIntersecting(line, selectionLine)) {
                // Удалить пересекающуюся линию
                removeLineFromSet(line)
                deletedLines.add(line)
                deletedCount++
                
                println("Удалена пересекающаяся линия: (${line.determineAX()}, ${line.determineAY()}) -> (${line.determineBX()}, ${line.determineBY()})")
            }
        }
        
        return deletedCount
    }
    
    private fun isLineIntersecting(line1: LineSegment, line2: LineSegment): Boolean {
        // Здесь должна быть логика проверки пересечения двух линий
        // Пока возвращаем false
        return false
    }
    
    private fun getAllLines(): List<LineSegment> {
        // Здесь должна быть логика получения всех линий
        // Пока возвращаем пустой список
        return emptyList()
    }
    
    private fun removeLineFromSet(line: LineSegment) {
        // Здесь должна быть логика удаления линии из набора
        println("Линия удалена из набора")
    }
    
    fun getSelectionLine(): LineSegment? = selectionLine
    
    fun getDeletedLines(): List<LineSegment> = deletedLines.toList()
    
    fun getDeletedCount(): Int = deletedLines.size
    
    fun isSelecting(): Boolean = isSelecting
    
    fun clearDeletedLines() {
        deletedLines.clear()
    }
    
    fun getSelectionLength(): Double {
        return selectionLine?.determineLength() ?: 0.0
    }
    
    fun hasValidSelection(): Boolean {
        return selectionLine?.determineLength() ?: 0.0 > 0.001
    }
    
    override fun getName(): String = "Удаление пересекающихся линий"
    
    override fun getDescription(): String = "Удалите линии, пересекающиеся с выбранной линией"
} 