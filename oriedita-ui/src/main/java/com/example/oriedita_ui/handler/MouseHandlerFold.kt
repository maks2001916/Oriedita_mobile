package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик сгибания
 * Адаптированная версия MouseHandlerFold для Android
 */
class MouseHandlerFold : BaseMouseHandler() {
    
    private var foldLine: LineSegment? = null
    private var startPoint: Point? = null
    private var isFolding = false
    private var foldDirection: FoldDirection = FoldDirection.MOUNTAIN
    
    enum class FoldDirection {
        MOUNTAIN,  // Гора
        VALLEY     // Долина
    }
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        isFolding = true
        
        println("Начало сгибания в точке: (${point.x}, ${point.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isFolding) return false
        
        val currentPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать временную линию сгиба
        foldLine = LineSegment(start, currentPoint)
        
        println("Предварительный сгиб: (${start.x}, ${start.y}) -> (${currentPoint.x}, ${currentPoint.y})")
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isFolding) return false
        
        val endPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать финальную линию сгиба
        val finalFoldLine = LineSegment(start, endPoint)
        foldLine = finalFoldLine
        
        // Выполнить сгибание
        performFold(finalFoldLine)
        
        // Сбросить состояние
        startPoint = null
        foldLine = null
        isFolding = false
        
        return true
    }
    
    private fun performFold(line: LineSegment) {
        // Здесь должна быть логика выполнения сгибания
        println("Выполнен сгиб: (${line.a.x}, ${line.a.y}) -> (${line.b.x}, ${line.b.y}) направление: ${getFoldDirectionName()}")
        
        // Добавить линию сгиба на холст
        addFoldLineToCanvas(line)
    }
    
    private fun addFoldLineToCanvas(line: LineSegment) {
        // Здесь должна быть логика добавления линии сгиба на холст
        println("Добавлена линия сгиба: (${line.a.x}, ${line.a.y}) -> (${line.b.x}, ${line.b.y})")
    }
    
    fun setFoldDirection(direction: FoldDirection) {
        foldDirection = direction
        println("Направление сгиба изменено на: ${getFoldDirectionName()}")
    }
    
    fun getFoldDirection(): FoldDirection = foldDirection
    
    fun getFoldDirectionName(): String {
        return when (foldDirection) {
            FoldDirection.MOUNTAIN -> "Гора"
            FoldDirection.VALLEY -> "Долина"
        }
    }
    
    fun toggleFoldDirection() {
        foldDirection = if (foldDirection == FoldDirection.MOUNTAIN) {
            FoldDirection.VALLEY
        } else {
            FoldDirection.MOUNTAIN
        }
        println("Направление сгиба переключено на: ${getFoldDirectionName()}")
    }
    
    fun getCurrentFoldLine(): LineSegment? = foldLine
    
    fun getStartPoint(): Point? = startPoint
    
    fun isFolding(): Boolean = isFolding
    
    override fun getName(): String = "Сгибание"
    
    override fun getDescription(): String = "Выполните сгибание по линии"
} 