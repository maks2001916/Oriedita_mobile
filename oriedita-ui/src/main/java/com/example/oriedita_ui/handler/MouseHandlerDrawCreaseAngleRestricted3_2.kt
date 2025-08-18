package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик рисования линий с ограничением угла 3/2
 * Создает линии под определенными углами с цветовой схемой
 * Адаптированная версия для Android
 */
class MouseHandlerDrawCreaseAngleRestricted3_2 : BaseMouseHandlerInputRestricted() {
    
    private var angleSystemDivider = 4
    private var angleSystem = 45.0
    private var isProcessing = false
    private var createdLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        // Обработка движения мыши для показа кандидатов
        if (getLineStepSize() <= 1) {
            handleMouseMoved(offset)
        }
        
        val honsuu = if (angleSystemDivider != 0) {
            angleSystemDivider * 2 - 1
        } else {
            6
        }
        
        var kakudo: Double
        
        if (getLineStepSize() == 0 || getLineStepSize() == 1) {
            val closestPoint = getClosestPoint(point)
            if (point.distance(closestPoint) < getSelectionDistance()) {
                val lineSegment = LineSegment(closestPoint, closestPoint, LineColor.fromNumber(getLineStepSize() + 1))
                addLineStep(lineSegment)
                println("Добавлена точка ${getLineStepSize()}: (${closestPoint.x}, ${closestPoint.y})")
            }
        }
        
        if (getLineStepSize() == 2) {
            // Создать линии под углами
            if (angleSystemDivider != 0) {
                angleSystem = 180.0 / angleSystemDivider
            } else {
                angleSystem = 180.0 / 4.0
            }
            
            if (angleSystemDivider != 0) {
                createAngleSystemLines()
            } else {
                createCustomAngleLines()
            }
            
            return true
        }
        
        if (getLineStepSize() == 2 + honsuu) {
            // Выбрать линию и создать сгиб
            selectAndCreateCrease(point)
            return true
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // Этот обработчик не требует перетаскивания
        return false
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        // Этот обработчик не требует отпускания
        return true
    }
    
    private fun createAngleSystemLines() {
        val sKiso = LineSegment(
            (getLineStep(1) as LineSegment).getA(),
            (getLineStep(0) as LineSegment).getA()
        )
        
        var kakudo = 0.0
        var iJyun = false
        
        val honsuu = angleSystemDivider * 2 - 1
        
        for (i in 1..honsuu) {
            iJyun = !iJyun
            kakudo += angleSystem
            
            val s = OritaCalc.lineSegment_rotate(sKiso, kakudo, 100.0)
            val coloredLine = if (iJyun) {
                LineSegment(s.a, s.b, LineColor.ORANGE_4)
            } else {
                LineSegment(s.a, s.b, LineColor.GREEN_6)
            }
            
            addLineStep(coloredLine)
            createdLines.add(coloredLine)
            
            println("Создана линия под углом ${kakudo}°: (${coloredLine.determineAX()}, ${coloredLine.determineAY()}) -> (${coloredLine.determineBX()}, ${coloredLine.determineBY()})")
        }
    }
    
    private fun createCustomAngleLines() {
        val sKiso = LineSegment(
            (getLineStep(1) as LineSegment).getA(),
            (getLineStep(0) as LineSegment).getA()
        )
        
        val angles = getCustomAngles()
        
        for (i in 0..5) {
            val kakudo = angles[i]
            val s = OritaCalc.lineSegment_rotate(sKiso, kakudo, 100.0)
            
            val coloredLine = when (i) {
                0 -> LineSegment(s.a, s.b, LineColor.ORANGE_4)
                1 -> LineSegment(s.a, s.b, LineColor.GREEN_6)
                2 -> LineSegment(s.a, s.b, LineColor.PURPLE_8)
                3 -> LineSegment(s.a, s.b, LineColor.ORANGE_4)
                4 -> LineSegment(s.a, s.b, LineColor.GREEN_6)
                5 -> LineSegment(s.a, s.b, LineColor.PURPLE_8)
                else -> LineSegment(s.a, s.b, LineColor.BLACK_0)
            }
            
            addLineStep(coloredLine)
            createdLines.add(coloredLine)
            
            println("Создана линия под углом ${kakudo}°: (${coloredLine.determineAX()}, ${coloredLine.determineAY()}) -> (${coloredLine.determineBX()}, ${coloredLine.determineBY()})")
        }
    }
    
