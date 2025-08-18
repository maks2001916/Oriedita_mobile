package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Circle
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc
import kotlin.math.abs

/**
 * Обработчик выбора двух концентрических кругов
 * Адаптированная версия для Android
 */
class MouseHandlerCircleDrawConcentricTwoCircleSelect : BaseMouseHandler() {
    
    private var circle1: Circle? = null
    private var circle2: Circle? = null
    private var currentStep = CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_1
    private var isProcessing = false
    private var createdCircles = mutableListOf<Circle>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_1 -> {
                handleSelectCircle1Press(point)
            }
            CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_2 -> {
                handleSelectCircle2Press(point)
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_1 -> {
                handleSelectCircle1Drag(point)
            }
            CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_2 -> {
                handleSelectCircle2Drag(point)
            }
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_1 -> {
                currentStep = handleSelectCircle1Release(point)
            }
            CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_2 -> {
                currentStep = handleSelectCircle2Release(point)
            }
        }
        
        return true
    }
    
    private fun handleSelectCircle1Press(point: Point) {
        handleSelectCircle1Drag(point)
    }
    
    private fun handleSelectCircle1Drag(point: Point) {
        val closestCircleMidpoint = getClosestCircleMidpoint(point)
        if (OritaCalc.distance_circumference(point, closestCircleMidpoint) < getSelectionDistance()) {
            circle1 = Circle(closestCircleMidpoint).apply {
                color = LineColor.GREEN_6
            }
        } else {
            circle1 = null
        }
    }
    
    private fun handleSelectCircle1Release(point: Point): CircleDrawConcentricTwoCircleSelectStep {
        if (circle1 == null) return CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_1
        
        println("Выбран первый круг с центром: (${circle1!!.determineCenter().x}, ${circle1!!.determineCenter().y}), радиус: ${circle1!!.getR()}")
        return CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_2
    }
    
    private fun handleSelectCircle2Press(point: Point) {
        handleSelectCircle2Drag(point)
    }
    
    private fun handleSelectCircle2Drag(point: Point) {
        val closestCircleMidpoint = getClosestCircleMidpoint(point)
        if (OritaCalc.distance_circumference(point, closestCircleMidpoint) < getSelectionDistance()) {
            // Проверить, что второй круг отличается от первого
            if (abs(circle1!!.getR() - closestCircleMidpoint.getR()) < 0.000001 &&
                circle1!!.determineCenter() == closestCircleMidpoint.determineCenter()) {
                circle2 = null
            } else {
                circle2 = Circle(closestCircleMidpoint).apply {
                    color = LineColor.ORANGE_4
                }
            }
        } else {
            circle2 = null
        }
    }
    
    private fun handleSelectCircle2Release(point: Point): CircleDrawConcentricTwoCircleSelectStep {
        if (circle2 == null) return CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_2
        
        // Создать концентрические круги
        createConcentricCircles()
        
        reset()
        return CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_1
    }
    
    private fun createConcentricCircles() {
        if (circle1 == null || circle2 == null) return
        
        val centerLineLength = OritaCalc.distance(circle1!!.determineCenter(), circle2!!.determineCenter())
        val concentricOffset = (centerLineLength - circle1!!.getR() - circle2!!.getR()) / 2.0
        
        // Создать новые концентрические круги
        val newCircle1 = Circle(circle1!!.determineCenter(), circle1!!.getR() + concentricOffset, LineColor.CYAN_3)
        val newCircle2 = Circle(circle2!!.determineCenter(), circle2!!.getR() + concentricOffset, LineColor.CYAN_3)
        
        addCircle(newCircle1)
        addCircle(newCircle2)
        
        createdCircles.add(newCircle1)
        createdCircles.add(newCircle2)
        
        println("Созданы концентрические круги:")
        println("Круг 1: центр (${newCircle1.determineCenter().x}, ${newCircle1.determineCenter().y}), радиус ${newCircle1.getR()}")
        println("Круг 2: центр (${newCircle2.determineCenter().x}, ${newCircle2.determineCenter().y}), радиус ${newCircle2.getR()}")
    }
    
    override fun getClosestCircleMidpoint(point: Point): Circle {
        // Здесь должна быть логика получения ближайшего круга
        // Пока возвращаем круг с центром в точке и радиусом 1.0
        return Circle(point, 1.0, LineColor.BLACK_0)
    }
    
    override fun reset() {
        circle1 = null
        circle2 = null
        currentStep = CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_1
        isProcessing = false
        
        println("Обработчик двух концентрических кругов сброшен")
    }
    
    fun getCurrentStep(): CircleDrawConcentricTwoCircleSelectStep = currentStep
    
    fun getCircle1(): Circle? = circle1
    
    fun getCircle2(): Circle? = circle2
    
    fun getCreatedCircles(): List<Circle> = createdCircles.toList()
    
    fun getCreatedCirclesCount(): Int = createdCircles.size
    
    fun isProcessing(): Boolean = isProcessing
    
    fun clearCreatedCircles() {
        createdCircles.clear()
    }
    
    fun getTwoCircleSelectDescription(): String {
        return when (currentStep) {
            CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_1 -> {
                if (circle1 != null) {
                    "Выбран первый круг, выберите второй"
                } else {
                    "Выберите первый круг"
                }
            }
            CircleDrawConcentricTwoCircleSelectStep.SELECT_CIRCLE_2 -> {
                if (circle2 != null) {
                    "Выбран второй круг, создание концентрических кругов"
                } else {
                    "Выберите второй круг"
                }
            }
        }
    }
    
    fun getCirclesInfo(): String {
        return buildString {
            append("Первый круг: ")
            if (circle1 != null) {
                append("центр (${circle1!!.determineCenter().x}, ${circle1!!.determineCenter().y}), ")
                append("радиус ${circle1!!.getR()}")
            } else {
                append("не выбран")
            }
            append("\nВторой круг: ")
            if (circle2 != null) {
                append("центр (${circle2!!.determineCenter().x}, ${circle2!!.determineCenter().y}), ")
                append("радиус ${circle2!!.getR()}")
            } else {
                append("не выбран")
            }
            append("\nСоздано кругов: ${createdCircles.size}")
        }
    }
    
    override fun getName(): String = "Выбор двух концентрических кругов"
    
    override fun getDescription(): String = "Выбирайте два круга для создания концентрических кругов"
}

enum class CircleDrawConcentricTwoCircleSelectStep {
    SELECT_CIRCLE_1,
    SELECT_CIRCLE_2
} 