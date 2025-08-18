package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor

/**
 * Обработчик рисования свободных линий сгиба
 * Адаптированная версия MouseHandlerDrawCreaseFree для Android
 */
class MouseHandlerDrawCreaseFree : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var currentLine: LineSegment? = null
    private var lineColor: LineColor = LineColor.BLACK_0
    
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
        
        // Создать финальную линию
        val finalLine = LineSegment(start, endPoint)
        addLineToCanvas(finalLine)
        
        // Сбросить состояние
        startPoint = null
        currentLine = null
        
        return true
    }
    
    private fun addLineToCanvas(line: LineSegment) {
        // Здесь должна быть логика добавления линии на холст
        // Пока просто логируем
        println("Добавлена линия: ${line.determineAX()}, ${line.determineAY()} -> ${line.determineBX()}, ${line.determineBY()}")
    }
    
    fun setLineColor(color: LineColor) {
        lineColor = color
    }
    
    fun getCurrentLine(): LineSegment? = currentLine
    
    override fun getName(): String = "Рисование линий"
    
    override fun getDescription(): String = "Нарисуйте линии сгиба"
} 