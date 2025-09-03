package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point

/**
 * Обработчик изменения стандартной грани
 * Адаптированная версия для Android
 */
class MouseHandlerChangeStandardFace : BaseMouseHandler() {
    
    private var oldStartingFaceId = -1
    private var newStartingFaceId = -1
    private var isProcessing = false
    private var selectedFigure: FoldedFigureDrawer? = null
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        // Изменение стандартной грани не требует обработки нажатия
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // Изменение стандартной грани не требует перетаскивания
        return false
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        // Получить выбранную фигуру
        selectedFigure = getSelectedFoldedFigure()
        
        if (selectedFigure != null) {
            // Получить старый ID начальной грани
            oldStartingFaceId = selectedFigure!!.getStartingFaceId()
            
            // Определить новую начальную грань
            newStartingFaceId = determineStartingFaceId(point, selectedFigure!!)
            
            if (newStartingFaceId >= 1) {
                // Установить новую начальную грань
                selectedFigure!!.setStartingFaceId(newStartingFaceId)
                
                println("Стандартная грань изменена: $oldStartingFaceId -> $newStartingFaceId")
                
                // Обновить состояние оценки, если ID изменился
                if (newStartingFaceId != oldStartingFaceId) {
                    updateEstimationStep(selectedFigure!!)
                }
                
                // Логировать информацию о грани
                logFaceInformation(newStartingFaceId, selectedFigure!!)
            }
        }
        
        return true
    }
    
    private fun getSelectedFoldedFigure(): FoldedFigureDrawer? {
        // Здесь должна быть логика получения выбранной сложенной фигуры
        // Пока возвращаем null
        return null
    }
    
    private fun determineStartingFaceId(point: Point, figure: FoldedFigureDrawer): Int {
        // Здесь должна быть логика определения ID грани по точке
        // Пока возвращаем -1
        return -1
    }
    
    private fun updateEstimationStep(figure: FoldedFigureDrawer) {
        // Здесь должна быть логика обновления шага оценки
        println("Шаг оценки обновлен для фигуры")
    }
    
    private fun logFaceInformation(faceId: Int, figure: FoldedFigureDrawer) {
        println("Стандартная грань ID: $faceId")
        
        // Здесь должна быть логика получения дополнительной информации о грани
        // Например, индекс последовательности и рейтинг
        val sequenceIndex = getSequenceIndex(faceId, figure)
        val weight = getFaceWeight(sequenceIndex, figure)
        
        if (sequenceIndex >= 0) {
            println("Индекс последовательности: $sequenceIndex, Рейтинг: $weight")
        }
    }
    
    private fun getSequenceIndex(faceId: Int, figure: FoldedFigureDrawer): Int {
        // Здесь должна быть логика получения индекса последовательности
        // Пока возвращаем -1
        return -1
    }
    
    private fun getFaceWeight(sequenceIndex: Int, figure: FoldedFigureDrawer): Double {
        // Здесь должна быть логика получения веса грани
        // Пока возвращаем 0.0
        return 0.0
    }
    
    fun getOldStartingFaceId(): Int = oldStartingFaceId
    
    fun getNewStartingFaceId(): Int = newStartingFaceId
    
    fun getSelectedFigure(): FoldedFigureDrawer? = selectedFigure
    
    fun isProcessing(): Boolean = isProcessing
    
    fun getFaceChangeDescription(): String {
        return if (oldStartingFaceId >= 0 && newStartingFaceId >= 0) {
            "Стандартная грань изменена с $oldStartingFaceId на $newStartingFaceId"
        } else {
            "Стандартная грань не изменена"
        }
    }
    
    fun getSelectedFigureDescription(): String {
        return selectedFigure?.let { "Выбрана фигура: ${it.name}" } ?: "Фигура не выбрана"
    }
    
    override fun getName(): String = "Изменение стандартной грани"
    
    override fun getDescription(): String = "Измените стандартную грань сложенной фигуры"
}

/**
 * Класс для представления сложенной фигуры
 */
data class FoldedFigureDrawer(
    val id: Int,
    val name: String,
    private var startingFaceId: Int = 1
) {
    fun getStartingFaceId(): Int = startingFaceId
    
    fun setStartingFaceId(faceId: Int) {
        startingFaceId = faceId
    }
    

} 