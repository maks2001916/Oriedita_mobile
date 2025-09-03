package com.example.oriedita_data.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.util.Log;

import com.example.oriedita_data.databinding.ApplicationModel;
import com.example.oriedita_data.databinding.GridModel;
import com.example.oriedita_common.editor.drawing.tools.Camera;
import com.example.oriedita_core.origami.Epsilon;
import com.example.oriedita_core.origami.crease_pattern.OritaCalc;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;

/**
 * Grid - класс для отрисовки координатной сетки в Android приложении Oriedita
 * 
 * Этот класс отвечает за отрисовку координатной сетки на экране устройства.
 * Поддерживает различные типы сеток: прямоугольную, диагональную,
 * с изменением цвета линий и масштабированием.
 */
public class Grid {
    
    private static final String TAG = "Grid";

    // Константы для оптимизации
    private static final double DEFAULT_GRID_WIDTH = 200.0;
    private static final double PAPER_SIZE = 400.0;
    private static final double PAPER_BOUNDARY = 200.0;
    /**
     * Горизонтальная длина единичного вектора сетки (a)
     * Определяет растяжение сетки по горизонтали
     */
    double aGridLength = 1.0;

    // Ширина базовой ячейки сетки в пикселях
    private double gridWidth = DEFAULT_GRID_WIDTH;


    /**
     * Вертикальная длина единичного вектора сетки (b)
     * Определяет растяжение сетки по вертикали
     */
    double bGridLength = 1.0;
    
    double gridAngle = -90.0; // Угол поворота сетки

    // Коэффициенты X компонента горизонтального единичного вектора сетки
    double d_grid_ax = 1.0;
    // Коэффициенты Y компонента горизонтального единичного вектора сетки
    double d_grid_ay = 0.0;

    // Коэффициенты X компонента вертикального единичного вектора сетки
    double d_grid_bx = 0.0;
    // Коэффициенты Y компонента вертикального единичного вектора сетки
    double d_grid_by = 1.0;

    // Коэффициенты для диагонального единичного вектора (разность векторов a и b)
    double d_grid_cx = d_grid_bx - d_grid_ax;
    double d_grid_cy = d_grid_by - d_grid_ay;

    /**
     * Координаты начала сетки в объектной системе координат
     * okx0 - X координата начала сетки
     * oky0 - Y координата начала сетки
     */
    double okx0 = -200.0;
    double oky0 = 200.0;

    /**
     * Диагонали ячейки сетки для оптимизации поиска ближайших точек
     * diagonal_max - большая диагональ ячейки
     * diagonal_min - малая диагональ ячейки
     */
    double diagonal_max = 1.0;
    double diagonal_min = 1.0;

    // Базовое состояние сетки: WITHIN_PAPER - только внутри бумаги, FULL - везде, HIDDEN - скрыта
    GridModel.State baseState = GridModel.State.WITHIN_PAPER;

    // Размер сетки: 1 - без разделения, 2 - 2x2, 4 - 4x4
    int gridSize = 2;

    // Интервалы и позиции для масштабных линий
    int horizontalScaleInterval = 5;
    int horizontalScalePosition = 0;

    int verticalScaleInterval = 5;
    int verticalScalePosition = 0;

    // Цвета сетки (Android int цвета)
    int grid_color;        // Цвет линий сетки
    int gridScaleColor;    // Цвет масштабных линий сетки

    int gridLineWidth = 1; // Ширина линий сетки

    boolean drawDiagonalGridlines = false; // Флаг отрисовки диагональных линий

    /**
     * Конструктор по умолчанию
     * Инициализирует сетку с базовыми параметрами
     */
    public Grid() {
    }

    /**
     * Устанавливает интервал горизонтальных масштабных линий
     * @param i интервал
     */
    public void setHorizontalScaleInterval(int i) {
        horizontalScaleInterval = i;
        // Сброс позиции, если она выходит за пределы нового интервала
        if (horizontalScalePosition >= horizontalScaleInterval) {
            horizontalScalePosition = 0;
        }
    }

