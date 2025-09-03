package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import kotlin.math.*

/**
 * Обработчик для модификации вычисленных форм
 * Позволяет изменять параметры геометрических фигур
 * Адаптированная версия для Android
 */
class MouseHandlerModifyCalculatedShape : BaseMouseHandlerInputRestricted() {
    
    private var selectedShape: CalculatedShape? = null
    private var modificationMode = ModificationMode.NONE
    private var isModifying = false
    private var originalShape: CalculatedShape? = null
    private var modifiedShapes = mutableListOf<CalculatedShape>()
    
    // Хранилище форм для демонстрации
    private val availableShapes = mutableListOf<CalculatedShape>()
    
    enum class ModificationMode {
        NONE, SCALE, ROTATE, TRANSLATE, DEFORM
    }
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        if (isModifying) {
            println("Модификация уже активна")
            return false
        }
        
        // Выбрать форму для модификации
        val shape = findShapeAtPoint(point)
        if (shape != null) {
            selectedShape = shape
            originalShape = shape.copy()
            isModifying = true
            
            println("Выбрана форма для модификации: ${shape.type}")
            return true
        }
        
        return false
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isModifying || selectedShape == null) return false
        
        val currentPoint = offsetToPoint(offset)
        
        when (modificationMode) {
            ModificationMode.SCALE -> performScale(currentPoint)
            ModificationMode.ROTATE -> performRotation(currentPoint)
            ModificationMode.TRANSLATE -> performTranslation(currentPoint)
            ModificationMode.DEFORM -> performDeformation(currentPoint)
            else -> {
                // Определить режим модификации на основе жеста
                determineModificationMode(currentPoint)
            }
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isModifying || selectedShape == null) return false
        
        val endPoint = offsetToPoint(offset)
        isModifying = false
        
        // Применить финальную модификацию
        applyFinalModification(endPoint)
        
        // Сохранить модифицированную форму
        selectedShape?.let { shape ->
            modifiedShapes.add(shape)
            updateShapeInList(shape)
            println("Форма модифицирована и сохранена")
        }
        
        // Сбросить состояние
        selectedShape = null
        originalShape = null
        modificationMode = ModificationMode.NONE
        
        return true
    }
    
    /**
     * Найти форму в указанной точке
     */
    private fun findShapeAtPoint(point: Point): CalculatedShape? {
        // Ищем форму среди доступных форм
        for (shape in availableShapes) {
            if (isPointInShape(point, shape)) {
                return shape
            }
        }
        
        // Если форма не найдена, создаем новую
        return createNewShapeAtPoint(point)
    }
    
    /**
     * Проверить, находится ли точка внутри формы
     */
    private fun isPointInShape(point: Point, shape: CalculatedShape): Boolean {
        val points = shape.points
        if (points.size < 3) return false
        
        var inside = false
        var j = points.size - 1
        
        for (i in points.indices) {
            val pi = points[i]
            val pj = points[j]
            
            if (((pi.y > point.y) != (pj.y > point.y)) &&
                (point.x < (pj.x - pi.x) * (point.y - pi.y) / (pj.y - pi.y) + pi.x)) {
                inside = !inside
            }
            j = i
        }
        
        return inside
    }
    
    /**
     * Создать новую форму в указанной точке
     */
    private fun createNewShapeAtPoint(center: Point): CalculatedShape {
        val shape = createTestShape(center)
        availableShapes.add(shape)
        return shape
    }
    
    /**
     * Создать тестовую форму для демонстрации
     */
    private fun createTestShape(center: Point): CalculatedShape {
        val points = listOf(
            Point(center.x - 50, center.y - 50),
            Point(center.x + 50, center.y - 50),
            Point(center.x + 50, center.y + 50),
            Point(center.x - 50, center.y + 50)
        )
        
        return CalculatedShape(
            type = "Прямоугольник",
            points = points,
            center = center,
            scale = 1.0,
            rotation = 0.0
        )
    }
    
    /**
     * Обновить форму в списке
     */
    private fun updateShapeInList(updatedShape: CalculatedShape) {
        for (i in availableShapes.indices) {
            if (availableShapes[i].center == updatedShape.center) {
                availableShapes[i] = updatedShape
                break
            }
        }
    }
    
    /**
     * Определить режим модификации на основе жеста
     */
    private fun determineModificationMode(point: Point) {
        val original = originalShape ?: return
        val center = original.center
        
        val distance = sqrt((point.x - center.x).pow(2) + (point.y - center.y).pow(2))
        val angle = atan2(point.y - center.y, point.x - center.x)
        
        // Определить режим на основе расстояния и угла
        modificationMode = when {
            distance > 100 -> ModificationMode.SCALE
            abs(angle) > PI / 4 -> ModificationMode.ROTATE
            else -> ModificationMode.TRANSLATE
        }
        
        println("Режим модификации: $modificationMode")
    }
    
    /**
     * Выполнить масштабирование
     */
    private fun performScale(point: Point) {
        val original = originalShape ?: return
        val center = original.center
        
        val originalDistance = 50.0 // Расстояние от центра до угла
        val currentDistance = sqrt((point.x - center.x).pow(2) + (point.y - center.y).pow(2))
        val scaleFactor = currentDistance / originalDistance
        
        selectedShape = original.copy(
            points = original.points.map { p ->
                val dx = p.x - center.x
                val dy = p.y - center.y
                Point(center.x + dx * scaleFactor, center.y + dy * scaleFactor)
            },
            scale = scaleFactor
        )
        
        println("Масштабирование: $scaleFactor")
    }
    
    /**
     * Выполнить поворот
     */
    private fun performRotation(point: Point) {
        val original = originalShape ?: return
        val center = original.center
        
        val angle = atan2(point.y - center.y, point.x - center.x)
        
        selectedShape = original.copy(
            points = original.points.map { p ->
                val dx = p.x - center.x
                val dy = p.y - center.y
                val rotatedX = dx * cos(angle) - dy * sin(angle)
                val rotatedY = dx * sin(angle) + dy * cos(angle)
                Point(center.x + rotatedX, center.y + rotatedY)
            },
            rotation = angle
        )
        
        println("Поворот: ${Math.toDegrees(angle)}°")
    }
    
    /**
     * Выполнить перемещение
     */
    private fun performTranslation(point: Point) {
        val original = originalShape ?: return
        val center = original.center
        
        val deltaX = point.x - center.x
        val deltaY = point.y - center.y
        
        selectedShape = original.copy(
            points = original.points.map { p ->
                Point(p.x + deltaX, p.y + deltaY)
            },
            center = Point(center.x + deltaX, center.y + deltaY)
        )
        
        println("Перемещение: ($deltaX, $deltaY)")
    }
    
    /**
     * Выполнить деформацию
     */
    private fun performDeformation(point: Point) {
        val original = originalShape ?: return
        
        // Простая деформация: сдвиг ближайшей точки
        val closestPointIndex = findClosestPointIndex(point, original.points)
        if (closestPointIndex >= 0) {
            val newPoints = original.points.toMutableList()
            newPoints[closestPointIndex] = point
            
            selectedShape = original.copy(points = newPoints)
            println("Деформация точки $closestPointIndex")
        }
    }
    
    /**
     * Найти индекс ближайшей точки
     */
    private fun findClosestPointIndex(target: Point, points: List<Point>): Int {
        var minDistance = Double.MAX_VALUE
        var closestIndex = -1
        
        for (i in points.indices) {
            val distance = sqrt(
                (points[i].x - target.x).pow(2) + (points[i].y - target.y).pow(2)
            )
            if (distance < minDistance) {
                minDistance = distance
                closestIndex = i
            }
        }
        
        return closestIndex
    }
    
    /**
     * Применить финальную модификацию
     */
    private fun applyFinalModification(point: Point) {
        when (modificationMode) {
            ModificationMode.SCALE -> performScale(point)
            ModificationMode.ROTATE -> performRotation(point)
            ModificationMode.TRANSLATE -> performTranslation(point)
            ModificationMode.DEFORM -> performDeformation(point)
            else -> {}
        }
    }
    
    /**
     * Создать тестовые формы для демонстрации
     */
    fun createTestShapes() {
        availableShapes.clear()
        
        // Создаем несколько тестовых форм
        val shapes = listOf(
            createTestShape(Point(-100.0, -100.0)),
            createTestShape(Point(100.0, -100.0)),
            createTestShape(Point(0.0, 100.0))
        )
        
        availableShapes.addAll(shapes)
        println("Создано ${shapes.size} тестовых форм")
    }
    
    /**
     * Установить режим модификации
     */
    fun setModificationMode(mode: ModificationMode) {
        modificationMode = mode
        println("Установлен режим модификации: $mode")
    }
    
    /**
     * Отменить последнюю модификацию
     */
    fun undoLastModification() {
        if (modifiedShapes.isNotEmpty()) {
            modifiedShapes.removeAt(modifiedShapes.size - 1)
            println("Отменена последняя модификация")
        }
    }
    
    /**
     * Получить все модифицированные формы
     */
    fun getModifiedShapes(): List<CalculatedShape> = modifiedShapes.toList()
    
    /**
     * Получить количество модифицированных форм
     */
    fun getModifiedShapesCount(): Int = modifiedShapes.size
    
    /**
     * Очистить все модифицированные формы
     */
    fun clearModifiedShapes() {
        modifiedShapes.clear()
        println("Все модифицированные формы очищены")
    }
    
    /**
     * Получить текущую выбранную форму
     */
    fun getSelectedShape(): CalculatedShape? = selectedShape
    
    /**
     * Получить режим модификации
     */
    fun getModificationMode(): ModificationMode = modificationMode
    
    /**
     * Получить все доступные формы
     */
    fun getAvailableShapes(): List<CalculatedShape> = availableShapes.toList()
    
    override fun getName(): String = "Модификация вычисленных форм"
    
    override fun getDescription(): String = "Изменяет параметры геометрических фигур"
    
    fun getModifyShapeDescription(): String {
        return when {
            isModifying -> "Модификация формы: $modificationMode"
            selectedShape != null -> "Форма выбрана: ${selectedShape!!.type}"
            else -> "Нет выбранной формы"
        }
    }
    
    fun getModifyShapeInfo(): String {
        return buildString {
            append("Статус: ${if (isModifying) "модификация" else "готов"}\n")
            append("Режим: $modificationMode\n")
            append("Доступных форм: ${availableShapes.size}\n")
            append("Модифицированных форм: ${modifiedShapes.size}\n")
            selectedShape?.let { shape ->
                append("Выбранная форма: ${shape.type}\n")
                append("Масштаб: ${String.format("%.2f", shape.scale)}\n")
                append("Поворот: ${String.format("%.1f", Math.toDegrees(shape.rotation))}°")
            }
        }
    }
}

/**
 * Класс для представления вычисленной формы
 */
data class CalculatedShape(
    val type: String,
    val points: List<Point>,
    val center: Point,
    val scale: Double = 1.0,
    val rotation: Double = 0.0
) {
    fun getArea(): Double {
        if (points.size < 3) return 0.0
        
        var area = 0.0
        for (i in points.indices) {
            val j = (i + 1) % points.size
            area += points[i].x * points[j].y
            area -= points[j].x * points[i].y
        }
        
        return abs(area) / 2.0 * scale * scale
    }
    
    fun getPerimeter(): Double {
        if (points.size < 2) return 0.0
        
        var perimeter = 0.0
        for (i in points.indices) {
            val j = (i + 1) % points.size
            perimeter += sqrt(
                (points[j].x - points[i].x).pow(2) + 
                (points[j].y - points[i].y).pow(2)
            )
        }
        
        return perimeter * scale
    }
    

} 