package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point

/**
 * Обработчик отмены выбора линий
 * Адаптированная версия MouseHandlerUnselect для Android
 */
class MouseHandlerUnselect : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var unselectedLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        
        // Найти ближайшую выбранную линию к точке
        val closestLine = findClosestSelectedLine(point)
        if (closestLine != null) {
            unselectLine(closestLine)
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val currentPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Отменить выбор всех линий в прямоугольнике
        val minX = minOf(start.x, currentPoint.x)
        val maxX = maxOf(start.x, currentPoint.x)
        val minY = minOf(start.y, currentPoint.y)
        val maxY = maxOf(start.y, currentPoint.y)
        
        val linesInBox = findSelectedLinesInBox(minX, maxX, minY, maxY)
        linesInBox.forEach { unselectLine(it) }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        startPoint = null
        return true
    }
    
    private fun findClosestSelectedLine(point: Point): LineSegment? {
        // Здесь должна быть логика поиска ближайшей выбранной линии
        // Пока возвращаем null
        return null
    }
    
    private fun findSelectedLinesInBox(minX: Double, maxX: Double, minY: Double, maxY: Double): List<LineSegment> {
        // Здесь должна быть логика поиска выбранных линий в прямоугольнике
        // Пока возвращаем пустой список
        return emptyList()
    }
    
    private fun unselectLine(line: LineSegment) {
        // Здесь должна быть логика отмены выбора линии
        unselectedLines.add(line)
        println("Отменен выбор линии: ${line.determineAX()}, ${line.determineAY()} -> ${line.determineBX()}, ${line.determineBY()}")
    }
    
    fun getUnselectedLines(): List<LineSegment> = unselectedLines.toList()
    
    fun clearUnselectedLines() {
        unselectedLines.clear()
    }
    
    override fun getName(): String = "Отмена выбора"
    
    override fun getDescription(): String = "Отмените выбор линий"
} 