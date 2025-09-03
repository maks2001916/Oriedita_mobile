package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик симметричных линий
 * Адаптированная версия MouseHandlerSymmetricLine для Android
 */
class MouseHandlerSymmetricLine : BaseMouseHandler() {
    
    private var symmetryLine: LineSegment? = null
    private var startPoint: Point? = null
    private var currentLine: LineSegment? = null
    private var isDrawing = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        isDrawing = true
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing) return false
        
        val currentPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать временную линию
        currentLine = LineSegment(start, currentPoint)
        
        // Если есть линия симметрии, создать отраженную линию
        symmetryLine?.let { symLine ->
            createSymmetricLine(currentLine!!, symLine)
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing) return false
        
        val endPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать финальную линию
        val finalLine = LineSegment(start, endPoint)
        addLineToCanvas(finalLine)
        
        // Если есть линия симметрии, создать отраженную линию
        symmetryLine?.let { symLine ->
            val reflectedLine = createSymmetricLine(finalLine, symLine)
            addLineToCanvas(reflectedLine)
        }
        
        // Сбросить состояние
        startPoint = null
        currentLine = null
        isDrawing = false
        
        return true
    }
    
    private fun createSymmetricLine(line: LineSegment, symmetryLine: LineSegment): LineSegment {
        // Вычислить отраженные точки
        val reflectedStart = reflectPoint(line.a, symmetryLine)
        val reflectedEnd = reflectPoint(line.b, symmetryLine)
        
        return LineSegment(reflectedStart, reflectedEnd)
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
    
    private fun addLineToCanvas(line: LineSegment) {
        // Здесь должна быть логика добавления линии на холст
        println("Добавлена симметричная линия: (${line.a.x}, ${line.a.y}) -> (${line.b.x}, ${line.b.y})")
    }
    
    fun setSymmetryLine(line: LineSegment) {
        symmetryLine = line
    }
    
    fun getSymmetryLine(): LineSegment? = symmetryLine
    
    fun getCurrentLine(): LineSegment? = currentLine
    
    fun isDrawing(): Boolean = isDrawing
    
    override fun getName(): String = "Симметричные линии"
    
    override fun getDescription(): String = "Нарисуйте линию с симметричным отражением"
} 