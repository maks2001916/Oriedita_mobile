package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Circle
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.StraightLine
import com.example.oriedita_core.origami.crease_pattern.OritaCalc
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Обработчик рисования касательных линий к кругам
 * Адаптированная версия для Android
 */
class MouseHandlerCircleDrawTangentLine : BaseMouseHandler() {
    
    private var closestCircumference = Circle(Point(100000.0, 100000.0), 10.0, LineColor.PURPLE_8)
    private var isProcessing = false
    private var createdLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        val closestCircleMidpoint = getClosestCircleMidpoint(point)
        closestCircumference.set(closestCircleMidpoint)
        
        // Выбрать точку
        if (getLineStepSize() == 0) {
            val closestPoint = getClosestPoint(point)
            if (point.distance(closestPoint) < getSelectionDistance()) {
                val lineSegment = LineSegment(closestPoint, closestPoint, getLineColor())
                addLineStep(lineSegment)
                println("Выбрана точка: (${closestPoint.x}, ${closestPoint.y})")
                return true
            }
        }
        
        // Выбрать круг
        if ((getCircleStepSize() == 0 && getLineStepSize() == 1) ||
            (getCircleStepSize() == 0 && getLineStepSize() == 0) ||
            (getCircleStepSize() == 1 && getLineStepSize() == 0)) {
            
            if (OritaCalc.distance_circumference(point, closestCircumference) <= getSelectionDistance()) {
                // Проверить, что точка не внутри круга
                if (getLineStepSize() > 0) {
                    val selectedPoint = (getLineStep(0) as LineSegment).getA()
                    if (closestCircumference.getR() > OritaCalc.distance(closestCircumference.determineCenter(), selectedPoint)) {
                        clearLineStep()
                        clearCircleStep()
                        println("Точка находится внутри круга, выбор отменен")
                        return true
                    }
                }
                
                val stepCircle = Circle(closestCircumference).apply {
                    color = LineColor.GREEN_6
                }
                addCircleStep(stepCircle)
                println("Выбран круг с центром: (${stepCircle.determineCenter().x}, ${stepCircle.determineCenter().y}), радиус: ${stepCircle.getR()}")
            }
        }
        
