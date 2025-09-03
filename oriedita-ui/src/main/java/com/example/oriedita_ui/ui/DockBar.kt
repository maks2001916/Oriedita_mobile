package com.example.oriedita_ui.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.oriedita_ui.viewmodel.DockBarViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.darkColorScheme
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import com.example.oriedita_data.resource.FontIconManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalInspectionMode

@Composable
fun DockBar(
    viewModel: DockBarViewModel,
    selectedTool: CanvasTool?,
    onToolSelected: (CanvasTool) -> Unit,
    onRequestPin: (Int) -> Unit
) {

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    var dragOffset by remember { mutableStateOf(viewModel.position) }

    val dockWidthHorizontal = 300.dp
    val dockHeightHorizontal = 60.dp
    val dockWidthVertical = 60.dp
    val dockHeightVertical = 300.dp

    val cornerRadius = 16.dp
    val magnetThreshold = 32.dp

    var offset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var orientation by remember { mutableStateOf(DockOrientation.HORIZONTAL) }

    // В Preview (Layout Editor) ресурсы могут быть недоступны — не создаём FontIconManager
    val isInPreview = LocalInspectionMode.current
    val fontIconManager: FontIconManager? = remember(isInPreview) {
        if (isInPreview) null else FontIconManager(context)
    }

    // Calculate orientation based on offset
    LaunchedEffect(offset) {
        val positionXDp = with(density) { offset.x.toDp() }
        val positionYDp = with(density) { offset.y.toDp() }

        val distanceToTop = positionYDp
        val distanceToBottom = screenHeight.dp - positionYDp - if (orientation == DockOrientation.HORIZONTAL) dockHeightHorizontal else dockHeightVertical
        val distanceToLeft = positionXDp
        val distanceToRight = screenWidth.dp - positionXDp - if (orientation == DockOrientation.HORIZONTAL) dockWidthHorizontal else dockWidthVertical

        val minDistance = minOf(distanceToTop, distanceToBottom, distanceToLeft, distanceToRight)

        if (minDistance <= magnetThreshold) {
            orientation = when (minDistance) {
                distanceToTop, distanceToBottom -> DockOrientation.HORIZONTAL
                distanceToLeft, distanceToRight -> DockOrientation.VERTICAL
                else -> orientation
            }
        }
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(dragOffset.x.toInt(), dragOffset.y.toInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    dragOffset += dragAmount
                    viewModel.moveTo(dragOffset, screenWidth, screenHeight)
                }
            }
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shape = if (viewModel.isVertical) RoundedCornerShape(24.dp) else RoundedCornerShape(50)
            )
            .size(if (orientation == DockOrientation.HORIZONTAL) dockWidthHorizontal else dockWidthVertical,
                if (orientation == DockOrientation.HORIZONTAL) dockHeightHorizontal else dockHeightVertical)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false }
                ) { change, dragAmount ->
                    // Ограничиваем перемещение в зависимости от ориентации
                    val newOffset = offset + dragAmount
                    val maxX = screenWidth - with(density) { (if (orientation == DockOrientation.HORIZONTAL) dockWidthHorizontal else dockWidthVertical).toPx() }
                    val maxY = screenHeight - with(density) { (if (orientation == DockOrientation.HORIZONTAL) dockHeightHorizontal else dockHeightVertical).toPx() }


                    // Добавляем отладочную информацию для перемещения
                    Log.d("DockBar", "Drag amount: $dragAmount")
                    Log.d("DockBar", "Current offset: $offset")
                    Log.d("DockBar", "New offset: $newOffset")
                    Log.d("DockBar", "Max X: $maxX, Max Y: $maxY")

                    offset = Offset(
                        x = newOffset.x.coerceIn(0f, maxX),
                        y = newOffset.y.coerceIn(0f, maxY)
                    )

                    Log.d("DockBar", "Final offset: $offset")
                    change.consume()
                }
            }
            .padding(8.dp)
    ) {
        // Кнопки адаптируются к ориентации
        if (orientation == DockOrientation.HORIZONTAL) {
            // Горизонтальное расположение кнопок
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(5) { index ->
                    IconButton(
                        onClick = { /* Обработка клика */ },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.LightGray, CircleShape)
                            .clip(CircleShape)
                    ) {
                        ToolIcon(
                            fontIconManager = fontIconManager,
                            tool = viewModel.pinnedTools.getOrNull(index),
                            size = 32,
                            contentDescription = "Инструмент ${index + 1}"
                        )
                    }
                }
            }
        } else {
            // Вертикальное расположение кнопок
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(5) { index ->
                    IconButton(
                        onClick = { /* Обработка клика */ },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.LightGray, CircleShape)
                            .clip(CircleShape)
                    ) {
                        ToolIcon(
                            fontIconManager = fontIconManager,
                            tool = viewModel.pinnedTools.getOrNull(index),
                            size = 32,
                            contentDescription = "Инструмент ${index + 1}"
                        )
                    }
                }
            }
        }
        //if (viewModel.isVertical) {
        //Column(horizontalAlignment = Alignment.CenterHorizontally) {
            //    DockBarButtons(viewModel, selectedTool, onToolSelected, onRequestPin) }
        //} else {
            //Row(verticalAlignment = Alignment.CenterVertically) {
            //    DockBarButtons(viewModel, selectedTool, onToolSelected, onRequestPin) }
        //}
    }
}

