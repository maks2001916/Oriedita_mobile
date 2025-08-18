package com.example.oriedita_ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {
    
    // Настройки сетки
    private val _isGridVisible = MutableStateFlow(true)
    val isGridVisible: StateFlow<Boolean> = _isGridVisible.asStateFlow()
    
    private val _gridSize = MutableStateFlow(20f)
    val gridSize: StateFlow<Float> = _gridSize.asStateFlow()
    
    private val _isGridSnapEnabled = MutableStateFlow(true)
    val isGridSnapEnabled: StateFlow<Boolean> = _isGridSnapEnabled.asStateFlow()
    
    // Настройки углов
    private val _isAngleSnapEnabled = MutableStateFlow(false)
    val isAngleSnapEnabled: StateFlow<Boolean> = _isAngleSnapEnabled.asStateFlow()
    
    private val _angleSnapValue = MutableStateFlow(15f)
    val angleSnapValue: StateFlow<Float> = _angleSnapValue.asStateFlow()
    
    // Настройки отображения
    private val _lineWidth = MutableStateFlow(2f)
    val lineWidth: StateFlow<Float> = _lineWidth.asStateFlow()
    
    private val _pointSize = MutableStateFlow(8f)
    val pointSize: StateFlow<Float> = _pointSize.asStateFlow()
    
    // Настройки масштабирования
    private val _zoomLevel = MutableStateFlow(1.0f)
    val zoomLevel: StateFlow<Float> = _zoomLevel.asStateFlow()
    
    // Методы для изменения настроек
    fun setGridVisible(visible: Boolean) {
        _isGridVisible.value = visible
    }
    
    fun setGridSize(size: Float) {
        _gridSize.value = size
    }
    
    fun setGridSnapEnabled(enabled: Boolean) {
        _isGridSnapEnabled.value = enabled
    }
    
    fun setAngleSnapEnabled(enabled: Boolean) {
        _isAngleSnapEnabled.value = enabled
    }
    
    fun setAngleSnapValue(value: Float) {
        _angleSnapValue.value = value
    }
    
    fun setLineWidth(width: Float) {
        _lineWidth.value = width
    }
    
    fun setPointSize(size: Float) {
        _pointSize.value = size
    }
    
    fun zoomIn() {
        _zoomLevel.value = (_zoomLevel.value * 1.2f).coerceAtMost(5.0f)
    }
    
    fun zoomOut() {
        _zoomLevel.value = (_zoomLevel.value / 1.2f).coerceAtLeast(0.1f)
    }
    
    fun zoomReset() {
        _zoomLevel.value = 1.0f
    }
} 