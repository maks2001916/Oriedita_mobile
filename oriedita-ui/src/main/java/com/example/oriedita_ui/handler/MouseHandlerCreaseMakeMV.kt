package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик создания горы/долины
 * Адаптированная версия MouseHandlerCreaseMakeMV для Android
 */
class MouseHandlerCreaseMakeMV : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var currentLine: LineSegment? = null
    private var isDrawing = false
    private var lineColor: LineColor = LineColor.RED_1
    private var overlappingLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        isDrawing = true
        
        // Найти ближайшую точку
        val closestPoint = findClosestPoint(point)
        
        if (point.distance(closestPoint) > getSelectionDistance()) {
            // Точка слишком далеко - сбросить
            startPoint = null
            isDrawing = false
            return false
        }
        
        // Создать начальную линию
        currentLine = LineSegment(point, closestPoint, lineColor)
        
        println("Начало создания горы/долины в точке: (${point.x}, ${point.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing) return false
        
        val currentPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Обновить текущую линию
        currentLine = LineSegment(start, currentPoint, lineColor)
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing) return false
        
        val endPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Найти ближайшую точку к конечной точке
        val closestEndPoint = findClosestPoint(endPoint)
        
        // Обновить конечную точку линии
        currentLine = currentLine?.let { line ->
            LineSegment(Point(line.determineAX(), line.determineAY()), closestEndPoint, lineColor)
        }
        
        if (endPoint.distance(closestEndPoint) <= getSelectionDistance()) {
            currentLine?.let { line ->
                if (line.determineLength() > 0.001) {
                    // Найти перекрывающиеся линии
                    findOverlappingLines(line)
                    
                    // Применить цвета к перекрывающимся линиям
                    applyColorsToOverlappingLines()
                    
                    println("Создана гора/долина: ${overlappingLines.size} перекрывающихся линий")
                }
            }
        }
        
        // Сбросить состояние
        startPoint = null
        currentLine = null
        isDrawing = false
        overlappingLines.clear()
        
        return true
    }
    
    private fun findOverlappingLines(line: LineSegment) {
        overlappingLines.clear()
        
        // Найти все линии, которые перекрываются с текущей линией
        val allLines = getAllLines()
        
        for (existingLine in allLines) {
            if (isLineSegmentOverlapping(existingLine, line)) {
                val endPoint = Point(line.determineBX(), line.determineBY())
                val distance = determineLineSegmentDistance(endPoint, existingLine)
                overlappingLines.add(existingLine)
                println("Найдена перекрывающаяся линия на расстоянии: $distance")
            }
        }
        
        // Сортировать по расстоянию
        overlappingLines.sortBy { 
            val endPoint = Point(line.determineBX(), line.determineBY())
            determineLineSegmentDistance(endPoint, it) 
        }
    }
    
    private fun applyColorsToOverlappingLines() {
        var currentColor = lineColor
        
        for (line in overlappingLines) {
            // Установить цвет линии
            setLineColor(line, currentColor)
            
            // Переключить цвет для следующей линии
            currentColor = when (currentColor) {
                LineColor.RED_1 -> LineColor.BLUE_2
                LineColor.BLUE_2 -> LineColor.RED_1
                else -> currentColor
            }
            
            println("Установлен цвет линии: ${getColorName(currentColor)}")
        }
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
    
    private fun getAllLines(): List<LineSegment> {
        // Здесь должна быть логика получения всех линий
        // Пока возвращаем пустой список
        return emptyList()
    }
    
    private fun isLineSegmentOverlapping(line1: LineSegment, line2: LineSegment): Boolean {
        // Здесь должна быть логика проверки перекрытия линий
        // Пока возвращаем false
        return false
    }
    
    private fun determineLineSegmentDistance(point: Point, line: LineSegment): Double {
        // Здесь должна быть логика вычисления расстояния от точки до линии
        // Пока возвращаем 0.0
        return 0.0
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
    
    fun setLineColor(color: LineColor) {
        lineColor = color
        println("Установлен цвет для создания: ${getColorName(color)}")
    }
    
    override fun getLineColor(): LineColor = lineColor
    
    fun getCurrentLine(): LineSegment? = currentLine
    
    fun getOverlappingLines(): List<LineSegment> = overlappingLines.toList()
    
    fun getOverlappingCount(): Int = overlappingLines.size
    
    fun isDrawing(): Boolean = isDrawing
    
    fun clearOverlappingLines() {
        overlappingLines.clear()
    }
    
    override fun getName(): String = "Создание горы/долины"
    
    override fun getDescription(): String = "Создайте линии сгиба типа гора/долина"
} 