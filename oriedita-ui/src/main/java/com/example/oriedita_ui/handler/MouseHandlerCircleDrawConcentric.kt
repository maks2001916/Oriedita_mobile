package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.elements.Circle
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик рисования концентрических кругов
 * Адаптированная версия для Android
 */
class MouseHandlerCircleDrawConcentric : BaseMouseHandler() {
    
    private var anchorPoint: Point? = null
    private var releasePoint: Point? = null
    private var radiusDifference: LineSegment? = null
    private var originalCircle: Circle? = null
    private var newCircle: Circle? = null
    private var currentStep = CircleDrawConcentricStep.SELECT_CIRCLE
    private var isProcessing = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            CircleDrawConcentricStep.SELECT_CIRCLE -> {
                handleSelectCirclePress(point)
            }
            CircleDrawConcentricStep.CLICK_DRAG_POINT -> {
                handleClickDragPointPress(point)
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            CircleDrawConcentricStep.SELECT_CIRCLE -> {
                handleSelectCircleDrag(point)
            }
            CircleDrawConcentricStep.CLICK_DRAG_POINT -> {
                handleClickDragPointDrag(point)
            }
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        when (currentStep) {
            CircleDrawConcentricStep.SELECT_CIRCLE -> {
                currentStep = handleSelectCircleRelease(point)
            }
            CircleDrawConcentricStep.CLICK_DRAG_POINT -> {
                currentStep = handleClickDragPointRelease(point)
            }
        }
        
        return true
    }
    
    private fun handleSelectCirclePress(point: Point) {
        handleSelectCircleDrag(point)
    }
    
    private fun handleSelectCircleDrag(point: Point) {
        // Получаем ближайший круг из набора кругов
        val closestCircle = getClosestCircle(point)
        if (closestCircle != null && OritaCalc.distance_circumference(point, closestCircle) < getSelectionDistance()) {
            originalCircle = Circle(closestCircle.getX(), closestCircle.getY(), closestCircle.getR(), LineColor.GREEN_6)
        } else {
            originalCircle = null
        }
    }
    
    private fun handleSelectCircleRelease(point: Point): CircleDrawConcentricStep {
        if (originalCircle == null) return CircleDrawConcentricStep.SELECT_CIRCLE
        
        println("Выбран исходный круг с центром: (${originalCircle!!.determineCenter().x}, ${originalCircle!!.determineCenter().y})")
        return CircleDrawConcentricStep.CLICK_DRAG_POINT
    }
    
    private fun handleClickDragPointPress(point: Point) {
        anchorPoint = point
        val closestPoint = getClosestPoint(point)
        if (anchorPoint!!.distance(closestPoint) < getSelectionDistance()) {
            anchorPoint = closestPoint
        } else {
            anchorPoint = null
        }
    }
    
    private fun handleClickDragPointDrag(point: Point) {
        releasePoint = point
        val closestPoint = getClosestPoint(point)
        if (releasePoint!!.distance(closestPoint) < getSelectionDistance()) {
            releasePoint = closestPoint
        }
        
        if (anchorPoint == null || anchorPoint == releasePoint) {
            newCircle = null
            radiusDifference = null
            return
        }
        
        radiusDifference = LineSegment(anchorPoint!!, releasePoint!!, LineColor.CYAN_3)
        newCircle = Circle(
            originalCircle!!.determineCenter(),
            originalCircle!!.getR() + radiusDifference!!.determineLength(),
            LineColor.CYAN_3
        )
        
        println("Предварительный концентрический круг создан с радиусом: ${newCircle!!.getR()}")
    }
    
