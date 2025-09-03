package com.example.oriedita_ui.viewmodel

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import com.example.oriedita_ui.service.Project
import com.example.oriedita_ui.service.ProjectRenderer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.oriedita_ui.ui.CanvasTool
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

sealed class CanvasObject {
    data class FreePath(
        val path: Path,
        val color: Color = Color.Black,
        val strokeWidth: Float = 3f,
        val type: LineType = LineType.EDGE
    ) : CanvasObject()
    
    data class Line(
        val start: Offset,
        val end: Offset,
        val color: Color = Color.Blue,
        val strokeWidth: Float = 3f,
        val type: LineType = LineType.EDGE
    ) : CanvasObject()
    
    data class Point(
        val position: Offset,
        val color: Color = Color.Red,
        val radius: Float = 7f
    ) : CanvasObject()
    
    data class Circle(
        val center: Offset,
        val radius: Float,
        val color: Color = Color.Green,
        val strokeWidth: Float = 3f,
        val type: LineType = LineType.EDGE
    ) : CanvasObject()
    
    data class Text(
        val position: Offset,
        val text: String,
        val color: Color = Color.Black,
        val fontSize: Float = 16f
    ) : CanvasObject()
}

enum class LineType {
    EDGE,      // Краевая линия
    MOUNTAIN,  // Горная линия
    VALLEY,    // Долинная линия
    AUX        // Вспомогательная линия
}

class CanvasViewModel : ViewModel() {
    private val _objects = MutableStateFlow<List<CanvasObject>>(emptyList())
    val objects: StateFlow<List<CanvasObject>> = _objects.asStateFlow()
    
    private val _selectedObjects = MutableStateFlow<Set<CanvasObject>>(emptySet())
    val selectedObjects: StateFlow<Set<CanvasObject>> = _selectedObjects.asStateFlow()
    
    private val _currentLineType = MutableStateFlow(LineType.EDGE)
    val currentLineType: StateFlow<LineType> = _currentLineType.asStateFlow()

    private var currentPath: Path? = null
    private var currentLineStart: Offset? = null
    private var currentCircleCenter: Offset? = null
    private var currentTool: CanvasTool = CanvasTool.DrawCreaseFree
    
    // Состояния для инструментов
    private var lastLine: CanvasObject.Line? = null // Для ParallelDraw
    private var selectedLine: CanvasObject.Line? = null // Для LengthenCrease
    private var circlePoints = mutableListOf<Offset>() // Для CircleDrawThreePoint
    private var lassoPoints = mutableListOf<Offset>() // Для SelectLasso
    private var moveStartPoint: Offset? = null // Для MoveCreasePattern
    private var selectedPolygon = mutableListOf<Offset>() // Для SelectPolygon
    private var textInput: String? = null // Для Text
    private var angleSystemPoints = mutableListOf<Offset>() // Для AngleSystem
    private var axiomPoints = mutableListOf<Offset>() // Для Axiom5 и Axiom7

    fun setTool(tool: CanvasTool) {
        currentTool = tool
        // Сброс состояний при смене инструмента
        currentPath = null
        currentLineStart = null
        currentCircleCenter = null
        lastLine = null
        selectedLine = null
        circlePoints.clear()
        lassoPoints.clear()
        moveStartPoint = null
        selectedPolygon.clear()
        textInput = null
        angleSystemPoints.clear()
        axiomPoints.clear()
    }

    fun getCurrentTool(): CanvasTool = currentTool

    /** Загружает проект и конвертирует его элементы в объекты канваса */
    suspend fun loadProject(context: Context, project: Project) {
        val objects = ProjectRenderer.loadProjectObjects(context, project)
        _objects.value = objects
    }

