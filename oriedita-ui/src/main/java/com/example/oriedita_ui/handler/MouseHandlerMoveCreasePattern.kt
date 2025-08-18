package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point

/**
 * Обработчик перемещения паттерна сгиба
 * Адаптированная версия MouseHandlerMoveCreasePattern для Android
 */
class MouseHandlerMoveCreasePattern : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var lastPoint: Point? = null
    private var isMoving = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        lastPoint = point
        isMoving = true
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isMoving) return false
        
        val currentPoint = offsetToPoint(offset)
        val last = lastPoint ?: return false
        
        // Вычислить смещение
        val deltaX = currentPoint.x - last.x
        val deltaY = currentPoint.y - last.y
        
        // Переместить весь паттерн
        movePattern(deltaX, deltaY)
        
        lastPoint = currentPoint
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        isMoving = false
        startPoint = null
        lastPoint = null
        return true
    }
    
    private fun movePattern(deltaX: Double, deltaY: Double) {
        // Здесь должна быть логика перемещения всего паттерна
        // Пока просто логируем
        println("Перемещение паттерна: dx=$deltaX, dy=$deltaY")
    }
    
    override fun getName(): String = "Перемещение"
    
    override fun getDescription(): String = "Переместите весь паттерн сгиба"
} 