package com.example.oriedita_ui.action

/**
 * Типы действий для Compose UI
 * Адаптированная версия ActionType для Android
 */
enum class ComposeActionType(val actionName: String) {
    // Основные действия
    NEW("newAction"),
    OPEN("openAction"),
    SAVE("saveAction"),
    SAVE_AS("saveAsAction"),
    EXIT("exitAction"),
    
    // Действия рисования
    DRAW_CREASE_FREE("drawCreaseFreeAction"),
    DRAW_CREASE_RESTRICTED("drawCreaseRestrictedAction"),
    DRAW_LINE_SEGMENT("drawLineSegmentAction"),
    DRAW_CIRCLE_FREE("circleDrawFreeAction"),
    DRAW_CIRCLE_THREE_POINT("circleDrawThreePointAction"),
    
    // Действия с линиями
    TO_MOUNTAIN("toMountainAction"),
    TO_VALLEY("toValleyAction"),
    TO_EDGE("toEdgeAction"),
    TO_AUX("toAuxAction"),
    LENGTHEN_CREASE("lengthenCreaseAction"),
    DELETE_LINE_SEGMENT("deleteSelectedLineSegmentAction"),
    
    // Действия выбора
    SELECT("selectAction"),
    SELECT_ALL("selectAllAction"),
    UNSELECT("unselectAction"),
    UNSELECT_ALL("unselectAllAction"),
    SELECT_LASSO("selectLassoAction"),
    UNSELECT_LASSO("unselectLassoAction"),
    SELECT_RECTANGLE("selectRectangleAction"),
    
    // Действия перемещения
    MOVE("moveAction"),
    MOVE_CREASE_PATTERN("moveCreasePatternAction"),
    COPY("copyAction"),
    
    // Действия с цветом
    COLOR_RED("colRedAction"),
    COLOR_BLUE("colBlueAction"),
    COLOR_BLACK("colBlackAction"),
    COLOR_CYAN("colCyanAction"),
    COLOR_ORANGE("colOrangeAction"),
    COLOR_YELLOW("colYellowAction"),
    
    // Действия с размерами
    LINE_WIDTH_INCREASE("lineWidthIncreaseAction"),
    LINE_WIDTH_DECREASE("lineWidthDecreaseAction"),
    POINT_SIZE_INCREASE("pointSizeIncreaseAction"),
    POINT_SIZE_DECREASE("pointSizeDecreaseAction"),
    
    // Действия с сеткой
    GRID_SIZE_INCREASE("gridSizeIncreaseAction"),
    GRID_SIZE_DECREASE("gridSizeDecreaseAction"),
    GRID_SIZE_SET("gridSizeSetAction"),
    GRID_COLOR("gridColorAction"),
    GRID_LINE_WIDTH_INCREASE("gridLineWidthIncreaseAction"),
    GRID_LINE_WIDTH_DECREASE("gridLineWidthDecreaseAction"),
    CHANGE_GRID_STATE("changeGridStateAction"),
    GRID_SNAP("gridSnapAction"),
    
    // Действия с углами
    ANGLE_SNAP("angleSnapAction"),
    
    // Действия масштабирования
    ZOOM_IN("creasePatternZoomInAction"),
    ZOOM_OUT("creasePatternZoomOutAction"),
    ROTATE_CLOCKWISE("rotateClockwiseAction"),
    ROTATE_ANTICLOCKWISE("rotateAnticlockwiseAction"),
    ZOOM("zoomAction"),
    PAN("panAction"),
    ROTATE("rotateAction"),
    
    // Действия симметрии
    SYMMETRIC_DRAW("symmetricDrawAction"),
    DOUBLE_SYMMETRIC_DRAW("doubleSymmetricDrawAction"),
    CONTINUOUS_SYMMETRIC_DRAW("continuousSymmetricDrawAction"),
    SYMMETRIC_LINE("symmetricLineAction"),
    SYMMETRIC_POINT("symmetricPointAction"),
    
    // Действия с углами
    PERPENDICULAR_DRAW("perpendicularDrawAction"),
    PARALLEL_DRAW("parallelDrawAction"),
    ANGLE_BISECTOR("angleBisectorAction"),
    PERPENDICULAR("perpendicularAction"),
    PARALLEL("parallelAction"),
    TANGENT("tangentAction"),
    
    // Действия с окружностями
    CIRCLE_INSCRIBED("circleInscribedAction"),
    
    // Действия с вершинами
    VERTEX_ADD("vertexAddAction"),
    VERTEX_DELETE("vertexDeleteAction"),
    
    // Действия отмены/повтора
    UNDO("undoAction"),
    REDO("redoAction"),
    
    // Действия складывания
    FOLD("foldAction"),
    UNFOLD("unfoldAction"),
    RESET("resetAction"),
    HALT("haltAction"),
    
    // Действия с фоном
    READ_BACKGROUND("readBackgroundAction"),
    BACKGROUND_TRIM("backgroundTrimAction"),
    BACKGROUND_TOGGLE("backgroundToggleAction"),
    
    // Действия отображения
    TOGGLE_ANTIALIAS("antiAliasToggleAction"),
    TOGGLE_HELP("toggleHelpAction"),
    DISPLAY_COMMENTS("displayCommentsAction"),
    DISPLAY_CP_LINES("displayCpLinesAction"),
    DISPLAY_AUX_LINES("displayAuxLinesAction"),
    
    // Действия с текстом
    TEXT("textAction"),
    COMMENT("commentAction"),
    
    // Действия с многоугольниками
    REGULAR_POLYGON("regularPolygonAction"),
    SELECT_POLYGON("select_polygonAction"),
    UNSELECT_POLYGON("unselect_polygonAction"),
    
    // Действия с группами и слоями
    GROUP("groupAction"),
    LAYER("layerAction"),
    VISIBILITY("visibilityAction"),
    
    // Действия с аксиомами
    AXIOM_5("axiom5Action"),
    AXIOM_7("axiom7Action"),
    
    // Действия измерения
    MEASURE("measureAction"),
    ANGLE_MEASURE("angleMeasureAction"),
    
    // Действия с ограничениями
    ADD_COLOR_CONSTRAINT("addColorConstraintAction"),
    
    // Действия с угловыми системами
    ANGLE_SYSTEM_A("angleSystemAAction"),
    ANGLE_SYSTEM_A_INCREASE("angleSystemAIncreaseAction"),
    ANGLE_SYSTEM_A_DECREASE("angleSystemADecreaseAction"),
    ANGLE_SYSTEM_B("angleSystemBAction"),
    ANGLE_SYSTEM_B_INCREASE("angleSystemBIncreaseAction"),
    ANGLE_SYSTEM_B_DECREASE("angleSystemBDecreaseAction");
    
    companion object {
        private val actionMap = values().associateBy { it.actionName }
        
        fun fromAction(action: String): ComposeActionType? {
            return actionMap[action]
        }
    }
} 