    fun onDown(offset: Offset) {
        when (currentTool) {
            is CanvasTool.DrawCreaseFree -> {
                currentPath = Path().apply { moveTo(offset.x, offset.y) }
            }
            is CanvasTool.MoveCreasePattern -> {
                moveStartPoint = offset
            }
            is CanvasTool.LineSegmentDelete -> {
                val nearestLine = findNearestLine(offset)
                if (nearestLine != null) {
                    _objects.value = _objects.value.filter { it != nearestLine }
                }
            }
            is CanvasTool.ChangeCreaseType -> {
                val nearestLine = findNearestLine(offset)
                if (nearestLine != null) {
                    val newType = when (nearestLine.type) {
                        LineType.EDGE -> LineType.MOUNTAIN
                        LineType.MOUNTAIN -> LineType.VALLEY
                        LineType.VALLEY -> LineType.AUX
                        LineType.AUX -> LineType.EDGE
                    }
                    _objects.value = _objects.value.map { 
                        if (it == nearestLine) (it as CanvasObject.Line).copy(type = newType) else it 
                    }
                }
            }
            is CanvasTool.DrawPoint -> {
                _objects.value = _objects.value + CanvasObject.Point(offset)
            }
            is CanvasTool.DeletePoint -> {
                val nearestPoint = findNearestPoint(offset)
                if (nearestPoint != null) {
                    _objects.value = _objects.value.filter { it != nearestPoint }
                }
            }
            is CanvasTool.CircleDraw, is CanvasTool.CircleDrawFree -> {
                currentCircleCenter = offset
            }
            is CanvasTool.CircleDrawThreePoint -> {
                if (circlePoints.size < 3) {
                    circlePoints.add(offset)
                    if (circlePoints.size == 3) {
                        createCircleFromThreePoints()
                    }
                }
            }
            is CanvasTool.ParallelDraw -> {
                currentLineStart = offset
            }
            is CanvasTool.PerpendicularDraw -> {
                currentLineStart = offset
            }
            is CanvasTool.LengthenCrease -> {
                selectedLine = findNearestLine(offset)
                if (selectedLine != null) {
                    currentLineStart = offset
                }
            }
            is CanvasTool.CreaseSelect -> {
                val nearestLine = findNearestLine(offset)
                if (nearestLine != null) {
                    _selectedObjects.value = _selectedObjects.value + nearestLine
                }
            }
            is CanvasTool.CreaseUnselect -> {
                val nearestLine = findNearestLine(offset)
                if (nearestLine != null) {
                    _selectedObjects.value = _selectedObjects.value - nearestLine
                }
            }
            is CanvasTool.SelectLasso -> {
                lassoPoints.clear()
                lassoPoints.add(offset)
            }
            is CanvasTool.UnselectLasso -> {
                lassoPoints.clear()
                lassoPoints.add(offset)
            }
            is CanvasTool.SelectPolygon -> {
                selectedPolygon.add(offset)
            }
            is CanvasTool.UnselectPolygon -> {
                selectedPolygon.clear()
            }
            is CanvasTool.Text -> {
                textInput = ""
                // Здесь можно показать диалог ввода текста
            }
            is CanvasTool.AngleSystem -> {
                if (angleSystemPoints.size < 3) {
                    angleSystemPoints.add(offset)
                }
            }
            is CanvasTool.Axiom5, is CanvasTool.Axiom7 -> {
                if (axiomPoints.size < 3) {
                    axiomPoints.add(offset)
                }
            }
            else -> {}
        }
    }

    fun onMove(offset: Offset) {
        when (currentTool) {
            is CanvasTool.DrawCreaseFree -> {
                currentPath?.lineTo(offset.x, offset.y)
            }
            is CanvasTool.MoveCreasePattern -> {
                moveStartPoint?.let { start ->
                    val dx = offset.x - start.x
                    val dy = offset.y - start.y
                    _objects.value = _objects.value.map { obj ->
                        when (obj) {
                            is CanvasObject.Line -> obj.copy(
                                start = Offset(obj.start.x + dx, obj.start.y + dy),
                                end = Offset(obj.end.x + dx, obj.end.y + dy)
                            )
                            is CanvasObject.Point -> obj.copy(
                                position = Offset(obj.position.x + dx, obj.position.y + dy)
                            )
                            is CanvasObject.Circle -> obj.copy(
                                center = Offset(obj.center.x + dx, obj.center.y + dy)
                            )
                            is CanvasObject.Text -> obj.copy(
                                position = Offset(obj.position.x + dx, obj.position.y + dy)
                            )
                            else -> obj
                        }
                    }
                    moveStartPoint = offset
                }
            }
            is CanvasTool.ParallelDraw -> {
                currentLineStart?.let { start ->
                    lastLine?.let { last ->
                        // Вычисляем параллельную линию
                        val dx = last.end.x - last.start.x
                        val dy = last.end.y - last.start.y
                        val length = kotlin.math.sqrt(dx * dx + dy * dy)
                        val angle = atan2(dy, dx)
                        
                        // Создаем параллельную линию
                        val parallelStart = Offset(
                            start.x + dy / length * 20f,
                            start.y - dx / length * 20f
                        )
                        val parallelEnd = Offset(
                            offset.x + dy / length * 20f,
                            offset.y - dx / length * 20f
                        )
                        _objects.value = _objects.value + CanvasObject.Line(
                            parallelStart,
                            parallelEnd,
                            type = _currentLineType.value
                        )
                    }
                }
            }
            is CanvasTool.PerpendicularDraw -> {
                currentLineStart?.let { start ->
                    lastLine?.let { last ->
                        // Вычисляем перпендикулярную линию
                        val dx = last.end.x - last.start.x
                        val dy = last.end.y - last.start.y
                        val length = kotlin.math.sqrt(dx * dx + dy * dy)
                        val angle = atan2(dy, dx) + PI / 2
                        
                        val perpendicularEnd = Offset(
                            start.x + cos(angle).toFloat() * length,
                            start.y + sin(angle).toFloat() * length
                        )
                        _objects.value = _objects.value + CanvasObject.Line(
                            start,
                            perpendicularEnd,
                            type = _currentLineType.value
                        )
                    }
                }
            }
            is CanvasTool.LengthenCrease -> {
                selectedLine?.let { line ->
                    currentLineStart?.let { start ->
                        // Удлиняем линию в направлении движения
                        val dx = offset.x - start.x
                        val dy = offset.y - start.y
                        val length = kotlin.math.sqrt(dx * dx + dy * dy)
                        val angle = atan2(dy, dx)
                        
                        val newEnd = Offset(
                            line.end.x + cos(angle).toFloat() * length,
                            line.end.y + sin(angle).toFloat() * length
                        )
                        _objects.value = _objects.value.map { 
                            if (it == line) (it as CanvasObject.Line).copy(end = newEnd) else it 
                        }
                    }
                }
            }
            is CanvasTool.SelectLasso, is CanvasTool.UnselectLasso -> {
                lassoPoints.add(offset)
            }
            else -> {}
        }
    }

