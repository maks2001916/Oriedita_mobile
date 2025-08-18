package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик привязки к сетке
 * Адаптированная версия MouseHandlerGridSnap для Android
 */
class MouseHandlerGridSnap : BaseMouseHandler() {
    
    private var gridSize: Double = 10.0
    private var isSnappingEnabled: Boolean = true
    private var snapTolerance: Double = 5.0
    private var lastSnappedPoint: Point? = null
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        val snappedPoint = snapToGrid(point)
        lastSnappedPoint = snappedPoint
        
        println("Привязка к сетке: (${point.x}, ${point.y}) -> (${snappedPoint.x}, ${snappedPoint.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val currentPoint = offsetToPoint(offset)
        val snappedPoint = snapToGrid(currentPoint)
        
        if (snappedPoint != lastSnappedPoint) {
            lastSnappedPoint = snappedPoint
            println("Привязка к сетке при перетаскивании: (${currentPoint.x}, ${currentPoint.y}) -> (${snappedPoint.x}, ${snappedPoint.y})")
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val endPoint = offsetToPoint(offset)
        val snappedPoint = snapToGrid(endPoint)
        lastSnappedPoint = snappedPoint
        
        println("Финальная привязка к сетке: (${endPoint.x}, ${endPoint.y}) -> (${snappedPoint.x}, ${snappedPoint.y})")
        
        return true
    }
    
    private fun snapToGrid(point: Point): Point {
        if (!isSnappingEnabled) return point
        
        // Привязка к сетке
        val snappedX = (point.x / gridSize) * gridSize
        val snappedY = (point.y / gridSize) * gridSize
        
        return Point(snappedX, snappedY)
    }
    
    fun setGridSize(size: Double) {
        gridSize = size
        println("Размер сетки изменен на: $size")
    }
    
    fun getGridSize(): Double = gridSize
    
    fun setSnappingEnabled(enabled: Boolean) {
        isSnappingEnabled = enabled
        println("Привязка к сетке ${if (enabled) "включена" else "отключена"}")
    }
    
    fun isSnappingEnabled(): Boolean = isSnappingEnabled
    
    fun setSnapTolerance(tolerance: Double) {
        snapTolerance = tolerance
        println("Допуск привязки изменен на: $tolerance")
    }
    
    fun getSnapTolerance(): Double = snapTolerance
    
    fun getLastSnappedPoint(): Point? = lastSnappedPoint
    
    fun toggleSnapping() {
        isSnappingEnabled = !isSnappingEnabled
        println("Привязка к сетке ${if (isSnappingEnabled) "включена" else "отключена"}")
    }
    
    fun increaseGridSize() {
        gridSize *= 2
        println("Размер сетки увеличен до: $gridSize")
    }
    
    fun decreaseGridSize() {
        gridSize /= 2
        if (gridSize < 1.0) gridSize = 1.0
        println("Размер сетки уменьшен до: $gridSize")
    }
    
    override fun getName(): String = "Привязка к сетке"
    
    override fun getDescription(): String = "Привязка к сетке"
} 