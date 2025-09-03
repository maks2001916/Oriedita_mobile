package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import kotlin.math.*

/**
 * Обработчик для создания биссектрисы квадрата
 * Создает биссектрисы углов квадрата для оригами
 * Адаптированная версия для Android
 */
class MouseHandlerSquareBisector : BaseMouseHandlerInputRestricted() {
    
    private var squarePoints = mutableListOf<Point>()
    private var bisectorLines = mutableListOf<LineSegment>()
    private var isCreating = false
    private var currentSquare: Square? = null
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        if (isCreating) {
            println("Создание биссектрисы уже активно")
            return false
        }
        
        // Добавить точку квадрата
        squarePoints.add(point)
        isCreating = true
        
        println("Добавлена точка квадрата: (${point.x}, ${point.y})")
        
        // Если у нас есть 4 точки, создать квадрат и биссектрисы
        if (squarePoints.size == 4) {
            createSquareAndBisectors()
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // Не требуется для создания биссектрисы
        return false
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        isCreating = false
        println("Завершение добавления точки квадрата")
        return true
    }
    
    /**
     * Создать квадрат и биссектрисы
     */
    private fun createSquareAndBisectors() {
        try {
            // Создать квадрат из точек
            val square = createSquareFromPoints(squarePoints)
            if (square != null) {
                currentSquare = square
                
                // Создать биссектрисы
                val bisectors = createBisectors(square)
                bisectorLines.addAll(bisectors)
                
                println("Создан квадрат и ${bisectors.size} биссектрис")
                
                // Очистить точки для следующего квадрата
                squarePoints.clear()
            } else {
                println("Не удалось создать квадрат из заданных точек")
                squarePoints.clear()
            }
            
        } catch (e: Exception) {
            println("Ошибка при создании биссектрис: ${e.message}")
            squarePoints.clear()
        }
    }
    
    /**
     * Создать квадрат из точек
     */
    private fun createSquareFromPoints(points: List<Point>): Square? {
        if (points.size != 4) return null
        
        // Найти центр квадрата
        val centerX = points.map { it.x }.average()
        val centerY = points.map { it.y }.average()
        val center = Point(centerX, centerY)
        
        // Сортировать точки по углу относительно центра
        val sortedPoints = points.sortedBy { point ->
            atan2(point.y - centerY, point.x - centerX)
        }
        
        // Проверить, что точки образуют квадрат
        val sides = mutableListOf<Double>()
        for (i in sortedPoints.indices) {
            val j = (i + 1) % sortedPoints.size
            val side = sqrt(
                (sortedPoints[j].x - sortedPoints[i].x).pow(2) + 
                (sortedPoints[j].y - sortedPoints[i].y).pow(2)
            )
            sides.add(side)
        }
        
        // Проверить, что все стороны равны
        val avgSide = sides.average()
        val tolerance = avgSide * 0.1 // 10% допуск
        
        val isSquare = sides.all { abs(it - avgSide) <= tolerance }
        
        if (!isSquare) {
            println("Точки не образуют квадрат")
            return null
        }
        
        return Square(sortedPoints[0], sortedPoints[1], sortedPoints[2], sortedPoints[3], center)
    }
    
    /**
     * Создать биссектрисы для квадрата
     */
    private fun createBisectors(square: Square): List<LineSegment> {
        val bisectors = mutableListOf<LineSegment>()
        
        // Биссектриса угла A
        val bisectorA = createAngleBisector(square.pointA, square.pointB, square.pointD)
        if (bisectorA != null) {
            bisectors.add(bisectorA)
        }
        
        // Биссектриса угла B
        val bisectorB = createAngleBisector(square.pointB, square.pointC, square.pointA)
        if (bisectorB != null) {
            bisectors.add(bisectorB)
        }
        
        // Биссектриса угла C
        val bisectorC = createAngleBisector(square.pointC, square.pointD, square.pointB)
        if (bisectorC != null) {
            bisectors.add(bisectorC)
        }
        
        // Биссектриса угла D
        val bisectorD = createAngleBisector(square.pointD, square.pointA, square.pointC)
        if (bisectorD != null) {
            bisectors.add(bisectorD)
        }
        
        // Диагонали (также являются биссектрисами)
        val diagonalAC = LineSegment(square.pointA, square.pointC, LineColor.RED_1)
        val diagonalBD = LineSegment(square.pointB, square.pointD, LineColor.RED_1)
        bisectors.add(diagonalAC)
        bisectors.add(diagonalBD)
        
        return bisectors
    }
    
