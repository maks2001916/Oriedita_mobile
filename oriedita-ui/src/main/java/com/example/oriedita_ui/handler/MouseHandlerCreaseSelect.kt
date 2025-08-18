package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик выбора линий сгиба
 * Адаптированная версия MouseHandlerCreaseSelect для Android
 */
class MouseHandlerCreaseSelect : BaseMouseHandler() {
    
    private var startPoint: Point? = null
    private var selectedLines = mutableListOf<LineSegment>()
    private var isSelecting = false
    private var tripleClick = false
    private var selectionMode: SelectionMode = SelectionMode.NORMAL
    
    enum class SelectionMode {
        NORMAL,     // Обычный выбор
        MOVE,       // Перемещение
        MOVE4P,     // Перемещение 4 точек
        COPY,       // Копирование
        COPY4P,     // Копирование 4 точек
        MIRROR      // Зеркальное отражение
    }
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        startPoint = point
        isSelecting = true
        
        if (tripleClick) {
            // Обработка тройного клика
            handleTripleClick(point)
        } else {
            // Обычный выбор
            handleNormalSelection(point)
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isSelecting) return false
        
        val currentPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        if (tripleClick) {
            // Обработка перетаскивания при тройном клике
            handleTripleClickDrag(currentPoint)
        } else {
            // Обычное перетаскивание для прямоугольного выделения
            handleNormalDrag(start, currentPoint)
        }
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (!isSelecting) return false
        
        val endPoint = offsetToPoint(offset)
        val start = startPoint ?: return false
        
        if (tripleClick) {
            // Завершение тройного клика
            handleTripleClickRelease(endPoint)
            tripleClick = false
        } else {
            // Завершение обычного выбора
            handleNormalRelease(start, endPoint)
        }
        
        // Сбросить состояние
        startPoint = null
        isSelecting = false
        
