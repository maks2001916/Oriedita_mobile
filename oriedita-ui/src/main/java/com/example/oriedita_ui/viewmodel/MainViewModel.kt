package com.example.oriedita_ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oriedita_ui.ui.Project
import com.example.oriedita_ui.viewmodel.CanvasViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    
    private var currentProject: Project? = null
    
    // CanvasViewModel для работы с редактором
    private val _canvasViewModel = MutableStateFlow(CanvasViewModel())
    val canvasViewModel: StateFlow<CanvasViewModel> = _canvasViewModel.asStateFlow()
    
    /**
     * Получает CanvasViewModel
     */
    fun getCanvasViewModel(): CanvasViewModel {
        return _canvasViewModel.value
    }
    
    /**
     * Получает список проектов
     * В будущем здесь будет загрузка реальных файлов из хранилища
     */
    fun getProjects(): List<Project> {
        // Возвращаем тестовые проекты для демонстрации
        return listOf(
            Project(
                id = 1,
                name = "Простой журавлик",
                imageRes = android.R.drawable.ic_menu_edit,
                filePath = "crane_simple.ori",
                lastModified = System.currentTimeMillis() - 86400000 // 1 день назад
            ),
            Project(
                id = 2,
                name = "Сложная роза",
                imageRes = android.R.drawable.ic_menu_edit,
                filePath = "rose_complex.ori",
                lastModified = System.currentTimeMillis() - 172800000 // 2 дня назад
            ),
            Project(
                id = 3,
                name = "Модульная звезда",
                imageRes = android.R.drawable.ic_menu_edit,
                filePath = "star_modular.ori",
                lastModified = System.currentTimeMillis() - 259200000 // 3 дня назад
            ),
            Project(
                id = 4,
                name = "Животные оригами",
                imageRes = android.R.drawable.ic_menu_edit,
                filePath = "animals.ori",
                lastModified = System.currentTimeMillis() - 345600000 // 4 дня назад
            )
        )
    }
    
    /**
     * Устанавливает текущий проект
     */
    fun setCurrentProject(project: Project) {
        currentProject = project
    }
    
    /**
     * Получает текущий проект
     */
    fun getCurrentProject(): Project? {
        return currentProject
    }
    
    /**
     * Загружает проект из файла
     */
    fun loadProject(filePath: String): Project? {
        // Пока возвращаем null
        // В будущем здесь будет загрузка реального .ori файла
        return null
    }
    
    /**
     * Сохраняет проект в файл
     */
    fun saveProject(project: Project) {
        // В будущем здесь будет сохранение в .ori файл
        currentProject = project
    }
    
    /**
     * Создает новый проект
     */
    fun createNewProject(name: String): Project {
        val newProject = Project(
            id = 1, // Пока используем фиксированный ID
            name = name,
            imageRes = android.R.drawable.ic_menu_edit, // Используем системную иконку
            filePath = "${name.lowercase()}.ori"
        )
        currentProject = newProject
        return newProject
    }
} 