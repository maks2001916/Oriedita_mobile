package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик удлинения линий сгиба
 * Адаптированная версия MouseHandlerLengthenCrease для Android
 */
class MouseHandlerLengthenCrease : BaseMouseHandler() {
    
    enum class Step {
        START,
        DRAW_SELECTION_LINE,
        DRAW_EXTENSION_POINT
    }
    
    private var currentStep = Step.START
    private var selectionLine: LineSegment? = null
    private var extensionPoint: Point? = null
    private var linesToExtend = mutableListOf<LineSegment>()
    private var extendedLines = mutableListOf<LineSegment>()
    private var isProcessing = false
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        isProcessing = true
        
        when (currentStep) {
            Step.START -> {
                // Начать с создания линии выбора
                linesToExtend.clear()
                selectionLine = LineSegment(point, point, LineColor.MAGENTA_5)
                currentStep = Step.DRAW_SELECTION_LINE
                println("Начало создания линии выбора в точке: (${point.x}, ${point.y})")
            }
            Step.DRAW_EXTENSION_POINT -> {
                // Установить точку расширения
                extensionPoint = point
                println("Установлена точка расширения: (${point.x}, ${point.y})")
            }
            else -> {
                // Ничего не делать
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isProcessing) return false
        
        val currentPoint = offsetToPoint(offset)
        
        when (currentStep) {
            Step.DRAW_SELECTION_LINE -> {
                // Обновить линию выбора
                selectionLine = selectionLine?.let { line ->
                    LineSegment(Point(line.determineAX(), line.determineAY()), currentPoint, line.color)
                }
            }
            Step.DRAW_EXTENSION_POINT -> {
                // Обновить точку расширения
                extensionPoint = currentPoint
            }
            else -> {
                // Ничего не делать
            }
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isProcessing) return false
        
        val endPoint = offsetToPoint(offset)
        
        when (currentStep) {
            Step.DRAW_SELECTION_LINE -> {
                // Завершить линию выбора
                selectionLine = selectionLine?.let { line ->
                    LineSegment(Point(line.determineAX(), line.determineAY()), endPoint, line.color)
                }
                
                // Найти линии для расширения
                findLinesToExtend()
                
                if (linesToExtend.isEmpty()) {
                    reset()
                    println("Не найдено линий для расширения")
                    return true
                }
                
                currentStep = Step.DRAW_EXTENSION_POINT
                println("Найдено линий для расширения: ${linesToExtend.size}")
            }
            
            Step.DRAW_EXTENSION_POINT -> {
                // Завершить точку расширения
                extensionPoint = endPoint
                
                // Выполнить расширение линий
                performLengthen()
                
                // Сбросить состояние
                reset()
            }
            
            else -> {
                // Ничего не делать
            }
        }
        
        return true
    }
    
    private fun findLinesToExtend() {
        linesToExtend.clear()
        
        selectionLine?.let { selection ->
            // Получить все линии
            val allLines = getAllLines()
            
            for (line in allLines) {
                if (isLineIntersecting(line, selection)) {
                    // Добавить линию для расширения
                    val extendedLine = LineSegment(line.getA(), line.getB(), LineColor.GREEN_6)
                    linesToExtend.add(extendedLine)
                    println("Добавлена линия для расширения: (${line.determineAX()}, ${line.determineAY()}) -> (${line.determineBX()}, ${line.determineBY()})")
                }
            }
            
            // Если не найдено пересечений и линия выбора очень короткая
            if (linesToExtend.isEmpty() && selection.determineLength() <= 0.000001) {
                val closestLine = findClosestLine(selection.determineBX(), selection.determineBY())
                if (closestLine != null) {
                    val selectionPoint = Point(selection.determineBX(), selection.determineBY())
                    val centerPoint = getLineCenter(closestLine)
                    if (OritaCalc.distance(selectionPoint, centerPoint) < getSelectionDistance()) {
                        linesToExtend.add(LineSegment(closestLine.getA(), closestLine.getB(), LineColor.GREEN_6))
                        println("Добавлена ближайшая линия для расширения")
                    }
                }
            }
        }
    }
    
    private fun performLengthen() {
        extensionPoint?.let { extension ->
            val closestLine = findClosestLine(extension.x, extension.y)
            
            if (closestLine == null) {
                println("Не найдена ближайшая линия")
                return
            }
            
            val centerPoint = getLineCenter(closestLine)
            if (OritaCalc.distance(extension, centerPoint) >= getSelectionDistance()) {
                println("Точка расширения слишком далеко от линий")
                return
            }
            
            // Проверить, есть ли выбранная линия среди линий для расширения
            val isSameLine = linesToExtend.any { line ->
                isLineEqual(line, closestLine)
            }
            
            if (isSameLine) {
                // Расширить линии до точки пересечения
                extendLinesToIntersection()
            } else {
                // Создать новые линии от пересечений
                createLinesFromIntersections(closestLine)
            }
            
            println("Выполнено расширение линий: ${extendedLines.size} новых линий")
        }
    }
    
