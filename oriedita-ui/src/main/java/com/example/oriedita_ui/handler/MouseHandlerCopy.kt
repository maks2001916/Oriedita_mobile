package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point

/**
 * Обработчик копирования линий
 * Адаптированная версия MouseHandlerCopy для Android
 */
class MouseHandlerCopy : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var copiedLines = mutableListOf<LineSegment>()
    private var selectedLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val currentPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Показать предварительный просмотр копирования
        showCopyPreview(start, currentPoint)
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val endPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Выполнить копирование
        performCopy(start, endPoint)
        
        // Сбросить состояние
        startPoint = null
        
        return true
    }
    
    private fun showCopyPreview(start: Point, end: Point) {
        // Здесь должна быть логика показа предварительного просмотра копирования
        val deltaX = end.x - start.x
        val deltaY = end.y - start.y
        println("Предварительный просмотр копирования: dx=$deltaX, dy=$deltaY")
    }
    
    private fun performCopy(start: Point, end: Point) {
        val deltaX = end.x - start.x
        val deltaY = end.y - start.y
        
        // Создать копии выбранных линий
        selectedLines.forEach { line ->
            val newLine = LineSegment(
                Point(line.determineAX() + deltaX, line.determineAY() + deltaY),
                Point(line.determineBX() + deltaX, line.determineBY() + deltaY)
            )
            copiedLines.add(newLine)
            addLineToCanvas(newLine)
        }
        
        println("Скопировано ${selectedLines.size} линий")
    }
    
    private fun addLineToCanvas(line: LineSegment) {
        // Здесь должна быть логика добавления линии на холст
        println("Добавлена скопированная линия: ${line.determineAX()}, ${line.determineAY()} -> ${line.determineBX()}, ${line.determineBY()}")
    }
    
    fun setSelectedLines(lines: List<LineSegment>) {
        selectedLines.clear()
        selectedLines.addAll(lines)
    }
    
    fun getCopiedLines(): List<LineSegment> = copiedLines.toList()
    
    fun clearCopiedLines() {
        copiedLines.clear()
    }
    
    override fun getName(): String = "Копирование"
    
    override fun getDescription(): String = "Скопируйте выбранные линии"
} 