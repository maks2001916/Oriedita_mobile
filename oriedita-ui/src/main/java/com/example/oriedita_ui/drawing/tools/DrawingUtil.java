package com.example.oriedita_ui.drawing.tools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.example.oriedita_common.editor.drawing.tools.Camera;
import com.example.oriedita_data.Colors;
import com.example.oriedita_common.editor.canvas.LineStyle;
import com.example.oriedita_core.origami.Epsilon;
import com.example.oriedita_core.origami.crease_pattern.FlatFoldabilityViolation;
import com.example.oriedita_core.origami.crease_pattern.LittleBigLittleViolation;
import com.example.oriedita_core.origami.crease_pattern.OritaCalc;
import com.example.oriedita_core.origami.crease_pattern.elements.Circle;
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor;
import com.example.oriedita_core.origami.crease_pattern.elements.LineSegment;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;

import android.graphics.Color;

import java.util.List;

/**
 * Static utility class for drawing
 */
public class DrawingUtil {
    //For drawing thick lines
    public static void widthLine(Canvas canvas, Paint paint, Point a, Point b, double width, LineColor iColor) {
        widthLine(canvas, paint, new LineSegment(a, b), width, iColor);
    }

    public static void widthLine(Canvas canvas, Paint paint, LineSegment s, double r, LineColor iColor) {
        switch (iColor) {
            case BLACK_0:
                paint.setColor(Colors.get(android.graphics.Color.BLACK));
                break;
            case RED_1:
                paint.setColor(Colors.get(android.graphics.Color.RED));
                break;
            case BLUE_2:
                paint.setColor(Colors.get(android.graphics.Color.BLUE));
                break;
            case CYAN_3:
                paint.setColor(Colors.get(android.graphics.Color.GREEN));
                break;
            case ORANGE_4:
                paint.setColor(Colors.get(android.graphics.Color.rgb(255, 165, 0)));
                break;
            default:
                break;
        }
        LineSegment sp = OritaCalc.moveParallel(s, r);
        LineSegment sm = OritaCalc.moveParallel(s, -r);

        Path path = new Path();
        path.moveTo((float) sp.determineAX(), (float) sp.determineAY());
        path.lineTo((float) sp.determineBX(), (float) sp.determineBY());
        path.lineTo((float) sm.determineBX(), (float) sm.determineBY());
        path.lineTo((float) sm.determineAX(), (float) sm.determineAY());
        path.close();
        
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
    }

    //Draw a cross around the designated Point
    public static void cross(Canvas canvas, Paint paint, Point t, double length, double width, LineColor icolor) {
        Point tx0 = new Point(t.getX() - length, t.getY());
        Point tx1 = new Point(t.getX() + length, t.getY());
        Point ty0 = new Point(t.getX(), t.getY() - length);
        Point ty1 = new Point(t.getX(), t.getY() + length);
        widthLine(canvas, paint, tx0, tx1, width, icolor);
        widthLine(canvas, paint, ty0, ty1, width, icolor);
    }

    public static void drawVertex(Canvas canvas, Paint paint, Point a, int pointSize) {
        paint.setColor(Colors.get(android.graphics.Color.GRAY));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect((float) (a.getX() - pointSize), (float) (a.getY() - pointSize), 
                       (float) (a.getX() + pointSize), (float) (a.getY() + pointSize), paint);

        paint.setColor(Colors.get(android.graphics.Color.BLACK));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect((float) (a.getX() - pointSize), (float) (a.getY() - pointSize), 
                       (float) (a.getX() + pointSize), (float) (a.getY() + pointSize), paint);
        paint.setStyle(Paint.Style.FILL);
    }

    //Draw a pointing diagram around the specified Point
    public static void pointingAt1(Canvas canvas, Paint paint, LineSegment s_tv) {
        paint.setColor(Colors.get(android.graphics.Color.argb(100, 255, 165, 0)));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine((float) s_tv.determineAX(), (float) s_tv.determineAY(), 
                       (float) s_tv.determineBX(), (float) s_tv.determineBY(), paint);
    }

    //Draw a pointing diagram around the specified Point
    public static void pointingAt2(Canvas canvas, Paint paint, LineSegment s_tv) {
        paint.setColor(Colors.get(android.graphics.Color.argb(100, 255, 165, 0)));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine((float) s_tv.determineAX(), (float) s_tv.determineAY(), 
                       (float) s_tv.determineBX(), (float) s_tv.determineBY(), paint);
    }

