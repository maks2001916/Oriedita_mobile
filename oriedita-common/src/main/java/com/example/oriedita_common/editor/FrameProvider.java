package com.example.oriedita_common.editor;

import android.app.Activity;
import android.content.Context;

/**
 * Интерфейс для предоставления доступа к основному контексту приложения в Android
 * Заменяет JFrame из Swing на Activity/Context из Android
 */
public interface FrameProvider {
    /**
     * Возвращает основной контекст приложения
     * @return контекст приложения
     */
    Context getContext();
    
    /**
     * Возвращает основную активность приложения (если доступна)
     * @return активность приложения или null
     */
    Activity getActivity();
    
    /**
     * Проверяет, доступна ли активность
     * @return true, если активность доступна
     */
    boolean isActivityAvailable();
}
