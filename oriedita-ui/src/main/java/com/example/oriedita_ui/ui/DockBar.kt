package com.example.oriedita_ui.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun DockBar(
    viewModel: DockBarViewModel,
    selectedTool: CanvasTool?,
    onToolSelected: (CanvasTool) -> Unit,
    onRequestPin: (Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
    var dragOffset by remember { mutableStateOf(viewModel.position) }

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
            .padding(8.dp)
    ) {
        if (viewModel.isVertical) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DockBarButtons(viewModel, selectedTool, onToolSelected, onRequestPin)
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                DockBarButtons(viewModel, selectedTool, onToolSelected, onRequestPin)
            }
        }
    }
}

@Composable
private fun DockBarButtons(
    viewModel: DockBarViewModel,
    selectedTool: CanvasTool?,
    onToolSelected: (CanvasTool) -> Unit,
    onRequestPin: (Int) -> Unit
) {
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
                val painter = tool.iconPainter()
                Icon(
                    painter = painter,
                    contentDescription = tool.label,
                    modifier = Modifier.align(Alignment.Center),
                    tint = if (tool == selectedTool) Color.White else Color.Unspecified
                )
            } else {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Add,
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