package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик аксиомы 7 оригами
 * Адаптированная версия для Android
 */
class MouseHandlerAxiom7 : BaseMouseHandler() {
    
    private var targetPoint: Point? = null
    private var targetSegment: LineSegment? = null
    private var perpendicularSegment: LineSegment? = null
    private var indicator: LineSegment? = null
    private var destinationSegment: LineSegment? = null
    private var currentStep = Axiom7Step.SELECT_TARGET_POINT
    private var isProcessing = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            Axiom7Step.SELECT_TARGET_POINT -> {
                handleSelectTargetPointPress(point)
            }
            Axiom7Step.SELECT_TARGET_SEGMENT -> {
                handleSelectTargetSegmentPress(point)
            }
            Axiom7Step.SELECT_PERPENDICULAR_SEGMENT -> {
                handleSelectPerpendicularSegmentPress(point)
            }
            Axiom7Step.SELECT_DESTINATION_OR_INDICATOR -> {
                handleSelectDestinationOrIndicatorPress(point)
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            Axiom7Step.SELECT_TARGET_POINT -> {
                handleSelectTargetPointDrag(point)
            }
            Axiom7Step.SELECT_TARGET_SEGMENT -> {
                handleSelectTargetSegmentDrag(point)
            }
            Axiom7Step.SELECT_PERPENDICULAR_SEGMENT -> {
                handleSelectPerpendicularSegmentDrag(point)
            }
            Axiom7Step.SELECT_DESTINATION_OR_INDICATOR -> {
                handleSelectDestinationOrIndicatorDrag(point)
            }
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            Axiom7Step.SELECT_TARGET_POINT -> {
                currentStep = handleSelectTargetPointRelease(point)
            }
            Axiom7Step.SELECT_TARGET_SEGMENT -> {
                currentStep = handleSelectTargetSegmentRelease(point)
            }
            Axiom7Step.SELECT_PERPENDICULAR_SEGMENT -> {
                currentStep = handleSelectPerpendicularSegmentRelease(point)
            }
            Axiom7Step.SELECT_DESTINATION_OR_INDICATOR -> {
                currentStep = handleSelectDestinationOrIndicatorRelease(point)
            }
        }
        
