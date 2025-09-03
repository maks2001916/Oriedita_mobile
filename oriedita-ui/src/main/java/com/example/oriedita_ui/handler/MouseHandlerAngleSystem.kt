package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик угловой системы
 * Адаптированная версия для Android
 */
class MouseHandlerAngleSystem : BaseMouseHandler() {
    
    private var anchorPoint: Point? = null
    private var releasePoint: Point? = null
    private var selectedSegment: LineSegment? = null
    private var destinationSegment: LineSegment? = null
    private var candidates = mutableListOf<LineSegment>()
    private var currentStep = AngleSystemStep.CLICK_DRAG_POINT
    private var isProcessing = false
    
    // Цвета для угловой системы
    private val customAngleColors = arrayOf(
        LineColor.ORANGE_4,
        LineColor.GREEN_6,
        LineColor.PURPLE_8
    )
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            AngleSystemStep.CLICK_DRAG_POINT -> {
                handleClickDragPointPress(point)
            }
            AngleSystemStep.SELECT_DIRECTION -> {
                handleSelectDirectionPress(point)
            }
            AngleSystemStep.SELECT_LENGTH -> {
                handleSelectLengthPress(point)
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            AngleSystemStep.CLICK_DRAG_POINT -> {
                handleClickDragPointDrag(point)
            }
            AngleSystemStep.SELECT_DIRECTION -> {
                handleSelectDirectionDrag(point)
            }
            AngleSystemStep.SELECT_LENGTH -> {
                handleSelectLengthDrag(point)
            }
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            AngleSystemStep.CLICK_DRAG_POINT -> {
                currentStep = handleClickDragPointRelease(point)
            }
            AngleSystemStep.SELECT_DIRECTION -> {
                currentStep = handleSelectDirectionRelease(point)
            }
            AngleSystemStep.SELECT_LENGTH -> {
                currentStep = handleSelectLengthRelease(point)
            }
        }
        
