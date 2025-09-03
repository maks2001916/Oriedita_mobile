package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point

/**
 * Обработчик удаления точек
 * Адаптированная версия MouseHandlerDeletePoint для Android
 */
class MouseHandlerDeletePoint : BaseMouseHandler() {
    
    private var deletedPoints = mutableListOf<Point>()
    private var selectionDistance: Double = 10.0
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        deleteClosestPoint(point)
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val currentPoint = offsetToPoint(offset)
        deleteClosestPoint(currentPoint)
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        // Подтвердить удаление
        confirmDeletion()
        return true
    }
    
    private fun deleteClosestPoint(point: Point) {
        // Здесь должна быть логика поиска и удаления ближайшей точки
        // Пока просто логируем
        println("Попытка удаления точки около (${point.x}, ${point.y})")
    }
    
    private fun confirmDeletion() {
        // Здесь должна быть логика подтверждения удаления
        println("Подтверждено удаление ${deletedPoints.size} точек")
    }
    
    fun setSelectionDistance(distance: Double) {
        selectionDistance = distance
    }
    
    override fun getSelectionDistance(): Double = selectionDistance
    
    fun getDeletedPoints(): List<Point> = deletedPoints.toList()
    
    fun clearDeletedPoints() {
        deletedPoints.clear()
    }
    
    fun getDeletedCount(): Int = deletedPoints.size
    
    override fun getName(): String = "Удаление точек"
    
    override fun getDescription(): String = "Удалите точки с холста"
} 