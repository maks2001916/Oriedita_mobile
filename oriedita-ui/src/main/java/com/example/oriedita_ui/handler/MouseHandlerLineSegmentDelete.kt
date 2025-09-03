package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик удаления сегментов линий
 * Адаптированная версия MouseHandlerLineSegmentDelete для Android
 */
class MouseHandlerLineSegmentDelete : BaseMouseHandler() {
    
    enum class RemovalMode {
        POLY_LINE,      // Удаление полилиний
        BLACK_LINE,     // Удаление черных линий
        AUX_LIVE_LINE,  // Удаление вспомогательных живых линий
        AUX_LINE,       // Удаление вспомогательных линий
        BOTH            // Удаление всех типов
    }
    
    private var startPoint: Point? = null
    private var endPoint: Point? = null
    private var isSelecting = false
    private var removalMode: RemovalMode = RemovalMode.POLY_LINE
    private var deletedLines = mutableListOf<LineSegment>()
    private var deletedCircles = mutableListOf<Any>() // Заглушка для кругов
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        isSelecting = true
        
        println("Начало выбора для удаления в точке: (${point.x}, ${point.y})")
        
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
            // Одиночный клик - удалить ближайший элемент
            performSingleDelete(releasePoint)
        } else {
            // Прямоугольное выделение - удалить элементы в прямоугольнике
            performRectangleDelete(start, releasePoint)
        }
        
        // Сбросить состояние
        startPoint = null
        endPoint = null
        isSelecting = false
        
        return true
    }
    
    private fun performSingleDelete(point: Point) {
        val removalMode = determineRemovalMode(point)
        
        when (removalMode) {
            RemovalMode.POLY_LINE -> deletePolyLine(point)
            RemovalMode.BLACK_LINE -> deleteBlackLine(point)
            RemovalMode.AUX_LIVE_LINE -> deleteAuxLiveLine(point)
            RemovalMode.AUX_LINE -> deleteAuxLine(point)
            RemovalMode.BOTH -> deleteBoth(point)
        }
    }
    
    private fun performRectangleDelete(start: Point, end: Point) {
        when (removalMode) {
            RemovalMode.POLY_LINE, RemovalMode.BOTH -> {
                deletePolyLinesInRectangle(start, end)
            }
            RemovalMode.BLACK_LINE -> {
                deleteBlackLinesInRectangle(start, end)
            }
            RemovalMode.AUX_LIVE_LINE, RemovalMode.BOTH -> {
                deleteAuxLiveLinesInRectangle(start, end)
            }
            RemovalMode.AUX_LINE, RemovalMode.BOTH -> {
                deleteAuxLinesInRectangle(start, end)
            }
        }
    }
    
    private fun determineRemovalMode(point: Point): RemovalMode {
        return when (removalMode) {
            RemovalMode.BOTH -> {
                // Определить тип ближайшего элемента
                val lineDistance = getClosestLineDistance(point)
                val circleDistance = getClosestCircleDistance(point)
                val auxLineDistance = getClosestAuxLineDistance(point)
                
                when {
                    lineDistance <= circleDistance && lineDistance <= auxLineDistance -> {
                        val closestLine = findClosestLine(point)
                        if (closestLine != null && closestLine.color.number < 3) {
                            RemovalMode.POLY_LINE
                        } else {
                            RemovalMode.AUX_LIVE_LINE
                        }
                    }
                    circleDistance < lineDistance && circleDistance <= auxLineDistance -> {
                        RemovalMode.AUX_LIVE_LINE
                    }
                    auxLineDistance < lineDistance && auxLineDistance < circleDistance -> {
                        RemovalMode.AUX_LINE
                    }
                    else -> RemovalMode.POLY_LINE
                }
            }
            else -> removalMode
        }
    }
    
    private fun deletePolyLine(point: Point) {
        val closestLine = findClosestLine(point)
        if (closestLine != null && point.distance(getLineCenter(closestLine)) < getSelectionDistance()) {
            if (closestLine.color.number < 3) {
                deleteLineSegment(closestLine)
                println("Удалена полилиния: (${closestLine.determineAX()}, ${closestLine.determineAY()}) -> (${closestLine.determineBX()}, ${closestLine.determineBY()})")
            }
        }
    }
    
    private fun deleteBlackLine(point: Point) {
        val closestLine = findClosestLine(point)
        if (closestLine != null && point.distance(getLineCenter(closestLine)) < getSelectionDistance()) {
            if (closestLine.color == LineColor.BLACK_0) {
                deleteLineSegment(closestLine)
                println("Удалена черная линия: (${closestLine.determineAX()}, ${closestLine.determineAY()}) -> (${closestLine.determineBX()}, ${closestLine.determineBY()})")
            }
        }
    }
    
    private fun deleteAuxLiveLine(point: Point) {
        val lineDistance = getClosestLineDistance(point)
        val circleDistance = getClosestCircleDistance(point)
        
        if (lineDistance <= circleDistance) {
            val closestLine = findClosestLine(point)
            if (closestLine != null && point.distance(getLineCenter(closestLine)) < getSelectionDistance()) {
                if (closestLine.color == LineColor.CYAN_3) {
                    deleteLineSegment(closestLine)
                    println("Удалена вспомогательная живая линия: (${closestLine.determineAX()}, ${closestLine.determineAY()}) -> (${closestLine.determineBX()}, ${closestLine.determineBY()})")
                }
            }
        } else {
            val closestCircle = findClosestCircle(point)
            if (closestCircle != null && point.distance(getCircleCenter(closestCircle)) < getSelectionDistance()) {
                deleteCircle(closestCircle)
                println("Удален круг")
            }
        }
    }
    
    private fun deleteAuxLine(point: Point) {
        val closestAuxLine = findClosestAuxLine(point)
        if (closestAuxLine != null && point.distance(getLineCenter(closestAuxLine)) < getSelectionDistance()) {
            deleteAuxLineSegment(closestAuxLine)
            println("Удалена вспомогательная линия: (${closestAuxLine.determineAX()}, ${closestAuxLine.determineAY()}) -> (${closestAuxLine.determineBX()}, ${closestAuxLine.determineBY()})")
        }
    }
    
    private fun deleteBoth(point: Point) {
        // Определить тип и удалить соответствующий элемент
        val removalMode = determineRemovalMode(point)
        when (removalMode) {
            RemovalMode.POLY_LINE -> deletePolyLine(point)
            RemovalMode.AUX_LIVE_LINE -> deleteAuxLiveLine(point)
            RemovalMode.AUX_LINE -> deleteAuxLine(point)
            else -> deletePolyLine(point)
        }
    }
    
    private fun deletePolyLinesInRectangle(start: Point, end: Point) {
        val linesInBox = findLinesInBox(start, end)
        var deletedCount = 0
        
        for (line in linesInBox) {
            if (line.color.number < 3) {
                deleteLineSegment(line)
                deletedLines.add(line)
                deletedCount++
            }
        }
        
        println("Удалено полилиний в прямоугольнике: $deletedCount")
    }
    
    private fun deleteBlackLinesInRectangle(start: Point, end: Point) {
        val linesInBox = findLinesInBox(start, end)
        var deletedCount = 0
        
        for (line in linesInBox) {
            if (line.color == LineColor.BLACK_0) {
                deleteLineSegment(line)
                deletedLines.add(line)
                deletedCount++
            }
        }
        
        println("Удалено черных линий в прямоугольнике: $deletedCount")
    }
    
    private fun deleteAuxLiveLinesInRectangle(start: Point, end: Point) {
        val linesInBox = findLinesInBox(start, end)
        var deletedCount = 0
        
        for (line in linesInBox) {
            if (line.color == LineColor.CYAN_3) {
                deleteLineSegment(line)
                deletedLines.add(line)
                deletedCount++
            }
        }
        
        println("Удалено вспомогательных живых линий в прямоугольнике: $deletedCount")
    }
    
    private fun deleteAuxLinesInRectangle(start: Point, end: Point) {
        val auxLinesInBox = findAuxLinesInBox(start, end)
        var deletedCount = 0
        
        for (line in auxLinesInBox) {
            deleteAuxLineSegment(line)
            deletedLines.add(line)
            deletedCount++
        }
        
        println("Удалено вспомогательных линий в прямоугольнике: $deletedCount")
    }
    
    // Вспомогательные методы (заглушки)
    private fun getClosestLineDistance(point: Point): Double {
        // Здесь должна быть логика получения расстояния до ближайшей линии
        return 100.0
    }
    
    private fun getClosestCircleDistance(point: Point): Double {
        // Здесь должна быть логика получения расстояния до ближайшего круга
        return 100.0
    }
    
    private fun getClosestAuxLineDistance(point: Point): Double {
        // Здесь должна быть логика получения расстояния до ближайшей вспомогательной линии
        return 100.0
    }
    
    private fun findClosestLine(point: Point): LineSegment? {
        // Здесь должна быть логика поиска ближайшей линии
        return null
    }
    
    private fun findClosestCircle(point: Point): Any? {
        // Здесь должна быть логика поиска ближайшего круга
        return null
    }
    
    private fun findClosestAuxLine(point: Point): LineSegment? {
        // Здесь должна быть логика поиска ближайшей вспомогательной линии
        return null
    }
    
    private fun getLineCenter(line: LineSegment): Point {
        val centerX = (line.determineAX() + line.determineBX()) / 2.0
        val centerY = (line.determineAY() + line.determineBY()) / 2.0
        return Point(centerX, centerY)
    }
    
    private fun getCircleCenter(circle: Any): Point {
        // Здесь должна быть логика получения центра круга
        return Point(0.0, 0.0)
    }
    
    override fun getSelectionDistance(): Double {
        // Здесь должна быть логика получения расстояния выбора
        return 10.0
    }
    
    private fun deleteLineSegment(line: LineSegment) {
        // Здесь должна быть логика удаления сегмента линии
        println("Удален сегмент линии")
    }
    
    private fun deleteAuxLineSegment(line: LineSegment) {
        // Здесь должна быть логика удаления вспомогательного сегмента линии
        println("Удален вспомогательный сегмент линии")
    }
    
    private fun deleteCircle(circle: Any) {
        // Здесь должна быть логика удаления круга
        deletedCircles.add(circle)
        println("Удален круг")
    }
    
    private fun findLinesInBox(start: Point, end: Point): List<LineSegment> {
        // Здесь должна быть логика поиска линий в прямоугольнике
        return emptyList()
    }
    
    private fun findAuxLinesInBox(start: Point, end: Point): List<LineSegment> {
        // Здесь должна быть логика поиска вспомогательных линий в прямоугольнике
        return emptyList()
    }
    
    fun setRemovalMode(mode: RemovalMode) {
        removalMode = mode
        println("Режим удаления изменен на: ${getRemovalModeName()}")
    }
    
    fun getRemovalMode(): RemovalMode = removalMode
    
    fun getRemovalModeName(): String {
        return when (removalMode) {
            RemovalMode.POLY_LINE -> "Полилинии"
            RemovalMode.BLACK_LINE -> "Черные линии"
            RemovalMode.AUX_LIVE_LINE -> "Вспомогательные живые линии"
            RemovalMode.AUX_LINE -> "Вспомогательные линии"
            RemovalMode.BOTH -> "Все типы"
        }
    }
    
    fun getDeletedLines(): List<LineSegment> = deletedLines.toList()
    
    fun getDeletedCircles(): List<Any> = deletedCircles.toList()
    
    fun getDeletedCount(): Int = deletedLines.size + deletedCircles.size
    
    fun clearDeletedItems() {
        deletedLines.clear()
        deletedCircles.clear()
    }
    
    override fun getName(): String = "Удаление сегментов линий"
    
    override fun getDescription(): String = "Удалите сегменты линий различных типов"
} 