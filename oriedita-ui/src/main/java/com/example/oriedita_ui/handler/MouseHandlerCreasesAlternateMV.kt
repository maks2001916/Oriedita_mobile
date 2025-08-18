package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик чередования цветов сгибов
 * Адаптированная версия для Android
 */
class MouseHandlerCreasesAlternateMV : BaseMouseHandler() {
    
    private var anchorPoint: Point? = null
    private var releasePoint: Point? = null
    private var dragSegment: LineSegment? = null
    private var currentStep = CreasesAlternateMVStep.CLICK_DRAG_POINT
    private var isProcessing = false
    private var modifiedSegments = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            CreasesAlternateMVStep.CLICK_DRAG_POINT -> {
                handleClickDragPointPress(point)
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            CreasesAlternateMVStep.CLICK_DRAG_POINT -> {
                handleClickDragPointDrag(point)
            }
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            CreasesAlternateMVStep.CLICK_DRAG_POINT -> {
                currentStep = handleClickDragPointRelease(point)
            }
        }
        
        return true
    }
    
    private fun handleClickDragPointPress(point: Point) {
        anchorPoint = point
        println("Установлена начальная точка: (${point.x}, ${point.y})")
    }
    
    private fun handleClickDragPointDrag(point: Point) {
        releasePoint = point
        if (anchorPoint != null) {
            var gapSegment = LineSegment(anchorPoint!!, releasePoint!!)
            dragSegment = LineSegment(gapSegment.a, gapSegment.b, getLineColor())
            println("Перетаскивание: (${anchorPoint!!.x}, ${anchorPoint!!.y}) -> (${point.x}, ${point.y})")
        }
    }
    
    private fun handleClickDragPointRelease(point: Point): CreasesAlternateMVStep {
        if (dragSegment == null || dragSegment!!.determineLength() <= 0.0) {
            reset()
            println("Сегмент слишком короткий, операция отменена")
            return CreasesAlternateMVStep.CLICK_DRAG_POINT
        }
        
        // Найти пересекающиеся сегменты
        val intersectingSegments = findIntersectingSegments(dragSegment!!)
        
        // Чередовать цвета
        alternateColors(intersectingSegments)
        
        println("Изменены цвета ${modifiedSegments.size} сегментов")
        
        reset()
        return CreasesAlternateMVStep.CLICK_DRAG_POINT
    }
    
    private fun findIntersectingSegments(dragSegment: LineSegment): List<LineSegment> {
        val intersectingSegments = mutableListOf<LineSegment>()
        
        // Получить все сегменты линий сгиба
        val allSegments = getFoldLineSet()?.getLineSegmentsIterable()

        if (allSegments != null) {
            for (segment in allSegments) {
                val intersection = OritaCalc.determineLineSegmentIntersection(segment, dragSegment, 0.0001)

                when (intersection) {
                    LineSegment.Intersection.INTERSECTS_1,
                    LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_27,
                    LineSegment.Intersection.INTERSECTS_TSHAPE_S2_VERTICAL_BAR_28 -> {
                        intersectingSegments.add(segment)
                    }
                    else -> {
                        // Сегмент не пересекается
                    }
                }
            }
        }
        
        // Сортировать по расстоянию от конечной точки
        return intersectingSegments.sortedBy { segment ->
            val intersectionPoint = OritaCalc.findIntersection(segment, dragSegment)
            dragSegment.getB().distance(intersectionPoint)
        }
    }
    
    private fun alternateColors(intersectingSegments: List<LineSegment>) {
        var alternateColor = getLineColor()
        
        for (segment in intersectingSegments) {
            // Используем метод setLineColor из базового класса
            setLineColor(segment, alternateColor)
            modifiedSegments.add(segment)
            
            // Переключить цвет
            alternateColor = when (alternateColor) {
                LineColor.RED_1 -> LineColor.BLUE_2
                LineColor.BLUE_2 -> LineColor.RED_1
                else -> alternateColor
            }
            
            println("Сегмент (${segment.determineAX()}, ${segment.determineAY()}) -> (${segment.determineBX()}, ${segment.determineBY()}) изменен на цвет: $alternateColor")
        }
    }
    
    override fun reset() {
        anchorPoint = null
        releasePoint = null
        dragSegment = null
        currentStep = CreasesAlternateMVStep.CLICK_DRAG_POINT
        isProcessing = false
        modifiedSegments.clear()
        
        println("Обработчик чередования цветов сгибов сброшен")
    }
    
    fun getCurrentStep(): CreasesAlternateMVStep = currentStep
    
    fun getAnchorPoint(): Point? = anchorPoint
    
    fun getReleasePoint(): Point? = releasePoint
    
    fun getDragSegment(): LineSegment? = dragSegment
    
    fun getModifiedSegments(): List<LineSegment> = modifiedSegments.toList()
    
    fun getModifiedSegmentsCount(): Int = modifiedSegments.size
    
    fun isProcessing(): Boolean = isProcessing
    
    fun clearModifiedSegments() {
        modifiedSegments.clear()
    }
    
    fun getCreasesAlternateDescription(): String {
        return when (currentStep) {
            CreasesAlternateMVStep.CLICK_DRAG_POINT -> {
                if (anchorPoint != null && releasePoint != null) {
                    "Перетащите для выбора области чередования цветов"
                } else if (anchorPoint != null) {
                    "Перетащите для завершения выбора"
                } else {
                    "Нажмите и перетащите для выбора области"
                }
            }
        }
    }
    
    fun getCreasesAlternateInfo(): String {
        return buildString {
            append("Начальная точка: ${anchorPoint?.let { "(${it.x}, ${it.y})" } ?: "не выбрана"}\n")
            append("Конечная точка: ${releasePoint?.let { "(${it.x}, ${it.y})" } ?: "не выбрана"}\n")
            append("Длина сегмента: ${dragSegment?.determineLength() ?: 0.0}\n")
            append("Измененные сегменты: ${modifiedSegments.size}")
        }
    }
    
    override fun getName(): String = "Чередование цветов сгибов"
    
    override fun getDescription(): String = "Чередуйте цвета сгибов в выбранной области"
}

enum class CreasesAlternateMVStep {
    CLICK_DRAG_POINT
} 