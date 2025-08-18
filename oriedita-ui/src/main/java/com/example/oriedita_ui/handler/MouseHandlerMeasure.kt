package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc
import kotlin.math.sqrt

/**
 * Обработчик измерения
 * Адаптированная версия MouseHandlerMeasure для Android
 */
class MouseHandlerMeasure : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var endPoint: Point? = null
    private var currentMeasurement: LineSegment? = null
    private var isMeasuring = false
    private var measurements = mutableListOf<Measurement>()
    private var unit: MeasurementUnit = MeasurementUnit.PIXELS
    
    data class Measurement(
        val start: Point,
        val end: Point,
        val distance: Double,
        val unit: MeasurementUnit
    )
    
    enum class MeasurementUnit(val displayName: String, val scale: Double) {
        PIXELS("пиксели", 1.0),
        MILLIMETERS("мм", 0.264583), // 1 пиксель = 0.264583 мм при 96 DPI
        CENTIMETERS("см", 0.0264583),
        INCHES("дюймы", 0.0104167)
    }
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        isMeasuring = true
        
        println("Начало измерения в точке: (${point.x}, ${point.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isMeasuring) return false
        
        val currentPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать временное измерение
        currentMeasurement = LineSegment(start, currentPoint)
        val distance = calculateDistance(start, currentPoint)
        
        println("Предварительное измерение: ${formatDistance(distance)}")
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isMeasuring) return false
        
        val end = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        endPoint = end
        
        // Создать финальное измерение
        val finalMeasurement = LineSegment(start, end)
        currentMeasurement = finalMeasurement
        
        val distance = calculateDistance(start, end)
        val measurement = Measurement(start, end, distance, unit)
        measurements.add(measurement)
        
        println("Финальное измерение: ${formatDistance(distance)}")
        
        // Сбросить состояние
        startPoint = null
        endPoint = null
        currentMeasurement = null
        isMeasuring = false
        
        return true
    }
    
    private fun calculateDistance(p1: Point, p2: Point): Double {
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        return sqrt(dx * dx + dy * dy)
    }
    
    private fun formatDistance(distance: Double): String {
        val convertedDistance = distance * unit.scale
        return String.format("%.2f %s", convertedDistance, unit.displayName)
    }
    
    fun setMeasurementUnit(newUnit: MeasurementUnit) {
        unit = newUnit
        println("Единица измерения изменена на: ${newUnit.displayName}")
    }
    
    fun getMeasurementUnit(): MeasurementUnit = unit
    
    fun getCurrentMeasurement(): LineSegment? = currentMeasurement
    
    fun getStartPoint(): Point? = startPoint
    
    fun getEndPoint(): Point? = endPoint
    
    fun isMeasuring(): Boolean = isMeasuring
    
    fun getMeasurements(): List<Measurement> = measurements.toList()
    
    fun clearMeasurements() {
        measurements.clear()
        println("Все измерения очищены")
    }
    
    fun getMeasurementCount(): Int = measurements.size
    
    fun getLastMeasurement(): Measurement? = measurements.lastOrNull()
    
    fun getTotalDistance(): Double {
        return measurements.sumOf { it.distance * it.unit.scale }
    }
    
    fun getAverageDistance(): Double {
        if (measurements.isEmpty()) return 0.0
        return measurements.map { it.distance * it.unit.scale }.average()
    }
    
    fun exportMeasurements(): String {
        val sb = StringBuilder()
        sb.appendLine("Измерения:")
        for ((index, measurement) in measurements.withIndex()) {
            sb.appendLine("${index + 1}. ${formatDistance(measurement.distance)}")
        }
        sb.appendLine("Общее расстояние: ${formatDistance(getTotalDistance())}")
        sb.appendLine("Среднее расстояние: ${formatDistance(getAverageDistance())}")
        return sb.toString()
    }
    
    override fun getName(): String = "Измерение"
    
    override fun getDescription(): String = "Измерьте расстояние между точками"
} 