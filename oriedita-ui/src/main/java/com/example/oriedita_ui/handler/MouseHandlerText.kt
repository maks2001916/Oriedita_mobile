package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик текста
 * Адаптированная версия MouseHandlerText для Android
 */
class MouseHandlerText : BaseMouseHandler() {
    
    private var textPosition: Point? = null
    private var textContent: String = ""
    private var isEditing = false
    private var fontSize: Float = 16.0f
    private var textColor: Int = 0xFF000000.toInt() // Черный цвет
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        if (!isEditing) {
            // Первый клик - установить позицию текста
            textPosition = point
            isEditing = true
            startTextInput()
        } else {
            // Второй клик - завершить редактирование
            finishTextInput()
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isEditing) return false
        
        val currentPoint = offsetToPoint(offset)
        
        // Обновить позицию текста при перетаскивании
        textPosition = currentPoint
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (isEditing) {
            val endPoint = offsetToPoint(offset)
            textPosition = endPoint
        }
        
        return true
    }
    
    private fun startTextInput() {
        // Здесь должна быть логика запуска ввода текста
        // Например, показать диалог ввода или текстовое поле
        println("Начало ввода текста в позиции: (${textPosition?.x}, ${textPosition?.y})")
        
        // Временный текст для демонстрации
        textContent = "Текст"
        addTextToCanvas()
    }
    
    private fun finishTextInput() {
        // Здесь должна быть логика завершения ввода текста
        println("Завершение ввода текста: '$textContent'")
        isEditing = false
    }
    
    private fun addTextToCanvas() {
        // Здесь должна быть логика добавления текста на холст
        textPosition?.let { pos ->
            println("Добавлен текст: '$textContent' в позиции (${pos.x}, ${pos.y})")
        }
    }
    
    fun setTextContent(content: String) {
        textContent = content
        if (textPosition != null) {
            addTextToCanvas()
        }
    }
    
    fun getTextContent(): String = textContent
    
    fun setTextPosition(position: Point) {
        textPosition = position
    }
    
    fun getTextPosition(): Point? = textPosition
    
    fun setFontSize(size: Float) {
        fontSize = size
    }
    
    fun getFontSize(): Float = fontSize
    
    fun setTextColor(color: Int) {
        textColor = color
    }
    
    fun getTextColor(): Int = textColor
    
    fun isEditing(): Boolean = isEditing
    
    fun startEditing() {
        isEditing = true
        startTextInput()
    }
    
    fun stopEditing() {
        isEditing = false
        finishTextInput()
    }
    
    override fun getName(): String = "Текст"
    
    override fun getDescription(): String = "Добавьте текст на холст"
} 