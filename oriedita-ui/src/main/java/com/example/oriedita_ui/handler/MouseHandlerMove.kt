package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point

/**
 * Обработчик перемещения линий
 * Адаптированная версия MouseHandlerMove для Android
 */
class MouseHandlerMove : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var lastPoint: Point? = null
    private var isMoving = false
    private var selectedLines = mutableListOf<LineSegment>()
    private var originalPositions = mutableMapOf<LineSegment, Pair<Point, Point>>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        lastPoint = point
        isMoving = true
        
        // Сохранить исходные позиции выбранных линий
        saveOriginalPositions()
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isMoving) return false
        
        val currentPoint = offsetToPoint(offset)
        val last = lastPoint ?: return false
        
        // Вычислить смещение
        val deltaX = currentPoint.x - last.x
        val deltaY = currentPoint.y - last.y
        
        // Переместить выбранные линии
        moveSelectedLines(deltaX, deltaY)
        
        lastPoint = currentPoint
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        isMoving = false
        startPoint = null
        lastPoint = null
        
        // Подтвердить перемещение
        confirmMove()
        
        return true
    }
    
    private fun saveOriginalPositions() {
        originalPositions.clear()
        selectedLines.forEach { line ->
            val start = Point(line.determineAX(), line.determineAY())
            val end = Point(line.determineBX(), line.determineBY())
            originalPositions[line] = Pair(start, end)
        }
    }
    
    private fun moveSelectedLines(deltaX: Double, deltaY: Double) {
        selectedLines.forEach { line ->
            val original = originalPositions[line]
            if (original != null) {
                val newStart = Point(original.first.x + deltaX, original.first.y + deltaY)
                val newEnd = Point(original.second.x + deltaX, original.second.y + deltaY)
                
                // Обновить позицию линии
                updateLinePosition(line, newStart, newEnd)
            }
        }
    }
    
    private fun updateLinePosition(line: LineSegment, newStart: Point, newEnd: Point) {
        // Здесь должна быть логика обновления позиции линии
        println("Перемещена линия: ${line.determineAX()}, ${line.determineAY()} -> ${line.determineBX()}, ${line.determineBY()}")
    }
    
    private fun confirmMove() {
        // Здесь должна быть логика подтверждения перемещения
        println("Подтверждено перемещение ${selectedLines.size} линий")
    }
    
    fun setSelectedLines(lines: List<LineSegment>) {
        selectedLines.clear()
        selectedLines.addAll(lines)
    }
    
    fun getSelectedLines(): List<LineSegment> = selectedLines.toList()
    
    fun isMoving(): Boolean = isMoving
    
    override fun getName(): String = "Перемещение"
    
    override fun getDescription(): String = "Переместите выбранные линии"
} 