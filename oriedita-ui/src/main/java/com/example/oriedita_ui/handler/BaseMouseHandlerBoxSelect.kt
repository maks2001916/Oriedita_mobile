package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor

/**
 * Базовый класс для обработчиков мыши, выполняющих выбор прямоугольной области
 * Адаптированная версия для Android
 */
abstract class BaseMouseHandlerBoxSelect : BaseMouseHandler() {
    
    protected var selectionStart = Point(0.0, 0.0)
    private var lines = Array(4) { LineSegment(Point(0.0, 0.0), Point(0.0, 0.0), LineColor.MAGENTA_5) }
    private var isSelecting = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        selectionStart = offsetToPoint(offset)
        isSelecting = true
        
        val point = offsetToPoint(offset)
        lines = Array(4) { LineSegment(point, point, LineColor.MAGENTA_5) }
        
        println("Начало выбора прямоугольной области: (${point.x}, ${point.y})")
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isSelecting) return false
        
        val currentPoint = offsetToPoint(offset)
        val startPoint = offsetToPoint(Offset(selectionStart.x.toFloat(), selectionStart.y.toFloat()))
        
        // Создать прямоугольник из четырех линий
        val p19_2 = Point(selectionStart.x, currentPoint.y)
        val p19_4 = Point(currentPoint.x, selectionStart.y)
        
        val p19_a = startPoint
        val p19_b = offsetToPoint(Offset(p19_2.x.toFloat(), p19_2.y.toFloat()))
        val p19_c = currentPoint
        val p19_d = offsetToPoint(Offset(p19_4.x.toFloat(), p19_4.y.toFloat()))
        
        lines[0] = LineSegment(p19_a, p19_b, LineColor.MAGENTA_5)
        lines[1] = LineSegment(p19_b, p19_c, LineColor.MAGENTA_5)
        lines[2] = LineSegment(p19_c, p19_d, LineColor.MAGENTA_5)
        lines[3] = LineSegment(p19_d, p19_a, LineColor.MAGENTA_5)
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isSelecting) return false
        
        val endPoint = offsetToPoint(offset)
        isSelecting = false
        
        // Очистить линии предварительного просмотра
        lines = Array(4) { LineSegment(Point(0.0, 0.0), Point(0.0, 0.0), LineColor.MAGENTA_5) }
        
        println("Завершение выбора прямоугольной области: (${endPoint.x}, ${endPoint.y})")
        return true
    }
    

    
    fun getSelectionLines(): Array<LineSegment> = lines.clone()
    
    fun isSelecting(): Boolean = isSelecting
    
    fun getSelectionRectangle(): Rectangle? {
        if (!isSelecting && lines.all { it.determineLength() > 0.0 }) {
            val minX = lines.minOf { minOf(it.determineAX(), it.determineBX()) }
            val maxX = lines.maxOf { maxOf(it.determineAX(), it.determineBX()) }
            val minY = lines.minOf { minOf(it.determineAY(), it.determineBY()) }
            val maxY = lines.maxOf { maxOf(it.determineAY(), it.determineBY()) }
            
            return Rectangle(
                topLeft = Point(minX, minY),
                bottomRight = Point(maxX, maxY)
            )
        }
        return null
    }
    
    fun getSelectionDescription(): String {
        return if (isSelecting) {
            "Выбор прямоугольной области активен"
        } else {
            "Выбор прямоугольной области завершен"
        }
    }
    
    fun getSelectionInfo(): String {
        return buildString {
            append("Начальная точка: (${selectionStart.x}, ${selectionStart.y})\n")
            append("Статус выбора: ${if (isSelecting) "активен" else "завершен"}\n")
            append("Количество линий: ${lines.size}")
        }
    }
}

data class Rectangle(
    val topLeft: Point,
    val bottomRight: Point
) {
    val width: Double get() = bottomRight.x - topLeft.x
    val height: Double get() = bottomRight.y - topLeft.y
    val center: Point get() = Point((topLeft.x + bottomRight.x) / 2, (topLeft.y + bottomRight.y) / 2)
    
    fun contains(point: Point): Boolean {
        return point.x >= topLeft.x && point.x <= bottomRight.x &&
               point.y >= topLeft.y && point.y <= bottomRight.y
    }
    
    fun intersects(other: Rectangle): Boolean {
        return !(other.bottomRight.x < topLeft.x || other.topLeft.x > bottomRight.x ||
                other.bottomRight.y < topLeft.y || other.topLeft.y > bottomRight.y)
    }
} 