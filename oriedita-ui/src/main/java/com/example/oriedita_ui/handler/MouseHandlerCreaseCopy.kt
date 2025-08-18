package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик копирования линий сгиба
 * Адаптированная версия MouseHandlerCreaseCopy для Android
 */
class MouseHandlerCreaseCopy : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var lastPoint: Point? = null
    private var isCopying = false
    private var selectedLines = mutableListOf<LineSegment>()
    private var copiedLines = mutableListOf<LineSegment>()
    private var delta = Point(0.0, 0.0)
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        lastPoint = point
        isCopying = true
        
        println("Начало копирования линий сгиба в точке: (${point.x}, ${point.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isCopying) return false
        
        val currentPoint = offsetToPoint(offset)
        val last = lastPoint ?: return false
        
        // Вычислить смещение
        val deltaX = currentPoint.x - last.x
        val deltaY = currentPoint.y - last.y
        
        delta = Point(deltaX, deltaY)
        
        // Показать предварительный просмотр копирования
        showCopyPreview(deltaX, deltaY)
        
        lastPoint = currentPoint
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isCopying) return false
        
        val endPoint = offsetToPoint(offset)
        
        // Выполнить финальное копирование
        performFinalCopy()
        
        // Сбросить состояние
        startPoint = null
        lastPoint = null
        isCopying = false
        delta = Point(0.0, 0.0)
        
        println("Завершено копирование линий сгиба")
        
        return true
    }
    
    private fun showCopyPreview(deltaX: Double, deltaY: Double) {
        // Создать копии линий для предварительного просмотра
        copiedLines.clear()
        selectedLines.forEach { line ->
            val copiedLine = createCopiedLine(line, deltaX, deltaY)
            copiedLines.add(copiedLine)
        }
        
        println("Предварительный просмотр копирования: ${copiedLines.size} линий")
    }
    
    private fun performFinalCopy() {
        if (delta.distance(Point(0.0, 0.0)) > 0.001) {
            // Создать временный набор линий для копирования
            val tempLineSet = createTempLineSet()
            
            // Переместить временный набор
            tempLineSet.move(delta.x, delta.y)
            
            // Снять выделение с копий
            tempLineSet.unselectAll()
            
            // Добавить копии в основной набор
            val oldTotal = getTotalLinesCount()
            addCopiedLines(tempLineSet)
            val newTotal = getTotalLinesCount()
            
            // Разделить линии с новыми линиями
            divideLinesWithNewLines(oldTotal, newTotal)
            
            // Снять выделение
            clearSelection()
            
            println("Выполнено копирование на (${delta.x}, ${delta.y}): $oldTotal -> $newTotal линий")
        }
    }
    
    private fun createCopiedLine(originalLine: LineSegment, deltaX: Double, deltaY: Double): LineSegment {
        val newStart = Point(originalLine.determineAX() + deltaX, originalLine.determineAY() + deltaY)
        val newEnd = Point(originalLine.determineBX() + deltaX, originalLine.determineBY() + deltaY)
        return LineSegment(newStart, newEnd)
    }
    
    private fun createTempLineSet(): TempLineSet {
        val tempSet = TempLineSet()
        selectedLines.forEach { line ->
            tempSet.addLine(line)
        }
        return tempSet
    }
    
    private fun addCopiedLines(tempLineSet: TempLineSet) {
        // Здесь должна быть логика добавления скопированных линий
        val copiedLines = tempLineSet.getLines()
        println("Добавлены скопированные линии: ${copiedLines.size}")
    }
    
    private fun divideLinesWithNewLines(oldTotal: Int, newTotal: Int) {
        // Здесь должна быть логика разделения линий с новыми линиями
        println("Разделение линий: $oldTotal -> $newTotal")
    }
    
    private fun getTotalLinesCount(): Int {
        // Здесь должна быть логика подсчета общего количества линий
        return 0
    }
    
    private fun clearSelection() {
        selectedLines.clear()
        copiedLines.clear()
        println("Выделение очищено")
    }
    
    fun setSelectedLines(lines: List<LineSegment>) {
        selectedLines.clear()
        selectedLines.addAll(lines)
    }
    
    fun getSelectedLines(): List<LineSegment> = selectedLines.toList()
    
    fun getCopiedLines(): List<LineSegment> = copiedLines.toList()
    
    fun getDelta(): Point = delta
    
    fun isCopying(): Boolean = isCopying
    
    fun getCopyDistance(): Double = delta.distance(Point(0.0, 0.0))
    
    // Временный класс для работы с набором линий
    private class TempLineSet {
        private val lines = mutableListOf<LineSegment>()
        
        fun move(deltaX: Double, deltaY: Double) {
            lines.forEach { line ->
                val newStart = Point(line.determineAX() + deltaX, line.determineAY() + deltaY)
                val newEnd = Point(line.determineBX() + deltaX, line.determineBY() + deltaY)
                // Обновить позицию линии
            }
        }
        
        fun unselectAll() {
            // Здесь должна быть логика снятия выделения со всех линий
            println("Снято выделение со всех линий в временном наборе")
        }
        
        fun addLine(line: LineSegment) {
            lines.add(line)
        }
        
        fun getLines(): List<LineSegment> = lines.toList()
    }
    
    override fun getName(): String = "Копирование линий сгиба"
    
    override fun getDescription(): String = "Скопируйте выбранные линии сгиба"
} 