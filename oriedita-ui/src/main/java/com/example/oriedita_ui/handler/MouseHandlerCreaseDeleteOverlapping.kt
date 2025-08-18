package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик удаления перекрывающихся линий
 * Адаптированная версия MouseHandlerCreaseDeleteOverlapping для Android
 */
class MouseHandlerCreaseDeleteOverlapping : BaseMouseHandler() {
    
    private var anchorPoint: Point? = null
    private var releasePoint: Point? = null
    private var dragSegment: LineSegment? = null
    private var isDragging = false
    private var deletedLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        isDragging = true
        
        // Найти ближайшую точку
        val closestPoint = findClosestPoint(point)
        
        if (point.distance(closestPoint) < getSelectionDistance()) {
            anchorPoint = closestPoint
            println("Установлена якорная точка: (${closestPoint.x}, ${closestPoint.y})")
        } else {
            anchorPoint = null
            println("Якорная точка не установлена - слишком далеко")
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDragging || anchorPoint == null) return false
        
        val currentPoint = offsetToPoint(offset)
        
        // Найти ближайшую точку к текущей позиции
        val closestPoint = findClosestPoint(currentPoint)
        
        if (currentPoint.distance(closestPoint) < getSelectionDistance()) {
            releasePoint = closestPoint
        } else {
            releasePoint = currentPoint
        }
        
        // Создать сегмент перетаскивания
        dragSegment = LineSegment(anchorPoint!!, releasePoint!!, getLineColor())
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDragging) return false
        
        val endPoint = offsetToPoint(offset)
        
        // Проверить, что якорная точка установлена
        if (anchorPoint == null) {
            reset()
            return true
        }
        
        // Найти ближайшую точку к конечной позиции
        val closestEndPoint = findClosestPoint(endPoint)
        
        if (endPoint.distance(closestEndPoint) > getSelectionDistance()) {
            // Конечная точка слишком далеко - сбросить
            reset()
            println("Конечная точка слишком далеко - операция отменена")
            return true
        }
        
        releasePoint = closestEndPoint
        
        // Создать финальный сегмент
        dragSegment = LineSegment(anchorPoint!!, releasePoint!!, getLineColor())
        
        // Выполнить удаление перекрывающихся линий
        if (dragSegment?.determineLength() ?: 0.0 > 0.001) {
            performDeleteOverlapping()
        }
        
        // Сбросить состояние
        reset()
        
        return true
    }
    
    private fun performDeleteOverlapping() {
        dragSegment?.let { segment ->
            // Удалить линии, перекрывающиеся с выбранным сегментом
            val deletedCount = deleteOverlappingLines(segment)
            
            println("Удалено перекрывающихся линий: $deletedCount")
        }
    }
    
    private fun deleteOverlappingLines(segment: LineSegment): Int {
        deletedLines.clear()
        
        // Получить все линии
        val allLines = getAllLines()
        var deletedCount = 0
        
        for (line in allLines) {
            if (isLineOverlapping(line, segment)) {
                // Удалить перекрывающуюся линию
                removeLineFromSet(line)
                deletedLines.add(line)
                deletedCount++
                
                println("Удалена перекрывающаяся линия: (${line.determineAX()}, ${line.determineAY()}) -> (${line.determineBX()}, ${line.determineBY()})")
            }
        }
        
        return deletedCount
    }
    
    private fun isLineOverlapping(line1: LineSegment, line2: LineSegment): Boolean {
        // Здесь должна быть логика проверки перекрытия двух линий
        // Пока возвращаем false
        return false
    }
    
    override fun reset() {
        anchorPoint = null
        releasePoint = null
        dragSegment = null
        isDragging = false
    }
    
    private fun findClosestPoint(point: Point): Point {
        // Здесь должна быть логика поиска ближайшей точки
        // Пока возвращаем исходную точку
        return point
    }
    
    override fun getSelectionDistance(): Double {
        // Здесь должна быть логика получения расстояния выбора
        return 10.0
    }
    
    override fun getLineColor(): LineColor {
        // Здесь должна быть логика получения текущего цвета линии
        return LineColor.BLACK_0
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
    
    fun getAnchorPoint(): Point? = anchorPoint
    
    fun getReleasePoint(): Point? = releasePoint
    
    fun getDragSegment(): LineSegment? = dragSegment
    
    fun getDeletedLines(): List<LineSegment> = deletedLines.toList()
    
    fun getDeletedCount(): Int = deletedLines.size
    
    fun isDragging(): Boolean = isDragging
    
    fun clearDeletedLines() {
        deletedLines.clear()
    }
    
    fun getSegmentLength(): Double {
        return dragSegment?.determineLength() ?: 0.0
    }
    
    fun hasValidSegment(): Boolean {
        return dragSegment?.determineLength() ?: 0.0 > 0.001
    }
    
    fun getSegmentDescription(): String {
        return dragSegment?.let { segment ->
            "Сегмент: (${segment.determineAX()}, ${segment.determineAY()}) -> (${segment.determineBX()}, ${segment.determineBY()})"
        } ?: "Нет сегмента"
    }
    
    override fun getName(): String = "Удаление перекрывающихся линий"
    
    override fun getDescription(): String = "Удалите линии, перекрывающиеся с выбранным сегментом"
} 