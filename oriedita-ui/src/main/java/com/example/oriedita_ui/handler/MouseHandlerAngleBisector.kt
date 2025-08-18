package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Обработчик биссектрисы угла
 * Адаптированная версия MouseHandlerAngleBisector для Android
 */
class MouseHandlerAngleBisector : BaseMouseHandler() {
    
    private var points = mutableListOf<Point>()
    private var currentBisector: LineSegment? = null
    private var isDrawing = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        if (points.size < 3) {
            points.add(point)
            isDrawing = true
            
            if (points.size == 3) {
                // Создать биссектрису угла
                createAngleBisector()
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing || points.size >= 3) return false
        
        val currentPoint = offsetToPoint(offset)
        
        // Показать предварительный просмотр
        if (points.size == 2) {
            val tempPoints = points.toMutableList()
            tempPoints.add(currentPoint)
            showPreviewBisector(tempPoints)
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (points.size >= 3) {
            // Сбросить состояние после создания биссектрисы
            points.clear()
            currentBisector = null
            isDrawing = false
        }
        
        return true
    }
    
    private fun createAngleBisector() {
        if (points.size != 3) return
        
        val vertex = points[1] // Вершина угла
        val p1 = points[0]     // Первая точка
        val p2 = points[2]     // Вторая точка
        
        val bisector = calculateAngleBisector(vertex, p1, p2)
        currentBisector = bisector
        addLineToCanvas(bisector)
        
        println("Создана биссектриса угла: (${bisector.a.x}, ${bisector.a.y}) -> (${bisector.b.x}, ${bisector.b.y})")
    }
    
    private fun calculateAngleBisector(vertex: Point, p1: Point, p2: Point): LineSegment {
        // Вычислить векторы от вершины к точкам
        val v1x = p1.x - vertex.x
        val v1y = p1.y - vertex.y
        val v2x = p2.x - vertex.x
        val v2y = p2.y - vertex.y
        
        // Вычислить длины векторов
        val len1 = sqrt(v1x * v1x + v1y * v1y)
        val len2 = sqrt(v2x * v2x + v2y * v2y)
        
        if (len1 == 0.0 || len2 == 0.0) {
            // Если один из векторов нулевой, вернуть линию от вершины
            return LineSegment(vertex, Point(vertex.x + 100, vertex.y))
        }
        
        // Нормализовать векторы
        val n1x = v1x / len1
        val n1y = v1y / len1
        val n2x = v2x / len2
        val n2y = v2y / len2
        
        // Вычислить биссектрису (сумма нормализованных векторов)
        val bisectorX = n1x + n2x
        val bisectorY = n1y + n2y
        
        // Нормализовать биссектрису
        val bisectorLen = sqrt(bisectorX * bisectorX + bisectorY * bisectorY)
        if (bisectorLen == 0.0) {
            // Если биссектриса нулевая, создать перпендикуляр
            val perpX = -n1y
            val perpY = n1x
            return LineSegment(vertex, Point(vertex.x + perpX * 100, vertex.y + perpY * 100))
        }
        
        val normalizedBisectorX = bisectorX / bisectorLen
        val normalizedBisectorY = bisectorY / bisectorLen
        
        // Создать линию биссектрисы
        val endPoint = Point(vertex.x + normalizedBisectorX * 100, vertex.y + normalizedBisectorY * 100)
        return LineSegment(vertex, endPoint)
    }
    
    private fun showPreviewBisector(tempPoints: List<Point>) {
        if (tempPoints.size == 3) {
            val vertex = tempPoints[1]
            val p1 = tempPoints[0]
            val p2 = tempPoints[2]
            currentBisector = calculateAngleBisector(vertex, p1, p2)
        }
    }
    
    private fun addLineToCanvas(line: LineSegment) {
        // Здесь должна быть логика добавления линии на холст
        println("Добавлена биссектриса: (${line.a.x}, ${line.a.y}) -> (${line.b.x}, ${line.b.y})")
    }
    
    fun getCurrentBisector(): LineSegment? = currentBisector
    
    fun getPoints(): List<Point> = points.toList()
    
    fun getPointCount(): Int = points.size
    
    fun isDrawing(): Boolean = isDrawing
    
    override fun getName(): String = "Биссектриса угла"
    
    override fun getDescription(): String = "Нарисуйте биссектрису угла по трем точкам"
} 