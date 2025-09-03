package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.elements.Rectangle

/**
 * Обработчик изменения позиции фона
 * Адаптированная версия для Android
 */
class MouseHandlerBackgroundChangePosition : BaseMouseHandler() {
    
    private var stepPoints = mutableListOf<Point>()
    private var stepSegments = mutableListOf<LineSegment>()
    private var isProcessing = false
    private var backgroundRectangle: Rectangle? = null
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        // Добавить точку в зависимости от количества уже добавленных точек
        when (stepPoints.size) {
            0 -> {
                addStepPoint(point, LineColor.RED_1)
                println("Добавлена первая точка фона: (${point.x}, ${point.y})")
            }
            1 -> {
                addStepPoint(point, LineColor.BLUE_2)
                println("Добавлена вторая точка фона: (${point.x}, ${point.y})")
            }
            2 -> {
                addStepPoint(point, LineColor.CYAN_3)
                println("Добавлена третья точка фона: (${point.x}, ${point.y})")
            }
            3 -> {
                addStepPoint(point, LineColor.ORANGE_4)
                println("Добавлена четвертая точка фона: (${point.x}, ${point.y})")
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // Изменение позиции фона не требует перетаскивания
        return false
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (stepPoints.size == 4) {
            // Создать прямоугольник фона из четырех точек
            createBackgroundRectangle()
            reset()
            println("Позиция фона изменена")
        }
        
        return true
    }
    
    private fun addStepPoint(point: Point, color: LineColor) {
        val closestPoint = getClosestPoint(point)
        val finalPoint = if (point.distance(closestPoint) < getSelectionDistance()) {
            closestPoint
        } else {
            point
        }
        
        stepPoints.add(finalPoint)
        val segment = LineSegment(finalPoint, finalPoint, color)
        stepSegments.add(segment)
        
        // Добавить сегмент в линию шага
        addLineStep(segment)
    }
    
    private fun createBackgroundRectangle() {
        if (stepPoints.size != 4) return
        
        val p1 = stepPoints[0]
        val p2 = stepPoints[1]
        val p3 = stepPoints[2]
        val p4 = stepPoints[3]
        
        // Создать прямоугольник из четырех точек
        backgroundRectangle = Rectangle(p1, p2, p3, p4)
        
        // Установить позицию фона
        setBackgroundPosition(backgroundRectangle!!)
        
        // Разблокировать фон
        setBackgroundLock(false)
        
        println("Создан прямоугольник фона: ${getRectangleDescription(backgroundRectangle!!)}")
    }
    
    private fun setBackgroundPosition(rectangle: Rectangle) {
        // Здесь должна быть логика установки позиции фона
        println("Установлена позиция фона: ${getRectangleDescription(rectangle)}")
    }
    
    private fun setBackgroundLock(locked: Boolean) {
        // Здесь должна быть логика блокировки/разблокировки фона
        println("Фон ${if (locked) "заблокирован" else "разблокирован"}")
    }
    
    private fun getRectangleDescription(rectangle: Rectangle): String {
        val p1 = rectangle.getP1()
        val p2 = rectangle.getP2()
        val p3 = rectangle.getP3()
        val p4 = rectangle.getP4()
        
        return "Прямоугольник: P1(${String.format("%.2f", p1.x)}, ${String.format("%.2f", p1.y)}), " +
               "P2(${String.format("%.2f", p2.x)}, ${String.format("%.2f", p2.y)}), " +
               "P3(${String.format("%.2f", p3.x)}, ${String.format("%.2f", p3.y)}), " +
               "P4(${String.format("%.2f", p4.x)}, ${String.format("%.2f", p4.y)})"
    }
    
     override fun reset() {
        stepPoints.clear()
        stepSegments.clear()
        isProcessing = false
        clearLineStep()
        
        println("Обработчик изменения позиции фона сброшен")
    }
    
    fun getStepPoints(): List<Point> = stepPoints.toList()
    
    fun getStepSegments(): List<LineSegment> = stepSegments.toList()
    
    fun getStepCount(): Int = stepPoints.size
    
    fun getBackgroundRectangle(): Rectangle? = backgroundRectangle
    
    fun isProcessing(): Boolean = isProcessing
    
    fun getBackgroundPositionDescription(): String {
        return when (stepPoints.size) {
            0 -> "Выберите первую точку фона"
            1 -> "Выберите вторую точку фона"
            2 -> "Выберите третью точку фона"
            3 -> "Выберите четвертую точку фона"
            4 -> "Позиция фона установлена"
            else -> "Неизвестное состояние"
        }
    }
    
    fun getBackgroundRectangleDescription(): String {
        return backgroundRectangle?.let { getRectangleDescription(it) } ?: "Прямоугольник фона не создан"
    }
    
    override fun getName(): String = "Изменение позиции фона"
    
    override fun getDescription(): String = "Установите новую позицию фона, выбрав четыре точки"
} 