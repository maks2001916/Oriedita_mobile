package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик рисования симметричных линий сгиба
 * Адаптированная версия MouseHandlerDrawCreaseSymmetric для Android
 */
class MouseHandlerDrawCreaseSymmetric : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var currentLine: LineSegment? = null
    private var lineColor: LineColor = LineColor.BLACK_0
    private var symmetryLine: LineSegment? = null
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val currentPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать временную линию для предварительного просмотра
        currentLine = LineSegment(start, currentPoint)
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val endPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать симметричные линии
        val lines = createSymmetricLines(start, endPoint)
        lines.forEach { addLineToCanvas(it) }
        
        // Сбросить состояние
        startPoint = null
        currentLine = null
        
        return true
    }
    
    private fun createSymmetricLines(start: Point, end: Point): List<LineSegment> {
        val lines = mutableListOf<LineSegment>()
        
        // Добавить основную линию
        lines.add(LineSegment(start, end))
        
        // Добавить симметричную линию, если есть линия симметрии
        symmetryLine?.let { symLine ->
            val symmetricStart = reflectPoint(start, symLine)
            val symmetricEnd = reflectPoint(end, symLine)
            lines.add(LineSegment(symmetricStart, symmetricEnd))
        }
        
        return lines
    }
    
    private fun reflectPoint(point: Point, line: LineSegment): Point {
        // Здесь должна быть логика отражения точки относительно линии
        // Пока возвращаем исходную точку
        return point
    }
    
    private fun addLineToCanvas(line: LineSegment) {
        // Здесь должна быть логика добавления линии на холст
        println("Добавлена симметричная линия: ${line.determineAX()}, ${line.determineAY()} -> ${line.determineBX()}, ${line.determineBY()}")
    }
    
    fun setLineColor(color: LineColor) {
        lineColor = color
    }
    
    fun setSymmetryLine(line: LineSegment) {
        symmetryLine = line
    }
    
    fun getCurrentLine(): LineSegment? = currentLine
    
    override fun getName(): String = "Рисование симметричных линий"
    
    override fun getDescription(): String = "Нарисуйте симметричные линии сгиба"
} 