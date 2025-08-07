package com.example.oriedita_common.editor.drawing.tools;

import com.example.oriedita_core.origami.crease_pattern.elements.Circle;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;

import android.graphics.Matrix;
import android.graphics.Path;
import java.io.Serializable;

/**
 * Камера для преобразования координат между объектными и экранными координатами
 * Обеспечивает масштабирование, поворот, отражение и перемещение элементов
 */
public class Camera implements Serializable {

    /** Позиция камеры по X */
    double camera_position_x;
    /** Позиция камеры по Y */
    double camera_position_y;
    /** Угол поворота камеры в градусах */
    double camera_angle;
    /** Коэффициент отражения (-1.0 или 1.0, меняет знак только по оси X) */
    double camera_mirror;
    /** Масштаб по оси X */
    double camera_zoom_x;
    /** Масштаб по оси Y */
    double camera_zoom_y;
    /** Позиция отображения по X */
    double display_position_x;
    /** Позиция отображения по Y */
    double display_position_y;

    /** Родительская камера для иерархических преобразований */
    Camera parent;

    /** Константа для преобразования градусов в радианы */
    double do2rad = 3.14159265 / 180.0;
    /** Угол камеры в радианах */
    double camera_rad;
    /** Синус угла камеры */
    double sin_rad;
    /** Косинус угла камеры */
    double cos_rad;

    /**
     * Конструктор камеры с настройками по умолчанию
     */
    public Camera() {
        camera_position_x = 0.0;
        camera_position_y = 0.0;
        camera_angle = 0.0;
        camera_rad = camera_angle * do2rad;
        sin_rad = Math.sin(camera_rad);
        cos_rad = Math.cos(camera_rad);
        camera_mirror = 1.0;       // Без отражения
        camera_zoom_x = 1.0;
        camera_zoom_y = 1.0;

        display_position_x = 350.0;
        display_position_y = 350.0;
    }

    /**
     * Сбрасывает настройки камеры к значениям по умолчанию
     */
    public void reset() {
        camera_position_x = 0.0;
        camera_position_y = 0.0;
        camera_angle = 0.0;
        camera_rad = camera_angle * do2rad;
        sin_rad = Math.sin(camera_rad);
        cos_rad = Math.cos(camera_rad);
        camera_mirror = 1.0;       // Без отражения
        camera_zoom_x = 1.0;
        camera_zoom_y = 1.0;
        parent = null;

        display_position_x = 350.0;
        display_position_y = 350.0;
    }

    /**
     * Получает родительскую камеру
     * @return родительская камера или null
     */
    public Camera getParent() {
        return parent;
    }

    /**
     * Устанавливает родительскую камеру
     * @param parent родительская камера
     */
    public void setParent(Camera parent) {
        this.parent = parent;
    }

    /**
     * Умножает масштаб по оси X на указанное значение
     * @param d множитель масштаба
     */
    public void multiplyCameraZoomX(double d) {
        camera_zoom_x = d * camera_zoom_x;
    }

    /**
     * Умножает масштаб по оси Y на указанное значение
     * @param d множитель масштаба
     */
    public void multiplyCameraZoomY(double d) {
        camera_zoom_y = d * camera_zoom_y;
    }

    /**
     * Копирует настройки из другой камеры
     * @param c0 камера, настройки которой нужно скопировать
     */
    public void setCamera(Camera c0) {
        double d_camera_position_x = c0.getCameraPositionX();
        double d_camera_position_y = c0.getCameraPositionY();
        double d_camera_angle = c0.getCameraAngle();
        double d_camera_mirror = c0.getCameraMirror();
        double d_camera_zoom_x = c0.getCameraZoomX();
        double d_camera_zoom_y = c0.getCameraZoomY();

        double d_display_position_x = c0.getDisplayPositionX();
        double d_display_position_y = c0.getDisplayPositionY();

        setCameraPositionX(d_camera_position_x);
        setCameraPositionY(d_camera_position_y);
        setCameraAngle(d_camera_angle);
        setCameraMirror(d_camera_mirror);
        setCameraZoomX(d_camera_zoom_x);
        setCameraZoomY(d_camera_zoom_y);
        setParent(c0.getParent());

        setDisplayPositionX(d_display_position_x);
        setDisplayPositionY(d_display_position_y);
    }

