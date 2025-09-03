package com.example.oriedita_common.editor;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Вспомогательный класс для работы с цветами в Android
 * Заменяет JColorChooser из Swing
 */
public class AndroidColorHelper {

    /**
     * Показывает диалог выбора цвета
     * @param context контекст приложения
     * @param title заголовок диалога
     * @param initialColor начальный цвет
     * @param listener слушатель результата
     */
    public static void showColorDialog(Context context, String title, int initialColor,
                                     OnColorSelectedListener listener) {
        View colorView = LayoutInflater.from(context).inflate(
                android.R.layout.simple_list_item_1, null);
        
        // Создаем простой диалог с предустановленными цветами
        String[] colors = {
                "Черный", "Белый", "Красный", "Зеленый", "Синий", 
                "Желтый", "Циан", "Пурпурный", "Серый", "Оранжевый"
        };
        
        int[] colorValues = {
                Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE,
                Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.GRAY, Color.rgb(255, 165, 0)
        };

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(colors, (dialog, which) -> {
                    if (listener != null) {
                        listener.onColorSelected(colorValues[which]);
                    }
                })
                .setNegativeButton("Отмена", (dialog, which) -> {
                    if (listener != null) {
                        listener.onColorSelected(initialColor);
                    }
                })
                .show();
    }

    /**
     * Показывает диалог выбора цвета с RGB слайдерами
     * @param context контекст приложения
     * @param title заголовок диалога
     * @param initialColor начальный цвет
     * @param listener слушатель результата
     */
    public static void showRGBColorDialog(Context context, String title, int initialColor,
                                        OnColorSelectedListener listener) {
        View dialogView = LayoutInflater.from(context).inflate(
                android.R.layout.simple_list_item_1, null);
        
        // Создаем простую версию с предустановленными цветами
        // В реальном приложении здесь можно создать кастомный layout с SeekBar
        showColorDialog(context, title, initialColor, listener);
    }

    /**
     * Конвертирует Android цвет в RGB компоненты
     * @param color Android цвет
     * @return массив [R, G, B]
     */
    public static int[] colorToRGB(int color) {
        return new int[]{
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        };
    }

    /**
     * Конвертирует RGB компоненты в Android цвет
     * @param r красный компонент (0-255)
     * @param g зеленый компонент (0-255)
     * @param b синий компонент (0-255)
     * @return Android цвет
     */
    public static int rgbToColor(int r, int g, int b) {
        return Color.rgb(r, g, b);
    }

    /**
     * Конвертирует Android цвет в HSV компоненты
     * @param color Android цвет
     * @return массив [H, S, V]
     */
    public static float[] colorToHSV(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv;
    }

    /**
     * Конвертирует HSV компоненты в Android цвет
     * @param h оттенок (0-360)
     * @param s насыщенность (0-1)
     * @param v яркость (0-1)
     * @return Android цвет
     */
    public static int hsvToColor(float h, float s, float v) {
        return Color.HSVToColor(new float[]{h, s, v});
    }

    /**
     * Получает яркость цвета
     * @param color Android цвет
     * @return яркость (0-255)
     */
    public static int getBrightness(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return (r + g + b) / 3;
    }

    /**
     * Проверяет, является ли цвет темным
     * @param color Android цвет
     * @return true, если цвет темный
     */
    public static boolean isDarkColor(int color) {
        return getBrightness(color) < 128;
    }

    /**
     * Получает контрастный цвет (черный или белый)
     * @param color Android цвет
     * @return контрастный цвет
     */
    public static int getContrastColor(int color) {
        return isDarkColor(color) ? Color.WHITE : Color.BLACK;
    }

    /**
     * Интерфейс для слушателя выбора цвета
     */
    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }
} 