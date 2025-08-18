package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик деления сегментов линий
 * Адаптированная версия MouseHandlerLineSegmentDivision для Android
 */
class MouseHandlerLineSegmentDivision : BaseMouseHandler() {
    
    private var anchorPoint: Point? = null
    private var releasePoint: Point? = null
    private var dragSegment: LineSegment? = null
    private var isDragging = false
    private var dividingNumber: Int = 2
    private var createdSegments = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        isDragging = true
        
        // Установить якорную точку
        val closestPoint = findClosestPoint(point)
        anchorPoint = if (OritaCalc.distance(point, closestPoint) < getSelectionDistance()) {
            closestPoint
        } else {
            point
        }
        
        println("Установлена якорная точка: (${anchorPoint!!.x}, ${anchorPoint!!.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDragging || anchorPoint == null) return false
        
        val currentPoint = offsetToPoint(offset)
        
        // Установить точку освобождения
        val closestPoint = findClosestPoint(currentPoint)
        releasePoint = if (OritaCalc.distance(currentPoint, closestPoint) < getSelectionDistance()) {
            closestPoint
        } else {
            currentPoint
        }
        
        // Создать сегмент перетаскивания
        dragSegment = LineSegment(anchorPoint!!, releasePoint!!, getLineColor())
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDragging) return false
        
        val endPoint = offsetToPoint(offset)
        
        // Установить финальную точку освобождения
        val closestEndPoint = findClosestPoint(endPoint)
        releasePoint = if (OritaCalc.distance(endPoint, closestEndPoint) < getSelectionDistance()) {
            closestEndPoint
        } else {
            endPoint
        }
        
        // Создать финальный сегмент
        dragSegment = LineSegment(anchorPoint!!, releasePoint!!, getLineColor())
        
        // Проверить, что сегмент имеет длину
        if (dragSegment?.determineLength() ?: 0.0 > 0.001) {
            // Выполнить деление сегмента
            performDivision()
        } else {
            println("Сегмент слишком короткий для деления")
        }
        
        // Сбросить состояние
        reset()
        
        return true
    }
    
    private fun performDivision() {
        dragSegment?.let { segment ->
            createdSegments.clear()
            
            // Создать разделенные сегменты
            for (i in 0 until dividingNumber - 1) {
                val ax = ((dividingNumber - i) * segment.determineAX() + i * segment.determineBX()) / dividingNumber.toDouble()
                val ay = ((dividingNumber - i) * segment.determineAY() + i * segment.determineBY()) / dividingNumber.toDouble()
                val bx = ((dividingNumber - i - 1) * segment.determineAX() + (i + 1) * segment.determineBX()) / dividingNumber.toDouble()
                val by = ((dividingNumber - i - 1) * segment.determineAY() + (i + 1) * segment.determineBY()) / dividingNumber.toDouble()
                
                val dividedSegment = LineSegment(Point(ax, ay), Point(bx, by), getLineColor())
                createdSegments.add(dividedSegment)
                
                // Добавить сегмент в набор
                addLineSegment(dividedSegment)
                
                println("Создан разделенный сегмент $i: (${ax}, ${ay}) -> (${bx}, ${by})")
            }
            
            println("Выполнено деление сегмента на ${dividingNumber - 1} частей")
        }
    }
    
    override fun reset() {
        anchorPoint = null
        releasePoint = null
        dragSegment = null
        isDragging = false
    }
    
    private fun findClosestPoint(point: Point): Point {
        // Здесь должна быть логика поиска ближайшей точки
        // Пока возвращаем исходную точку
        return point
    }
    
    override fun getSelectionDistance(): Double {
        // Здесь должна быть логика получения расстояния выбора
        return 10.0
    }
    
    override fun getLineColor(): LineColor {
        // Здесь должна быть логика получения текущего цвета линии
        return LineColor.BLACK_0
    }
    
    override fun addLineSegment(segment: LineSegment) {
        // Здесь должна быть логика добавления сегмента линии в набор
        println("Сегмент линии добавлен в набор")
    }
    
    fun setDividingNumber(number: Int) {
        if (number > 1) {
            dividingNumber = number
            println("Количество делений установлено: $number")
        } else {
            println("Количество делений должно быть больше 1")
        }
    }
    
    fun getDividingNumber(): Int = dividingNumber
    
    fun getAnchorPoint(): Point? = anchorPoint
    
    fun getReleasePoint(): Point? = releasePoint
    
    fun getDragSegment(): LineSegment? = dragSegment
    
    fun getCreatedSegments(): List<LineSegment> = createdSegments.toList()
    
    fun getCreatedCount(): Int = createdSegments.size
    
    fun isDragging(): Boolean = isDragging
    
    fun clearCreatedSegments() {
        createdSegments.clear()
    }
    
    fun getSegmentLength(): Double {
        return dragSegment?.determineLength() ?: 0.0
    }
    
    fun hasValidSegment(): Boolean {
        return dragSegment?.determineLength() ?: 0.0 > 0.001
    }
    
    fun getSegmentDescription(): String {
        return dragSegment?.let { segment ->
            "Сегмент: (${segment.determineAX()}, ${segment.determineAY()}) -> (${segment.determineBX()}, ${segment.determineBY()})"
        } ?: "Нет сегмента"
    }
    
    fun getDivisionDescription(): String {
        return "Деление на ${dividingNumber - 1} сегментов"
    }
    
    override fun getName(): String = "Деление сегментов линий"
    
    override fun getDescription(): String = "Разделите сегмент линии на несколько частей"
} 