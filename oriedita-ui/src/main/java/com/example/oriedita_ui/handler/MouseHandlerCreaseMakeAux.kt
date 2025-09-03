package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик создания вспомогательных линий
 * Адаптированная версия MouseHandlerCreaseMakeAux для Android
 */
class MouseHandlerCreaseMakeAux : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var endPoint: Point? = null
    private var isSelecting = false
    private var convertedLines = mutableListOf<LineSegment>()
    private var createdAuxLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        isSelecting = true
        
        println("Начало создания вспомогательных линий в точке: (${point.x}, ${point.y})")
        
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
            if (closestLine.color.number < 3) {
                // Преобразовать линию в вспомогательную
                val auxLine = LineSegment(closestLine.getA(), closestLine.getB(), LineColor.CYAN_3)
                
                // Удалить исходную линию
                deleteLineSegment(closestLine)
                
                // Добавить вспомогательную линию
                addLineSegment(auxLine)
                
                convertedLines.add(closestLine)
                createdAuxLines.add(auxLine)
                
                println("Преобразована линия в вспомогательную: (${closestLine.determineAX()}, ${closestLine.determineAY()}) -> (${closestLine.determineBX()}, ${closestLine.determineBY()})")
            }
        }
    }
    
    private fun performRectangleConversion(start: Point, end: Point) {
        // Преобразовать линии в прямоугольнике в вспомогательные
        val linesInBox = findLinesInBox(start, end)
        var convertedCount = 0
        
        for (line in linesInBox) {
            if (line.color.number < 3) {
                // Преобразовать линию в вспомогательную
                val auxLine = LineSegment(line.getA(), line.getB(), LineColor.CYAN_3)
                
                // Удалить исходную линию
                deleteLineSegment(line)
                
                // Добавить вспомогательную линию
                addLineSegment(auxLine)
                
                convertedLines.add(line)
                createdAuxLines.add(auxLine)
                convertedCount++
                
                println("Преобразована линия в вспомогательную: (${line.determineAX()}, ${line.determineAY()}) -> (${line.determineBX()}, ${line.determineBY()})")
            }
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
    
    private fun deleteLineSegment(line: LineSegment) {
        // Здесь должна быть логика удаления сегмента линии
        println("Удален сегмент линии")
    }
    
    override fun addLineSegment(line: LineSegment) {
        // Здесь должна быть логика добавления сегмента линии
        println("Добавлен сегмент линии")
    }
    
    private fun findLinesInBox(start: Point, end: Point): List<LineSegment> {
        // Здесь должна быть логика поиска линий в прямоугольнике
        // Пока возвращаем пустой список
        return emptyList()
    }
    
    fun getConvertedLines(): List<LineSegment> = convertedLines.toList()
    
    fun getCreatedAuxLines(): List<LineSegment> = createdAuxLines.toList()
    
    fun getConvertedCount(): Int = convertedLines.size
    
    fun getCreatedCount(): Int = createdAuxLines.size
    
    fun isSelecting(): Boolean = isSelecting
    
    fun clearConvertedLines() {
        convertedLines.clear()
    }
    
    fun clearCreatedAuxLines() {
        createdAuxLines.clear()
    }
    
    fun getConversionDescription(): String {
        return "Преобразовано ${convertedLines.size} линий в вспомогательные"
    }
    
    override fun getName(): String = "Создание вспомогательных линий"
    
    override fun getDescription(): String = "Преобразуйте линии в вспомогательные"
} 