    private fun selectAndCreateCrease(point: Point) {
        val closestStepLineSegment = getClosestLineStepSegment(point, 3, 2 + (angleSystemDivider * 2 - 1))
        
        if (OritaCalc.determineLineSegmentDistance(point, closestStepLineSegment) >= getSelectionDistance()) {
            clearLineStep()
            return
        }
        
        val mokuhyouPoint = OritaCalc.findProjection(closestStepLineSegment, point)
        
        val closestLineSegment = getClosestLineSegment(point)
        if (OritaCalc.determineLineSegmentDistance(point, closestLineSegment) < getSelectionDistance()) {
            if (OritaCalc.isLineSegmentParallel(closestStepLineSegment, closestLineSegment, 0.000001) == OritaCalc.ParallelJudgement.NOT_PARALLEL) {
                val mokuhyouPoint2 = OritaCalc.findIntersection(closestStepLineSegment, closestLineSegment)
                if (point.distance(mokuhyouPoint) * 2.0 > point.distance(mokuhyouPoint2)) {
                    val addSen = LineSegment(mokuhyouPoint2, (getLineStep(1) as LineSegment).getA(), getLineColor())
                    addLineSegment(addSen)
                    createdLines.add(addSen)
                    
                    println("Создан сгиб: (${addSen.determineAX()}, ${addSen.determineAY()}) -> (${addSen.determineBX()}, ${addSen.determineBY()})")
                }
            }
        } else {
            val addSen = LineSegment(mokuhyouPoint, (getLineStep(1) as LineSegment).getA(), getLineColor())
            addLineSegment(addSen)
            createdLines.add(addSen)
            
            println("Создан сгиб: (${addSen.determineAX()}, ${addSen.determineAY()}) -> (${addSen.determineBX()}, ${addSen.determineBY()})")
        }
        
        clearLineStep()
    }
    
    private fun getCustomAngles(): DoubleArray {
        // Возвращает массив из 6 углов для пользовательской системы
        return doubleArrayOf(0.0, 30.0, 60.0, 90.0, 120.0, 150.0)
    }
    
    fun setAngleSystemDivider(divider: Int) {
        angleSystemDivider = divider
        angleSystem = if (divider != 0) 180.0 / divider else 45.0
        println("Установлен делитель угловой системы: $divider, угол: ${angleSystem}°")
    }
    
    fun getAngleSystemDivider(): Int = angleSystemDivider
    
    fun getAngleSystem(): Double = angleSystem
    
    fun getCreatedLines(): List<LineSegment> = createdLines.toList()
    
    fun getCreatedLinesCount(): Int = createdLines.size
    
    fun isProcessing(): Boolean = isProcessing
    
    fun clearCreatedLines() {
        createdLines.clear()
    }
    
    fun getAngleRestrictedDescription(): String {
        return when (getLineStepSize()) {
            0 -> "Выберите первую точку"
            1 -> "Выберите вторую точку"
            2 -> "Создание линий под углами"
            else -> "Выберите линию для создания сгиба"
        }
    }
    
    fun getAngleRestrictedInfo(): String {
        return buildString {
            append("Делитель угловой системы: $angleSystemDivider\n")
            append("Угол системы: ${String.format("%.2f", angleSystem)}°\n")
            append("Шагов линий: ${getLineStepSize()}\n")
            append("Созданные линии: ${createdLines.size}")
        }
    }
    
    override fun getName(): String = "Рисование линий с ограничением угла 3/2"
    
    override fun getDescription(): String = "Создавайте линии под определенными углами с цветовой схемой"
} 