    private fun extendLinesToIntersection() {
        selectionLine?.let { selection ->
            for (line in linesToExtend) {
                val intersection = findIntersection(line, selection)
                if (intersection != null) {
                    val extendedLine = createExtendedLine(line, intersection)
                    if (extendedLine.determineLength() > 0.001) {
                        addExtendedLine(extendedLine, line.color)
                    }
                }
            }
        }
    }
    
    private fun createLinesFromIntersections(closestLine: LineSegment) {
        for (line in linesToExtend) {
            if (!isLineParallel(line, closestLine)) {
                val intersection = findIntersection(line, closestLine)
                if (intersection != null) {
                    val newLine = createLineFromIntersection(line, intersection)
                    if (newLine.determineLength() > 0.001) {
                        addExtendedLine(newLine, getLineColor())
                    }
                }
            }
        }
    }
    
    private fun createExtendedLine(originalLine: LineSegment, intersection: Point): LineSegment {
        // Создать линию от ближайшего конца до точки пересечения
        val distanceToA = intersection.distance(Point(originalLine.determineAX(), originalLine.determineAY()))
        val distanceToB = intersection.distance(Point(originalLine.determineBX(), originalLine.determineBY()))
        
        return if (distanceToA < distanceToB) {
            LineSegment(Point(originalLine.determineAX(), originalLine.determineAY()), intersection)
        } else {
            LineSegment(Point(originalLine.determineBX(), originalLine.determineBY()), intersection)
        }
    }
    
    private fun createLineFromIntersection(originalLine: LineSegment, intersection: Point): LineSegment {
        // Создать линию от ближайшего конца до точки пересечения
        return createExtendedLine(originalLine, intersection)
    }
    
    private fun addExtendedLine(line: LineSegment, color: LineColor) {
        val coloredLine = LineSegment(line.getA(), line.getB(), color)
        extendedLines.add(coloredLine)
        addLineToSet(coloredLine)
        println("Добавлена расширенная линия: (${line.determineAX()}, ${line.determineAY()}) -> (${line.determineBX()}, ${line.determineBY()})")
    }
    
    override fun reset() {
        currentStep = Step.START
        selectionLine = null
        extensionPoint = null
        linesToExtend.clear()
        extendedLines.clear()
        isProcessing = false
    }
    
    private fun isLineIntersecting(line1: LineSegment, line2: LineSegment): Boolean {
        // Здесь должна быть логика проверки пересечения линий
        // Пока возвращаем false
        return false
    }
    
    private fun isLineEqual(line1: LineSegment, line2: LineSegment): Boolean {
        // Здесь должна быть логика проверки равенства линий
        // Пока возвращаем false
        return false
    }
    
    private fun isLineParallel(line1: LineSegment, line2: LineSegment): Boolean {
        // Здесь должна быть логика проверки параллельности линий
        // Пока возвращаем false
        return false
    }
    
    private fun findIntersection(line1: LineSegment, line2: LineSegment): Point? {
        // Здесь должна быть логика поиска пересечения линий
        // Пока возвращаем null
        return null
    }
    
    private fun findClosestLine(x: Double, y: Double): LineSegment? {
        // Здесь должна быть логика поиска ближайшей линии
        // Пока возвращаем null
        return null
    }
    
    private fun getLineCenter(line: LineSegment): Point {
        val centerX = (line.determineAX() + line.determineBX()) / 2.0
        val centerY = (line.determineAY() + line.determineBY()) / 2.0
        return Point(centerX, centerY)
    }
    
    override fun getSelectionDistance(): Double {
        // Здесь должна быть логика получения расстояния выбора
        return 10.0
    }
    
    override fun getLineColor(): LineColor {
        // Здесь должна быть логика получения текущего цвета линии
        return LineColor.BLACK_0
    }
    
    private fun getAllLines(): List<LineSegment> {
        // Здесь должна быть логика получения всех линий
        // Пока возвращаем пустой список
        return emptyList()
    }
    
    private fun addLineToSet(line: LineSegment) {
        // Здесь должна быть логика добавления линии в набор
        println("Линия добавлена в набор")
    }
    
    fun getCurrentStep(): Step = currentStep
    
    fun getSelectionLine(): LineSegment? = selectionLine
    
    fun getExtensionPoint(): Point? = extensionPoint
    
    fun getLinesToExtend(): List<LineSegment> = linesToExtend.toList()
    
    fun getExtendedLines(): List<LineSegment> = extendedLines.toList()
    
    fun getExtendedCount(): Int = extendedLines.size
    
    fun isProcessing(): Boolean = isProcessing
    
    fun clearExtendedLines() {
        extendedLines.clear()
    }
    
    fun getStepDescription(): String {
        return when (currentStep) {
            Step.START -> "Начало"
            Step.DRAW_SELECTION_LINE -> "Рисование линии выбора"
            Step.DRAW_EXTENSION_POINT -> "Рисование точки расширения"
        }
    }
    
    override fun getName(): String = "Удлинение линий сгиба"
    
    override fun getDescription(): String = "Удлините выбранные линии сгиба"
} 