    private fun handleClickDragPointRelease(point: Point): CircleDrawConcentricStep {
        if (anchorPoint == null) return CircleDrawConcentricStep.CLICK_DRAG_POINT
        
        val closestPoint = getClosestPoint(point)
        if (releasePoint == null || releasePoint!!.distance(closestPoint) > getSelectionDistance()) {
            anchorPoint = null
            releasePoint = null
            radiusDifference = null
            newCircle = null
            return CircleDrawConcentricStep.CLICK_DRAG_POINT
        }
        
        releasePoint = closestPoint
        radiusDifference = LineSegment(anchorPoint!!, releasePoint!!, LineColor.CYAN_3)
        newCircle = Circle(
            originalCircle!!.determineCenter(),
            originalCircle!!.getR() + radiusDifference!!.determineLength(),
            LineColor.CYAN_3
        )
        
        addCircle(newCircle!!)
        println("Добавлен концентрический круг с радиусом: ${newCircle!!.getR()}")
        
        reset()
        return CircleDrawConcentricStep.SELECT_CIRCLE
    }
    
    override fun getClosestCircleMidpoint(point: Point): Circle {
        // Получаем ближайший круг и возвращаем его центр
        val closestCircle = getClosestCircle(point)
        return if (closestCircle != null) {
            Circle(closestCircle.determineCenter(), 0.0, LineColor.GREEN_6)
        } else {
            // Если круг не найден, возвращаем круг с нулевым радиусом в указанной точке
            Circle(point, 0.0, LineColor.GREEN_6)
        }
    }
    
    override fun getClosestCircle(point: Point): Circle? {
        // Получаем все круги из паттерна сгибов
        val circles = getCircleStepList()
        if (circles.isEmpty()) return null
        
        var closestCircle: Circle? = null
        var minDistance = Double.MAX_VALUE
        
        for (circle in circles) {
            val center = circle.determineCenter()
            val distance = point.distance(center)
            
            if (distance < minDistance) {
                minDistance = distance
                closestCircle = circle
            }
        }
        
        return closestCircle
    }
    
    override fun reset() {
        anchorPoint = null
        releasePoint = null
        originalCircle = null
        newCircle = null
        radiusDifference = null
        currentStep = CircleDrawConcentricStep.SELECT_CIRCLE
        isProcessing = false
        
        println("Обработчик концентрических кругов сброшен")
    }
    
    fun getCurrentStep(): CircleDrawConcentricStep = currentStep
    
    fun getOriginalCircle(): Circle? = originalCircle
    
    fun getNewCircle(): Circle? = newCircle
    
    fun getRadiusDifference(): LineSegment? = radiusDifference
    
    fun getAnchorPoint(): Point? = anchorPoint
    
    fun getReleasePoint(): Point? = releasePoint
    
    fun isProcessing(): Boolean = isProcessing
    
    fun getConcentricCircleDescription(): String {
        return when (currentStep) {
            CircleDrawConcentricStep.SELECT_CIRCLE -> {
                if (originalCircle != null) {
                    "Выбран круг с центром: (${originalCircle!!.determineCenter().x}, ${originalCircle!!.determineCenter().y})"
                } else {
                    "Выберите исходный круг"
                }
            }
            CircleDrawConcentricStep.CLICK_DRAG_POINT -> {
                if (newCircle != null) {
                    "Концентрический круг с радиусом: ${newCircle!!.getR()}"
                } else {
                    "Укажите разность радиусов"
                }
            }
        }
    }
    
    fun getCircleInfo(): String {
        return buildString {
            append("Исходный круг: ")
            if (originalCircle != null) {
                append("центр (${originalCircle!!.determineCenter().x}, ${originalCircle!!.determineCenter().y}), ")
                append("радиус ${originalCircle!!.getR()}")
            } else {
                append("не выбран")
            }
            append("\nНовый круг: ")
            if (newCircle != null) {
                append("радиус ${newCircle!!.getR()}")
            } else {
                append("не создан")
            }
        }
    }
    
    override fun getName(): String = "Рисование концентрических кругов"
    
    override fun getDescription(): String = "Создавайте концентрические круги относительно выбранного круга"
}

enum class CircleDrawConcentricStep {
    SELECT_CIRCLE,
    CLICK_DRAG_POINT
} 