package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Circle
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor

/**
 * Обработчик рисования окружности по трем точкам
 * Адаптированная версия MouseHandlerCircleDrawThreePoint для Android
 */
class MouseHandlerCircleDrawThreePoint : BaseMouseHandler() {
    
    private var points = mutableListOf<Point>()
    private var currentCircle: Circle? = null
    private var isDrawing = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        if (points.size < 3) {
            points.add(point)
            isDrawing = true
            
            if (points.size == 3) {
                // Создать окружность по трем точкам
                createCircleFromThreePoints()
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
            showPreviewCircle(tempPoints)
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (points.size >= 3) {
            // Сбросить состояние после создания окружности
            points.clear()
            currentCircle = null
            isDrawing = false
        }
        
        return true
    }
    
    private fun createCircleFromThreePoints() {
        if (points.size != 3) return
        
        val p1 = points[0]
        val p2 = points[1]
        val p3 = points[2]
        
        // Вычислить центр и радиус окружности по трем точкам
        val center = calculateCircleCenter(p1, p2, p3)
        val radius = center.distance(p1)
        
        currentCircle = Circle(center, radius, LineColor.BLACK_0)
        addCircleToCanvas(currentCircle!!)
        
        println("Создана окружность по трем точкам: центр(${center.x}, ${center.y}), радиус=$radius")
    }
    
    private fun calculateCircleCenter(p1: Point, p2: Point, p3: Point): Point {
        // Формула для вычисления центра окружности по трем точкам
        val x1 = p1.x
        val y1 = p1.y
        val x2 = p2.x
        val y2 = p2.y
        val x3 = p3.x
        val y3 = p3.y
        
        val d = 2 * (x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2))
        
        if (d == 0.0) {
            // Точки коллинеарны, возвращаем среднюю точку
            return Point((x1 + x2 + x3) / 3, (y1 + y2 + y3) / 3)
        }
        
        val ux = ((x1 * x1 + y1 * y1) * (y2 - y3) + (x2 * x2 + y2 * y2) * (y3 - y1) + (x3 * x3 + y3 * y3) * (y1 - y2)) / d
        val uy = ((x1 * x1 + y1 * y1) * (x3 - x2) + (x2 * x2 + y2 * y2) * (x1 - x3) + (x3 * x3 + y3 * y3) * (x2 - x1)) / d
        
        return Point(ux, uy)
    }
    
    private fun showPreviewCircle(tempPoints: List<Point>) {
        if (tempPoints.size == 3) {
            val center = calculateCircleCenter(tempPoints[0], tempPoints[1], tempPoints[2])
            val radius = center.distance(tempPoints[0])
            currentCircle = Circle(center, radius, LineColor.BLACK_0)
        }
    }
    
    private fun addCircleToCanvas(circle: Circle) {
        // Здесь должна быть логика добавления окружности на холст
        println("Добавлена окружность по трем точкам: центр(${circle.determineCenter().x}, ${circle.determineCenter().y}), радиус=${circle.getR()}")
    }
    
    fun getCurrentCircle(): Circle? = currentCircle
    
    fun getPoints(): List<Point> = points.toList()
    
    fun getPointCount(): Int = points.size
    
    fun isDrawing(): Boolean = isDrawing
    
    override fun getName(): String = "Окружность по трем точкам"
    
    override fun getDescription(): String = "Нарисуйте окружность по трем точкам"
} 