package com.example.oriedita_ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.oriedita_ui.ui.ProjectGridScreen
import com.example.oriedita_ui.ui.EditorScreen
import com.example.oriedita_ui.ui.SettingsScreen
import com.example.oriedita_ui.ui.theme.OrieditaTheme
import com.example.oriedita_ui.viewmodel.MainViewModel
import com.example.oriedita_ui.viewmodel.SettingsViewModel

/**
 * Главная активность приложения Oriedita
 * Содержит навигацию между тремя основными экранами:
 * 1. Экран с сеткой файлов
 * 2. Экран графического редактора
 * 3. Экран настроек
 */
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OrieditaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OrieditaApp()
                }
            }
        }
    }
}

@Composable
fun OrieditaApp() {
    val mainViewModel: MainViewModel = viewModel()
    val navController = rememberNavController()
    OrieditaApp(navController, mainViewModel)
}

@Composable
fun OrieditaApp(navController: NavHostController, mainViewModel: MainViewModel) {
    NavHost(
        navController = navController,
        startDestination = "project_grid"
    ) {
        // Экран с сеткой файлов (главный экран)
        composable("project_grid") {
            ProjectGridScreen(
                projects = mainViewModel.getProjects(),
                onProjectClick = { project ->
                    mainViewModel.setCurrentProject(project)
                    navController.navigate("editor")
                },
                onSettingsClick = {
                    navController.navigate("settings")
                }
            )
        }
        
        // Экран графического редактора
        composable("editor") {
            EditorScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onSettingsClick = {
                    navController.navigate("settings")
                },
                canvasViewModel = mainViewModel.getCanvasViewModel()
            )
        }
        
        // Экран настроек
        composable("settings") {
            val settingsViewModel: SettingsViewModel = viewModel()
            SettingsScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                viewModel = settingsViewModel
            )
        }
    }
}

@Preview(showBackground = true)
@Composable()
fun OrieditaAppPreview() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    OrieditaApp(navController, mainViewModel)
}