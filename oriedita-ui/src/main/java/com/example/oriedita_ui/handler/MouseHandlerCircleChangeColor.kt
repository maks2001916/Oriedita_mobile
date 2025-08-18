package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик изменения цвета кругов
 * Адаптированная версия для Android
 */
class MouseHandlerCircleChangeColor : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var endPoint: Point? = null
    private var isSelecting = false
    private var changedElements = mutableListOf<Any>()
    private var customCircleColor = LineColor.RED_1
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        isSelecting = true
        
        println("Начало изменения цвета кругов в точке: (${point.x}, ${point.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isSelecting) return false
        
        val currentPoint = offsetToPoint(offset)
        endPoint = currentPoint
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isSelecting) return false
        
        val releasePoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Проверить, является ли это одиночным кликом или прямоугольным выделением
        if (start.distance(releasePoint) <= 0.000001) {
            // Одиночный клик - изменить цвет ближайшего элемента
            performSingleColorChange(releasePoint)
        } else {
            // Прямоугольное выделение - изменить цвета элементов в прямоугольнике
            performRectangleColorChange(start, releasePoint)
        }
        
        // Сбросить состояние
        startPoint = null
        endPoint = null
        isSelecting = false
        clearLineStep()
        
        return true
    }
    
    private fun performSingleColorChange(point: Point) {
        val closestLineDistance = getClosestLineSegmentDistance(point)
        val closestCircleDistance = getClosestCircleDistance(point)
        
        if (closestLineDistance <= closestCircleDistance) {
            // Ближайший элемент - линия
            if (closestLineDistance < getSelectionDistance()) {
                val closestLine = getClosestLineSegmentReversedOrder(point)
                if (closestLine.color == LineColor.CYAN_3) {
                    setLineCustomized(closestLine, true)
                    setLineCustomizedColor(closestLine, customCircleColor)
                    changedElements.add(closestLine)
                    
                    println("Изменен цвет линии: (${closestLine.determineAX()}, ${closestLine.determineAY()}) -> (${closestLine.determineBX()}, ${closestLine.determineBY()})")
                }
            }
        } else {
            // Ближайший элемент - круг
            if (closestCircleDistance < getSelectionDistance()) {
                val circleIndex = getClosestCircleSearchReverseOrder(point)
                setCircleCustomized(circleIndex, true)
                setCircleCustomizedColor(circleIndex, customCircleColor)
                changedElements.add("Circle_$circleIndex")
                
                println("Изменен цвет круга с индексом: $circleIndex")
            }
        }
    }
    
    private fun performRectangleColorChange(start: Point, end: Point) {
        // Изменить цвета элементов в прямоугольнике
        val elementsInBox = findElementsInBox(start, end)
        var changedCount = 0
        
        for (element in elementsInBox) {
            when (element) {
                is LineSegment -> {
                    if (element.color == LineColor.CYAN_3) {
                        setLineCustomized(element, true)
                        setLineCustomizedColor(element, customCircleColor)
                        changedElements.add(element)
                        changedCount++
                        
                        println("Изменен цвет линии в прямоугольнике: (${element.determineAX()}, ${element.determineAY()}) -> (${element.determineBX()}, ${element.determineBY()})")
                    }
                }
                is Int -> {
                    setCircleCustomized(element, true)
                    setCircleCustomizedColor(element, customCircleColor)
                    changedElements.add("Circle_$element")
                    changedCount++
                    
                    println("Изменен цвет круга в прямоугольнике с индексом: $element")
                }
            }
        }
        
        println("Изменено цветов элементов в прямоугольнике: $changedCount")
    }
    
    private fun getClosestLineSegmentDistance(point: Point): Double {
        // Здесь должна быть логика получения расстояния до ближайшей линии
        return 10.0
    }
    
    private fun getClosestCircleDistance(point: Point): Double {
        // Здесь должна быть логика получения расстояния до ближайшего круга
        return 15.0
    }
    
    private fun getClosestLineSegmentReversedOrder(point: Point): LineSegment {
        // Здесь должна быть логика получения ближайшей линии в обратном порядке
        return LineSegment(Point(0.0, 0.0), Point(1.0, 1.0), LineColor.CYAN_3)
    }
    
    private fun getClosestCircleSearchReverseOrder(point: Point): Int {
        // Здесь должна быть логика получения индекса ближайшего круга в обратном порядке
        return 0
    }
    
    private fun setLineCustomized(line: LineSegment, customized: Boolean) {
        // Здесь должна быть логика установки кастомизации линии
        println("Линия кастомизирована: $customized")
    }
    
    private fun setLineCustomizedColor(line: LineSegment, color: LineColor) {
        // Здесь должна быть логика установки кастомного цвета линии
        println("Установлен кастомный цвет линии: ${getColorName(color)}")
    }
    
    private fun setCircleCustomized(circleIndex: Int, customized: Boolean) {
        // Здесь должна быть логика установки кастомизации круга
        println("Круг $circleIndex кастомизирован: $customized")
    }
    
    private fun setCircleCustomizedColor(circleIndex: Int, color: LineColor) {
        // Здесь должна быть логика установки кастомного цвета круга
        println("Установлен кастомный цвет круга $circleIndex: ${getColorName(color)}")
    }
    
    private fun findElementsInBox(start: Point, end: Point): List<Any> {
        // Здесь должна быть логика поиска элементов в прямоугольнике
        // Пока возвращаем пустой список
        return emptyList()
    }
    
    private fun getColorName(color: LineColor): String {
        return when (color) {
            LineColor.BLACK_0 -> "Черный (Край)"
            LineColor.RED_1 -> "Красный (Гора)"
            LineColor.BLUE_2 -> "Синий (Долина)"
            LineColor.CYAN_3 -> "Голубой (Вспомогательная)"
            LineColor.ORANGE_4 -> "Оранжевый"
            LineColor.GREEN_6 -> "Зеленый"
            LineColor.PURPLE_8 -> "Фиолетовый"
            else -> "Другой"
        }
    }
    
    fun setCustomCircleColor(color: LineColor) {
        customCircleColor = color
        println("Установлен кастомный цвет круга: ${getColorName(color)}")
    }
    
    override fun getCustomCircleColor(): LineColor = customCircleColor
    
    fun getChangedElements(): List<Any> = changedElements.toList()
    
    fun getChangedCount(): Int = changedElements.size
    
    fun isSelecting(): Boolean = isSelecting
    
    fun clearChangedElements() {
        changedElements.clear()
    }
    
    fun getColorChangeDescription(): String {
        return "Изменено цветов ${changedElements.size} элементов"
    }
    
    fun getCustomColorDescription(): String {
        return "Кастомный цвет круга: ${getColorName(customCircleColor)}"
    }
    
    override fun getName(): String = "Изменение цвета кругов"
    
    override fun getDescription(): String = "Изменяйте цвета кругов и вспомогательных линий"
} 