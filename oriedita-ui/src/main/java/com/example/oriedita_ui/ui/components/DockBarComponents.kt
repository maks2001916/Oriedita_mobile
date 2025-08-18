package com.example.oriedita_ui.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.oriedita_ui.ui.constants.UiConstants
import com.example.oriedita_ui.viewmodel.CanvasViewModel
import kotlin.math.roundToInt

/**
 * Состояние панели инструментов
 */
data class DockBarState(
    val selectedTool: Int = 0
)

/**
 * Динамическая панель инструментов
 * Адаптивная панель, которая может менять ориентацию в зависимости от положения
 */
@Composable
fun DynamicDockBar(viewModel: CanvasViewModel) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp
    val screenWidthPx = with(density) { screenWidthDp.toPx() }
    val screenHeightPx = with(density) { screenHeightDp.toPx() }

    var dockBarState by remember { mutableStateOf(DockBarState()) }

    var offset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var orientation by remember { mutableStateOf(DockOrientation.HORIZONTAL) }

    // Вычисляем ориентацию на основе положения
    LaunchedEffect(offset) {
        val positionXDp = with(density) { offset.x.toDp() }
        val positionYDp = with(density) { offset.y.toDp() }

        val distanceToTop = positionYDp
        val distanceToBottom = screenHeightDp - positionYDp - if (orientation == DockOrientation.HORIZONTAL) UiConstants.DockBar.HEIGHT_HORIZONTAL else UiConstants.DockBar.HEIGHT_VERTICAL
        val distanceToLeft = positionXDp
        val distanceToRight = screenWidthDp - positionXDp - if (orientation == DockOrientation.HORIZONTAL) UiConstants.DockBar.WIDTH_HORIZONTAL else UiConstants.DockBar.WIDTH_VERTICAL

        val minDistance = minOf(distanceToTop, distanceToBottom, distanceToLeft, distanceToRight)

        if (minDistance <= UiConstants.DockBar.MAGNET_THRESHOLD) {
            orientation = when (minDistance) {
                distanceToTop, distanceToBottom -> DockOrientation.HORIZONTAL
                distanceToLeft, distanceToRight -> DockOrientation.VERTICAL
                else -> orientation
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        offset.x.roundToInt(),
                        offset.y.roundToInt()
                    )
                }
                .clip(
                    RoundedCornerShape(
                        if (isNearEdge(
                                offset,
                                screenWidthDp,
                                screenHeightDp,
                                UiConstants.DockBar.MAGNET_THRESHOLD,
                                orientation,
                                UiConstants.DockBar.WIDTH_HORIZONTAL,
                                UiConstants.DockBar.HEIGHT_HORIZONTAL,
                                UiConstants.DockBar.WIDTH_VERTICAL,
                                UiConstants.DockBar.HEIGHT_VERTICAL,
                                density
                            )
                        ) 0.dp else UiConstants.DockBar.CORNER_RADIUS
                    )
                )
                .background(UiConstants.Colors.DOCK_BAR_BACKGROUND)
                .size(
                    if (orientation == DockOrientation.HORIZONTAL) UiConstants.DockBar.WIDTH_HORIZONTAL else UiConstants.DockBar.WIDTH_VERTICAL,
                    if (orientation == DockOrientation.HORIZONTAL) UiConstants.DockBar.HEIGHT_HORIZONTAL else UiConstants.DockBar.HEIGHT_VERTICAL
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false }
                    ) { change, dragAmount ->
                        // Ограничиваем перемещение в зависимости от ориентации
                        val newOffset = offset + dragAmount
                        val maxX = screenWidthPx - with(density) { (if (orientation == DockOrientation.HORIZONTAL) UiConstants.DockBar.WIDTH_HORIZONTAL else UiConstants.DockBar.WIDTH_VERTICAL).toPx() }
                        val maxY = screenHeightPx - with(density) { (if (orientation == DockOrientation.HORIZONTAL) UiConstants.DockBar.HEIGHT_HORIZONTAL else UiConstants.DockBar.HEIGHT_VERTICAL).toPx() }

                        offset = Offset(
                            x = newOffset.x.coerceIn(0f, maxX),
                            y = newOffset.y.coerceIn(0f, maxY)
                        )
                        
                        // Обновляем позицию локально
                        // viewModel.updateDockBarPosition(offset.x, offset.y)

                        change.consume()
                    }
                }
        ) {
            // Кнопки адаптируются к ориентации
            if (orientation == DockOrientation.HORIZONTAL) {
                // Горизонтальное расположение кнопок
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(UiConstants.DockBar.PADDING),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(UiConstants.TOOL_COUNT) { index ->
                        IconButton(
                            onClick = { 
                                // Обработка нажатия на инструмент
                                dockBarState = dockBarState.copy(selectedTool = index)
                            },
                            modifier = Modifier
                                .size(UiConstants.DockBar.BUTTON_SIZE)
                                .background(
                                    if (dockBarState.selectedTool == index) Color.Blue else UiConstants.Colors.BUTTON_BACKGROUND,
                                    CircleShape
                                )
                                .clip(CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = UiConstants.TOOL_NAMES.getOrNull(index) ?: "Инструмент ${index + 1}",
                                tint = UiConstants.Colors.BUTTON_ICON_TINT
                            )
                        }
                    }
                }
            } else {
                // Вертикальное расположение кнопок
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(UiConstants.DockBar.PADDING),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    repeat(UiConstants.TOOL_COUNT) { index ->
                        IconButton(
                            onClick = { 
                                // Обработка нажатия на инструмент
                                dockBarState = dockBarState.copy(selectedTool = index)
                            },
                            modifier = Modifier
                                .size(UiConstants.DockBar.BUTTON_SIZE)
                                .background(
                                    if (dockBarState.selectedTool == index) Color.Blue else UiConstants.Colors.BUTTON_BACKGROUND,
                                    CircleShape
                                )
                                .clip(CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = UiConstants.TOOL_NAMES.getOrNull(index) ?: "Инструмент ${index + 1}",
                                tint = UiConstants.Colors.BUTTON_ICON_TINT
                            )
                        }
                    }
                }
            }
        }
    }

    // Определяем ориентацию на основе позиции
    val newOrientation = calculateOrientation(
        offset = offset,
        screenWidth = screenWidthDp,
        screenHeight = screenHeightDp,
        magnetThreshold = UiConstants.DockBar.MAGNET_THRESHOLD,
        currentOrientation = orientation
    )

    // Обновляем ориентацию
    LaunchedEffect(newOrientation) {
        orientation = newOrientation
        // viewModel.updateDockBarOrientation(orientation == DockOrientation.HORIZONTAL)
    }
}

