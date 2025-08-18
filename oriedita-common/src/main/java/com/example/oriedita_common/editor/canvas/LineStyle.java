package com.example.oriedita_common.editor.canvas;

/**
 * Стили отображения линий: 1 = цветная, 2 = черно-белая, 3 = цветная с формой, 4 = черная пунктирная с одной точкой, 5 = черная пунктирная с двумя точками
 */
public enum LineStyle {
    /** Цветная линия */
    COLOR(1),
    /** Черно-белая линия */
    BLACK_WHITE(2),
    /** Цветная линия с формой */
    COLOR_AND_SHAPE(3),
    /** Черная пунктирная линия с одной точкой */
    BLACK_ONE_DOT(4),
    /** Черная пунктирная линия с двумя точками */
    BLACK_TWO_DOT(5),
    ;

    /** Числовой тип стиля линии */
    private final int type;

    /**
     * Конструктор стиля линии
     * @param type числовой тип стиля
     */
    LineStyle(int type) {
        this.type = type;
    }

    /**
     * Создает стиль линии из строкового представления
     * @param type строковое представление типа
     * @return стиль линии
     */
    public static LineStyle from(String type) {
        return from(Integer.parseInt(type));
    }

    /**
     * Создает стиль линии из числового типа
     * @param type числовой тип стиля
     * @return стиль линии
     * @throws IllegalArgumentException если тип не найден
     */
    public static LineStyle from(int type) {
        for (LineStyle ls : values()) {
            if (ls.type == type) {
                return ls;
            }
        }

        throw new IllegalArgumentException();
    }

    /**
     * Переходит к следующему стилю линии (циклически)
     * @return следующий стиль линии
     */
    public LineStyle advance() {
        return values()[(ordinal() + 1) % values().length];
    }

    /**
     * Возвращает строковое представление типа стиля
     * @return строковое представление типа
     */
    @Override
    public String toString() {
        return Integer.toString(type);
    }

    /**
     * Получает числовой тип стиля линии
     * @return числовой тип стиля
     */
    public int getType(){
        return this.type;
    }
}
