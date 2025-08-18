package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик снятия выделения с линий сгиба
 * Адаптированная версия MouseHandlerCreaseUnselect для Android
 */
class MouseHandlerCreaseUnselect : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var unselectedLines = mutableListOf<LineSegment>()
    private var isUnselecting = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        isUnselecting = true
        
        println("Начало снятия выделения в точке: (${point.x}, ${point.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isUnselecting) return false
        
        val currentPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать прямоугольник для снятия выделения
        val minX = minOf(start.x, currentPoint.x)
        val maxX = maxOf(start.x, currentPoint.x)
        val minY = minOf(start.y, currentPoint.y)
        val maxY = maxOf(start.y, currentPoint.y)
        
        val linesInBox = findLinesInBox(minX, maxX, minY, maxY)
        unselectedLines.clear()
        unselectedLines.addAll(linesInBox)
        
        println("Прямоугольное снятие выделения: ${linesInBox.size} линий")
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isUnselecting) return false
        
        val endPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Выполнить снятие выделения
        performUnselect(start, endPoint)
        
        // Сбросить состояние
        startPoint = null
        isUnselecting = false
        
        return true
    }
    
    private fun performUnselect(start: Point, end: Point) {
        val beforeSelectNum = getSelectedLinesCount()
        
        // Снять выделение в прямоугольнике
        unselectInRectangle(start, end)
        
        // Если точки близки, снять выделение с ближайшей линии
        if (start.distance(end) <= 0.001) {
            val closestLine = findClosestLine(start)
            if (closestLine != null) {
                unselectLine(closestLine)
                println("Снято выделение с ближайшей линии")
            }
        }
        
        val afterSelectNum = getSelectedLinesCount()
        
        if (beforeSelectNum != afterSelectNum) {
            println("Снято выделение: было $beforeSelectNum, стало $afterSelectNum")
        }
    }
    
    private fun unselectInRectangle(start: Point, end: Point) {
        val minX = minOf(start.x, end.x)
        val maxX = maxOf(start.x, end.x)
        val minY = minOf(start.y, end.y)
        val maxY = maxOf(start.y, end.y)
        
        val linesInBox = findLinesInBox(minX, maxX, minY, maxY)
        
        for (line in linesInBox) {
            unselectLine(line)
        }
        
        println("Снято выделение с ${linesInBox.size} линий в прямоугольнике")
    }
    
    private fun unselectLine(line: LineSegment) {
        // Здесь должна быть логика снятия выделения с линии
        // Например, установить флаг selected в false
        println("Снято выделение с линии: (${line.determineAX()}, ${line.determineAY()}) -> (${line.determineBX()}, ${line.determineBY()})")
    }
    
    private fun findClosestLine(point: Point): LineSegment? {
        // Здесь должна быть логика поиска ближайшей линии
        // Пока возвращаем null
        return null
    }
    
    private fun findLinesInBox(minX: Double, maxX: Double, minY: Double, maxY: Double): List<LineSegment> {
        // Здесь должна быть логика поиска линий в прямоугольнике
        // Пока возвращаем пустой список
        return emptyList()
    }
    
    private fun getSelectedLinesCount(): Int {
        // Здесь должна быть логика подсчета выбранных линий
        // Пока возвращаем 0
        return 0
    }
    
    fun getUnselectedLines(): List<LineSegment> = unselectedLines.toList()
    
    fun clearUnselectedLines() {
        unselectedLines.clear()
    }
    
    fun getUnselectedCount(): Int = unselectedLines.size
    
    fun isUnselecting(): Boolean = isUnselecting
    
    override fun getName(): String = "Снятие выделения с линий сгиба"
    
    override fun getDescription(): String = "Снимите выделение с линий сгиба"
} 