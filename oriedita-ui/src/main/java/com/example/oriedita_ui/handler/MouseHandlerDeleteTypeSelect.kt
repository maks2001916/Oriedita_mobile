package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor

/**
 * Обработчик удаления линий определенного типа
 * Адаптированная версия для Android
 */
class MouseHandlerDeleteTypeSelect : BaseMouseHandler() {
    
    private var selectionStart = Point(0.0, 0.0)
    private var isProcessing = false
    private var deletedSegments = mutableListOf<LineSegment>()
    private var deleteType = CustomLineTypes.ANY
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        selectionStart = point
        println("Начало выбора: (${point.x}, ${point.y})")
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // Обработка перетаскивания для выбора области
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        clearLineStep()
        
        if (selectionStart.distance(point) > 0.000001) {
            // Выбор области
            handleAreaSelection(selectionStart, point)
        } else {
            // Выбор одной линии
            handleSingleLineSelection(point)
        }
        
        return true
    }
    
    private fun handleAreaSelection(start: Point, end: Point) {
        // Получить все линии в выбранной области
        val linesInArea = getLinesInArea(start, end)
        
        // Удалить линии в зависимости от типа
        for (line in linesInArea) {
            when (deleteType) {
                CustomLineTypes.ANY -> {
                    deleteLineFromFoldLineSet(line)
                    deletedSegments.add(line)
                }
                CustomLineTypes.EDGE -> {
                    if (line.color == LineColor.BLACK_0) {
                        deleteLineFromFoldLineSet(line)
                        deletedSegments.add(line)
                    }
                }
                CustomLineTypes.MANDV -> {
                    if (line.color == LineColor.RED_1 || line.color == LineColor.BLUE_2) {
                        deleteLineFromFoldLineSet(line)
                        deletedSegments.add(line)
                    }
                }
                CustomLineTypes.MOUNTAIN -> {
                    if (line.color == LineColor.fromNumber(deleteType.number - 1)) {
                        deleteLineFromFoldLineSet(line)
                        deletedSegments.add(line)
                    }
                }
                CustomLineTypes.VALLEY -> {
                    if (line.color == LineColor.fromNumber(deleteType.number - 1)) {
                        deleteLineFromFoldLineSet(line)
                        deletedSegments.add(line)
                    }
                }
                CustomLineTypes.AUX -> {
                    if (line.color == LineColor.fromNumber(deleteType.number - 1)) {
                        deleteLineFromFoldLineSet(line)
                        deletedSegments.add(line)
                    }
                }
            }
        }
        
        println("Удалены ${deletedSegments.size} линий в выбранной области")
    }
    
    private fun getLinesInArea(start: Point, end: Point): List<LineSegment> {
        val linesInArea = mutableListOf<LineSegment>()
        
        // Получить все линии из FoldLineSet
        val allLines = getFoldLineSet()?.getLineSegmentsIterable()
        
        if (allLines != null) {
            for (line in allLines) {
                // Проверить, находится ли линия в выбранной области
                if (isLineInArea(line, start, end)) {
                    linesInArea.add(line)
                }
            }
        }
        
        return linesInArea
    }
    
    private fun isLineInArea(line: LineSegment, start: Point, end: Point): Boolean {
        // Простая проверка: если хотя бы одна точка линии находится в области
        val lineStart = line.getA()
        val lineEnd = line.getB()
        
        return (lineStart.x >= minOf(start.x, end.x) && lineStart.x <= maxOf(start.x, end.x) &&
                lineStart.y >= minOf(start.y, end.y) && lineStart.y <= maxOf(start.y, end.y)) ||
               (lineEnd.x >= minOf(start.x, end.x) && lineEnd.x <= maxOf(start.x, end.x) &&
                lineEnd.y >= minOf(start.y, end.y) && lineEnd.y <= maxOf(start.y, end.y))
    }
    
    private fun handleSingleLineSelection(point: Point) {
        val closestSegment = getClosestLineSegment(point)
        
        if (closestSegment != null) {
            when (deleteType) {
                CustomLineTypes.ANY -> {
                    deleteLineFromFoldLineSet(closestSegment)
                    deletedSegments.add(closestSegment)
                    println("Удалена линия любого типа")
                }
                CustomLineTypes.EDGE -> {
                    if (closestSegment.color == LineColor.BLACK_0) {
                        deleteLineFromFoldLineSet(closestSegment)
                        deletedSegments.add(closestSegment)
                        println("Удалена краевая линия")
                    }
                }
                CustomLineTypes.MANDV -> {
                    if (closestSegment.color == LineColor.RED_1 || closestSegment.color == LineColor.BLUE_2) {
                        deleteLineFromFoldLineSet(closestSegment)
                        deletedSegments.add(closestSegment)
                        println("Удалена линия сгиба (гора/долина)")
                    }
                }
                CustomLineTypes.MOUNTAIN -> {
                    if (closestSegment.color == LineColor.fromNumber(deleteType.number - 1)) {
                        deleteLineFromFoldLineSet(closestSegment)
                        deletedSegments.add(closestSegment)
                        println("Удалена горная линия")
                    }
                }
                CustomLineTypes.VALLEY -> {
                    if (closestSegment.color == LineColor.fromNumber(deleteType.number - 1)) {
                        deleteLineFromFoldLineSet(closestSegment)
                        deletedSegments.add(closestSegment)
                        println("Удалена долинная линия")
                    }
                }
                CustomLineTypes.AUX -> {
                    if (closestSegment.color == LineColor.fromNumber(deleteType.number - 1)) {
                        deleteLineFromFoldLineSet(closestSegment)
                        deletedSegments.add(closestSegment)
                        println("Удалена вспомогательная линия")
                    }
                }
            }
        }
    }
    
    fun setDeleteType(type: CustomLineTypes) {
        deleteType = type
        println("Установлен тип удаления: $type")
    }
    
    fun getDeleteType(): CustomLineTypes = deleteType
    
    fun getSelectionStart(): Point = selectionStart
    
    fun getDeletedSegments(): List<LineSegment> = deletedSegments.toList()
    
    fun getDeletedSegmentsCount(): Int = deletedSegments.size
    
    fun isProcessing(): Boolean = isProcessing
    
    fun clearDeletedSegments() {
        deletedSegments.clear()
    }
    
    fun getDeleteTypeDescription(): String {
        return when (deleteType) {
            CustomLineTypes.ANY -> "Удаление линий любого типа"
            CustomLineTypes.EDGE -> "Удаление только краевых линий"
            CustomLineTypes.MANDV -> "Удаление линий сгиба (гора/долина)"
            CustomLineTypes.MOUNTAIN -> "Удаление только горных линий"
            CustomLineTypes.VALLEY -> "Удаление только долинных линий"
            CustomLineTypes.AUX -> "Удаление только вспомогательных линий"
        }
    }
    
    fun getDeleteTypeInfo(): String {
        return buildString {
            append("Тип удаления: $deleteType\n")
            append("Начальная точка: (${selectionStart.x}, ${selectionStart.y})\n")
            append("Удаленные сегменты: ${deletedSegments.size}")
        }
    }
    
    private fun deleteLineFromFoldLineSet(line: LineSegment) {
        // Удалить линию из FoldLineSet через creasePatternWorker
        val foldLineSet = getFoldLineSet()
        if (foldLineSet != null) {
            try {
                foldLineSet.deleteLine(line)
                println("Линия успешно удалена из FoldLineSet")
            } catch (e: Exception) {
                println("Ошибка при удалении линии: ${e.message}")
            }
        } else {
            println("FoldLineSet недоступен для удаления линии")
        }
    }
    
    override fun getName(): String = "Удаление линий по типу"
    
    override fun getDescription(): String = "Удаляйте линии определенного типа"
}

enum class CustomLineTypes(val number: Int) {
    ANY(0),
    EDGE(1),
    MANDV(2),
    MOUNTAIN(3),
    VALLEY(4),
    AUX(5)
} 