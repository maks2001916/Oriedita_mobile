package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc
import kotlin.math.sqrt

/**
 * Обработчик параллельных линий
 * Адаптированная версия MouseHandlerParallel для Android
 */
class MouseHandlerParallel : BaseMouseHandler() {
    
    private var baseLine: LineSegment? = null
    private var basePoint: Point? = null
    private var currentParallel: LineSegment? = null
    private var isDrawing = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        if (baseLine == null) {
            // Первый клик - выбрать базовую линию
            basePoint = point
            isDrawing = true
        } else {
            // Второй клик - создать параллельную линию
            createParallel(point)
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing) return false
        
        val currentPoint = offsetToPoint(offset)
        
        if (baseLine == null) {
            // Показать предварительный просмотр базовой линии
            basePoint?.let { start ->
                currentParallel = LineSegment(start, currentPoint)
            }
        } else {
            // Показать предварительный просмотр параллельной линии
            val parallelPoints = calculateParallelPoints(currentPoint, baseLine!!)
            currentParallel = LineSegment(parallelPoints.first, parallelPoints.second)
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
            // Создать параллельную линию
            createParallel(endPoint)
        }
        
        return true
    }
    
    private fun createParallel(point: Point) {
        baseLine?.let { line ->
            val parallelPoints = calculateParallelPoints(point, line)
            val parallel = LineSegment(parallelPoints.first, parallelPoints.second)
            currentParallel = parallel
            addLineToCanvas(parallel)
            
            println("Создана параллельная линия: (${parallelPoints.first.x}, ${parallelPoints.first.y}) -> (${parallelPoints.second.x}, ${parallelPoints.second.y})")
            
            // Сбросить состояние
            baseLine = null
            basePoint = null
            isDrawing = false
        }
    }
    
    private fun calculateParallelPoints(point: Point, baseLine: LineSegment): Pair<Point, Point> {
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
            return Pair(point, point)
        }
        
        // Вычислить перпендикулярный вектор для смещения
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
        
        // Вычислить смещение от базовой линии
        val offsetX = x0 - intersectionX
        val offsetY = y0 - intersectionY
        val offsetDistance = sqrt(offsetX * offsetX + offsetY * offsetY)
        
        // Создать параллельную линию с тем же смещением
        val parallelStartX = x1 + normalizedPerpX * offsetDistance
        val parallelStartY = y1 + normalizedPerpY * offsetDistance
        val parallelEndX = x2 + normalizedPerpX * offsetDistance
        val parallelEndY = y2 + normalizedPerpY * offsetDistance
        
        return Pair(Point(parallelStartX, parallelStartY), Point(parallelEndX, parallelEndY))
    }
    
    private fun addLineToCanvas(line: LineSegment) {
        // Здесь должна быть логика добавления линии на холст
        println("Добавлена линия: (${line.a.x}, ${line.a.y}) -> (${line.b.x}, ${line.b.y})")
    }
    
    fun getBaseLine(): LineSegment? = baseLine
    
    fun getCurrentParallel(): LineSegment? = currentParallel
    
    fun getBasePoint(): Point? = basePoint
    
    fun isDrawing(): Boolean = isDrawing
    
    override fun getName(): String = "Параллельная линия"
    
    override fun getDescription(): String = "Нарисуйте параллельную линию"
} 