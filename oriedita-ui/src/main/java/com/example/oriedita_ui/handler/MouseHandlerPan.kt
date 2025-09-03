package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик панорамирования
 * Адаптированная версия MouseHandlerPan для Android
 */
class MouseHandlerPan : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var currentOffset: Point = Point(0.0, 0.0)
    private var totalOffset: Point = Point(0.0, 0.0)
    private var isPanning = false
    private var panSpeed: Double = 1.0
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        isPanning = true
        
        println("Начало панорамирования в точке: (${point.x}, ${point.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isPanning) return false
        
        val currentPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Вычислить смещение
        val deltaX = (currentPoint.x - start.x) * panSpeed
        val deltaY = (currentPoint.y - start.y) * panSpeed
        
        currentOffset = Point(deltaX, deltaY)
        totalOffset = Point(totalOffset.x + deltaX, totalOffset.y + deltaY)
        
        // Обновить начальную точку для следующего кадра
        startPoint = currentPoint
        
        println("Панорамирование: смещение (${String.format("%.2f", deltaX)}, ${String.format("%.2f", deltaY)})")
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (isPanning) {
            val endPoint = offsetToPoint(offset)
            println("Завершение панорамирования в точке: (${endPoint.x}, ${endPoint.y})")
            
            // Сбросить состояние
            startPoint = null
            currentOffset = Point(0.0, 0.0)
            isPanning = false
        }
        
        return true
    }
    
    fun setPanSpeed(speed: Double) {
        panSpeed = speed
        println("Скорость панорамирования установлена: $speed")
    }
    
    fun getPanSpeed(): Double = panSpeed
    
    fun getCurrentOffset(): Point = currentOffset
    
    fun getTotalOffset(): Point = totalOffset
    
    fun resetOffset() {
        totalOffset = Point(0.0, 0.0)
        currentOffset = Point(0.0, 0.0)
        println("Смещение сброшено")
    }
    
    fun setOffset(offset: Point) {
        totalOffset = offset
        println("Смещение установлено: (${offset.x}, ${offset.y})")
    }
    
    fun panBy(deltaX: Double, deltaY: Double) {
        totalOffset = Point(totalOffset.x + deltaX, totalOffset.y + deltaY)
        println("Панорамирование на: (${deltaX}, ${deltaY})")
    }
    
    fun panTo(x: Double, y: Double) {
        totalOffset = Point(x, y)
        println("Панорамирование в точку: (${x}, ${y})")
    }
    
    fun getStartPoint(): Point? = startPoint
    
    fun isPanning(): Boolean = isPanning
    
    fun getPanDistance(): Double {
        return kotlin.math.sqrt(totalOffset.x * totalOffset.x + totalOffset.y * totalOffset.y)
    }
    
    fun getPanDirection(): String {
        if (totalOffset.x == 0.0 && totalOffset.y == 0.0) return "Нет движения"
        
        val angle = kotlin.math.atan2(totalOffset.y, totalOffset.x) * 180.0 / kotlin.math.PI
        return when {
            angle >= -22.5 && angle < 22.5 -> "Вправо"
            angle >= 22.5 && angle < 67.5 -> "Вправо-вниз"
            angle >= 67.5 && angle < 112.5 -> "Вниз"
            angle >= 112.5 && angle < 157.5 -> "Влево-вниз"
            angle >= 157.5 || angle < -157.5 -> "Влево"
            angle >= -157.5 && angle < -112.5 -> "Влево-вверх"
            angle >= -112.5 && angle < -67.5 -> "Вверх"
            else -> "Вправо-вверх"
        }
    }
    
    override fun getName(): String = "Панорамирование"
    
    override fun getDescription(): String = "Переместите вид по холсту"
} 