package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.Circle
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc
import kotlin.math.sqrt
import kotlin.math.abs

/**
 * Обработчик вписанной окружности
 * Адаптированная версия MouseHandlerCircleInscribed для Android
 */
class MouseHandlerCircleInscribed : BaseMouseHandler() {
    
    private var trianglePoints = mutableListOf<Point>()
    private var currentCircle: Circle? = null
    private var isDrawing = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        if (trianglePoints.size < 3) {
            trianglePoints.add(point)
            isDrawing = true
            
            if (trianglePoints.size == 3) {
                // Создать вписанную окружность
                createInscribedCircle()
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing || trianglePoints.size >= 3) return false
        
        val currentPoint = offsetToPoint(offset)
        
        // Показать предварительный просмотр
        if (trianglePoints.size == 2) {
            val tempPoints = trianglePoints.toMutableList()
            tempPoints.add(currentPoint)
            showPreviewInscribedCircle(tempPoints)
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (trianglePoints.size >= 3) {
            // Сбросить состояние после создания вписанной окружности
            trianglePoints.clear()
            currentCircle = null
            isDrawing = false
        }
        
        return true
    }
    
    private fun createInscribedCircle() {
        if (trianglePoints.size != 3) return
        
        val p1 = trianglePoints[0]
        val p2 = trianglePoints[1]
        val p3 = trianglePoints[2]
        
        val inscribedCircle = calculateInscribedCircle(p1, p2, p3)
        currentCircle = inscribedCircle
        addCircleToCanvas(inscribedCircle)
        
        println("Создана вписанная окружность: центр(${inscribedCircle.x}, ${inscribedCircle.y}), радиус=${inscribedCircle.r}")
    }
    
    private fun calculateInscribedCircle(p1: Point, p2: Point, p3: Point): Circle {
        // Вычислить длины сторон треугольника
        val a = p2.distance(p3)
        val b = p1.distance(p3)
        val c = p1.distance(p2)
        
        // Вычислить полупериметр
        val s = (a + b + c) / 2.0
        
        // Вычислить площадь треугольника по формуле Герона
        val area = sqrt(s * (s - a) * (s - b) * (s - c))
        
        // Радиус вписанной окружности
        val radius = area / s
        
        // Координаты центра вписанной окружности (инцентр)
        val centerX = (a * p1.x + b * p2.x + c * p3.x) / (a + b + c)
        val centerY = (a * p1.y + b * p2.y + c * p3.y) / (a + b + c)
        
        return Circle(Point(centerX, centerY), radius, LineColor.BLACK_0)
    }
    
    private fun showPreviewInscribedCircle(tempPoints: List<Point>) {
        if (tempPoints.size == 3) {
            val p1 = tempPoints[0]
            val p2 = tempPoints[1]
            val p3 = tempPoints[2]
            currentCircle = calculateInscribedCircle(p1, p2, p3)
        }
    }
    
    private fun addCircleToCanvas(circle: Circle) {
        // Здесь должна быть логика добавления окружности на холст
        println("Добавлена вписанная окружность: центр(${circle.determineCenter().x}, ${circle.determineCenter().y}), радиус=${circle.getR()}")
    }
    
    fun getCurrentCircle(): Circle? = currentCircle
    
    fun getTrianglePoints(): List<Point> = trianglePoints.toList()
    
    fun getPointCount(): Int = trianglePoints.size
    
    fun isDrawing(): Boolean = isDrawing
    
    override fun getName(): String = "Вписанная окружность"
    
    override fun getDescription(): String = "Нарисуйте вписанную окружность треугольника"
} 