package com.example.oriedita.editor.factory

import com.example.oriedita_ui.action.ComposeActionType
import com.example.oriedita_ui.handler.*

/**
 * Фабрика действий для Android
 * Адаптированная версия ActionFactory для Android
 */
class ActionFactory {
    
    /**
     * Создать обработчик мыши по типу действия
     */
    fun createMouseHandler(actionType: ComposeActionType): BaseMouseHandler? {
        return when (actionType) {
            ComposeActionType.SELECT -> MouseHandlerSelect()
            ComposeActionType.DRAW_CREASE_FREE -> MouseHandlerDrawCreaseFree()
            ComposeActionType.MOVE_CREASE_PATTERN -> MouseHandlerMoveCreasePattern()
            ComposeActionType.DRAW_CREASE_RESTRICTED -> MouseHandlerDrawCreaseRestricted()
            ComposeActionType.SYMMETRIC_DRAW -> MouseHandlerDrawCreaseSymmetric()
            ComposeActionType.UNSELECT -> MouseHandlerUnselect()
            ComposeActionType.SELECT_ALL -> MouseHandlerSelectAll()
            ComposeActionType.UNSELECT_ALL -> MouseHandlerUnselectAll()
            ComposeActionType.COPY -> MouseHandlerCopy()
            ComposeActionType.MOVE -> MouseHandlerMove()
            ComposeActionType.DRAW_CIRCLE_FREE -> MouseHandlerCircleDrawFree()
            ComposeActionType.DRAW_CIRCLE_THREE_POINT -> MouseHandlerCircleDrawThreePoint()
            ComposeActionType.VERTEX_ADD -> MouseHandlerDrawPoint()
            ComposeActionType.VERTEX_DELETE -> MouseHandlerDeletePoint()
            ComposeActionType.TO_MOUNTAIN -> MouseHandlerCreaseMakeMountain()
            ComposeActionType.TO_VALLEY -> MouseHandlerCreaseMakeValley()
            ComposeActionType.SYMMETRIC_LINE -> MouseHandlerSymmetricLine()
            ComposeActionType.SYMMETRIC_POINT -> MouseHandlerSymmetricPoint()
            ComposeActionType.ANGLE_BISECTOR -> MouseHandlerAngleBisector()
            ComposeActionType.PERPENDICULAR -> MouseHandlerPerpendicular()
            ComposeActionType.PARALLEL -> MouseHandlerParallel()
            ComposeActionType.TANGENT -> MouseHandlerTangent()
            ComposeActionType.CIRCLE_INSCRIBED -> MouseHandlerCircleInscribed()
            ComposeActionType.TEXT -> MouseHandlerText()
            ComposeActionType.COMMENT -> MouseHandlerComment()
            ComposeActionType.GRID_SNAP -> MouseHandlerGridSnap()
            ComposeActionType.ANGLE_SNAP -> MouseHandlerAngleSnap()
            ComposeActionType.FOLD -> MouseHandlerFold()
            ComposeActionType.UNFOLD -> MouseHandlerUnfold()
            ComposeActionType.MEASURE -> MouseHandlerMeasure()
            ComposeActionType.ANGLE_MEASURE -> MouseHandlerAngleMeasure()
            ComposeActionType.ZOOM -> MouseHandlerZoom()
            ComposeActionType.PAN -> MouseHandlerPan()
            ComposeActionType.ROTATE -> MouseHandlerRotate()
            ComposeActionType.SELECT_RECTANGLE -> MouseHandlerSelectRectangle()
            ComposeActionType.GROUP -> MouseHandlerGroup()
            ComposeActionType.LAYER -> MouseHandlerLayer()
            ComposeActionType.VISIBILITY -> MouseHandlerVisibility()
            ComposeActionType.UNDO -> MouseHandlerUndo()
            ComposeActionType.REDO -> MouseHandlerRedo()
            // Добавить остальные обработчики здесь
            else -> null
        }
    }
    
