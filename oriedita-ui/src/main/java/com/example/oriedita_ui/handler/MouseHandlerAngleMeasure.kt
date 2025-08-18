package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc
import kotlin.math.atan2

/**
 * Обработчик измерения углов
 * Адаптированная версия MouseHandlerAngleMeasure для Android
 */
class MouseHandlerAngleMeasure : BaseMouseHandler() {
    
    private var points = mutableListOf<Point>()
    private var currentAngle: Double? = null
    private var isMeasuring = false
    private var angleMeasurements = mutableListOf<AngleMeasurement>()
    private var angleUnit: AngleUnit = AngleUnit.DEGREES
    
    data class AngleMeasurement(
        val vertex: Point,
        val point1: Point,
        val point2: Point,
        val angle: Double,
        val unit: AngleUnit
    )
    
    enum class AngleUnit(val displayName: String, val toDegrees: Double) {
        DEGREES("градусы", 1.0),
        RADIANS("радианы", 180.0 / Math.PI),
        GRADIANS("грады", 0.9)
    }
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        if (points.size < 3) {
            points.add(point)
            isMeasuring = true
            
            if (points.size == 3) {
                // Вычислить угол
                calculateAngle()
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isMeasuring || points.size >= 3) return false
        
        val currentPoint = offsetToPoint(offset)
        
        // Показать предварительный просмотр угла
        if (points.size == 2) {
            val tempPoints = points.toMutableList()
            tempPoints.add(currentPoint)
            showPreviewAngle(tempPoints)
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (points.size >= 3) {
            // Сбросить состояние после измерения угла
            points.clear()
            currentAngle = null
            isMeasuring = false
        }
        
        return true
    }
    
    private fun calculateAngle() {
        if (points.size != 3) return
        
        val vertex = points[1] // Вершина угла
        val p1 = points[0]     // Первая точка
        val p2 = points[2]     // Вторая точка
        
        val angle = calculateAngleBetweenPoints(vertex, p1, p2)
        currentAngle = angle
        
        val measurement = AngleMeasurement(vertex, p1, p2, angle, angleUnit)
        angleMeasurements.add(measurement)
        
        println("Измерен угол: ${formatAngle(angle)}")
    }
    
    private fun calculateAngleBetweenPoints(vertex: Point, p1: Point, p2: Point): Double {
        // Вычислить векторы от вершины к точкам
        val v1x = p1.x - vertex.x
        val v1y = p1.y - vertex.y
        val v2x = p2.x - vertex.x
        val v2y = p2.y - vertex.y
        
        // Вычислить углы векторов
        val angle1 = atan2(v1y, v1x)
        val angle2 = atan2(v2y, v2x)
        
        // Вычислить разность углов
        var angleDiff = angle2 - angle1
        
        // Нормализовать угол в диапазон [0, 2π)
        while (angleDiff < 0) angleDiff += 2 * Math.PI
        while (angleDiff >= 2 * Math.PI) angleDiff -= 2 * Math.PI
        
        // Преобразовать в градусы
        val angleDegrees = angleDiff * 180.0 / Math.PI
        
        return angleDegrees
    }
    
    private fun showPreviewAngle(tempPoints: List<Point>) {
        if (tempPoints.size == 3) {
            val vertex = tempPoints[1]
            val p1 = tempPoints[0]
            val p2 = tempPoints[2]
            currentAngle = calculateAngleBetweenPoints(vertex, p1, p2)
        }
    }
    
    private fun formatAngle(angle: Double): String {
        val convertedAngle = angle * angleUnit.toDegrees
        return String.format("%.2f %s", convertedAngle, angleUnit.displayName)
    }
    
    fun setAngleUnit(newUnit: AngleUnit) {
        angleUnit = newUnit
        println("Единица измерения угла изменена на: ${newUnit.displayName}")
    }
    
    fun getAngleUnit(): AngleUnit = angleUnit
    
    fun getCurrentAngle(): Double? = currentAngle
    
    fun getPoints(): List<Point> = points.toList()
    
    fun getPointCount(): Int = points.size
    
    fun isMeasuring(): Boolean = isMeasuring
    
    fun getAngleMeasurements(): List<AngleMeasurement> = angleMeasurements.toList()
    
    fun clearAngleMeasurements() {
        angleMeasurements.clear()
        println("Все измерения углов очищены")
    }
    
    fun getAngleMeasurementCount(): Int = angleMeasurements.size
    
    fun getLastAngleMeasurement(): AngleMeasurement? = angleMeasurements.lastOrNull()
    
    fun getAverageAngle(): Double {
        if (angleMeasurements.isEmpty()) return 0.0
        return angleMeasurements.map { it.angle }.average()
    }
    
    fun exportAngleMeasurements(): String {
        val sb = StringBuilder()
        sb.appendLine("Измерения углов:")
        for ((index, measurement) in angleMeasurements.withIndex()) {
            sb.appendLine("${index + 1}. ${formatAngle(measurement.angle)}")
        }
        sb.appendLine("Средний угол: ${formatAngle(getAverageAngle())}")
        return sb.toString()
    }
    
    override fun getName(): String = "Измерение углов"
    
    override fun getDescription(): String = "Измерьте угол между тремя точками"
} 