    /**
     * Получает позицию камеры по оси X
     * @return позиция по X
     */
    public double getCameraPositionX() {
        return camera_position_x;
    }

    /**
     * Устанавливает позицию камеры по оси X
     * @param d позиция по X
     */
    public void setCameraPositionX(double d) {
        camera_position_x = d;
    }

    /**
     * Получает позицию камеры по оси Y
     * @return позиция по Y
     */
    public double getCameraPositionY() {
        return camera_position_y;
    }

    /**
     * Устанавливает позицию камеры по оси Y
     * @param d позиция по Y
     */
    public void setCameraPositionY(double d) {
        camera_position_y = d;
    }

    /**
     * Получает угол поворота камеры в градусах
     * @return угол в градусах
     */
    public double getCameraAngle() {
        return camera_angle;
    }

    /**
     * Устанавливает угол поворота камеры в градусах
     * @param d угол в градусах
     */
    public void setCameraAngle(double d) {
        camera_angle = d;
        camera_rad = camera_angle * do2rad;
        sin_rad = Math.sin(camera_rad);
        cos_rad = Math.cos(camera_rad);
    }

    /**
     * Получает коэффициент отражения камеры
     * @return коэффициент отражения (-1.0 или 1.0)
     */
    public double getCameraMirror() {
        return camera_mirror;
    }

    /**
     * Устанавливает коэффициент отражения камеры
     * @param d коэффициент отражения (-1.0 или 1.0)
     */
    public void setCameraMirror(double d) {
        camera_mirror = d;
    }

    /**
     * Определяет, отражена ли камера
     * @return true если камера отражена
     */
    public boolean determineIsCameraMirrored() {
        return camera_mirror == -1.0;
    }

    /**
     * Получает масштаб камеры по оси X
     * @return масштаб по X
     */
    public double getCameraZoomX() {
        return camera_zoom_x;
    }

    /**
     * Устанавливает масштаб камеры по оси X
     * @param d масштаб по X
     */
    public void setCameraZoomX(double d) {
        camera_zoom_x = d;
    }

    /**
     * Получает масштаб камеры по оси Y
     * @return масштаб по Y
     */
    public double getCameraZoomY() {
        return camera_zoom_y;
    }

    /**
     * Устанавливает масштаб камеры по оси Y
     * @param d масштаб по Y
     */
    public void setCameraZoomY(double d) {
        camera_zoom_y = d;
    }

    /**
     * Получает позицию отображения по оси X
     * @return позиция отображения по X
     */
    public double getDisplayPositionX() {
        return display_position_x;
    }

    /**
     * Устанавливает позицию отображения по оси X
     * @param d позиция отображения по X
     */
    public void setDisplayPositionX(double d) {
        display_position_x = d;
    }

    /**
     * Получает позицию отображения по оси Y
     * @return позиция отображения по Y
     */
    public double getDisplayPositionY() {
        return display_position_y;
    }

    /**
     * Устанавливает позицию отображения по оси Y
     * @param d позиция отображения по Y
     */
    public void setDisplayPositionY(double d) {
        display_position_y = d;
    }

    /**
     * Устанавливает позицию отображения из точки
     * @param p точка с координатами позиции отображения
     */
    public void setDisplayPosition(Point p) {
        setDisplayPositionX(p.getX());
        setDisplayPositionY(p.getY());
    }

    /**
     * Получает позицию камеры как точку
     * @return точка с координатами позиции камеры
     */
    public Point getCameraPosition() {
        return new Point(camera_position_x, camera_position_y);
    }

    /**
     * Устанавливает позицию камеры из точки
     * @param p точка с координатами позиции камеры
     */
    public void setCameraPosition(Point p) {
        setCameraPositionX(p.getX());
        setCameraPositionY(p.getY());
    }

