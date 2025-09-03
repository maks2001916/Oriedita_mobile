package com.example.oriedita_ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.oriedita_ui.service.PermissionManager
import com.example.oriedita_ui.service.PermissionStatus
import com.example.oriedita_ui.ui.MainScreen
import com.example.oriedita_ui.ui.PermissionScreen
import android.util.Log
import com.example.oriedita_ui.ui.HomeScreen

/**
 * Главная Activity приложения Oriedita
 */
class MainActivity : FragmentActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate вызван")
        setContent {
            MainActivityContent()
        }
    }
}

/**
 * Основной контент MainActivity с логикой разрешений
 */
@Composable
private fun MainActivityContent() {
    val context = LocalContext.current
    val permissionManager = remember { PermissionManager(context) }
    
    var showPermissions by remember { mutableStateOf(false) }
    var permissionsGranted by remember { mutableStateOf(false) }
    
    // Проверяем статус при запуске
    LaunchedEffect(Unit) {
        Log.d("MainActivity", "LaunchedEffect запущен")
        
        val isFirstLaunch = permissionManager.isFirstLaunch()
        Log.d("MainActivity", "isFirstLaunch: $isFirstLaunch")
        
        val permissionStatus = permissionManager.checkFilePermissions()
        Log.d("MainActivity", "permissionStatus: $permissionStatus")
        
        if (isFirstLaunch || permissionStatus == PermissionStatus.NOT_GRANTED) {
            Log.d("MainActivity", "Показываем экран разрешений")
            showPermissions = true
        } else {
            Log.d("MainActivity", "Разрешения уже предоставлены, показываем основной экран")
            permissionsGranted = true
            permissionManager.markFirstLaunchComplete()
        }
    }
    
    // Обработчик предоставления разрешений
    val onPermissionsGranted = {
        Log.d("MainActivity", "Разрешения предоставлены пользователем")
        permissionsGranted = true
        showPermissions = false
        permissionManager.markFirstLaunchComplete()
        permissionManager.markPermissionsGranted()
    }
    
    // Обработчик пропуска разрешений
    val onSkipPermissions = {
        Log.d("MainActivity", "Пользователь пропустил разрешения")
        permissionsGranted = true
        showPermissions = false
        permissionManager.markFirstLaunchComplete()
    }
    
    // Показываем экран разрешений или основной экран
    if (showPermissions) {
        Log.d("MainActivity", "Отображаем PermissionScreen")
        //PermissionScreen(onPermissionsGranted = onPermissionsGranted, onSkipPermissions = onSkipPermissions)
        //ProjectCard() { }
    } else if (permissionsGranted) {
        Log.d("MainActivity", "Отображаем MainScreen")
        MainScreen()
    } else {
        Log.d("MainActivity", "Состояние не определено: showPermissions=$showPermissions, permissionsGranted=$permissionsGranted")
    }
}