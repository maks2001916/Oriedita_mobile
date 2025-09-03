package com.example.oriedita_ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.oriedita_data.databinding.ApplicationModel
import com.example.oriedita_data.databinding.GridModel
import com.example.oriedita_data.drawing.Grid
import com.example.oriedita_ui.ui.state.EditorAction
import com.example.oriedita_ui.ui.state.EditorState
import com.example.oriedita_ui.ui.state.GridDisplayMode

/**
 * ViewModel для управления состоянием редактора
 */
class EditorViewModel : ViewModel() {

    private val _state = mutableStateOf(EditorState())
    val state: State<EditorState> = _state

    init {
        initializeGrid()
    }

    /**
     * Обработка действий редактора
     */
    fun dispatch(action: EditorAction) {
        when (action) {
            is EditorAction.SetPreviewMode -> {
                _state.value = _state.value.copy(isPreviewMode = action.enabled)
            }
            is EditorAction.SetPreviewZoom -> {
                _state.value = _state.value.copy(previewZoom = action.zoom)
            }
            is EditorAction.SetShowGrid -> {
                _state.value = _state.value.copy(showGrid = action.show)
            }
            is EditorAction.SetGridMode -> {
                _state.value = _state.value.copy(gridMode = action.mode)
            }
            is EditorAction.SetShowCreaseTypes -> {
                _state.value = _state.value.copy(showCreaseTypes = action.show)
            }
            is EditorAction.SetShowPoints -> {
                _state.value = _state.value.copy(showPoints = action.show)
            }
            is EditorAction.SetShowText -> {
                _state.value = _state.value.copy(showText = action.show)
            }
            is EditorAction.SetShowToolMenu -> {
                _state.value = _state.value.copy(showToolMenu = action.show)
            }
            is EditorAction.SetShowLineTypeMenu -> {
                _state.value = _state.value.copy(showLineTypeMenu = action.show)
            }
            is EditorAction.SetShowPreviewMenu -> {
                _state.value = _state.value.copy(showPreviewMenu = action.show)
            }
            is EditorAction.SetShowFilePicker -> {
                _state.value = _state.value.copy(showFilePicker = action.show)
            }
            is EditorAction.InitializeGrid -> {
                initializeGrid(action.gridSize)
            }
        }
    }

    /**
     * Инициализация сетки с настройками
     */
    private fun initializeGrid(gridSize: Int = 8) {
        val grid = Grid().apply {
            setGridSize(gridSize)
            val appModel = ApplicationModel()
            val gridModel = GridModel()
            setData(appModel)
            setGridConfigurationData(gridModel)
        }

        _state.value = _state.value.copy(grid = grid)
    }

    /**
     * Переключение режима предпросмотра
     */
    fun togglePreviewMode() {
        dispatch(EditorAction.SetPreviewMode(!_state.value.isPreviewMode))
    }

    /**
     * Установка масштаба предпросмотра
     */
    fun setPreviewZoom(zoom: Float) {
        dispatch(EditorAction.SetPreviewZoom(zoom))
    }

    /**
     * Переключение отображения сетки
     */
    fun toggleGrid() {
        dispatch(EditorAction.SetShowGrid(!_state.value.showGrid))
    }

    fun setGridMode(mode: GridDisplayMode) {
        dispatch(EditorAction.SetGridMode(mode))
    }

    /**
     * Переключение отображения типов складок
     */
    fun toggleCreaseTypes() {
        dispatch(EditorAction.SetShowCreaseTypes(!_state.value.showCreaseTypes))
    }

    /**
     * Переключение отображения точек
     */
    fun togglePoints() {
        dispatch(EditorAction.SetShowPoints(!_state.value.showPoints))
    }

    /**
     * Переключение отображения текста
     */
    fun toggleText() {
        dispatch(EditorAction.SetShowText(!_state.value.showText))
    }

    /**
     * Переключение меню инструментов
     */
    fun toggleToolMenu() {
        dispatch(EditorAction.SetShowToolMenu(!_state.value.showToolMenu))
    }

    /**
     * Переключение меню типов линий
     */
    fun toggleLineTypeMenu() {
        dispatch(EditorAction.SetShowLineTypeMenu(!_state.value.showLineTypeMenu))
    }

    /**
     * Переключение меню предпросмотра
     */
    fun togglePreviewMenu() {
        dispatch(EditorAction.SetShowPreviewMenu(!_state.value.showPreviewMenu))
    }

    /**
     * Переключение File Picker
     */
    fun toggleFilePicker() {
        dispatch(EditorAction.SetShowFilePicker(!_state.value.showFilePicker))
    }
}