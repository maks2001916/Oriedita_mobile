package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange

/**
 * Менеджер обработчиков мыши
 * Управляет всеми обработчиками мыши в приложении
 */
class MouseHandlerManager {
    
    private var currentHandler: BaseMouseHandler? = null
    private val handlers = mutableMapOf<String, BaseMouseHandler>()
    
    init {
        // Регистрируем все обработчики
        registerHandlers()
    }
    
    private fun registerHandlers() {
        handlers["select"] = MouseHandlerSelect()
        handlers["draw_crease_free"] = MouseHandlerDrawCreaseFree()
        handlers["move_crease_pattern"] = MouseHandlerMoveCreasePattern()
        handlers["draw_crease_restricted"] = MouseHandlerDrawCreaseRestricted()
        handlers["draw_crease_symmetric"] = MouseHandlerDrawCreaseSymmetric()
        handlers["draw_crease_angle_restricted"] = MouseHandlerDrawCreaseAngleRestricted()
        handlers["unselect"] = MouseHandlerUnselect()
        handlers["select_all"] = MouseHandlerSelectAll()
        handlers["unselect_all"] = MouseHandlerUnselectAll()
        handlers["copy"] = MouseHandlerCopy()
        handlers["move"] = MouseHandlerMove()
        handlers["circle_draw"] = MouseHandlerCircleDraw()
        handlers["circle_draw_free"] = MouseHandlerCircleDrawFree()
        handlers["circle_draw_three_point"] = MouseHandlerCircleDrawThreePoint()
        handlers["draw_point"] = MouseHandlerDrawPoint()
        handlers["delete_point"] = MouseHandlerDeletePoint()
        handlers["change_crease_type"] = MouseHandlerChangeCreaseType()
        handlers["crease_make_mountain"] = MouseHandlerCreaseMakeMountain()
        handlers["crease_make_valley"] = MouseHandlerCreaseMakeValley()
        handlers["symmetric_line"] = MouseHandlerSymmetricLine()
        handlers["symmetric_point"] = MouseHandlerSymmetricPoint()
        handlers["angle_bisector"] = MouseHandlerAngleBisector()
        handlers["perpendicular"] = MouseHandlerPerpendicular()
        handlers["parallel"] = MouseHandlerParallel()
        handlers["tangent"] = MouseHandlerTangent()
        handlers["circle_inscribed"] = MouseHandlerCircleInscribed()
        handlers["text"] = MouseHandlerText()
        handlers["comment"] = MouseHandlerComment()
        handlers["grid_snap"] = MouseHandlerGridSnap()
        handlers["angle_snap"] = MouseHandlerAngleSnap()
        handlers["fold"] = MouseHandlerFold()
        handlers["unfold"] = MouseHandlerUnfold()
        handlers["measure"] = MouseHandlerMeasure()
        handlers["angle_measure"] = MouseHandlerAngleMeasure()
        handlers["zoom"] = MouseHandlerZoom()
        handlers["pan"] = MouseHandlerPan()
        handlers["rotate"] = MouseHandlerRotate()
        handlers["select_rectangle"] = MouseHandlerSelectRectangle()
        handlers["group"] = MouseHandlerGroup()
        handlers["layer"] = MouseHandlerLayer()
        handlers["visibility"] = MouseHandlerVisibility()
        handlers["undo"] = MouseHandlerUndo()
        handlers["redo"] = MouseHandlerRedo()
        handlers["search"] = MouseHandlerSearch()
        handlers["filter"] = MouseHandlerFilter()
        handlers["crease_select"] = MouseHandlerCreaseSelect()
        handlers["crease_unselect"] = MouseHandlerCreaseUnselect()
        handlers["crease_move"] = MouseHandlerCreaseMove()
        handlers["crease_copy"] = MouseHandlerCreaseCopy()
        handlers["crease_toggle_mv"] = MouseHandlerCreaseToggleMV()
        handlers["crease_move_4p"] = MouseHandlerCreaseMove4p()
        handlers["crease_copy_4p"] = MouseHandlerCreaseCopy4p()
        handlers["crease_make_mv"] = MouseHandlerCreaseMakeMV()
        handlers["crease_advance_type"] = MouseHandlerCreaseAdvanceType()
        handlers["crease_delete_intersecting"] = MouseHandlerCreaseDeleteIntersecting()
        handlers["crease_delete_overlapping"] = MouseHandlerCreaseDeleteOverlapping()
        handlers["lengthen_crease"] = MouseHandlerLengthenCrease()
        handlers["line_segment_delete"] = MouseHandlerLineSegmentDelete()
        handlers["line_segment_division"] = MouseHandlerLineSegmentDivision()
        handlers["line_segment_ratio_set"] = MouseHandlerLineSegmentRatioSet()
        handlers["crease_make_aux"] = MouseHandlerCreaseMakeAux()
        handlers["crease_make_edge"] = MouseHandlerCreaseMakeEdge()
        handlers["crease_make_mountain"] = MouseHandlerCreaseMakeMountain()
        handlers["crease_make_valley"] = MouseHandlerCreaseMakeValley()
        handlers["crease_make_aux_live"] = MouseHandlerCreaseMakeAuxLive()
        // Добавить остальные обработчики здесь
    }
    
    /**
     * Установить текущий обработчик
     */
    fun setCurrentHandler(handlerName: String) {
        currentHandler = handlers[handlerName]
    }
    
    /**
     * Получить текущий обработчик
     */
    fun getCurrentHandler(): BaseMouseHandler? = currentHandler
    
    /**
     * Получить список всех обработчиков
     */
    fun getAllHandlers(): Map<String, BaseMouseHandler> = handlers.toMap()
    
    /**
     * Обработка нажатия мыши
     */
    fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        return currentHandler?.onPress(offset, change) ?: false
    }
    
    /**
     * Обработка перетаскивания мыши
     */
    fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        return currentHandler?.onDrag(offset, change) ?: false
    }
    
    /**
     * Обработка отпускания мыши
     */
    fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        return currentHandler?.onRelease(offset, change) ?: false
    }
    
    /**
     * Обработка двойного клика
     */
    fun onDoubleClick(offset: Offset): Boolean {
        return currentHandler?.onDoubleClick(offset) ?: false
    }
} 