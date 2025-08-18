package com.example.oriedita_data.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.DashPathEffect;

import com.example.oriedita_common.editor.drawing.tools.Camera;
import com.example.oriedita_common.editor.canvas.LineStyle;
import com.example.oriedita_core.origami.crease_pattern.FlatFoldabilityViolation;
import com.example.oriedita_core.origami.crease_pattern.LittleBigLittleViolation;
import com.example.oriedita_core.origami.crease_pattern.OritaCalc;
import com.example.oriedita_core.origami.crease_pattern.elements.Circle;
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;

import com.example.oriedita_core.origami.Epsilon;

/**
 * Статический утилитарный класс для рисования в Android
 */
public class DrawingUtil {
    
    // Для рисования толстых линий
    public static void widthLine(Canvas canvas, Paint paint, Point a, Point b, double width, LineColor iColor) {
        widthLine(canvas, paint, new LineSegment(a, b), width, iColor);
    }

    public static void widthLine(Canvas canvas, Paint paint, LineSegment s, double r, LineColor iColor) {
        setColor(paint, iColor);
        
        LineSegment sp = OritaCalc.moveParallel(s, r);
        LineSegment sm = OritaCalc.moveParallel(s, -r);

        float[] x = new float[5];
        float[] y = new float[5];

        x[0] = (float) sp.determineAX();
        y[0] = (float) sp.determineAY();
        x[1] = (float) sp.determineBX();
        y[1] = (float) sp.determineBY();
        x[2] = (float) sm.determineBX();
        y[2] = (float) sm.determineBY();
        x[3] = (float) sm.determineAX();
        y[3] = (float) sm.determineAY();

        Path path = new Path();
        path.moveTo(x[0], y[0]);
        for (int i = 1; i < 4; i++) {
            path.lineTo(x[i], y[i]);
        }
        path.close();
        
        canvas.drawPath(path, paint);
    }

    // Рисование креста вокруг указанной точки
    public static void cross(Canvas canvas, Paint paint, Point t, double length, double width, LineColor icolor) {
        Point tx0 = new Point(t.getX() - length, t.getY());
        Point tx1 = new Point(t.getX() + length, t.getY());
        Point ty0 = new Point(t.getX(), t.getY() - length);
        Point ty1 = new Point(t.getX(), t.getY() + length);
        widthLine(canvas, paint, tx0, tx1, width, icolor);
        widthLine(canvas, paint, ty0, ty1, width, icolor);
    }

    public static void drawVertex(Canvas canvas, Paint paint, Point a, int pointSize) {
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(
            (float) (a.getX() - pointSize), 
            (float) (a.getY() - pointSize), 
            (float) (a.getX() + pointSize), 
            (float) (a.getY() + pointSize), 
            paint
        );

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(
            (float) (a.getX() - pointSize), 
            (float) (a.getY() - pointSize), 
            (float) (a.getX() + pointSize), 
            (float) (a.getY() + pointSize), 
            paint
        );
    }

    // Рисование указательной диаграммы вокруг указанной точки
    public static void pointingAt1(Canvas canvas, Paint paint, LineSegment s_tv) {
        paint.setColor(Color.argb(100, 255, 165, 0)); // Оранжевый с прозрачностью
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(
            (float) s_tv.determineAX(), 
            (float) s_tv.determineAY(), 
            (float) s_tv.determineBX(), 
            (float) s_tv.determineBY(), 
            paint
        );
    }

    // Рисование указательной диаграммы вокруг указанной точки
    public static void pointingAt2(Canvas canvas, Paint paint, LineSegment s_tv) {
        paint.setColor(Color.argb(100, 255, 165, 0)); // Оранжевый с прозрачностью
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(
            (float) s_tv.determineAX(), 
            (float) s_tv.determineAY(), 
            (float) s_tv.determineBX(), 
            (float) s_tv.determineBY(), 
            paint
        );
    }

    // Рисование указательной диаграммы вокруг указанной точки
    public static void pointingAt3(Canvas canvas, Paint paint, LineSegment s_tv) {
        paint.setColor(Color.argb(50, 255, 200, 0)); // Желтый с прозрачностью
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(
            (float) s_tv.determineAX(), 
            (float) s_tv.determineAY(), 
            (float) s_tv.determineBX(), 
            (float) s_tv.determineBY(), 
            paint
        );
    }

