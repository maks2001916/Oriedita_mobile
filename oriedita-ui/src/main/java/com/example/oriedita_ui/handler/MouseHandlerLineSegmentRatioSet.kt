package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик установки соотношения сегментов
 * Адаптированная версия MouseHandlerLineSegmentRatioSet для Android
 */
class MouseHandlerLineSegmentRatioSet : BaseMouseHandler() {
    
    private var anchorPoint: Point? = null
    private var releasePoint: Point? = null
    private var dragSegment: LineSegment? = null
    private var isDragging = false
    private var internalDivisionRatioS: Double = 1.0
    private var internalDivisionRatioT: Double = 1.0
    private var createdSegments = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        isDragging = true
        
        // Установить якорную точку
        val closestPoint = findClosestPoint(point)
        anchorPoint = if (point.distance(closestPoint) < getSelectionDistance()) {
            closestPoint
        } else {
            point
        }
        
        println("Установлена якорная точка: (${anchorPoint!!.x}, ${anchorPoint!!.y})")
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDragging || anchorPoint == null) return false
        
        val currentPoint = offsetToPoint(offset)
        
        // Установить точку освобождения
        val closestPoint = findClosestPoint(currentPoint)
        releasePoint = if (currentPoint.distance(closestPoint) < getSelectionDistance()) {
            closestPoint
        } else {
            currentPoint
        }
        
        // Создать сегмент перетаскивания
        dragSegment = LineSegment(anchorPoint!!, releasePoint!!, getLineColor())
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isDragging) return false
        
        val endPoint = offsetToPoint(offset)
        
        // Установить финальную точку освобождения
        val closestEndPoint = findClosestPoint(endPoint)
        releasePoint = if (endPoint.distance(closestEndPoint) < getSelectionDistance()) {
            closestEndPoint
        } else {
            endPoint
        }
        
        // Создать финальный сегмент
        dragSegment = LineSegment(anchorPoint!!, releasePoint!!, getLineColor())
        
        // Проверить, что сегмент имеет длину
        if (dragSegment?.determineLength() ?: 0.0 > 0.001) {
            // Выполнить установку соотношения
            performRatioSet()
        } else {
            println("Сегмент слишком короткий для установки соотношения")
        }
        
        // Сбросить состояние
        reset()
        
        return true
    }
    
    private fun performRatioSet() {
        dragSegment?.let { segment ->
            createdSegments.clear()
            
            // Поменять местами точки A и B
            val swappedSegment = LineSegment(Point(segment.determineBX(), segment.determineBY()),
                                           Point(segment.determineAX(), segment.determineAY()),
                                           segment.color)
            
            when {
                // Если только одно соотношение равно 0
                internalDivisionRatioS == 0.0 && internalDivisionRatioT != 0.0 -> {
                    addLineSegment(swappedSegment)
                    createdSegments.add(swappedSegment)
                    println("Добавлен сегмент с соотношением T: ${internalDivisionRatioT}")
                }
                
                internalDivisionRatioS != 0.0 && internalDivisionRatioT == 0.0 -> {
                    addLineSegment(swappedSegment)
                    createdSegments.add(swappedSegment)
                    println("Добавлен сегмент с соотношением S: ${internalDivisionRatioS}")
                }
                
                // Если оба соотношения не равны 0
                internalDivisionRatioS != 0.0 && internalDivisionRatioT != 0.0 -> {
                    // Вычислить точку внутреннего деления
                    val nx = (internalDivisionRatioT * swappedSegment.determineBX() + internalDivisionRatioS * swappedSegment.determineAX()) / 
                            (internalDivisionRatioS + internalDivisionRatioT)
                    val ny = (internalDivisionRatioT * swappedSegment.determineBY() + internalDivisionRatioS * swappedSegment.determineAY()) / 
                            (internalDivisionRatioS + internalDivisionRatioT)
                    
                    val divisionPoint = Point(nx, ny)
                    
                    // Создать два сегмента от концов до точки деления
                    val segment1 = LineSegment(
                        Point(swappedSegment.determineAX(), swappedSegment.determineAY()),
                        Point(nx, ny),
                        getLineColor())
                    val segment2 = LineSegment(
                        Point(swappedSegment.determineBX(), swappedSegment.determineBY()),
                        Point(nx, ny),
                        getLineColor())
                    
                    addLineSegment(segment1)
                    addLineSegment(segment2)
                    createdSegments.add(segment1)
                    createdSegments.add(segment2)
                    
                    println("Созданы сегменты с соотношением S:T = ${internalDivisionRatioS}:${internalDivisionRatioT}")
                    println("Точка деления: (${nx}, ${ny})")
                }
                
                else -> {
                    // Оба соотношения равны 0 - ничего не делать
                    println("Оба соотношения равны 0 - сегмент не создан")
                }
            }
        }
    }
    
    override fun reset() {
        anchorPoint = null
        releasePoint = null
        dragSegment = null
        isDragging = false
    }
    
    private fun findClosestPoint(point: Point): Point {
        // Здесь должна быть логика поиска ближайшей точки
        // Пока возвращаем исходную точку
        return point
    }
    
    override fun getSelectionDistance(): Double {
        // Здесь должна быть логика получения расстояния выбора
        return 10.0
    }
    
    override fun getLineColor(): LineColor {
        // Здесь должна быть логика получения текущего цвета линии
        return LineColor.BLACK_0
    }
    
    override fun addLineSegment(segment: LineSegment) {
        // Здесь должна быть логика добавления сегмента линии в набор
        println("Сегмент линии добавлен в набор")
    }
    
    fun setInternalDivisionRatioS(ratio: Double) {
        internalDivisionRatioS = ratio
        println("Соотношение S установлено: $ratio")
    }
    
    fun setInternalDivisionRatioT(ratio: Double) {
        internalDivisionRatioT = ratio
        println("Соотношение T установлено: $ratio")
    }
    
    fun setInternalDivisionRatios(ratioS: Double, ratioT: Double) {
        internalDivisionRatioS = ratioS
        internalDivisionRatioT = ratioT
        println("Соотношения установлены: S=$ratioS, T=$ratioT")
    }
    
    fun getInternalDivisionRatioS(): Double = internalDivisionRatioS
    
    fun getInternalDivisionRatioT(): Double = internalDivisionRatioT
    
    fun getAnchorPoint(): Point? = anchorPoint
    
    fun getReleasePoint(): Point? = releasePoint
    
    fun getDragSegment(): LineSegment? = dragSegment
    
    fun getCreatedSegments(): List<LineSegment> = createdSegments.toList()
    
    fun getCreatedCount(): Int = createdSegments.size
    
    fun isDragging(): Boolean = isDragging
    
    fun clearCreatedSegments() {
        createdSegments.clear()
    }
    
    fun getSegmentLength(): Double {
        return dragSegment?.determineLength() ?: 0.0
    }
    
    fun hasValidSegment(): Boolean {
        return dragSegment?.determineLength() ?: 0.0 > 0.001
    }
    
    fun getSegmentDescription(): String {
        return dragSegment?.let { segment ->
            "Сегмент: (${segment.determineAX()}, ${segment.determineAY()}) -> (${segment.determineBX()}, ${segment.determineBY()})"
        } ?: "Нет сегмента"
    }
    
    fun getRatioDescription(): String {
        return "Соотношение S:T = ${internalDivisionRatioS}:${internalDivisionRatioT}"
    }
    
    fun getDivisionPoint(): Point? {
        dragSegment?.let { segment ->
            if (internalDivisionRatioS != 0.0 && internalDivisionRatioT != 0.0) {
                val nx = (internalDivisionRatioT * segment.determineBX() + internalDivisionRatioS * segment.determineAX()) / 
                        (internalDivisionRatioS + internalDivisionRatioT)
                val ny = (internalDivisionRatioT * segment.determineBY() + internalDivisionRatioS * segment.determineAY()) / 
                        (internalDivisionRatioS + internalDivisionRatioT)
                return Point(nx, ny)
            }
        }
        return null
    }
    
    override fun getName(): String = "Установка соотношения сегментов"
    
    override fun getDescription(): String = "Установите соотношение деления сегмента"
} 