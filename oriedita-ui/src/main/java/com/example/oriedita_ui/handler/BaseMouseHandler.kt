package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_common.editor.canvas.FoldLineAdditionalInputMode
import com.example.oriedita_common.editor.canvas.LineStyle
import com.example.oriedita_common.editor.drawing.tools.Camera
import com.example.oriedita_core.origami.crease_pattern.FoldLineSet
import com.example.oriedita_core.origami.crease_pattern.LineSegmentSet
import com.example.oriedita_core.origami.crease_pattern.elements.Circle
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor
import com.example.oriedita_core.origami.crease_pattern.CustomLineTypes
import com.example.oriedita_data.canvas.CreasePattern_Worker
import com.example.oriedita_data.canvas.OperationFrame
import com.example.oriedita_data.canvas.TextWorker
import com.example.oriedita_data.databinding.ApplicationModel
import com.example.oriedita_data.databinding.CanvasModel
import com.example.oriedita_data.databinding.GridModel
import com.example.oriedita_data.drawing.DrawingUtil
import com.example.oriedita_data.save.Save
import com.example.oriedita_data.save.SaveProvider
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

/**
 * Базовый класс для обработчиков мыши в Compose
 * Адаптированная версия BaseMouseHandler для Android
 */
abstract class BaseMouseHandler {
    
    // Ссылка на CreasePattern_Worker для доступа к данным
    protected var creasePatternWorker: CreasePattern_Worker? = null
    
    // Временные списки для хранения линий (в реальной реализации должны быть подключены к модели)
    protected open val lineStep = mutableListOf<LineSegment>()
    protected open val lineCandidate = mutableListOf<LineSegment>()
    