    /**
     * Преобразует точку из объектных координат в экранные координаты
     * @param t_ob точка в объектных координатах
     * @return точка в экранных координатах
     */
    public Point object2TV(Point t_ob) {
        double x1 = t_ob.getX() - camera_position_x;
        double y1 = t_ob.getY() - camera_position_y;
        double x2 = cos_rad * x1 + sin_rad * y1;
        double y2 = -sin_rad * x1 + cos_rad * y1;

        x2 = x2 * camera_mirror;       // Отражение
        x2 = x2 * camera_zoom_x;
        y2 = y2 * camera_zoom_y;
        Point t_tv = new Point(x2 + display_position_x, y2 + display_position_y);
        if (parent != null) {
            t_tv = parent.object2TV(t_tv);
        }
        return t_tv;
    }

    /**
     * Преобразует отрезок из объектных координат в экранные координаты
     * @param s_ob отрезок в объектных координатах
     * @return отрезок в экранных координатах
     */
    public LineSegment object2TV(LineSegment s_ob) {
        return s_ob
                .withA(object2TV(s_ob.getA()))
                .withB(object2TV(s_ob.getB()));
    }

    /**
     * Преобразует путь из объектных координат в экранные координаты
     * @param path_ob путь в объектных координатах
     * @return путь в экранных координатах
     */
    public Path object2TV(Path path_ob) {
        Matrix transform = new Matrix();
        transform.postTranslate((float)display_position_x, (float)display_position_y);
        transform.postRotate((float)(-camera_angle));
        transform.postScale((float)getCameraZoomX(), (float)getCameraZoomY());
        transform.postTranslate((float)(-camera_position_x), (float)(-camera_position_y));
        
        Path result = new Path();
        result.addPath(path_ob, transform);
        return result;
    }

    /**
     * Преобразует окружность из объектных координат в экранные координаты
     * @param s_ob окружность в объектных координатах
     * @return окружность в экранных координатах
     */
    public Circle object2TV(Circle s_ob) {
        Point p_ob = s_ob.determineCenter();
        Point p_tv = object2TV(p_ob);
        return new Circle(p_tv, s_ob.getR() * camera_zoom_x, s_ob.getColor());
    }

    /**
     * Преобразует точку из экранных координат в объектные координаты
     * @param t_tv точка в экранных координатах
     * @return точка в объектных координатах
     */
    public Point TV2object(Point t_tv) {
        if (parent != null) {
            t_tv = parent.TV2object(t_tv);
        }
        double x1, y1;
        double x2, y2;
        x1 = t_tv.getX();
        y1 = t_tv.getY();
        x1 = x1 - display_position_x;
        y1 = y1 - display_position_y;
        x1 = x1 / camera_zoom_x;
        y1 = y1 / camera_zoom_y;

        x1 = x1 * camera_mirror;       // Отражение

        x2 = cos_rad * x1 - sin_rad * y1;
        y2 = sin_rad * x1 + cos_rad * y1;

        return new Point(x2 + camera_position_x, y2 + camera_position_y);
    }

    /**
     * Преобразует отрезок из экранных координат в объектные координаты
     * @param s_tv отрезок в экранных координатах
     * @return отрезок в объектных координатах
     */
    public LineSegment TV2object(LineSegment s_tv) {
        return s_tv
                .withA(TV2object(s_tv.getA()))
                .withB(TV2object(s_tv.getB()));
    }

    /**
     * Перемещает позицию отображения на указанное смещение
     * @param tuika смещение для перемещения позиции отображения
     */
    public void displayPositionMove(Point tuika) {
        if (parent != null) {
            Point origin = parent.TV2object(new Point(0,0));
            Point delta_tr = parent.TV2object(tuika);
            tuika = origin.delta(delta_tr);
        }
        display_position_x = display_position_x + tuika.getX();
        display_position_y = display_position_y + tuika.getY();
    }

    /**
     * Устанавливает позицию камеры так, чтобы объект в указанной точке экрана
     * отображался в центре, не изменяя отображение на экране
     * @param p точка на экране, к которой нужно привязать позицию камеры
     */
    public void camera_position_specify_from_TV(Point p) {
        if (parent != null) {
            setCameraPosition(TV2object(p));
            setDisplayPosition(parent.TV2object(p));
        } else {
            setCameraPosition(TV2object(p));
            setDisplayPosition(p);
        }
    }

    /**
     * Получает позицию отображения как точку
     * @return точка с координатами позиции отображения
     */
    public Point getDisplayPosition() {
        return new Point(display_position_x, display_position_y);
    }
}
