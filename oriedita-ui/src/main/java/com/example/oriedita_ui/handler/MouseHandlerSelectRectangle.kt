package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик прямоугольного выделения
 * Адаптированная версия MouseHandlerSelectRectangle для Android
 */
class MouseHandlerSelectRectangle : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var currentRectangle: Rectangle? = null
    private var isSelecting = false
    private var selectedElements = mutableListOf<Any>()
    private var selectionMode: SelectionMode = SelectionMode.ADD
    
    data class Rectangle(
        val x: Double,
        val y: Double,
        val width: Double,
        val height: Double
    ) {
        fun contains(point: Point): Boolean {
            return point.x >= x && point.x <= x + width &&
                   point.y >= y && point.y <= y + height
        }
        
        fun intersects(line: LineSegment): Boolean {
            // Простая проверка пересечения линии с прямоугольником
            val lineStart = line.a
            val lineEnd = line.b
            
            // Проверить, находятся ли концы линии внутри прямоугольника
            if (contains(lineStart) || contains(lineEnd)) return true
            
            // Проверить пересечение с каждой стороной прямоугольника
            val sides = listOf(
                LineSegment(Point(x, y), Point(x + width, y)), // Верхняя сторона
                LineSegment(Point(x + width, y), Point(x + width, y + height)), // Правая сторона
                LineSegment(Point(x + width, y + height), Point(x, y + height)), // Нижняя сторона
                LineSegment(Point(x, y + height), Point(x, y)) // Левая сторона
            )
            
            return sides.any { side -> linesIntersect(line, side) }
        }
        
        private fun linesIntersect(line1: LineSegment, line2: LineSegment): Boolean {
            // Простая проверка пересечения двух линий
            // Здесь должна быть более сложная логика
            return false
        }
    }
    
    enum class SelectionMode {
        ADD,      // Добавить к выделению
        REPLACE,  // Заменить выделение
        SUBTRACT  // Вычесть из выделения
    }
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        isSelecting = true
        
        println("Начало прямоугольного выделения в точке: (${point.x}, ${point.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isSelecting) return false
        
        val currentPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать прямоугольник выделения
        val x = minOf(start.x, currentPoint.x)
        val y = minOf(start.y, currentPoint.y)
        val width = kotlin.math.abs(currentPoint.x - start.x)
        val height = kotlin.math.abs(currentPoint.y - start.y)
        
        currentRectangle = Rectangle(x, y, width, height)
        
        println("Прямоугольное выделение: (${x}, ${y}) размером ${width}x${height}")
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isSelecting) return false
        
        val endPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        // Создать финальный прямоугольник выделения
        val x = minOf(start.x, endPoint.x)
        val y = minOf(start.y, endPoint.y)
        val width = kotlin.math.abs(endPoint.x - start.x)
        val height = kotlin.math.abs(endPoint.y - start.y)
        
        val finalRectangle = Rectangle(x, y, width, height)
        currentRectangle = finalRectangle
        
        // Выполнить выделение
        performSelection(finalRectangle)
        
        // Сбросить состояние
        startPoint = null
        currentRectangle = null
        isSelecting = false
        
        return true
    }
    
    private fun performSelection(rectangle: Rectangle) {
        // Здесь должна быть логика поиска элементов в прямоугольнике
        println("Выполнено прямоугольное выделение: ${rectangle.width}x${rectangle.height}")
        
        // Найти элементы в прямоугольнике
        val elementsInRectangle = findElementsInRectangle(rectangle)
        
        when (selectionMode) {
            SelectionMode.ADD -> {
                selectedElements.addAll(elementsInRectangle)
                println("Добавлено к выделению: ${elementsInRectangle.size} элементов")
            }
            SelectionMode.REPLACE -> {
                selectedElements.clear()
                selectedElements.addAll(elementsInRectangle)
                println("Заменено выделение: ${elementsInRectangle.size} элементов")
            }
            SelectionMode.SUBTRACT -> {
                selectedElements.removeAll(elementsInRectangle.toSet())
                println("Вычтено из выделения: ${elementsInRectangle.size} элементов")
            }
        }
    }
    
    private fun findElementsInRectangle(rectangle: Rectangle): List<Any> {
        // Здесь должна быть логика поиска элементов в прямоугольнике
        // Пока возвращаем пустой список
        return emptyList()
    }
    

    
    fun setSelectionMode(mode: SelectionMode) {
        selectionMode = mode
        println("Режим выделения изменен на: ${getSelectionModeName()}")
    }
    
    fun getSelectionMode(): SelectionMode = selectionMode
    
    fun getSelectionModeName(): String {
        return when (selectionMode) {
            SelectionMode.ADD -> "Добавить"
            SelectionMode.REPLACE -> "Заменить"
            SelectionMode.SUBTRACT -> "Вычесть"
        }
    }
    
    fun getCurrentRectangle(): Rectangle? = currentRectangle
    
    fun getStartPoint(): Point? = startPoint
    
    fun isSelecting(): Boolean = isSelecting
    
    fun getSelectedElements(): List<Any> = selectedElements.toList()
    
    fun clearSelection() {
        selectedElements.clear()
        println("Выделение очищено")
    }
    
    fun getSelectionCount(): Int = selectedElements.size
    
    fun invertSelection() {
        // Здесь должна быть логика инвертирования выделения
        println("Выделение инвертировано")
    }
    
    override fun getName(): String = "Прямоугольное выделение"
    
    override fun getDescription(): String = "Выделите элементы прямоугольником"
} 