    fun onUp(offset: Offset) {
        when (currentTool) {
            is CanvasTool.DrawCreaseFree -> {
                currentPath?.let { _objects.value = _objects.value + CanvasObject.FreePath(it, type = _currentLineType.value) }
                currentPath = null
            }
            is CanvasTool.MoveCreasePattern -> {
                moveStartPoint = null
            }
            is CanvasTool.ParallelDraw -> {
                currentLineStart?.let { start ->
                    _objects.value = _objects.value + CanvasObject.Line(start, offset, type = _currentLineType.value)
                    lastLine = CanvasObject.Line(start, offset, type = _currentLineType.value)
                }
                currentLineStart = null
            }
            is CanvasTool.PerpendicularDraw -> {
                currentLineStart?.let { start ->
                    lastLine?.let { last ->
                        val dx = last.end.x - last.start.x
                        val dy = last.end.y - last.start.y
                        val length = kotlin.math.sqrt(dx * dx + dy * dy)
                        val angle = atan2(dy, dx) + PI / 2
                        
                        val perpendicularEnd = Offset(
                            start.x + cos(angle).toFloat() * length,
                            start.y + sin(angle).toFloat() * length
                        )
                        _objects.value = _objects.value + CanvasObject.Line(
                            start,
                            perpendicularEnd,
                            type = _currentLineType.value
                        )
                    }
                }
                currentLineStart = null
            }
            is CanvasTool.CircleDraw, is CanvasTool.CircleDrawFree -> {
                currentCircleCenter?.let { center ->
                    val radius = (offset - center).getDistance()
                    _objects.value = _objects.value + CanvasObject.Circle(center, radius, type = _currentLineType.value)
                }
                currentCircleCenter = null
            }
            is CanvasTool.LengthenCrease -> {
                selectedLine = null
                currentLineStart = null
            }
            is CanvasTool.SelectLasso -> {
                // Выбираем все объекты внутри лассо
                val selected = _objects.value.filter { obj ->
                    when (obj) {
                        is CanvasObject.Line -> isPointInLasso(obj.start) || isPointInLasso(obj.end)
                        is CanvasObject.Point -> isPointInLasso(obj.position)
                        is CanvasObject.Circle -> isPointInLasso(obj.center)
                        is CanvasObject.Text -> isPointInLasso(obj.position)
                        else -> false
                    }
                }
                _selectedObjects.value = _selectedObjects.value + selected
                lassoPoints.clear()
            }
            is CanvasTool.UnselectLasso -> {
                // Отменяем выбор объектов внутри лассо
                val unselected = _objects.value.filter { obj ->
                    when (obj) {
                        is CanvasObject.Line -> isPointInLasso(obj.start) || isPointInLasso(obj.end)
                        is CanvasObject.Point -> isPointInLasso(obj.position)
                        is CanvasObject.Circle -> isPointInLasso(obj.center)
                        is CanvasObject.Text -> isPointInLasso(obj.position)
                        else -> false
                    }
                }
                _selectedObjects.value = _selectedObjects.value - unselected
                lassoPoints.clear()
            }
            else -> {}
        }
    }

