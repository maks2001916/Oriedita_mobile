package com.example.oriedita_ui.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.example.oriedita_common.resources.AndroidResourceManager
import com.example.oriedita_common.resources.ResourceConstants
import com.example.oriedita_ui.viewmodel.CanvasViewModel
import com.example.oriedita_ui.viewmodel.CanvasObject
import com.example.oriedita_ui.viewmodel.LineType
import com.example.oriedita_ui.ui.components.DynamicDockBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    onNavigateBack: () -> Unit,
    onSettingsClick: () -> Unit,
    canvasViewModel: CanvasViewModel
) {
    val context = LocalContext.current
    val resourceManager = remember { AndroidResourceManager(context) }
    var showToolMenu by remember { mutableStateOf(false) }
    var showLineTypeMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(resourceManager.getString(ResourceConstants.APP_NAME)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, resourceManager.getString(ResourceConstants.BACK))
                    }
                },
                actions = {
                    IconButton(onClick = { showToolMenu = !showToolMenu }) {
                        Icon(Icons.Default.Build, resourceManager.getString(ResourceConstants.MENU_TOOLS))
                    }
                    IconButton(onClick = { showLineTypeMenu = !showLineTypeMenu }) {
                        Icon(Icons.Default.Add, resourceManager.getString(ResourceConstants.MENU_LINE_TYPE))
                    }
                    IconButton(onClick = { canvasViewModel.undo() }) {
                        Icon(Icons.Default.Refresh, resourceManager.getString(ResourceConstants.MENU_UNDO))
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, resourceManager.getString(ResourceConstants.MENU_SETTINGS))
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            DrawingCanvas(canvasViewModel)
            DynamicDockBar(canvasViewModel)
            if (showToolMenu) {
                ToolMenu(
                    currentTool = canvasViewModel.getCurrentTool(),
                    onToolSelected = { tool ->
                        canvasViewModel.setTool(tool)
                        showToolMenu = false
                    },
                    onDismiss = { showToolMenu = false },
                    resourceManager = resourceManager
                )
            }
            
            if (showLineTypeMenu) {
                LineTypeMenu(
                    currentType = canvasViewModel.currentLineType.collectAsState().value,
                    onTypeSelected = { type ->
                        canvasViewModel.setLineType(type)
                        showLineTypeMenu = false
                    },
                    onDismiss = { showLineTypeMenu = false },
                    resourceManager = resourceManager
                )
            }
        }
    }
}

@Composable
private fun DrawingCanvas(viewModel: CanvasViewModel) {
    val objects by viewModel.objects.collectAsState()
    val selectedObjects by viewModel.selectedObjects.collectAsState()
    
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset -> viewModel.onDown(offset) },
                    onDrag = { _, offset -> viewModel.onMove(offset) },
                    onDragEnd = { viewModel.onUp(Offset.Zero) }
                )
            }
    ) {
        // Рисуем все объекты
        objects.forEach { obj ->
            when (obj) {
                is CanvasObject.FreePath -> {
                    drawPath(
                        path = obj.path,
                        color = obj.color,
                        style = Stroke(
                            width = obj.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
                is CanvasObject.Line -> {
                    drawLine(
                        color = obj.color,
                        start = obj.start,
                        end = obj.end,
                        strokeWidth = obj.strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
                is CanvasObject.Point -> {
                    drawCircle(
                        color = obj.color,
                        radius = obj.radius,
                        center = obj.position
                    )
                }
                is CanvasObject.Circle -> {
                    drawCircle(
                        color = obj.color,
                        radius = obj.radius,
                        center = obj.center,
                        style = Stroke(width = obj.strokeWidth)
                    )
                }
                is CanvasObject.Text -> {
                    drawContext.canvas.nativeCanvas.drawText(
                        obj.text,
                        obj.position.x,
                        obj.position.y,
                        android.graphics.Paint().apply {
                            color = obj.color.toArgb()
                            textSize = obj.fontSize
                        }
                    )
                }
            }
        }
        
        // Подсвечиваем выбранные объекты
        selectedObjects.forEach { obj ->
            when (obj) {
                is CanvasObject.Line -> {
                    drawLine(
                        color = Color.Blue.copy(alpha = 0.3f),
                        start = obj.start,
                        end = obj.end,
                        strokeWidth = obj.strokeWidth + 4f,
                        cap = StrokeCap.Round
                    )
                }
                is CanvasObject.Point -> {
                    drawCircle(
                        color = Color.Blue.copy(alpha = 0.3f),
                        radius = obj.radius + 4f,
                        center = obj.position
                    )
                }
                is CanvasObject.Circle -> {
                    drawCircle(
                        color = Color.Blue.copy(alpha = 0.3f),
                        radius = obj.radius + 4f,
                        center = obj.center,
                        style = Stroke(width = obj.strokeWidth + 4f)
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun ToolMenu(
    currentTool: CanvasTool,
    onToolSelected: (CanvasTool) -> Unit,
    onDismiss: () -> Unit,
    resourceManager: AndroidResourceManager
) {
    AlertDialog(
        onDismissRequest = onDismiss,
                    title = { Text(resourceManager.getString(ResourceConstants.MENU_TOOLS)) },
        text = {
            Column {
                ToolButton(
                    tool = CanvasTool.DrawCreaseFree,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.DrawCreaseFree) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.MoveCreasePattern,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.MoveCreasePattern) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.LineSegmentDelete,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.LineSegmentDelete) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.ChangeCreaseType,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.ChangeCreaseType) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.LengthenCrease,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.LengthenCrease) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.DrawPoint,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.DrawPoint) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.DeletePoint,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.DeletePoint) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.CircleDraw,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.CircleDraw) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.CircleDrawThreePoint,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.CircleDrawThreePoint) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.CircleDrawFree,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.CircleDrawFree) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.ParallelDraw,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.ParallelDraw) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.PerpendicularDraw,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.PerpendicularDraw) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.CreaseSelect,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.CreaseSelect) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.CreaseUnselect,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.CreaseUnselect) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.SelectLasso,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.SelectLasso) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.UnselectLasso,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.UnselectLasso) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.SelectPolygon,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.SelectPolygon) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.UnselectPolygon,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.UnselectPolygon) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.Text,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.Text) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.AngleSystem,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.AngleSystem) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.Axiom5,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.Axiom5) },
                    resourceManager = resourceManager
                )
                ToolButton(
                    tool = CanvasTool.Axiom7,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.Axiom7) },
                    resourceManager = resourceManager
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(resourceManager.getString(ResourceConstants.CLOSE))
            }
        }
    )
}

