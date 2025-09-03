package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Circle
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor

/**
 * Обработчик свободного рисования окружностей
 * Адаптированная версия MouseHandlerCircleDrawFree для Android
 */
class MouseHandlerCircleDrawFree : BaseMouseHandler() {
    
    private var centerPoint: Point? = null
    private var currentCircle: Circle? = null
    private var radius: Double = 0.0
    private var isDrawing = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        centerPoint = point
        isDrawing = true
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing) return false
        
        val currentPoint = offsetToPoint(offset)
        val center = centerPoint ?: return false
        
        // Вычислить радиус
        radius = center.distance(currentPoint)
        
        // Создать временную окружность для предварительного просмотра
        currentCircle = Circle(center, 1.0, LineColor.BLACK_0)
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing) return false
        
        val endPoint = offsetToPoint(offset)
        val center = centerPoint ?: return false
        
        // Создать финальную окружность
        radius = center.distance(endPoint)
        val finalCircle = Circle(center, 1.0, LineColor.BLACK_0)
        addCircleToCanvas(finalCircle)
        
        // Сбросить состояние
        centerPoint = null
        currentCircle = null
        isDrawing = false
        
        return true
    }
    
    private fun addCircleToCanvas(circle: Circle) {
        // Здесь должна быть логика добавления окружности на холст
        println("Добавлена свободная окружность: центр(${circle.x}, ${circle.y}), радиус=${circle.r}")
    }
    
    fun getCurrentCircle(): Circle? = currentCircle
    
    fun getRadius(): Double = radius
    
    fun isDrawing(): Boolean = isDrawing
    
    override fun getName(): String = "Свободное рисование окружностей"
    
    override fun getDescription(): String = "Свободно нарисуйте окружность"
} 