        return true
    }
    
    private fun handleSelectTargetPointPress(point: Point) {
        val closestPoint = getClosestPoint(point)
        if (point.distance(closestPoint) < getSelectionDistance()) {
            targetPoint = closestPoint
        } else {
            targetPoint = null
        }
        
        println("Целевая точка аксиомы 7: ${targetPoint?.let { "(${it.x}, ${it.y})" } ?: "не выбрана"}")
    }
    
    private fun handleSelectTargetPointDrag(point: Point) {
        handleSelectTargetPointPress(point)
    }
    
    private fun handleSelectTargetPointRelease(point: Point): Axiom7Step {
        if (targetPoint == null) return Axiom7Step.SELECT_TARGET_POINT
        
        println("Переход к выбору целевого сегмента аксиомы 7")
        return Axiom7Step.SELECT_TARGET_SEGMENT
    }
    
    private fun handleSelectTargetSegmentPress(point: Point) {
        val closestLineSegment = getClosestLineSegment(point)
        if (OritaCalc.determineLineSegmentDistance(point, closestLineSegment) < getSelectionDistance() &&
            !OritaCalc.isPointWithinLineSpan(targetPoint!!, closestLineSegment)) {
            targetSegment = LineSegment(closestLineSegment.getA(), closestLineSegment.getB(), LineColor.GREEN_6)
        } else {
            targetSegment = null
        }
    }
    
    private fun handleSelectTargetSegmentDrag(point: Point) {
        handleSelectTargetSegmentPress(point)
    }
    
    private fun handleSelectTargetSegmentRelease(point: Point): Axiom7Step {
        if (targetSegment == null) return Axiom7Step.SELECT_TARGET_SEGMENT
        
        println("Переход к выбору перпендикулярного сегмента")
        return Axiom7Step.SELECT_PERPENDICULAR_SEGMENT
    }
    
    private fun handleSelectPerpendicularSegmentPress(point: Point) {
        val closestLineSegment = getClosestLineSegment(point)
        if (OritaCalc.determineLineSegmentDistance(point, closestLineSegment) < getSelectionDistance() &&
            OritaCalc.isLineSegmentParallel(closestLineSegment, targetSegment!!) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
            perpendicularSegment = LineSegment(closestLineSegment.getA(), closestLineSegment.getB(), LineColor.GREEN_6)
        } else {
            perpendicularSegment = null
        }
    }
    
    private fun handleSelectPerpendicularSegmentDrag(point: Point) {
        handleSelectPerpendicularSegmentPress(point)
    }
    
    private fun handleSelectPerpendicularSegmentRelease(point: Point): Axiom7Step {
        if (perpendicularSegment == null) return Axiom7Step.SELECT_PERPENDICULAR_SEGMENT
        
        drawAxiom7FoldIndicators()
        println("Переход к выбору назначения или индикатора аксиомы 7")
        return Axiom7Step.SELECT_DESTINATION_OR_INDICATOR
    }
    
    private fun handleSelectDestinationOrIndicatorPress(point: Point) {
        updateDestinationSegment(point)
    }
    
    private fun handleSelectDestinationOrIndicatorDrag(point: Point) {
        updateDestinationSegment(point)
    }
    
    private fun handleSelectDestinationOrIndicatorRelease(point: Point): Axiom7Step {
        if (OritaCalc.determineLineSegmentDistance(point, indicator!!) < getSelectionDistance()) {
            addLineSegment(LineSegment(indicator!!.getA(), indicator!!.getB(), getLineColor()))
            println("Добавлена линия аксиомы 7")
            reset()
            return Axiom7Step.SELECT_TARGET_POINT
        }
        
        if (destinationSegment == null) return Axiom7Step.SELECT_DESTINATION_OR_INDICATOR
        
        val result = getExtendedSegment(indicator!!, destinationSegment!!, getLineColor())
        if (result != null) {
            addLineSegment(result)
            println("Добавлена линия аксиомы 7")
        }
        
        reset()
        return Axiom7Step.SELECT_TARGET_POINT
    }
    
    private fun updateDestinationSegment(point: Point) {
        val indicatorDistance = OritaCalc.determineLineSegmentDistance(point, indicator!!)
        val normalDistance = OritaCalc.determineLineSegmentDistance(point, getClosestLineSegment(point))
        
        destinationSegment = when {
            indicatorDistance < normalDistance && indicatorDistance < getSelectionDistance() -> {
                LineSegment(indicator!!.getA(), indicator!!.getB(), LineColor.ORANGE_4)
            }
            normalDistance < indicatorDistance && normalDistance < getSelectionDistance() &&
            OritaCalc.isLineSegmentParallel(getClosestLineSegment(point), indicator!!) == OritaCalc.ParallelJudgement.NOT_PARALLEL -> {
                val closestSegment = getClosestLineSegment(point)
                LineSegment(closestSegment.getA(), closestSegment.getB(), LineColor.ORANGE_4)
            }
            else -> null
        }
    }
    
    private fun drawAxiom7FoldIndicators() {
        val temp = LineSegment(targetPoint!!, Point(
            targetPoint!!.x + perpendicularSegment!!.determineBX() - perpendicularSegment!!.determineAX(),
            targetPoint!!.y + perpendicularSegment!!.determineBY() - perpendicularSegment!!.determineAY()
        ))
        
        val extendLine = getExtendedSegment(temp, targetSegment!!, LineColor.PURPLE_8)
        if (extendLine == null) return
        
        val mid = OritaCalc.midPoint(targetPoint!!, OritaCalc.findIntersection(extendLine, targetSegment!!))
        indicator = OritaCalc.fullExtendUntilHit(getFoldLineSet(), 
            LineSegment(mid, OritaCalc.findProjection(OritaCalc.moveParallel(extendLine, 1.0), mid), LineColor.PURPLE_8))
        indicator = OritaCalc.fullExtendUntilHit(getFoldLineSet(), 
            LineSegment(indicator!!.getB(), indicator!!.getA(), indicator!!.getColor()))
        
        println("Индикаторы аксиомы 7 созданы")
    }
    
    private fun getExtendedSegment(sO: LineSegment, sK: LineSegment, color: LineColor): LineSegment? {
        if (OritaCalc.isLineSegmentParallel(sO, sK, 0.0000001) == OritaCalc.ParallelJudgement.PARALLEL_NOT_EQUAL) {
            return null
        }
        
        val crossPoint = when (OritaCalc.isLineSegmentParallel(sO, sK, 0.0000001)) {
            OritaCalc.ParallelJudgement.PARALLEL_EQUAL -> {
                if (OritaCalc.distance(sO.getA(), sK.getA()) > OritaCalc.distance(sO.getA(), sK.getB())) {
                    sK.getB()
                } else {
                    sK.getA()
                }
            }
            OritaCalc.ParallelJudgement.NOT_PARALLEL -> {
                OritaCalc.findIntersection(sO, sK)
            }
            else -> sK.getA()
        }
        
        val addSegment = LineSegment(crossPoint, sO.getA(), color)
        return if (addSegment.determineLength() > 0.0) {
            addSegment
        } else {
            null
        }
    }
    
    override fun reset() {
        targetPoint = null
        targetSegment = null
        perpendicularSegment = null
        indicator = null
        destinationSegment = null
        currentStep = Axiom7Step.SELECT_TARGET_POINT
        isProcessing = false
        
        println("Аксиома 7 сброшена")
    }
    
    fun getCurrentStep(): Axiom7Step = currentStep
    
    fun getTargetPoint(): Point? = targetPoint
    
    fun getTargetSegment(): LineSegment? = targetSegment
    
    fun getPerpendicularSegment(): LineSegment? = perpendicularSegment
    
    fun getIndicator(): LineSegment? = indicator
    
    fun getDestinationSegment(): LineSegment? = destinationSegment
    
    fun isProcessing(): Boolean = isProcessing
    
    fun getAxiom7Description(): String {
        return "Аксиома 7 - шаг: ${currentStep.name}, целевая точка: ${targetPoint?.let { "(${it.x}, ${it.y})" } ?: "не выбрана"}"
    }
    
    override fun getName(): String = "Аксиома 7"
    
    override fun getDescription(): String = "Создавайте складки через точку и перпендикулярную линию"
}

enum class Axiom7Step {
    SELECT_TARGET_POINT,
    SELECT_TARGET_SEGMENT,
    SELECT_PERPENDICULAR_SEGMENT,
    SELECT_DESTINATION_OR_INDICATOR
} 