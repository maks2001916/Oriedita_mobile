package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик разгибания
 * Адаптированная версия MouseHandlerUnfold для Android
 */
class MouseHandlerUnfold : BaseMouseHandler() {
    
    private var unfoldLine: LineSegment? = null
    private var startPoint: Point? = null
    private var isUnfolding = false
    private var unfoldedLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        isUnfolding = true
        
        println("Начало разгибания в точке: (${point.x}, ${point.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isUnfolding) return false
        
        val currentPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать временную линию разгиба
        unfoldLine = LineSegment(start, currentPoint)
        
        println("Предварительное разгибание: (${start.x}, ${start.y}) -> (${currentPoint.x}, ${currentPoint.y})")
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isUnfolding) return false
        
        val endPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать финальную линию разгиба
        val finalUnfoldLine = LineSegment(start, endPoint)
        unfoldLine = finalUnfoldLine
        
        // Выполнить разгибание
        performUnfold(finalUnfoldLine)
        
        // Сбросить состояние
        startPoint = null
        unfoldLine = null
        isUnfolding = false
        
        return true
    }
    
    private fun performUnfold(line: LineSegment) {
        // Здесь должна быть логика выполнения разгибания
        println("Выполнено разгибание: (${line.a.x}, ${line.a.y}) -> (${line.b.x}, ${line.b.y})")
        
        // Найти и разогнуть линии, пересекающие линию разгиба
        val intersectingLines = findIntersectingLines(line)
        
        for (intersectingLine in intersectingLines) {
            val unfoldedLine = unfoldLine(intersectingLine, line)
            unfoldedLines.add(unfoldedLine)
            addUnfoldedLineToCanvas(unfoldedLine)
        }
        
        println("Разогнуто линий: ${unfoldedLines.size}")
    }
    
    private fun findIntersectingLines(unfoldLine: LineSegment): List<LineSegment> {
        // Здесь должна быть логика поиска линий, пересекающих линию разгиба
        // Пока возвращаем пустой список
        return emptyList()
    }
    
    private fun unfoldLine(line: LineSegment, unfoldLine: LineSegment): LineSegment {
        // Здесь должна быть логика разгибания линии относительно линии разгиба
        // Пока возвращаем исходную линию
        return line
    }
    
    private fun addUnfoldedLineToCanvas(line: LineSegment) {
        // Здесь должна быть логика добавления разогнутой линии на холст
        println("Добавлена разогнутая линия: (${line.a.x}, ${line.a.y}) -> (${line.b.x}, ${line.b.y})")
    }
    
    fun getCurrentUnfoldLine(): LineSegment? = unfoldLine
    
    fun getStartPoint(): Point? = startPoint
    
    fun isUnfolding(): Boolean = isUnfolding
    
    fun getUnfoldedLines(): List<LineSegment> = unfoldedLines.toList()
    
    fun clearUnfoldedLines() {
        unfoldedLines.clear()
    }
    
    fun getUnfoldedCount(): Int = unfoldedLines.size
    
    override fun getName(): String = "Разгибание"
    
    override fun getDescription(): String = "Выполните разгибание по линии"
} 