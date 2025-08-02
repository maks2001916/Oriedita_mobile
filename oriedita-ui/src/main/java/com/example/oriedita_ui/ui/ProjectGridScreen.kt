package com.example.oriedita_ui.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.oriedita_ui.R

// Модель данных проекта
// В дальнейшем можно вынести в отдельный файл model/Project.kt

data class Project(
    val id: Int,
    val name: String,
    val imageRes: Int
)

// Карточка проекта
@Composable
fun ProjectCard(project: Project, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = project.imageRes),
                contentDescription = project.name,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = project.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

// Экран сетки проектов
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProjectGridScreen(
    projects: List<Project>,
    onProjectClick: (Project) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Oriedita") })
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = padding
        ) {
            items(projects) { project ->
                ProjectCard(project = project) { onProjectClick(project) }
            }
        }
    }
}

// Пример использования (можно убрать после интеграции с реальными данными)
val sampleProjects = listOf(
    Project(1, "Журавль", R.drawable.fishbase),
    Project(2, "Лягушка", R.drawable.fishbase),
    Project(3, "Самолётик", R.drawable.fishbase)
) 