package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик слоев
 * Адаптированная версия MouseHandlerLayer для Android
 */
class MouseHandlerLayer : BaseMouseHandler() {
    
    private var layers = mutableListOf<Layer>()
    private var currentLayer: Layer? = null
    private var selectedElements = mutableListOf<Any>()
    private var isLayerOperation = false
    
    data class Layer(
        val id: String,
        val name: String,
        val elements: MutableList<Any>,
        val isVisible: Boolean = true,
        val isLocked: Boolean = false,
        val opacity: Float = 1.0f,
        val zIndex: Int = 0
    ) {
        fun addElement(element: Any) {
            if (!elements.contains(element)) {
                elements.add(element)
            }
        }
        
        fun removeElement(element: Any) {
            elements.remove(element)
        }
        
        fun getElementCount(): Int = elements.size
        
        fun isEmpty(): Boolean = elements.isEmpty()
        
        fun isActive(): Boolean = isVisible && !isLocked
    }
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        // Найти элемент под курсором
        val elementUnderCursor = findElementAt(point)
        
        if (elementUnderCursor != null) {
            // Добавить элемент к текущему выделению
            if (!selectedElements.contains(elementUnderCursor)) {
                selectedElements.add(elementUnderCursor)
                println("Элемент добавлен к выделению: $elementUnderCursor")
            }
        } else {
            // Начать новое выделение
            selectedElements.clear()
            println("Начато новое выделение")
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        val currentPoint = offsetToPoint(offset)
        
        // Здесь можно добавить логику для выделения элементов при перетаскивании
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val endPoint = offsetToPoint(offset)
        
        // Завершить выделение
        println("Завершено выделение. Выделено элементов: ${selectedElements.size}")
        
        return true
    }
    
    private fun findElementAt(point: Point): Any? {
        // Здесь должна быть логика поиска элемента в указанной точке
        // Пока возвращаем null
        return null
    }
    
    fun createLayer(name: String): Layer {
        val layerId = generateLayerId()
        val layer = Layer(layerId, name, mutableListOf(), zIndex = layers.size)
        layers.add(layer)
        currentLayer = layer
        
        println("Создан слой '$name'")
        
        return layer
    }
    
    fun deleteLayer(layer: Layer) {
        layers.remove(layer)
        if (currentLayer == layer) {
            currentLayer = layers.firstOrNull()
        }
        println("Слой '${layer.name}' удален")
    }
    
    fun setCurrentLayer(layer: Layer?) {
        currentLayer = layer
        println("Текущий слой: ${layer?.name ?: "нет"}")
    }
    
    fun getCurrentLayer(): Layer? = currentLayer
    
    fun getLayers(): List<Layer> = layers.toList()
    
    fun getLayerCount(): Int = layers.size
    
    fun findLayerByName(name: String): Layer? {
        return layers.find { it.name == name }
    }
    
    fun findLayerById(id: String): Layer? {
        return layers.find { it.id == id }
    }
    
    fun toggleLayerVisibility(layer: Layer) {
        val newVisibility = !layer.isVisible
        val updatedLayer = layer.copy(isVisible = newVisibility)
        val index = layers.indexOf(layer)
        if (index != -1) {
            layers[index] = updatedLayer
        }
        println("Видимость слоя '${layer.name}' ${if (newVisibility) "включена" else "отключена"}")
    }
    
    fun toggleLayerLock(layer: Layer) {
        val newLocked = !layer.isLocked
        val updatedLayer = layer.copy(isLocked = newLocked)
        val index = layers.indexOf(layer)
        if (index != -1) {
            layers[index] = updatedLayer
        }
        println("Блокировка слоя '${layer.name}' ${if (newLocked) "включена" else "отключена"}")
    }
    
    fun setLayerOpacity(layer: Layer, opacity: Float) {
        val clampedOpacity = opacity.coerceIn(0.0f, 1.0f)
        val updatedLayer = layer.copy(opacity = clampedOpacity)
        val index = layers.indexOf(layer)
        if (index != -1) {
            layers[index] = updatedLayer
        }
        println("Прозрачность слоя '${layer.name}' установлена: ${clampedOpacity}")
    }
    
    fun moveLayerUp(layer: Layer) {
        val index = layers.indexOf(layer)
        if (index != -1 && index < layers.size - 1) {
            val updatedLayer = layer.copy(zIndex = layer.zIndex + 1)
            layers[index] = updatedLayer
            layers[index + 1] = layers[index + 1].copy(zIndex = layers[index + 1].zIndex - 1)
            layers.sortBy { it.zIndex }
            println("Слой '${layer.name}' перемещен вверх")
        }
    }
    
    fun moveLayerDown(layer: Layer) {
        val index = layers.indexOf(layer)
        if (index != -1 && index > 0) {
            val updatedLayer = layer.copy(zIndex = layer.zIndex - 1)
            layers[index] = updatedLayer
            layers[index - 1] = layers[index - 1].copy(zIndex = layers[index - 1].zIndex + 1)
            layers.sortBy { it.zIndex }
            println("Слой '${layer.name}' перемещен вниз")
        }
    }
    
    fun moveToLayer(layer: Layer) {
        if (selectedElements.isNotEmpty()) {
            // Переместить выделенные элементы в указанный слой
            for (element in selectedElements) {
                // Удалить элемент из всех слоев
                layers.forEach { it.removeElement(element) }
                // Добавить элемент в указанный слой
                layer.addElement(element)
            }
            println("${selectedElements.size} элементов перемещено в слой '${layer.name}'")
        }
    }
    
    fun copyToLayer(layer: Layer) {
        if (selectedElements.isNotEmpty()) {
            // Скопировать выделенные элементы в указанный слой
            for (element in selectedElements) {
                layer.addElement(element)
            }
            println("${selectedElements.size} элементов скопировано в слой '${layer.name}'")
        }
    }
    
    fun getVisibleLayers(): List<Layer> {
        return layers.filter { it.isVisible }
    }
    
    fun getActiveLayers(): List<Layer> {
        return layers.filter { it.isActive() }
    }
    
    fun getSelectedElements(): List<Any> = selectedElements.toList()
    
    fun clearSelection() {
        selectedElements.clear()
        println("Выделение очищено")
    }
    
    fun getSelectionCount(): Int = selectedElements.size
    
    private fun generateLayerId(): String {
        return "layer_${System.currentTimeMillis()}_${layers.size}"
    }
    
    override fun getName(): String = "Слои"
    
    override fun getDescription(): String = "Управляйте слоями"
} 