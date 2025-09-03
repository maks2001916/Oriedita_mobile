package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик двойного симметричного рисования
 * Создает симметричные линии относительно выбранной оси
 * Адаптированная версия для Android
 */
class MouseHandlerDoubleSymmetricDraw : BaseMouseHandler() {
    
    private var anchorPoint: Point? = null
    private var releasePoint: Point? = null
    private var dragSegment: LineSegment? = null
    private var currentStep = DoubleSymmetricDrawStep.CLICK_DRAG_POINT
    private var isProcessing = false
    private var createdLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            DoubleSymmetricDrawStep.CLICK_DRAG_POINT -> {
                handleClickDragPointPress(point)
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            DoubleSymmetricDrawStep.CLICK_DRAG_POINT -> {
                handleClickDragPointDrag(point)
            }
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            DoubleSymmetricDrawStep.CLICK_DRAG_POINT -> {
                currentStep = handleClickDragPointRelease(point)
            }
        }
        
        return true
    }
    
    private fun handleClickDragPointPress(point: Point) {
        handleClickDragPointMove(point)
    }
    
    private fun handleClickDragPointMove(point: Point) {
        val closestPoint = getClosestPoint(point)
        if (point.distance(closestPoint) < getSelectionDistance()) {
            anchorPoint = closestPoint
        } else {
            anchorPoint = null
        }
    }
    
    private fun handleClickDragPointDrag(point: Point) {
        if (anchorPoint == null) return
        
        releasePoint = point
        val closestPoint = getClosestPoint(point)
        if (point.distance(closestPoint) < getSelectionDistance()) {
            releasePoint = closestPoint
        }
        
        dragSegment = LineSegment(anchorPoint!!, releasePoint!!, getLineColor())
    }
    
    private fun handleClickDragPointRelease(point: Point): DoubleSymmetricDrawStep {
        if (anchorPoint == null) return DoubleSymmetricDrawStep.CLICK_DRAG_POINT
        
        val closestPoint = getClosestPoint(point)
        if (point.distance(closestPoint) > getSelectionDistance()) {
            reset()
            return DoubleSymmetricDrawStep.CLICK_DRAG_POINT
        }
        
        dragSegment = LineSegment(anchorPoint!!, releasePoint!!)
        
        if (releasePoint!!.distance(point) > getSelectionDistance()) {
            reset()
            return DoubleSymmetricDrawStep.CLICK_DRAG_POINT
        }
        
        if (dragSegment!!.determineLength() <= 0.0) {
            reset()
            return DoubleSymmetricDrawStep.CLICK_DRAG_POINT
        }
        
        // Создать симметричные линии
        createSymmetricLines()
        
        reset()
        return DoubleSymmetricDrawStep.CLICK_DRAG_POINT
    }
    
    private fun createSymmetricLines() {
        if (dragSegment == null) return
        
        var isChanged = false
        
        val allSegments = getFoldLineSet()?.getLineSegmentsIterable()

        if (allSegments != null) {
            for (segment in allSegments) {
                val intersection = OritaCalc.determineLineSegmentIntersectionSweet(
                    segment, dragSegment!!, 0.001, 0.001
                )

                if (intersection == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_25 ||
                    intersection == LineSegment.Intersection.INTERSECTS_TSHAPE_S1_VERTICAL_BAR_26) {

                    val tMoto = if (OritaCalc.determineLineSegmentDistance(segment.getA(), dragSegment!!) <
                        OritaCalc.determineLineSegmentDistance(segment.getB(), dragSegment!!)) {
                        segment.getA()
                    } else {
                        segment.getB()
                    }

                    // Найти симметричную точку
                    val tTaisyou = OritaCalc.findLineSymmetryPoint(
                        dragSegment!!.getA(), dragSegment!!.getB(), tMoto
                    )

                    // Создать линию от пересечения до симметричной точки
                    val intersectionPoint = OritaCalc.findIntersection(segment, dragSegment!!)
                    val addLine = LineSegment(intersectionPoint, tTaisyou)

                    // Расширить линию до точки пересечения

                    val extendedLine = extendToIntersectionPoint(LineSegment(addLine.a, addLine.b, segment.color))

                    if (extendedLine.determineLength() > 0.0) {
                        isChanged = true
                        addLineSegment(extendedLine)
                        createdLines.add(extendedLine)

                        println("Создана симметричная линия: (${extendedLine.determineAX()}, ${extendedLine.determineAY()}) -> (${extendedLine.determineBX()}, ${extendedLine.determineBY()})")
                    }
                }
            }
        }
        
        if (isChanged) {
            println("Создано ${createdLines.size} симметричных линий")
        }
    }
    
    override fun reset() {
        anchorPoint = null
        releasePoint = null
        dragSegment = null
        currentStep = DoubleSymmetricDrawStep.CLICK_DRAG_POINT
        isProcessing = false
        createdLines.clear()
        
        println("Обработчик двойного симметричного рисования сброшен")
    }
    
    fun getCurrentStep(): DoubleSymmetricDrawStep = currentStep
    
    fun getAnchorPoint(): Point? = anchorPoint
    
    fun getReleasePoint(): Point? = releasePoint
    
    fun getDragSegment(): LineSegment? = dragSegment
    
    fun getCreatedLines(): List<LineSegment> = createdLines.toList()
    
    fun getCreatedLinesCount(): Int = createdLines.size
    
    fun isProcessing(): Boolean = isProcessing
    
    fun clearCreatedLines() {
        createdLines.clear()
    }
    
    fun getDoubleSymmetricDescription(): String {
        return when (currentStep) {
            DoubleSymmetricDrawStep.CLICK_DRAG_POINT -> {
                if (anchorPoint != null && releasePoint != null) {
                    "Перетащите для завершения симметричного рисования"
                } else if (anchorPoint != null) {
                    "Выберите конечную точку оси симметрии"
                } else {
                    "Выберите начальную точку оси симметрии"
                }
            }
        }
    }
    
    fun getDoubleSymmetricInfo(): String {
        return buildString {
            append("Начальная точка: ${anchorPoint?.let { "(${it.x}, ${it.y})" } ?: "не выбрана"}\n")
            append("Конечная точка: ${releasePoint?.let { "(${it.x}, ${it.y})" } ?: "не выбрана"}\n")
            append("Длина сегмента: ${dragSegment?.determineLength() ?: 0.0}\n")
            append("Созданные линии: ${createdLines.size}")
        }
    }
    
    override fun getName(): String = "Двойное симметричное рисование"
    
    override fun getDescription(): String = "Создавайте симметричные линии относительно выбранной оси"
}

enum class DoubleSymmetricDrawStep {
    CLICK_DRAG_POINT
} 