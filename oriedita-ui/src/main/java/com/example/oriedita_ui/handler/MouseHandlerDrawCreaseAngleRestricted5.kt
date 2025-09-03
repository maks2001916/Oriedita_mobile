package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик рисования линий с ограничением угла 5
 * Создает линии с привязкой к активной угловой системе
 * Адаптированная версия для Android
 */
class MouseHandlerDrawCreaseAngleRestricted5 : BaseMouseHandlerInputRestricted() {
    
    private var start: Point? = null
    private var angleSystemDivider = 4
    private var isProcessing = false
    private var createdLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        start = getClosestPoint(point)
        
        if (point.distance(start!!) > getSelectionDistance()) {
            return false
        }
        
        val lineSegment = LineSegment(point, start!!, getLineColor()).apply {
            setActive(LineSegment.ActiveState.ACTIVE_B_2)
        }
        
        addLineStep(lineSegment)
        println("Начало рисования линии с ограничением угла: (${start!!.x}, ${start!!.y})")
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (getLineStepSize() == 1) {
            val syuuseiPoint = syuuseiPointA37(offset)
            
            val gapLine = getLineStep(0)
            val updatedLine = LineSegment(syuuseiPoint, gapLine.b, gapLine.color)

            setLineStep(0, updatedLine)
            setLineColor(updatedLine, getLineColor())
            
            if (isGridInputAssistEnabled()) {
                clearLineCandidate()
                val candidatePoint = kouhoPointA37(syuuseiPoint)
                val candidate = LineSegment(candidatePoint, candidatePoint, getLineColor()).apply {
                    setActive(LineSegment.ActiveState.ACTIVE_BOTH_3)
                }
                addLineCandidate(candidate)
                setLineStep(0, LineSegment(candidatePoint, updatedLine.b, updatedLine.color))
            }
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (getLineStepSize() == 1) {
            val syuuseiPoint = syuuseiPointA37(offset)
            val gapLine = getLineStep(0)
            val finalLine = LineSegment( kouhoPointA37(syuuseiPoint), gapLine.b, gapLine.color)
            
            if (finalLine.determineLength() > 0.0) {
                addLineSegment(finalLine)
                createdLines.add(finalLine)
                println("Создана линия с ограничением угла: (${finalLine.determineAX()}, ${finalLine.determineAY()}) -> (${finalLine.determineBX()}, ${finalLine.determineBY()})")
            }
            
            clearLineStep()
        }
        
        return true
    }
    
    /**
     * Корректировка точки A для режима 37
     */
    private fun syuuseiPointA37(offset: Offset): Point {
        val point = offsetToPoint(offset)
        return snapToActiveAngleSystem(start!!, point)
    }
    
    /**
     * Кандидат точка A для режима 37
     */
    private fun kouhoPointA37(syuuseiPoint: Point): Point {
        val closestPoint = getClosestPoint(syuuseiPoint)
        val zureKakudo = OritaCalc.angle(
            (getLineStep(0) as LineSegment).getB(),
            syuuseiPoint,
            (getLineStep(0) as LineSegment).getB(),
            closestPoint
        )
        
        val zureFlg = (0.00001 < zureKakudo) && (zureKakudo <= 360.0 - 0.00001)
        
        return if (zureFlg || (syuuseiPoint.distance(closestPoint) > getSelectionDistance())) {
            syuuseiPoint
        } else {
            // Ближайшая точка находится на угловой системе и близка к корректировочной точке
            closestPoint
        }
    }
    
    /**
     * Привязка к активной угловой системе
     */
    private fun snapToActiveAngleSystem(startPoint: Point, targetPoint: Point): Point {
        // Здесь должна быть логика привязки к угловой системе
        // Пока возвращаем целевую точку
        return targetPoint
    }
    
    fun setAngleSystemDivider(divider: Int) {
        angleSystemDivider = divider
        println("Установлен делитель угловой системы: $divider")
    }
    
    fun getAngleSystemDivider(): Int = angleSystemDivider
    
    fun getStartPoint(): Point? = start
    
    fun getCreatedLines(): List<LineSegment> = createdLines.toList()
    
    fun getCreatedLinesCount(): Int = createdLines.size
    
    fun isProcessing(): Boolean = isProcessing
    
    fun clearCreatedLines() {
        createdLines.clear()
    }
    
    fun getAngleRestricted5Description(): String {
        return when (getLineStepSize()) {
            0 -> "Выберите начальную точку"
            1 -> "Перетащите для создания линии с ограничением угла"
            else -> "Линия создана"
        }
    }
    
    fun getAngleRestricted5Info(): String {
        return buildString {
            append("Начальная точка: ${start?.let { "(${it.x}, ${it.y})" } ?: "не выбрана"}\n")
            append("Делитель угловой системы: $angleSystemDivider\n")
            append("Шагов линий: ${getLineStepSize()}\n")
            append("Созданные линии: ${createdLines.size}")
        }
    }
    
    override fun getName(): String = "Рисование линий с ограничением угла 5"
    
    override fun getDescription(): String = "Создавайте линии с привязкой к активной угловой системе"
} 