        // Выбрать касательную линию
        if (getLineStepSize() > 1) {
            val closestStepLineSegment = getClosestLineStepSegment(point, 1, getLineStepSize())
            
            if (OritaCalc.determineLineSegmentDistance(point, closestStepLineSegment) <= getSelectionDistance()) {
                val extendedLine = OritaCalc.fullExtendUntilHit(getFoldLineSet(), closestStepLineSegment)
                val reversedLine = LineSegment(extendedLine.getB(), extendedLine.getA(), getLineColor())
                val finalLine = OritaCalc.fullExtendUntilHit(getFoldLineSet(), reversedLine)
                
                addLineSegment(finalLine)
                createdLines.add(finalLine)
                println("Добавлена касательная линия")
                
                clearLineStep()
                clearCircleStep()
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // Рисование касательных линий не требует перетаскивания
        return false
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        // Касательные между двумя кругами
        if (getCircleStepSize() == 2 && getLineStepSize() == 0) {
            createTwoCircleTangents()
        }
        
        // Касательные от точки к кругу
        if (getCircleStepSize() == 1 && getLineStepSize() == 1) {
            createPointCircleTangents()
        }
        
        return true
    }
    
    private fun createTwoCircleTangents() {
        val firstCircle = getCircleStep(0) as Circle
        val secondCircle = getCircleStep(1) as Circle
        
        val c1 = firstCircle.determineCenter()
        val c2 = secondCircle.determineCenter()
        
        val x1 = firstCircle.getX()
        val y1 = firstCircle.getY()
        val r1 = firstCircle.getR()
        val x2 = secondCircle.getX()
        val y2 = secondCircle.getY()
        val r2 = secondCircle.getR()
        
        val xp = x2 - x1
        val yp = y2 - y1
        
        if (c1.distance(c2) < 0.000001) {
            clearCircleStep()
            println("Круги совпадают, касательные невозможны")
            return
        }
        
        if ((xp * xp + yp * yp) < (r1 - r2) * (r1 - r2)) {
            clearCircleStep()
            println("Круги не пересекаются, внешние касательные невозможны")
            return
        }
        
        // Создать касательные линии в зависимости от взаимного расположения кругов
        createTangentLines(x1, y1, r1, x2, y2, r2, xp, yp)
    }
    
    private fun createTangentLines(x1: Double, y1: Double, r1: Double, x2: Double, y2: Double, r2: Double, xp: Double, yp: Double) {
        val distanceSquared = xp * xp + yp * yp
        val r1MinusR2Squared = (r1 - r2) * (r1 - r2)
        val r1PlusR2Squared = (r1 + r2) * (r1 + r2)
        
        when {
            abs(distanceSquared - r1MinusR2Squared) < 0.0000001 -> {
                // Одна внешняя касательная
                createSingleExternalTangent(x1, y1, r1, x2, y2, r2, xp, yp)
            }
            r1MinusR2Squared < distanceSquared && distanceSquared < r1PlusR2Squared -> {
                // Две внешние касательные
                createTwoExternalTangents(x1, y1, r1, x2, y2, r2, xp, yp)
            }
            abs(distanceSquared - r1PlusR2Squared) < 0.0000001 -> {
                // Две внешние и одна внутренняя касательная
                createTwoExternalOneInternalTangent(x1, y1, r1, x2, y2, r2, xp, yp)
            }
            r1PlusR2Squared < distanceSquared -> {
                // Две внешние и две внутренние касательные
                createFourTangents(x1, y1, r1, x2, y2, r2, xp, yp)
            }
        }
    }
    
    private fun createSingleExternalTangent(x1: Double, y1: Double, r1: Double, x2: Double, y2: Double, r2: Double, xp: Double, yp: Double) {
        val c1 = Point(x1, y1)
        val c2 = Point(x2, y2)
        val kouten = OritaCalc.internalDivisionRatio(c1, c2, -r1, r2)
        val ty = StraightLine(c1, kouten).orthogonalize(kouten)
        
        val tangentCircle = Circle(kouten, (r1 + r2) / 2.0, LineColor.BLACK_0)
        val tangentLine = OritaCalc.circle_to_straightLine_no_intersect_wo_connect_LineSegment(tangentCircle, ty)
        addLineStep(tangentLine)
        createdLines.add(tangentLine)
        
        println("Создана одна внешняя касательная")
    }
    
    private fun createTwoExternalTangents(x1: Double, y1: Double, r1: Double, x2: Double, y2: Double, r2: Double, xp: Double, yp: Double) {
        val distanceSquared = xp * xp + yp * yp
        val sqrtTerm = sqrt(distanceSquared - (r1 - r2) * (r1 - r2))
        
        val xq1 = r1 * (xp * (r1 - r2) + yp * sqrtTerm) / distanceSquared
        val yq1 = r1 * (yp * (r1 - r2) - xp * sqrtTerm) / distanceSquared
        val xq2 = r1 * (xp * (r1 - r2) - yp * sqrtTerm) / distanceSquared
        val yq2 = r1 * (yp * (r1 - r2) + xp * sqrtTerm) / distanceSquared
        
        val xr1 = xq1 + x1
        val yr1 = yq1 + y1
        val xr2 = xq2 + x1
        val yr2 = yq2 + y1
        
        val t1 = StraightLine(x1, y1, xr1, yr1).orthogonalize(Point(xr1, yr1))
        val t2 = StraightLine(x1, y1, xr2, yr2).orthogonalize(Point(xr2, yr2))
        
        val line1 = LineSegment(Point(xr1, yr1), OritaCalc.findProjection(t1, Point(x2, y2)), LineColor.PURPLE_8)
        val line2 = LineSegment(Point(xr2, yr2), OritaCalc.findProjection(t2, Point(x2, y2)), LineColor.PURPLE_8)
        
        addLineStep(line1)
        addLineStep(line2)
        createdLines.add(line1)
        createdLines.add(line2)
        
        println("Созданы две внешние касательные")
    }
    
    private fun createTwoExternalOneInternalTangent(x1: Double, y1: Double, r1: Double, x2: Double, y2: Double, r2: Double, xp: Double, yp: Double) {
        // Создать две внешние касательные
        createTwoExternalTangents(x1, y1, r1, x2, y2, r2, xp, yp)
        
        // Создать одну внутреннюю касательную
        val c1 = Point(x1, y1)
        val c2 = Point(x2, y2)
        val kouten = OritaCalc.internalDivisionRatio(c1, c2, r1, r2)
        val ty = StraightLine(c1, kouten).orthogonalize(kouten)
        
        val tangentCircle = Circle(kouten, (r1 + r2) / 2.0, LineColor.BLACK_0)
        val originalLine = OritaCalc.circle_to_straightLine_no_intersect_wo_connect_LineSegment(tangentCircle, ty)
        val tangentLine = LineSegment(originalLine.getA(), originalLine.getB(), LineColor.PURPLE_8)
        addLineStep(tangentLine)
        createdLines.add(tangentLine)
        
        println("Созданы две внешние и одна внутренняя касательная")
    }
    
    private fun createFourTangents(x1: Double, y1: Double, r1: Double, x2: Double, y2: Double, r2: Double, xp: Double, yp: Double) {
        val distanceSquared = xp * xp + yp * yp
        val sqrtTerm1 = sqrt(distanceSquared - (r1 - r2) * (r1 - r2))
        val sqrtTerm2 = sqrt(distanceSquared - (r1 + r2) * (r1 + r2))
        
        // Внешние касательные
        val xq1 = r1 * (xp * (r1 - r2) + yp * sqrtTerm1) / distanceSquared
        val yq1 = r1 * (yp * (r1 - r2) - xp * sqrtTerm1) / distanceSquared
        val xq2 = r1 * (xp * (r1 - r2) - yp * sqrtTerm1) / distanceSquared
        val yq2 = r1 * (yp * (r1 - r2) + xp * sqrtTerm1) / distanceSquared
        
        // Внутренние касательные
        val xq3 = r1 * (xp * (r1 + r2) + yp * sqrtTerm2) / distanceSquared
        val yq3 = r1 * (yp * (r1 + r2) - xp * sqrtTerm2) / distanceSquared
        val xq4 = r1 * (xp * (r1 + r2) - yp * sqrtTerm2) / distanceSquared
        val yq4 = r1 * (yp * (r1 + r2) + xp * sqrtTerm2) / distanceSquared
        
        val xr1 = xq1 + x1
        val yr1 = yq1 + y1
        val xr2 = xq2 + x1
        val yr2 = yq2 + y1
        val xr3 = xq3 + x1
        val yr3 = yq3 + y1
        val xr4 = xq4 + x1
        val yr4 = yq4 + y1
        
        val t1 = StraightLine(x1, y1, xr1, yr1).orthogonalize(Point(xr1, yr1))
        val t2 = StraightLine(x1, y1, xr2, yr2).orthogonalize(Point(xr2, yr2))
        val t3 = StraightLine(x1, y1, xr3, yr3).orthogonalize(Point(xr3, yr3))
        val t4 = StraightLine(x1, y1, xr4, yr4).orthogonalize(Point(xr4, yr4))
        
        val line1 = LineSegment(Point(xr1, yr1), OritaCalc.findProjection(t1, Point(x2, y2)), LineColor.PURPLE_8)
        val line2 = LineSegment(Point(xr2, yr2), OritaCalc.findProjection(t2, Point(x2, y2)), LineColor.PURPLE_8)
        val line3 = LineSegment(Point(xr3, yr3), OritaCalc.findProjection(t3, Point(x2, y2)), LineColor.PURPLE_8)
        val line4 = LineSegment(Point(xr4, yr4), OritaCalc.findProjection(t4, Point(x2, y2)), LineColor.PURPLE_8)
        
        addLineStep(line1)
        addLineStep(line2)
        addLineStep(line3)
        addLineStep(line4)
        createdLines.addAll(listOf(line1, line2, line3, line4))
        
        println("Созданы четыре касательные (две внешние и две внутренние)")
    }
    
    private fun createPointCircleTangents() {
        val circle = getCircleStep(0) as Circle
        val point = (getLineStep(0) as LineSegment).getA()
        
        if (abs(circle.getR() - OritaCalc.distance(circle.determineCenter(), point)) < 0.0000001) {
            // Точка на окружности
            val projectionLine = LineSegment(circle.determineCenter(), point)
            val line1 = OritaCalc.fullExtendUntilHit(getFoldLineSet(), 
                LineSegment(point, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, 1.0), point), LineColor.PURPLE_8))
            val line2 = OritaCalc.fullExtendUntilHit(getFoldLineSet(), 
                LineSegment(point, OritaCalc.findProjection(OritaCalc.moveParallel(projectionLine, -1.0), point), LineColor.PURPLE_8))
            
            addLineStep(line1)
            addLineStep(line2)
            createdLines.add(line1)
            createdLines.add(line2)
            
            println("Созданы касательные от точки на окружности")
        } else {
            // Точка вне окружности
            val diameter = LineSegment(point, circle.determineCenter())
            val constructCircle = Circle(diameter.determineAX(), diameter.determineAY(), diameter.determineLength(), LineColor.GREEN_6)
            val connectSegment = OritaCalc.circle_to_circle_no_intersection_wo_musubu_lineSegment(constructCircle, circle)
            
            val line1 = LineSegment(point, connectSegment.getA(), LineColor.PURPLE_8)
            val line2 = LineSegment(point, connectSegment.getB(), LineColor.PURPLE_8)
            
            addLineStep(line1)
            addLineStep(line2)
            createdLines.add(line1)
            createdLines.add(line2)
            
            println("Созданы касательные от точки вне окружности")
        }
    }
    
    override fun getClosestCircleMidpoint(point: Point): Circle {
        // Здесь должна быть логика получения ближайшего круга
        // Пока возвращаем круг с центром в точке и радиусом 1.0
        return Circle(point, 1.0, LineColor.BLACK_0)
    }
    
    override fun getClosestLineStepSegment(point: Point, startIndex: Int, endIndex: Int): LineSegment {
        // Здесь должна быть логика получения ближайшего сегмента линии из шагов
        // Пока возвращаем пустой сегмент
        return LineSegment(Point(0.0, 0.0), Point(1.0, 1.0))
    }
    
    fun getCreatedLines(): List<LineSegment> = createdLines.toList()
    
    fun getCreatedLinesCount(): Int = createdLines.size
    
    fun isProcessing(): Boolean = isProcessing
    
    fun clearCreatedLines() {
        createdLines.clear()
    }
    
    fun getTangentLineDescription(): String {
        return when {
            getCircleStepSize() == 0 && getLineStepSize() == 0 -> "Выберите точку или круг"
            getCircleStepSize() == 1 && getLineStepSize() == 0 -> "Выберите второй круг или точку"
            getCircleStepSize() == 0 && getLineStepSize() == 1 -> "Выберите круг"
            getCircleStepSize() == 2 && getLineStepSize() == 0 -> "Создание касательных между кругами"
            getCircleStepSize() == 1 && getLineStepSize() == 1 -> "Создание касательных от точки к кругу"
            else -> "Неизвестное состояние"
        }
    }
    
    fun getTangentLineInfo(): String {
        return buildString {
            append("Шаги линий: ${getLineStepSize()}\n")
            append("Шаги кругов: ${getCircleStepSize()}\n")
            append("Созданные касательные: ${createdLines.size}")
        }
    }
    
    override fun getName(): String = "Рисование касательных линий к кругам"
    
    override fun getDescription(): String = "Создавайте касательные линии к кругам и между кругами"
} 