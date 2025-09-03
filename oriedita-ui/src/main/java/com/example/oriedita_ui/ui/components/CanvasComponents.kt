package com.example.oriedita_ui.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.example.oriedita_ui.ui.constants.UiConstants

/**
 * Состояние холста
 */
data class CanvasState(
    val tapCount: Int = 0,
    val lastTapPosition: Pair<Float, Float> = Pair(0f, 0f)
)

/**
 * Холст для рисования
 * Обрабатывает касания и отображает базовые элементы
 */
@Composable
fun DrawCanvas() {
    var canvasState by remember { mutableStateOf(CanvasState()) }
    
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset: Offset ->
                    canvasState = canvasState.copy(
                        tapCount = canvasState.tapCount + 1,
                        lastTapPosition = Pair(offset.x, offset.y)
                    )
                }
            }
    ) {
        // Фон холста
        drawRect(
            color = UiConstants.Colors.CANVAS_BACKGROUND,
            size = Size(size.width, size.height)
        )
        
        // Базовый элемент для демонстрации
        drawRect(
            color = Color.Red,
            size = Size(200f, 200f)
        )
        
        // Отображение последнего касания
        if (canvasState.tapCount > 0) {
            drawCircle(
                color = Color.Blue,
                radius = 10f,
                center = Offset(canvasState.lastTapPosition.first, canvasState.lastTapPosition.second)
            )
        }
    }
} 