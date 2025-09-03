package com.example.oriedita_ui.ui

import com.example.oriedita_ui.ui.CanvasTool

// Группы инструментов для отображения в меню
// Можно расширять и локализовать

data class ToolGroup(val name: String, val tools: List<CanvasTool>)

// Пример группировки (названия и состав можно скорректировать)
val toolGroups = listOf(
    ToolGroup(
        "Линии",
        listOf(
            CanvasTool.DrawCreaseFree,
            CanvasTool.LengthenCrease,
            CanvasTool.SquareBisector,
            CanvasTool.PerpendicularDraw,
            CanvasTool.SymmetricDraw,
            CanvasTool.DrawCreaseRestricted,
            CanvasTool.DrawCreaseSymmetric,
            CanvasTool.DrawCreaseAngleRestricted,
            CanvasTool.DrawCreaseAngleRestricted3,
            CanvasTool.DrawCreaseAngleRestricted5,
            CanvasTool.ParallelDraw,
            CanvasTool.ParallelDrawWidth,
            CanvasTool.ContinuousSymmetricDraw,
            CanvasTool.FishBoneDraw,
            CanvasTool.DoubleSymmetricDraw,
            CanvasTool.FoldableLineDraw,
            CanvasTool.FoldableLineInput
        )
    ),
    ToolGroup(
        "Окружности",
        listOf(
            CanvasTool.CircleDraw,
            CanvasTool.CircleDrawThreePoint,
            CanvasTool.CircleDrawSeparate,
            CanvasTool.CircleDrawTangentLine,
            CanvasTool.CircleDrawInverted,
            CanvasTool.CircleDrawFree,
            CanvasTool.CircleDrawConcentric,
            CanvasTool.CircleDrawConcentricSelect,
            CanvasTool.CircleDrawTwoConcentricSelect,
            CanvasTool.CircleChangeColor
        )
    ),
    ToolGroup(
        "Выделение и удаление",
        listOf(
            CanvasTool.CreaseSelect,
            CanvasTool.CreaseUnselect,
            CanvasTool.LineSegmentDelete,
            CanvasTool.DeletePoint,
            CanvasTool.VertexDeleteOnCrease,
            CanvasTool.CreaseDeleteOverlapping,
            CanvasTool.CreaseDeleteIntersecting,
            CanvasTool.SelectPolygon,
            CanvasTool.UnselectPolygon,
            CanvasTool.SelectLineIntersecting,
            CanvasTool.UnselectLineIntersecting,
            CanvasTool.SelectLasso,
            CanvasTool.UnselectLasso,
            CanvasTool.DeleteLineTypeSelect
        )
    ),
    ToolGroup(
        "Измерения и вспомогательные",
        listOf(
            CanvasTool.DisplayLengthBetweenPoints1,
            CanvasTool.DisplayLengthBetweenPoints2,
            CanvasTool.DisplayAngleBetweenThreePoints1,
            CanvasTool.DisplayAngleBetweenThreePoints2,
            CanvasTool.DisplayAngleBetweenThreePoints3,
            CanvasTool.Text,
            CanvasTool.AddFoldingConstraint,
            CanvasTool.FlatFoldableCheck
        )
    ),
    ToolGroup(
        "Прочее",
        listOf(
            CanvasTool.MoveCreasePattern,
            CanvasTool.ChangeCreaseType,
            CanvasTool.Inward,
            CanvasTool.DrawPoint,
            CanvasTool.AngleSystem,
            CanvasTool.CreaseMove,
            CanvasTool.CreaseCopy,
            CanvasTool.CreaseMakeMountain,
            CanvasTool.CreaseMakeValley,
            CanvasTool.CreaseMakeEdge,
            CanvasTool.BackgroundChangePosition,
            CanvasTool.LineSegmentDivision,
            CanvasTool.LineSegmentRatioSet,
            CanvasTool.PolygonSetNoCorners,
            CanvasTool.CreaseAdvanceType,
            CanvasTool.CreaseMove4P,
            CanvasTool.CreaseCopy4P,
            CanvasTool.CreaseMakeMV,
            CanvasTool.CreasesAlternateMV,
            CanvasTool.VertexMakeAngularlyFlatFoldable,
            CanvasTool.ReplaceLineTypeSelect,
            CanvasTool.OperationFrameCreate,
            CanvasTool.VoronoiCreate,
            CanvasTool.LengthenCreaseSameColor,
            CanvasTool.Axiom5,
            CanvasTool.Axiom7,
            CanvasTool.ModifyCalculatedShape,
            CanvasTool.MoveCalculatedShape,
            CanvasTool.ChangeStandardFace
        )
    )
) 