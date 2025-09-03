package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.elements.StraightLine
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик рисования рыбьей кости
 * Создает перпендикулярные линии вдоль основной линии
 * Адаптированная версия для Android
 */
class MouseHandlerFishBoneDraw : BaseMouseHandler() {
    
    private var anchorPoint: Point? = null
    private var releasePoint: Point? = null
    private var dragSegment: LineSegment? = null
    private var currentStep = FishBoneDrawStep.CLICK_DRAG_POINT
    private var isProcessing = false
    private var createdLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            FishBoneDrawStep.CLICK_DRAG_POINT -> {
                handleClickDragPointPress(point)
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            FishBoneDrawStep.CLICK_DRAG_POINT -> {
                handleClickDragPointDrag(point)
            }
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            FishBoneDrawStep.CLICK_DRAG_POINT -> {
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
    
    private fun handleClickDragPointRelease(point: Point): FishBoneDrawStep {
        if (releasePoint == null || releasePoint!!.distance(getClosestPoint(releasePoint!!)) > getSelectionDistance()) {
            reset()
            return FishBoneDrawStep.CLICK_DRAG_POINT
        }
        
        if (dragSegment == null || dragSegment!!.determineLength() <= 0.0) {
            reset()
            return FishBoneDrawStep.CLICK_DRAG_POINT
        }
        
        // Создать рыбью кость
        createFishBone()
        
        reset()
        return FishBoneDrawStep.CLICK_DRAG_POINT
    }
    
    private fun createFishBone() {
        if (dragSegment == null) return
        
        val gridWidth = (getGrid()?.getGridSize() ?: 1.0).toDouble()
        val length = dragSegment!!.determineLength()
        
        val dx = (dragSegment!!.determineAX() - dragSegment!!.determineBX()) * gridWidth / length
        val dy = (dragSegment!!.determineAY() - dragSegment!!.determineBY()) * gridWidth / length
        var icolTemp = getLineColor()
        
        val steps = (length / gridWidth).toInt()
        
        for (i in 0..steps) {
            val px = dragSegment!!.determineBX() + i * dx
            val py = dragSegment!!.determineBY() + i * dy
            val pxy = Point(px, py)
            
            if (getClosestLineSegmentDistance(pxy, dragSegment!!) <= 0.0001) {
                continue
            }
            
            var iSen = 0
            
            // Первая перпендикулярная линия
            val adds = LineSegment(Point(px, py), Point(px - dy, py + dx))
            if (koutenAriNasi(adds) == 1) {
                val extendedLine = extendToIntersectionPoint(adds)
                val coloredLine = LineSegment(extendedLine.getA(), extendedLine.getB(), icolTemp)
                addLineSegment(coloredLine)
                createdLines.add(coloredLine)
                iSen++
            }
            
            // Вторая перпендикулярная линия
            val adds2 = LineSegment(Point(px, py), Point(px + dy, py - dx))
            if (koutenAriNasi(adds2) == 1) {
                val extendedLine2 = extendToIntersectionPoint(adds2)
                val coloredLine2 = LineSegment(extendedLine2.getA(), extendedLine2.getB(), icolTemp)
                addLineSegment(coloredLine2)
                createdLines.add(coloredLine2)
                iSen++
            }
            
            // Удалить вершину, если созданы две линии
            if (iSen == 2) {
                // Заглушка для удаления вершины
                println("Вершина в точке (${pxy.x}, ${pxy.y}) должна быть удалена")
            }
            
            // Переключить цвет
            icolTemp = when (icolTemp) {
                LineColor.RED_1 -> LineColor.BLUE_2
                LineColor.BLUE_2 -> LineColor.RED_1
                else -> icolTemp
            }
        }
        
        println("Создана рыбья кость с ${createdLines.size} линиями")
    }
    
    /**
     * Проверить, есть ли пересечения у линии
     */
    private fun koutenAriNasi(lineSegment: LineSegment): Int {
        val addLine = LineSegment(lineSegment)
        val tyoku1 = StraightLine(addLine.getA(), addLine.getB())
        
        for (ls in getFoldLineSet()?.getLineSegmentsIterable() ?: emptyList()) {
            val intersectionFlag = tyoku1.lineSegment_intersect_reverse_detail(ls)
            if (!intersectionFlag.isIntersecting()) continue
            
            val intersectionPoint = OritaCalc.findIntersection(tyoku1, ls)
            if (OritaCalc.distance(intersectionPoint, addLine.getA()) <= 0.00001) continue
            
            val dKakudo = OritaCalc.angle(addLine.getA(), addLine.getB(), addLine.getA(), intersectionPoint)
            if (dKakudo < 1.0 || dKakudo > 359.0) return 1
        }
        
        return 0
    }
    
    override fun reset() {
        anchorPoint = null
        releasePoint = null
        dragSegment = null
        currentStep = FishBoneDrawStep.CLICK_DRAG_POINT
        isProcessing = false
        createdLines.clear()
        
        println("Обработчик рыбьей кости сброшен")
    }
    
    fun getCurrentStep(): FishBoneDrawStep = currentStep
    
    fun getAnchorPoint(): Point? = anchorPoint
    
    fun getReleasePoint(): Point? = releasePoint
    
    fun getDragSegment(): LineSegment? = dragSegment
    
    fun getCreatedLines(): List<LineSegment> = createdLines.toList()
    
    fun getCreatedLinesCount(): Int = createdLines.size
    
    fun isProcessing(): Boolean = isProcessing
    
    fun clearCreatedLines() {
        createdLines.clear()
    }
    
    fun getFishBoneDescription(): String {
        return when (currentStep) {
            FishBoneDrawStep.CLICK_DRAG_POINT -> {
                if (anchorPoint != null && releasePoint != null) {
                    "Перетащите для завершения рыбьей кости"
                } else if (anchorPoint != null) {
                    "Выберите конечную точку основной линии"
                } else {
                    "Выберите начальную точку основной линии"
                }
            }
        }
    }
    
    fun getFishBoneInfo(): String {
        return buildString {
            append("Начальная точка: ${anchorPoint?.let { "(${it.x}, ${it.y})" } ?: "не выбрана"}\n")
            append("Конечная точка: ${releasePoint?.let { "(${it.x}, ${it.y})" } ?: "не выбрана"}\n")
            append("Длина сегмента: ${dragSegment?.determineLength() ?: 0.0}\n")
            append("Созданные линии: ${createdLines.size}")
        }
    }
    
    /**
     * Получить расстояние до ближайшего сегмента линии
     */
    private fun getClosestLineSegmentDistance(point: Point, excludeLine: LineSegment): Double {
        val foldLineSet = getFoldLineSet()
        if (foldLineSet != null) {
            var minDistance = Double.MAX_VALUE
            val allLines = foldLineSet.getLineSegmentsIterable()
            
            for (line in allLines) {
                if (line == excludeLine) continue

                // Вычисляем расстояние от точки до ближайшей точки на линии
                val distanceToA = OritaCalc.distance(point, line.getA())
                val distanceToB = OritaCalc.distance(point, line.getB())
                val distance = minOf(distanceToA, distanceToB)

                if (distance < minDistance) {
                    minDistance = distance
                }
            }
            
            return if (minDistance == Double.MAX_VALUE) 0.0 else minDistance
        }
        return 0.0
    }
    
    /**
     * Расширить линию до точки пересечения
     */
    override fun extendToIntersectionPoint(line: LineSegment): LineSegment {
        // Заглушка - возвращаем исходную линию
        // В реальной реализации здесь должна быть логика расширения до пересечения
        return line
    }
    
    override fun getName(): String = "Рисование рыбьей кости"
    
    override fun getDescription(): String = "Создавайте перпендикулярные линии вдоль основной линии"
}

enum class FishBoneDrawStep {
    CLICK_DRAG_POINT
} 