package com.example.oriedita_common.editor.service;

import com.example.oriedita_common.editor.canvas.animation.Interpolation;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface AnimationService {
    /**
     * Анимирует значение от текущего значения (определяется применением getter) к новому значению,
     * которое определяется применением calculateEndValue либо к текущему значению, если анимация с тем же ключом
     * не выполняется в данный момент; либо к конечному значению выполняющейся анимации с тем же ключом, если она существует.
     * Анимация выполняется в течение time секунд, и анимированное значение устанавливается с помощью setter
     * каждый раз при вызове update.
     * @param key идентификатор анимации, должен быть уникальным для каждого свойства
     * @param setter метод для установки значения анимируемого свойства во время анимации
     * @param getter метод для получения начального значения анимируемого свойства для определения конечного значения
     * @param calculateEndValue метод для определения конечного значения анимируемого свойства, вызывается с текущим значением
     *                          если анимация с тем же ключом не выполняется, или с конечным значением текущей анимации
     *                          с тем же ключом, если она выполняется
     * @param time продолжительность анимации в секундах
     * @param interpolation интерполяция для использования в анимации
     */
    void animate(String key, Consumer<Double> setter, Supplier<Double> getter, UnaryOperator<Double> calculateEndValue, double time, Interpolation interpolation);

    default void animate(String key, Consumer<Double> setter, Supplier<Double> getter, UnaryOperator<Double> calculateEndValue, double time) {
        animate(key, setter, getter, calculateEndValue, time, getDefaultInterpolation());
    }

    default void animate(String key, Consumer<Double> setter, Supplier<Double> getter, double endValue, double time, Interpolation interpolation) {
        animate(key, setter, getter, n -> endValue, time, interpolation);
    }

    default void animate(String key, Consumer<Double> setter, Supplier<Double> getter, double endValue, double time) {
        animate(key, setter, getter, n -> endValue, time);
    }

    Interpolation getDefaultInterpolation();

    /**
     * Анимирует точку от текущего положения к новому положению
     * 
     * @param key уникальная строка, идентифицирующая анимацию. анимации с тем же ключом будут перезаписывать друг друга
     * @param setter метод для установки анимируемого значения
     * @param getter метод для получения анимируемого значения
     * @param calculateEndPoint метод для вычисления значения, к которому должна анимироваться точка, на основе текущего значения (до анимации)
     * @param time время анимации
     * @param interpolation кривая интерполяции для использования в анимации
     */
    void animatePoint(String key, Consumer<Point> setter, Supplier<Point> getter, UnaryOperator<Point> calculateEndPoint, double time, Interpolation interpolation);

    default void animatePoint(String key, Consumer<Point> setter, Supplier<Point> getter, UnaryOperator<Point> calculateEndPoint, double time) {
        animatePoint(key, setter, getter, calculateEndPoint, time, getDefaultInterpolation());
    }

    default void animatePoint(String key, Consumer<Point> setter, Supplier<Point> getter, Point endPoint, double time, Interpolation interpolation) {
        animatePoint(key, setter, getter, p -> endPoint, time, interpolation);
    }

    default void animatePoint(String key, Consumer<Point> setter, Supplier<Point> getter, Point endPoint, double time) {
        animatePoint(key, setter, getter, endPoint, time, getDefaultInterpolation());
    }


    /**
     * Обновляет все выполняющиеся в данный момент анимации и устанавливает их соответствующие свойства в правильные значения
     */
    void update();

    /**
     * @return true если какая-либо анимация выполняется в данный момент
     */
    boolean isAnimating();
}
