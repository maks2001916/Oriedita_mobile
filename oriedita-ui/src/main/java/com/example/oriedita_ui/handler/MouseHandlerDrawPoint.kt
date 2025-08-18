package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point

/**
 * Обработчик рисования точек
 * Адаптированная версия MouseHandlerDrawPoint для Android
 */
class MouseHandlerDrawPoint : BaseMouseHandler() {
    
    private var drawnPoints = mutableListOf<Point>()
    private var pointSize: Int = 3
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        addPointToCanvas(point)
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // При перетаскивании также добавляем точки
        val point = offsetToPoint(offset)
        addPointToCanvas(point)
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        // При отпускании добавляем финальную точку
        val point = offsetToPoint(offset)
        addPointToCanvas(point)
        return true
    }
    
    private fun addPointToCanvas(point: Point) {
        // Здесь должна быть логика добавления точки на холст
        drawnPoints.add(point)
        println("Добавлена точка: (${point.x}, ${point.y})")
    }
    
    fun setPointSize(size: Int) {
        pointSize = size
    }
    
    override fun getPointSize(): Int = pointSize
    
    fun getDrawnPoints(): List<Point> = drawnPoints.toList()
    
    fun clearPoints() {
        drawnPoints.clear()
    }
    
    fun getPointCount(): Int = drawnPoints.size
    
    override fun getName(): String = "Рисование точек"
    
    override fun getDescription(): String = "Нарисуйте точки на холсте"
} 