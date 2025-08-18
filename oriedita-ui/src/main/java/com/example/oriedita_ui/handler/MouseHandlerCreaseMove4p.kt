package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик перемещения 4 точек
 * Адаптированная версия MouseHandlerCreaseMove4p для Android
 */
class MouseHandlerCreaseMove4p : BaseMouseHandler() {
    
    private var selectedPoints = mutableListOf<Point>()
    private var isSelecting = false
    private var selectedLines = mutableListOf<LineSegment>()
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        isSelecting = true
        
        // Найти ближайшую точку
        val closestPoint = findClosestPoint(point)
        
        when (selectedPoints.size) {
            0 -> {
                // Первый этап - выбор первой точки
                if (point.distance(closestPoint) < getSelectionDistance()) {
                    selectedPoints.add(closestPoint)
                    println("Выбрана первая точка: (${closestPoint.x}, ${closestPoint.y})")
                }
            }
            1 -> {
                // Второй этап - выбор второй точки
                if (point.distance(closestPoint) < getSelectionDistance()) {
                    // Проверить, что точки не совпадают
                    if (selectedPoints[0].distance(closestPoint) > 0.001) {
                        selectedPoints.add(closestPoint)
                        println("Выбрана вторая точка: (${closestPoint.x}, ${closestPoint.y})")
                    } else {
                        // Точки совпадают - сбросить выбор
                        selectedPoints.clear()
                        println("Точки совпадают - выбор сброшен")
                    }
                } else {
                    // Точка не выбрана - сбросить выбор
                    selectedPoints.clear()
                    println("Точка не выбрана - выбор сброшен")
                }
            }
            2 -> {
                // Третий этап - выбор третьей точки
                if (point.distance(closestPoint) < getSelectionDistance()) {
                    selectedPoints.add(closestPoint)
                    println("Выбрана третья точка: (${closestPoint.x}, ${closestPoint.y})")
                } else {
                    // Точка не выбрана - сбросить выбор
                    selectedPoints.clear()
                    println("Точка не выбрана - выбор сброшен")
                }
            }
            3 -> {
                // Четвертый этап - выбор четвертой точки
                if (point.distance(closestPoint) < getSelectionDistance()) {
                    // Проверить, что точки не совпадают
                    if (selectedPoints[2].distance(closestPoint) > 0.001) {
                        selectedPoints.add(closestPoint)
                        println("Выбрана четвертая точка: (${closestPoint.x}, ${closestPoint.y})")
                        
                        // Выполнить перемещение
                        performMove()
                    } else {
                        // Точки совпадают - сбросить выбор
                        selectedPoints.clear()
                        println("Точки совпадают - выбор сброшен")
                    }
                } else {
                    // Точка не выбрана - сбросить выбор
                    selectedPoints.clear()
                    println("Точка не выбрана - выбор сброшен")
                }
            }
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // Перетаскивание не используется в этом обработчике
        return false
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (selectedPoints.size == 4) {
            // Завершить операцию
            isSelecting = false
            println("Завершено перемещение 4 точек")
        }
        
        return true
    }
    
    private fun performMove() {
        if (selectedPoints.size != 4) return
        
        // Создать временный набор линий для перемещения
        val tempLineSet = createTempLineSet()
        
        // Удалить выбранные линии из основного набора
        removeSelectedLines()
        
        // Переместить временный набор по 4 точкам
        tempLineSet.move4p(
            selectedPoints[0], // Исходная точка 1
            selectedPoints[1], // Исходная точка 2
            selectedPoints[2], // Целевая точка 1
            selectedPoints[3]  // Целевая точка 2
        )
        
        // Добавить перемещенные линии обратно
        val oldTotal = getTotalLinesCount()
        addMovedLines(tempLineSet)
        val newTotal = getTotalLinesCount()
        
        // Разделить линии с новыми линиями
        divideLinesWithNewLines(oldTotal, newTotal)
        
        // Снять выделение
        clearSelection()
        
        println("Выполнено перемещение 4 точек: $oldTotal -> $newTotal линий")
    }
    
    private fun createTempLineSet(): TempLineSet {
        val tempSet = TempLineSet()
        selectedLines.forEach { line ->
            tempSet.addLine(line)
        }
        return tempSet
    }
    
    private fun removeSelectedLines() {
        // Здесь должна быть логика удаления выбранных линий
        println("Удалены выбранные линии")
    }
    
    private fun addMovedLines(tempLineSet: TempLineSet) {
        // Здесь должна быть логика добавления перемещенных линий
        val movedLines = tempLineSet.getLines()
        println("Добавлены перемещенные линии: ${movedLines.size}")
    }
    
    private fun divideLinesWithNewLines(oldTotal: Int, newTotal: Int) {
        // Здесь должна быть логика разделения линий с новыми линиями
        println("Разделение линий: $oldTotal -> $newTotal")
    }
    
    private fun getTotalLinesCount(): Int {
        // Здесь должна быть логика подсчета общего количества линий
        return 0
    }
    
    private fun clearSelection() {
        selectedPoints.clear()
        selectedLines.clear()
        println("Выделение очищено")
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
    
    fun setSelectedLines(lines: List<LineSegment>) {
        selectedLines.clear()
        selectedLines.addAll(lines)
    }
    
    fun getSelectedLines(): List<LineSegment> = selectedLines.toList()
    
    fun getSelectedPoints(): List<Point> = selectedPoints.toList()
    
    fun getPointCount(): Int = selectedPoints.size
    
    fun isSelecting(): Boolean = isSelecting
    
    fun clearPoints() {
        selectedPoints.clear()
        println("Точки очищены")
    }
    
    // Временный класс для работы с набором линий
    private class TempLineSet {
        private val lines = mutableListOf<LineSegment>()
        
        fun move4p(source1: Point, source2: Point, target1: Point, target2: Point) {
            // Вычислить преобразование из двух пар точек
            val transformation = calculateTransformation(source1, source2, target1, target2)
            
            // Применить преобразование ко всем линиям
            lines.forEach { line ->
                val newStart = transformPoint(line.determineAX(), line.determineAY(), transformation)
                val newEnd = transformPoint(line.determineBX(), line.determineBY(), transformation)
                // Обновить позицию линии
            }
        }
        
        private fun calculateTransformation(source1: Point, source2: Point, target1: Point, target2: Point): TransformationMatrix {
            // Здесь должна быть логика вычисления матрицы преобразования
            // Пока возвращаем единичную матрицу
            return TransformationMatrix()
        }
        
        private fun transformPoint(x: Double, y: Double, transformation: TransformationMatrix): Point {
            // Здесь должна быть логика применения преобразования к точке
            return Point(x, y)
        }
        
        fun addLine(line: LineSegment) {
            lines.add(line)
        }
        
        fun getLines(): List<LineSegment> = lines.toList()
    }
    
    // Класс для матрицы преобразования
    private class TransformationMatrix {
        // Здесь должны быть поля матрицы преобразования
    }
    
    override fun getName(): String = "Перемещение 4 точек"
    
    override fun getDescription(): String = "Переместите линии по 4 точкам"
} 