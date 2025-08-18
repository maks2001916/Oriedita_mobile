package com.example.oriedita_common.graphics;

/**
 * Интерфейс для OpenGL операций, совместимый с Android OpenGL ES
 * Заменяет LWJGL для Android платформы
 */
public interface OpenGLWrapper {
    
    /**
     * Инициализирует OpenGL контекст
     */
    void init();
    
    /**
     * Очищает экран
     */
    void clear();
    
    /**
     * Устанавливает цвет очистки
     */
    void setClearColor(float r, float g, float b, float a);
    
    /**
     * Рисует линию между двумя точками
     */
    void drawLine(float x1, float y1, float x2, float y2);
    
    /**
     * Рисует точку
     */
    void drawPoint(float x, float y);
    
    /**
     * Рисует круг
     */
    void drawCircle(float x, float y, float radius);
    
    /**
     * Устанавливает цвет рисования
     */
    void setColor(float r, float g, float b, float a);
    
    /**
     * Устанавливает толщину линии
     */
    void setLineWidth(float width);
    
    /**
     * Применяет трансформацию
     */
    void pushMatrix();
    
    /**
     * Отменяет трансформацию
     */
    void popMatrix();
    
    /**
     * Перемещает объект
     */
    void translate(float x, float y, float z);
    
    /**
     * Масштабирует объект
     */
    void scale(float x, float y, float z);
    
    /**
     * Поворачивает объект
     */
    void rotate(float angle, float x, float y, float z);
    
    /**
     * Завершает работу с OpenGL
     */
    void dispose();
} 