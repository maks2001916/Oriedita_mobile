package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor

/**
 * Обработчик для проверки складности оригами
 * Проверяет, можно ли сложить модель без самопересечений
 * Адаптированная версия для Android
 */
class MouseHandlerFlatFoldableCheck : BaseMouseHandlerInputRestricted() {
    
    private var checkResult = false
    private var checkMessage = ""
    private var isChecking = false
    
    // Временное хранилище линий для демонстрации (в реальной реализации должно быть подключено к модели)
    private val testLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        if (isChecking) {
            println("Проверка складности уже выполняется")
            return false
        }
        
        isChecking = true
        checkResult = false
        checkMessage = "Выполняется проверка складности..."
        
        println("Начало проверки складности в точке: (${point.x}, ${point.y})")
        
        // Выполнить проверку складности
        performFlatFoldableCheck(point)
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // Не требуется для проверки складности
        return false
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        isChecking = false
        println("Завершение проверки складности")
        return true
    }
    
    /**
     * Выполнить проверку складности в указанной точке
     */
    private fun performFlatFoldableCheck(point: Point) {
        try {
            // Получить все линии в области вокруг точки
            val nearbyLines = getLinesNearPoint(point, getSelectionDistance() * 3)
            
            if (nearbyLines.isEmpty()) {
                checkResult = false
                checkMessage = "Нет линий для проверки в указанной области"
                return
            }
            
            // Проверить правила складности
            val violations = mutableListOf<String>()
            
            // Проверка 1: Каждая вершина должна иметь четное количество линий
            val vertexViolations = checkVertexLineCount(nearbyLines)
            violations.addAll(vertexViolations)
            
            // Проверка 2: Сумма углов вокруг каждой вершины должна быть 360°
            val angleViolations = checkVertexAngles(nearbyLines)
            violations.addAll(angleViolations)
            
            // Проверка 3: Линии не должны пересекаться
            val intersectionViolations = checkLineIntersections(nearbyLines)
            violations.addAll(intersectionViolations)
            
            checkResult = violations.isEmpty()
            checkMessage = if (checkResult) {
                "Модель складная ✓"
            } else {
                "Найдены нарушения складности:\n" + violations.joinToString("\n")
            }
            
            println("Результат проверки: $checkMessage")
            
        } catch (e: Exception) {
            checkResult = false
            checkMessage = "Ошибка при проверке: ${e.message}"
            println("Ошибка проверки складности: ${e.message}")
        }
    }
    
    /**
     * Проверить количество линий в каждой вершине
     */
    private fun checkVertexLineCount(lines: List<LineSegment>): List<String> {
        val violations = mutableListOf<String>()
        val vertexLineCount = mutableMapOf<Point, Int>()
        
        // Подсчитать количество линий для каждой вершины
        for (line in lines) {
            vertexLineCount[line.getA()] = vertexLineCount.getOrDefault(line.getA(), 0) + 1
            vertexLineCount[line.getB()] = vertexLineCount.getOrDefault(line.getB(), 0) + 1
        }
        
        // Проверить четность
        for ((vertex, count) in vertexLineCount) {
            if (count % 2 != 0) {
                violations.add("Вершина (${vertex.x}, ${vertex.y}) имеет нечетное количество линий: $count")
            }
        }
        
        return violations
    }
    
    /**
     * Проверить углы вокруг каждой вершины
     */
    private fun checkVertexAngles(lines: List<LineSegment>): List<String> {
        val violations = mutableListOf<String>()
        val vertexLines = mutableMapOf<Point, MutableList<LineSegment>>()
        
        // Сгруппировать линии по вершинам
        for (line in lines) {
            vertexLines.getOrPut(line.getA()) { mutableListOf() }.add(line)
            vertexLines.getOrPut(line.getB()) { mutableListOf() }.add(line)
        }
        
        // Проверить углы для каждой вершины
        for ((vertex, vertexLineList) in vertexLines) {
            if (vertexLineList.size < 2) continue
            
            val angles = calculateAnglesAtVertex(vertex, vertexLineList)
            val totalAngle = angles.sum()
            
            if (Math.abs(totalAngle - 360.0) > 1.0) {
                violations.add("Вершина (${vertex.x}, ${vertex.y}): сумма углов = ${String.format("%.1f", totalAngle)}° (должна быть 360°)")
            }
        }
        
        return violations
    }
    
    /**
     * Проверить пересечения линий
     */
    private fun checkLineIntersections(lines: List<LineSegment>): List<String> {
        val violations = mutableListOf<String>()
        
        for (i in lines.indices) {
            for (j in (i + 1) until lines.size) {
                val line1 = lines[i]
                val line2 = lines[j]
                
                // Пропустить линии с общими вершинами
                if (line1.getA() == line2.getA() || line1.getA() == line2.getB() ||
                    line1.getB() == line2.getA() || line1.getB() == line2.getB()) {
                    continue
                }
                
                if (linesIntersect(line1, line2)) {
                    violations.add("Линии пересекаются: (${line1.getA().x}, ${line1.getA().y})-(${line1.getB().x}, ${line1.getB().y}) и (${line2.getA().x}, ${line2.getA().y})-(${line2.getB().x}, ${line2.getB().y})")
                }
            }
        }
        
        return violations
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
     * Получить линии вблизи точки
     */
    private fun getLinesNearPoint(point: Point, radius: Double): List<LineSegment> {
        val allLines = mutableListOf<LineSegment>()
        
        // Добавляем тестовые линии
        allLines.addAll(testLines)
        
        // Добавляем глобальные линии
        allLines.addAll(getGlobalLines())
        
        // Фильтруем линии по расстоянию
        return allLines.filter { line ->
            val distanceToA = point.distance(line.getA())
            val distanceToB = point.distance(line.getB())
            val lineLength = line.determineLength()
            
            // Проверяем, находится ли точка вблизи линии
            if (lineLength > 0) {
                val projection = getProjectionOnLine(point, line)
                val distanceToLine = point.distance(projection)
                distanceToLine <= radius || distanceToA <= radius || distanceToB <= radius
            } else {
                distanceToA <= radius || distanceToB <= radius
            }
        }
    }
    
    /**
     * Получить проекцию точки на линию
     */
    private fun getProjectionOnLine(point: Point, line: LineSegment): Point {
        val x1 = line.getA().x
        val y1 = line.getA().y
        val x2 = line.getB().x
        val y2 = line.getB().y
        
        val dx = x2 - x1
        val dy = y2 - y1
        
        if (dx == 0.0 && dy == 0.0) {
            return line.getA()
        }
        
        val t = ((point.x - x1) * dx + (point.y - y1) * dy) / (dx * dx + dy * dy)
        val clampedT = t.coerceIn(0.0, 1.0)
        
        return Point(x1 + clampedT * dx, y1 + clampedT * dy)
    }
    
    /**
     * Добавить тестовую линию для демонстрации
     */
    fun addTestLine(line: LineSegment) {
        testLines.add(line)
        // Также добавляем в глобальный список для других обработчиков
        addGlobalLine(line)
    }
    
    /**
     * Очистить тестовые линии
     */
    fun clearTestLines() {
        testLines.clear()
    }
    
    /**
     * Создать тестовую модель для демонстрации
     */
    fun createTestModel() {
        // Создаем простую тестовую модель - квадрат с диагоналями
        val center = Point(0.0, 0.0)
        val size = 100.0
        
        val p1 = Point(center.x - size, center.y - size)
        val p2 = Point(center.x + size, center.y - size)
        val p3 = Point(center.x + size, center.y + size)
        val p4 = Point(center.x - size, center.y + size)
        
        // Добавляем стороны квадрата
        addTestLine(LineSegment(p1, p2, LineColor.BLACK_0))
        addTestLine(LineSegment(p2, p3, LineColor.BLACK_0))
        addTestLine(LineSegment(p3, p4, LineColor.BLACK_0))
        addTestLine(LineSegment(p4, p1, LineColor.BLACK_0))
        
        // Добавляем диагонали
        addTestLine(LineSegment(p1, p3, LineColor.RED_1))
        addTestLine(LineSegment(p2, p4, LineColor.RED_1))
        
        println("Создана тестовая модель: квадрат с диагоналями")
    }
    
    fun getCheckResult(): Boolean = checkResult
    
    fun getCheckMessage(): String = checkMessage
    
    fun isChecking(): Boolean = isChecking
    
    override fun getName(): String = "Проверка складности"
    
    override fun getDescription(): String = "Проверяет возможность складывания модели без самопересечений"
    
    fun getFlatFoldableDescription(): String {
        return when {
            isChecking -> "Выполняется проверка..."
            checkResult -> "Модель складная ✓"
            else -> "Найдены нарушения складности"
        }
    }
    
    fun getFlatFoldableInfo(): String {
        return buildString {
            append("Статус: ${if (isChecking) "проверка" else if (checkResult) "складная" else "не складная"}\n")
            append("Сообщение: $checkMessage\n")
            append("Тестовых линий: ${testLines.size}\n")
            append("Всего линий: ${getGlobalLines().size}")
        }
    }
} 