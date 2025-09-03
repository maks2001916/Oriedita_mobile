package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик переключения типа сгиба
 * Адаптированная версия MouseHandlerCreaseToggleMV для Android
 */
class MouseHandlerCreaseToggleMV : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var toggledLines = mutableListOf<LineSegment>()
    private var isToggling = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        isToggling = true
        
        println("Начало переключения типа сгиба в точке: (${point.x}, ${point.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isToggling) return false
        
        val currentPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать прямоугольник для переключения
        val minX = minOf(start.x, currentPoint.x)
        val maxX = maxOf(start.x, currentPoint.x)
        val minY = minOf(start.y, currentPoint.y)
        val maxY = maxOf(start.y, currentPoint.y)
        
        val linesInBox = findLinesInBox(minX, maxX, minY, maxY)
        toggledLines.clear()
        toggledLines.addAll(linesInBox)
        
        println("Прямоугольное переключение: ${linesInBox.size} линий")
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isToggling) return false
        
        val endPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Выполнить переключение типа сгиба
        performToggle(start, endPoint)
        
        // Сбросить состояние
        startPoint = null
        isToggling = false
        
        return true
    }
    
    private fun performToggle(start: Point, end: Point) {
        if (start.distance(end) > 0.001) {
            // Переключение в прямоугольнике
            if (toggleLinesInRectangle(start, end) != 0) {
                println("Переключен тип сгиба в прямоугольнике")
            }
        } else {
            // Переключение ближайшей линии
            toggleClosestLine(start)
        }
    }
    
    private fun toggleLinesInRectangle(start: Point, end: Point): Int {
        val minX = minOf(start.x, end.x)
        val maxX = maxOf(start.x, end.x)
        val minY = minOf(start.y, end.y)
        val maxY = maxOf(start.y, end.y)
        
        val linesInBox = findLinesInBox(minX, maxX, minY, maxY)
        var toggleCount = 0
        
        for (line in linesInBox) {
            if (toggleLineColor(line)) {
                toggleCount++
            }
        }
        
        println("Переключено $toggleCount линий в прямоугольнике")
        return toggleCount
    }
    
    private fun toggleClosestLine(point: Point) {
        val closestLine = findClosestLine(point)
        if (closestLine != null) {
            if (toggleLineColor(closestLine)) {
                println("Переключен тип сгиба ближайшей линии")
            }
        }
    }
    
    private fun toggleLineColor(line: LineSegment): Boolean {
        val currentColor = getLineColor(line)
        
        val newColor = when (currentColor) {
            LineColor.RED_1 -> LineColor.BLUE_2
            LineColor.BLUE_2 -> LineColor.RED_1
            else -> currentColor // Не изменять другие цвета
        }
        
        if (newColor != currentColor) {
            setLineColor(line, newColor)
            println("Переключен цвет линии: ${getColorName(currentColor)} -> ${getColorName(newColor)}")
            return true
        }
        
        return false
    }
    
    private fun getLineColor(line: LineSegment): LineColor {
        // Здесь должна быть логика получения цвета линии
        // Пока возвращаем красный цвет
        return LineColor.RED_1
    }

    override fun setLineColor(line: LineSegment, color: LineColor) {
        // Здесь должна быть логика установки цвета линии
        println("Установлен цвет линии: ${getColorName(color)}")

        // Создать новую линию с измененным цветом
        val newLine = LineSegment(line.getA(), line.getB(), color)

        // Обновить цвет в базовом классе
        super.setLineColor(line, color)
    }
    
    private fun getColorName(color: LineColor): String {
        return when (color) {
            LineColor.RED_1 -> "Красный (Гора)"
            LineColor.BLUE_2 -> "Синий (Долина)"
            else -> "Другой"
        }
    }
    
    private fun findClosestLine(point: Point): LineSegment? {
        // Здесь должна быть логика поиска ближайшей линии
        // Пока возвращаем null
        return null
    }
    
    private fun findLinesInBox(minX: Double, maxX: Double, minY: Double, maxY: Double): List<LineSegment> {
        // Здесь должна быть логика поиска линий в прямоугольнике
        // Пока возвращаем пустой список
        return emptyList()
    }
    
    fun getToggledLines(): List<LineSegment> = toggledLines.toList()
    
    fun clearToggledLines() {
        toggledLines.clear()
    }
    
    fun getToggledCount(): Int = toggledLines.size
    
    fun isToggling(): Boolean = isToggling
    
    fun toggleSingleLine(line: LineSegment): Boolean {
        return toggleLineColor(line)
    }
    
    fun getLineTypeName(line: LineSegment): String {
        val color = getLineColor(line)
        return when (color) {
            LineColor.RED_1 -> "Гора"
            LineColor.BLUE_2 -> "Долина"
            else -> "Неизвестно"
        }
    }
    
    override fun getName(): String = "Переключение типа сгиба"
    
    override fun getDescription(): String = "Переключите тип сгиба между горой и долиной"
} 