    public static void pointingAt3(Canvas canvas, Paint paint, LineSegment s_tv) {
        paint.setColor(Colors.get(android.graphics.Color.argb(50, 255, 200, 0)));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine((float) s_tv.determineAX(), (float) s_tv.determineAY(), 
                       (float) s_tv.determineBX(), (float) s_tv.determineBY(), paint);
    }

    public static void setColor(Canvas canvas, LineColor i) {
        // This method is deprecated - use paint.setColor() instead
        // Keeping for backward compatibility but it should not be used
    }

    public static void drawSelectLine(Canvas canvas, Paint paint, LineSegment s, Camera camera) {
        paint.setColor(Colors.get(android.graphics.Color.GREEN));
        paint.setStyle(Paint.Style.STROKE);

        LineSegment s_tv = camera.object2TV(s);

        //なぜEpsilon.UNKNOWN_0000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため
        // TODO: check if adding 1e-6 is really necessary
        Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);

        canvas.drawLine((float) a.getX(), (float) a.getY(), (float) b.getX(), (float) b.getY(), paint);
    }

    public static void drawAuxLiveLine(Canvas canvas, Paint paint, LineSegment as, Camera camera, float lineWidth, int pointSize, float f_h_WireframeLineWidth) {
        paint.setColor(Colors.get(as.getColor()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(f_h_WireframeLineWidth);

        LineSegment s_tv = camera.object2TV(as);
        Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);

        canvas.drawLine((float) a.getX(), (float) a.getY(), (float) b.getX(), (float) b.getY(), paint);

        if (lineWidth < 2.0f) {//Draw a square at the vertex
            paint.setColor(Colors.get(android.graphics.Color.GRAY));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect((float) a.getX() - pointSize, (float) a.getY() - pointSize, 
                           (float) a.getX() + pointSize + 1, (float) a.getY() + pointSize + 1, paint);
            canvas.drawRect((float) b.getX() - pointSize, (float) b.getY() - pointSize, 
                           (float) b.getX() + pointSize + 1, (float) b.getY() + pointSize + 1, paint);

            paint.setColor(Colors.get(android.graphics.Color.BLACK));
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect((float) a.getX() - pointSize, (float) a.getY() - pointSize, 
                           (float) a.getX() + pointSize + 1, (float) a.getY() + pointSize + 1, paint);
            canvas.drawRect((float) b.getX() - pointSize, (float) b.getY() - pointSize, 
                           (float) b.getX() + pointSize + 1, (float) b.getY() + pointSize + 1, paint);
            paint.setStyle(Paint.Style.FILL);
        }

        if (lineWidth >= 2.0f) {//  Thick line
            paint.setColor(Colors.get(android.graphics.Color.GRAY));
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) (lineWidth / 2.0 + pointSize), paint);

            paint.setColor(Colors.get(android.graphics.Color.BLACK));
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) (lineWidth / 2.0 + pointSize), paint);

            paint.setColor(Colors.get(android.graphics.Color.GRAY));
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            canvas.drawCircle((float) b.getX(), (float) b.getY(), (float) (lineWidth / 2.0 + pointSize), paint);

            paint.setColor(Colors.get(android.graphics.Color.BLACK));
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            canvas.drawCircle((float) b.getX(), (float) b.getY(), (float) (lineWidth / 2.0 + pointSize), paint);
        }
    }

    public static void drawCircle(Canvas canvas, Paint paint, Circle circle, Camera camera, float lineWidth, int pointSize) {
        paint.setColor(Colors.get(circle.getColor()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setAntiAlias(true);

        Point a = camera.object2TV(circle.determineCenter());

        //円周の描画
        double d_width = circle.getR() * camera.getCameraZoomX();
        canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) d_width, paint);

        a = camera.object2TV(circle.determineCenter());
        paint.setColor(Colors.get(new Color(0, 255, 255, 255)));

        if (lineWidth >= 2.0f) {//  太線指定時の中心を示す黒い小円を描く
            if (pointSize != 0) {
                d_width = (double) lineWidth / 2.0 + (double) pointSize;

                paint.setColor(Colors.get(Color.white));
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) d_width, paint);

                paint.setColor(Colors.get(android.graphics.Color.BLACK));
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) d_width, paint);
            }
        }
    }

    public static void drawAuxLine(Canvas canvas, Paint paint, LineSegment s, Camera camera, float lineWidth, int pointSize, boolean useRoundedEnds) {
        paint.setColor(Colors.get(s.getColor()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        if (useRoundedEnds) {
            paint.setStrokeCap(Paint.Cap.ROUND);
        } else {
            paint.setStrokeCap(Paint.Cap.BUTT);
        }

        if (s.getCustomized() == 0) {
            LineSegment s_tv = camera.object2TV(s);
            Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
            Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);

            canvas.drawLine((float) a.getX(), (float) a.getY(), (float) b.getX(), (float) b.getY(), paint);
        }

        if (lineWidth >= 2.0f) {//  太線
            LineSegment s_tv = camera.object2TV(s);
            Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
            Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);
            double d_width = (double) lineWidth / 2.0 + (double) pointSize;

            paint.setColor(Colors.get(Color.white));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) d_width, paint);

            paint.setColor(Colors.get(android.graphics.Color.GRAY));
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) d_width, paint);

            paint.setColor(Colors.get(Color.white));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle((float) b.getX(), (float) b.getY(), (float) d_width, paint);

            paint.setColor(Colors.get(android.graphics.Color.GRAY));
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle((float) b.getX(), (float) b.getY(), (float) d_width, paint);
        }
    }

    public static void drawCurve(Canvas canvas, Paint paint, Path curve, float lineWidth) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        canvas.drawPath(curve, paint);
    }

    public static void drawLineStep(Canvas canvas, Paint paint, LineSegment s, Camera camera, float lineWidth, boolean gridInputAssist) {
        paint.setColor(Colors.get(s.getColor()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);

        LineSegment s_tv = camera.object2TV(s);
        Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);

        canvas.drawLine((float) a.getX(), (float) a.getY(), (float) b.getX(), (float) b.getY(), paint);

        int i_width_nyuiiryokuji = 3;
        if (gridInputAssist) {
            i_width_nyuiiryokuji = 5;
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
        paint.setColor(Colors.get(color));
        paint.setStyle(Paint.Style.FILL);
        Point a = camera.object2TV(p);

        int i_width_nyuiiryokuji = 3;
        if (gridInputAssist) {
            i_width_nyuiiryokuji = 5;
        }

        canvas.drawCircle((float) a.getX(), (float) a.getY(), i_width_nyuiiryokuji, paint);
    }

    public static void drawLineCandidate(Canvas canvas, Paint paint, LineSegment s, Camera camera, int pointSize) {
        paint.setColor(Colors.get(s.getColor()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.0f);

        LineSegment s_tv = camera.object2TV(s);
        Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);

        canvas.drawLine((float) a.getX(), (float) a.getY(), (float) b.getX(), (float) b.getY(), paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle((float) a.getX(), (float) a.getY(), pointSize, paint);
        canvas.drawCircle((float) b.getX(), (float) b.getY(), pointSize, paint);
    }

    public static void drawCircleStep(Canvas canvas, Paint paint, Circle c, Camera camera) {
        paint.setColor(Colors.get(c.getColor()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.0f);
        Point a = camera.object2TV(c.determineCenter());

        double d_width = c.getR() * camera.getCameraZoomX();

        canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) d_width, paint);
    }

    private static final float[] dash_M1 = {10.0f, 3.0f, 3.0f, 3.0f};//一点鎖線
    private static final float[] dash_M2 = {10.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f};//二点鎖線
    private static final float[] dash_V = {8.0f, 8.0f};//破線

    private static final Point defaultMove = new Point(Epsilon.UNKNOWN_1EN6, Epsilon.UNKNOWN_1EN6);

    public static void drawCpLine(Canvas canvas, Paint paint, LineSegment s, Camera camera, LineStyle lineStyle, float lineWidth, int pointSize, int clipX, int clipY, boolean useRoundedEnds) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(lineWidth);

        LineSegment s_tv = camera.object2TV(s);
        Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);

        // Cohen-Sutherland line clipping
        int regionA = cohenSutherlandRegion(clipX, clipY, a);
        int regionB = cohenSutherlandRegion(clipX, clipY, b);

        if ((regionA & regionB) != 0) {
            return; // Line is completely outside the viewport
        }

        // Set stroke cap based on useRoundedEnds
        Paint.Cap cap = useRoundedEnds ? Paint.Cap.ROUND : Paint.Cap.BUTT;

        switch (lineStyle) {
            case COLOR:
                paint.setColor(Colors.get(s.getColor()));
                paint.setStrokeCap(cap);
                paint.setStrokeJoin(Paint.Join.MITER);
                break;
            case BLACK_WHITE:
                paint.setColor(Colors.get(s.getColor()));
                if (s.getColor() == LineColor.BLACK_0) {
                    paint.setStrokeCap(cap);
                    paint.setStrokeJoin(Paint.Join.MITER);
                }
                if (s.getColor() == LineColor.RED_1) {
                    paint.setColor(Colors.get(LineColor.BLACK_0));
                    paint.setStrokeCap(cap);
                    paint.setStrokeJoin(Paint.Join.MITER);
                }
                if (s.getColor() == LineColor.BLUE_2) {
                    paint.setColor(Colors.get(LineColor.GREY_10));
                    paint.setStrokeCap(cap);
                    paint.setStrokeJoin(Paint.Join.MITER);
                }
                break;
            case COLOR_AND_SHAPE:
                paint.setColor(Colors.get(s.getColor()));
                if (s.getColor() == LineColor.BLACK_0) {
                    paint.setStrokeCap(cap);
                    paint.setStrokeJoin(Paint.Join.MITER);
                }
                if (s.getColor() == LineColor.RED_1) {
                    paint.setStrokeCap(cap);
                    paint.setStrokeJoin(Paint.Join.MITER);
                    paint.setPathEffect(new android.graphics.DashPathEffect(dash_M1, 0));
                }
                if (s.getColor() == LineColor.BLUE_2) {
                    paint.setStrokeCap(cap);
                    paint.setStrokeJoin(Paint.Join.MITER);
                    paint.setPathEffect(new android.graphics.DashPathEffect(dash_V, 0));
                }
                break;
            case BLACK_ONE_DOT:
                if (s.getColor() == LineColor.BLACK_0) {
                    paint.setColor(Colors.get(LineColor.BLACK_0));
                    paint.setStrokeCap(cap);
                    paint.setStrokeJoin(Paint.Join.MITER);
                }
                if (s.getColor() == LineColor.RED_1) {
                    paint.setColor(Colors.get(LineColor.BLACK_0));
                    paint.setStrokeCap(cap);
                    paint.setStrokeJoin(Paint.Join.MITER);
                    paint.setPathEffect(new android.graphics.DashPathEffect(dash_M1, 0));
                }
                if (s.getColor() == LineColor.BLUE_2) {
                    paint.setColor(Colors.get(LineColor.BLACK_0));
                    paint.setStrokeCap(cap);
                    paint.setStrokeJoin(Paint.Join.MITER);
                    paint.setPathEffect(new android.graphics.DashPathEffect(dash_V, 0));
                }
                break;
            case BLACK_TWO_DOT:
                if (s.getColor() == LineColor.BLACK_0) {
                    paint.setColor(Colors.get(LineColor.BLACK_0));
                    paint.setStrokeCap(cap);
                    paint.setStrokeJoin(Paint.Join.MITER);
                }
                if (s.getColor() == LineColor.RED_1) {
                    paint.setColor(Colors.get(LineColor.BLACK_0));
                    paint.setStrokeCap(cap);
                    paint.setStrokeJoin(Paint.Join.MITER);
                    paint.setPathEffect(new android.graphics.DashPathEffect(dash_M2, 0));
                }
                if (s.getColor() == LineColor.BLUE_2) {
                    paint.setColor(Colors.get(LineColor.BLACK_0));
                    paint.setStrokeCap(cap);
                    paint.setStrokeJoin(Paint.Join.MITER);
                    paint.setPathEffect(new android.graphics.DashPathEffect(dash_V, 0));
                }
                break;
        }

        canvas.drawLine((float) a.getX(), (float) a.getY(), (float) b.getX(), (float) b.getY(), paint);

        if (Epsilon.high.eq0(lineWidth) || pointSize == 0) {
            return;
        }
        if (lineWidth < 2.0f) {//頂点の黒い正方形を描く
            drawVertex(canvas, paint, a, pointSize);
            if (a.distance(b) > 1) {
                drawVertex(canvas, paint, b, pointSize);
            }
        } else if (lineWidth >= 2.0f) {//  太線
            paint.setStrokeWidth(1.0f + lineWidth % 1.0f);
            paint.setStrokeCap(Paint.Cap.BUTT);
            paint.setStrokeJoin(Paint.Join.MITER);
            double d_width = (double) lineWidth / 2.0 + (double) pointSize;

            paint.setColor(Colors.get(android.graphics.Color.GRAY));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) d_width, paint);

            paint.setColor(Colors.get(android.graphics.Color.BLACK));
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle((float) a.getX(), (float) a.getY(), (float) d_width, paint);

            paint.setColor(Colors.get(android.graphics.Color.GRAY));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle((float) b.getX(), (float) b.getY(), (float) d_width, paint);

            paint.setColor(Colors.get(android.graphics.Color.BLACK));
            paint.setStyle(Paint.Style.STROKE);
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
     * returns the region according to the Cohen-Sutherland Line Clipping Algorithm using a viewport rectangle
     * going from (clipLowX, clipLowY) to (clipHighX, clipHighY). If the point is inside the viewport, the region
     * will be CENTER (= 0), otherwise, the direction of the point in relation to the viewport can be retrieved
     * using bitwise-and with the Constants WEST, EAST, NORTH, SOUTH.
     * <p>
     * If the bitwise-and of the Endpoints of a Line is not CENTER, the whole line is outside the viewport.
     * <p>
     * for more info, see https://en.wikipedia.org/wiki/Cohen%E2%80%93Sutherland_algorithm
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

    /**
     * Draws a Flatfoldability violation to the graphics object.
     *
     * @param canvas            Graphics on which to draw
     * @param p            point that violates flatfoldability
     * @param violation    object that describes the violation
     * @param transparency how transparently the violation should be drawn
     * @param useAdvanced  whether to use the "legacy" way to draw (purple circles) or the newer one which differentiates
     *                     between types of violations
     */
    public static void drawViolation(Canvas canvas, Paint paint, Point p, FlatFoldabilityViolation violation, int transparency, boolean useAdvanced) {
        paint.setColor(Colors.get(new Color(255, 0, 147, transparency)));
        paint.setAntiAlias(true);

        if (!useAdvanced) {
            paint.setColor(Colors.get(new Color(255, 0, 147, transparency)));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle((float) p.getX(), (float) p.getY(), 11.5f, paint);
            return;
        }

        Color c = violation.getColor().getColor();
        Color actualColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), transparency);
        paint.setColor(actualColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1.5f);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeJoin(Paint.Join.BEVEL);
        
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
                canvas.drawRect((float) p.getX() - 9, (float) p.getY() - 9, 
                               (float) p.getX() + 9, (float) p.getY() + 9, paint);
                break;
            case LITTLE_BIG_LITTLE:
                if (violation instanceof LittleBigLittleViolation) {
                    LittleBigLittleViolation lblViolation = (LittleBigLittleViolation) violation;
                    List<LineSegment> violating = lblViolation.getViolatingSegments();
                    List<LineSegment> all = lblViolation.getAllSegments();

                    for (int i = 0; i < all.size(); i++) {
                        LineSegment current = all.get(i);
                        LineSegment next = all.get((i + 1) % all.size());

                        float[] xCoords = new float[]{
                                (float) p.getX(),
                                (float) (p.getX() + current.determineDeltaX()),
                                (float) (p.getX() + next.determineDeltaX())
                        };
                        float[] yCoords = new float[]{
                                (float) p.getY(),
                                (float) (p.getY() + current.determineDeltaY()),
                                (float) (p.getY() + next.determineDeltaY())
                        };
                        
                        paint.setStyle(Paint.Style.STROKE);
                        canvas.drawLines(new float[]{
                                xCoords[0], yCoords[0], xCoords[1], yCoords[1],
                                xCoords[1], yCoords[1], xCoords[2], yCoords[2],
                                xCoords[2], yCoords[2], xCoords[0], yCoords[0]
                        }, paint);
                        
                        if (violating.get(i)) {
                            paint.setStyle(Paint.Style.FILL);
                            Path path = new Path();
                            path.moveTo(xCoords[0], yCoords[0]);
                            path.lineTo(xCoords[1], yCoords[1]);
                            path.lineTo(xCoords[2], yCoords[2]);
                            path.close();
                            canvas.drawPath(path, paint);
                        }
                    }
                }
                break;
        }
    }

    private static void drawTriangleAroundPoint(Canvas canvas, Paint paint, Point p) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        
        Path path = new Path();
        path.moveTo((float) p.getX(), (float) p.getY() - 9);
        path.lineTo((float) p.getX() - 10, (float) p.getY() + 7);
        path.lineTo((float) p.getX() + 10, (float) p.getY() + 7);
        path.close();
        
        canvas.drawPath(path, paint);
    }
}
