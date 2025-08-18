package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик комментариев
 * Адаптированная версия MouseHandlerComment для Android
 */
class MouseHandlerComment : BaseMouseHandler() {
    
    private var commentPosition: Point? = null
    private var commentContent: String = ""
    private var isEditing = false
    private var fontSize: Float = 14.0f
    private var commentColor: Int = 0xFF666666.toInt() // Серый цвет
    private var commentStyle: CommentStyle = CommentStyle.BUBBLE
    
    enum class CommentStyle {
        BUBBLE,    // Пузырь
        RECTANGLE, // Прямоугольник
        SIMPLE     // Простой текст
    }
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        val point = offsetToPoint(offset)
        
        if (!isEditing) {
            // Первый клик - установить позицию комментария
            commentPosition = point
            isEditing = true
            startCommentInput()
        } else {
            // Второй клик - завершить редактирование
            finishCommentInput()
        }
        
        return true
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        if (!isEditing) return false
        
        val currentPoint = offsetToPoint(offset)
        
        // Обновить позицию комментария при перетаскивании
        commentPosition = currentPoint
        
        return true
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        if (isEditing) {
            val endPoint = offsetToPoint(offset)
            commentPosition = endPoint
        }
        
        return true
    }
    
    private fun startCommentInput() {
        // Здесь должна быть логика запуска ввода комментария
        println("Начало ввода комментария в позиции: (${commentPosition?.x}, ${commentPosition?.y})")
        
        // Временный комментарий для демонстрации
        commentContent = "Комментарий"
        addCommentToCanvas()
    }
    
    private fun finishCommentInput() {
        // Здесь должна быть логика завершения ввода комментария
        println("Завершение ввода комментария: '$commentContent'")
        isEditing = false
    }
    
    private fun addCommentToCanvas() {
        // Здесь должна быть логика добавления комментария на холст
        commentPosition?.let { pos ->
            println("Добавлен комментарий: '$commentContent' в позиции (${pos.x}, ${pos.y}) стиль: $commentStyle")
        }
    }
    
    fun setCommentContent(content: String) {
        commentContent = content
        if (commentPosition != null) {
            addCommentToCanvas()
        }
    }
    
    fun getCommentContent(): String = commentContent
    
    fun setCommentPosition(position: Point) {
        commentPosition = position
    }
    
    fun getCommentPosition(): Point? = commentPosition
    
    fun setFontSize(size: Float) {
        fontSize = size
    }
    
    fun getFontSize(): Float = fontSize
    
    fun setCommentColor(color: Int) {
        commentColor = color
    }
    
    fun getCommentColor(): Int = commentColor
    
    fun setCommentStyle(style: CommentStyle) {
        commentStyle = style
    }
    
    fun getCommentStyle(): CommentStyle = commentStyle
    
    fun isEditing(): Boolean = isEditing
    
    fun startEditing() {
        isEditing = true
        startCommentInput()
    }
    
    fun stopEditing() {
        isEditing = false
        finishCommentInput()
    }
    
    fun getCommentStyleName(): String {
        return when (commentStyle) {
            CommentStyle.BUBBLE -> "Пузырь"
            CommentStyle.RECTANGLE -> "Прямоугольник"
            CommentStyle.SIMPLE -> "Простой"
        }
    }
    
    override fun getName(): String = "Комментарий"
    
    override fun getDescription(): String = "Добавьте комментарий на холст"
} 