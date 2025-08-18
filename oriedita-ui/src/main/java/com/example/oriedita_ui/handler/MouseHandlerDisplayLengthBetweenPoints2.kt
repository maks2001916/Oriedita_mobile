package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик измерения расстояния между точками (тип 2)
 * Адаптированная версия для Android
 */
class MouseHandlerDisplayLengthBetweenPoints2 : BaseMouseHandler() {
    
    private var isProcessing = false
    private var measuredLength = 0.0
    private var selectedPoints = mutableListOf<Point>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        val closestPoint = getClosestPoint(point)
        
        if (point.distance(closestPoint) < getSelectionDistance()) {
            val lineSegment = LineSegment(closestPoint, closestPoint, getLineColor())
            addLineStep(lineSegment)
            selectedPoints.add(closestPoint)
            
            println("Выбрана точка ${selectedPoints.size}: (${closestPoint.x}, ${closestPoint.y})")
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // Этот обработчик не требует перетаскивания
        return false
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (getLineStepSize() == 2) {
            calculateLength()
            clearLineStep()
            selectedPoints.clear()
        }
        
        return true
    }
    
    private fun calculateLength() {
        if (getLineStepSize() >= 2) {
            val point1 = (getLineStep(0) as LineSegment).getA() // Первая точка
            val point2 = (getLineStep(1) as LineSegment).getA() // Вторая точка
            
            val rawDistance = OritaCalc.distance(point1, point2)
            val gridSize = (getGrid()?.getGridSize() ?: 1.0).toDouble()
            measuredLength = rawDistance * gridSize / 400.0

            println("Измерено расстояние (тип 2): ${String.format("%.4f", measuredLength)}")
            println("Между точками:")
            println("  Точка 1: (${point1.x}, ${point1.y})")
            println("  Точка 2: (${point2.x}, ${point2.y})")
            println("  Сырое расстояние: ${String.format("%.4f", rawDistance)}")
            println("  Размер сетки: ${gridSize}")
        }
    }
    
    fun getMeasuredLength(): Double = measuredLength
    
    fun getSelectedPoints(): List<Point> = selectedPoints.toList()
    
    fun getSelectedPointsCount(): Int = selectedPoints.size
    
    fun isProcessing(): Boolean = isProcessing
    
    fun clearSelectedPoints() {
        selectedPoints.clear()
    }
    
    fun getLengthDescription(): String {
        return when (getLineStepSize()) {
            0 -> "Выберите первую точку"
            1 -> "Выберите вторую точку"
            2 -> "Расстояние измерено: ${String.format("%.4f", measuredLength)}"
            else -> "Неизвестное состояние"
        }
    }
    
    fun getLengthInfo(): String {
        return buildString {
            val gridSize = (getGrid()?.getGridSize() ?: 1.0).toDouble()
            append("Выбранные точки: ${selectedPoints.size}\n")
            if (selectedPoints.isNotEmpty()) {
                selectedPoints.forEachIndexed { index, point ->
                    append("  Точка ${index + 1}: (${String.format("%.2f", point.x)}, ${String.format("%.2f", point.y)})\n")
                }
            }
            append("Измеренное расстояние (тип 2): ${String.format("%.4f", measuredLength)}\n")
            append("Размер сетки: $gridSize")
        }
    }
    
    override fun getName(): String = "Измерение расстояния между точками (тип 2)"
    
    override fun getDescription(): String = "Измеряйте расстояния между двумя выбранными точками (второй тип)"
} 