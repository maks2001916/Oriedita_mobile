package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc
import kotlin.math.abs

/**
 * Обработчик рисования линий сгиба с ограничением угла
 * Адаптированная версия MouseHandlerDrawCreaseAngleRestricted для Android
 */
class MouseHandlerDrawCreaseAngleRestricted : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var currentLine: LineSegment? = null
    private var lineColor: LineColor = LineColor.BLACK_0
    private var referenceLine: LineSegment? = null
    private var allowedAngles: List<Double> = listOf(0.0, 45.0, 90.0, 135.0, 180.0, 225.0, 270.0, 315.0)
    private var angleTolerance: Double = 5.0 // градусы
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val currentPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать линию с ограничением угла
        val restrictedPoint = snapToAllowedAngle(start, currentPoint)
        currentLine = LineSegment(start, restrictedPoint)
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val endPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать финальную линию с ограничением угла
        val restrictedPoint = snapToAllowedAngle(start, endPoint)
        val finalLine = LineSegment(start, restrictedPoint)
        addLineToCanvas(finalLine)
        
        // Сбросить состояние
        startPoint = null
        currentLine = null
        
        return true
    }
    
    private fun snapToAllowedAngle(start: Point, end: Point): Point {
        val angle = OritaCalc.angle(LineSegment(start, end))
        val snappedAngle = findClosestAllowedAngle(angle)
        
        // Вычислить новую конечную точку с исправленным углом
        val distance = start.distance(end)
        val radians = Math.toRadians(snappedAngle)
        val newX = start.x + distance * Math.cos(radians)
        val newY = start.y + distance * Math.sin(radians)
        
        return Point(newX, newY)
    }
    
    private fun findClosestAllowedAngle(angle: Double): Double {
        var closestAngle = allowedAngles[0]
        var minDifference = abs(angle - closestAngle)
        
        for (allowedAngle in allowedAngles) {
            val difference = abs(angle - allowedAngle)
            if (difference < minDifference) {
                minDifference = difference
                closestAngle = allowedAngle
            }
        }
        
        return closestAngle
    }
    
    private fun addLineToCanvas(line: LineSegment) {
        // Здесь должна быть логика добавления линии на холст
        println("Добавлена линия с ограничением угла: ${line.determineAX()}, ${line.determineAY()} -> ${line.determineBX()}, ${line.determineBY()}")
    }
    
    fun setLineColor(color: LineColor) {
        lineColor = color
    }
    
    fun setReferenceLine(line: LineSegment) {
        referenceLine = line
    }
    
    fun setAllowedAngles(angles: List<Double>) {
        allowedAngles = angles
    }
    
    fun setAngleTolerance(tolerance: Double) {
        angleTolerance = tolerance
    }
    
    fun getCurrentLine(): LineSegment? = currentLine
    
    override fun getName(): String = "Рисование линий с ограничением угла"
    
    override fun getDescription(): String = "Нарисуйте линии сгиба с ограничением угла"
} 