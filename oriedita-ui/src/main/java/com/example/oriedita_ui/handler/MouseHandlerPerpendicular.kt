package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc
import kotlin.math.sqrt

/**
 * Обработчик перпендикуляров
 * Адаптированная версия MouseHandlerPerpendicular для Android
 */
class MouseHandlerPerpendicular : BaseMouseHandler() {
    
    private var baseLine: LineSegment? = null
    private var basePoint: Point? = null
    private var currentPerpendicular: LineSegment? = null
    private var isDrawing = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        if (baseLine == null) {
            // Первый клик - выбрать базовую линию
            basePoint = point
            isDrawing = true
        } else {
            // Второй клик - создать перпендикуляр
            createPerpendicular(point)
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing) return false
        
        val currentPoint = offsetToPoint(offset)
        
        if (baseLine == null) {
            // Показать предварительный просмотр базовой линии
            basePoint?.let { start ->
                currentPerpendicular = LineSegment(start, currentPoint)
            }
        } else {
            // Показать предварительный просмотр перпендикуляра
            val perpPoint = calculatePerpendicularPoint(currentPoint, baseLine!!)
            currentPerpendicular = LineSegment(currentPoint, perpPoint)
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing) return false
        
        val endPoint = offsetToPoint(offset)
        
        if (baseLine == null) {
            // Создать базовую линию
            basePoint?.let { start ->
                baseLine = LineSegment(start, endPoint)
                addLineToCanvas(baseLine!!)
                println("Создана базовая линия: (${start.x}, ${start.y}) -> (${endPoint.x}, ${endPoint.y})")
            }
        } else {
            // Создать перпендикуляр
            createPerpendicular(endPoint)
        }
        
        return true
    }
    
    private fun createPerpendicular(point: Point) {
        baseLine?.let { line ->
            val perpPoint = calculatePerpendicularPoint(point, line)
            val perpendicular = LineSegment(point, perpPoint)
            currentPerpendicular = perpendicular
            addLineToCanvas(perpendicular)
            
            println("Создан перпендикуляр: (${point.x}, ${point.y}) -> (${perpPoint.x}, ${perpPoint.y})")
            
            // Сбросить состояние
            baseLine = null
            basePoint = null
            isDrawing = false
        }
    }
    
    private fun calculatePerpendicularPoint(point: Point, baseLine: LineSegment): Point {
        val x0 = point.x
        val y0 = point.y
        
        val x1 = baseLine.a.x
        val y1 = baseLine.a.y
        val x2 = baseLine.b.x
        val y2 = baseLine.b.y
        
        // Вычислить направляющий вектор базовой линии
        val dx = x2 - x1
        val dy = y2 - y1
        
        if (dx == 0.0 && dy == 0.0) {
            // Если базовая линия - точка, вернуть исходную точку
            return point
        }
        
        // Вычислить перпендикулярный вектор
        val perpX = -dy
        val perpY = dx
        
        // Нормализовать перпендикулярный вектор
        val perpLen = sqrt(perpX * perpX + perpY * perpY)
        val normalizedPerpX = perpX / perpLen
        val normalizedPerpY = perpY / perpLen
        
        // Найти точку пересечения перпендикуляра с базовой линией
        val t = ((x0 - x1) * dx + (y0 - y1) * dy) / (dx * dx + dy * dy)
        val intersectionX = x1 + t * dx
        val intersectionY = y1 + t * dy
        
        // Создать точку на перпендикуляре на расстоянии от точки пересечения
        val distance = 50.0 // Расстояние от точки пересечения
        val perpPointX = intersectionX + normalizedPerpX * distance
        val perpPointY = intersectionY + normalizedPerpY * distance
        
        return Point(perpPointX, perpPointY)
    }
    
    private fun addLineToCanvas(line: LineSegment) {
        // Здесь должна быть логика добавления линии на холст
        println("Добавлена линия: (${line.a.x}, ${line.a.y}) -> (${line.b.x}, ${line.b.y})")
    }
    
    fun getBaseLine(): LineSegment? = baseLine
    
    fun getCurrentPerpendicular(): LineSegment? = currentPerpendicular
    
    fun getBasePoint(): Point? = basePoint
    
    fun isDrawing(): Boolean = isDrawing
    
    override fun getName(): String = "Перпендикуляр"
    
    override fun getDescription(): String = "Нарисуйте перпендикуляр к линии"
} 