    /**
     * Устанавливает интервал вертикальных масштабных линий
     * @param i интервал
     */
    public void setVerticalScaleInterval(int i) {
        verticalScaleInterval = i;
        // Сброс позиции, если она выходит за пределы нового интервала
        if (verticalScalePosition >= verticalScaleInterval) {
            verticalScalePosition = 0;
        }
    }

    /**
     * Устанавливает ширину линий сетки
     * @param i0 ширина линии
     */
    public void setGridLineWidth(int i0) {
        gridLineWidth = i0;
    }

    /**
     * Устанавливает позицию горизонтальных масштабных линий
     * @param i0 позиция
     */
    public void setHorizontalScalePosition(int i0) {
        horizontalScalePosition = i0;
    }

    /**
     * Устанавливает позицию вертикальных масштабных линий
     * @param i0 позиция
     */
    public void setVerticalScalePosition(int i0) {
        verticalScalePosition = i0;
    }

    /**
     * Устанавливает размер сетки
     * @param i размер сетки
     */
    public void setGridSize(int i) {
        gridSize = i;
        // Пересчет ширины ячейки в зависимости от размера сетки
        gridWidth = PAPER_SIZE / (double) gridSize;

        calculateGrid();
    }

    /**
     * Возвращает ширину сетки
     * @return ширина сетки
     */
    public double getGridWidth() {
        return gridWidth;
    }

    /**
     * Возвращает размер сетки
     * @return размер сетки
     */
    public int getGridSize() {
        return gridSize;
    }

    /**
     * Устанавливает параметры сетки
     * @param dkxn горизонтальная длина
     * @param dkyn вертикальная длина
     * @param dkk угол поворота
     */
    public void setGrid(double dkxn, double dkyn, double dkk) {
        aGridLength = dkxn;
        bGridLength = dkyn;
        gridAngle = -dkk; // Инвертируем угол для правильного направления

        calculateGrid();
    }

    /**
     * Вычисляет параметры сетки на основе текущих настроек
     */
    public void calculateGrid() {
        // Вычисление коэффициентов горизонтального вектора
        d_grid_ax = gridWidth * aGridLength;
        d_grid_ay = gridWidth * 0.0;

        // Вычисление коэффициентов вертикального вектора с учетом поворота
        double d_rad = (Math.PI / 180) * gridAngle;
        d_grid_bx = gridWidth * bGridLength * Math.cos(d_rad);
        d_grid_by = gridWidth * bGridLength * Math.sin(d_rad);

        // Вычисление диагонального вектора
        d_grid_cx = d_grid_bx - d_grid_ax;
        d_grid_cy = d_grid_by - d_grid_ay;

        // Вычисление диагоналей ячейки для оптимизации
        diagonal_max = OritaCalc.distance(new Point(0.0, 0.0), new Point(d_grid_ax + d_grid_bx, d_grid_ay + d_grid_by));
        diagonal_min = OritaCalc.distance(new Point(d_grid_ax, d_grid_ay), new Point(d_grid_bx, d_grid_by));
        
        // Определение большей и меньшей диагонали
        if (diagonal_max < diagonal_min) {
            diagonal_min = OritaCalc.distance(new Point(0.0, 0.0), new Point(d_grid_ax + d_grid_bx, d_grid_ay + d_grid_by));
            diagonal_max = OritaCalc.distance(new Point(d_grid_ax, d_grid_ay), new Point(d_grid_bx, d_grid_by));
        }

        resetGrid();
    }

    /**
     * Сбрасывает состояние сетки в зависимости от параметров
     * Если сетка нестандартная, переключается в режим FULL
     */
    private void resetGrid() {
        if (baseState == GridModel.State.WITHIN_PAPER) {
            if (Math.abs(aGridLength - 1.0) > Epsilon.UNKNOWN_1EN6) {
                setBaseState(GridModel.State.FULL);
            }
            if (Math.abs(bGridLength - 1.0) > Epsilon.UNKNOWN_1EN6) {
                setBaseState(GridModel.State.FULL);
            }
            if (Math.abs(gridAngle - (-90.0)) > Epsilon.UNKNOWN_1EN6) {
                setBaseState(GridModel.State.FULL);
            }
        }
    }

