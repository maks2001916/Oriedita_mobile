package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик создания вспомогательных живых линий
 * Адаптированная версия для Android
 */
class MouseHandlerCreaseMakeAuxLive : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var endPoint: Point? = null
    private var isSelecting = false
    private var convertedLines = mutableListOf<LineSegment>()
    private var createdAuxLiveLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        isSelecting = true
        
        println("Начало создания вспомогательных живых линий в точке: (${point.x}, ${point.y})")
        
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
        
        // Сбросить состояние
        startPoint = null
        endPoint = null
        isSelecting = false
        
        return true
    }
    
    private fun performSingleConversion(point: Point) {
        val closestLine = findClosestLine(point)
        
        if (closestLine != null && point.distance(getLineCenter(closestLine)) < getSelectionDistance()) {
            // Преобразовать линию в вспомогательную живую (голубую)
            val auxLiveLine = LineSegment(closestLine.a, closestLine.b, LineColor.CYAN_3)

            // Установить цвет линии
            setLineColor(closestLine, LineColor.CYAN_3)
            
            convertedLines.add(closestLine)
            createdAuxLiveLines.add(auxLiveLine)
            
            println("Преобразована линия в вспомогательную живую: (${closestLine.determineAX()}, ${closestLine.determineAY()}) -> (${closestLine.determineBX()}, ${closestLine.determineBY()})")
        }
    }
    
    private fun performRectangleConversion(start: Point, end: Point) {
        // Преобразовать линии в прямоугольнике в вспомогательные живые
        val linesInBox = findLinesInBox(start, end)
        var convertedCount = 0
        
        for (line in linesInBox) {
            // Преобразовать линию в вспомогательную живую (голубую)
            val auxLiveLine = LineSegment(line.a, line.b, LineColor.CYAN_3)
            // Установить цвет линии
            setLineColor(line, LineColor.CYAN_3)
            
            convertedLines.add(line)
            createdAuxLiveLines.add(auxLiveLine)
            convertedCount++
            
            println("Преобразована линия в вспомогательную живую: (${line.determineAX()}, ${line.determineAY()}) -> (${line.determineBX()}, ${line.determineBY()})")
        }
        
        println("Преобразовано линий в прямоугольнике: $convertedCount")
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
            LineColor.CYAN_3 -> "Голубой (Вспомогательная живая)"
            else -> "Другой"
        }
    }
    
    private fun findLinesInBox(start: Point, end: Point): List<LineSegment> {
        // Здесь должна быть логика поиска линий в прямоугольнике
        // Пока возвращаем пустой список
        return emptyList()
    }
    
    fun getConvertedLines(): List<LineSegment> = convertedLines.toList()
    
    fun getCreatedAuxLiveLines(): List<LineSegment> = createdAuxLiveLines.toList()
    
    fun getConvertedCount(): Int = convertedLines.size
    
    fun getCreatedCount(): Int = createdAuxLiveLines.size
    
    fun isSelecting(): Boolean = isSelecting
    
    fun clearConvertedLines() {
        convertedLines.clear()
    }
    
    fun clearCreatedAuxLiveLines() {
        createdAuxLiveLines.clear()
    }
    
    fun getConversionDescription(): String {
        return "Преобразовано ${convertedLines.size} линий в вспомогательные живые"
    }
    
    fun getAuxLiveLineDescription(): String {
        return "Вспомогательные живые линии (голубые) используются для временных построений"
    }
    
    override fun getName(): String = "Создание вспомогательных живых линий"
    
    override fun getDescription(): String = "Преобразуйте линии в вспомогательные живые (голубые)"
} 