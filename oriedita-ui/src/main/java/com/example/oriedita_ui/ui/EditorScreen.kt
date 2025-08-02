package com.example.oriedita_ui.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.oriedita_ui.R
import com.example.oriedita_ui.viewmodel.CanvasViewModel
import com.example.oriedita_ui.viewmodel.DockBarViewModel
import com.example.oriedita_ui.viewmodel.CanvasObject
import com.example.oriedita_ui.viewmodel.LineType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    onNavigateBack: () -> Unit,
    viewModel: CanvasViewModel = viewModel()
) {
    var showToolMenu by remember { mutableStateOf(false) }
    var showLineTypeMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { showToolMenu = !showToolMenu }) {
                        Icon(Icons.Default.Build, stringResource(R.string.menu_tools))
                    }
                    IconButton(onClick = { showLineTypeMenu = !showLineTypeMenu }) {
                        Icon(Icons.Default.Style, stringResource(R.string.menu_line_type))
                    }
                    IconButton(onClick = { viewModel.undo() }) {
                        Icon(Icons.Default.Undo, stringResource(R.string.menu_undo))
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
            DrawingCanvas(viewModel)
            
            if (showToolMenu) {
                ToolMenu(
                    currentTool = viewModel.getCurrentTool(),
                    onToolSelected = { tool ->
                        viewModel.setTool(tool)
                        showToolMenu = false
                    },
                    onDismiss = { showToolMenu = false }
                )
            }
            
            if (showLineTypeMenu) {
                LineTypeMenu(
                    currentType = viewModel.currentLineType.collectAsState().value,
                    onTypeSelected = { type ->
                        viewModel.setLineType(type)
                        showLineTypeMenu = false
                    },
                    onDismiss = { showLineTypeMenu = false }
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
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.menu_tools)) },
        text = {
            Column {
                ToolButton(
                    tool = CanvasTool.DrawCreaseFree,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.DrawCreaseFree) }
                )
                ToolButton(
                    tool = CanvasTool.MoveCreasePattern,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.MoveCreasePattern) }
                )
                ToolButton(
                    tool = CanvasTool.LineSegmentDelete,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.LineSegmentDelete) }
                )
                ToolButton(
                    tool = CanvasTool.ChangeCreaseType,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.ChangeCreaseType) }
                )
                ToolButton(
                    tool = CanvasTool.LengthenCrease,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.LengthenCrease) }
                )
                ToolButton(
                    tool = CanvasTool.DrawPoint,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.DrawPoint) }
                )
                ToolButton(
                    tool = CanvasTool.DeletePoint,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.DeletePoint) }
                )
                ToolButton(
                    tool = CanvasTool.CircleDraw,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.CircleDraw) }
                )
                ToolButton(
                    tool = CanvasTool.CircleDrawThreePoint,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.CircleDrawThreePoint) }
                )
                ToolButton(
                    tool = CanvasTool.CircleDrawFree,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.CircleDrawFree) }
                )
                ToolButton(
                    tool = CanvasTool.ParallelDraw,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.ParallelDraw) }
                )
                ToolButton(
                    tool = CanvasTool.PerpendicularDraw,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.PerpendicularDraw) }
                )
                ToolButton(
                    tool = CanvasTool.CreaseSelect,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.CreaseSelect) }
                )
                ToolButton(
                    tool = CanvasTool.CreaseUnselect,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.CreaseUnselect) }
                )
                ToolButton(
                    tool = CanvasTool.SelectLasso,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.SelectLasso) }
                )
                ToolButton(
                    tool = CanvasTool.UnselectLasso,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.UnselectLasso) }
                )
                ToolButton(
                    tool = CanvasTool.SelectPolygon,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.SelectPolygon) }
                )
                ToolButton(
                    tool = CanvasTool.UnselectPolygon,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.UnselectPolygon) }
                )
                ToolButton(
                    tool = CanvasTool.Text,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.Text) }
                )
                ToolButton(
                    tool = CanvasTool.AngleSystem,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.AngleSystem) }
                )
                ToolButton(
                    tool = CanvasTool.Axiom5,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.Axiom5) }
                )
                ToolButton(
                    tool = CanvasTool.Axiom7,
                    currentTool = currentTool,
                    onClick = { onToolSelected(CanvasTool.Axiom7) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
private fun ToolButton(
    tool: CanvasTool,
    currentTool: CanvasTool,
    onClick: () -> Unit
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
                is CanvasTool.DrawCreaseFree -> stringResource(R.string.tool_draw_crease_free)
                is CanvasTool.MoveCreasePattern -> stringResource(R.string.tool_move_crease_pattern)
                is CanvasTool.LineSegmentDelete -> stringResource(R.string.tool_line_segment_delete)
                is CanvasTool.ChangeCreaseType -> stringResource(R.string.tool_change_crease_type)
                is CanvasTool.LengthenCrease -> stringResource(R.string.tool_lengthen_crease)
                is CanvasTool.DrawPoint -> stringResource(R.string.tool_draw_point)
                is CanvasTool.DeletePoint -> stringResource(R.string.tool_delete_point)
                is CanvasTool.CircleDraw -> stringResource(R.string.tool_circle_draw)
                is CanvasTool.CircleDrawThreePoint -> stringResource(R.string.tool_circle_draw_three_point)
                is CanvasTool.CircleDrawFree -> stringResource(R.string.tool_circle_draw_free)
                is CanvasTool.ParallelDraw -> stringResource(R.string.tool_parallel_draw)
                is CanvasTool.PerpendicularDraw -> stringResource(R.string.tool_perpendicular_draw)
                is CanvasTool.SymmetricDraw -> stringResource(R.string.tool_symmetric_draw)
                is CanvasTool.DrawCreaseRestricted -> stringResource(R.string.tool_draw_crease_restricted)
                is CanvasTool.DrawCreaseSymmetric -> stringResource(R.string.tool_draw_crease_symmetric)
                is CanvasTool.DrawCreaseAngleRestricted -> stringResource(R.string.tool_draw_crease_angle_restricted)
                is CanvasTool.AngleSystem -> stringResource(R.string.tool_angle_system)
                is CanvasTool.CreaseSelect -> stringResource(R.string.tool_crease_select)
                is CanvasTool.CreaseUnselect -> stringResource(R.string.tool_crease_unselect)
                is CanvasTool.CreaseMove -> stringResource(R.string.tool_crease_move)
                is CanvasTool.CreaseCopy -> stringResource(R.string.tool_crease_copy)
                is CanvasTool.CreaseMakeMountain -> stringResource(R.string.tool_crease_make_mountain)
                is CanvasTool.CreaseMakeValley -> stringResource(R.string.tool_crease_make_valley)
                is CanvasTool.CreaseMakeEdge -> stringResource(R.string.tool_crease_make_edge)
                is CanvasTool.CreaseMakeAux -> stringResource(R.string.tool_crease_make_aux)
                is CanvasTool.CreaseToggleMV -> stringResource(R.string.tool_crease_toggle_mv)
                is CanvasTool.CreaseDeleteOverlapping -> stringResource(R.string.tool_crease_delete_overlapping)
                is CanvasTool.CreaseDeleteIntersecting -> stringResource(R.string.tool_crease_delete_intersecting)
                is CanvasTool.SelectPolygon -> stringResource(R.string.tool_select_polygon)
                is CanvasTool.UnselectPolygon -> stringResource(R.string.tool_unselect_polygon)
                is CanvasTool.SelectLasso -> stringResource(R.string.tool_select_lasso)
                is CanvasTool.UnselectLasso -> stringResource(R.string.tool_unselect_lasso)
                is CanvasTool.Text -> stringResource(R.string.tool_text)
                is CanvasTool.AddFoldingConstraint -> stringResource(R.string.tool_add_folding_constraint)
                is CanvasTool.Axiom5 -> stringResource(R.string.tool_axiom5)
                is CanvasTool.Axiom7 -> stringResource(R.string.tool_axiom7)
            }
        )
    }
}