/**
 * Ориентация панели инструментов
 */
enum class DockOrientation {
    HORIZONTAL, VERTICAL
}

/**
 * Вычисляет оптимальную ориентацию панели на основе её положения
 */
@Composable
private fun calculateOrientation(
    offset: Offset,
    screenWidth: Dp,
    screenHeight: Dp,
    magnetThreshold: Dp,
    currentOrientation: DockOrientation
): DockOrientation {
    val density = LocalDensity.current
    val position = OffsetDp(
        x = with(density) { offset.x.toDp() },
        y = with(density) { offset.y.toDp() }
    )

    // Определяем, к какому краю ближе всего
    val distanceToTop = position.y
    val distanceToBottom = screenHeight - position.y
    val distanceToLeft = position.x
    val distanceToRight = screenWidth - position.x

    val minDistance = minOf(distanceToTop, distanceToBottom, distanceToLeft, distanceToRight)

    return when {
        minDistance <= magnetThreshold -> {
            when (minDistance) {
                distanceToTop, distanceToBottom -> DockOrientation.HORIZONTAL
                distanceToLeft, distanceToRight -> DockOrientation.VERTICAL
                else -> currentOrientation
            }
        }
        else -> currentOrientation
    }
}

/**
 * Проверяет, находится ли панель близко к краю экрана
 */
@Composable
private fun isNearEdge(
    offset: Offset,
    screenWidth: Dp,
    screenHeight: Dp,
    threshold: Dp,
    orientation: DockOrientation,
    dockWidthHorizontal: Dp,
    dockHeightHorizontal: Dp,
    dockWidthVertical: Dp,
    dockHeightVertical: Dp,
    density: Density
): Boolean {
    val positionXDp = with(density) { offset.x.toDp() }
    val positionYDp = with(density) { offset.y.toDp() }

    val distanceToTop = positionYDp
    val distanceToBottom = screenHeight - positionYDp - if (orientation == DockOrientation.HORIZONTAL) dockHeightHorizontal else dockHeightVertical
    val distanceToLeft = positionXDp
    val distanceToRight = screenWidth - positionXDp - if (orientation == DockOrientation.HORIZONTAL) dockWidthHorizontal else dockWidthVertical

    val minDistance = minOf(distanceToTop, distanceToBottom, distanceToLeft, distanceToRight)
    return minDistance <= threshold
}

/**
 * Представление позиции в Dp
 */
private data class OffsetDp(val x: Dp, val y: Dp) 