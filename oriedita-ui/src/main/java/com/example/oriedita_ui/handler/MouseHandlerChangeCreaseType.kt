package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.elements.Point


/**
 * Обработчик изменения типа линий сгиба
 * Адаптированная версия MouseHandlerChangeCreaseType для Android
 */
class MouseHandlerChangeCreaseType : BaseMouseHandler() {
    
    private var changedLines = mutableListOf<LineSegment>()
    private var newLineType: LineColor = LineColor.BLACK_0
    private var selectionDistance: Double = 10.0
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        changeClosestLineType(point)
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val currentPoint = offsetToPoint(offset)
        changeClosestLineType(currentPoint)
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        // Подтвердить изменения
        confirmChanges()
        return true
    }
    
    private fun changeClosestLineType(point: Point) {
        // Здесь должна быть логика поиска ближайшей линии и изменения её типа
        // Пока просто логируем
        println("Изменение типа линии около (${point.x}, ${point.y}) на ${getLineTypeName(newLineType)}")
    }
    
    private fun getLineTypeName(lineType: LineColor): String {
        return when (lineType) {
            LineColor.BLACK_0 -> "Черная (сгиб)"
            LineColor.RED_1 -> "Красная (гора)"
            LineColor.BLUE_2 -> "Синяя (долина)"
            LineColor.CYAN_3 -> "Голубая (вспомогательная)"
            LineColor.ORANGE_4 -> "Оранжевая"
            else -> "Неизвестный тип"
        }
    }
    
    private fun confirmChanges() {
        // Здесь должна быть логика подтверждения изменений
        println("Подтверждено изменение типа ${changedLines.size} линий")
    }
    
    fun setNewLineType(lineType: LineColor) {
        newLineType = lineType
    }
    
    fun getNewLineType(): LineColor = newLineType
    
    fun setSelectionDistance(distance: Double) {
        selectionDistance = distance
    }
    
    override fun getSelectionDistance(): Double = selectionDistance
    
    fun getChangedLines(): List<LineSegment> = changedLines.toList()
    
    fun clearChangedLines() {
        changedLines.clear()
    }
    
    fun getChangedCount(): Int = changedLines.size
    
    override fun getName(): String = "Изменение типа линий"
    
    override fun getDescription(): String = "Измените тип линий сгиба"
} 