    public static void setColor(Paint paint, LineColor i) {
        switch (i) {
            case BLACK_0:
                paint.setColor(Color.BLACK);
                break;
            case RED_1:
                paint.setColor(Color.RED);
                break;
            case BLUE_2:
                paint.setColor(Color.BLUE);
                break;
            case CYAN_3:
                paint.setColor(Color.rgb(100, 200, 200));
                break;
            case ORANGE_4:
                paint.setColor(Color.rgb(255, 165, 0));
                break;
            case MAGENTA_5:
                paint.setColor(Color.MAGENTA);
                break;
            case GREEN_6:
                paint.setColor(Color.GREEN);
                break;
            case YELLOW_7:
                paint.setColor(Color.YELLOW);
                break;
            case PURPLE_8:
                paint.setColor(Color.rgb(210, 0, 255));
                break;
            case GREY_10:
                paint.setColor(Color.rgb(162, 162, 162));
                break;
            default:
                break;
        }
    }

    public static void drawSelectLine(Canvas canvas, Paint paint, LineSegment s, Camera camera) {
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);

        LineSegment s_tv = camera.object2TV(s);

        // Причина добавления Epsilon.UNKNOWN_0000001 - предотвращение влияния исходной линии сгиба на новую при рисовании на дисплее
        Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);

        canvas.drawLine(
            (float) a.getX(), 
            (float) a.getY(), 
            (float) b.getX(), 
            (float) b.getY(), 
            paint
        );
    }

    public static void drawAuxLiveLine(Canvas canvas, Paint paint, LineSegment as, Camera camera, float lineWidth, int pointSize, float f_h_WireframeLineWidth) {
        setColor(paint, as.getColor());

        LineSegment s_tv = camera.object2TV(as);
        Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(
            (float) a.getX(), 
            (float) a.getY(), 
            (float) b.getX(), 
            (float) b.getY(), 
            paint
        );

        if (lineWidth < 2.0f) { // Рисование квадрата в вершине
            paint.setColor(Color.GRAY);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(
                (float) a.getX() - pointSize, 
                (float) a.getY() - pointSize, 
                (float) a.getX() + pointSize, 
                (float) a.getY() + pointSize, 
                paint
            );
            canvas.drawRect(
                (float) b.getX() - pointSize, 
                (float) b.getY() - pointSize, 
                (float) b.getX() + pointSize, 
                (float) b.getY() + pointSize, 
                paint
            );

            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(
                (float) a.getX() - pointSize, 
                (float) a.getY() - pointSize, 
                (float) a.getX() + pointSize, 
                (float) a.getY() + pointSize, 
                paint
            );
            canvas.drawRect(
                (float) b.getX() - pointSize, 
                (float) b.getY() - pointSize, 
                (float) b.getX() + pointSize, 
                (float) b.getY() + pointSize, 
                paint
            );
        }

        if (lineWidth >= 2.0f) { // Толстая линия
            paint.setStrokeWidth(1.0f + f_h_WireframeLineWidth % 1.0f);
            paint.setStrokeCap(Paint.Cap.BUTT);
            paint.setStrokeJoin(Paint.Join.MITER);

            if (pointSize != 0) {
                double d_width = (double) lineWidth / 2.0 + (double) pointSize;

                paint.setColor(Color.GRAY);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) d_width, paint);
                canvas.drawCircle((float) b.getX(), (float) b.getY(), (float) d_width, paint);

                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) d_width, paint);
                canvas.drawCircle((float) b.getX(), (float) b.getY(), (float) d_width, paint);
            }

            paint.setStrokeWidth(f_h_WireframeLineWidth);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.MITER);
        }
    }

    public static void drawCircle(Canvas canvas, Paint paint, Circle circle, Camera camera, float lineWidth, int pointSize) {
        Point a = camera.object2TV(circle.determineCenter());

        paint.setStrokeWidth(lineWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);

        if (circle.getCustomized() == 0) {
            setColor(paint, circle.getColor());
        } else if (circle.getCustomized() == 1) {
            paint.setColor(circle.getCustomizedColor());
        }

        // Рисование окружности
        double d_width = circle.getR() * camera.getCameraZoomX();
        canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) d_width, paint);

        a = camera.object2TV(circle.determineCenter());

        paint.setStrokeWidth(lineWidth);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setColor(Color.rgb(0, 255, 255));

        // Рисование центра круга
        if (lineWidth < 2.0f) { // Черный квадрат в центре
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(
                (float) a.getX() - pointSize, 
                (float) a.getY() - pointSize, 
                (float) a.getX() + pointSize, 
                (float) a.getY() + pointSize, 
                paint
            );
        }

        if (lineWidth >= 2.0f) { // Маленький черный круг в центре для толстых линий
            paint.setStrokeWidth(1.0f + lineWidth % 1.0f);
            paint.setStrokeCap(Paint.Cap.BUTT);
            if (pointSize != 0) {
                d_width = (double) lineWidth / 2.0 + (double) pointSize;

                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) d_width, paint);

                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) d_width, paint);
            }
        }
    }

    public static void drawAuxLine(Canvas canvas, Paint paint, LineSegment s, Camera camera, float lineWidth, int pointSize, boolean useRoundedEnds) {
        paint.setStrokeWidth(lineWidth);
        paint.setStrokeCap(useRoundedEnds ? Paint.Cap.ROUND : Paint.Cap.BUTT);
        paint.setStyle(Paint.Style.STROKE);

        if (s.getCustomized() == 0) {
            setColor(paint, s.getColor());
        } else if (s.getCustomized() == 1) {
            paint.setColor(s.getCustomizedColor());
        }

        LineSegment s_tv = camera.object2TV(s);
        Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);

        canvas.drawLine(
            (float) a.getX(), 
            (float) a.getY(), 
            (float) b.getX(), 
            (float) b.getY(), 
            paint
        );
    }

    public static void drawCurve(Canvas canvas, Paint paint, Path curve, float lineWidth) {
        paint.setStrokeWidth(lineWidth);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(curve, paint);
    }

    public static void drawLineStep(Canvas canvas, Paint paint, LineSegment s, Camera camera, float lineWidth, boolean gridInputAssist) {
        if (s == null) return;
        
        setColor(paint, s.getColor());
        paint.setStrokeWidth(lineWidth);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStyle(Paint.Style.STROKE);

        LineSegment s_tv = camera.object2TV(s);
        Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);

        canvas.drawLine(
            (float) a.getX(), 
            (float) a.getY(), 
            (float) b.getX(), 
            (float) b.getY(), 
            paint
        );
        
        int i_width_nyuiiryokuji = 3;
        if (gridInputAssist) {
            i_width_nyuiiryokuji = 2;
        }

        paint.setStyle(Paint.Style.FILL);
        switch (s.getActive()) {
            case ACTIVE_A_1:
                canvas.drawCircle((float) a.getX(), (float) a.getY(), i_width_nyuiiryokuji, paint);
                break;
            case ACTIVE_B_2:
                canvas.drawCircle((float) b.getX(), (float) b.getY(), i_width_nyuiiryokuji, paint);
                break;
            case ACTIVE_BOTH_3:
                canvas.drawCircle((float) a.getX(), (float) a.getY(), i_width_nyuiiryokuji, paint);
                canvas.drawCircle((float) b.getX(), (float) b.getY(), i_width_nyuiiryokuji, paint);
                break;
            default:
                break;
        }
    }

    public static void drawStepVertex(Canvas canvas, Paint paint, Point p, LineColor color, Camera camera, boolean gridInputAssist) {
        if (p == null) return;
        setColor(paint, color);
        Point a = camera.object2TV(p);
        int i_width_nyuiiryokuji = 3;
        if (gridInputAssist) {
            i_width_nyuiiryokuji = 2;
        }

        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle((float) a.getX(), (float) a.getY(), i_width_nyuiiryokuji, paint);
    }

    public static void drawLineCandidate(Canvas canvas, Paint paint, LineSegment s, Camera camera, int pointSize) {
        setColor(paint, s.getColor());
        paint.setStyle(Paint.Style.STROKE);

        LineSegment s_tv = camera.object2TV(s);
        Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);

        canvas.drawLine(
            (float) a.getX(), 
            (float) a.getY(), 
            (float) b.getX(), 
            (float) b.getY(), 
            paint
        );
        
        int i_width = pointSize + 5;

        switch (s.getActive()) {
            case ACTIVE_A_1:
                canvas.drawLine((float) a.getX() - i_width, (float) a.getY(), (float) a.getX() + i_width, (float) a.getY(), paint);
                canvas.drawLine((float) a.getX(), (float) a.getY() - i_width, (float) a.getX(), (float) a.getY() + i_width, paint);
                break;
            case ACTIVE_B_2:
                canvas.drawLine((float) b.getX() - i_width, (float) b.getY(), (float) b.getX() + i_width, (float) b.getY(), paint);
                canvas.drawLine((float) b.getX(), (float) b.getY() - i_width, (float) b.getX(), (float) b.getY() + i_width, paint);
                break;
            case ACTIVE_BOTH_3:
                canvas.drawLine((float) a.getX() - i_width, (float) a.getY(), (float) a.getX() + i_width, (float) a.getY(), paint);
                canvas.drawLine((float) a.getX(), (float) a.getY() - i_width, (float) a.getX(), (float) a.getY() + i_width, paint);
                canvas.drawLine((float) b.getX() - i_width, (float) b.getY(), (float) b.getX() + i_width, (float) b.getY(), paint);
                canvas.drawLine((float) b.getX(), (float) b.getY() - i_width, (float) b.getX(), (float) b.getY() + i_width, paint);
                break;
            default:
                break;
        }
    }

    public static void drawCircleStep(Canvas canvas, Paint paint, Circle c, Camera camera) {
        if (c == null) return;
        
        setColor(paint, c.getColor());
        paint.setStyle(Paint.Style.STROKE);
        
        Point a = camera.object2TV(c.determineCenter());
        a = new Point(a.getX() + Epsilon.UNKNOWN_1EN6, a.getY() + Epsilon.UNKNOWN_1EN6);

        double d_width = c.getR() * camera.getCameraZoomX();

        canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) d_width, paint);
    }

    public static void drawText(Canvas canvas, Paint paint, String text, Point position, Camera camera) {
        if (position == null) return;
        if (text == null || text.trim().isEmpty()) return;
        
        Point p = camera.object2TV(position);
        int tempColor = paint.getColor();
        paint.setColor(Color.BLACK);
        paint.setTextSize(16f); // Размер текста по умолчанию
        canvas.drawText(text, (float) p.getX(), (float) p.getY(), paint);
        paint.setColor(tempColor);
    }

    /**
     * Рисование линии CP (Crease Pattern) с различными стилями
     */
    public static void drawCpLine(Canvas canvas, Paint paint, LineSegment s, Camera camera, LineStyle lineStyle, float lineWidth, int pointSize, int clipX, int clipY, boolean useRoundedEnds) {
        if (s == null) return;
        
        LineSegment s_tv = camera.object2TV(s);
        Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);

        // Проверка отсечения по алгоритму Коэна-Сазерленда
        int aflag = cohenSutherlandRegion(clipX, clipY, a);
        if (aflag != CENTER) {
            int bflag = cohenSutherlandRegion(clipX, clipY, b);
            if ((aflag & bflag) != CENTER) {
                return; // Линия полностью вне области отображения
            }
        }

        // Настройка стиля линии
        paint.setStrokeCap(useRoundedEnds ? Paint.Cap.ROUND : Paint.Cap.BUTT);
        paint.setStrokeJoin(Paint.Join.MITER);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);

        switch (lineStyle) {
            case COLOR:
                setColor(paint, s.getColor());
                break;
            case BLACK_WHITE:
                if (s.getColor() == LineColor.BLACK_0) {
                    setColor(paint, s.getColor());
                } else if (s.getColor() == LineColor.RED_1) {
                    setColor(paint, LineColor.BLACK_0);
                } else if (s.getColor() == LineColor.BLUE_2) {
                    setColor(paint, LineColor.GREY_10);
                }
                break;
            case COLOR_AND_SHAPE:
                setColor(paint, s.getColor());
                if (s.getColor() == LineColor.RED_1) {
                    // Штрихпунктирная линия для красного цвета
                    paint.setPathEffect(new DashPathEffect(new float[]{10.0f, 5.0f, 2.0f, 5.0f}, 0.0f));
                } else if (s.getColor() == LineColor.BLUE_2) {
                    // Пунктирная линия для синего цвета
                    paint.setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, 0.0f));
                }
                break;
            case BLACK_ONE_DOT:
                if (s.getColor() == LineColor.BLACK_0) {
                    setColor(paint, s.getColor());
                } else if (s.getColor() == LineColor.RED_1) {
                    // Штрихпунктирная линия
                    paint.setPathEffect(new DashPathEffect(new float[]{10.0f, 5.0f, 2.0f, 5.0f}, 0.0f));
                } else if (s.getColor() == LineColor.BLUE_2) {
                    // Пунктирная линия
                    paint.setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, 0.0f));
                }
                break;
            case BLACK_TWO_DOT:
                if (s.getColor() == LineColor.BLACK_0) {
                    setColor(paint, s.getColor());
                } else if (s.getColor() == LineColor.RED_1) {
                    // Двухштрихпунктирная линия
                    paint.setPathEffect(new DashPathEffect(new float[]{10.0f, 5.0f, 2.0f, 5.0f, 2.0f, 5.0f}, 0.0f));
                } else if (s.getColor() == LineColor.BLUE_2) {
                    // Пунктирная линия
                    paint.setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, 0.0f));
                }
                break;
        }

        // Рисование основной линии
        canvas.drawLine((float) a.getX(), (float) a.getY(), (float) b.getX(), (float) b.getY(), paint);

        // Сброс эффекта пути для последующего рисования
        paint.setPathEffect(null);

        if (lineWidth <= 0.0f || pointSize == 0) {
            return;
        }

        if (lineWidth < 2.0f) {
            // Рисование черных квадратов в вершинах для тонких линий
            drawVertex(canvas, paint, a, pointSize);
            if (a.distance(b) > 1) {
                drawVertex(canvas, paint, b, pointSize);
            }
        } else if (lineWidth >= 2.0f) {
            // Рисование толстых линий с круглыми вершинами
            paint.setStrokeWidth(1.0f + lineWidth % 1.0f);
            paint.setStrokeCap(Paint.Cap.BUTT);
            
            double d_width = (double) lineWidth / 2.0 + (double) pointSize;

            // Рисование серых кругов в вершинах
            paint.setColor(Color.GRAY);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) d_width, paint);
            canvas.drawCircle((float) b.getX(), (float) b.getY(), (float) d_width, paint);

            // Рисование черных контуров кругов
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) d_width, paint);
            canvas.drawCircle((float) b.getX(), (float) b.getY(), (float) d_width, paint);
        }
    }

    public static final int CENTER = 0;
    public static final int WEST = 0b0001;
    public static final int EAST = 0b0010;
    public static final int NORTH = 0b0100;
    public static final int SOUTH = 0b1000;

    public static int cohenSutherlandRegion(int clipX, int clipY, Point point) {
        return cohenSutherlandRegion(0, 0, clipX, clipY, point);
    }

    /**
     * Возвращает регион согласно алгоритму отсечения линий Коэна-Сазерленда, используя прямоугольник области просмотра
     * от (clipLowX, clipLowY) до (clipHighX, clipHighY). Если точка находится внутри области просмотра, регион
     * будет CENTER (= 0), в противном случае направление точки относительно области просмотра можно получить
     * используя побитовое И с константами WEST, EAST, NORTH, SOUTH.
     * <p>
     * Если побитовое И конечных точек линии не равно CENTER, вся линия находится вне области просмотра.
     * <p>
     * для получения дополнительной информации см. https://en.wikipedia.org/wiki/Cohen%E2%80%93Sutherland_algorithm
     */
    public static int cohenSutherlandRegion(int clipLowX, int clipLowY, int clipHighX, int clipHighY, Point point) {
        int region = CENTER;
        if (point.getX() < clipLowX) {
            region |= WEST;
        } else if (point.getX() > clipHighX) {
            region |= EAST;
        }
        if (point.getY() < clipLowY) {
            region |= SOUTH;
        } else if (point.getY() > clipHighY) {
            region |= NORTH;
        }
        return region;
    }

    public static void drawViolation(Canvas canvas, Paint paint, Point p, FlatFoldabilityViolation violation, int transparency, boolean useAdvanced) {
        paint.setColor(Color.argb(transparency, 255, 0, 147));

        if (!useAdvanced) {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle((float) p.getX(), (float) p.getY(), 11.5f, paint);
            return;
        }
        
        int color;
        switch (violation.getColor()) {
            case NOT_ENOUGH_MOUNTAIN:
                color = Color.RED;
                break;
            case NOT_ENOUGH_VALLEY:
                color = Color.BLUE;
                break;
            case UNKNOWN:
                color = Color.rgb(255, 0, 147);
                break;
            default:
                color = Color.GRAY;
                break;
        }
        
        int actualColor = Color.argb(transparency, Color.red(color), Color.green(color), Color.blue(color));
        paint.setColor(actualColor);
        paint.setStrokeWidth(1.5f);
        paint.setStrokeCap(Paint.Cap.BUTT);
        
        switch (violation.getViolatedRule()) {
            case NUMBER_OF_FOLDS:
                drawTriangleAroundPoint(canvas, paint, p);
                break;
            case ANGLES:
                if (violation.getColor() == FlatFoldabilityViolation.Color.CORRECT) {
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle((float) p.getX(), (float) p.getY(), 11.5f, paint);
                } else {
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle((float) p.getX(), (float) p.getY(), 11.5f, paint);
                }
                break;
            case MAEKAWA:
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(
                    (float) p.getX() - 9, 
                    (float) p.getY() - 9, 
                    (float) p.getX() + 9, 
                    (float) p.getY() + 9, 
                    paint
                );
                break;
            case LITTLE_BIG_LITTLE:
                LittleBigLittleViolation lViolation;
                if (violation instanceof LittleBigLittleViolation) {
                    lViolation = (LittleBigLittleViolation) violation;
                } else {
                    // Логирование предупреждения
                    break;
                }
                LineSegment[] segments = lViolation.getLineSegments();
                boolean[] violating = lViolation.getViolatingLBL();
                for (int i = 0; i < segments.length; i++) {
                    if (i == segments.length - 1 && segments[i].getColor() == LineColor.BLACK_0) {
                        break;
                    }
                    LineSegment current = OritaCalc.lineSegmentChangeLength(segments[i], 15);
                    LineSegment next = OritaCalc.lineSegmentChangeLength(segments[(i + 1) % segments.length], 15);
                    
                    Path path = new Path();
                    path.moveTo((float) p.getX(), (float) p.getY());
                    path.lineTo((float) (p.getX() + current.determineDeltaX()), (float) (p.getY() + current.determineDeltaY()));
                    path.lineTo((float) (p.getX() + next.determineDeltaX()), (float) (p.getY() + next.determineDeltaY()));
                    path.close();
                    
                    paint.setStyle(Paint.Style.STROKE);
                    canvas.drawPath(path, paint);
                    
                    if (violating[i]) {
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawPath(path, paint);
                    }
                }
                break;
            case NONE:
                break;
        }
    }

    private static void drawTriangleAroundPoint(Canvas canvas, Paint paint, Point p) {
        Path path = new Path();
        path.moveTo((float) p.getX(), (float) (p.getY() - 9));
        path.lineTo((float) (p.getX() - 10), (float) (p.getY() + 7));
        path.lineTo((float) (p.getX() + 10), (float) (p.getY() + 7));
        path.close();
        
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
    }
    
    /**
     * Рисует комментарии на холсте
     */
    public static void drawComments(Canvas canvas, Paint paint, Camera camera, float lineWidth) {
        if (canvas == null || paint == null || camera == null) return;
        
        try {
            // TODO: Интегрировать с существующей системой комментариев TextWorker
            // Пока используем базовое рисование комментариев
            System.out.println("Рисование комментариев через DrawingUtil...");
            
            // Здесь можно добавить логику рисования комментариев
            // Например, получение списка комментариев и их отрисовка
            // paint.setTextSize(16f);
            // paint.setColor(Color.BLACK);
            // canvas.drawText("Комментарии", 10, 20, paint);
            
        } catch (Exception e) {
            System.out.println("Ошибка при рисовании комментариев: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Рисует рамку операции на холсте
     */
    public static void drawOperationFrame(Canvas canvas, Paint paint, Camera camera, float lineWidth) {
        if (canvas == null || paint == null || camera == null) return;
        
        try {
            // TODO: Интегрировать с существующей системой рамок OperationFrame
            // Пока используем базовое рисование рамки
            System.out.println("Рисование рамки операции через DrawingUtil...");
            
            // Здесь можно добавить логику рисования рамки операции
            // Например, получение границ рамки и их отрисовка
            // paint.setStyle(Paint.Style.STROKE);
            // paint.setStrokeWidth(lineWidth);
            // paint.setColor(Color.GREEN);
            // canvas.drawRect(10, 10, 100, 100, paint);
            
        } catch (Exception e) {
            System.out.println("Ошибка при рисовании рамки операции: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 