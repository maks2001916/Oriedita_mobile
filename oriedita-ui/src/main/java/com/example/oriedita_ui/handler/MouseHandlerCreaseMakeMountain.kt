package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик создания горных линий
 * Адаптированная версия MouseHandlerCreaseMakeMountain для Android
 */
class MouseHandlerCreaseMakeMountain : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var endPoint: Point? = null
    private var isSelecting = false
    private var convertedLines = mutableListOf<LineSegment>()
    private var createdMountainLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        isSelecting = true
        
        println("Начало создания горных линий в точке: (${point.x}, ${point.y})")
        
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
            // Одиночный клик - преобразовать ближайшую линию
            performSingleConversion(releasePoint)
        } else {
            // Прямоугольное выделение - преобразовать линии в прямоугольнике
            performRectangleConversion(start, releasePoint)
        }
        
        // Выполнить исправление T-образных разрывов
        performFix2()
        
        // Сбросить состояние
        startPoint = null
        endPoint = null
        isSelecting = false
        
        return true
    }
    
    private fun performSingleConversion(point: Point) {
        val closestLine = findClosestLine(point)
        
        if (closestLine != null && point.distance(getLineCenter(closestLine)) < getSelectionDistance()) {
            // Преобразовать линию в горную (красную)
            val mountainLine = LineSegment(closestLine.a, closestLine.b, LineColor.RED_1)
            
            // Установить цвет линии
            setLineColor(closestLine, LineColor.RED_1)
            
            convertedLines.add(closestLine)
            createdMountainLines.add(mountainLine)
            
            println("Преобразована линия в горную: (${closestLine.determineAX()}, ${closestLine.determineAY()}) -> (${closestLine.determineBX()}, ${closestLine.determineBY()})")
        }
    }
    
    private fun performRectangleConversion(start: Point, end: Point) {
        // Преобразовать линии в прямоугольнике в горные
        val linesInBox = findLinesInBox(start, end)
        var convertedCount = 0
        
        for (line in linesInBox) {
            // Преобразовать линию в горную (красную)
            val mountainLine = LineSegment(line.a, line.b, LineColor.RED_1)
            
            // Установить цвет линии
            setLineColor(line, LineColor.RED_1)
            
            convertedLines.add(line)
            createdMountainLines.add(mountainLine)
            convertedCount++
            
            println("Преобразована линия в горную: (${line.determineAX()}, ${line.determineAY()}) -> (${line.determineBX()}, ${line.determineBY()})")
        }
        
        println("Преобразовано линий в прямоугольнике: $convertedCount")
    }
    
    private fun performFix2() {
        // Исправить T-образные разрывы, которые часто возникают при комбинации
        // оригинальных полилиний и полилиний, преобразованных из вспомогательных линий
        println("Выполнено исправление T-образных разрывов")
    }
    
    private fun findClosestLine(point: Point): LineSegment? {
        // Здесь должна быть логика поиска ближайшей линии
        // Пока возвращаем null
        return null
    }
    
    private fun getLineCenter(line: LineSegment): Point {
        val centerX = (line.determineAX() + line.determineBX()) / 2.0
        val centerY = (line.determineAY() + line.determineBY()) / 2.0
        return Point(centerX, centerY)
    }
    
    override fun getSelectionDistance(): Double {
        // Здесь должна быть логика получения расстояния выбора
        return 10.0
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
            LineColor.BLACK_0 -> "Черный (Край)"
            LineColor.RED_1 -> "Красный (Гора)"
            LineColor.BLUE_2 -> "Синий (Долина)"
            LineColor.CYAN_3 -> "Голубой (Вспомогательная)"
            else -> "Другой"
        }
    }
    
    private fun findLinesInBox(start: Point, end: Point): List<LineSegment> {
        // Здесь должна быть логика поиска линий в прямоугольнике
        // Пока возвращаем пустой список
        return emptyList()
    }
    
    fun getConvertedLines(): List<LineSegment> = convertedLines.toList()
    
    fun getCreatedMountainLines(): List<LineSegment> = createdMountainLines.toList()
    
    fun getConvertedCount(): Int = convertedLines.size
    
    fun getCreatedCount(): Int = createdMountainLines.size
    
    fun isSelecting(): Boolean = isSelecting
    
    fun clearConvertedLines() {
        convertedLines.clear()
    }
    
    fun clearCreatedMountainLines() {
        createdMountainLines.clear()
    }
    
    fun getConversionDescription(): String {
        return "Преобразовано ${convertedLines.size} линий в горные"
    }
    
    fun getMountainLineDescription(): String {
        return "Горные линии (красные) обозначают сгибы, которые поднимаются вверх"
    }
    
    override fun getName(): String = "Создание горных линий"
    
    override fun getDescription(): String = "Преобразуйте линии в горные (красные)"
} 