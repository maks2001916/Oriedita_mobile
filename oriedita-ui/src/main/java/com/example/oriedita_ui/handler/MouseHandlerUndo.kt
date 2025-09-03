package com.example.oriedita_ui.handler

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import com.example.oriedita_core.origami.crease_pattern.elements.Point
import com.example.oriedita_core.origami.crease_pattern.OritaCalc

/**
 * Обработчик отмены
 * Адаптированная версия MouseHandlerUndo для Android
 */
class MouseHandlerUndo : BaseMouseHandler() {
    
    private var undoStack = mutableListOf<UndoAction>()
    private var redoStack = mutableListOf<UndoAction>()
    private var maxHistorySize: Int = 100
    private var isUndoOperation = false
    
    data class UndoAction(
        val id: String,
        val name: String,
        val timestamp: Long,
        val actionType: ActionType,
        val data: Any,
        val description: String
    ) {
        fun canUndo(): Boolean = true
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
        // Обработчик отмены обычно не реагирует на нажатия мыши
        // Вместо этого он вызывается через горячие клавиши или меню
        return false
    }
    
    override fun onDrag(offset: Offset, change: PointerInputChange): Boolean {
        // Обработчик отмены не реагирует на перетаскивание
        return false
    }
    
    override fun onRelease(offset: Offset, change: PointerInputChange): Boolean {
        // Обработчик отмены не реагирует на отпускание мыши
        return false
    }
    
    fun addAction(
        name: String,
        actionType: ActionType,
        data: Any,
        description: String
    ) {
        val action = UndoAction(
            id = generateActionId(),
            name = name,
            timestamp = System.currentTimeMillis(),
            actionType = actionType,
            data = data,
            description = description
        )
        
        undoStack.add(action)
        
        // Очистить стек повтора при добавлении нового действия
        redoStack.clear()
        
        // Ограничить размер истории
        if (undoStack.size > maxHistorySize) {
            undoStack.removeAt(0)
        }
        
        println("Добавлено действие в историю: $name")
    }
    
    override fun undo(): Boolean {
        if (undoStack.isEmpty()) {
            println("Нет действий для отмены")
            return false
        }
        
        val action = undoStack.removeAt(undoStack.size - 1)
        
        if (action.canUndo()) {
            // Выполнить отмену действия
            performUndo(action)
            
            // Добавить действие в стек повтора
            redoStack.add(action)
            
            println("Отменено действие: ${action.name}")
            return true
        } else {
            println("Действие не может быть отменено: ${action.name}")
            return false
        }
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
            
            // Добавить действие обратно в стек отмены
            undoStack.add(action)
            
            println("Повторено действие: ${action.name}")
            return true
        } else {
            println("Действие не может быть повторено: ${action.name}")
            return false
        }
    }
    
    private fun performUndo(action: UndoAction) {
        when (action.actionType) {
            ActionType.DRAW_LINE -> {
                // Удалить нарисованную линию
                println("Отмена: удаление линии")
            }
            ActionType.DELETE_LINE -> {
                // Восстановить удаленную линию
                println("Отмена: восстановление линии")
            }
            ActionType.MOVE_ELEMENT -> {
                // Вернуть элемент в исходное положение
                println("Отмена: возврат элемента в исходное положение")
            }
            ActionType.COPY_ELEMENT -> {
                // Удалить скопированный элемент
                println("Отмена: удаление скопированного элемента")
            }
            ActionType.CHANGE_PROPERTY -> {
                // Восстановить исходное свойство
                println("Отмена: восстановление исходного свойства")
            }
            ActionType.CREATE_GROUP -> {
                // Удалить созданную группу
                println("Отмена: удаление созданной группы")
            }
            ActionType.DELETE_GROUP -> {
                // Восстановить удаленную группу
                println("Отмена: восстановление удаленной группы")
            }
            ActionType.LAYER_OPERATION -> {
                // Отменить операцию со слоем
                println("Отмена: отмена операции со слоем")
            }
            ActionType.VISIBILITY_CHANGE -> {
                // Отменить изменение видимости
                println("Отмена: отмена изменения видимости")
            }
            ActionType.CUSTOM_ACTION -> {
                // Выполнить пользовательскую отмену
                println("Отмена: пользовательское действие")
            }
        }
    }
    
    private fun performRedo(action: UndoAction) {
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
    
    fun canUndo(): Boolean = undoStack.isNotEmpty()
    
    fun canRedo(): Boolean = redoStack.isNotEmpty()
    
    fun getUndoStackSize(): Int = undoStack.size
    
    fun getRedoStackSize(): Int = redoStack.size
    
    fun getMaxHistorySize(): Int = maxHistorySize
    
    fun setMaxHistorySize(size: Int) {
        maxHistorySize = size
        // Удалить лишние элементы из истории
        while (undoStack.size > maxHistorySize) {
            undoStack.removeAt(0)
        }
        println("Максимальный размер истории установлен: $size")
    }
    
    fun clearHistory() {
        undoStack.clear()
        redoStack.clear()
        println("История очищена")
    }
    
    fun getLastAction(): UndoAction? = undoStack.lastOrNull()
    
    fun getNextRedoAction(): UndoAction? = redoStack.lastOrNull()
    
    fun getActionHistory(): List<UndoAction> = undoStack.toList()
    
    fun getRedoHistory(): List<UndoAction> = redoStack.toList()
    
    fun getHistoryStatistics(): String {
        return "История: ${getUndoStackSize()}, Повтор: ${getRedoStackSize()}, Максимум: $maxHistorySize"
    }
    
    private fun generateActionId(): String {
        return "action_${System.currentTimeMillis()}_${undoStack.size}"
    }
    
    override fun getName(): String = "Отмена"
    
    override fun getDescription(): String = "Отмените последнее действие"
} 