    /**
     * Возвращает базовое состояние сетки
     * @return состояние сетки
     */
    public GridModel.State getBaseState() {
        return baseState;
    }

    /**
     * Устанавливает базовое состояние сетки
     * @param i состояние сетки
     */
    public void setBaseState(GridModel.State i) {
        baseState = i;
        resetGrid();
    }

    /**
     * Получает индекс сетки из координат точки в объектной системе
     * @param t0 точка в объектных координатах
     * @return индекс сетки
     */
    public Point getPosition(Point t0) {
        // Матрица [d_grid_ax, d_grid_bx] преобразует [1] в вектор решетки a и [1] в вектор решетки b.
        // [d_grid_ay, d_grid_by] [0] [0]
        // С помощью обратной матрицы координаты точки в объектной системе преобразуются в индекс решетки.
        
        // Определение символов матрицы
        double ax = d_grid_ax;
        double ay = d_grid_ay;
        double bx = d_grid_bx;
        double by = d_grid_by;

        // Определение символов обратной матрицы
        double det = ax * by - bx * ay;
        double gax = by / det;
        double gay = -ay / det;
        double gbx = -bx / det;
        double gby = ax / det;

        // Смещение относительно начала сетки
        double kx = t0.getX() - okx0;
        double ky = t0.getY() - oky0;

        // Применение обратного преобразования
        double index_x = gax * kx + gbx * ky;
        double index_y = gay * kx + gby * ky;

        return new Point(index_x, index_y);
    }

    /**
     * Находит минимальный индекс a для четырех точек
     * Используется для определения границ области отрисовки
     * @param p_a точка A
     * @param p_b точка B
     * @param p_c точка C
     * @param p_d точка D
     * @return минимальный индекс a
     */
    private int get_a_index_min(Point p_a, Point p_b, Point p_c, Point p_d) {
        Point p_a_index = getPosition(p_a);
        Point p_b_index = getPosition(p_b);
        Point p_c_index = getPosition(p_c);
        Point p_d_index = getPosition(p_d);

        double a_index_min = p_a_index.getX();
        if (p_b_index.getX() < a_index_min) {
            a_index_min = p_b_index.getX();
        }
        if (p_c_index.getX() < a_index_min) {
            a_index_min = p_c_index.getX();
        }
        if (p_d_index.getX() < a_index_min) {
            a_index_min = p_d_index.getX();
        }

        return (int) Math.floor(a_index_min);
    }

    /**
     * Находит максимальный индекс a для четырех точек
     * @param p_a точка A
     * @param p_b точка B
     * @param p_c точка C
     * @param p_d точка D
     * @return максимальный индекс a
     */
    private int get_a_index_max(Point p_a, Point p_b, Point p_c, Point p_d) {
        Point p_a_index = getPosition(p_a);
        Point p_b_index = getPosition(p_b);
        Point p_c_index = getPosition(p_c);
        Point p_d_index = getPosition(p_d);

        double a_index_max = p_a_index.getX();
        if (p_b_index.getX() > a_index_max) {
            a_index_max = p_b_index.getX();
        }
        if (p_c_index.getX() > a_index_max) {
            a_index_max = p_c_index.getX();
        }
        if (p_d_index.getX() > a_index_max) {
            a_index_max = p_d_index.getX();
        }

        return (int) Math.ceil(a_index_max);
    }

    /**
     * Находит минимальный индекс b для четырех точек
     * @param p_a точка A
     * @param p_b точка B
     * @param p_c точка C
     * @param p_d точка D
     * @return минимальный индекс b
     */
    private int get_b_index_min(Point p_a, Point p_b, Point p_c, Point p_d) {
        Point p_a_index = getPosition(p_a);
        Point p_b_index = getPosition(p_b);
        Point p_c_index = getPosition(p_c);
        Point p_d_index = getPosition(p_d);

        double b_index_min = p_a_index.getY();
        if (p_b_index.getY() < b_index_min) {
            b_index_min = p_b_index.getY();
        }
        if (p_c_index.getY() < b_index_min) {
            b_index_min = p_c_index.getY();
        }
        if (p_d_index.getY() < b_index_min) {
            b_index_min = p_d_index.getY();
        }

        return (int) Math.floor(b_index_min);
    }

