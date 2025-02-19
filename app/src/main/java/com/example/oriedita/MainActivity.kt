package com.example.oriedita

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            MainScreen()

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Oriedita") }) },
        floatingActionButton = { ToolsMenu(viewModel) },
        content = { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // Холст для рисования
                CanvasView(viewModel)
                // Панель инструментов
                ToolsPanel(viewModel)
            }
        }
    )
}

@Composable
fun CanvasView(viewModel: MainViewModel) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    offset += pan
                }
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        ) {
            // Рендерим линии складок
            viewModel.creasePattern.lines.forEach { line ->
                drawLine(
                    color = Color.Black,
                    start = Offset(line.start.x.toFloat(), line.start.y.toFloat()),
                    end = Offset(line.end.x.toFloat(), line.end.y.toFloat()),
                    strokeWidth = 2f
                )
            }
        }
    }
}

@Composable
fun ToolsMenu(viewModel: MainViewModel) {
    var expanded by remember { mutableStateOf(false) }

    ExtendedFloatingActionButton(
        onClick = { expanded = true },
        icon = { Icon(Icons.Default.Build, "Tools") },
        text = { Text("Инструменты") }
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("Карандаш") },
            onClick = { viewModel.selectTool(Tool.PENCIL) }
        )
        DropdownMenuItem(
            text = { Text("Ластик") },
            onClick = { viewModel.selectTool(Tool.ERASER) }
        )
        Divider()
        DropdownMenuItem(
            text = { Text("Сохранить") },
            onClick = { /* Открыть диалог сохранения */ }
        )
    }
}

@Composable
fun SaveDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var filename by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Сохранить проект") },
        text = {
            OutlinedTextField(
                value = filename,
                onValueChange = { filename = it },
                label = { Text("Имя файла") }
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(filename) }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun Canvas(modifier: Any, content: () -> Unit) {

}

fun getTime(): String {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minutes = calendar.get(Calendar.MINUTE)
    return "$hour:$minutes"
}

@Composable
fun Preview() {

}

val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
    addCategory(Intent.CATEGORY_OPENABLE)
    type = "application/json" // или другой MIME-тип  
}
startActivityForResult(intent, REQUEST_CODE_OPEN_FILE)

// Обработка результата  
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_CODE_OPEN_FILE && resultCode == RESULT_OK) {
        data?.data?.let { uri ->
            contentResolver.openInputStream(uri)?.use { stream ->
                viewModel.loadProject(stream)
            }
        }
    }
}  