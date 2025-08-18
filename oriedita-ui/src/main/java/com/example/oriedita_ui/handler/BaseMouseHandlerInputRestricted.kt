package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor

/**
 * Базовый класс для обработчиков мыши с ограниченным вводом
 * Показывает кандидатов для выбора при наведении мыши
 * Адаптированная версия для Android
 */
abstract class BaseMouseHandlerInputRestricted : BaseMouseHandler() {
    
    protected var candidatePoint: Point? = null
    private var isGridInputAssistEnabled = true
    
    // Временные списки для хранения линий (в реальной реализации должны быть подключены к модели)
    override val lineStep = mutableListOf<LineSegment>()
    override val lineCandidate = mutableListOf<LineSegment>()
    
    // Глобальные настройки для всех обработчиков
    companion object {
        private var globalSelectionDistance = 50.0
        private var globalLineColor = LineColor.MAGENTA_5
        private val globalPoints = mutableListOf<Point>()
        private val globalLines = mutableListOf<LineSegment>()
    }
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        return true
    }
    
    /**
     * Обработка движения мыши для показа кандидатов выбора
     */
    fun handleMouseMoved(offset: Offset) {
        if (!isGridInputAssistEnabled) {
            clearCandidatePoint()
            return
        }
        
        val point = offsetToPoint(offset)
        val closestPoint = getClosestPoint(point)
        
        if (point.distance(closestPoint) < getSelectionDistance()) {
            candidatePoint = closestPoint
            val candidate = LineSegment(closestPoint, closestPoint, getLineColor()).apply {
                setActive(LineSegment.ActiveState.ACTIVE_BOTH_3)
            }
            addLineCandidate(candidate)
            println("Показан кандидат: (${closestPoint.x}, ${closestPoint.y})")
        } else {
            clearCandidatePoint()
        }
    }
    

    
    protected fun clearCandidatePoint() {
        candidatePoint = null
        clearLineCandidate()
    }
    

    

    
    fun setGridInputAssistEnabled(enabled: Boolean) {
        isGridInputAssistEnabled = enabled
        if (!enabled) {
            clearCandidatePoint()
        }
    }
    
    // Методы для работы с линиями (в реальной реализации должны быть подключены к модели)
    override fun addLineStep(line: LineSegment) {
        lineStep.add(line)
    }
    
    override fun getLineStepSize(): Int = lineStep.size
    
    override fun getLineStep(index: Int): LineSegment = lineStep[index]
    
    override fun setLineStep(index: Int, line: LineSegment) {
        if (index < lineStep.size) {
            lineStep[index] = line
        }
    }
    
    override fun clearLineStep() {
        lineStep.clear()
    }
    
    override fun addLineCandidate(line: LineSegment) {
        lineCandidate.clear() // Очищаем предыдущих кандидатов
        lineCandidate.add(line)
    }
    
    override fun clearLineCandidate() {
        lineCandidate.clear()
    }
    
    // Реализация методов, которые должны быть подключены к модели
    override fun getClosestPoint(point: Point): Point {
        // Ищем ближайшую точку среди всех доступных точек
        var closestPoint = point
        var minDistance = Double.MAX_VALUE
        
        // Проверяем точки из глобального списка
        for (globalPoint in globalPoints) {
            val distance = point.distance(globalPoint)
            if (distance < minDistance) {
                minDistance = distance
                closestPoint = globalPoint
            }
        }
        
        // Проверяем точки из линий
        for (line in globalLines) {
            val distanceToA = point.distance(line.getA())
            val distanceToB = point.distance(line.getB())
            
            if (distanceToA < minDistance) {
                minDistance = distanceToA
                closestPoint = line.getA()
            }
            if (distanceToB < minDistance) {
                minDistance = distanceToB
                closestPoint = line.getB()
            }
        }
        
        return closestPoint
    }
    
    override fun getSelectionDistance(): Double {
        return globalSelectionDistance
    }
    
    override fun getLineColor(): LineColor {
        return globalLineColor
    }
    
    // Методы для управления глобальными настройками
    fun setGlobalSelectionDistance(distance: Double) {
        globalSelectionDistance = distance
    }
    
    fun setGlobalLineColor(color: LineColor) {
        globalLineColor = color
    }
    
    fun addGlobalPoint(point: Point) {
        globalPoints.add(point)
    }
    
    fun addGlobalLine(line: LineSegment) {
        globalLines.add(line)
    }
    
    fun clearGlobalData() {
        globalPoints.clear()
        globalLines.clear()
    }
    
    fun getGlobalPoints(): List<Point> = globalPoints.toList()
    
    fun getGlobalLines(): List<LineSegment> = globalLines.toList()
    
    fun getInputRestrictedDescription(): String {
        return if (candidatePoint != null) {
            "Кандидат выбран: (${candidatePoint!!.x}, ${candidatePoint!!.y})"
        } else {
            "Нет кандидатов для выбора"
        }
    }
    
    fun getInputRestrictedInfo(): String {
        return buildString {
            append("Помощь ввода: ${if (isGridInputAssistEnabled) "включена" else "отключена"}\n")
            append("Кандидат: ${candidatePoint?.let { "(${it.x}, ${it.y})" } ?: "нет"}\n")
            append("Расстояние выбора: ${getSelectionDistance()}\n")
            append("Глобальных точек: ${globalPoints.size}\n")
            append("Глобальных линий: ${globalLines.size}")
        }
    }
} 