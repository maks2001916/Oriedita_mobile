package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Обработчик аксиомы 5 оригами
 * Адаптированная версия для Android
 */
class MouseHandlerAxiom5 : BaseMouseHandler() {
    
    private var targetPoint: Point? = null
    private var targetSegment: LineSegment? = null
    private var pivotPoint: Point? = null
    private var indicator1: LineSegment? = null
    private var indicator2: LineSegment? = null
    private var destinationSegment: LineSegment? = null
    private var currentStep = Axiom5Step.SELECT_TARGET_POINT
    private var isProcessing = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            Axiom5Step.SELECT_TARGET_POINT -> {
                handleSelectTargetPointPress(point)
            }
            Axiom5Step.SELECT_TARGET_SEGMENT -> {
                handleSelectTargetSegmentPress(point)
            }
            Axiom5Step.SELECT_PIVOT_POINT -> {
                handleSelectPivotPointPress(point)
            }
            Axiom5Step.SELECT_DESTINATION_OR_INDICATOR -> {
                handleSelectDestinationOrIndicatorPress(point)
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            Axiom5Step.SELECT_TARGET_POINT -> {
                handleSelectTargetPointDrag(point)
            }
            Axiom5Step.SELECT_TARGET_SEGMENT -> {
                handleSelectTargetSegmentDrag(point)
            }
            Axiom5Step.SELECT_PIVOT_POINT -> {
                handleSelectPivotPointDrag(point)
            }
            Axiom5Step.SELECT_DESTINATION_OR_INDICATOR -> {
                handleSelectDestinationOrIndicatorDrag(point)
            }
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            Axiom5Step.SELECT_TARGET_POINT -> {
                currentStep = handleSelectTargetPointRelease(point)
            }
            Axiom5Step.SELECT_TARGET_SEGMENT -> {
                currentStep = handleSelectTargetSegmentRelease(point)
            }
            Axiom5Step.SELECT_PIVOT_POINT -> {
                currentStep = handleSelectPivotPointRelease(point)
            }
            Axiom5Step.SELECT_DESTINATION_OR_INDICATOR -> {
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
        
        println("Целевая точка: ${targetPoint?.let { "(${it.x}, ${it.y})" } ?: "не выбрана"}")
    }
    
    private fun handleSelectTargetPointDrag(point: Point) {
        handleSelectTargetPointPress(point)
    }
    
    private fun handleSelectTargetPointRelease(point: Point): Axiom5Step {
        if (targetPoint == null) return Axiom5Step.SELECT_TARGET_POINT
        
        println("Переход к выбору целевого сегмента")
        return Axiom5Step.SELECT_TARGET_SEGMENT
    }
    
    private fun handleSelectTargetSegmentPress(point: Point) {
        val closestLineSegment = getClosestLineSegment(point)
        if (OritaCalc.determineLineSegmentDistance(point, closestLineSegment) < getSelectionDistance()) {
            targetSegment = LineSegment(closestLineSegment.getA(), closestLineSegment.getB(), LineColor.GREEN_6)
        } else {
            targetSegment = null
        }
    }
    
    private fun handleSelectTargetSegmentDrag(point: Point) {
        handleSelectTargetSegmentPress(point)
    }
    
    private fun handleSelectTargetSegmentRelease(point: Point): Axiom5Step {
        if (targetSegment == null) return Axiom5Step.SELECT_TARGET_SEGMENT
        
        println("Переход к выбору точки поворота")
        return Axiom5Step.SELECT_PIVOT_POINT
    }
    
    private fun handleSelectPivotPointPress(point: Point) {
        val closestPoint = getClosestPoint(point)
        if (point.distance(closestPoint) < getSelectionDistance() &&
            OritaCalc.determineLineSegmentDistance(closestPoint, LineSegment(targetPoint!!, targetPoint!!)) > 0.0000001 &&
            !(OritaCalc.isPointWithinLineSpan(closestPoint, targetSegment!!) && 
              OritaCalc.isPointWithinLineSpan(targetPoint!!, targetSegment!!))) {
            pivotPoint = closestPoint
        } else {
            pivotPoint = null
        }
    }
    
    private fun handleSelectPivotPointDrag(point: Point) {
        handleSelectPivotPointPress(point)
    }
    
    private fun handleSelectPivotPointRelease(point: Point): Axiom5Step {
        if (pivotPoint == null) return Axiom5Step.SELECT_PIVOT_POINT
        
        val radius = OritaCalc.distance(targetPoint!!, pivotPoint!!)
        drawAxiom5FoldIndicators(radius)
        
        println("Переход к выбору назначения или индикатора")
        return Axiom5Step.SELECT_DESTINATION_OR_INDICATOR
    }
    
    private fun handleSelectDestinationOrIndicatorPress(point: Point) {
        updateDestinationSegment(point)
    }
    
    private fun handleSelectDestinationOrIndicatorDrag(point: Point) {
        updateDestinationSegment(point)
    }
    
    private fun handleSelectDestinationOrIndicatorRelease(point: Point): Axiom5Step {
        if (OritaCalc.determineLineSegmentDistance(point, indicator1!!) < getSelectionDistance() ||
            OritaCalc.determineLineSegmentDistance(point, indicator2!!) < getSelectionDistance()) {
            
            val selectedIndicator = if (OritaCalc.determineLineSegmentDistance(point, indicator1!!) < 
                                       OritaCalc.determineLineSegmentDistance(point, indicator2!!)) {
                indicator1!!
            } else {
                indicator2!!
            }
            
            val newSegment = LineSegment(selectedIndicator.getB(), selectedIndicator.getA(), getLineColor())
            val extendedSegment = OritaCalc.fullExtendUntilHit(getFoldLineSet(), newSegment)
            
            addLineSegment(extendedSegment)
            println("Добавлена линия аксиомы 5")
            
            reset()
            return Axiom5Step.SELECT_TARGET_POINT
        }
        
        if (destinationSegment == null) return Axiom5Step.SELECT_DESTINATION_OR_INDICATOR
        
        val intersectPoint1 = OritaCalc.findIntersection(indicator1!!, destinationSegment!!)
        val intersectPoint2 = OritaCalc.findIntersection(indicator2!!, destinationSegment!!)
        
        val d1 = OritaCalc.distance(point, intersectPoint1)
        val d2 = OritaCalc.distance(point, intersectPoint2)
        
        val newSegment = LineSegment(pivotPoint!!, if (d1 < d2) intersectPoint1 else intersectPoint2, getLineColor())
        addLineSegment(newSegment)
        
        println("Добавлена линия аксиомы 5")
        reset()
        return Axiom5Step.SELECT_TARGET_POINT
    }
    
    private fun updateDestinationSegment(point: Point) {
        val indicator1Distance = OritaCalc.determineLineSegmentDistance(point, indicator1!!)
        val indicator2Distance = OritaCalc.determineLineSegmentDistance(point, indicator2!!)
        val targetSegmentDistance = OritaCalc.determineLineSegmentDistance(point, targetSegment!!)
        val normalDistance = OritaCalc.determineLineSegmentDistance(point, getClosestLineSegment(point))
        
        val minDistance = minOf(indicator1Distance, indicator2Distance, targetSegmentDistance, normalDistance)
        
        destinationSegment = when {
            abs(minDistance - indicator1Distance) < 0.000001 && indicator1Distance < getSelectionDistance() -> {
                LineSegment(indicator1!!.getA(), indicator1!!.getB(), LineColor.ORANGE_4)
            }
            abs(minDistance - indicator2Distance) < 0.000001 && indicator2Distance < getSelectionDistance() -> {
                LineSegment(indicator2!!.getA(), indicator2!!.getB(), LineColor.ORANGE_4)
            }
            abs(minDistance - targetSegmentDistance) < 0.000001 && targetSegmentDistance < getSelectionDistance() -> {
                null
            }
            abs(minDistance - normalDistance) < 0.000001 && normalDistance < getSelectionDistance() -> {
                val closestSegment = getClosestLineSegment(point)
                LineSegment(closestSegment.getA(), closestSegment.getB(), LineColor.ORANGE_4)
            }
            else -> null
        }
    }
    
    private fun drawAxiom5FoldIndicators(radius: Double) {
        if (radius <= 0.0000001) {
            reset()
            return
        }
        
        val center = Point(pivotPoint!!)
        val lengthA = if (!OritaCalc.isPointWithinLineSpan(pivotPoint!!, targetSegment!!)) {
            OritaCalc.distance(center, OritaCalc.findProjection(targetSegment!!, center))
        } else {
            0.0
        }
        
        if (abs(lengthA - radius) < 0.0000001) {
            // Пересечение в одной точке
            val projectionPoint = OritaCalc.findProjection(targetSegment!!, pivotPoint!!)
            val projectionLine = LineSegment(pivotPoint!!, projectionPoint)
            
            if (OritaCalc.isPointWithinLineSpan(targetPoint!!, projectionLine)) {
                if (OritaCalc.distance(projectionPoint, targetPoint!!) < 0.0000001) {
                    val midPoint = Point(OritaCalc.midPoint(pivotPoint!!, projectionPoint))
                    
                    indicator1 = OritaCalc.fullExtendUntilHit(getFoldLineSet(), 
                        LineSegment(midPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, -1.0), midPoint), LineColor.PURPLE_8))
                    indicator2 = OritaCalc.fullExtendUntilHit(getFoldLineSet(), 
                        LineSegment(midPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, 1.0), midPoint), LineColor.PURPLE_8))
                } else {
                    indicator1 = OritaCalc.fullExtendUntilHit(getFoldLineSet(), 
                        LineSegment(pivotPoint!!, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, 1.0), pivotPoint!!), LineColor.PURPLE_8))
                    indicator2 = OritaCalc.fullExtendUntilHit(getFoldLineSet(), 
                        LineSegment(pivotPoint!!, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, -1.0), pivotPoint!!), LineColor.PURPLE_8))
                }
            } else {
                val s = if (OritaCalc.isLineSegmentParallel(LineSegment(pivotPoint!!, targetPoint!!), projectionLine) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                    OritaCalc.fullExtendUntilHit(getFoldLineSet(), 
                        LineSegment(pivotPoint!!, OritaCalc.center(pivotPoint!!, targetPoint!!, projectionPoint), LineColor.PURPLE_8))
                } else {
                    OritaCalc.fullExtendUntilHit(getFoldLineSet(), 
                        LineSegment(pivotPoint!!, projectionPoint, LineColor.PURPLE_8))
                }
                indicator1 = s
                indicator2 = s
            }
        } else if (lengthA > radius) {
            // Не пересекается
            reset()
        } else {
            // Пересечение в двух точках
            val lengthB = sqrt((radius * radius) - (lengthA * lengthA))
            val projectPoint = OritaCalc.findProjection(targetSegment!!, pivotPoint!!)
            
            val l1 = processProjectedLineOfIndicator(pivotPoint!!, projectPoint, lengthB)
            val l2 = processProjectedLineOfIndicator(pivotPoint!!, projectPoint, -lengthB)
            
            val center1 = processCenter(pivotPoint!!, LineSegment(targetPoint!!, pivotPoint!!), l1)
            val center2 = processCenter(pivotPoint!!, LineSegment(targetPoint!!, pivotPoint!!), l2)
            
            indicator1 = OritaCalc.fullExtendUntilHit(getFoldLineSet(), 
                LineSegment(center, center1, LineColor.PURPLE_8))
            indicator2 = OritaCalc.fullExtendUntilHit(getFoldLineSet(), 
                LineSegment(center, center2, LineColor.PURPLE_8))
        }
        
        println("Индикаторы аксиомы 5 созданы")
    }
    
    private fun processProjectedLineOfIndicator(pivot: Point, projectPoint: Point, length: Double): LineSegment {
        val projectLine = LineSegment(pivot, projectPoint)
        val line = LineSegment(projectPoint, OritaCalc.findProjection(OritaCalc.moveParallel(projectLine, length), projectPoint))
        return LineSegment(pivot, line.getB())
    }
    
    private fun processCenter(pivot: Point, l1: LineSegment, l2: LineSegment): Point {
        return OritaCalc.center(pivot, l2.getB(), l1.getB())
    }
    
    override fun reset() {
        targetPoint = null
        targetSegment = null
        pivotPoint = null
        indicator1 = null
        indicator2 = null
        destinationSegment = null
        currentStep = Axiom5Step.SELECT_TARGET_POINT
        isProcessing = false
        
        println("Аксиома 5 сброшена")
    }
    
    fun getCurrentStep(): Axiom5Step = currentStep
    
    fun getTargetPoint(): Point? = targetPoint
    
    fun getTargetSegment(): LineSegment? = targetSegment
    
    fun getPivotPoint(): Point? = pivotPoint
    
    fun getIndicators(): Pair<LineSegment?, LineSegment?> = Pair(indicator1, indicator2)
    
    fun getDestinationSegment(): LineSegment? = destinationSegment
    
    fun isProcessing(): Boolean = isProcessing
    
    fun getAxiom5Description(): String {
        return "Аксиома 5 - шаг: ${currentStep.name}, целевая точка: ${targetPoint?.let { "(${it.x}, ${it.y})" } ?: "не выбрана"}"
    }
    
    override fun getName(): String = "Аксиома 5"
    
    override fun getDescription(): String = "Создавайте складки через точку и линию"
}

enum class Axiom5Step {
    SELECT_TARGET_POINT,
    SELECT_TARGET_SEGMENT,
    SELECT_PIVOT_POINT,
    SELECT_DESTINATION_OR_INDICATOR
} 