@Composable
private fun ToolButton(
    tool: CanvasTool,
    currentTool: CanvasTool,
    onClick: () -> Unit,
    resourceManager: AndroidResourceManager
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (tool == currentTool) MaterialTheme.colorScheme.primaryContainer
                           else MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            when (tool) {
                is CanvasTool.DrawCreaseFree -> resourceManager.getString(ResourceConstants.TOOL_DRAW_CREASE_FREE)
                is CanvasTool.MoveCreasePattern -> resourceManager.getString(ResourceConstants.TOOL_MOVE_CREASE_PATTERN)
                is CanvasTool.LineSegmentDelete -> resourceManager.getString(ResourceConstants.TOOL_LINE_SEGMENT_DELETE)
                is CanvasTool.ChangeCreaseType -> resourceManager.getString(ResourceConstants.TOOL_CHANGE_CREASE_TYPE)
                is CanvasTool.LengthenCrease -> resourceManager.getString(ResourceConstants.TOOL_LENGTHEN_CREASE)
                is CanvasTool.DrawPoint -> resourceManager.getString(ResourceConstants.TOOL_DRAW_POINT)
                is CanvasTool.DeletePoint -> resourceManager.getString(ResourceConstants.TOOL_DELETE_POINT)
                is CanvasTool.CircleDraw -> resourceManager.getString(ResourceConstants.TOOL_CIRCLE_DRAW)
                is CanvasTool.CircleDrawThreePoint -> resourceManager.getString(ResourceConstants.TOOL_CIRCLE_DRAW_THREE_POINT)
                is CanvasTool.CircleDrawFree -> resourceManager.getString(ResourceConstants.TOOL_CIRCLE_DRAW_FREE)
                is CanvasTool.ParallelDraw -> resourceManager.getString(ResourceConstants.TOOL_PARALLEL_DRAW)
                is CanvasTool.PerpendicularDraw -> resourceManager.getString(ResourceConstants.TOOL_PERPENDICULAR_DRAW)
                is CanvasTool.SymmetricDraw -> resourceManager.getString(ResourceConstants.TOOL_SYMMETRIC_DRAW)
                is CanvasTool.DrawCreaseRestricted -> resourceManager.getString(ResourceConstants.TOOL_DRAW_CREASE_RESTRICTED)
                is CanvasTool.DrawCreaseSymmetric -> resourceManager.getString(ResourceConstants.TOOL_DRAW_CREASE_SYMMETRIC)
                is CanvasTool.DrawCreaseAngleRestricted -> resourceManager.getString(ResourceConstants.TOOL_DRAW_CREASE_ANGLE_RESTRICTED)
                is CanvasTool.AngleSystem -> resourceManager.getString(ResourceConstants.TOOL_ANGLE_SYSTEM)
                is CanvasTool.CreaseSelect -> resourceManager.getString(ResourceConstants.TOOL_CREASE_SELECT)
                is CanvasTool.CreaseUnselect -> resourceManager.getString(ResourceConstants.TOOL_CREASE_UNSELECT)
                is CanvasTool.CreaseMove -> resourceManager.getString(ResourceConstants.TOOL_CREASE_MOVE)
                is CanvasTool.CreaseCopy -> resourceManager.getString(ResourceConstants.TOOL_CREASE_COPY)
                is CanvasTool.CreaseMakeMountain -> resourceManager.getString(ResourceConstants.TOOL_CREASE_MAKE_MOUNTAIN)
                is CanvasTool.CreaseMakeValley -> resourceManager.getString(ResourceConstants.TOOL_CREASE_MAKE_VALLEY)
                is CanvasTool.CreaseMakeEdge -> resourceManager.getString(ResourceConstants.TOOL_CREASE_MAKE_EDGE)
                is CanvasTool.CreaseMakeAux -> resourceManager.getString(ResourceConstants.TOOL_CREASE_MAKE_AUX)
                is CanvasTool.CreaseToggleMV -> resourceManager.getString(ResourceConstants.TOOL_CREASE_TOGGLE_MV)
                is CanvasTool.CreaseDeleteOverlapping -> resourceManager.getString(ResourceConstants.TOOL_CREASE_DELETE_OVERLAPPING)
                is CanvasTool.CreaseDeleteIntersecting -> resourceManager.getString(ResourceConstants.TOOL_CREASE_DELETE_INTERSECTING)
                is CanvasTool.SelectPolygon -> resourceManager.getString(ResourceConstants.TOOL_SELECT_POLYGON)
                is CanvasTool.UnselectPolygon -> resourceManager.getString(ResourceConstants.TOOL_UNSELECT_POLYGON)
                is CanvasTool.SelectLasso -> resourceManager.getString(ResourceConstants.TOOL_SELECT_LASSO)
                is CanvasTool.UnselectLasso -> resourceManager.getString(ResourceConstants.TOOL_UNSELECT_LASSO)
                is CanvasTool.Text -> resourceManager.getString(ResourceConstants.TOOL_TEXT)
                is CanvasTool.AddFoldingConstraint -> resourceManager.getString(ResourceConstants.TOOL_ADD_FOLDING_CONSTRAINT)
                is CanvasTool.Axiom5 -> resourceManager.getString(ResourceConstants.TOOL_AXIOM5)
                is CanvasTool.Axiom7 -> resourceManager.getString(ResourceConstants.TOOL_AXIOM7)
                is CanvasTool.BackgroundChangePosition -> resourceManager.getString(ResourceConstants.TOOL_BACKGROUND_CHANGE_POSITION)
                is CanvasTool.ChangeStandardFace -> resourceManager.getString(ResourceConstants.TOOL_CHANGE_STANDARD_FACE)
                is CanvasTool.CircleChangeColor -> resourceManager.getString(ResourceConstants.TOOL_CIRCLE_CHANGE_COLOR)
                is CanvasTool.CircleDrawConcentric -> resourceManager.getString(ResourceConstants.TOOL_CIRCLE_DRAW_CONCENTRIC)
                is CanvasTool.CircleDrawConcentricSelect -> resourceManager.getString(ResourceConstants.TOOL_CIRCLE_DRAW_CONCENTRIC_SELECT)
                is CanvasTool.CircleDrawInverted -> resourceManager.getString(ResourceConstants.TOOL_CIRCLE_DRAW_INVERTED)
                is CanvasTool.CircleDrawSeparate -> resourceManager.getString(ResourceConstants.TOOL_CIRCLE_DRAW_SEPARATE)
                is CanvasTool.SquareBisector -> resourceManager.getString(ResourceConstants.TOOL_SQUARE_BISECTOR)
                is CanvasTool.Inward -> resourceManager.getString(ResourceConstants.TOOL_INWARD)
                is CanvasTool.DrawCreaseAngleRestricted3 -> resourceManager.getString(ResourceConstants.TOOL_DRAW_CREASE_ANGLE_RESTRICTED3)
                is CanvasTool.LineSegmentDivision -> resourceManager.getString(ResourceConstants.TOOL_LINE_SEGMENT_DIVISION)
                is CanvasTool.LineSegmentRatioSet -> resourceManager.getString(ResourceConstants.TOOL_LINE_SEGMENT_RATIO_SET)
                is CanvasTool.PolygonSetNoCorners -> resourceManager.getString(ResourceConstants.TOOL_POLYGON_SET_NO_CORNERS)
                is CanvasTool.FishBoneDraw -> resourceManager.getString(ResourceConstants.TOOL_FISH_BONE_DRAW)
                is CanvasTool.DoubleSymmetricDraw -> resourceManager.getString(ResourceConstants.TOOL_DOUBLE_SYMMETRIC_DRAW)
                is CanvasTool.CreasesAlternateMV -> resourceManager.getString(ResourceConstants.TOOL_CREASES_ALTERNATE_MV)
                is CanvasTool.DrawCreaseAngleRestricted5 -> resourceManager.getString(ResourceConstants.TOOL_DRAW_CREASE_ANGLE_RESTRICTED5)
                is CanvasTool.VertexMakeAngularlyFlatFoldable -> resourceManager.getString(ResourceConstants.TOOL_VERTEX_MAKE_ANGULARLY_FLAT_FOLDABLE)
                is CanvasTool.FoldableLineInput -> resourceManager.getString(ResourceConstants.TOOL_FOLDABLE_LINE_INPUT)
                is CanvasTool.VertexDeleteOnCrease -> resourceManager.getString(ResourceConstants.TOOL_VERTEX_DELETE_ON_CREASE)
                is CanvasTool.ParallelDrawWidth -> resourceManager.getString(ResourceConstants.TOOL_PARALLEL_DRAW_WIDTH)
                is CanvasTool.OperationFrameCreate -> resourceManager.getString(ResourceConstants.TOOL_OPERATION_FRAME_CREATE)
                is CanvasTool.VoronoiCreate -> resourceManager.getString(ResourceConstants.TOOL_VORONOI_CREATE)
                is CanvasTool.FlatFoldableCheck -> resourceManager.getString(ResourceConstants.TOOL_FLAT_FOLDABLE_CHECK)
                is CanvasTool.SelectLineIntersecting -> resourceManager.getString(ResourceConstants.TOOL_SELECT_LINE_INTERSECTING)
                is CanvasTool.UnselectLineIntersecting -> resourceManager.getString(ResourceConstants.TOOL_UNSELECT_LINE_INTERSECTING)
                is CanvasTool.LengthenCreaseSameColor -> resourceManager.getString(ResourceConstants.TOOL_LENGTHEN_CREASE_SAME_COLOR)
                is CanvasTool.FoldableLineDraw -> resourceManager.getString(ResourceConstants.TOOL_FOLDABLE_LINE_DRAW)
                is CanvasTool.ReplaceLineTypeSelect -> resourceManager.getString(ResourceConstants.TOOL_REPLACE_LINE_TYPE_SELECT)
                is CanvasTool.DeleteLineTypeSelect -> resourceManager.getString(ResourceConstants.TOOL_DELETE_LINE_TYPE_SELECT)
                is CanvasTool.ModifyCalculatedShape -> resourceManager.getString(ResourceConstants.TOOL_MODIFY_CALCULATED_SHAPE)
                is CanvasTool.MoveCalculatedShape -> resourceManager.getString(ResourceConstants.TOOL_MOVE_CALCULATED_SHAPE)
                is CanvasTool.CircleDrawTangentLine -> resourceManager.getString(ResourceConstants.TOOL_CIRCLE_DRAW_TANGENT_LINE)
                is CanvasTool.CircleDrawTwoConcentricSelect -> resourceManager.getString(ResourceConstants.TOOL_CIRCLE_DRAW_TWO_CONCENTRIC_SELECT)
                is CanvasTool.ContinuousSymmetricDraw -> resourceManager.getString(ResourceConstants.TOOL_CONTINUOUS_SYMMETRIC_DRAW)
                is CanvasTool.CreaseAdvanceType -> resourceManager.getString(ResourceConstants.TOOL_CREASE_ADVANCE_TYPE)
                is CanvasTool.CreaseMakeMV -> resourceManager.getString(ResourceConstants.TOOL_CREASE_MAKE_MV)
                else -> resourceManager.getString(ResourceConstants.TOOL_UNKNOWN)
            }
        )
    }
}

@Composable
private fun LineTypeMenu(
    currentType: LineType,
    onTypeSelected: (LineType) -> Unit,
    onDismiss: () -> Unit,
    resourceManager: AndroidResourceManager
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(resourceManager.getString(ResourceConstants.MENU_LINE_TYPE)) },
        text = {
            Column {
                LineType.values().forEach { type ->
                    Button(
                        onClick = { onTypeSelected(type) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == currentType) MaterialTheme.colorScheme.primaryContainer
                                           else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(
                            when (type) {
                                LineType.EDGE -> resourceManager.getString(ResourceConstants.LINE_TYPE_EDGE)
                                LineType.MOUNTAIN -> resourceManager.getString(ResourceConstants.LINE_TYPE_MOUNTAIN)
                                LineType.VALLEY -> resourceManager.getString(ResourceConstants.LINE_TYPE_VALLEY)
                                LineType.AUX -> resourceManager.getString(ResourceConstants.LINE_TYPE_AUX)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(resourceManager.getString(ResourceConstants.CLOSE))
            }
        }
    )
}

 