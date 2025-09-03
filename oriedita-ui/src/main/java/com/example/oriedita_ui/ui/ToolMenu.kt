package com.example.oriedita_ui.ui

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.darkColorScheme

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

// Preview методы для разработки и тестирования UI

@Preview(showBackground = true, name = "ToolMenu - Основное меню")
@Composable
private fun ToolMenuPreviewTwo() {
    MaterialTheme {
        ToolMenu(
            show = true,
            onDismiss = {},
            onToolSelected = {},
            onToolPinRequest = {},
            selectedTool = CanvasTool.DrawCreaseFree
        )
    }
}

@Preview(showBackground = true, name = "ToolMenu - Без выбранного инструмента")
@Composable
private fun ToolMenuNoSelectionPreview() {
    MaterialTheme {
        ToolMenu(
            show = true,
            onDismiss = {},
            onToolSelected = {},
            onToolPinRequest = {},
            selectedTool = null
        )
    }
}

@Preview(showBackground = true, name = "ToolMenu - С выбранным инструментом")
@Composable
private fun ToolMenuWithSelectionPreview() {
    MaterialTheme {
        ToolMenu(
            show = true,
            onDismiss = {},
            onToolSelected = {},
            onToolPinRequest = {},
            selectedTool = CanvasTool.AngleSystem
        )
    }
}

@Preview(showBackground = true, name = "ToolMenu - Темная тема")
@Composable
private fun ToolMenuDarkThemePreview() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        ToolMenu(
            show = true,
            onDismiss = {},
            onToolSelected = {},
            onToolPinRequest = {},
            selectedTool = CanvasTool.Axiom7
        )
    }
}

@Preview(showBackground = true, name = "ToolMenu - Скрытое меню")
@Composable
private fun ToolMenuHiddenPreview() {
    MaterialTheme {
        ToolMenu(
            show = false,
            onDismiss = {},
            onToolSelected = {},
            onToolPinRequest = {},
            selectedTool = null
        )
    }
}

@Preview(showBackground = true, name = "ToolMenu - Компактный вид")
@Composable
private fun ToolMenuCompactPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ToolMenu(
                show = true,
                onDismiss = {},
                onToolSelected = {},
                onToolPinRequest = {},
                selectedTool = CanvasTool.DrawCreaseFree
            )
        }
    }
} 