    /**
     * Создать биссектрису угла
     */
    private fun createAngleBisector(vertex: Point, point1: Point, point2: Point): LineSegment? {
        // Векторы от вершины к точкам
        val vector1X = point1.x - vertex.x
        val vector1Y = point1.y - vertex.y
        val vector2X = point2.x - vertex.x
        val vector2Y = point2.y - vertex.y
        
        // Нормализовать векторы
        val length1 = sqrt(vector1X * vector1X + vector1Y * vector1Y)
        val length2 = sqrt(vector2X * vector2X + vector2Y * vector2Y)
        
        if (length1 < 1e-10 || length2 < 1e-10) {
            return null
        }
        
        val normalized1X = vector1X / length1
        val normalized1Y = vector1Y / length1
        val normalized2X = vector2X / length2
        val normalized2Y = vector2Y / length2
        
        // Вектор биссектрисы (сумма нормализованных векторов)
        val bisectorX = normalized1X + normalized2X
        val bisectorY = normalized1Y + normalized2Y
        
        val bisectorLength = sqrt(bisectorX * bisectorX + bisectorY * bisectorY)
        
        if (bisectorLength < 1e-10) {
            return null
        }
        
        // Нормализовать биссектрису
        val normalizedBisectorX = bisectorX / bisectorLength
        val normalizedBisectorY = bisectorY / bisectorLength
        
        // Создать линию биссектрисы
        val endPoint = Point(
            vertex.x + normalizedBisectorX * 200.0,
            vertex.y + normalizedBisectorY * 200.0
        )
        
        return LineSegment(vertex, endPoint, LineColor.BLUE_2)
    }
    
    /**
     * Создать дополнительные биссектрисы для оригами
     */
    fun createAdvancedBisectors(): List<LineSegment> {
        val advancedBisectors = mutableListOf<LineSegment>()
        
        val square = currentSquare ?: return emptyList()
        
        // Биссектрисы сторон
        val midAB = Point((square.pointA.x + square.pointB.x) / 2, (square.pointA.y + square.pointB.y) / 2)
        val midBC = Point((square.pointB.x + square.pointC.x) / 2, (square.pointB.y + square.pointC.y) / 2)
        val midCD = Point((square.pointC.x + square.pointD.x) / 2, (square.pointC.y + square.pointD.y) / 2)
        val midDA = Point((square.pointD.x + square.pointA.x) / 2, (square.pointD.y + square.pointA.y) / 2)
        
        // Биссектрисы от центра к серединам сторон
        advancedBisectors.add(LineSegment(square.center, midAB, LineColor.CYAN_3))
        advancedBisectors.add(LineSegment(square.center, midBC, LineColor.CYAN_3))
        advancedBisectors.add(LineSegment(square.center, midCD, LineColor.CYAN_3))
        advancedBisectors.add(LineSegment(square.center, midDA, LineColor.CYAN_3))
        
        // Биссектрисы между серединами сторон
        advancedBisectors.add(LineSegment(midAB, midCD, LineColor.ORANGE_4))
        advancedBisectors.add(LineSegment(midBC, midDA, LineColor.ORANGE_4))
        
        return advancedBisectors
    }
    
    fun getSquarePoints(): List<Point> = squarePoints.toList()
    
    fun getBisectorLines(): List<LineSegment> = bisectorLines.toList()
    
    fun getSquarePointsCount(): Int = squarePoints.size
    
    fun getBisectorLinesCount(): Int = bisectorLines.size
    
    fun isCreating(): Boolean = isCreating
    
    fun getCurrentSquare(): Square? = currentSquare
    
    fun clearSquareBisector() {
        squarePoints.clear()
        bisectorLines.clear()
        currentSquare = null
        println("Биссектрисы квадрата очищены")
    }
    
    override fun getName(): String = "Биссектриса квадрата"
    
    override fun getDescription(): String = "Создает биссектрисы углов и сторон квадрата для оригами"
    
    fun getSquareBisectorDescription(): String {
        return when {
            isCreating -> "Создание биссектрисы квадрата..."
            squarePoints.isEmpty() -> "Нет точек квадрата"
            squarePoints.size < 4 -> "Точек квадрата: ${squarePoints.size}/4"
            else -> "Квадрат создан, биссектрис: ${bisectorLines.size}"
        }
    }
    
    fun getSquareBisectorInfo(): String {
        return buildString {
            append("Статус: ${if (isCreating) "создание" else "готов"}\n")
            append("Точек квадрата: ${squarePoints.size}/4\n")
            append("Биссектрис: ${bisectorLines.size}\n")
            if (currentSquare != null) {
                append("Квадрат: ${String.format("%.1f", currentSquare!!.getArea())} кв.ед.\n")
                append("Сторона: ${String.format("%.1f", currentSquare!!.getSideLength())}")
            }
        }
    }
}

/**
 * Класс для представления квадрата
 */
data class Square(
    val pointA: Point,
    val pointB: Point,
    val pointC: Point,
    val pointD: Point,
    val center: Point
) {
    fun getArea(): Double {
        val side = getSideLength()
        return side * side
    }
    
    fun getSideLength(): Double {
        return sqrt(
            (pointB.x - pointA.x).pow(2) + (pointB.y - pointA.y).pow(2)
        )
    }
    
    fun getPerimeter(): Double {
        return getSideLength() * 4
    }
    
    fun getPoints(): List<Point> = listOf(pointA, pointB, pointC, pointD)
    

    
    fun getDiagonalLength(): Double {
        return sqrt(
            (pointC.x - pointA.x).pow(2) + (pointC.y - pointA.y).pow(2)
        )
    }
} 