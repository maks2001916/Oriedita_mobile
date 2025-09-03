package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Circle
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment

/**
 * Обработчик рисования разделенных кругов
 * Адаптированная версия для Android
 */
class MouseHandlerCircleDrawSeparate : BaseMouseHandler() {
    
    private var isProcessing = false
    private var createdElements = mutableListOf<Any>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        val closestPoint = getClosestPoint(point)
        
        when (getLineStepSize()) {
            0 -> {
                handleFirstPress(point, closestPoint)
            }
            1 -> {
                handleSecondPress(point, closestPoint)
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        if (getLineStepSize() == 2) {
            // Обновить вторую линию и круг
            updateSecondLine(point)
            updateCircle()
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (getLineStepSize() == 2) {
            val point = offsetToPoint(offset)
            val closestPoint = getClosestPoint(point)
            
            // Обновить вторую линию с ближайшей точкой
            updateSecondLine(closestPoint)
            
            if (point.distance(closestPoint) <= getSelectionDistance()) {
                val secondLine = getLineStep(1) as LineSegment
                if (secondLine.determineLength() > 0.0) {
                    // Добавить линию и круг
                    addLineSegment(secondLine)
                    val firstPoint = (getLineStep(0) as LineSegment).getA()
                    val circle = Circle(firstPoint, secondLine.determineLength(), LineColor.CYAN_3)
                    addCircle(circle)
                    
                    createdElements.add(secondLine)
                    createdElements.add(circle)
                    
                    println("Добавлена разделяющая линия и круг")
                    println("Линия: (${secondLine.determineAX()}, ${secondLine.determineAY()}) -> (${secondLine.determineBX()}, ${secondLine.determineBY()})")
                    println("Круг: центр (${circle.determineCenter().x}, ${circle.determineCenter().y}), радиус ${circle.getR()}")
                }
            }
            
            // Очистить шаги
            clearLineStep()
            clearCircleStep()
        }
        
        return true
    }
    
    private fun handleFirstPress(point: Point, closestPoint: Point) {
        clearCircleStep()
        
        if (point.distance(closestPoint) <= getSelectionDistance()) {
            val lineSegment = LineSegment(closestPoint, closestPoint, LineColor.CYAN_3)
            addLineStep(lineSegment)
            println("Установлена начальная точка: (${closestPoint.x}, ${closestPoint.y})")
        }
    }
    
    private fun handleSecondPress(point: Point, closestPoint: Point) {
        if (point.distance(closestPoint) <= getSelectionDistance()) {
            val lineSegment = LineSegment(point, closestPoint, LineColor.CYAN_3)
            addLineStep(lineSegment)
            
            // Создать круг с нулевым радиусом
            val firstPoint = (getLineStep(0) as LineSegment).getA()
            val circle = Circle(firstPoint, 0.0, LineColor.CYAN_3)
            clearCircleStep()
            addCircleStep(circle)
            
            println("Установлена вторая точка: (${closestPoint.x}, ${closestPoint.y})")
        }
    }
    
    private fun updateSecondLine(point: Point) {
        if (getLineStepSize() >= 2) {
            val secondLine = getLineStep(1) as LineSegment
            val updatedLine = LineSegment(point, secondLine.getB(), secondLine.getColor())
            setLineStep(1, updatedLine)
        }
    }
    
    private fun updateCircle() {
        if (getLineStepSize() >= 1 && getCircleStepSize() >= 1) {
            val firstLine = getLineStep(0) as LineSegment
            val circle = getCircleStep(0) as Circle
            circle.setR(firstLine.determineLength())
        }
    }
    
    fun getCreatedElements(): List<Any> = createdElements.toList()
    
    fun getCreatedElementsCount(): Int = createdElements.size
    
    fun isProcessing(): Boolean = isProcessing
    
    fun clearCreatedElements() {
        createdElements.clear()
    }
    
    fun getSeparateCircleDescription(): String {
        return when (getLineStepSize()) {
            0 -> "Выберите начальную точку"
            1 -> "Выберите вторую точку"
            2 -> "Перетащите для настройки разделения"
            else -> "Неизвестное состояние"
        }
    }
    
    fun getSeparateCircleInfo(): String {
        return buildString {
            append("Шаги линий: ${getLineStepSize()}\n")
            append("Шаги кругов: ${getCircleStepSize()}\n")
            if (getLineStepSize() >= 1) {
                val firstLine = getLineStep(0) as LineSegment
                append("Первая линия: (${firstLine.determineAX()}, ${firstLine.determineAY()}) -> ")
                append("(${firstLine.determineBX()}, ${firstLine.determineBY()})\n")
            }
            if (getLineStepSize() >= 2) {
                val secondLine = getLineStep(1) as LineSegment
                append("Вторая линия: (${secondLine.determineAX()}, ${secondLine.determineAY()}) -> ")
                append("(${secondLine.determineBX()}, ${secondLine.determineBY()})\n")
            }
            append("Созданные элементы: ${createdElements.size}")
        }
    }
    
    override fun getName(): String = "Рисование разделенных кругов"
    
    override fun getDescription(): String = "Создавайте круги с разделяющими линиями"
} 