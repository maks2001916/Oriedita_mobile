package com.example.oriedita.editor

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrawCanvas()

        }
    }

}


@Composable
fun DrawCanvas() {

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { offset: Offset ->
                    println("tap at $offset")
                })
            }) {
        drawRect(
            color = Color.Red,
            size = Size(200f, 200f)
        )
    }
}

@Composable
fun DynamicDockBar() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val cornerRadius = 16.dp
    val magnetThreshold = 32.dp

    var offset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    var orientation by remember { mutableStateOf(DockOrientation.HORIZONTAL) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
                                screenWidth,
                                screenHeight,
                                magnetThreshold
                            )
                        ) 0.dp else cornerRadius
                    )
                )
                .background(Color(0xFF2C3E50))
                .size(if (orientation == DockOrientation.HORIZONTAL) 300.dp else 60.dp,
                    if (orientation == DockOrientation.HORIZONTAL) 60.dp else 300.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false }
                    ) { change, dragAmount ->
                        // Ограничиваем перемещение в зависимости от ориентации
                        val newOffset = offset + dragAmount
                        val maxX = if (orientation == DockOrientation.HORIZONTAL) {
                            screenWidth.toPx() - 300.dp.toPx()
                        } else {
                            screenWidth.toPx() - 60.dp.toPx()
                        }
                        val maxY = if (orientation == DockOrientation.HORIZONTAL) {
                            screenHeight.toPx() - 60.dp.toPx()
                        } else {
                            screenHeight.toPx() - 300.dp.toPx()
                        }

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
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.White
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
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.White
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
        screenWidth = screenWidth,
        screenHeight = screenHeight,
        magnetThreshold = magnetThreshold,
        currentOrientation = orientation
    )

    // Обновляем ориентацию
    LaunchedEffect(newOrientation) {
        orientation = newOrientation
    }
}

enum class DockOrientation {
    HORIZONTAL, VERTICAL
}

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

    // Добавляем отладочную информацию
    Log.d("DockBar", "Position: $position")
    Log.d("DockBar", "Distance to top: $distanceToTop, bottom: $distanceToBottom, left: $distanceToLeft, right: $distanceToRight")
    Log.d("DockBar", "Magnet threshold: $magnetThreshold")

    val minDistance = minOf(distanceToTop, distanceToBottom, distanceToLeft, distanceToRight)
    Log.d("DockBar", "Min distance: $minDistance")

    val newOrientation = when {
        minDistance <= magnetThreshold -> {
            when (minDistance) {
                distanceToTop, distanceToBottom -> {
                    Log.d("DockBar", "Switching to HORIZONTAL")
                    DockOrientation.HORIZONTAL
                }
                distanceToLeft, distanceToRight -> {
                    Log.d("DockBar", "Switching to VERTICAL")
                    DockOrientation.VERTICAL
                }
                else -> {
                    Log.d("DockBar", "Keeping current orientation: $currentOrientation")
                    currentOrientation
                }
            }
        }
        else -> {
            Log.d("DockBar", "Not near edge, keeping current orientation: $currentOrientation")
            currentOrientation
        }
    }

    Log.d("DockBar", "New orientation: $newOrientation")
    return newOrientation
}

@Composable
private fun isNearEdge(
    offset: Offset,
    screenWidth: Dp,
    screenHeight: Dp,
    threshold: Dp
): Boolean {
    val density = LocalDensity.current
    val position = OffsetDp(
        x = with(density) { offset.x.toDp() },
        y = with(density) { offset.y.toDp() }
    )
    val distanceToTop = position.y
    val distanceToBottom = screenHeight - position.y
    val distanceToLeft = position.x
    val distanceToRight = screenWidth - position.x

    val minDistance = minOf(distanceToTop, distanceToBottom, distanceToLeft, distanceToRight)
    return minDistance <= threshold
}



private data class OffsetDp(val x: Dp, val y: Dp)


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OrieditaUiDemoTwoTheme {
        DrawCanvas()
        DynamicDockBar()
    }
}  