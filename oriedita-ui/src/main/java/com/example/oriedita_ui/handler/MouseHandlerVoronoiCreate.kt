package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import kotlin.math.*

/**
 * Обработчик для создания диаграмм Вороного
 * Создает ячейки Вороного на основе заданных точек
 * Адаптированная версия для Android
 */
class MouseHandlerVoronoiCreate : BaseMouseHandlerInputRestricted() {
    
    private var voronoiPoints = mutableListOf<Point>()
    private var voronoiCells = mutableListOf<VoronoiCell>()
    private var isCreating = false
    private var boundingBox: Rectangle? = null
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        if (isCreating) {
            println("Создание диаграммы Вороного уже активно")
            return false
        }
        
        // Добавить точку
        voronoiPoints.add(point)
        addGlobalPoint(point)
        isCreating = true
        
        println("Добавлена точка Вороного: (${point.x}, ${point.y})")
        
        // Пересчитать диаграмму
        recalculateVoronoi()
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // Не требуется для создания диаграммы Вороного
        return false
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        isCreating = false
        println("Завершение добавления точки Вороного")
        return true
    }
    
    /**
     * Пересчитать диаграмму Вороного
     */
    private fun recalculateVoronoi() {
        if (voronoiPoints.size < 2) {
            voronoiCells.clear()
            return
        }
        
        try {
            // Очистить предыдущие ячейки
            voronoiCells.clear()
            
            // Создать ограничивающий прямоугольник
            createBoundingBox()
            
            // Создать ячейки Вороного для каждой точки
            for (i in voronoiPoints.indices) {
                val cell = createVoronoiCell(voronoiPoints[i], i)
                voronoiCells.add(cell)
            }
            
            // Добавить границы ячеек в глобальный список линий
            addCellBoundariesToGlobal()
            
            println("Диаграмма Вороного пересчитана: ${voronoiCells.size} ячеек")
            
        } catch (e: Exception) {
            println("Ошибка при создании диаграммы Вороного: ${e.message}")
        }
    }
    
    /**
     * Добавить границы ячеек в глобальный список линий
     */
    private fun addCellBoundariesToGlobal() {
        for (cell in voronoiCells) {
            val polygon = cell.getPolygon()
            for (i in polygon.indices) {
                val j = (i + 1) % polygon.size
                val line = LineSegment(polygon[i], polygon[j], LineColor.BLUE_2)
                addGlobalLine(line)
            }
        }
    }
    
    /**
     * Создать ограничивающий прямоугольник
     */
    private fun createBoundingBox() {
        if (voronoiPoints.isEmpty()) return
        
        val minX = voronoiPoints.minOf { it.x } - 100.0
        val maxX = voronoiPoints.maxOf { it.x } + 100.0
        val minY = voronoiPoints.minOf { it.y } - 100.0
        val maxY = voronoiPoints.maxOf { it.y } + 100.0
        
        boundingBox = Rectangle(
            topLeft = Point(minX, minY),
            bottomRight = Point(maxX, maxY)
        )
    }
    
    /**
     * Создать ячейку Вороного для точки
     */
    private fun createVoronoiCell(point: Point, pointIndex: Int): VoronoiCell {
        val cell = VoronoiCell(point, pointIndex)
        
        // Найти все другие точки
        val otherPoints = voronoiPoints.filterIndexed { index, _ -> index != pointIndex }
        
        // Создать границы ячейки
        val boundaries = mutableListOf<LineSegment>()
        
        for (otherPoint in otherPoints) {
            // Создать перпендикулярную биссектрису
            val bisector = createPerpendicularBisector(point, otherPoint)
            
            // Обрезать биссектрису до границ
            val clippedBisector = clipLineToBoundingBox(bisector)
            
            if (clippedBisector != null) {
                boundaries.add(clippedBisector)
            }
        }
        
        // Найти пересечения границ для создания многоугольника
        val polygon = createPolygonFromBoundaries(boundaries, point)
        cell.setPolygon(polygon)
        
        return cell
    }
    
    /**
     * Создать перпендикулярную биссектрису между двумя точками
     */
    private fun createPerpendicularBisector(p1: Point, p2: Point): LineSegment {
        // Середина между точками
        val midX = (p1.x + p2.x) / 2.0
        val midY = (p1.y + p2.y) / 2.0
        val midPoint = Point(midX, midY)
        
        // Вектор направления
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        
        // Перпендикулярный вектор
        val perpX = -dy
        val perpY = dx
        
        // Нормализовать
        val length = sqrt(perpX * perpX + perpY * perpY)
        val normalizedX = perpX / length
        val normalizedY = perpY / length
        
        // Создать линию длиной 1000 единиц
        val startPoint = Point(midX - normalizedX * 500, midY - normalizedY * 500)
        val endPoint = Point(midX + normalizedX * 500, midY + normalizedY * 500)
        
        return LineSegment(startPoint, endPoint, LineColor.BLUE_2)
    }
    
    /**
     * Обрезать линию до границ
     */
    private fun clipLineToBoundingBox(line: LineSegment): LineSegment? {
        val box = boundingBox ?: return line
        
        val x1 = line.getA().x
        val y1 = line.getA().y
        var x2 = line.getB().x
        var y2 = line.getB().y
        
        // Алгоритм Коэна-Сазерленда для обрезки
        var code1 = getRegionCode(x1, y1, box)
        var code2 = getRegionCode(x2, y2, box)
        
        var x = x1
        var y = y1
        
        while (true) {
            if ((code1 or code2) == 0) {
                // Линия полностью внутри
                return LineSegment(Point(x1, y1), Point(x2, y2), line.color)
            } else if ((code1 and code2) != 0) {
                // Линия полностью снаружи
                return null
            } else {
                // Линия частично внутри
                val code = if (code1 != 0) code1 else code2
                
                val (newX, newY) = when {
                    (code and 1) != 0 -> { // Верхняя граница
                        val newX = x1 + (x2 - x1) * (box.topLeft.y - y1) / (y2 - y1)
                        val newY = box.topLeft.y
                        Pair(newX, newY)
                    }
                    (code and 2) != 0 -> { // Нижняя граница
                        val newX = x1 + (x2 - x1) * (box.bottomRight.y - y1) / (y2 - y1)
                        val newY = box.bottomRight.y
                        Pair(newX, newY)
                    }
                    (code and 4) != 0 -> { // Правая граница
                        val newX = box.bottomRight.x
                        val newY = y1 + (y2 - y1) * (box.bottomRight.x - x1) / (x2 - x1)
                        Pair(newX, newY)
                    }
                    else -> { // Левая граница
                        val newX = box.topLeft.x
                        val newY = y1 + (y2 - y1) * (box.topLeft.x - x1) / (x2 - x1)
                        Pair(newX, newY)
                    }
                }
                
                if (code == code1) {
                    x = newX
                    y = newY
                    code1 = getRegionCode(x, y, box)
                } else {
                    x2 = newX
                    y2 = newY
                    code2 = getRegionCode(x2, y2, box)
                }
            }
        }
    }
    
    /**
     * Получить код региона для точки
     */
    private fun getRegionCode(x: Double, y: Double, box: Rectangle): Int {
        var code = 0
        if (y < box.topLeft.y) code = code or 1
        if (y > box.bottomRight.y) code = code or 2
        if (x > box.bottomRight.x) code = code or 4
        if (x < box.topLeft.x) code = code or 8
        return code
    }
    
    /**
     * Создать многоугольник из границ
     */
    private fun createPolygonFromBoundaries(boundaries: List<LineSegment>, centerPoint: Point): List<Point> {
        if (boundaries.isEmpty()) return listOf(centerPoint)
        
        // Простая реализация: найти точки пересечения
        val intersections = mutableListOf<Point>()
        
        for (i in boundaries.indices) {
            for (j in (i + 1) until boundaries.size) {
                val intersection = findIntersection(boundaries[i], boundaries[j])
                if (intersection != null) {
                    intersections.add(intersection)
                }
            }
        }
        
        // Сортировать точки по углу относительно центра
        val sortedPoints = intersections.sortedBy { point ->
            atan2(point.y - centerPoint.y, point.x - centerPoint.x)
        }
        
        return if (sortedPoints.isNotEmpty()) sortedPoints else listOf(centerPoint)
    }
    
    /**
     * Найти пересечение двух линий
     */
    private fun findIntersection(line1: LineSegment, line2: LineSegment): Point? {
        val x1 = line1.getA().x
        val y1 = line1.getA().y
        val x2 = line1.getB().x
        val y2 = line1.getB().y
        
        val x3 = line2.getA().x
        val y3 = line2.getA().y
        val x4 = line2.getB().x
        val y4 = line2.getB().y
        
        val denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)
        
        if (abs(denominator) < 1e-10) {
            return null // Линии параллельны
        }
        
        val t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denominator
        
        val intersectionX = x1 + t * (x2 - x1)
        val intersectionY = y1 + t * (y2 - y1)
        
        return Point(intersectionX, intersectionY)
    }
    
    /**
     * Создать тестовую диаграмму Вороного
     */
    fun createTestVoronoi() {
        clearVoronoi()
        
        // Создаем несколько тестовых точек
        val testPoints = listOf(
            Point(-100.0, -100.0),
            Point(100.0, -100.0),
            Point(0.0, 100.0),
            Point(-50.0, 0.0),
            Point(50.0, 0.0)
        )
        
        for (point in testPoints) {
            voronoiPoints.add(point)
            addGlobalPoint(point)
        }
        
        recalculateVoronoi()
        println("Создана тестовая диаграмма Вороного с ${testPoints.size} точками")
    }
    
    /**
     * Получить статистику диаграммы Вороного
     */
    fun getVoronoiStatistics(): String {
        return buildString {
            append("Точек: ${voronoiPoints.size}\n")
            append("Ячеек: ${voronoiCells.size}\n")
            if (voronoiCells.isNotEmpty()) {
                val avgArea = voronoiCells.map { it.getArea() }.average()
                val avgPerimeter = voronoiCells.map { it.getPerimeter() }.average()
                append("Средняя площадь ячейки: ${String.format("%.2f", avgArea)}\n")
                append("Средний периметр ячейки: ${String.format("%.2f", avgPerimeter)}")
            }
        }
    }
    
    fun getVoronoiPoints(): List<Point> = voronoiPoints.toList()
    
    fun getVoronoiCells(): List<VoronoiCell> = voronoiCells.toList()
    
    fun getVoronoiPointsCount(): Int = voronoiPoints.size
    
    fun getVoronoiCellsCount(): Int = voronoiCells.size
    
    fun isCreating(): Boolean = isCreating
    
    fun clearVoronoi() {
        voronoiPoints.clear()
        voronoiCells.clear()
        boundingBox = null
        println("Диаграмма Вороного очищена")
    }
    
    override fun getName(): String = "Создание диаграммы Вороного"
    
    override fun getDescription(): String = "Создает ячейки Вороного на основе заданных точек"
    
    fun getVoronoiDescription(): String {
        return when {
            isCreating -> "Создание диаграммы Вороного..."
            voronoiPoints.isEmpty() -> "Нет точек для диаграммы Вороного"
            else -> "Диаграмма Вороного: ${voronoiPoints.size} точек, ${voronoiCells.size} ячеек"
        }
    }
    
    fun getVoronoiInfo(): String {
        return buildString {
            append("Статус: ${if (isCreating) "создание" else "готов"}\n")
            append("Точек: ${voronoiPoints.size}\n")
            append("Ячеек: ${voronoiCells.size}\n")
            append("Глобальных точек: ${getGlobalPoints().size}\n")
            append("Глобальных линий: ${getGlobalLines().size}\n")
            if (voronoiPoints.isNotEmpty()) {
                append("Последняя точка: (${voronoiPoints.last().x}, ${voronoiPoints.last().y})")
            }
        }
    }
}

/**
 * Класс для представления ячейки Вороного
 */
data class VoronoiCell(
    val centerPoint: Point,
    val pointIndex: Int
) {
    private var polygon: List<Point> = emptyList()
    
    fun setPolygon(points: List<Point>) {
        polygon = points
    }
    
    fun getPolygon(): List<Point> = polygon
    
    fun getArea(): Double {
        if (polygon.size < 3) return 0.0
        
        var area = 0.0
        for (i in polygon.indices) {
            val j = (i + 1) % polygon.size
            area += polygon[i].x * polygon[j].y
            area -= polygon[j].x * polygon[i].y
        }
        
        return abs(area) / 2.0
    }
    
    fun getPerimeter(): Double {
        if (polygon.size < 2) return 0.0
        
        var perimeter = 0.0
        for (i in polygon.indices) {
            val j = (i + 1) % polygon.size
            perimeter += sqrt(
                (polygon[j].x - polygon[i].x).pow(2) + 
                (polygon[j].y - polygon[i].y).pow(2)
            )
        }
        
        return perimeter
    }
} 