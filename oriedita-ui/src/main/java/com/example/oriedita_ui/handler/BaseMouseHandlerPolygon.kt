package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor

/**
 * Базовый класс для обработчиков мыши с рисованием полигонов
 * Позволяет создавать многоугольники и выполнять действия с ними
 * Адаптированная версия для Android
 */
abstract class BaseMouseHandlerPolygon : BaseMouseHandler() {
    
    private var polygonCompleted = false
    private var isDrawingPolygon = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        val lineSegment = if (getLineStepSize() == 0) {
            // Первая точка полигона
            val closestPoint = getClosestPoint(point)
            val startPoint = if (point.distance(closestPoint) > getSelectionDistance()) {
                point
            } else {
                closestPoint
            }
            LineSegment(startPoint, point, LineColor.MAGENTA_5)
        } else {
            // Следующие точки полигона
            val lastLine = getLineStep(getLineStepSize() - 1) as LineSegment
            LineSegment(lastLine.getB(), point, LineColor.MAGENTA_5)
        }
        
        addLineStep(lineSegment)
        isDrawingPolygon = true
        
        println("Добавлена точка полигона: (${point.x}, ${point.y})")
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawingPolygon) return false
        
        val point = offsetToPoint(offset)
        
        // Обновить последнюю линию
        if (getLineStepSize() > 0) {
            val lastLine = getLineStep(getLineStepSize() - 1) as LineSegment
            val updatedLine = LineSegment(lastLine.getA(), point, LineColor.MAGENTA_5)
            setLineStep(getLineStepSize() - 1, updatedLine)
        }
        
        updateGridAssistCandidate(point)
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawingPolygon) return false
        
        val point = offsetToPoint(offset)
        
        // Обновить последнюю линию
        if (getLineStepSize() > 0) {
            val lastLine = getLineStep(getLineStepSize() - 1) as LineSegment
            val updatedLine = LineSegment(lastLine.getA(), point, LineColor.MAGENTA_5)
            setLineStep(getLineStepSize() - 1, updatedLine)
        }
        
        // Проверить, можно ли замкнуть полигон
        if (getLineStepSize() >= 2) {
            val firstPoint = (getLineStep(0) as LineSegment).getA()
            if (point.distance(firstPoint) <= getSelectionDistance()) {
                // Замкнуть полигон
                val lastLine = getLineStep(getLineStepSize() - 1) as LineSegment
                val closedLine = LineSegment(lastLine.getA(), firstPoint, LineColor.MAGENTA_5)
                setLineStep(getLineStepSize() - 1, closedLine)
                
                polygonCompleted = true
                println("Полигон замкнут с ${getLineStepSize()} сторонами")
            }
        }
        
        if (polygonCompleted) {
            performAction()
            clearLineStep()
            polygonCompleted = false
            isDrawingPolygon = false
        }
        
        return true
    }
    
    /**
     * Обновить кандидата для помощи сетки
     */
    private fun updateGridAssistCandidate(point: Point) {
        if (!isGridInputAssistEnabled() || getLineStepSize() <= 1) return
        
        clearLineCandidate()
        
        val closestPoint = getClosestPoint(point)
        val candidatePoint = if (point.distance(closestPoint) > point.distance((getLineStep(0) as LineSegment).getA())) {
            (getLineStep(0) as LineSegment).getA()
        } else {
            closestPoint
        }
        
        val finalPoint = if (point.distance(candidatePoint) < getSelectionDistance()) {
            candidatePoint
        } else {
            point
        }
        
        val candidate = LineSegment(finalPoint, finalPoint, LineColor.MAGENTA_5).apply {
            setActive(LineSegment.ActiveState.ACTIVE_BOTH_3)
        }
        addLineCandidate(candidate)
    }
    
    fun isPolygonCompleted(): Boolean = polygonCompleted
    
    fun isDrawingPolygon(): Boolean = isDrawingPolygon
    
    fun getPolygonDescription(): String {
        return when {
            !isDrawingPolygon -> "Готов к рисованию полигона"
            polygonCompleted -> "Полигон завершен"
            else -> "Рисование полигона: ${getLineStepSize()} точек"
        }
    }
    
    fun getPolygonInfo(): String {
        return buildString {
            append("Статус рисования: ${if (isDrawingPolygon) "активно" else "неактивно"}\n")
            append("Завершен: ${if (polygonCompleted) "да" else "нет"}\n")
            append("Количество точек: ${getLineStepSize()}\n")
            append("Помощь сетки: ${if (isGridInputAssistEnabled()) "включена" else "отключена"}")
        }
    }
    
    override fun reset() {
        super.reset()
        polygonCompleted = false
        isDrawingPolygon = false
        
        println("Обработчик полигона сброшен")
    }
    
    // Абстрактные методы, которые должны быть реализованы в подклассах
    abstract fun performAction()
} 