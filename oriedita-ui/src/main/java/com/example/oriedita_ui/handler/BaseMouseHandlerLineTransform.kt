package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.FoldLineSet

/**
 * Базовый класс для обработчиков мыши с трансформацией линий
 * Позволяет перемещать и трансформировать выбранные линии
 * Адаптированная версия для Android
 */
abstract class BaseMouseHandlerLineTransform : BaseMouseHandlerLineSelect() {
    
    protected var delta = Point(0.0, 0.0)
    protected var lines: FoldLineSet? = null
    protected var active = false
    
    private var needsRerender = false
    private var lastZoomX = 0.0
    private var lastZoomY = 0.0
    private var lastAngle = 0.0
    private var bottomLeft: Point? = null
    private var topRight: Point? = null
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        super.onPress(offset, change)
        
        delta = Point(0.0, 0.0)
        
        // Получить выбранные линии
        val selectedLines = getSelectedLines()
        lines = selectedLines
        active = true
        needsRerender = true
        bottomLeft = null
        topRight = null
        
        println("Начало трансформации линий: ${selectedLines.getTotal()} линий")
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        super.onDrag(offset, change)
        
        if (selectionLine != null) {
            val selectionLine = selectionLine!!
            delta = Point(
                -selectionLine.determineBX() + selectionLine.determineAX(),
                -selectionLine.determineBY() + selectionLine.determineAY()
            )
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (selectionLine != null) {
            val selectionLine = selectionLine!!
            delta = Point(
                -selectionLine.determineBX() + selectionLine.determineAX(),
                -selectionLine.determineBY() + selectionLine.determineAY()
            )
        }
        
        clearLineStep()
        active = false
        
        println("Завершение трансформации линий: дельта (${delta.x}, ${delta.y})")
        return true
    }
    
    /**
     * Получить выбранные линии для трансформации
     */
    protected fun getSelectedLines(): FoldLineSet {
        val selectedLines = FoldLineSet()
        // Здесь должна быть логика получения выбранных линий
        // Пока возвращаем пустой набор
        return selectedLines
    }
    
    /**
     * Применить трансформацию к линиям
     */
    protected fun applyTransformation() {
        lines?.let { foldLineSet ->
            for (line in foldLineSet.getLineSegmentsIterable()) {
                val newA = line.getA().move(delta)
                val newB = line.getB().move(delta)
                val transformedLine = LineSegment(newA, newB, line.getColor())
                // Здесь должна быть логика применения трансформации
            }
        }
    }
    
    /**
     * Проверить, изменилась ли камера
     */
    private fun determineCameraChanged(zoomX: Double, zoomY: Double, angle: Double): Boolean {
        return zoomX != lastZoomX || zoomY != lastZoomY || angle != lastAngle
    }
    
    /**
     * Обновить кэш рендеринга
     */
    protected fun updateRenderCache(zoomX: Double, zoomY: Double, angle: Double) {
        if (determineCameraChanged(zoomX, zoomY, angle)) {
            needsRerender = true
        }
        
        lastZoomX = zoomX
        lastZoomY = zoomY
        lastAngle = angle
    }
    
    /**
     * Получить ограничивающий прямоугольник линий
     */
    protected fun getBoundingBox(): BoundingBox? {
        lines?.let { foldLineSet ->
            if (foldLineSet.getTotal() > 0) {
                try {
                    val minX = foldLineSet.getMinX()
                    val maxX = foldLineSet.getMaxX()
                    val minY = foldLineSet.getMinY()
                    val maxY = foldLineSet.getMaxY()
                    
                    // Проверяем, что координаты валидны
                    if (minX.isFinite() && maxX.isFinite() && minY.isFinite() && maxY.isFinite() &&
                        minX <= maxX && minY <= maxY) {
                        
                        return BoundingBox(
                            topLeft = Point(minX, minY),
                            bottomRight = Point(maxX, maxY)
                        )
                    } else {
                        // Если координаты невалидны, вычисляем вручную
                        return calculateBoundingBoxManually(foldLineSet)
                    }
                } catch (e: Exception) {
                    // Если методы не работают, вычисляем вручную
                    return calculateBoundingBoxManually(foldLineSet)
                }
            }
        }
        return null
    }
    
    /**
     * Вычисление ограничивающего прямоугольника вручную
     */
    private fun calculateBoundingBoxManually(foldLineSet: FoldLineSet): BoundingBox? {
        if (foldLineSet.getTotal() <= 0) return null
        
        var minX = Double.POSITIVE_INFINITY
        var maxX = Double.NEGATIVE_INFINITY
        var minY = Double.POSITIVE_INFINITY
        var maxY = Double.NEGATIVE_INFINITY
        
        try {
            for (i in 1..foldLineSet.getTotal()) {
                val line = foldLineSet.get(i)
                val ax = line.determineAX()
                val ay = line.determineAY()
                val bx = line.determineBX()
                val by = line.determineBY()
                
                minX = minOf(minX, ax, bx)
                maxX = maxOf(maxX, ax, bx)
                minY = minOf(minY, ay, by)
                maxY = maxOf(maxY, ay, by)
            }
            
            // Проверяем, что координаты валидны
            if (minX.isFinite() && maxX.isFinite() && minY.isFinite() && maxY.isFinite() &&
                minX <= maxX && minY <= maxY) {
                
                return BoundingBox(
                    topLeft = Point(minX, minY),
                    bottomRight = Point(maxX, maxY)
                )
            }
        } catch (e: Exception) {
            println("Ошибка при вычислении ограничивающего прямоугольника: ${e.message}")
        }
        
        return null
    }
    

    
    fun getLinesCount(): Int = lines?.getTotal() ?: 0
    
    override fun reset() {
        super.reset()
        delta = Point(0.0, 0.0)
        lines = null
        active = false
        needsRerender = false
        bottomLeft = null
        topRight = null
        lastZoomX = 0.0
        lastZoomY = 0.0
        lastAngle = 0.0
        
        println("Обработчик трансформации линий сброшен")
    }
    
    fun getLineTransformDescription(): String {
        return when {
            !active -> "Готов к трансформации"
            lines == null -> "Трансформация..."
            else -> "Трансформация ${lines!!.getTotal()} линий: дельта (${String.format("%.2f", delta.x)}, ${String.format("%.2f", delta.y)})"
        }
    }
    
    fun getLineTransformInfo(): String {
        return buildString {
            append("Статус: ${if (active) "активна" else "неактивна"}\n")
            append("Линий: ${getLinesCount()}\n")
            append("Дельта: (${String.format("%.4f", delta.x)}, ${String.format("%.4f", delta.y)})\n")
            append("Нужен перерендер: ${if (needsRerender) "да" else "нет"}")
        }
    }
}

data class BoundingBox(
    val topLeft: Point,
    val bottomRight: Point
) {
    val width: Double get() = bottomRight.x - topLeft.x
    val height: Double get() = bottomRight.y - topLeft.y
    val center: Point get() = Point((topLeft.x + bottomRight.x) / 2, (topLeft.y + bottomRight.y) / 2)
    
    fun contains(point: Point): Boolean {
        return point.x >= topLeft.x && point.x <= bottomRight.x &&
               point.y >= topLeft.y && point.y <= bottomRight.y
    }
    
    fun intersects(other: BoundingBox): Boolean {
        return !(other.bottomRight.x < topLeft.x || other.topLeft.x > bottomRight.x ||
                other.bottomRight.y < topLeft.y || other.topLeft.y > bottomRight.y)
    }
} 