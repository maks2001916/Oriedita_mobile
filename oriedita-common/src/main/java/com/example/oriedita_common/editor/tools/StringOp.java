package com.example.oriedita_common.editor.tools;

import android.graphics.Color;

/**
 * Утилиты для работы со строками в Android приложении
 * Предоставляет методы для конвертации строк в числа и работы с цветами
 */
public class StringOp {

    /**
     * Конвертирует строку в целое число
     * Поддерживает как целые числа, так и числа с плавающей точкой (округляет)
     * 
     * @param str0 строка для конвертации
     * @param henkan_error_modoriti значение, возвращаемое при ошибке конвертации
     * @return целое число или значение по умолчанию при ошибке
     */
    public static int String2int(String str0, int henkan_error_modoriti) {

        int s_decision;
        s_decision = String2suuti_decision(str0);

        if (s_decision == 1) {
            return Integer.parseInt(str0);
        }
        if (s_decision == 2) {
            double d0;
            d0 = Double.parseDouble(str0);
            return (int) Math.round(d0);
        }

        return henkan_error_modoriti;
    }

    /**
     * Конвертирует строку в число с плавающей точкой
     * Поддерживает как целые числа, так и числа с плавающей точкой
     * 
     * @param str0 строка для конвертации
     * @param henkan_error_modoriti значение, возвращаемое при ошибке конвертации
     * @return число с плавающей точкой или значение по умолчанию при ошибке
     */
    public static double String2double(String str0, double henkan_error_modoriti) {

        int s_decision;
        s_decision = String2suuti_decision(str0);

        if (s_decision == 1) {
            return Integer.parseInt(str0);
        }
        if (s_decision == 2) {
            return Double.parseDouble(str0);
        }

        return henkan_error_modoriti;
    }

    /**
     * Проверяет, можно ли конвертировать строку в целое число
     * 
     * @param str0 строка для проверки
     * @return 1, если конвертация возможна; 0, если невозможно
     */
    public static int String2int_confirm(String str0) {
        try {
            Integer.parseInt(str0);
            return 1;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Проверяет, можно ли конвертировать строку в число с плавающей точкой
     * 
     * @param str0 строка для проверки
     * @return 1, если конвертация возможна; 0, если невозможно
     */
    public static int String2double_confirm(String str0) {
        try {
            Double.parseDouble(str0);
            return 1;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Определяет тип числового значения в строке
     * 
     * @param str0 строка для анализа
     * @return 2 для double, 1 для int, 0 если не является числом
     */
    public static int String2suuti_decision(String str0) {
        int i_r = 0;
        if (String2double_confirm(str0) == 1) {
            i_r = 2;
        }
        if (String2int_confirm(str0) == 1) {
            i_r = 1;
        }
        return i_r;
    }

    // ---------------------------------

    /**
     * Конвертирует Android цвет в HTML цветовой код
     * 
     * @param col Android цвет для конвертации
     * @return HTML цветовой код в формате #RRGGBB
     */
    public static String toHtmlColor(int col) {
        // Возвращает белый цвет, если передан недопустимый цвет
        if (col == Color.TRANSPARENT) {
            return "#FFFFFF";
        }
        String str;
        String colCode;

        str = "#";

        // Получаем красный компонент в шестнадцатеричном формате
        colCode = Integer.toHexString(Color.red(col));
        if (colCode.length() == 1) {
            colCode = "0" + colCode;
        }
        str = str + colCode;
        
        // Получаем зеленый компонент в шестнадцатеричном формате
        colCode = Integer.toHexString(Color.green(col));
        if (colCode.length() == 1) {
            colCode = "0" + colCode;
        }
        str = str + colCode;

        // Получаем синий компонент в шестнадцатеричном формате
        colCode = Integer.toHexString(Color.blue(col));
        if (colCode.length() == 1) {
            colCode = "0" + colCode;
        }
        str = str + colCode;

        return str;
    }

    /**
     * Конвертирует Android цвет в HTML цветовой код (перегрузка для совместимости)
     * 
     * @param col Android цвет для конвертации (может быть null)
     * @return HTML цветовой код в формате #RRGGBB
     */
    public static String toHtmlColor(Integer col) {
        // Возвращает белый цвет, если передан null
        if (col == null) {
            return "#FFFFFF";
        }
        return toHtmlColor(col.intValue());
    }

    /**
     * Проверяет, является ли строка пустой или null
     * 
     * @param val строка для проверки
     * @return true, если строка null или пустая; false в противном случае
     */
    public static boolean isEmpty(String val) {
        return val == null || val.isEmpty();
    }

    /**
     * Конвертирует HTML цветовой код в Android цвет
     * 
     * @param htmlColor HTML цветовой код в формате #RRGGBB или #RGB
     * @return Android цвет или Color.BLACK при ошибке
     */
    public static int htmlColorToAndroidColor(String htmlColor) {
        try {
            if (htmlColor == null || htmlColor.isEmpty()) {
                return Color.BLACK;
            }
            
            // Убираем символ # если есть
            String color = htmlColor.startsWith("#") ? htmlColor.substring(1) : htmlColor;
            
            // Поддерживаем форматы #RGB и #RRGGBB
            if (color.length() == 3) {
                // Расширяем #RGB до #RRGGBB
                color = String.valueOf(color.charAt(0)) + color.charAt(0) +
                       String.valueOf(color.charAt(1)) + color.charAt(1) +
                       String.valueOf(color.charAt(2)) + color.charAt(2);
            }
            
            if (color.length() == 6) {
                int red = Integer.parseInt(color.substring(0, 2), 16);
                int green = Integer.parseInt(color.substring(2, 4), 16);
                int blue = Integer.parseInt(color.substring(4, 6), 16);
                return Color.rgb(red, green, blue);
            }
            
            return Color.BLACK;
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    /**
     * Конвертирует строку в boolean значение
     * 
     * @param str строка для конвертации
     * @param defaultValue значение по умолчанию при ошибке
     * @return boolean значение
     */
    public static boolean stringToBoolean(String str, boolean defaultValue) {
        if (isEmpty(str)) {
            return defaultValue;
        }
        
        String lowerStr = str.toLowerCase().trim();
        return "true".equals(lowerStr) || "1".equals(lowerStr) || "yes".equals(lowerStr);
    }

    /**
     * Безопасно конвертирует строку в целое число
     * 
     * @param str строка для конвертации
     * @param defaultValue значение по умолчанию при ошибке
     * @return целое число или значение по умолчанию
     */
    public static int safeStringToInt(String str, int defaultValue) {
        if (isEmpty(str)) {
            return defaultValue;
        }
        
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Безопасно конвертирует строку в число с плавающей точкой
     * 
     * @param str строка для конвертации
     * @param defaultValue значение по умолчанию при ошибке
     * @return число с плавающей точкой или значение по умолчанию
     */
    public static double safeStringToDouble(String str, double defaultValue) {
        if (isEmpty(str)) {
            return defaultValue;
        }
        
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
