package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.elements.StraightLine
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик непрерывного симметричного рисования
 * Адаптированная версия для Android
 */
class MouseHandlerContinuousSymmetricDraw : BaseMouseHandler() {
    
    private var p1: Point? = null
    private var p2: Point? = null
    private var currentStep = ContinuousSymmetricDrawStep.SELECT_P1
    private var isProcessing = false
    private var resultantSegments = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            ContinuousSymmetricDrawStep.SELECT_P1 -> {
                handleSelectP1Press(point)
            }
            ContinuousSymmetricDrawStep.SELECT_P2 -> {
                handleSelectP2Press(point)
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            ContinuousSymmetricDrawStep.SELECT_P1 -> {
                handleSelectP1Drag(point)
            }
            ContinuousSymmetricDrawStep.SELECT_P2 -> {
                handleSelectP2Drag(point)
            }
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            ContinuousSymmetricDrawStep.SELECT_P1 -> {
                currentStep = handleSelectP1Release(point)
            }
            ContinuousSymmetricDrawStep.SELECT_P2 -> {
                currentStep = handleSelectP2Release(point)
            }
        }
        
        return true
    }
    
    private fun handleSelectP1Press(point: Point) {
        handleSelectP1Drag(point)
    }
    
    private fun handleSelectP1Drag(point: Point) {
        p1 = point
        val closestPoint = getClosestPoint(point)
        if (point.distance(closestPoint) < getSelectionDistance()) {
            p1 = closestPoint
        }
    }
    
    private fun handleSelectP1Release(point: Point): ContinuousSymmetricDrawStep {
        println("Выбрана первая точка: ${p1?.let { "(${it.x}, ${it.y})" } ?: "не выбрана"}")
        return ContinuousSymmetricDrawStep.SELECT_P2
    }
    
    private fun handleSelectP2Press(point: Point) {
        handleSelectP2Drag(point)
    }
    
    private fun handleSelectP2Drag(point: Point) {
        p2 = point
        val closestPoint = getClosestPoint(point)
        if (point.distance(closestPoint) < getSelectionDistance()) {
            p2 = closestPoint
        }
    }
    
    private fun handleSelectP2Release(point: Point): ContinuousSymmetricDrawStep {
        if (p1 != null && p2 != null) {
            continuousFoldingNew(p1!!, p2!!, null)
            
            var lineType = getLineColor()
            for (segment in resultantSegments) {
                val lineSegment = LineSegment(segment.getA(), segment.getB(), lineType)
                lineType = lineType.changeMV()
                addLineSegment(lineSegment)
            }
            
            println("Создано ${resultantSegments.size} симметричных линий")
        }
        
        reset()
        return ContinuousSymmetricDrawStep.SELECT_P1
    }
    
    private fun continuousFoldingNew(a: Point, b: Point, start: Point?) {
        // Улучшенная версия непрерывного складывания
        
        // Найти ближайшую точку пересечения
        val intersectionInfo = findNearestIntersection(a, b)
        if (intersectionInfo.intersectionType == StraightLine.Intersection.NONE_0) {
            return // Нет пересечений
        }
        
        val s = LineSegment(intersectionInfo.lineSegment)
        resultantSegments.add(s)
        s.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3)
        
        if (start != null && start.distance(s.getB()) < 0.000001) {
            return
        }
        
        if (intersectionInfo.firstLineSegment.color == LineColor.BLACK_0) {
            return // Остановиться при достижении края бумаги
        }
        
        val newStart = start ?: s.getB()
        
        // Обработать пересечение в зависимости от типа
        when (intersectionInfo.intersectionType) {
            StraightLine.Intersection.INTERSECT_X_1 -> {
                handleXIntersection(a, b, intersectionInfo, newStart)
            }
            StraightLine.Intersection.INTERSECT_T_A_21,
            StraightLine.Intersection.INTERSECT_T_B_22 -> {
                handleTIntersection(a, b, intersectionInfo, newStart)
            }
            else -> {
                // Другие типы пересечений не обрабатываются
            }
        }
    }
    
    private fun handleXIntersection(a: Point, b: Point, intersectionInfo: IntersectionInfo, start: Point) {
        val kousatenMadeNobasiSaisyonoLineSegment = LineSegment(intersectionInfo.firstLineSegment)
        val newA = intersectionInfo.intersectionPoint
        val newB = OritaCalc.findLineSymmetryPoint(
            kousatenMadeNobasiSaisyonoLineSegment.getA(),
            kousatenMadeNobasiSaisyonoLineSegment.getB(),
            a
        )
        
        continuousFoldingNew(newA, newB, start)
    }
    
    private fun handleTIntersection(a: Point, b: Point, intersectionInfo: IntersectionInfo, start: Point) {
        val tyoku1 = StraightLine(a, b)
        val surroundingLines = getSurroundingFoldLines(intersectionInfo.lineSegment)
        
        when (surroundingLines.size) {
            2 -> handleTwoLineIntersection(tyoku1, surroundingLines, a, intersectionInfo, start)
            3 -> handleThreeLineIntersection(tyoku1, surroundingLines, a, intersectionInfo, start)
        }
    }
    
    private fun handleTwoLineIntersection(
        tyoku1: StraightLine,
        surroundingLines: List<LineSegment>,
        a: Point,
        intersectionInfo: IntersectionInfo,
        start: Point
    ) {
        // Проверить, что ни одна из линий не включена в прямую
        for (line in surroundingLines) {
            val intersection = tyoku1.lineSegment_intersect_reverse_detail(line)
            if (intersection == StraightLine.Intersection.INCLUDED_3) {
                return
            }
        }
        
        // Проверить, что линии не параллельны
        val tyoku2 = StraightLine(surroundingLines[0])
        val intersection = tyoku2.lineSegment_intersect_reverse_detail(surroundingLines[1])
        if (intersection == StraightLine.Intersection.INCLUDED_3) {
            createSymmetricalLine(a, intersectionInfo, start)
        }
    }
    
    private fun handleThreeLineIntersection(
        tyoku1: StraightLine,
        surroundingLines: List<LineSegment>,
        a: Point,
        intersectionInfo: IntersectionInfo,
        start: Point
    ) {
        // Проверить различные комбинации линий
        for (i in surroundingLines.indices) {
            val intersection = tyoku1.lineSegment_intersect_reverse_detail(surroundingLines[i])
            if (intersection == StraightLine.Intersection.INCLUDED_3) {
                val otherLines = surroundingLines.filterIndexed { index, _ -> index != i }
                val tyoku2 = StraightLine(otherLines[0])
                val otherIntersection = tyoku2.lineSegment_intersect_reverse_detail(otherLines[1])
                if (otherIntersection == StraightLine.Intersection.INCLUDED_3) {
                    createSymmetricalLine(a, intersectionInfo, start)
                    return
                }
            }
        }
    }
    
    private fun createSymmetricalLine(a: Point, intersectionInfo: IntersectionInfo, start: Point) {
        val kousatenMadeNobasiSaisyonoLineSegment = LineSegment(intersectionInfo.firstLineSegment)
        val newA = intersectionInfo.intersectionPoint
        val newB = OritaCalc.findLineSymmetryPoint(
            kousatenMadeNobasiSaisyonoLineSegment.getA(),
            kousatenMadeNobasiSaisyonoLineSegment.getB(),
            a
        )
        
        continuousFoldingNew(newA, newB, start)
    }
    
    private fun findNearestIntersection(a: Point, b: Point): IntersectionInfo {
        // Здесь должна быть логика поиска ближайшего пересечения
        // Пока возвращаем пустую информацию
        return IntersectionInfo(
            intersectionType = StraightLine.Intersection.NONE_0,
            intersectionPoint = Point(0.0, 0.0),
            lineSegment = LineSegment(Point(0.0, 0.0), Point(1.0, 1.0)),
            firstLineSegment = LineSegment(Point(0.0, 0.0), Point(1.0, 1.0))
        )
    }
    
    private fun getSurroundingFoldLines(lineSegment: LineSegment): List<LineSegment> {
        // Здесь должна быть логика получения окружающих линий сгиба
        // Пока возвращаем пустой список
        return emptyList()
    }
    
    override fun reset() {
        p1 = null
        p2 = null
        currentStep = ContinuousSymmetricDrawStep.SELECT_P1
        isProcessing = false
        resultantSegments.clear()
        
        println("Обработчик непрерывного симметричного рисования сброшен")
    }
    
    fun getCurrentStep(): ContinuousSymmetricDrawStep = currentStep
    
    fun getP1(): Point? = p1
    
    fun getP2(): Point? = p2
    
    fun getResultantSegments(): List<LineSegment> = resultantSegments.toList()
    
    fun getResultantSegmentsCount(): Int = resultantSegments.size
    
    fun isProcessing(): Boolean = isProcessing
    
    fun clearResultantSegments() {
        resultantSegments.clear()
    }
    
    fun getContinuousSymmetricDescription(): String {
        return when (currentStep) {
            ContinuousSymmetricDrawStep.SELECT_P1 -> {
                if (p1 != null) {
                    "Выбрана первая точка, выберите вторую"
                } else {
                    "Выберите первую точку"
                }
            }
            ContinuousSymmetricDrawStep.SELECT_P2 -> {
                if (p2 != null) {
                    "Выбрана вторая точка, создание симметричных линий"
                } else {
                    "Выберите вторую точку"
                }
            }
        }
    }
    
    fun getContinuousSymmetricInfo(): String {
        return buildString {
            append("Первая точка: ${p1?.let { "(${it.x}, ${it.y})" } ?: "не выбрана"}\n")
            append("Вторая точка: ${p2?.let { "(${it.y}, ${it.y})" } ?: "не выбрана"}\n")
            append("Созданные сегменты: ${resultantSegments.size}")
        }
    }
    
    override fun getName(): String = "Непрерывное симметричное рисование"
    
    override fun getDescription(): String = "Создавайте непрерывные симметричные линии сгиба"
}

enum class ContinuousSymmetricDrawStep {
    SELECT_P1,
    SELECT_P2
}

data class IntersectionInfo(
    val intersectionType: StraightLine.Intersection,
    val intersectionPoint: Point,
    val lineSegment: LineSegment,
    val firstLineSegment: LineSegment
) 