package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик масштабирования
 * Адаптированная версия MouseHandlerZoom для Android
 */
class MouseHandlerZoom : BaseMouseHandler() {
    
    private var currentZoom: Float = 1.0f
    private var minZoom: Float = 0.1F
    private var maxZoom: Float = 10.0f
    private var zoomStep: Float = 0.1f
    private var zoomCenter: Point? = null
    private var isZooming = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        zoomCenter = point
        isZooming = true
        
        println("Начало масштабирования в точке: (${point.x}, ${point.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isZooming) return false
        
        val currentPoint = offsetToPoint(offset)
        val center = zoomCenter ?: return false
        
        // Вычислить изменение масштаба на основе движения мыши
        val deltaY = currentPoint.y - center.y
        val zoomFactor = 1.0f + (deltaY / 100.0f) * zoomStep
        
        val newZoom = (currentZoom * zoomFactor).coerceIn(minZoom.toDouble(), maxZoom.toDouble())
        
        if (newZoom.toFloat() != currentZoom) {
            currentZoom = newZoom.toFloat()
            println("Масштаб изменен на: ${String.format("%.2f", currentZoom)}x")
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (isZooming) {
            val endPoint = offsetToPoint(offset)
            println("Завершение масштабирования в точке: (${endPoint.x}, ${endPoint.y})")
            
            // Сбросить состояние
            zoomCenter = null
            isZooming = false
        }
        
        return true
    }
    
    fun zoomIn() {
        val newZoom = (currentZoom * (1.0f + zoomStep)).coerceAtMost(maxZoom)
        if (newZoom != currentZoom) {
            currentZoom = newZoom
            println("Увеличение масштаба: ${String.format("%.2f", currentZoom)}x")
        }
    }
    
    fun zoomOut() {
        val newZoom = (currentZoom * (1.0f - zoomStep)).coerceAtLeast(minZoom)
        if (newZoom != currentZoom) {
            currentZoom = newZoom
            println("Уменьшение масштаба: ${String.format("%.2f", currentZoom)}x")
        }
    }
    
    fun setZoom(zoom: Float) {
        val clampedZoom = zoom.coerceIn(minZoom, maxZoom)
        if (clampedZoom != currentZoom) {
            currentZoom = clampedZoom
            println("Масштаб установлен: ${String.format("%.2f", currentZoom)}x")
        }
    }
    
    fun getZoom(): Float = currentZoom
    
    fun setMinZoom(min: Float) {
        minZoom = min
        if (currentZoom < minZoom) {
            currentZoom = minZoom
        }
        println("Минимальный масштаб установлен: ${minZoom}x")
    }
    
    fun getMinZoom(): Float = minZoom
    
    fun setMaxZoom(max: Float) {
        maxZoom = max
        if (currentZoom > maxZoom) {
            currentZoom = maxZoom
        }
        println("Максимальный масштаб установлен: ${maxZoom}x")
    }
    
    fun getMaxZoom(): Float = maxZoom
    
    fun setZoomStep(step: Float) {
        zoomStep = step
        println("Шаг масштабирования установлен: ${zoomStep}")
    }
    
    fun getZoomStep(): Float = zoomStep
    
    fun resetZoom() {
        currentZoom = 1.0f
        println("Масштаб сброшен: 1.00x")
    }
    
    fun fitToScreen() {
        // Здесь должна быть логика подгонки под экран
        currentZoom = 1.0f
        println("Подгонка под экран: 1.00x")
    }
    
    fun getZoomCenter(): Point? = zoomCenter
    
    fun isZooming(): Boolean = isZooming
    
    fun getZoomPercentage(): Int {
        return ((currentZoom / maxZoom) * 100).toInt()
    }
    
    override fun getName(): String = "Масштабирование"
    
    override fun getDescription(): String = "Измените масштаб изображения"
} 