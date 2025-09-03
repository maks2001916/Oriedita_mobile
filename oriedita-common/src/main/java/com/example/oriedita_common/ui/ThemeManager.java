package com.example.oriedita_common.ui;

/**
 * Интерфейс для управления темами UI, совместимый с Android Material Design
 * Заменяет FlatLaf для Android платформы
 */
public interface ThemeManager {
    
    /**
     * Применяет светлую тему
     */
    void applyLightTheme();
    
    /**
     * Применяет темную тему
     */
    void applyDarkTheme();
    
    /**
     * Применяет тему в зависимости от системных настроек
     */
    void applySystemTheme();
    
    /**
     * Применяет пользовательскую тему
     */
    void applyCustomTheme(String themeName);
    
    /**
     * Получает текущую тему
     */
    String getCurrentTheme();
    
    /**
     * Проверяет, активна ли темная тема
     */
    boolean isDarkTheme();
    
    /**
     * Получает цвет акцента
     */
    int getAccentColor();
    
    /**
     * Получает основной цвет
     */
    int getPrimaryColor();
    
    /**
     * Получает цвет фона
     */
    int getBackgroundColor();
    
    /**
     * Получает цвет текста
     */
    int getTextColor();
    
    /**
     * Получает цвет границ
     */
    int getBorderColor();
} 