        return true
    }
    
    private fun handleClickDragPointPress(point: Point) {
        val closestPoint = getClosestPoint(point)
        if (point.distance(closestPoint) < getSelectionDistance()) {
            anchorPoint = closestPoint
        } else {
            anchorPoint = null
        }
        
        println("Начальная точка установлена: ${anchorPoint?.let { "(${it.x}, ${it.y})" } ?: "не выбрана"}")
    }
    
    private fun handleClickDragPointDrag(point: Point) {
        if (anchorPoint == null) return
        
        releasePoint = point
        val closestPoint = getClosestPoint(point)
        if (point.distance(closestPoint) < getSelectionDistance()) {
            releasePoint = closestPoint
        }
        
        candidates = makePreviewLines(anchorPoint!!, releasePoint!!)
        
        println("Создано ${candidates.size} кандидатов для угловой системы")
    }
    
    private fun handleClickDragPointRelease(point: Point): AngleSystemStep {
        if (point == null || anchorPoint == null || releasePoint == null || anchorPoint == releasePoint) {
            reset()
            return AngleSystemStep.CLICK_DRAG_POINT
        }
        
        println("Переход к выбору направления")
        return AngleSystemStep.SELECT_DIRECTION
    }
    
    private fun handleSelectDirectionPress(point: Point) {
        selectedSegment = determineSelectedCandidate(point)
    }
    
    private fun handleSelectDirectionDrag(point: Point) {
        selectedSegment = determineSelectedCandidate(point)
    }
    
    private fun handleSelectDirectionRelease(point: Point): AngleSystemStep {
        if (selectedSegment == null) return AngleSystemStep.SELECT_DIRECTION
        
        candidates.clear()
        println("Переход к выбору длины")
        return AngleSystemStep.SELECT_LENGTH
    }
    
    private fun handleSelectLengthPress(point: Point) {
        updateDestinationSegment(point)
    }
    
    private fun handleSelectLengthDrag(point: Point) {
        updateDestinationSegment(point)
    }
    
    private fun handleSelectLengthRelease(point: Point): AngleSystemStep {
        if (destinationSegment == null) return AngleSystemStep.SELECT_LENGTH
        
        val lineSegmentToAdd = determineLineSegmentToAdd(point)
        if (lineSegmentToAdd != null && lineSegmentToAdd.determineLength() > 0.0) {
            addLineSegment(lineSegmentToAdd)
            println("Добавлена линия угловой системы")
        }
        
        reset()
        return AngleSystemStep.CLICK_DRAG_POINT
    }
    
    private fun updateDestinationSegment(point: Point) {
        if (selectedSegment == null || releasePoint == null) return
        
        val projection = OritaCalc.findProjection(selectedSegment!!, point)
        selectedSegment = LineSegment(releasePoint!!, projection, getLineColor())
        
        val closestLineSegment = getClosestLineSegment(point)
        if (OritaCalc.determineLineSegmentDistance(point, closestLineSegment) < getSelectionDistance()) {
            destinationSegment = LineSegment(closestLineSegment.getA(), closestLineSegment.getB(), LineColor.ORANGE_4)
        } else {
            destinationSegment = null
        }
    }
    
    private fun determineLineSegmentToAdd(point: Point): LineSegment? {
        val closestLineSegment = getClosestLineSegment(point)
        if (OritaCalc.determineLineSegmentDistance(point, closestLineSegment) < getSelectionDistance()) {
            val startingPoint = OritaCalc.findIntersection(closestLineSegment, selectedSegment!!)
            return LineSegment(startingPoint, releasePoint!!, getLineColor())
        }
        return null
    }
    
    private fun determineSelectedCandidate(point: Point): LineSegment? {
        val closestCandidate = candidates.minByOrNull { candidate ->
            OritaCalc.determineLineSegmentDistance(point, candidate)
        }
        
        if (closestCandidate != null && 
            OritaCalc.determineLineSegmentDistance(point, closestCandidate) < getSelectionDistance()) {
            return LineSegment(closestCandidate.getA(), closestCandidate.getB(), getLineColor())
        }
        return null
    }
    
    private fun makePreviewLines(pStart: Point, pEnd: Point): MutableList<LineSegment> {
        val candidates = mutableListOf<LineSegment>()
        val numPreviewLines = 6 // По умолчанию 6 линий
        
        val startingSegment = LineSegment(pStart, pEnd, LineColor.GREEN_6)
        candidates.add(startingSegment)
        
        val angles = doubleArrayOf(30.0, 45.0, 60.0, 90.0, 120.0, 135.0)
        
        for (i in 0 until 6) {
            val rotatedSegment = OritaCalc.lineSegment_rotate(startingSegment, angles[i], 1.0)
            rotatedSegment.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3)
            candidates.add(rotatedSegment)
            rotatedSegment.setColor(customAngleColors[i % 3])
        }
        
        return candidates
    }
    
    override fun reset() {
        anchorPoint = null
        releasePoint = null
        selectedSegment = null
        destinationSegment = null
        candidates.clear()
        currentStep = AngleSystemStep.CLICK_DRAG_POINT
        isProcessing = false
        
        println("Угловая система сброшена")
    }
    
    fun getCurrentStep(): AngleSystemStep = currentStep
    
    fun getCandidatesCount(): Int = candidates.size
    
    fun getSelectedSegment(): LineSegment? = selectedSegment
    
    fun getDestinationSegment(): LineSegment? = destinationSegment
    
    fun isProcessing(): Boolean = isProcessing
    
    fun getAngleSystemDescription(): String {
        return "Угловая система - шаг: ${currentStep.name}, кандидатов: ${candidates.size}"
    }
    
    override fun getName(): String = "Угловая система"
    
    override fun getDescription(): String = "Создавайте линии под определенными углами"
    


}

enum class AngleSystemStep {
    CLICK_DRAG_POINT,
    SELECT_DIRECTION,
    SELECT_LENGTH
} 