package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor

/**
 * Обработчик продвинутого изменения типа
 * Адаптированная версия MouseHandlerCreaseAdvanceType для Android
 */
class MouseHandlerCreaseAdvanceType : BaseMouseHandler() {
    
    private var selectedLine: LineSegment? = null
    private var isProcessing = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        isProcessing = true
        
        // Сбросить предыдущее состояние
        reset()
        
        // Найти ближайшую линию
        val closestLine = findClosestLine(point)
        
        if (closestLine != null && point.distance(getLineCenter(closestLine)) < getSelectionDistance()) {
            selectedLine = closestLine
            
            // Удалить линию из основного набора
            removeLineFromSet(selectedLine!!)
            
            println("Выбрана линия для изменения типа: (${closestLine.determineAX()}, ${closestLine.determineAY()}) -> (${closestLine.determineBX()}, ${closestLine.determineBY()})")
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isProcessing) return false
        
        // Если есть выбранная линия, добавить её обратно
        selectedLine?.let { line ->
            addLineToSet(line)
            reset()
            println("Линия возвращена при перетаскивании")
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isProcessing) return false
        
        selectedLine?.let { line ->
            // Изменить тип линии согласно продвинутому алгоритму
            val modifiedLine = advanceLineType(line)
            
            // Добавить измененную линию обратно
            addLineToSet(modifiedLine)
            
            println("Изменен тип линии: ${getLineTypeDescription(modifiedLine)}")
        }
        
        // Сбросить состояние
        reset()
        
        return true
    }
    
    private fun advanceLineType(line: LineSegment): LineSegment {
        val currentColor = getLineColor(line)
        val currentSelected = getLineSelected(line)
        
        return when {
            // Черная линия, не выделенная -> выделить
            currentColor == LineColor.BLACK_0 && currentSelected == 0 -> {
                setLineSelected(line, 2)
                line
            }
            // Черная линия, выделенная -> сделать красной и снять выделение
            currentColor == LineColor.BLACK_0 && currentSelected == 2 -> {
                setLineColor(line, LineColor.RED_1)
                setLineSelected(line, 0)
                line
            }
            // Красная линия, не выделенная -> сделать синей
            currentColor == LineColor.RED_1 && currentSelected == 0 -> {
                setLineColor(line, LineColor.BLUE_2)
                line
            }
            // Синяя линия, не выделенная -> сделать черной
            currentColor == LineColor.BLUE_2 && currentSelected == 0 -> {
                setLineColor(line, LineColor.BLACK_0)
                line
            }
            // Другие случаи - оставить без изменений
            else -> line
        }
    }
    
    override fun reset() {
        selectedLine = null
        isProcessing = false
    }
    
    private fun findClosestLine(point: Point): LineSegment? {
        // Здесь должна быть логика поиска ближайшей линии
        // Пока возвращаем null
        return null
    }
    
    private fun getLineCenter(line: LineSegment): Point {
        val x = (line.determineAX() + line.determineBX()) / 2.0
        val y = (line.determineAY() + line.determineBY()) / 2.0
        return Point(x, y)
    }
    
    override fun getSelectionDistance(): Double {
        // Здесь должна быть логика получения расстояния выбора
        return 10.0
    }
    
    private fun removeLineFromSet(line: LineSegment) {
        // Здесь должна быть логика удаления линии из набора
        println("Линия удалена из набора")
    }
    
    private fun addLineToSet(line: LineSegment) {
        // Здесь должна быть логика добавления линии в набор
        println("Линия добавлена в набор")
    }
    
    private fun getLineColor(line: LineSegment): LineColor {
        // Здесь должна быть логика получения цвета линии
        // Пока возвращаем черный цвет
        return LineColor.BLACK_0
    }
    
    override fun setLineColor(line: LineSegment, color: LineColor): Unit {
        // Здесь должна быть логика установки цвета линии
        println("Установлен цвет линии: ${getColorName(color)}")
    }
    
    private fun getLineSelected(line: LineSegment): Int {
        // Здесь должна быть логика получения состояния выделения линии
        // Пока возвращаем 0
        return 0
    }
    
    private fun setLineSelected(line: LineSegment, selected: Int) {
        // Здесь должна быть логика установки состояния выделения линии
        println("Установлено состояние выделения: $selected")
    }
    
    private fun getColorName(color: LineColor): String {
        return when (color) {
            LineColor.BLACK_0 -> "Черный"
            LineColor.RED_1 -> "Красный (Гора)"
            LineColor.BLUE_2 -> "Синий (Долина)"
            else -> "Другой"
        }
    }
    
    private fun getLineTypeDescription(line: LineSegment): String {
        val color = getLineColor(line)
        val selected = getLineSelected(line)
        
        return when {
            color == LineColor.BLACK_0 && selected == 0 -> "Черная линия"
            color == LineColor.BLACK_0 && selected == 2 -> "Выделенная черная линия"
            color == LineColor.RED_1 -> "Красная линия (Гора)"
            color == LineColor.BLUE_2 -> "Синяя линия (Долина)"
            else -> "Неизвестный тип"
        }
    }
    
    fun getSelectedLine(): LineSegment? = selectedLine
    
    fun isProcessing(): Boolean = isProcessing
    
    fun getCurrentLineType(): String {
        return selectedLine?.let { getLineTypeDescription(it) } ?: "Нет выбранной линии"
    }
    
    override fun getName(): String = "Продвинутое изменение типа"
    
    override fun getDescription(): String = "Измените тип линии по продвинутому алгоритму"
} 