@Composable
private fun LineTypeMenu(
    currentType: LineType,
    onTypeSelected: (LineType) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.menu_line_type)) },
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
                                LineType.EDGE -> stringResource(R.string.line_type_edge)
                                LineType.MOUNTAIN -> stringResource(R.string.line_type_mountain)
                                LineType.VALLEY -> stringResource(R.string.line_type_valley)
                                LineType.AUX -> stringResource(R.string.line_type_aux)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

// Инструменты для холста
sealed class CanvasTool {
    object DrawCreaseFree : CanvasTool() // Свободное рисование линий
    object MoveCreasePattern : CanvasTool() // Перемещение паттерна
    object LineSegmentDelete : CanvasTool() // Удаление линий
    object ChangeCreaseType : CanvasTool() // Изменение типа линии
    object LengthenCrease : CanvasTool() // Удлинение линий
    object DrawPoint : CanvasTool() // Рисование точек
    object CircleDraw : CanvasTool() // Рисование окружностей
    object CircleDrawThreePoint : CanvasTool() // Рисование окружности по трем точкам
    object CircleDrawFree : CanvasTool() // Свободное рисование окружностей
    object ParallelDraw : CanvasTool() // Рисование параллельных линий
    object PerpendicularDraw : CanvasTool() // Рисование перпендикулярных линий
    object SymmetricDraw : CanvasTool() // Симметричное рисование
    object DrawCreaseRestricted : CanvasTool() // Рисование линий с ограничениями
    object DrawCreaseSymmetric : CanvasTool() // Симметричное рисование линий
    object DrawCreaseAngleRestricted : CanvasTool() // Рисование линий с угловыми ограничениями
    object DeletePoint : CanvasTool() // Удаление точек
    object AngleSystem : CanvasTool() // Система углов
    object CreaseSelect : CanvasTool() // Выбор линий
    object CreaseUnselect : CanvasTool() // Отмена выбора линий
    object CreaseMove : CanvasTool() // Перемещение линий
    object CreaseCopy : CanvasTool() // Копирование линий
    object CreaseMakeMountain : CanvasTool() // Создание горной линии
    object CreaseMakeValley : CanvasTool() // Создание долинной линии
    object CreaseMakeEdge : CanvasTool() // Создание краевой линии
    object CreaseMakeAux : CanvasTool() // Создание вспомогательной линии
    object CreaseToggleMV : CanvasTool() // Переключение между горной и долинной линиями
    object CreaseDeleteOverlapping : CanvasTool() // Удаление перекрывающихся линий
    object CreaseDeleteIntersecting : CanvasTool() // Удаление пересекающихся линий
    object SelectPolygon : CanvasTool() // Выбор многоугольника
    object UnselectPolygon : CanvasTool() // Отмена выбора многоугольника
    object SelectLasso : CanvasTool() // Выбор лассо
    object UnselectLasso : CanvasTool() // Отмена выбора лассо
    object Text : CanvasTool() // Текст
    object AddFoldingConstraint : CanvasTool() // Добавление ограничений складывания
    object Axiom5 : CanvasTool() // Аксиома 5
    object Axiom7 : CanvasTool() // Аксиома 7
} 