    private fun findNearestLine(point: Offset): CanvasObject.Line? {
        var nearestLine: CanvasObject.Line? = null
        var minDistance = Float.MAX_VALUE
        
        for (obj in _objects.value) {
            if (obj is CanvasObject.Line) {
                val distance = distanceToLine(point, obj.start, obj.end)
                if (distance < minDistance) {
                    minDistance = distance
                    nearestLine = obj
                }
            }
        }
        
        return if (minDistance < 20f) nearestLine else null
    }

    private fun findNearestPoint(point: Offset): CanvasObject.Point? {
        var nearestPoint: CanvasObject.Point? = null
        var minDistance = Float.MAX_VALUE
        
        for (obj in _objects.value) {
            if (obj is CanvasObject.Point) {
                val distance = (point - obj.position).getDistance()
                if (distance < minDistance) {
                    minDistance = distance
                    nearestPoint = obj
                }
            }
        }
        
        return if (minDistance < 20f) nearestPoint else null
    }

    private fun distanceToLine(point: Offset, lineStart: Offset, lineEnd: Offset): Float {
        val lineLength = (lineEnd - lineStart).getDistance()
        if (lineLength == 0f) return (point - lineStart).getDistance()
        
        val t = ((point.x - lineStart.x) * (lineEnd.x - lineStart.x) +
                (point.y - lineStart.y) * (lineEnd.y - lineStart.y)) /
                (lineLength * lineLength)
        
        val tClamped = t.coerceIn(0f, 1f)
        val projection = Offset(
            lineStart.x + tClamped * (lineEnd.x - lineStart.x),
            lineStart.y + tClamped * (lineEnd.y - lineStart.y)
        )
        
        return (point - projection).getDistance()
    }

    private fun createCircleFromThreePoints() {
        if (circlePoints.size != 3) return
        
        val p1 = circlePoints[0]
        val p2 = circlePoints[1]
        val p3 = circlePoints[2]
        
        // Вычисляем центр и радиус окружности по трем точкам
        val d = 2 * (p1.x * (p2.y - p3.y) + p2.x * (p3.y - p1.y) + p3.x * (p1.y - p2.y))
        if (abs(d) < 0.0001f) return // Точки на одной прямой
        
        val ux = ((p1.x * p1.x + p1.y * p1.y) * (p2.y - p3.y) +
                (p2.x * p2.x + p2.y * p2.y) * (p3.y - p1.y) +
                (p3.x * p3.x + p3.y * p3.y) * (p1.y - p2.y)) / d
        
        val uy = ((p1.x * p1.x + p1.y * p1.y) * (p3.x - p2.x) +
                (p2.x * p2.x + p2.y * p2.y) * (p1.x - p3.x) +
                (p3.x * p3.x + p3.y * p3.y) * (p2.x - p1.x)) / d
        
        val center = Offset(ux, uy)
        val radius = (center - p1).getDistance()
        
        _objects.value = _objects.value + CanvasObject.Circle(center, radius, type = _currentLineType.value)
        circlePoints.clear()
    }

    private fun isPointInLasso(point: Offset): Boolean {
        if (lassoPoints.size < 3) return false
        
        var inside = false
        var j = lassoPoints.size - 1
        
        for (i in lassoPoints.indices) {
            if ((lassoPoints[i].y > point.y) != (lassoPoints[j].y > point.y) &&
                (point.x < (lassoPoints[j].x - lassoPoints[i].x) * (point.y - lassoPoints[i].y) /
                        (lassoPoints[j].y - lassoPoints[i].y) + lassoPoints[i].x)) {
                inside = !inside
            }
            j = i
        }
        
        return inside
    }

    fun setLineType(type: LineType) {
        _currentLineType.value = type
    }

    fun undo() {
        if (_objects.value.isNotEmpty()) {
            _objects.value = _objects.value.dropLast(1)
        }
    }
    
    /**
     * Устанавливает объекты для предпросмотра
     * Используется только в Preview методах
     */
    fun setObjectsForPreview(objects: List<CanvasObject>) {
        _objects.value = objects
    }
}

private fun Offset.getDistance(): Float = kotlin.math.sqrt(x * x + y * y)