    /**
     * Находит максимальный индекс b для четырех точек
     * @param p_a точка A
     * @param p_b точка B
     * @param p_c точка C
     * @param p_d точка D
     * @return максимальный индекс b
     */
    private int get_b_index_max(Point p_a, Point p_b, Point p_c, Point p_d) {
        Point p_a_index = getPosition(p_a);
        Point p_b_index = getPosition(p_b);
        Point p_c_index = getPosition(p_c);
        Point p_d_index = getPosition(p_d);

        double b_index_max = p_a_index.getY();
        if (p_b_index.getY() > b_index_max) {
            b_index_max = p_b_index.getY();
        }
        if (p_c_index.getY() > b_index_max) {
            b_index_max = p_c_index.getY();
        }
        if (p_d_index.getY() > b_index_max) {
            b_index_max = p_d_index.getY();
        }

        return (int) Math.ceil(b_index_max);
    }

    /**
     * Устанавливает цвет линий сетки
     * @param color0 цвет
     */
    public void setGridColor(int color0) {
        grid_color = color0;
    }

    /**
     * Устанавливает цвет масштабных линий сетки
     * @param color0 цвет
     */
    public void setGridScaleColor(int color0) {
        gridScaleColor = color0;
    }

    /**
     * Основной метод отрисовки сетки на Android Canvas
     * 
     * @param canvas Android Canvas для отрисовки
     * @param paint Android Paint для настройки стиля линий
     * @param camera камера для преобразования координат между системами
     * @param p0x_max максимальная X координата экрана в пикселях
     * @param p0y_max максимальная Y координата экрана в пикселях
     * @param colorChange флаг изменения цвета линий через интервалы
     * @param minGridUnitSize минимальный размер единицы сетки для адаптивного масштабирования
     */
    public void draw(Canvas canvas, Paint paint, Camera camera, int p0x_max, int p0y_max, boolean colorChange, double minGridUnitSize) {
        Log.d(TAG, "Начинаем отрисовку сетки");
        
        // Проверка необходимости отрисовки сетки
        if (!shouldDrawGrid(camera)) {
            Log.d(TAG, "Сетка не отрисовывается - слишком мелкий масштаб");
            return;
        }
        
        // Оптимизация настроек Paint для сетки
        optimizePaintForGrid(paint);
        
        // Настройка стиля линий для Android
        paint.setStrokeWidth((float) gridLineWidth);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeJoin(Paint.Join.MITER);

        // Определение координат углов экрана в TV системе (экранные координаты)
        Point p0_a = new Point(0, 0);           // Левый верхний угол экрана
        Point p0_b = new Point(0, p0y_max);     // Левый нижний угол экрана
        Point p0_c = new Point(p0x_max, p0y_max); // Правый нижний угол экрана
        Point p0_d = new Point(p0x_max, 0);     // Правый верхний угол экрана

        // Преобразование в объектные координаты
        Point p_a = camera.TV2object(p0_a); // Объектные координаты левого верхнего угла экрана
        Point p_b = camera.TV2object(p0_b); // Объектные координаты левого нижнего угла экрана
        Point p_c = camera.TV2object(p0_c); // Объектные координаты правого нижнего угла экрана
        Point p_d = camera.TV2object(p0_d); // Объектные координаты правого верхнего угла экрана

        // Определение границ области отрисовки сетки
        int grid_screen_a_max = get_a_index_max(p_a, p_b, p_c, p_d);
        int grid_screen_a_min = get_a_index_min(p_a, p_b, p_c, p_d);
        int grid_screen_b_max = get_b_index_max(p_a, p_b, p_c, p_d);
        int grid_screen_b_min = get_b_index_min(p_a, p_b, p_c, p_d);

        // Ограничение сетки в пределах бумаги
        if (baseState == GridModel.State.WITHIN_PAPER) {
            int grid_yousi_x_max = getGridSize();
            int grid_yousi_x_min = 0;
            int grid_yousi_y_max = getGridSize();
            int grid_yousi_y_min = 0;

            if (grid_screen_a_max > grid_yousi_x_max) {
                grid_screen_a_max = grid_yousi_x_max;
            }
            if (grid_screen_a_min < grid_yousi_x_min) {
                grid_screen_a_min = grid_yousi_x_min;
            }
            if (grid_screen_b_max > grid_yousi_y_max) {
                grid_screen_b_max = grid_yousi_y_max;
            }
            if (grid_screen_b_min < grid_yousi_y_min) {
                grid_screen_b_min = grid_yousi_y_min;
            }
        }

        // Настройка основного цвета сетки
        paint.setColor(grid_color);
        LineSegment s_tv;
        LineSegment s_ob;
        
        // Вычисление адаптивного шага сетки в зависимости от масштаба
        int step = 1;
        s_ob = new LineSegment(d_grid_ax * 0,
                d_grid_ay * 0,
                d_grid_ax * 1,
                d_grid_ay * 1);
        s_tv = camera.object2TV(s_ob);
        
        // Увеличение шага, если линии сетки слишком близко друг к другу
        while (s_tv.determineLength()*step < minGridUnitSize){
            step *= 2;
        }
        int halfStep = Math.max(1, step/2);
        int adjustedGridSize = gridSize*step;
        
        // Вычисление смещений для выравнивания сетки
        int offsetX = grid_screen_a_min % adjustedGridSize;
        int offsetY = grid_screen_b_min % adjustedGridSize;
        if (offsetX < 0) {
            offsetX += adjustedGridSize;
        }
        if (offsetY < 0) {
            offsetY += adjustedGridSize;
        }
        
        // Вычисление прозрачности для альтернативных линий
        int alpha = Math.min(255, (int) ( (1-minGridUnitSize/(s_tv.determineLength()*step)) * 255 * 2));

        // Создаем цвет с прозрачностью для альтернативных линий
        int c = Color.argb(alpha, Color.red(grid_color), Color.green(grid_color), Color.blue(grid_color));
        
        // Отрисовка вертикальных линий сетки
        for (int i = grid_screen_a_min - offsetX; i <= grid_screen_a_max; i+= halfStep) {
            s_ob = new LineSegment(d_grid_ax * i + d_grid_bx * grid_screen_b_min + okx0,
                    d_grid_ay * i + d_grid_by * grid_screen_b_min + oky0,
                    d_grid_ax * i + d_grid_bx * grid_screen_b_max + okx0,
                    d_grid_ay * i + d_grid_by * grid_screen_b_max + oky0);

            s_tv = camera.object2TV(s_ob);
            
            // Применение альтернативного цвета для четных линий
            if (step != halfStep && i/halfStep % 2 != 0) {
                paint.setColor(c);
            } else {
                paint.setColor(grid_color);
            }
            
            // Отрисовка линии на Android Canvas
            canvas.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), 
                          (int) s_tv.determineBX(), (int) s_tv.determineBY(), paint);
        }
        
        paint.setColor(grid_color);
        
        // Отрисовка горизонтальных линий сетки
        for (int i = grid_screen_b_min - offsetY; i <= grid_screen_b_max; i+=halfStep) {
            s_ob = new LineSegment(d_grid_ax * grid_screen_a_min + d_grid_bx * i + okx0,
                    d_grid_ay * grid_screen_a_min + d_grid_by * i + oky0,
                    d_grid_ax * grid_screen_a_max + d_grid_bx * i + okx0,
                    d_grid_ay * grid_screen_a_max + d_grid_by * i + oky0);

            s_tv = camera.object2TV(s_ob);
            
            // Применение альтернативного цвета для четных линий
            if (step != halfStep && i/halfStep % 2 != 0) {
                paint.setColor(c);
            } else {
                paint.setColor(grid_color);
            }

            // Отрисовка линии на Android Canvas
            canvas.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), 
                          (int) s_tv.determineBX(), (int) s_tv.determineBY(), paint);
        }

        // Отрисовка диагональных линий сетки
        if (drawDiagonalGridlines) {
            // Первые диагональные линии: \.. <- ((ax+cx)*i
            // \\.
            // \\\ <- (ax*i+bx*bmin, ay*i+by*bmin)
            for (int i = grid_screen_a_min - (offsetX+offsetY); i <= grid_screen_a_max; i+= step) {
                s_ob = new LineSegment(
                        i * d_grid_ax + grid_screen_b_min * d_grid_bx + okx0,
                        i * d_grid_ay + grid_screen_b_min * d_grid_by + oky0,
                        i * d_grid_ax + grid_screen_b_min * d_grid_bx + okx0 + (i - grid_screen_a_min) * d_grid_cx,
                        i * d_grid_ay + grid_screen_b_min * d_grid_by + oky0 + (i - grid_screen_a_min) * d_grid_cy
                );

                if (step != halfStep && i/halfStep % 2 != 0) {
                    paint.setColor(c);
                } else {
                    paint.setColor(grid_color);
                }

                s_tv = camera.object2TV(s_ob);
                canvas.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), 
                              (int) s_tv.determineBX(), (int) s_tv.determineBY(), paint);
            }
            
            // Вторые диагональные линии: .\\
            // ..\
            // ...
            for (int i = grid_screen_b_min - (offsetX+offsetY); i <= grid_screen_b_max; i+= step) {
                s_ob = new LineSegment(
                        grid_screen_a_max * d_grid_ax + i * d_grid_bx + okx0,
                        grid_screen_a_max * d_grid_ay + i * d_grid_by + oky0,
                        grid_screen_a_max * d_grid_ax + i * d_grid_bx + okx0 + (grid_screen_b_max - i) * d_grid_cx,
                        grid_screen_a_max * d_grid_ay + i * d_grid_by + oky0 + (grid_screen_b_max - i) * d_grid_cy
                );

                if (step != halfStep && i/halfStep % 2 != 0) {
                    paint.setColor(c);
                } else {
                    paint.setColor(grid_color);
                }

                s_tv = camera.object2TV(s_ob);
                canvas.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), 
                              (int) s_tv.determineBX(), (int) s_tv.determineBY(), paint);
            }
        }

        // Изменение цвета линий сетки через определенные интервалы
        if (colorChange) {
            paint.setColor(gridScaleColor);
            int gsc = Color.argb(alpha, Color.red(gridScaleColor), Color.green(gridScaleColor), Color.blue(gridScaleColor));

            int i_balance; // Остаток

            // Вертикальные масштабные линии
            for (int i = grid_screen_a_min - offsetX; i <= grid_screen_a_max; i+= halfStep) {
                i_balance = i % verticalScaleInterval;
                if (i_balance < 0) {
                    i_balance = i_balance + verticalScaleInterval;
                }
                if (i_balance == verticalScalePosition) {
                    s_ob = new LineSegment(d_grid_ax * i + d_grid_bx * grid_screen_b_min + okx0,
                            d_grid_ay * i + d_grid_by * grid_screen_b_min + oky0,
                            d_grid_ax * i + d_grid_bx * grid_screen_b_max + okx0,
                            d_grid_ay * i + d_grid_by * grid_screen_b_max + oky0);
                    if (step != halfStep && i/halfStep % 2 != 0) {
                        paint.setColor(gsc);
                    } else {
                        paint.setColor(gridScaleColor);
                    }
                    s_tv = camera.object2TV(s_ob);
                    canvas.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), 
                                  (int) s_tv.determineBX(), (int) s_tv.determineBY(), paint);
                }
            }

            // Горизонтальные масштабные линии
            for (int i = grid_screen_b_min - offsetY; i <= grid_screen_b_max; i+= halfStep) {
                i_balance = i % horizontalScaleInterval;
                if (i_balance < 0) {
                    i_balance = i_balance + horizontalScaleInterval;
                }

                if (i_balance == horizontalScalePosition) {
                    s_ob = new LineSegment(d_grid_ax * grid_screen_a_min + d_grid_bx * i + okx0,
                            d_grid_ay * grid_screen_a_min + d_grid_by * i + oky0,
                            d_grid_ax * grid_screen_a_max + d_grid_bx * i + okx0,
                            d_grid_ay * grid_screen_a_max + d_grid_by * i + oky0);

                    if (step != halfStep && i/halfStep % 2 != 0) {
                        paint.setColor(gsc);
                    } else {
                        paint.setColor(gridScaleColor);
                    }

                    s_tv = camera.object2TV(s_ob);
                    canvas.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), 
                                  (int) s_tv.determineBX(), (int) s_tv.determineBY(), paint);
                }
            }

            // Диагональные масштабные линии
            if (drawDiagonalGridlines) {
                // \\\4
                // \\\
                // \\\
                // 012
                // Первые диагональные масштабные линии
                for (int i = grid_screen_a_min- (offsetX+offsetY); i <= grid_screen_a_max; i+= step) {
                    if ((i + grid_screen_b_min) % horizontalScaleInterval != 0) {
                        continue;
                    }
                    if (step != halfStep && i/halfStep % 2 != 0) {
                        paint.setColor(gsc);
                    } else {
                        paint.setColor(gridScaleColor);
                    }

                    s_ob = new LineSegment(
                            i * d_grid_ax + grid_screen_b_min * d_grid_bx + okx0,
                            i * d_grid_ay + grid_screen_b_min * d_grid_by + oky0,
                            i * d_grid_ax + grid_screen_b_min * d_grid_bx + okx0 + (i - grid_screen_a_min) * d_grid_cx,
                            i * d_grid_ay + grid_screen_b_min * d_grid_by + oky0 + (i - grid_screen_a_min) * d_grid_cy
                    );
                    s_tv = camera.object2TV(s_ob);
                    canvas.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), 
                                  (int) s_tv.determineBX(), (int) s_tv.determineBY(), paint);
                }
                
                // Вторые диагональные масштабные линии
                for (int i = grid_screen_b_min-(offsetX+offsetY); i <= grid_screen_b_max; i+= halfStep) {
                    if ((i + grid_screen_a_max) % horizontalScaleInterval != 0) {
                        continue;
                    }
                    if (step != halfStep && i/halfStep % 2 != 0) {
                        paint.setColor(gsc);
                    } else {
                        paint.setColor(gridScaleColor);
                    }
                    s_ob = new LineSegment(
                            grid_screen_a_max * d_grid_ax + i * d_grid_bx + okx0,
                            grid_screen_a_max * d_grid_ay + i * d_grid_by + oky0,
                            grid_screen_a_max * d_grid_ax + i * d_grid_bx + okx0 + (grid_screen_b_max - i) * d_grid_cx,
                            grid_screen_a_max * d_grid_ay + i * d_grid_by + oky0 + (grid_screen_b_max - i) * d_grid_cy
                    );

                    s_tv = camera.object2TV(s_ob);
                    canvas.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), 
                                  (int) s_tv.determineBX(), (int) s_tv.determineBY(), paint);
                }
            }
        }
        
        Log.d(TAG, "Отрисовка сетки завершена");
    }

    /**
     * Находит ближайшую точку сетки к заданной точке
     * Используется для привязки объектов к сетке
     * @param t0 заданная точка в объектных координатах
     * @return ближайшая точка сетки
     */
    public Point closestGridPoint(Point t0) {
        Point t2 = new Point(); // Точка сетки

        if (gridSize <= 0) {
            return t2;
        }

        if (baseState == GridModel.State.HIDDEN) {
            return t2;
        }

        // Определение области поиска вокруг заданной точки
        Point t_1 = new Point(t0.getX() - diagonal_max, t0.getY() - diagonal_max);
        Point t_2 = new Point(t0.getX() - diagonal_max, t0.getY() + diagonal_max);
        Point t_3 = new Point(t0.getX() + diagonal_max, t0.getY() + diagonal_max);
        Point t_4 = new Point(t0.getX() + diagonal_max, t0.getY() - diagonal_max);

        // Определение границ области поиска в индексах сетки
        int grid_a_max = get_a_index_max(t_1, t_2, t_3, t_4);
        int grid_a_min = get_a_index_min(t_1, t_2, t_3, t_4);
        int grid_b_max = get_b_index_max(t_1, t_2, t_3, t_4);
        int grid_b_min = get_b_index_min(t_1, t_2, t_3, t_4);

        // Поиск ближайшей точки сетки
        double distance_min = diagonal_max;
        for (int i = grid_a_min; i <= grid_a_max; i++) {
            for (int j = grid_b_min; j <= grid_b_max; j++) {
                Point t_tmp = new Point(okx0 + d_grid_ax * i + d_grid_bx * j, oky0 + d_grid_ay * i + d_grid_by * j);

                // Проверка ограничений сетки
                if (baseState == GridModel.State.FULL || (baseState == GridModel.State.WITHIN_PAPER && isWithinPaper(t_tmp))) {
                    if (t0.distance(t_tmp) <= distance_min) {
                        distance_min = t0.distance(t_tmp);
                        t2 = t_tmp;
                    }
                }
            }
        }

        return t2;
    }

    /**
     * Проверяет, находится ли точка в пределах бумаги
     * Используется для ограничения сетки в режиме WITHIN_PAPER
     * @param t_tmp проверяемая точка
     * @return true, если точка в пределах бумаги
     */
    private boolean isWithinPaper(Point t_tmp) {
        return ((-PAPER_BOUNDARY - Epsilon.UNKNOWN_1EN6 <= t_tmp.getX()) && (t_tmp.getX() <= PAPER_BOUNDARY + Epsilon.UNKNOWN_1EN6)) && 
               ((-PAPER_BOUNDARY - Epsilon.UNKNOWN_1EN6 <= t_tmp.getY()) && (t_tmp.getY() <= PAPER_BOUNDARY + Epsilon.UNKNOWN_1EN6));
    }

    /**
     * Устанавливает данные из модели приложения
     * Синхронизирует настройки сетки с глобальными настройками приложения
     * @param applicationModel модель приложения с настройками
     */
    public void setData(ApplicationModel applicationModel) {
        setGridLineWidth(applicationModel.getGridLineWidth());
        setGridScaleColor(applicationModel.getGridScaleColor());
        setGridColor(applicationModel.getGridColor());
    }

    /**
     * Устанавливает данные конфигурации сетки из модели сетки
     * Синхронизирует все параметры сетки с моделью
     * @param gridModel модель сетки с настройками
     */
    public void setGridConfigurationData(GridModel gridModel) {
        setGridSize(gridModel.getGridSize());
        setGrid(gridModel.determineGridXLength(), gridModel.determineGridYLength(), gridModel.getGridAngle());
        setHorizontalScalePosition(gridModel.getHorizontalScalePosition());
        setVerticalScalePosition(gridModel.getVerticalScalePosition());
        setHorizontalScaleInterval(gridModel.getIntervalGridSize());
        setVerticalScaleInterval(gridModel.getIntervalGridSize());
        setBaseState(gridModel.getBaseState());
        drawDiagonalGridlines = gridModel.getDrawDiagonalGridlines();
    }

    /**
     * Оптимизирует настройки Paint для Android отрисовки
     * Устанавливает оптимальные параметры для быстрой отрисовки сетки
     * @param paint объект Paint для настройки
     */
    public void optimizePaintForGrid(Paint paint) {
        paint.setAntiAlias(false); // Отключаем сглаживание для сетки
        paint.setDither(false);    // Отключаем дизеринг
        paint.setFilterBitmap(false); // Отключаем фильтрацию битмапов
    }

    /**
     * Проверяет, нужно ли отрисовывать сетку в текущем масштабе
     * Оптимизация производительности - пропускаем отрисовку при сильном зуме
     * @param camera камера для определения текущего масштаба
     * @return true, если сетку нужно отрисовывать
     */
    public boolean shouldDrawGrid(Camera camera) {
        if (baseState == GridModel.State.HIDDEN) {
            return false;
        }
        
        // Проверяем минимальный размер ячейки сетки на экране
        LineSegment testSegment = new LineSegment(0, 0, d_grid_ax, d_grid_ay);
        LineSegment screenSegment = camera.object2TV(testSegment);
        double cellSize = screenSegment.determineLength();
        
        // Не отрисовываем сетку, если ячейки слишком малы (менее 2 пикселей)
        return cellSize >= 2.0;
    }
}