        return true
    }
    
    private fun handleNormalSelection(point: Point) {
        // Найти ближайшую линию к точке
        val closestLine = findClosestLine(point)
        if (closestLine != null) {
            if (!selectedLines.contains(closestLine)) {
                selectedLines.add(closestLine)
                println("Добавлена линия к выделению: ${closestLine.determineAX()}, ${closestLine.determineAY()} -> ${closestLine.determineBX()}, ${closestLine.determineBY()}")
            }
        }
    }
    
    private fun handleNormalDrag(start: Point, current: Point) {
        // Создать прямоугольник выделения
        val minX = minOf(start.x, current.x)
        val maxX = maxOf(start.x, current.x)
        val minY = minOf(start.y, current.y)
        val maxY = maxOf(start.y, current.y)
        
        val linesInBox = findLinesInBox(minX, maxX, minY, maxY)
        selectedLines.clear()
        selectedLines.addAll(linesInBox)
        
        println("Прямоугольное выделение: ${linesInBox.size} линий")
    }
    
    private fun handleNormalRelease(start: Point, end: Point) {
        // Если точки близки, выбрать ближайшую линию
        if (start.distance(end) <= 0.001) {
            val closestLine = findClosestLine(start)
            if (closestLine != null) {
                selectedLines.clear()
                selectedLines.add(closestLine)
                println("Выбрана ближайшая линия")
            }
        } else {
            // Прямоугольное выделение
            val minX = minOf(start.x, end.x)
            val maxX = maxOf(start.x, end.x)
            val minY = minOf(start.y, end.y)
            val maxY = maxOf(start.y, end.y)
            
            val linesInBox = findLinesInBox(minX, maxX, minY, maxY)
            selectedLines.clear()
            selectedLines.addAll(linesInBox)
            println("Завершено прямоугольное выделение: ${linesInBox.size} линий")
        }
    }
    
    private fun handleTripleClick(point: Point) {
        when (selectionMode) {
            SelectionMode.MOVE -> {
                // Переключиться на режим перемещения
                println("Тройной клик: режим перемещения")
            }
            SelectionMode.MOVE4P -> {
                // Переключиться на режим перемещения 4 точек
                println("Тройной клик: режим перемещения 4 точек")
            }
            SelectionMode.COPY -> {
                // Переключиться на режим копирования
                println("Тройной клик: режим копирования")
            }
            SelectionMode.COPY4P -> {
                // Переключиться на режим копирования 4 точек
                println("Тройной клик: режим копирования 4 точек")
            }
            SelectionMode.MIRROR -> {
                // Переключиться на режим зеркального отражения
                println("Тройной клик: режим зеркального отражения")
            }
            else -> {
                // Обычный выбор
                handleNormalSelection(point)
            }
        }
    }
    
    private fun handleTripleClickDrag(point: Point) {
        when (selectionMode) {
            SelectionMode.MOVE -> {
                // Обработка перетаскивания в режиме перемещения
                println("Перетаскивание в режиме перемещения")
            }
            SelectionMode.MOVE4P -> {
                // Обработка перетаскивания в режиме перемещения 4 точек
                println("Перетаскивание в режиме перемещения 4 точек")
            }
            SelectionMode.COPY -> {
                // Обработка перетаскивания в режиме копирования
                println("Перетаскивание в режиме копирования")
            }
            SelectionMode.COPY4P -> {
                // Обработка перетаскивания в режиме копирования 4 точек
                println("Перетаскивание в режиме копирования 4 точек")
            }
            SelectionMode.MIRROR -> {
                // Обработка перетаскивания в режиме зеркального отражения
                println("Перетаскивание в режиме зеркального отражения")
            }
            else -> {
                // Обычное перетаскивание
                startPoint?.let { start ->
                    handleNormalDrag(start, point)
                }
            }
        }
    }
    
    private fun handleTripleClickRelease(point: Point) {
        when (selectionMode) {
            SelectionMode.MOVE -> {
                // Завершение перемещения
                println("Завершено перемещение")
            }
            SelectionMode.MOVE4P -> {
                // Завершение перемещения 4 точек
                println("Завершено перемещение 4 точек")
            }
            SelectionMode.COPY -> {
                // Завершение копирования
                println("Завершено копирование")
            }
            SelectionMode.COPY4P -> {
                // Завершение копирования 4 точек
                println("Завершено копирование 4 точек")
            }
            SelectionMode.MIRROR -> {
                // Завершение зеркального отражения
                println("Завершено зеркальное отражение")
            }
            else -> {
                // Обычное завершение
                startPoint?.let { start ->
                    handleNormalRelease(start, point)
                }
            }
        }
    }
    
    private fun findClosestLine(point: Point): LineSegment? {
        // Здесь должна быть логика поиска ближайшей линии
        // Пока возвращаем null
        return null
    }
    
    private fun findLinesInBox(minX: Double, maxX: Double, minY: Double, maxY: Double): List<LineSegment> {
        // Здесь должна быть логика поиска линий в прямоугольнике
        // Пока возвращаем пустой список
        return emptyList()
    }
    
    fun setSelectionMode(mode: SelectionMode) {
        selectionMode = mode
        println("Режим выбора изменен на: ${getSelectionModeName()}")
    }
    
    fun getSelectionMode(): SelectionMode = selectionMode
    
    fun getSelectionModeName(): String {
        return when (selectionMode) {
            SelectionMode.NORMAL -> "Обычный"
            SelectionMode.MOVE -> "Перемещение"
            SelectionMode.MOVE4P -> "Перемещение 4 точек"
            SelectionMode.COPY -> "Копирование"
            SelectionMode.COPY4P -> "Копирование 4 точек"
            SelectionMode.MIRROR -> "Зеркальное отражение"
        }
    }
    
    fun setTripleClick(enabled: Boolean) {
        tripleClick = enabled
        println("Тройной клик ${if (enabled) "включен" else "отключен"}")
    }
    
    fun getSelectedLines(): List<LineSegment> = selectedLines.toList()
    
    fun clearSelection() {
        selectedLines.clear()
        println("Выделение очищено")
    }
    
    fun getSelectionCount(): Int = selectedLines.size
    
    fun isTripleClick(): Boolean = tripleClick
    
    override fun getName(): String = "Выбор линий сгиба"
    
    override fun getDescription(): String = "Выберите линии сгиба для редактирования"
} 