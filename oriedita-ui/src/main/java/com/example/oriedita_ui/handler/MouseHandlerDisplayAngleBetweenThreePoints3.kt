package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик отображения угла между тремя точками (тип 3)
 * Адаптированная версия для Android
 */
class MouseHandlerDisplayAngleBetweenThreePoints3 : BaseMouseHandler() {
    
    private var isProcessing = false
    private var measuredAngle = 0.0
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
        if (getLineStepSize() == 3) {
            calculateAngle()
            clearLineStep()
            selectedPoints.clear()
        }
        
        return true
    }
    
    private fun calculateAngle() {
        if (getLineStepSize() >= 3) {
            val point1 = (getLineStep(1) as LineSegment).getA() // Вторая точка
            val point2 = (getLineStep(2) as LineSegment).getA() // Третья точка
            val point0 = (getLineStep(0) as LineSegment).getA() // Первая точка
            
            measuredAngle = OritaCalc.angle(point1, point2, point1, point0)
            
            println("Измерен угол (тип 3): ${Math.toDegrees(measuredAngle)}° (${measuredAngle} радиан)")
            println("Между точками:")
            println("  Точка 1: (${point0.x}, ${point0.y})")
            println("  Точка 2: (${point1.x}, ${point1.y}) - вершина угла")
            println("  Точка 3: (${point2.x}, ${point2.y})")
        }
    }
    
    fun getMeasuredAngle(): Double = measuredAngle
    
    fun getMeasuredAngleDegrees(): Double = Math.toDegrees(measuredAngle)
    
    fun getSelectedPoints(): List<Point> = selectedPoints.toList()
    
    fun getSelectedPointsCount(): Int = selectedPoints.size
    
    fun isProcessing(): Boolean = isProcessing
    
    fun clearSelectedPoints() {
        selectedPoints.clear()
    }
    
    fun getAngleDescription(): String {
        return when (getLineStepSize()) {
            0 -> "Выберите первую точку"
            1 -> "Выберите вторую точку (вершину угла)"
            2 -> "Выберите третью точку"
            3 -> "Угол измерен: ${String.format("%.2f", Math.toDegrees(measuredAngle))}°"
            else -> "Неизвестное состояние"
        }
    }
    
    fun getAngleInfo(): String {
        return buildString {
            append("Выбранные точки: ${selectedPoints.size}\n")
            if (selectedPoints.isNotEmpty()) {
                selectedPoints.forEachIndexed { index, point ->
                    append("  Точка ${index + 1}: (${String.format("%.2f", point.x)}, ${String.format("%.2f", point.y)})\n")
                }
            }
            append("Измеренный угол (тип 3): ${String.format("%.2f", Math.toDegrees(measuredAngle))}°\n")
            append("В радианах: ${String.format("%.4f", measuredAngle)}")
        }
    }
    
    override fun getName(): String = "Измерение угла между тремя точками (тип 3)"
    
    override fun getDescription(): String = "Измеряйте углы между тремя выбранными точками (третий тип)"
} 