enum class DockOrientation {
    HORIZONTAL, VERTICAL
}

@Composable
private fun DockBarButtons(
    viewModel: DockBarViewModel,
    selectedTool: CanvasTool?,
    onToolSelected: (CanvasTool) -> Unit,
    onRequestPin: (Int) -> Unit
) {
    val context = LocalContext.current
    val fontIconManager = remember { FontIconManager(context) }
    
    viewModel.pinnedTools.forEachIndexed { index, tool ->
        var showPinMenu by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .padding(4.dp)
                .clip(CircleShape)
                .background(if (tool == selectedTool) MaterialTheme.colorScheme.primary else Color.LightGray)
                .pointerInput(tool) {
                    detectTapGestures(
                        onTap = { tool?.let { onToolSelected(it) } },
                        onLongPress = { showPinMenu = true }
                    )
                }
                .size(48.dp)
        ) {
            if (tool != null) {
                ToolIcon(
                    fontIconManager = fontIconManager,
                    tool = tool,
                    size = 32,
                    contentDescription = tool.label
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить инструмент",
                    modifier = Modifier.align(Alignment.Center),
                    tint = Color.Black
                )
            }
            DropdownMenu(
                expanded = showPinMenu,
                onDismissRequest = { showPinMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Изменить инструмент") },
                    onClick = {
                        showPinMenu = false
                        onRequestPin(index)
                    }
                )
            }
        }
    }
} 

@Composable
private fun FontIcon(
    fontIconManager: FontIconManager?,
    iconCode: String,
    size: Int,
    color: Color,
    contentDescription: String? = null
) {
    val iconBitmap = remember(iconCode, size, color) {
        fontIconManager?.createIcon(iconCode, size, color.toArgb())
    }

    if (iconBitmap != null) {
        androidx.compose.foundation.Image(
            bitmap = iconBitmap.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = Modifier.size(size.dp)
        )
    } else {
        // В режиме Preview используем стандартную иконку-заглушку
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(size.dp)
        )
    }
}

@Composable
private fun ToolIcon(
    fontIconManager: FontIconManager?,
    tool: CanvasTool?,
    size: Int = 48,
    contentDescription: String? = null
) {
    when (tool) {
        CanvasTool.DrawCreaseFree -> {
            FontIcon(
                fontIconManager = fontIconManager,
                iconCode = FontIconManager.Companion.IconCodes.DRAW_CREASE_FREE,
                size = size,
                color = Color.White,
                contentDescription = contentDescription ?: "Свободное рисование"
            )
        }
        CanvasTool.DrawCreaseRestricted -> {
            FontIcon(
                fontIconManager = fontIconManager,
                iconCode = FontIconManager.Companion.IconCodes.DRAW_CREASE_RESTRICTED,
                size = size,
                color = Color.White,
                contentDescription = contentDescription ?: "Ограниченное рисование"
            )
        }
        CanvasTool.AngleSystem -> {
            FontIcon(
                fontIconManager = fontIconManager,
                iconCode = FontIconManager.Companion.IconCodes.VORONOI,
                size = size,
                color = Color.White,
                contentDescription = contentDescription ?: "Диаграмма Вороного"
            )
        }
        CanvasTool.Axiom7 -> {
            FontIcon(
                fontIconManager = fontIconManager,
                iconCode = FontIconManager.Companion.IconCodes.MAKE_FLAT_FOLDABLE,
                size = size,
                color = Color.White,
                contentDescription = contentDescription ?: "Сделать плоскосгибаемым"
            )
        }
        CanvasTool.BackgroundChangePosition -> {
            FontIcon(
                fontIconManager = fontIconManager,
                iconCode = FontIconManager.Companion.IconCodes.FOLD,
                size = size,
                color = Color.White,
                contentDescription = contentDescription ?: "Сгибание"
            )
        }
        CanvasTool.ChangeCreaseType -> {
            FontIcon(
                fontIconManager = fontIconManager,
                iconCode = FontIconManager.Companion.IconCodes.UNDO,
                size = size,
                color = Color.White,
                contentDescription = contentDescription ?: "Отменить"
            )
        }
        CanvasTool.AddFoldingConstraint -> {
            FontIcon(
                fontIconManager = fontIconManager,
                iconCode = FontIconManager.Companion.IconCodes.REDO,
                size = size,
                color = Color.White,
                contentDescription = contentDescription ?: "Повторить"
            )
        }
        else -> {
            // Для инструментов без иконок в Icons2.ttf используем стандартную иконку
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = contentDescription ?: "Добавить инструмент",
                tint = Color.White
            )
        }
    }
}

