package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point

/**
 * Базовый класс для обработчиков мыши с выбором лассо
 * Позволяет создавать произвольные области выбора
 * Адаптированная версия для Android
 */
abstract class BaseMouseHandlerLasso : BaseMouseHandler() {
    
    private var lassoPoints = mutableListOf<Point>()
    private var isDrawingLasso = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        if (lassoPoints.isEmpty()) {
            lassoPoints.clear()
            lassoPoints.add(point)
            isDrawingLasso = true
            
            println("Начало рисования лассо: (${point.x}, ${point.y})")
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawingLasso) return false
        
        val point = offsetToPoint(offset)
        lassoPoints.add(point)
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawingLasso) return false
        
        val endPoint = offsetToPoint(offset)
        lassoPoints.add(endPoint)
        
        // Замкнуть путь лассо
        if (lassoPoints.size > 2) {
            lassoPoints.add(lassoPoints.first())
        }
        
        isDrawingLasso = false
        
        println("Завершение лассо с ${lassoPoints.size} точками")
        
        // Выполнить действие
        performAction()
        
        // Очистить путь
        lassoPoints.clear()
        
        return true
    }
    
    /**
     * Абстрактный метод для выполнения действия с выбранной областью
     */
    protected abstract fun performAction()
    
    fun getLassoPoints(): List<Point> = lassoPoints.toList()
    
    fun getLassoPointsCount(): Int = lassoPoints.size
    
    fun isDrawingLasso(): Boolean = isDrawingLasso
    
    fun clearLasso() {
        lassoPoints.clear()
        isDrawingLasso = false
    }
    
    /**
     * Проверить, находится ли точка внутри лассо
     */
    fun isPointInsideLasso(point: Point): Boolean {
        if (lassoPoints.size < 3) return false
        
        var inside = false
        var j = lassoPoints.size - 1
        
        for (i in lassoPoints.indices) {
            val pi = lassoPoints[i]
            val pj = lassoPoints[j]
            
            if (((pi.y > point.y) != (pj.y > point.y)) &&
                (point.x < (pj.x - pi.x) * (point.y - pi.y) / (pj.y - pi.y) + pi.x)) {
                inside = !inside
            }
            j = i
        }
        
        return inside
    }
    
    /**
     * Получить ограничивающий прямоугольник лассо
     */
    fun getLassoBoundingBox(): Rectangle? {
        if (lassoPoints.isEmpty()) return null
        
        val minX = lassoPoints.minOf { it.x }
        val maxX = lassoPoints.maxOf { it.x }
        val minY = lassoPoints.minOf { it.y }
        val maxY = lassoPoints.maxOf { it.y }
        
        return Rectangle(
            topLeft = Point(minX, minY),
            bottomRight = Point(maxX, maxY)
        )
    }
    
    fun getLassoDescription(): String {
        return when {
            lassoPoints.isEmpty() -> "Лассо не нарисовано"
            isDrawingLasso -> "Рисование лассо: ${lassoPoints.size} точек"
            else -> "Лассо завершено: ${lassoPoints.size} точек"
        }
    }
    
    fun getLassoInfo(): String {
        return buildString {
            append("Точек в лассо: ${lassoPoints.size}\n")
            append("Статус: ${if (isDrawingLasso) "рисование" else "завершено"}\n")
            if (lassoPoints.isNotEmpty()) {
                append("Первая точка: (${lassoPoints.first().x}, ${lassoPoints.first().y})\n")
                append("Последняя точка: (${lassoPoints.last().x}, ${lassoPoints.last().y})")
            }
        }
    }
} 