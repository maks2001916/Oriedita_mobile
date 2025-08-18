package com.example.oriedita_ui.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import java.text.SimpleDateFormat
import java.util.*

// Модель данных проекта
data class Project(
    val id: Int,
    val name: String,
    val imageRes: Int,
    val filePath: String? = null,
    val lastModified: Long = System.currentTimeMillis()
)

// Карточка проекта
@Composable
fun ProjectCard(project: Project, onClick: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Заглушка для изображения (используем системную иконку)
            Box(
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = project.imageRes),
                    contentDescription = project.name,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Изменен: ${dateFormat.format(Date(project.lastModified))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (project.filePath != null) {
                    Text(
                        text = project.filePath,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// Карточка для создания нового проекта
@Composable
fun NewProjectCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Создать новый проект",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Новый проект",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

// Экран сетки проектов
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProjectGridScreen(
    projects: List<Project>,
    onProjectClick: (Project) -> Unit,
    onSettingsClick: () -> Unit
) {
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var newProjectName by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Oriedita") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки")
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = padding
        ) {
            // Карточка для создания нового проекта
            item {
                NewProjectCard(
                    onClick = { showNewProjectDialog = true }
                )
            }
            
            // Список существующих проектов
            items(projects) { project ->
                ProjectCard(project = project) { onProjectClick(project) }
            }
        }
        
        // Диалог создания нового проекта
        if (showNewProjectDialog) {
            AlertDialog(
                onDismissRequest = { showNewProjectDialog = false },
                title = { Text("Создать новый проект") },
                text = {
                    OutlinedTextField(
                        value = newProjectName,
                        onValueChange = { newProjectName = it },
                        label = { Text("Название проекта") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newProjectName.isNotBlank()) {
                                // Здесь можно добавить логику создания проекта
                                showNewProjectDialog = false
                                newProjectName = ""
                            }
                        }
                    ) {
                        Text("Создать")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showNewProjectDialog = false
                            newProjectName = ""
                        }
                    ) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

// Пример использования (можно убрать после интеграции с реальными данными)
