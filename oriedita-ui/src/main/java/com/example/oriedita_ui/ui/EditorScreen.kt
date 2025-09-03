package com.example.oriedita_ui.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.oriedita_common.resources.ResourceConstants
import com.example.oriedita_data.resource.ResourceManager
import com.example.oriedita_ui.service.Project
import com.example.oriedita_ui.viewmodel.CanvasViewModel
import com.example.oriedita_ui.viewmodel.EditorViewModel
import com.example.oriedita_ui.ui.components.*
import com.example.oriedita_ui.ui.preview.PreviewUtils
import com.example.oriedita_ui.viewmodel.LineType
import com.example.oriedita_ui.viewmodel.DockBarViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Offset

/**
 * Главный экран редактора Oriedita
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    onNavigateBack: () -> Unit,
    onSettingsClick: () -> Unit,
    canvasViewModel: CanvasViewModel,
    project: Project?
) {
    val context = LocalContext.current
    val resourceManager = remember { ResourceManager(context) }
    val editorViewModel = remember { EditorViewModel() }
    val editorState by editorViewModel.state
    LaunchedEffect(project?.id) {
        if (project != null) {
            canvasViewModel.loadProject(context, project)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { resourceManager.getString(ResourceConstants.APP_NAME)?.let { Text(it) } },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, resourceManager.getString(ResourceConstants.BACK))
                    }
                },
                actions = {
                    // Кнопка загрузки файлов
                    IconButton(
                        onClick = { editorViewModel.toggleFilePicker() }
                    ) {
                        Icon(Icons.Default.DateRange, "Загрузить файл")
                    }
                    
                    // Кнопка переключения режима предпросмотра
                    IconButton(
                        onClick = { editorViewModel.togglePreviewMode() }
                    ) {
                        Icon(Icons.Default.Search, "Предпросмотр")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (editorState.isPreviewMode) {
                PreviewCanvas(
                    canvasViewModel = canvasViewModel,
                    zoom = editorState.previewZoom,
                    showGrid = editorState.showGrid,
                    gridMode = editorState.gridMode,
                    showCreaseTypes = editorState.showCreaseTypes,
                    showPoints = editorState.showPoints,
                    showText = editorState.showText,
                    grid = editorState.grid
                )
                
                // Панель управления предпросмотром
                PreviewControlPanel(
                    zoom = editorState.previewZoom,
                    onZoomChange = { editorViewModel.setPreviewZoom(it) },
                    showGrid = editorState.showGrid,
                    onShowGridChange = { editorViewModel.toggleGrid() },
                    showCreaseTypes = editorState.showCreaseTypes,
                    onShowCreaseTypesChange = { editorViewModel.toggleCreaseTypes() },
                    showPoints = editorState.showPoints,
                    onShowPointsChange = { editorViewModel.togglePoints() },
                    showText = editorState.showText,
                    onShowTextChange = { editorViewModel.toggleText() }
                )
            } else {
                WorkspaceCanvas(
                    canvasViewModel = canvasViewModel,
                    grid = editorState.grid,
                    showGrid = editorState.showGrid,
                    gridMode = editorState.gridMode,
                    onDrag = { _, offset -> canvasViewModel.onMove(offset) },
                    onDragEnd = { canvasViewModel.onUp(Offset.Zero) }
                )
            }
            
            // Меню инструментов
            if (editorState.showToolMenu) {
                ToolMenu(
                    selectedTool = CanvasTool.DrawCreaseFree,
                    onToolSelected = { /* TODO: Implement tool selection */ }
                )
            }
            
            // File Picker
            if (editorState.showFilePicker) {
                FilePicker(
                    onFileSelected = { file ->
                        // TODO: Обработка выбранного файла
                        editorViewModel.toggleFilePicker()
                    },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 80.dp)
                )
            }

            val dockVm = remember { DockBarViewModel() }
            DockBar(
                viewModel = dockVm,
                selectedTool = canvasViewModel.getCurrentTool(),
                onToolSelected = { canvasViewModel.setTool(it) },
                onRequestPin = { /* TODO: handle pin */ }
            )

        }
    }
}



// Preview функции
@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun EditorScreenPreview() {
    MaterialTheme {
        EditorScreen(
            onNavigateBack = { },
            onSettingsClick = { },
            canvasViewModel = PreviewUtils.createEmptyCanvasViewModel(),
            project = null
        )
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun WorkspaceCanvasPreview() {
    MaterialTheme {
        WorkspaceCanvas(
            canvasViewModel = PreviewUtils.createPreviewCanvasViewModel(),
            grid = PreviewUtils.createPreviewGrid(),
            showGrid = true
        )
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun PreviewCanvasPreview() {
    MaterialTheme {
        PreviewCanvas(
            canvasViewModel = PreviewUtils.createPreviewCanvasViewModel(),
            zoom = 1.0f,
            showGrid = true,
            showCreaseTypes = true,
            showPoints = true,
            showText = true,
            grid = PreviewUtils.createPreviewGrid()
        )
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun PreviewControlPanelPreview() {
    MaterialTheme {
        PreviewControlPanel(
            zoom = 1.0f,
            onZoomChange = { },
            showGrid = true,
            onShowGridChange = { },
            showCreaseTypes = true,
            onShowCreaseTypesChange = { },
            showPoints = true,
            onShowPointsChange = { },
            showText = true,
            onShowTextChange = { }
        )
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun ToolMenuPreview() {
    MaterialTheme {
        ToolMenu(
            selectedTool = CanvasTool.DrawCreaseFree,
            onToolSelected = { }
        )
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun LineTypeMenuPreview() {
    MaterialTheme {
        LineTypeMenu(
            selectedLineType = LineType.EDGE,
            onLineTypeSelected = { }
        )
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun FilePickerPreview() {
    MaterialTheme {
        FilePicker(
            onFileSelected = { }
        )
    }
}

 