// Preview методы для разработки и тестирования UI

@Preview(showBackground = true, name = "DockBar - Горизонтальная ориентация", apiLevel = 34)
@Composable
private fun DockBarHorizontalPreview() {
    MaterialTheme {
        val mockViewModel = object : DockBarViewModel() {
            override var position: Offset = Offset(100f, 100f)
            override var isVertical: Boolean = false
            override var pinnedTools: List<CanvasTool?> = listOf(
                CanvasTool.DrawCreaseFree,
                CanvasTool.AddFoldingConstraint,
                CanvasTool.AngleSystem,
                CanvasTool.Axiom7,
                null
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            DockBar(
                viewModel = mockViewModel,
                selectedTool = CanvasTool.DrawCreaseFree,
                onToolSelected = {},
                onRequestPin = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "DockBar - Вертикальная ориентация", apiLevel = 34)
@Composable
private fun DockBarVerticalPreview() {
    MaterialTheme {
        val mockViewModel = object : DockBarViewModel() {
            override var position: Offset = Offset(100f, 100f)
            override var isVertical: Boolean = true
            override var pinnedTools: List<CanvasTool?> = listOf(
                CanvasTool.DrawCreaseFree,
                CanvasTool.AddFoldingConstraint,
                CanvasTool.AngleSystem,
                CanvasTool.Axiom7,
                null
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            DockBar(
                viewModel = mockViewModel,
                selectedTool = CanvasTool.DrawCreaseFree,
                onToolSelected = {},
                onRequestPin = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "DockBar - Без выбранного инструмента", apiLevel = 34)
@Composable
private fun DockBarNoSelectionPreview() {
    MaterialTheme {
        val mockViewModel = object : DockBarViewModel() {
            override var position: Offset = Offset(100f, 100f)
            override var isVertical: Boolean = false
            override var pinnedTools: List<CanvasTool?> = listOf(
                CanvasTool.DrawCreaseFree,
                CanvasTool.AddFoldingConstraint,
                CanvasTool.AngleSystem,
                CanvasTool.Axiom7,
                null
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            DockBar(
                viewModel = mockViewModel,
                selectedTool = null,
                onToolSelected = {},
                onRequestPin = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "DockBar - Темная тема", apiLevel = 34)
@Composable
private fun DockBarDarkThemePreview() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        val mockViewModel = object : DockBarViewModel() {
            override var position: Offset = Offset(100f, 100f)
            override var isVertical: Boolean = false
            override var pinnedTools: List<CanvasTool?> = listOf(
                CanvasTool.DrawCreaseFree,
                CanvasTool.AddFoldingConstraint,
                CanvasTool.AngleSystem,
                CanvasTool.Axiom7,
                null
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray)
        ) {
            DockBar(
                viewModel = mockViewModel,
                selectedTool = CanvasTool.DrawCreaseFree,
                onToolSelected = {},
                onRequestPin = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "DockBar - Компактный вид", apiLevel = 34)
@Composable
private fun DockBarCompactPreview() {
    MaterialTheme {
        val mockViewModel = object : DockBarViewModel() {
            override var position: Offset = Offset(50f, 50f)
            override var isVertical: Boolean = false
            override var pinnedTools: List<CanvasTool?> = listOf(
                CanvasTool.DrawCreaseFree,
                CanvasTool.AddFoldingConstraint,
                CanvasTool.AngleSystem,
                CanvasTool.Axiom7,
                null
            )
        }
        
        Box(
            modifier = Modifier
                .size(400.dp)
                .background(Color.LightGray)
        ) {
            DockBar(
                viewModel = mockViewModel,
                selectedTool = CanvasTool.DrawCreaseFree,
                onToolSelected = {},
                onRequestPin = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "DockBar - С частичными инструментами", apiLevel = 34)
@Composable
private fun DockBarPartialToolsPreview() {
    MaterialTheme {
        val mockViewModel = object : DockBarViewModel() {
            override var position: Offset = Offset(100f, 100f)
            override var isVertical: Boolean = false
            override var pinnedTools: List<CanvasTool?> = listOf(
                CanvasTool.DrawCreaseFree,
                CanvasTool.AngleSystem,
                null,
                null,
                null
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            DockBar(
                viewModel = mockViewModel,
                selectedTool = CanvasTool.DrawCreaseFree,
                onToolSelected = {},
                onRequestPin = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "DockBar - Вертикальная с частичными инструментами", apiLevel = 34)
@Composable
private fun DockBarVerticalPartialPreview() {
    MaterialTheme {
        val mockViewModel = object : DockBarViewModel() {
            override var position: Offset = Offset(100f, 100f)
            override var isVertical: Boolean = true
            override var pinnedTools: List<CanvasTool?> = listOf(
                CanvasTool.DrawCreaseFree,
                CanvasTool.AngleSystem,
                CanvasTool.AddFoldingConstraint,
                null,
                null
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            DockBar(
                viewModel = mockViewModel,
                selectedTool = CanvasTool.LineSegmentDelete,
                onToolSelected = {},
                onRequestPin = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "DockBar - Все инструменты", apiLevel = 34)
@Composable
private fun DockBarAllToolsPreview() {
    MaterialTheme {
        val mockViewModel = object : DockBarViewModel() {
            override var position: Offset = Offset(100f, 100f)
            override var isVertical: Boolean = false
            override var pinnedTools: List<CanvasTool?> = listOf(
                CanvasTool.DrawCreaseFree,
                CanvasTool.AddFoldingConstraint,
                CanvasTool.AngleSystem,
                CanvasTool.Axiom7,
                CanvasTool.DrawPoint
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            DockBar(
                viewModel = mockViewModel,
                selectedTool = CanvasTool.DrawPoint,
                onToolSelected = {},
                onRequestPin = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "DockBar - Пустые слоты", apiLevel = 34)
@Composable
private fun DockBarEmptySlotsPreview() {
    MaterialTheme {
        val mockViewModel = object : DockBarViewModel() {
            override var position: Offset = Offset(100f, 100f)
            override var isVertical: Boolean = false
            override var pinnedTools: List<CanvasTool?> = listOf(
                null,
                null,
                null,
                null,
                null
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            DockBar(
                viewModel = mockViewModel,
                selectedTool = null,
                onToolSelected = {},
                onRequestPin = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "DockBar - Размеры экрана", apiLevel = 34)
@Composable
private fun DockBarScreenSizesPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Маленький экран
            Text("Маленький экран", style = MaterialTheme.typography.titleMedium)
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.LightGray)
            ) {
                val mockViewModel = object : DockBarViewModel() {
                    override var position: Offset = Offset(50f, 50f)
                    override var isVertical: Boolean = false
                    override var pinnedTools: List<CanvasTool?> = listOf(
                        CanvasTool.DrawCreaseFree,
                        CanvasTool.CircleDraw,
                        null,
                        null,
                        null
                    )
                }
                
                DockBar(
                    viewModel = mockViewModel,
                    selectedTool = CanvasTool.DrawCreaseFree,
                    onToolSelected = {},
                    onRequestPin = {}
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Средний экран
            Text("Средний экран", style = MaterialTheme.typography.titleMedium)
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(Color.LightGray)
            ) {
                val mockViewModel = object : DockBarViewModel() {
                    override var position: Offset = Offset(100f, 100f)
                    override var isVertical: Boolean = false
                    override var pinnedTools: List<CanvasTool?> = listOf(
                        CanvasTool.DrawCreaseFree,
                        CanvasTool.AddFoldingConstraint,
                        CanvasTool.AngleSystem,
                        CanvasTool.Axiom7,
                        null
                    )
                }
                
                DockBar(
                    viewModel = mockViewModel,
                    selectedTool = CanvasTool.LineSegmentDelete,
                    onToolSelected = {},
                    onRequestPin = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "DockBarButtons - Горизонтальные", apiLevel = 34)
@Composable
private fun DockBarButtonsHorizontalPreview() {
    MaterialTheme {
        val mockViewModel = object : DockBarViewModel() {
            override var position: Offset = Offset.Zero
            override var isVertical: Boolean = false
            override var pinnedTools: List<CanvasTool?> = listOf(
                CanvasTool.DrawCreaseFree,
                CanvasTool.AddFoldingConstraint,
                CanvasTool.AngleSystem,
                CanvasTool.Axiom7,
                null
            )
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DockBarButtons(
                viewModel = mockViewModel,
                selectedTool = CanvasTool.DrawCreaseFree,
                onToolSelected = {},
                onRequestPin = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "DockBarButtons - Вертикальные", apiLevel = 34)
@Composable
private fun DockBarButtonsVerticalPreview() {
    MaterialTheme {
        val mockViewModel = object : DockBarViewModel() {
            override var position: Offset = Offset.Zero
            override var isVertical: Boolean = true
            override var pinnedTools: List<CanvasTool?> = listOf(
                CanvasTool.DrawCreaseFree,
                CanvasTool.AddFoldingConstraint,
                CanvasTool.AngleSystem,
                CanvasTool.Axiom7,
                null
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DockBarButtons(
                viewModel = mockViewModel,
                selectedTool = CanvasTool.AngleSystem,
                onToolSelected = {},
                onRequestPin = {}
            )
        }
    }
} 