    // Временные списки для хранения кругов (в реальной реализации должны быть подключены к модели)
    protected open val circleStep = mutableListOf<Circle>()
    protected open val circleCandidate = mutableListOf<Circle>()
    

    
    /**
     * Обработка нажатия мыши
     */
    open fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        return false
    }
    
    /**
     * Обработка перетаскивания мыши
     */
    open fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        return false
    }
    
    /**
     * Обработка отпускания мыши
     */
    open fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        return false
    }
    
    /**
     * Обработка двойного клика
     */
    open fun onDoubleClick(offset: Offset): Boolean {
        return false
    }
    
    /**
     * Преобразование Offset в Point
     */
    protected fun offsetToPoint(offset: Offset): Point {
        return Point(offset.x.toDouble(), offset.y.toDouble())
    }
    
    /**
     * Преобразование Point в Offset
     */
    protected fun pointToOffset(point: Point): Offset {
        return Offset(point.x.toFloat(), point.y.toFloat())
    }
    
    /**
     * Получение названия обработчика
     */
    abstract fun getName(): String
    
    /**
     * Получение описания обработчика
     */
    abstract fun getDescription(): String
    
    /**
     * Сброс состояния обработчика
     */
    open fun reset() {
        clearLineStep()
        clearLineCandidate()
        clearCircleStep()
        clearCircleCandidate()
        println("Базовый обработчик мыши сброшен")
    }
    
    // Методы для работы с линиями (в реальной реализации должны быть подключены к модели)
    protected open fun addLineStep(line: LineSegment) {
        lineStep.add(line)
        creasePatternWorker?.lineStepAdd(line)
    }
    
    protected open fun getLineStepSize(): Int = lineStep.size
    
    protected open fun getLineStep(index: Int): LineSegment = lineStep[index]
    
    protected open fun setLineStep(index: Int, line: LineSegment) {
        if (index < lineStep.size) {
            lineStep[index] = line
        }
    }
    
    protected open fun clearLineStep() {
        lineStep.clear()
    }
    
    protected open fun addLineCandidate(line: LineSegment) {
        lineCandidate.clear() // Очищаем предыдущих кандидатов
        lineCandidate.add(line)
    }
    
    protected open fun clearLineCandidate() {
        lineCandidate.clear()
    }
    
    // Методы для работы с кругами (в реальной реализации должны быть подключены к модели)
    protected open fun addCircleStep(circle: Circle) {
        circleStep.add(circle)
        creasePatternWorker?.addCircle(circle.determineCenter().x, circle.determineCenter().y, circle.getR(), circle.color)
    }
    
    protected open fun getCircleStepSize(): Int = circleStep.size
    
    protected open fun getCircleStep(index: Int): Circle = circleStep[index]
    
    protected open fun setCircleStep(index: Int, circle: Circle) {
        if (index < circleStep.size) {
            circleStep[index] = circle
        }
    }
    
    protected open fun clearCircleStep() {
        circleStep.clear()
    }
    
    protected open fun addCircleCandidate(circle: Circle) {
        circleCandidate.clear() // Очищаем предыдущих кандидатов
        circleCandidate.add(circle)
    }
    
    protected open fun clearCircleCandidate() {
        circleCandidate.clear()
    }
    
    // Реализации методов, основанные на CreasePattern_Worker
    protected open fun getClosestPoint(point: Point): Point {
        return creasePatternWorker?.getClosestPoint(point) ?: point
    }
    
    protected open fun getSelectionDistance(): Double {
        return creasePatternWorker?.getSelectionDistance() ?: 10.0
    }
    
    protected open fun isGridInputAssistEnabled(): Boolean {
        return creasePatternWorker?.getGridInputAssist() ?: false
    }
    
    protected open fun getLineColor(): LineColor {
        return creasePatternWorker?.getLineColor() ?: LineColor.MAGENTA_5
    }
    
    protected open fun addLineSegment(lineSegment: LineSegment) {
        creasePatternWorker?.addLineSegment(lineSegment)
    }
    
    protected open fun addCircle(circle: Circle) {
        creasePatternWorker?.addCircle(circle.determineCenter().x, circle.determineCenter().y, circle.getR(), circle.color)
    }
    
    protected open fun getClosestLineSegment(point: Point): LineSegment {
        return creasePatternWorker?.getClosestLineSegment(point) 
            ?: LineSegment(Point(0.0, 0.0), Point(0.0, 0.0), LineColor.BLACK_0)
    }
    
    protected open fun setLineColor(lineSegment: LineSegment, color: LineColor) {
        // Получаем FoldLineSet для обновления цвета линии
        val foldLineSet = getFoldLineSet()
        if (foldLineSet != null) {
            try {
                // Обновляем цвет линии в FoldLineSet
                foldLineSet.setColor(lineSegment, color)
                
                // Также обновляем цвет в текущих временных линиях, если они совпадают
                lineStep.forEachIndexed { index, step ->
                    if (step.a == lineSegment.a && step.b == lineSegment.b) {
                        lineStep[index] = LineSegment(step.a, step.b, color)
                    }
                }
                
                lineCandidate.forEachIndexed { index, candidate ->
                    if (candidate.a == lineSegment.a && candidate.b == lineSegment.b) {
                        lineCandidate[index] = LineSegment(candidate.a, candidate.b, color)
                    }
                }
                
                // Уведомляем об изменении
                println("Цвет линии успешно изменен на: ${color}")
            } catch (e: Exception) {
                println("Ошибка при изменении цвета линии: ${e.message}")
                // Fallback: создаем новую линию с нужным цветом
                val newSegment = LineSegment(lineSegment.a, lineSegment.b, color)
                // Добавляем новую линию, если не удалось обновить существующую
                addLineSegment(newSegment)
            }
        } else {
            // Если FoldLineSet недоступен, просто создаем новую линию
            val newSegment = LineSegment(lineSegment.a, lineSegment.b, color)
            addLineSegment(newSegment)
            println("FoldLineSet недоступен, создана новая линия с цветом: ${color}")
        }
    }
    
    protected open fun record() {
        creasePatternWorker?.record()
    }
    
    protected open fun getFoldLineSet(): FoldLineSet? {
        return creasePatternWorker?.foldLineSet
    }
    
    protected open fun getAuxFoldLineSet(): FoldLineSet? {
        return creasePatternWorker?.auxFoldLineSet
    }
    
    protected open fun getCamera(): Camera? {
        return creasePatternWorker?.camera
    }
    
    protected open fun getGrid(): com.example.oriedita_data.drawing.Grid? {
        return creasePatternWorker?.grid
    }
    
    protected open fun getFoldLineAdditional(): FoldLineAdditionalInputMode? {
        return creasePatternWorker?.i_foldLine_additional
    }
    
    protected open fun getAuxLineColor(): LineColor {
        return creasePatternWorker?.auxLineColor ?: LineColor.CYAN_3
    }
    
    protected open fun getTotal(): Int {
        return creasePatternWorker?.total ?: 0
    }
    
    protected open fun getCandidateSize(): Int {
        return creasePatternWorker?.candidateSize ?: 0
    }
    
    protected open fun getLineStepList(): List<LineSegment> {
        return creasePatternWorker?.lineStep ?: emptyList()
    }
    
    protected open fun getLineCandidateList(): List<LineSegment> {
        return creasePatternWorker?.lineCandidate ?: emptyList()
    }
    
    protected open fun getCircleStepList(): List<Circle> {
        return creasePatternWorker?.circleStep ?: emptyList()
    }
    
    protected open fun getPointSize(): Int {
        return creasePatternWorker?.pointSize ?: 5
    }
    
    protected open fun isSelectionEmpty(): Boolean {
        return creasePatternWorker?.isSelectionEmpty ?: true
    }
    
    protected open fun refreshIsSelectionEmpty() {
        creasePatternWorker?.refreshIsSelectionEmpty()
    }
    
    protected open fun select_all() {
        creasePatternWorker?.select_all()
    }
    
    protected open fun unselect_all() {
        creasePatternWorker?.unselect_all()
    }
    
    protected open fun select(p0a: Point, p0b: Point) {
        creasePatternWorker?.select(p0a, p0b)
    }
    
    protected open fun unselect(p0a: Point, p0b: Point) {
        creasePatternWorker?.unselect(p0a, p0b)
    }
    
    protected open fun deleteInside_foldingLine(p0a: Point, p0b: Point): Boolean {
        return creasePatternWorker?.deleteInside_foldingLine(p0a, p0b) ?: false
    }
    
    protected open fun deleteInside_edge(p0a: Point, p0b: Point): Boolean {
        return creasePatternWorker?.deleteInside_edge(p0a, p0b) ?: false
    }
    
    protected open fun deleteInside_aux(p0a: Point, p0b: Point): Boolean {
        return creasePatternWorker?.deleteInside_aux(p0a, p0b) ?: false
    }
    
    protected open fun deleteInside(p0a: Point, p0b: Point): Boolean {
        return creasePatternWorker?.deleteInside(p0a, p0b) ?: false
    }
    
    protected open fun MV_change(p0a: Point, p0b: Point): Int {
        return creasePatternWorker?.MV_change(p0a, p0b) ?: 0
    }
    
    protected open fun insideToMountain(p0a: Point, p0b: Point): Boolean {
        return creasePatternWorker?.insideToMountain(p0a, p0b) ?: false
    }
    
    protected open fun insideToValley(p0a: Point, p0b: Point): Boolean {
        return creasePatternWorker?.insideToValley(p0a, p0b) ?: false
    }
    
    protected open fun insideToEdge(p0a: Point, p0b: Point): Boolean {
        return creasePatternWorker?.insideToEdge(p0a, p0b) ?: false
    }
    
    protected open fun insideToAux(p0a: Point, p0b: Point): Boolean {
        return creasePatternWorker?.insideToAux(p0a, p0b) ?: false
    }
    
    protected open fun del_selected_senbun() {
        creasePatternWorker?.del_selected_senbun()
    }
    
    protected open fun v_del_all() {
        creasePatternWorker?.v_del_all()
    }
    
    protected open fun v_del_all_cc() {
        creasePatternWorker?.v_del_all_cc()
    }
    
    protected open fun addPreviewLinesToCp() {
        creasePatternWorker?.addPreviewLinesToCp()
    }
    
    protected open fun extendToIntersectionPoint(lineSegment: LineSegment): LineSegment {
        return creasePatternWorker?.extendToIntersectionPoint(lineSegment) ?: lineSegment
    }
    
    protected open fun getClosestLineStepSegment(point: Point, imin: Int, imax: Int): LineSegment {
        return creasePatternWorker?.getClosestLineStepSegment(point, imin, imax) 
            ?: LineSegment(Point(0.0, 0.0), Point(0.0, 0.0), LineColor.BLACK_0)
    }
    
    protected open fun getClosestCircleMidpoint(point: Point): Circle {
        return (creasePatternWorker?.getClosestCircleMidpoint(point)?.let { circle ->
            // Если getClosestCircleMidpoint возвращает Circle, извлекаем его центр
            Circle(circle.determineCenter(), circle.getR(), circle.color)
        } ?: Circle(point, 1.0, LineColor.BLACK_0))
    }
    
    protected open fun getClosestCircle(point: Point): Circle? {
        return creasePatternWorker?.getClosestCircleMidpoint(point)
    }
    
    protected open fun getGridPosition(point: Point): Point {
        return creasePatternWorker?.getGridPosition(point) ?: point
    }
    
    protected open fun resetLineStep(i: Int) {
        creasePatternWorker?.resetLineStep(i)
    }
    
    protected open fun addCircle(point: Point, radius: Double, color: LineColor) {
        creasePatternWorker?.addCircle(point.x, point.y, radius, color)
    }
    
    protected open fun addCircle(x: Double, y: Double, radius: Double, color: LineColor) {
        creasePatternWorker?.addCircle(x, y, radius, color)
    }
    
    protected open fun addLineSegment_auxiliary(lineSegment: LineSegment) {
        creasePatternWorker?.addLineSegment_auxiliary(lineSegment)
    }
    
    protected open fun selectConnected(point: Point) {
        creasePatternWorker?.selectConnected(point)
    }
    
    protected open fun getCameraPosition(): Point {
        return creasePatternWorker?.cameraPosition ?: Point(0.0, 0.0)
    }
    
    protected open fun getLinePath(): Path {
        val path = Path()
        
        try {
            // Получаем все линии из CreasePattern_Worker
            val allLines = mutableListOf<LineSegment>()
            
            // Добавляем основные линии сгиба из FoldLineSet
            val foldLineSet = getFoldLineSet()
            if (foldLineSet != null) {
                try {
                    // Получаем все линии из основного набора по индексам
                    val total = foldLineSet.getTotal()
                    for (i in 1..total) {
                        val line = foldLineSet.get(i)
                        if (line != null) {
                            allLines.add(line)
                        }
                    }
                } catch (e: Exception) {
                    println("Ошибка при получении основных линий: ${e.message}")
                }
            }
            
            // Добавляем вспомогательные линии из AuxFoldLineSet
            val auxFoldLineSet = getAuxFoldLineSet()
            if (auxFoldLineSet != null) {
                try {
                    // Получаем все вспомогательные линии по индексам
                    val total = auxFoldLineSet.getTotal()
                    for (i in 1..total) {
                        val line = auxFoldLineSet.get(i)
                        if (line != null) {
                            allLines.add(line)
                        }
                    }
                } catch (e: Exception) {
                    println("Ошибка при получении вспомогательных линий: ${e.message}")
                }
            }
            
            // Добавляем временные линии (lineStep) - они имеют приоритет
            allLines.addAll(lineStep)
            
            // Добавляем кандидатов (lineCandidate) - они отображаются поверх
            allLines.addAll(lineCandidate)
            
            // Добавляем линии из LineSegmentSet если доступны
            val lineSegmentSet = creasePatternWorker?.get()
            if (lineSegmentSet != null) {
                try {
                    val numSegments = lineSegmentSet.getNumLineSegments()
                    for (i in 0 until numSegments) {
                        val segment = lineSegmentSet.get(i)
                        if (segment != null) {
                            allLines.add(segment)
                        }
                    }
                } catch (e: Exception) {
                    println("Ошибка при получении сегментов линий: ${e.message}")
                }
            }
            
            // Строим путь из всех линий с учетом их типа и цвета
            if (allLines.isNotEmpty()) {
                // Группируем линии по цвету для лучшей организации
                val linesByColor = allLines.groupBy { it.color }
                
                for ((color, lines) in linesByColor) {
                    if (lines.isNotEmpty()) {
                        // Начинаем новый подпуть для каждой группы цветов
                        val firstLine = lines.first()
                        path.moveTo(firstLine.a.x.toFloat(), firstLine.a.y.toFloat())
                        
                        for (line in lines) {
                            // Проверяем, нужно ли начать новый подпуть
                            // (если линия не связана с предыдущей)
                            if (line.a != firstLine.a && !isConnected(line, firstLine)) {
                                path.moveTo(line.a.x.toFloat(), line.a.y.toFloat())
                            }
                            
                            // Рисуем линию до конечной точки
                            path.lineTo(line.b.x.toFloat(), line.b.y.toFloat())
                        }
                    }
                }
                
                println("Создан путь из ${allLines.size} линий, сгруппированных по ${linesByColor.size} цветам")
            } else {
                println("Нет линий для создания пути")
            }
            
        } catch (e: Exception) {
            println("Ошибка при создании пути: ${e.message}")
            e.printStackTrace()
            // Возвращаем пустой путь в случае ошибки
        }
        
        return path
    }
    
    /**
     * Проверяет, связаны ли две линии
     */
    private fun isConnected(line1: LineSegment, line2: LineSegment): Boolean {
        return line1.b == line2.a || line1.a == line2.b || line1.a == line2.a || line1.b == line2.b
    }
    
    protected open fun setGridInputAssist(enabled: Boolean) {
        creasePatternWorker?.setGridInputAssist(enabled)
    }
    
    protected open fun setAuxLineColor(color: LineColor) {
        creasePatternWorker?.setAuxLineColor(color)
    }
    
    protected open fun setFoldLineAdditional(mode: FoldLineAdditionalInputMode) {
        creasePatternWorker?.setFoldLineAdditional(mode)
    }
    
    protected open fun check1() {
        creasePatternWorker?.check1()
    }
    
    protected open fun check2() {
        creasePatternWorker?.check2()
    }
    
    protected open fun check3() {
        creasePatternWorker?.check3()
    }
    
    protected open fun check4() {
        creasePatternWorker?.check4()
    }
    
    protected open fun fix1() {
        creasePatternWorker?.fix1()
    }
    
    protected open fun fix2() {
        creasePatternWorker?.fix2()
    }
    
    protected open fun lightenCheck4Color() {
        creasePatternWorker?.lightenCheck4Color()
    }
    
    protected open fun darkenCheck4Color() {
        creasePatternWorker?.darkenCheck4Color()
    }
    
    protected open fun organizeCircles() {
        creasePatternWorker?.organizeCircles()
    }
    
    protected open fun branch_trim() {
        creasePatternWorker?.branch_trim()
    }
    
    protected open fun allMountainValleyChange() {
        creasePatternWorker?.allMountainValleyChange()
    }
    
    protected open fun point_removal() {
        creasePatternWorker?.point_removal()
    }
    
    protected open fun overlapping_line_removal() {
        creasePatternWorker?.overlapping_line_removal()
    }
    
    protected open fun undo(): Boolean {
        return (creasePatternWorker?.undo() as Boolean? ?: "Undo") as Boolean
    }
    
    protected open fun redo(): Boolean {
        return (creasePatternWorker?.redo() as Boolean? ?: "Redo") as Boolean
    }
    
    protected open fun auxUndo() {
        creasePatternWorker?.auxUndo()
    }
    
    protected open fun auxRedo() {
        creasePatternWorker?.auxRedo()
    }
    
    protected open fun auxRecord() {
        creasePatternWorker?.auxRecord()
    }
    
    protected open fun clearCreasePattern() {
        creasePatternWorker?.clearCreasePattern()
    }
    
    protected open fun resetCreasePattern() {
        creasePatternWorker?.reset()
    }
    
    protected open fun initialize() {
        creasePatternWorker?.initialize()
    }
    
    protected open fun setTitle(title: String) {
        creasePatternWorker?.setTitle(title)
    }
    
    protected open fun getTitle(): String {
        return creasePatternWorker?.getS_title() ?: "no title"
    }
    
    protected open fun getFoldLineTotalForSelectFolding(): Int {
        return creasePatternWorker?.foldLineTotalForSelectFolding ?: 0
    }
    
    protected open fun get(): LineSegmentSet {
        return creasePatternWorker?.get() ?: LineSegmentSet()
    }
    
    protected open fun getForSelectFolding(): LineSegmentSet {
        return creasePatternWorker?.getForSelectFolding() ?: LineSegmentSet()
    }
    
    protected open fun getSave_for_export(): Save {
        return creasePatternWorker?.save_for_export ?: SaveProvider.createInstance()
    }
    
    protected open fun getSave_for_export_with_applicationModel(): Save {
        return creasePatternWorker?.save_for_export_with_applicationModel ?: SaveProvider.createInstance()
    }
    
    protected open fun saveAdditionalInformation(save: Save) {
        creasePatternWorker?.saveAdditionalInformation(save)
    }
    
    protected open fun setSave_for_reading(save: Save) {
        creasePatternWorker?.setSave_for_reading(save)
    }
    
    protected open fun setSave_for_reading_tuika(save: Save) {
        creasePatternWorker?.setSave_for_reading_tuika(save)
    }
    
    protected open fun setSaveForPaste(save: Save) {
        creasePatternWorker?.setSaveForPaste(save)
    }
    
    protected open fun setAuxMemo(save: Save) {
        creasePatternWorker?.setAuxMemo(save)
    }
    
    protected open fun setCamera(camera: Camera) {
        creasePatternWorker?.setCamera(camera)
    }
    
    protected open fun setGridConfigurationData(gridModel: GridModel) {
        creasePatternWorker?.setGridConfigurationData(gridModel)
    }
    
    protected open fun setData(e: PropertyChangeEvent ,applicationModel: ApplicationModel) {
        creasePatternWorker?.setData(e, applicationModel)
    }
    
    protected open fun setData(canvasModel: CanvasModel) {
        creasePatternWorker?.setData(canvasModel)
    }
    
    protected open fun addPropertyChangeListener(listener: PropertyChangeListener) {
        creasePatternWorker?.addPropertyChangeListener(listener)
    }
    
    protected open fun removePropertyChangeListener(listener: PropertyChangeListener) {
        creasePatternWorker?.removePropertyChangeListener(listener)
    }
    
    protected open fun drawWithCamera(
        canvas: Canvas,
        paint: Paint,
        displayComments: Boolean,
        displayCpLines: Boolean,
        displayAuxLines: Boolean,
        displayAuxLiveLines: Boolean,
        lineWidth: Float,
        lineStyle: LineStyle,
        f_h_WireframeLineWidth: Float,
        p0x_max: Int,
        p0y_max: Int,
        i_mejirusi_display: Boolean,
        hideOperationFrame: Boolean
    ) {
        try {
            // Получаем камеру для трансформации координат
            val camera = getCamera()
            if (camera == null) {
                println("Камера недоступна для рисования")
                return
            }
            
            // Делегируем рисование в CreasePattern_Worker если он доступен
            // Это позволяет избежать дублирования логики рисования
            creasePatternWorker?.let { worker ->
                // Рисуем сетку через существующий метод Grid.draw()
                if (isGridInputAssistEnabled()) {
                    val grid = getGrid()
                    grid?.draw(
                        canvas as android.graphics.Canvas?,
                        paint as android.graphics.Paint?,
                        camera,
                        p0x_max,
                        p0y_max,
                        true,
                        1.0
                    )
                }
                
                // Рисуем основные линии сгиба через DrawingUtil
                if (displayCpLines) {
                    val foldLineSet = getFoldLineSet()
                    if (foldLineSet != null) {
                        val total = foldLineSet.getTotal()
                        for (i in 1..total) {
                            val line = foldLineSet.get(i)
                            if (line != null && line.color.isFoldingLine()) {
                                // Используем существующий DrawingUtil.drawLineStep
                                DrawingUtil.drawLineStep(
                                    canvas as android.graphics.Canvas?,
                                    paint as android.graphics.Paint?,
                                    line,
                                    camera,
                                    lineWidth,
                                    isGridInputAssistEnabled()
                                )
                            }
                        }
                    }
                }
                
                // Рисуем вспомогательные линии
                if (displayAuxLines) {
                    val auxFoldLineSet = getAuxFoldLineSet()
                    if (auxFoldLineSet != null) {
                        val total = auxFoldLineSet.getTotal()
                        for (i in 1..total) {
                            val line = auxFoldLineSet.get(i)
                            if (line != null) {
                                DrawingUtil.drawLineStep(
                                    canvas as android.graphics.Canvas?,
                                    paint as android.graphics.Paint?,
                                    line,
                                    camera,
                                    lineWidth,
                                    isGridInputAssistEnabled()
                                )
                            }
                        }
                    }
                }
                
                // Рисуем временные линии (lineStep) через DrawingUtil
                if (displayAuxLiveLines) {
                    for (line in lineStep) {
                        DrawingUtil.drawLineStep(
                            canvas as android.graphics.Canvas?,
                            paint as android.graphics.Paint?,
                            line,
                            camera,
                            lineWidth,
                            isGridInputAssistEnabled()
                        )
                    }
                }
                
                // Рисуем кандидатов (lineCandidate) через DrawingUtil
                for (line in lineCandidate) {
                    DrawingUtil.drawLineStep(
                        canvas as android.graphics.Canvas?,
                        paint as android.graphics.Paint?,
                        line,
                        camera,
                        lineWidth,
                        isGridInputAssistEnabled()
                    )
                }
                
                // Рисуем комментарии если включены
                if (displayComments) {
                    DrawingUtil.drawComments(canvas as android.graphics.Canvas?, paint as android.graphics.Paint?, camera, lineWidth)
                }
                
                // Рисуем рамку операции если не скрыта
                if (!hideOperationFrame) {
                    DrawingUtil.drawOperationFrame(canvas as android.graphics.Canvas?, paint as android.graphics.Paint?, camera, lineWidth)
                }
                
                println("Рисование завершено успешно через существующие утилиты")
            } ?: run {
                println("CreasePattern_Worker недоступен, используем базовое рисование")
                // Fallback: базовое рисование если worker недоступен
                drawBasicElements(canvas, paint, camera, lineWidth)
            }
            
        } catch (e: Exception) {
            println("Ошибка при рисовании: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * Базовое рисование элементов (fallback)
     */
    private fun drawBasicElements(canvas: Canvas, paint: Paint, camera: Camera, lineWidth: Float) {
        // Рисуем только временные линии и кандидатов
        for (line in lineStep) {
            drawBasicLine(canvas, paint, line, camera, lineWidth)
        }
        
        for (line in lineCandidate) {
            drawBasicLine(canvas, paint, line, camera, lineWidth)
        }
    }
    
    /**
     * Базовое рисование линии (fallback)
     */
    private fun drawBasicLine(canvas: Canvas, paint: Paint, line: LineSegment, camera: Camera, lineWidth: Float) {
        try {
            // Трансформируем координаты через камеру
            val startPoint = camera.object2TV(line)
            val start = Offset(startPoint.determineAX().toFloat(), startPoint.determineAY().toFloat())
            val end = Offset(startPoint.determineBX().toFloat(), startPoint.determineBY().toFloat())
            
            // Настраиваем paint
            paint.strokeWidth = lineWidth
            paint.color = lineColorToComposeColor(line.color)
            
            // Рисуем линию
            canvas.drawLine(start, end, paint)
            
        } catch (e: Exception) {
            println("Ошибка при базовом рисовании линии: ${e.message}")
        }
    }
    
    /**
     * Конвертирует LineColor в Compose Color
     */
    private fun lineColorToComposeColor(lineColor: LineColor): Color {
        return when (lineColor) {
            LineColor.BLACK_0 -> Color.Black
            LineColor.RED_1 -> Color.Red
            LineColor.BLUE_2 -> Color.Blue
            LineColor.CYAN_3 -> Color.Cyan
            LineColor.ORANGE_4 -> Color(0xFFFF8C00) // Orange
            LineColor.MAGENTA_5 -> Color.Magenta
            LineColor.GREEN_6 -> Color.Green
            LineColor.YELLOW_7 -> Color.Yellow
            LineColor.PURPLE_8 -> Color(0xFFD200FF) // Purple
            LineColor.GREY_10 -> Color.Gray
            LineColor.ANGLE -> Color(0xFF808080) // Dark Gray
            LineColor.NONE -> Color.Transparent
            LineColor.OTHER_9 -> Color(0xFF808080) // Medium Gray
            else -> Color.Gray
        }
    }
    

    
    protected open fun resetCircleStep() {
        creasePatternWorker?.resetCircleStep()
    }
    
    protected open fun getOperationFrame(): OperationFrame {
        return creasePatternWorker?.operationFrame ?: OperationFrame()
    }
    
    protected open fun getNumPolygonCorners(): Int {
        return creasePatternWorker?.numPolygonCorners ?: 4
    }
    
    protected open fun getCustomCircleColor(): LineColor {
        return (creasePatternWorker?.customCircleColor ?: LineColor.BLACK_0) as LineColor
    }
    
    protected open fun getI_select_mode(): CanvasModel.SelectionOperationMode {
        return creasePatternWorker?.i_select_mode ?: CanvasModel.SelectionOperationMode.NORMAL_0
    }
    
    protected open fun getFoldLineDividingNumber(): Int {
        return creasePatternWorker?.foldLineDividingNumber ?: 1
    }
    
    protected open fun getTextWorker(): TextWorker {
        return creasePatternWorker?.textWorker ?: TextWorker()
    }
    
    protected open fun isCheck1(): Boolean {
        return creasePatternWorker?.isCheck1 ?: false
    }
    
    protected open fun isCheck2(): Boolean {
        return creasePatternWorker?.isCheck2 ?: false
    }
    
    protected open fun isCheck3(): Boolean {
        return creasePatternWorker?.isCheck3 ?: false
    }
    
    protected open fun isCheck4(): Boolean {
        return creasePatternWorker?.isCheck4 ?: false
    }
    
    protected open fun set_i_check1(enabled: Boolean) {
        creasePatternWorker?.set_i_check1(enabled)
    }
    
    protected open fun setCheck2(enabled: Boolean) {
        creasePatternWorker?.setCheck2(enabled)
    }
    
    protected open fun setCheck4(enabled: Boolean) {
        creasePatternWorker?.setCheck4(enabled)
    }
    
    protected open fun setFoldLineDividingNumber(number: Int) {
        creasePatternWorker?.setFoldLineDividingNumber(number)
    }
    
    protected open fun setNumPolygonCorners(corners: Int) {
        creasePatternWorker?.setNumPolygonCorners(corners)
    }
    
    protected open fun change_property_in_4kakukei(p0a: Point, p0b: Point): Boolean {
        return creasePatternWorker?.change_property_in_4kakukei(p0a, p0b) ?: false
    }
    
    protected open fun deleteInside_text(p1: Point, p2: Point): Boolean {
        return creasePatternWorker?.deleteInside_text(p1, p2) ?: false
    }
    
    protected open fun insideToReplaceType(p0a: Point, p0b: Point, from: CustomLineTypes, to: CustomLineTypes): Boolean {
        return creasePatternWorker?.insideToReplaceType(p0a, p0b, from, to) ?: false
    }
    
    protected open fun insideToDeleteType(p0a: Point, p0b: Point, del: CustomLineTypes): Boolean {
        return creasePatternWorker?.insideToDeleteType(p0a, p0b, del) ?: false
    }
} 