    /**
     * Создать обработчик мыши по имени
     */
    fun createMouseHandlerByName(handlerName: String): BaseMouseHandler? {
        return when (handlerName.lowercase()) {
            "select" -> MouseHandlerSelect()
            "draw_crease_free" -> MouseHandlerDrawCreaseFree()
            "move_crease_pattern" -> MouseHandlerMoveCreasePattern()
            "draw_crease_restricted" -> MouseHandlerDrawCreaseRestricted()
            "draw_crease_symmetric" -> MouseHandlerDrawCreaseSymmetric()
            "draw_crease_angle_restricted" -> MouseHandlerDrawCreaseAngleRestricted()
            "unselect" -> MouseHandlerUnselect()
            "select_all" -> MouseHandlerSelectAll()
            "unselect_all" -> MouseHandlerUnselectAll()
            "copy" -> MouseHandlerCopy()
            "move" -> MouseHandlerMove()
            "circle_draw" -> MouseHandlerCircleDraw()
            "circle_draw_free" -> MouseHandlerCircleDrawFree()
            "circle_draw_three_point" -> MouseHandlerCircleDrawThreePoint()
            "draw_point" -> MouseHandlerDrawPoint()
            "delete_point" -> MouseHandlerDeletePoint()
            "change_crease_type" -> MouseHandlerChangeCreaseType()
            "crease_make_mountain" -> MouseHandlerCreaseMakeMountain()
            "crease_make_valley" -> MouseHandlerCreaseMakeValley()
            "symmetric_line" -> MouseHandlerSymmetricLine()
            "symmetric_point" -> MouseHandlerSymmetricPoint()
            "angle_bisector" -> MouseHandlerAngleBisector()
            "perpendicular" -> MouseHandlerPerpendicular()
            "parallel" -> MouseHandlerParallel()
            "tangent" -> MouseHandlerTangent()
            "circle_inscribed" -> MouseHandlerCircleInscribed()
            "text" -> MouseHandlerText()
            "comment" -> MouseHandlerComment()
            "grid_snap" -> MouseHandlerGridSnap()
            "angle_snap" -> MouseHandlerAngleSnap()
            "fold" -> MouseHandlerFold()
            "unfold" -> MouseHandlerUnfold()
            "measure" -> MouseHandlerMeasure()
            "angle_measure" -> MouseHandlerAngleMeasure()
            "zoom" -> MouseHandlerZoom()
            "pan" -> MouseHandlerPan()
            "rotate" -> MouseHandlerRotate()
            "select_rectangle" -> MouseHandlerSelectRectangle()
            "group" -> MouseHandlerGroup()
            "layer" -> MouseHandlerLayer()
            "visibility" -> MouseHandlerVisibility()
            "undo" -> MouseHandlerUndo()
            "redo" -> MouseHandlerRedo()
            // Добавить остальные обработчики здесь
            else -> null
        }
    }
    
    /**
     * Получить список всех доступных обработчиков
     */
    fun getAvailableHandlers(): List<String> {
        return listOf(
            "select",
            "draw_crease_free", 
            "move_crease_pattern",
            "draw_crease_restricted",
            "draw_crease_symmetric",
            "draw_crease_angle_restricted",
            "unselect",
            "select_all",
            "unselect_all",
            "copy",
            "move",
            "circle_draw",
            "circle_draw_free",
            "circle_draw_three_point",
            "draw_point",
            "delete_point",
            "change_crease_type",
            "crease_make_mountain",
            "crease_make_valley",
            "symmetric_line",
            "symmetric_point",
            "angle_bisector",
            "perpendicular",
            "parallel",
            "tangent",
            "circle_inscribed",
            "text",
            "comment",
            "grid_snap",
            "angle_snap",
            "fold",
            "unfold",
            "measure",
            "angle_measure",
            "zoom",
            "pan",
            "rotate",
            "select_rectangle",
            "group",
            "layer",
            "visibility",
            "undo",
            "redo"
            // Добавить остальные обработчики здесь
        )
    }
    
    /**
     * Получить описание обработчика
     */
    fun getHandlerDescription(handlerName: String): String {
        val handler = createMouseHandlerByName(handlerName)
        return handler?.getDescription() ?: "Неизвестный обработчик"
    }
} 