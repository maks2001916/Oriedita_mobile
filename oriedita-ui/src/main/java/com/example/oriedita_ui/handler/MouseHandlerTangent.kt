package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.Circle
import com.example.oriedita_core.origami.crease_pattern.OritaCalc
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import kotlin.math.sqrt
import kotlin.math.pow

/**
 * Обработчик касательных к окружности
 * Адаптированная версия MouseHandlerTangent для Android
 */
class MouseHandlerTangent : BaseMouseHandler() {
    
    private var circle: Circle? = null
    private var externalPoint: Point? = null
    private var currentTangent: LineSegment? = null
    private var isDrawing = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        if (circle == null) {
            // Первый клик - выбрать окружность
            circle = findClosestCircle(point)
            isDrawing = true
        } else {
            // Второй клик - создать касательную
            createTangent(point)
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing) return false
        
        val currentPoint = offsetToPoint(offset)
        
        if (circle == null) {
            // Показать предварительный просмотр выбора окружности
            val previewCircle = findClosestCircle(currentPoint)
            // Здесь можно показать подсветку окружности
        } else {
            // Показать предварительный просмотр касательной
            externalPoint = currentPoint
            val tangentPoints = calculateTangentPoints(currentPoint, circle!!)
            if (tangentPoints.isNotEmpty()) {
                currentTangent = LineSegment(currentPoint, tangentPoints.first())
            }
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing) return false
        
        val endPoint = offsetToPoint(offset)
        
        if (circle == null) {
            // Создать окружность (если не найдена существующая)
            circle = Circle(endPoint, 50.0, LineColor.BLACK_0) // Радиус по умолчанию
            println("Создана окружность: центр(${endPoint.x}, ${endPoint.y}), радиус=50.0")
        } else {
            // Создать касательную
            createTangent(endPoint)
        }
        
        return true
    }
    
    private fun createTangent(point: Point) {
        circle?.let { c ->
            externalPoint = point
            val tangentPoints = calculateTangentPoints(point, c)
            
            if (tangentPoints.isNotEmpty()) {
                val tangent = LineSegment(point, tangentPoints.first())
                currentTangent = tangent
                addLineToCanvas(tangent)
                
                println("Создана касательная: (${point.x}, ${point.y}) -> (${tangentPoints.first().x}, ${tangentPoints.first().y})")
                
                // Если есть вторая точка касания, создать вторую касательную
                if (tangentPoints.size > 1) {
                    val secondTangent = LineSegment(point, tangentPoints[1])
                    addLineToCanvas(secondTangent)
                    println("Создана вторая касательная: (${point.x}, ${point.y}) -> (${tangentPoints[1].x}, ${tangentPoints[1].y})")
                }
            }
            
            // Сбросить состояние
            circle = null
            externalPoint = null
            isDrawing = false
        }
    }
    
    private fun findClosestCircle(point: Point): Circle? {
        // Здесь должна быть логика поиска ближайшей окружности
        // Пока возвращаем null для создания новой окружности
        return null
    }
    
    private fun calculateTangentPoints(externalPoint: Point, circle: Circle): List<Point> {
        val cx = circle.x
        val cy = circle.y
        val r = circle.r
        val px = externalPoint.x
        val py = externalPoint.y
        
        // Расстояние от внешней точки до центра окружности
        val distance = sqrt((px - cx).pow(2) + (py - cy).pow(2))
        
        if (distance <= r) {
            // Точка внутри или на окружности - нет касательных
            return emptyList()
        }
        
        // Вычислить точки касания
        val d = distance
        val l = sqrt(d.pow(2) - r.pow(2)) // Длина касательной
        
        // Угол между линией от центра к внешней точке и касательной
        val angle = kotlin.math.acos(r / d)
        
        // Угол от центра к внешней точке
        val baseAngle = kotlin.math.atan2(py - cy, px - cx)
        
        // Два угла для точек касания
        val angle1 = baseAngle + angle
        val angle2 = baseAngle - angle
        
        // Вычислить точки касания
        val tangent1 = Point(
            cx + r * kotlin.math.cos(angle1),
            cy + r * kotlin.math.sin(angle1)
        )
        val tangent2 = Point(
            cx + r * kotlin.math.cos(angle2),
            cy + r * kotlin.math.sin(angle2)
        )
        
        return listOf(tangent1, tangent2)
    }
    
    private fun addLineToCanvas(line: LineSegment) {
        // Здесь должна быть логика добавления линии на холст
        println("Добавлена касательная: (${line.a.x}, ${line.a.y}) -> (${line.b.x}, ${line.b.y})")
    }
    
    fun getCircle(): Circle? = circle
    
    fun getCurrentTangent(): LineSegment? = currentTangent
    
    fun getExternalPoint(): Point? = externalPoint
    
    fun isDrawing(): Boolean = isDrawing
    
    override fun getName(): String = "Касательная к окружности"
    
    override fun getDescription(): String = "Нарисуйте касательную к окружности"
} 