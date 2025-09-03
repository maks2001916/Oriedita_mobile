package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Circle
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик рисования окружностей
 * Адаптированная версия MouseHandlerCircleDraw для Android
 */
class MouseHandlerCircleDraw : BaseMouseHandler() {
    
    private var centerPoint: Point? = null
    private var currentCircle: Circle? = null
    private var radius: Double = 0.0
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        centerPoint = point
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val currentPoint = offsetToPoint(offset)
        val center = centerPoint ?: return false
        
        // Вычислить радиус
        radius = center.distance(currentPoint)
        
        // Создать временную окружность для предварительного просмотра
        currentCircle = Circle(center, radius, LineColor.BLUE_2)
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val endPoint = offsetToPoint(offset)
        val center = centerPoint ?: return false
        
        // Создать финальную окружность
        radius = center.distance(endPoint)
        val finalCircle = Circle(center, radius, getLineColor())
        addCircleToCanvas(finalCircle)
        
        // Сбросить состояние
        centerPoint = null
        currentCircle = null
        
        return true
    }
    
    private fun addCircleToCanvas(circle: Circle) {
        // Здесь должна быть логика добавления окружности на холст
        println("Добавлена окружность: центр(${circle.getX()}, ${circle.getY()}), радиус=${circle.getR()}")
    }
    
    fun getCurrentCircle(): Circle? = currentCircle
    
    fun getRadius(): Double = radius
    
    override fun getName(): String = "Рисование окружностей"
    
    override fun getDescription(): String = "Нарисуйте окружность"
} 