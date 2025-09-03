package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import kotlin.math.roundToInt

/**
 * Обработчик привязки к углам
 * Адаптированная версия MouseHandlerAngleSnap для Android
 */
class MouseHandlerAngleSnap : BaseMouseHandler() {
    
    private var angleStep: Double = 15.0 // Шаг угла в градусах
    private var isSnappingEnabled: Boolean = true
    private var snapTolerance: Double = 5.0
    private var startPoint: Point? = null
    private var lastSnappedAngle: Double? = null
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        lastSnappedAngle = null
        
        println("Начало привязки к углам в точке: (${point.x}, ${point.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isSnappingEnabled || startPoint == null) return false
        
        val currentPoint = offsetToPoint(offset)
        val snappedPoint = snapToAngle(startPoint!!, currentPoint)
        
        val angle = calculateAngle(startPoint!!, currentPoint)
        val snappedAngle = calculateAngle(startPoint!!, snappedPoint)
        
        if (snappedAngle != lastSnappedAngle) {
            lastSnappedAngle = snappedAngle
            println("Привязка к углу: ${angle}° -> ${snappedAngle}°")
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (startPoint == null) return false
        
        val endPoint = offsetToPoint(offset)
        val snappedPoint = snapToAngle(startPoint!!, endPoint)
        
        val angle = calculateAngle(startPoint!!, endPoint)
        val snappedAngle = calculateAngle(startPoint!!, snappedPoint)
        
        println("Финальная привязка к углу: ${angle}° -> ${snappedAngle}°")
        
        // Сбросить состояние
        startPoint = null
        lastSnappedAngle = null
        
        return true
    }
    
    private fun snapToAngle(start: Point, end: Point): Point {
        if (!isSnappingEnabled) return end
        
        val angle = calculateAngle(start, end)
        val snappedAngle = snapAngleToStep(angle)
        
        val distance = start.distance(end)
        val angleRad = snappedAngle * PI / 180.0
        
        val snappedX = start.x + distance * cos(angleRad)
        val snappedY = start.y + distance * sin(angleRad)
        
        return Point(snappedX, snappedY)
    }
    
    private fun calculateAngle(start: Point, end: Point): Double {
        val dx = end.x - start.x
        val dy = end.y - start.y
        var angle = atan2(dy, dx) * 180.0 / PI
        
        // Нормализовать угол в диапазон [0, 360)
        if (angle < 0) angle += 360.0
        
        return angle
    }
    
    private fun snapAngleToStep(angle: Double): Double {
        val stepRad = angleStep * PI / 180.0
        val snappedRad = (angle * PI / 180.0 / stepRad).roundToInt() * stepRad
        return snappedRad * 180.0 / PI
    }
    
    fun setAngleStep(step: Double) {
        angleStep = step
        println("Шаг угла изменен на: ${step}°")
    }
    
    fun getAngleStep(): Double = angleStep
    
    fun setSnappingEnabled(enabled: Boolean) {
        isSnappingEnabled = enabled
        println("Привязка к углам ${if (enabled) "включена" else "отключена"}")
    }
    
    fun isSnappingEnabled(): Boolean = isSnappingEnabled
    
    fun setSnapTolerance(tolerance: Double) {
        snapTolerance = tolerance
        println("Допуск привязки к углам изменен на: $tolerance")
    }
    
    fun getSnapTolerance(): Double = snapTolerance
    
    fun getLastSnappedAngle(): Double? = lastSnappedAngle
    
    fun toggleSnapping() {
        isSnappingEnabled = !isSnappingEnabled
        println("Привязка к углам ${if (isSnappingEnabled) "включена" else "отключена"}")
    }
    
    fun setCommonAngles() {
        angleStep = 15.0
        println("Установлены стандартные углы: 15°")
    }
    
    fun setPreciseAngles() {
        angleStep = 5.0
        println("Установлены точные углы: 5°")
    }
    
    fun setFineAngles() {
        angleStep = 1.0
        println("Установлены мелкие углы: 1°")
    }
    
    override fun getName(): String = "Привязка к углам"
    
    override fun getDescription(): String = "Привязка к углам"
} 