package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик перемещения линий сгиба
 * Адаптированная версия MouseHandlerCreaseMove для Android
 */
class MouseHandlerCreaseMove : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var lastPoint: Point? = null
    private var isMoving = false
    private var selectedLines = mutableListOf<LineSegment>()
    private var originalPositions = mutableMapOf<LineSegment, Pair<Point, Point>>()
    private var delta = Point(0.0, 0.0)
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        lastPoint = point
        isMoving = true
        
        // Сохранить исходные позиции выбранных линий
        saveOriginalPositions()
        
        println("Начало перемещения линий сгиба в точке: (${point.x}, ${point.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isMoving) return false
        
        val currentPoint = offsetToPoint(offset)
        val last = lastPoint ?: return false
        
        // Вычислить смещение
        val deltaX = currentPoint.x - last.x
        val deltaY = currentPoint.y - last.y
        
        delta = Point(deltaX, deltaY)
        
        // Переместить выбранные линии
        moveSelectedLines(deltaX, deltaY)
        
        lastPoint = currentPoint
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isMoving) return false
        
        val endPoint = offsetToPoint(offset)
        
        // Выполнить финальное перемещение
        performFinalMove()
        
        // Сбросить состояние
        startPoint = null
        lastPoint = null
        isMoving = false
        delta = Point(0.0, 0.0)
        
        println("Завершено перемещение линий сгиба")
        
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
    
    private fun performFinalMove() {
        if (delta.distance(Point(0.0, 0.0)) > 0.001) {
            // Создать временный набор линий для перемещения
            val tempLineSet = createTempLineSet()
            
            // Удалить выбранные линии из основного набора
            removeSelectedLines()
            
            // Переместить временный набор
            tempLineSet.move(delta.x, delta.y)
            
            // Добавить перемещенные линии обратно
            addMovedLines(tempLineSet)
            
            // Разделить линии с новыми линиями
            divideLinesWithNewLines()
            
            // Снять выделение
            clearSelection()
            
            println("Выполнено перемещение на (${delta.x}, ${delta.y})")
        }
    }
    
    private fun createTempLineSet(): TempLineSet {
        // Здесь должна быть логика создания временного набора линий
        return TempLineSet()
    }
    
    private fun removeSelectedLines() {
        // Здесь должна быть логика удаления выбранных линий
        println("Удалены выбранные линии")
    }
    
    private fun addMovedLines(tempLineSet: TempLineSet) {
        // Здесь должна быть логика добавления перемещенных линий
        println("Добавлены перемещенные линии")
    }
    
    private fun divideLinesWithNewLines() {
        // Здесь должна быть логика разделения линий с новыми линиями
        println("Выполнено разделение линий")
    }
    
    private fun updateLinePosition(line: LineSegment, newStart: Point, newEnd: Point) {
        // Здесь должна быть логика обновления позиции линии
        println("Обновлена позиция линии: (${newStart.x}, ${newStart.y}) -> (${newEnd.x}, ${newEnd.y})")
    }
    
    private fun clearSelection() {
        selectedLines.clear()
        originalPositions.clear()
        println("Выделение очищено")
    }
    
    fun setSelectedLines(lines: List<LineSegment>) {
        selectedLines.clear()
        selectedLines.addAll(lines)
    }
    
    fun getSelectedLines(): List<LineSegment> = selectedLines.toList()
    
    fun getDelta(): Point = delta
    
    fun isMoving(): Boolean = isMoving
    
    fun getMoveDistance(): Double = delta.distance(Point(0.0, 0.0))
    
    // Временный класс для работы с набором линий
    private class TempLineSet {
        private val lines = mutableListOf<LineSegment>()
        
        fun move(deltaX: Double, deltaY: Double) {
            lines.forEach { line ->
                val newStart = Point(line.determineAX() + deltaX, line.determineAY() + deltaY)
                val newEnd = Point(line.determineBX() + deltaX, line.determineBY() + deltaY)
                // Обновить позицию линии
            }
        }
        
        fun addLine(line: LineSegment) {
            lines.add(line)
        }
        
        fun getLines(): List<LineSegment> = lines.toList()
    }
    
    override fun getName(): String = "Перемещение линий сгиба"
    
    override fun getDescription(): String = "Переместите выбранные линии сгиба"
} 