package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик повтора
 * Адаптированная версия MouseHandlerRedo для Android
 */
class MouseHandlerRedo : BaseMouseHandler() {
    
    private var redoStack = mutableListOf<RedoAction>()
    private var maxRedoSize: Int = 100
    private var isRedoOperation = false
    
    data class RedoAction(
        val id: String,
        val name: String,
        val timestamp: Long,
        val actionType: ActionType,
        val data: Any,
        val description: String
    ) {
        fun canRedo(): Boolean = true
    }
    
    enum class ActionType {
        DRAW_LINE,
        DELETE_LINE,
        MOVE_ELEMENT,
        COPY_ELEMENT,
        CHANGE_PROPERTY,
        CREATE_GROUP,
        DELETE_GROUP,
        LAYER_OPERATION,
        VISIBILITY_CHANGE,
        CUSTOM_ACTION
    }
    
    override fun onPress(offset: Offset, change: PointerInputChange): Boolean {
        // Обработчик повтора обычно не реагирует на нажатия мыши
        // Вместо этого он вызывается через горячие клавиши или меню
        return false
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // Обработчик повтора не реагирует на перетаскивание
        return false
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        // Обработчик повтора не реагирует на отпускание мыши
        return false
    }
    
    fun addRedoAction(
        name: String,
        actionType: ActionType,
        data: Any,
        description: String
    ) {
        val action = RedoAction(
            id = generateActionId(),
            name = name,
            timestamp = System.currentTimeMillis(),
            actionType = actionType,
            data = data,
            description = description
        )
        
        redoStack.add(action)
        
        // Ограничить размер стека повтора
        if (redoStack.size > maxRedoSize) {
            redoStack.removeAt(0)
        }
        
        println("Добавлено действие для повтора: $name")
    }
    
    override fun redo(): Boolean {
        if (redoStack.isEmpty()) {
            println("Нет действий для повтора")
            return false
        }
        
        val action = redoStack.removeAt(redoStack.size - 1)
        
        if (action.canRedo()) {
            // Выполнить повтор действия
            performRedo(action)
            
            println("Повторено действие: ${action.name}")
            return true
        } else {
            println("Действие не может быть повторено: ${action.name}")
            return false
        }
    }
    
    fun redoMultiple(count: Int): Int {
        var successCount = 0
        for (i in 0 until count) {
            if (redo()) {
                successCount++
            } else {
                break
            }
        }
        println("Повторено $successCount действий из $count")
        return successCount
    }
    
    private fun performRedo(action: RedoAction) {
        when (action.actionType) {
            ActionType.DRAW_LINE -> {
                // Повторить рисование линии
                println("Повтор: рисование линии")
            }
            ActionType.DELETE_LINE -> {
                // Повторить удаление линии
                println("Повтор: удаление линии")
            }
            ActionType.MOVE_ELEMENT -> {
                // Повторить перемещение элемента
                println("Повтор: перемещение элемента")
            }
            ActionType.COPY_ELEMENT -> {
                // Повторить копирование элемента
                println("Повтор: копирование элемента")
            }
            ActionType.CHANGE_PROPERTY -> {
                // Повторить изменение свойства
                println("Повтор: изменение свойства")
            }
            ActionType.CREATE_GROUP -> {
                // Повторить создание группы
                println("Повтор: создание группы")
            }
            ActionType.DELETE_GROUP -> {
                // Повторить удаление группы
                println("Повтор: удаление группы")
            }
            ActionType.LAYER_OPERATION -> {
                // Повторить операцию со слоем
                println("Повтор: операция со слоем")
            }
            ActionType.VISIBILITY_CHANGE -> {
                // Повторить изменение видимости
                println("Повтор: изменение видимости")
            }
            ActionType.CUSTOM_ACTION -> {
                // Выполнить пользовательский повтор
                println("Повтор: пользовательское действие")
            }
        }
    }
    
    fun canRedo(): Boolean = redoStack.isNotEmpty()
    
    fun getRedoStackSize(): Int = redoStack.size
    
    fun getMaxRedoSize(): Int = maxRedoSize
    
    fun setMaxRedoSize(size: Int) {
        maxRedoSize = size
        // Удалить лишние элементы из стека повтора
        while (redoStack.size > maxRedoSize) {
            redoStack.removeAt(0)
        }
        println("Максимальный размер стека повтора установлен: $size")
    }
    
    fun clearRedoStack() {
        redoStack.clear()
        println("Стек повтора очищен")
    }
    
    fun getNextRedoAction(): RedoAction? = redoStack.lastOrNull()
    
    fun getRedoHistory(): List<RedoAction> = redoStack.toList()
    
    fun getRedoStatistics(): String {
        return "Повтор: ${getRedoStackSize()}, Максимум: $maxRedoSize"
    }
    
    fun peekNextAction(): RedoAction? {
        return redoStack.lastOrNull()
    }
    
    fun getNextActionDescription(): String {
        val nextAction = peekNextAction()
        return nextAction?.description ?: "Нет действий для повтора"
    }
    
    fun getNextActionName(): String {
        val nextAction = peekNextAction()
        return nextAction?.name ?: "Нет действий"
    }
    
    fun getRedoStackInfo(): String {
        if (redoStack.isEmpty()) {
            return "Стек повтора пуст"
        }
        
        val sb = StringBuilder()
        sb.appendLine("Стек повтора (${redoStack.size} действий):")
        
        for ((index, action) in redoStack.asReversed().withIndex()) {
            if (index < 5) { // Показать только последние 5 действий
                sb.appendLine("${index + 1}. ${action.name} - ${action.description}")
            }
        }
        
        if (redoStack.size > 5) {
            sb.appendLine("... и еще ${redoStack.size - 5} действий")
        }
        
        return sb.toString()
    }
    
    private fun generateActionId(): String {
        return "redo_${System.currentTimeMillis()}_${redoStack.size}"
    }
    
    override fun getName(): String = "Повтор"
    
    override fun getDescription(): String = "Повторите отмененное действие"
} 