package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor

/**
 * Обработчик для рисования складных линий оригами
 * Создает линии, которые можно сложить без самопересечений
 * Адаптированная версия для Android
 */
class MouseHandlerFoldableLineDraw : BaseMouseHandlerInputRestricted() {
    
    private var startPoint: Point? = null
    private var currentLine: LineSegment? = null
    private var isDrawing = false
    private var foldableLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        if (isDrawing) {
            println("Рисование уже активно")
            return false
        }
        
        startPoint = point
        isDrawing = true
        
        // Создать начальную линию
        currentLine = LineSegment(point, point, getLineColor())
        addLineStep(currentLine!!)
        
        println("Начало рисования складной линии: (${point.x}, ${point.y})")
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing || startPoint == null) return false
        
        val currentPoint = offsetToPoint(offset)
        
        // Обновить текущую линию
        currentLine = LineSegment(currentLine?.a,  currentPoint, currentLine?.color)
        if (currentLine != null) {
            setLineStep(getLineStepSize() - 1, currentLine!!)
        }
        
        // Проверить складность в реальном времени
        checkFoldability(currentPoint)
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDrawing || startPoint == null) return false
        
        val endPoint = offsetToPoint(offset)
        isDrawing = false
        
        // Завершить линию
        currentLine = LineSegment(currentLine?.a, endPoint, currentLine?.color)
        if (currentLine != null) {
            setLineStep(getLineStepSize() - 1, currentLine!!)
            
            // Проверить финальную складность
            if (isLineFoldable(currentLine!!)) {
                foldableLines.add(currentLine!!)
                addGlobalLine(currentLine!!)
                println("Складная линия добавлена: (${startPoint!!.x}, ${startPoint!!.y}) - (${endPoint.x}, ${endPoint.y})")
            } else {
                println("Линия не является складной и не добавлена")
            }
        }
        
        // Очистить временные данные
        clearLineStep()
        startPoint = null
        currentLine = null
        
        return true
    }
    
    /**
     * Проверить складность линии в реальном времени
     */
    private fun checkFoldability(point: Point) {
        if (currentLine == null) return
        
        val isFoldable = isLineFoldable(currentLine!!)
        
        // Изменить цвет линии в зависимости от складности
        val newColor = if (isFoldable) LineColor.MAGENTA_5 else LineColor.RED_1
        currentLine = LineSegment(currentLine!!.a, currentLine!!.b, newColor)
        
        if (currentLine != null) {
            setLineStep(getLineStepSize() - 1, currentLine!!)
        }
        
        if (!isFoldable) {
            println("Предупреждение: линия может быть не складной")
        }
    }
    
    /**
     * Проверить, является ли линия складной
     */
    private fun isLineFoldable(line: LineSegment): Boolean {
        try {
            // Получить все существующие линии
            val existingLines = getAllExistingLines()
            
            // Проверить пересечения
            for (existingLine in existingLines) {
                if (linesIntersect(line, existingLine)) {
                    return false
                }
            }
            
            // Проверить правила складности для вершин
            val startVertexValid = isVertexFoldable(line.getA(), line, existingLines)
            val endVertexValid = isVertexFoldable(line.getB(), line, existingLines)
            
            return startVertexValid && endVertexValid
            
        } catch (e: Exception) {
            println("Ошибка при проверке складности: ${e.message}")
            return false
        }
    }
    
    /**
     * Проверить, является ли вершина складной
     */
    private fun isVertexFoldable(vertex: Point, newLine: LineSegment, existingLines: List<LineSegment>): Boolean {
        // Найти все линии, которые проходят через эту вершину
        val linesAtVertex = mutableListOf<LineSegment>()
        
        for (line in existingLines) {
            if (line.getA() == vertex || line.getB() == vertex) {
                linesAtVertex.add(line)
            }
        }
        
        // Добавить новую линию
        linesAtVertex.add(newLine)
        
        // Проверить количество линий (должно быть четным)
        if (linesAtVertex.size % 2 != 0) {
            return false
        }
        
        // Проверить углы (сумма должна быть 360°)
        val angles = calculateAnglesAtVertex(vertex, linesAtVertex)
        val totalAngle = angles.sum()
        
        return Math.abs(totalAngle - 360.0) <= 1.0
    }
    
    /**
     * Вычислить углы в вершине
     */
    private fun calculateAnglesAtVertex(vertex: Point, lines: List<LineSegment>): List<Double> {
        val angles = mutableListOf<Double>()
        
        for (line in lines) {
            val otherPoint = if (line.getA() == vertex) line.getB() else line.getA()
            val angle = Math.toDegrees(Math.atan2(otherPoint.y - vertex.y, otherPoint.x - vertex.x))
            angles.add((angle + 360) % 360)
        }
        
        angles.sort()
        
        val angleDifferences = mutableListOf<Double>()
        for (i in angles.indices) {
            val nextAngle = angles[(i + 1) % angles.size]
            val difference = (nextAngle - angles[i] + 360) % 360
            angleDifferences.add(difference)
        }
        
        return angleDifferences
    }
    
    /**
     * Проверить пересечение двух линий
     */
    private fun linesIntersect(line1: LineSegment, line2: LineSegment): Boolean {
        val x1 = line1.getA().x
        val y1 = line1.getA().y
        val x2 = line1.getB().x
        val y2 = line1.getB().y
        
        val x3 = line2.getA().x
        val y3 = line2.getA().y
        val x4 = line2.getB().x
        val y4 = line2.getB().y
        
        val denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)
        
        if (Math.abs(denominator) < 1e-10) {
            return false // Линии параллельны
        }
        
        val t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denominator
        val u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / denominator
        
        return t in 0.0..1.0 && u in 0.0..1.0
    }
    
    /**
     * Получить все существующие линии
     */
    private fun getAllExistingLines(): List<LineSegment> {
        val allLines = mutableListOf<LineSegment>()
        
        // Добавляем складные линии
        allLines.addAll(foldableLines)
        
        // Добавляем глобальные линии
        allLines.addAll(getGlobalLines())
        
        return allLines
    }
    
    /**
     * Создать тестовую модель для демонстрации
     */
    fun createTestModel() {
        // Создаем простую тестовую модель - треугольник
        val center = Point(0.0, 0.0)
        val size = 80.0
        
        val p1 = Point(center.x, center.y - size)
        val p2 = Point(center.x - size * 0.866, center.y + size * 0.5)
        val p3 = Point(center.x + size * 0.866, center.y + size * 0.5)
        
        // Добавляем стороны треугольника
        val line1 = LineSegment(p1, p2, LineColor.BLACK_0)
        val line2 = LineSegment(p2, p3, LineColor.BLACK_0)
        val line3 = LineSegment(p3, p1, LineColor.BLACK_0)
        
        foldableLines.addAll(listOf(line1, line2, line3))
        addGlobalLine(line1)
        addGlobalLine(line2)
        addGlobalLine(line3)
        
        println("Создана тестовая модель: треугольник")
    }
    
    /**
     * Проверить всю модель на складность
     */
    fun checkModelFoldability(): Boolean {
        val allLines = getAllExistingLines()
        val violations = mutableListOf<String>()
        
        // Проверяем каждую вершину
        val vertexLines = mutableMapOf<Point, MutableList<LineSegment>>()
        
        for (line in allLines) {
            vertexLines.getOrPut(line.getA()) { mutableListOf() }.add(line)
            vertexLines.getOrPut(line.getB()) { mutableListOf() }.add(line)
        }
        
        for ((vertex, lines) in vertexLines) {
            if (lines.size % 2 != 0) {
                violations.add("Вершина (${vertex.x}, ${vertex.y}) имеет нечетное количество линий: ${lines.size}")
            }
            
            val angles = calculateAnglesAtVertex(vertex, lines)
            val totalAngle = angles.sum()
            
            if (Math.abs(totalAngle - 360.0) > 1.0) {
                violations.add("Вершина (${vertex.x}, ${vertex.y}): сумма углов = ${String.format("%.1f", totalAngle)}°")
            }
        }
        
        if (violations.isNotEmpty()) {
            println("Найдены нарушения складности:")
            violations.forEach { println("  - $it") }
            return false
        }
        
        println("Модель полностью складная ✓")
        return true
    }
    
    fun getFoldableLines(): List<LineSegment> = foldableLines.toList()
    
    fun getFoldableLinesCount(): Int = foldableLines.size
    
    fun isDrawing(): Boolean = isDrawing
    
    fun clearFoldableLines() {
        foldableLines.clear()
        println("Все складные линии очищены")
    }
    
    override fun getName(): String = "Рисование складных линий"
    
    override fun getDescription(): String = "Создает линии, которые можно сложить без самопересечений"
    
    fun getFoldableLineDescription(): String {
        return when {
            isDrawing -> "Рисование складной линии..."
            foldableLines.isEmpty() -> "Нет складных линий"
            else -> "Складных линий: ${foldableLines.size}"
        }
    }
    
    fun getFoldableLineInfo(): String {
        return buildString {
            append("Статус: ${if (isDrawing) "рисование" else "готов"}\n")
            append("Складных линий: ${foldableLines.size}\n")
            append("Всего линий в модели: ${getGlobalLines().size}\n")
            if (foldableLines.isNotEmpty()) {
                append("Последняя линия: (${foldableLines.last().getA().x}, ${foldableLines.last().getA().y}) - (${foldableLines.last().getB().x}, ${foldableLines.last().getB().y})")
            }
        }
    }
} 