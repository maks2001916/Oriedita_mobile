package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик симметричных точек
 * Адаптированная версия MouseHandlerSymmetricPoint для Android
 */
class MouseHandlerSymmetricPoint : BaseMouseHandler() {
    
    private var symmetryLine: LineSegment? = null
    private var originalPoint: Point? = null
    private var reflectedPoint: Point? = null
    private var isDrawing = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        originalPoint = point
        isDrawing = true
        
        // Если есть линия симметрии, создать отраженную точку
        symmetryLine?.let { symLine ->
            reflectedPoint = reflectPoint(point, symLine)
            addPointToCanvas(point)
            addPointToCanvas(reflectedPoint!!)
        } ?: run {
            // Если нет линии симметрии, просто добавить точку
            addPointToCanvas(point)
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing) return false
        
        val currentPoint = offsetToPoint(offset)
        originalPoint = currentPoint
        
        // Если есть линия симметрии, обновить отраженную точку
        symmetryLine?.let { symLine ->
            reflectedPoint = reflectPoint(currentPoint, symLine)
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing) return false
        
        val finalPoint = offsetToPoint(offset)
        originalPoint = finalPoint
        
        // Если есть линия симметрии, создать финальную отраженную точку
        symmetryLine?.let { symLine ->
            reflectedPoint = reflectPoint(finalPoint, symLine)
            addPointToCanvas(finalPoint)
            addPointToCanvas(reflectedPoint!!)
        } ?: run {
            addPointToCanvas(finalPoint)
        }
        
        // Сбросить состояние
        originalPoint = null
        reflectedPoint = null
        isDrawing = false
        
        return true
    }
    
    private fun reflectPoint(point: Point, symmetryLine: LineSegment): Point {
        // Формула отражения точки относительно прямой
        val x = point.x
        val y = point.y
        
        val x1 = symmetryLine.a.x
        val y1 = symmetryLine.a.y
        val x2 = symmetryLine.b.x
        val y2 = symmetryLine.b.y
        
        val dx = x2 - x1
        val dy = y2 - y1
        
        if (dx == 0.0 && dy == 0.0) return point
        
        val a = (dx * dx - dy * dy) / (dx * dx + dy * dy)
        val b = 2 * dx * dy / (dx * dx + dy * dy)
        
        val reflectedX = a * (x - x1) + b * (y - y1) + x1
        val reflectedY = b * (x - x1) - a * (y - y1) + y1
        
        return Point(reflectedX, reflectedY)
    }
    
    private fun addPointToCanvas(point: Point) {
        // Здесь должна быть логика добавления точки на холст
        println("Добавлена симметричная точка: (${point.x}, ${point.y})")
    }
    
    fun setSymmetryLine(line: LineSegment) {
        symmetryLine = line
    }
    
    fun getSymmetryLine(): LineSegment? = symmetryLine
    
    fun getOriginalPoint(): Point? = originalPoint
    
    fun getReflectedPoint(): Point? = reflectedPoint
    
    fun isDrawing(): Boolean = isDrawing
    
    override fun getName(): String = "Симметричные точки"
    
    override fun getDescription(): String = "Нарисуйте точку с симметричным отражением"
} 