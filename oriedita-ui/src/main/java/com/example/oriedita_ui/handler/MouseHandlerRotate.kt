package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc
import kotlin.math.atan2
import kotlin.math.PI
import kotlin.math.roundToInt

/**
 * Обработчик вращения
 * Адаптированная версия MouseHandlerRotate для Android
 */
class MouseHandlerRotate : BaseMouseHandler() {
    
    private var rotationCenter: Point? = null
    private var startAngle: Double = 0.0
    private var currentAngle: Double = 0.0
    private var totalRotation: Double = 0.0
    private var isRotating = false
    private var rotationSpeed: Double = 1.0
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        rotationCenter = point
        isRotating = true
        
        println("Начало вращения вокруг точки: (${point.x}, ${point.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isRotating) return false
        
        val currentPoint = offsetToPoint(offset)
        val center = rotationCenter ?: return false
        
        // Вычислить угол от центра до текущей точки
        val angle = calculateAngle(center, currentPoint)
        
        if (startAngle == 0.0) {
            startAngle = angle
        }
        
        // Вычислить изменение угла
        var deltaAngle = (angle - startAngle) * rotationSpeed
        
        // Нормализовать угол в диапазон [-180, 180]
        while (deltaAngle > 180) deltaAngle -= 360
        while (deltaAngle < -180) deltaAngle += 360
        
        currentAngle = deltaAngle
        totalRotation += deltaAngle
        
        // Обновить начальный угол для следующего кадра
        startAngle = angle
        
        println("Вращение: ${String.format("%.2f", deltaAngle)}° (общее: ${String.format("%.2f", totalRotation)}°)")
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (isRotating) {
            val endPoint = offsetToPoint(offset)
            println("Завершение вращения в точке: (${endPoint.x}, ${endPoint.y})")
            
            // Сбросить состояние
            rotationCenter = null
            startAngle = 0.0
            currentAngle = 0.0
            isRotating = false
        }
        
        return true
    }
    
    private fun calculateAngle(center: Point, point: Point): Double {
        val dx = point.x - center.x
        val dy = point.y - center.y
        var angle = atan2(dy, dx) * 180.0 / PI
        
        // Нормализовать угол в диапазон [0, 360)
        if (angle < 0) angle += 360.0
        
        return angle
    }
    
    fun setRotationSpeed(speed: Double) {
        rotationSpeed = speed
        println("Скорость вращения установлена: $speed")
    }
    
    fun getRotationSpeed(): Double = rotationSpeed
    
    fun getCurrentAngle(): Double = currentAngle
    
    fun getTotalRotation(): Double = totalRotation
    
    fun resetRotation() {
        totalRotation = 0.0
        currentAngle = 0.0
        startAngle = 0.0
        println("Вращение сброшено")
    }
    
    fun setRotation(angle: Double) {
        totalRotation = angle
        println("Вращение установлено: ${String.format("%.2f", angle)}°")
    }
    
    fun rotateBy(angle: Double) {
        totalRotation += angle
        println("Вращение на: ${String.format("%.2f", angle)}°")
    }
    
    fun rotateTo(angle: Double) {
        totalRotation = angle
        println("Вращение в: ${String.format("%.2f", angle)}°")
    }
    
    fun getRotationCenter(): Point? = rotationCenter
    
    fun isRotating(): Boolean = isRotating
    
    fun getRotationInRadians(): Double {
        return totalRotation * PI / 180.0
    }
    
    fun getRotationInDegrees(): Double = totalRotation
    
    fun getRotationDirection(): String {
        return when {
            totalRotation > 0 -> "По часовой стрелке"
            totalRotation < 0 -> "Против часовой стрелки"
            else -> "Нет вращения"
        }
    }
    
    fun snapToAngle(step: Double) {
        val snappedAngle = (totalRotation / step).roundToInt() * step
        totalRotation = snappedAngle
        println("Вращение привязано к углу: ${String.format("%.2f", snappedAngle)}°")
    }
    
    override fun getName(): String = "Вращение"
    
    override fun getDescription(): String = "Вращайте вид вокруг центра"
} 