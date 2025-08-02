package com.example.oriedita_ui.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun ToolMenu(
    show: Boolean,
    onDismiss: () -> Unit,
    onToolSelected: (CanvasTool) -> Unit,
    onToolPinRequest: (CanvasTool) -> Unit,
    selectedTool: CanvasTool?
) {
    if (!show) return
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                toolGroups.forEach { group ->
                    Text(
                        text = group.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .fillMaxWidth()
                    ) {
                        group.tools.forEach { tool ->
                            var showPinMenu by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .pointerInput(tool) {
                                        detectTapGestures(
                                            onTap = { onToolSelected(tool) },
                                            onLongPress = { showPinMenu = true }
                                        )
                                    }
                            ) {
                                FilterChip(
                                    selected = tool == selectedTool,
                                    onClick = { onToolSelected(tool) },
                                    label = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            val painter = tool.iconPainter()
                                            Icon(
                                                painter = painter,
                                                contentDescription = tool.label,
                                                modifier = Modifier.size(20.dp),
                                                tint = if (tool == selectedTool) MaterialTheme.colorScheme.primary else Color.Unspecified
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(tool.label)
                                        }
                                    },
                                    modifier = Modifier.height(40.dp)
                                )
                                DropdownMenu(
                                    expanded = showPinMenu,
                                    onDismissRequest = { showPinMenu = false }
                                ) {
                                    (1..5).forEach { pos ->
                                        DropdownMenuItem(
                                            text = { Text("Закрепить на позиции $pos") },
                                            onClick = {
                                                showPinMenu = false
                                                onToolPinRequest(tool)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 