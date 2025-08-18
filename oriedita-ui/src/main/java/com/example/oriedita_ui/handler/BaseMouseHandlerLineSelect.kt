package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor

/**
 * Базовый класс для обработчиков мыши с выбором линий
 * Позволяет рисовать линии выбора и выполнять действия на их основе
 * Адаптированная версия для Android
 */
abstract class BaseMouseHandlerLineSelect : BaseMouseHandler() {
    
    protected var selectionLine: LineSegment? = null
    protected var snapping = false
    private var isDrawingLine = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        val lineSegment = LineSegment(point, point, LineColor.MAGENTA_5).apply {
            setActive(LineSegment.ActiveState.ACTIVE_B_2)
        }
        
        val closestPoint = getClosestPoint(point)
        if (point.distance(closestPoint) < getSelectionDistance()) {
            selectionLine = LineSegment(point, closestPoint, LineColor.MAGENTA_5).apply {
                setActive(LineSegment.ActiveState.ACTIVE_B_2)
            }
        } else {
            selectionLine = lineSegment
        }
        
        addLineStep(selectionLine!!)
        isDrawingLine = true
        
        println("Начало рисования линии выбора: (${point.x}, ${point.y})")
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawingLine || selectionLine == null) return false
        
        val point = offsetToPoint(offset)
        val closestPoint = getClosestPoint(point)
        
        val newSelectionLine = if (point.distance(closestPoint) < getSelectionDistance() && !snapping) {
            LineSegment(selectionLine!!.getA(), closestPoint, LineColor.MAGENTA_5).apply {
                setActive(LineSegment.ActiveState.ACTIVE_B_2)
            }
        } else {
            LineSegment(selectionLine!!.getA(), point, LineColor.MAGENTA_5).apply {
                setActive(LineSegment.ActiveState.ACTIVE_B_2)
            }
        }
        
        selectionLine = newSelectionLine
        
        if (isGridInputAssistEnabled()) {
            clearLineCandidate()
            addLineCandidate(newSelectionLine)
        }
        
        setLineStep(0, newSelectionLine)
        
        if (snapping) {
            snapLine()
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (isDrawingLine) {
            isDrawingLine = false
            println("Завершение рисования линии выбора")
            reset()
        }
        return true
    }
    
    /**
     * Привязка линии к ближайшей точке в активной угловой системе
     */
    private fun snapLine() {
        if (selectionLine == null) return
        
        val snappedPoint = snapToClosePointInActiveAngleSystem(
            selectionLine!!.getB(),
            selectionLine!!.getA()
        )
        
        selectionLine = LineSegment(selectionLine!!.getA(), snappedPoint, LineColor.MAGENTA_5).apply {
            setActive(LineSegment.ActiveState.ACTIVE_B_2)
        }
        setLineStep(0, selectionLine!!)
        
        println("Линия привязана к точке: (${snappedPoint.x}, ${snappedPoint.y})")
    }
    
    fun setSnappingEnabled(enabled: Boolean) {
        snapping = enabled
        println("Привязка ${if (enabled) "включена" else "отключена"}")
    }
    
    fun isSnapping(): Boolean = snapping
    
    fun isDrawingLine(): Boolean = isDrawingLine
    
    override fun reset() {
        super.reset()
        selectionLine = null
        snapping = false
        isDrawingLine = false
        
        println("Обработчик выбора линий сброшен")
    }
    
    fun getLineSelectDescription(): String {
        return when {
            !isDrawingLine -> "Готов к рисованию линии"
            selectionLine == null -> "Рисование линии..."
            else -> "Линия: (${selectionLine!!.determineAX()}, ${selectionLine!!.determineAY()}) -> (${selectionLine!!.determineBX()}, ${selectionLine!!.determineBY()})"
        }
    }
    
    fun getLineSelectInfo(): String {
        return buildString {
            append("Статус рисования: ${if (isDrawingLine) "активно" else "неактивно"}\n")
            append("Привязка: ${if (snapping) "включена" else "отключена"}\n")
            append("Помощь ввода: ${if (isGridInputAssistEnabled()) "включена" else "отключена"}\n")
            if (selectionLine != null) {
                append("Длина линии: ${String.format("%.4f", selectionLine!!.determineLength())}")
            }
        }
    }
    
    // Заглушка для метода, который должен быть реализован в подклассе
    protected open fun snapToClosePointInActiveAngleSystem(start: Point, end: Point): Point {
        // Реализация привязки к угловой системе
        // Сначала получаем ближайшую точку
        val closestPoint = getClosestPoint(end)
        
        // Если ближайшая точка находится в пределах расстояния выбора, используем её
        if (end.distance(closestPoint) <= getSelectionDistance()) {
            return closestPoint
        }
        
        // Иначе пытаемся найти точку на угловой системе
        val angleSystemPoint = findPointInActiveAngleSystem(start, end)
        
        // Если найдена точка в угловой системе, проверяем её близость
        if (angleSystemPoint != null && end.distance(angleSystemPoint) <= getSelectionDistance() * 2) {
            return angleSystemPoint
        }
        
        // Если ничего не найдено, возвращаем исходную точку
        return end
    }
    
    /**
     * Поиск точки в активной угловой системе
     */
    private fun findPointInActiveAngleSystem(start: Point, end: Point): Point? {
        // Получаем угол между точками
        val angle = calculateAngle(start, end)
        
        // Получаем активную угловую систему
        val angleSystem = getActiveAngleSystem()
        
        // Ищем ближайший угол в системе
        val snappedAngle = findClosestAngleInSystem(angle, angleSystem)
        
        // Вычисляем новую точку с привязанным углом
        val distance = start.distance(end)
        val snappedPoint = Point(
            start.x + distance * Math.cos(Math.toRadians(snappedAngle)),
            start.y + distance * Math.sin(Math.toRadians(snappedAngle))
        )
        
        return snappedPoint
    }
    
    /**
     * Вычисление угла между двумя точками в градусах
     */
    private fun calculateAngle(start: Point, end: Point): Double {
        val deltaX = end.x - start.x
        val deltaY = end.y - start.y
        val angleRadians = Math.atan2(deltaY, deltaX)
        return Math.toDegrees(angleRadians)
    }
    
    /**
     * Поиск ближайшего угла в угловой системе
     */
    private fun findClosestAngleInSystem(angle: Double, angleSystem: List<Double>): Double {
        if (angleSystem.isEmpty()) return angle
        
        var closestAngle = angleSystem[0]
        var minDifference = Math.abs(angle - closestAngle)
        
        for (systemAngle in angleSystem) {
            val difference = Math.abs(angle - systemAngle)
            if (difference < minDifference) {
                minDifference = difference
                closestAngle = systemAngle
            }
        }
        
        return closestAngle
    }
    
    /**
     * Получение активной угловой системы
     * Заглушка - должна быть переопределена в подклассе
     */
    protected open fun getActiveAngleSystem(): List<Double> {
        // Стандартная угловая система: 0°, 45°, 90°, 135°, 180°, 225°, 270°, 315°
        return listOf(0.0, 45.0, 90.0, 135.0, 180.0, 225.0, 270.0, 315.0)
    }
} 