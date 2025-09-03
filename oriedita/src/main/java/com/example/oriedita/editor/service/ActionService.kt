package com.example.oriedita.editor.service

import com.example.oriedita_ui.action.ComposeActionType

/**
 * Сервис действий для Android
 * Адаптированная версия ActionService для Android
 */
class ActionService {
    
    private val actionHandlers = mutableMapOf<ComposeActionType, () -> Unit>()
    
    /**
     * Зарегистрировать обработчик действия
     */
    fun registerAction(actionType: ComposeActionType, handler: () -> Unit) {
        actionHandlers[actionType] = handler
    }
    
    /**
     * Выполнить действие
     */
    fun executeAction(actionType: ComposeActionType): Boolean {
        val handler = actionHandlers[actionType]
        return if (handler != null) {
            try {
                handler()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } else {
            false
        }
    }
    
    /**
     * Проверить, зарегистрировано ли действие
     */
    fun isActionRegistered(actionType: ComposeActionType): Boolean {
        return actionHandlers.containsKey(actionType)
    }
    
    /**
     * Получить список всех зарегистрированных действий
     */
    fun getRegisteredActions(): List<ComposeActionType> {
        return actionHandlers.keys.toList()
    }
    
    /**
     * Очистить все действия
     */
    fun clearActions() {
        actionHandlers.clear()
    }
} 