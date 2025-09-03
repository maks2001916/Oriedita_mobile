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

/**
 * Обработчик рисования инвертированных кругов
 * Адаптированная версия для Android
 */
class MouseHandlerCircleDrawInverted : BaseMouseHandler() {
    
    private var selectedElements = mutableListOf<Any>()
    private var isProcessing = false
    private var createdElements = mutableListOf<Any>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        val closestCircumference = Circle(Point(100000.0, 100000.0), 10.0, LineColor.PURPLE_8)
        val closestCircleMidpoint = getClosestCircleMidpoint(point)
        closestCircumference.set(closestCircleMidpoint)
        
        when {
            getLineStepSize() + getCircleStepSize() == 0 -> {
                handleFirstSelection(point, closestCircumference)
            }
            getLineStepSize() + getCircleStepSize() == 1 -> {
                handleSecondSelection(point, closestCircumference)
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // Рисование инвертированных кругов не требует перетаскивания
        return false
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        when {
            getLineStepSize() == 1 && getCircleStepSize() == 1 -> {
                val line = getLineStep(0) as LineSegment
                val circle = getCircleStep(0) as Circle
                addInversion(line, circle)
                clearSteps()
            }
            getLineStepSize() == 0 && getCircleStepSize() == 2 -> {
                val circle1 = getCircleStep(0) as Circle
                val circle2 = getCircleStep(1) as Circle
                addInversion(circle1, circle2)
                clearSteps()
            }
        }
        
        return true
    }
    
    private fun handleFirstSelection(point: Point, closestCircumference: Circle) {
        val closestLineSegment = getClosestLineSegment(point)
        
        if (closestLineSegment != null) {
            val coloredLineSegment = LineSegment(closestLineSegment.getA(), closestLineSegment.getB(), LineColor.GREEN_6)
            
            if (OritaCalc.determineLineSegmentDistance(point, coloredLineSegment) < OritaCalc.distance_circumference(point, closestCircumference)) {
                // Линия ближе, чем окружность
                if (OritaCalc.determineLineSegmentDistance(point, coloredLineSegment) <= getSelectionDistance()) {
                    addLineStep(coloredLineSegment)
                    selectedElements.add(coloredLineSegment)
                    println("Выбрана линия для инверсии")
                }
            } else {
                // Окружность ближе, чем линия
                clearLineStep()
                if (OritaCalc.distance_circumference(point, closestCircumference) <= getSelectionDistance()) {
                    val circle = Circle(closestCircumference.determineCenter(), closestCircumference.getR(), LineColor.GREEN_6)
                    addCircleStep(circle)
                    selectedElements.add(circle)
                    println("Выбран круг для инверсии")
                }
            }
        } else {
            // Линия не найдена, выбираем только окружность
            clearLineStep()
            if (OritaCalc.distance_circumference(point, closestCircumference) <= getSelectionDistance()) {
                val circle = Circle(closestCircumference.determineCenter(), closestCircumference.getR(), LineColor.GREEN_6)
                addCircleStep(circle)
                selectedElements.add(circle)
                println("Выбран круг для инверсии (линия не найдена)")
            }
        }
    }
    
    private fun handleSecondSelection(point: Point, closestCircumference: Circle) {
        if (OritaCalc.distance_circumference(point, closestCircumference) <= getSelectionDistance()) {
            val circle = Circle(closestCircumference.determineCenter(), closestCircumference.getR(), LineColor.RED_1)
            addCircleStep(circle)
            selectedElements.add(circle)
            println("Выбран второй элемент для инверсии")
        }
    }
    
    private fun addInversion(circle0: Circle, circleH: Circle) {
        // Проверить, проходит ли окружность circle0 через центр circleH
        if (abs(OritaCalc.distance(circle0.determineCenter(), circleH.determineCenter()) - circle0.getR()) < 0.0000001) {
            val lineSegment = circleH.turnAround_CircleToLineSegment(circle0)
            addLineSegment(lineSegment)
            createdElements.add(lineSegment)
            println("Создана инвертированная линия")
        } else {
            // Создать инвертированный круг
            val invertedCircle = Circle()
            invertedCircle.set(circleH.turnAround(circle0))
            addCircle(invertedCircle)
            createdElements.add(invertedCircle)
            println("Создан инвертированный круг")
        }
    }
    
    private fun addInversion(lineSegment: LineSegment, circleH: Circle) {
        val straightLine = StraightLine(lineSegment)
        
        // Проверить, лежит ли центр circleH на линии
        if (straightLine.calculateDistance(circleH.determineCenter()) < 0.0000001) {
            println("Центр круга лежит на линии, инверсия невозможна")
            return
        }
        
        // Создать инвертированный круг
        val invertedCircle = Circle()
        invertedCircle.set(circleH.turnAround_LineSegmentToCircle(lineSegment))
        addCircle(invertedCircle)
        createdElements.add(invertedCircle)
        println("Создан инвертированный круг относительно линии")
    }
    
    override fun getClosestCircleMidpoint(point: Point): Circle {
        // Здесь должна быть логика получения ближайшего круга
        // Пока возвращаем круг с центром в точке и радиусом 1.0
        return Circle(point, 1.0, LineColor.BLACK_0)
    }
    
    private fun clearSteps() {
        clearLineStep()
        clearCircleStep()
        selectedElements.clear()
    }
    
    fun getSelectedElements(): List<Any> = selectedElements.toList()
    
    fun getCreatedElements(): List<Any> = createdElements.toList()
    
    fun getSelectedElementsCount(): Int = selectedElements.size
    
    fun getCreatedElementsCount(): Int = createdElements.size
    
    fun isProcessing(): Boolean = isProcessing
    
    fun clearCreatedElements() {
        createdElements.clear()
    }
    
    fun getInvertedCircleDescription(): String {
        return when {
            getLineStepSize() + getCircleStepSize() == 0 -> "Выберите первый элемент (линию или круг)"
            getLineStepSize() + getCircleStepSize() == 1 -> "Выберите второй элемент (круг)"
            getLineStepSize() + getCircleStepSize() == 2 -> "Обработка инверсии"
            else -> "Неизвестное состояние"
        }
    }
    
    fun getInversionInfo(): String {
        return buildString {
            append("Выбранные элементы: ${selectedElements.size}\n")
            selectedElements.forEachIndexed { index, element ->
                when (element) {
                    is LineSegment -> {
                        append("${index + 1}. Линия: (${element.determineAX()}, ${element.determineAY()}) -> ")
                        append("(${element.determineBX()}, ${element.determineBY()})\n")
                    }
                    is Circle -> {
                        append("${index + 1}. Круг: центр (${element.determineCenter().x}, ${element.determineCenter().y}), ")
                        append("радиус ${element.getR()}\n")
                    }
                }
            }
            append("Созданные элементы: ${createdElements.size}")
        }
    }
    
    override fun getName(): String = "Рисование инвертированных кругов"
    
    override fun getDescription(): String = "Создавайте инвертированные круги и линии относительно других элементов"
} 