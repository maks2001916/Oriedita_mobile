package com.example.oriedita_ui.drawing.tools;

import androidx.compose.ui.graphics.Canvas;

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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

/**
 * Static utility class for drawing
 */
public class DrawingUtil {
    //For drawing thick lines
    public static void widthLine(Canvas canvas, Point a, Point b, double width, LineColor iColor) {
        widthLine(canvas, new LineSegment(a, b), width, iColor);
    }

    public static void widthLine(Canvas canvas, LineSegment s, double r, LineColor iColor) {
        switch (iColor) {
            case BLACK_0:
                canvas.setColor(Colors.get(Color.black));
                break;
            case RED_1:
                canvas.setColor(Colors.get(Color.red));
                break;
            case BLUE_2:
                canvas.setColor(Colors.get(Color.blue));
                break;
            case CYAN_3:
                canvas.setColor(Colors.get(Color.green));
                break;
            case ORANGE_4:
                canvas.setColor(Colors.get(Color.orange));
                break;
            default:
                break;
        }
        LineSegment sp = OritaCalc.moveParallel(s, r);
        LineSegment sm = OritaCalc.moveParallel(s, -r);

        int[] x = new int[5];
        int[] y = new int[5];

        x[0] = (int) sp.determineAX();
        y[0] = (int) sp.determineAY();
        x[1] = (int) sp.determineBX();
        y[1] = (int) sp.determineBY();
        x[2] = (int) sm.determineBX();
        y[2] = (int) sm.determineBY();
        x[3] = (int) sm.determineAX();
        y[3] = (int) sm.determineAY();

        canvas.fillPolygon(x, y, 4);
    }

    //Draw a cross around the designated Point
    public static void cross(Canvas canvas, Point t, double length, double width, LineColor icolor) {
        Point tx0 = new Point(t.getX() - length, t.getY());
        Point tx1 = new Point(t.getX() + length, t.getY());
        Point ty0 = new Point(t.getX(), t.getY() - length);
        Point ty1 = new Point(t.getX(), t.getY() + length);
        widthLine(canvas, tx0, tx1, width, icolor);
        widthLine(canvas, ty0, ty1, width, icolor);
    }

    public static void drawVertex(Graphics2D canvas, Point a, int pointSize) {
        canvas.setColor(Colors.get(Color.gray));
        canvas.fillRect((int) (a.getX() - pointSize), (int) (a.getY() - pointSize), (int) (pointSize * 2 + 0.5), (int) (pointSize * 2 + 0.5));

        canvas.setColor(Colors.get(Color.black));
        canvas.drawRect((int) (a.getX() - pointSize), (int) (a.getY() - pointSize), (int) (pointSize * 2 + 0.5), (int) (pointSize * 2 + 0.5));
    }

    //Draw a pointing diagram around the specified Point
    public static void pointingAt1(Canvas canvas, LineSegment s_tv) {
        canvas.setColor(Colors.get(new Color(255, 165, 0, 100)));//canvas.setColor(Colors.get(Color.ORANGE));
        canvas.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), (int) s_tv.determineBX(), (int) s_tv.determineBY()); //直線
    }

    //Draw a pointing diagram around the specified Point
    public static void pointingAt2(Canvas canvas, LineSegment s_tv) {
        canvas.setColor(Colors.get(new Color(255, 165, 0, 100)));//canvas.setColor(Colors.get(Color.ORANGE));
        canvas.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), (int) s_tv.determineBX(), (int) s_tv.determineBY()); //直線

    }

    //Draw a pointing diagram around the specified Point
    public static void pointingAt3(Canvas canvas, LineSegment s_tv) {
        canvas.setColor(Colors.get(new Color(255, 200, 0, 50)));
        canvas.drawLine((int) s_tv.determineAX(), (int) s_tv.determineAY(), (int) s_tv.determineBX(), (int) s_tv.determineBY()); //直線
    }

    public static void setColor(Canvas canvas, LineColor i) {
        switch (i) {
            case BLACK_0:
                canvas.setColor(Colors.get(Color.black));
                break;
            case RED_1:
                canvas.setColor(Colors.get(Color.red));
                break;
            case BLUE_2:
                canvas.setColor(Colors.get(Color.blue));
                break;
            case CYAN_3:
                canvas.setColor(Colors.get(new Color(100, 200, 200)));
                break;
            case ORANGE_4:
                canvas.setColor(Colors.get(Color.orange));
                break;
            case MAGENTA_5:
                canvas.setColor(Colors.get(Color.magenta));
                break;
            case GREEN_6:
                canvas.setColor(Colors.get(Color.green));
                break;
            case YELLOW_7:
                canvas.setColor(Colors.get(Color.yellow));
                break;
            case PURPLE_8:
                canvas.setColor(Colors.get(new Color(210, 0, 255)));
                break;
            case GREY_10:
                canvas.setColor(Colors.get(new Color(162, 162, 162)));
                break;
            default:
                break;
        }
    }

    public static void drawSelectLine(Canvas canvas, LineSegment s, Camera camera) {
        canvas.setColor(Colors.get(Color.green));

        LineSegment s_tv = camera.object2TV(s);

        //なぜEpsilon.UNKNOWN_0000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため
        // TODO: check if adding 1e-6 is really necessary
        Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);

        canvas.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
    }

    public static void drawAuxLiveLine(Canvas canvas, LineSegment as, Camera camera, float lineWidth, int pointSize, float f_h_WireframeLineWidth) {
        setColor(canvas, as.getColor());

        Graphics2D g2 = (Graphics2D) canvas;

        LineSegment s_tv = camera.object2TV(as);
        Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);//なぜEpsilon.UNKNOWN_0000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

        canvas.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線

        if (lineWidth < 2.0f) {//Draw a square at the vertex
            canvas.setColor(Colors.get(Color.gray));
            canvas.fillRect((int) a.getX() - pointSize, (int) a.getY() - pointSize, 2 * pointSize + 1, 2 * pointSize + 1); //正方形を描く//canvas.fillRect(10, 10, 100, 50);長方形を描く
            canvas.fillRect((int) b.getX() - pointSize, (int) b.getY() - pointSize, 2 * pointSize + 1, 2 * pointSize + 1); //正方形を描く

            canvas.setColor(Colors.get(Color.black));
            canvas.drawRect((int) a.getX() - pointSize, (int) a.getY() - pointSize, 2 * pointSize + 1, 2 * pointSize + 1);
            canvas.drawRect((int) b.getX() - pointSize, (int) b.getY() - pointSize, 2 * pointSize + 1, 2 * pointSize + 1);
        }

        if (lineWidth >= 2.0f) {//  Thick line
            g2.setStroke(new BasicStroke(1.0f + f_h_WireframeLineWidth % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状

            if (pointSize != 0) {
                double d_width = (double) lineWidth / 2.0 + (double) pointSize;

                canvas.setColor(Colors.get(Color.gray));
                g2.fill(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                canvas.setColor(Colors.get(Color.black));
                g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                canvas.setColor(Colors.get(Color.gray));
                g2.fill(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                canvas.setColor(Colors.get(Color.black));
                g2.draw(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
            }

            g2.setStroke(new BasicStroke(f_h_WireframeLineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状

        }
    }

    public static void drawCircle(Canvas canvas, Circle circle, Camera camera, float lineWidth, int pointSize) {
        Point a = camera.object2TV(circle.determineCenter());//この場合のaは描画座標系での円の中心の位置

        Graphics2D g2 = (Graphics2D) canvas;
        g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状

        if (circle.getCustomized() == 0) {
            setColor(canvas, circle.getColor());
        } else if (circle.getCustomized() == 1) {
            canvas.setColor(circle.getCustomizedColor());
        }

        //円周の描画
        double d_width = circle.getR() * camera.getCameraZoomX();//d_habaは描画時の円の半径。なお、camera.get_camera_bairitsu_x()＝camera.get_camera_bairitsu_y()を前提としている。
        g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

        a = camera.object2TV(circle.determineCenter());//この場合のaは描画座標系での円の中心の位置

        g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
        canvas.setColor(Colors.get(new Color(0, 255, 255, 255)));

        //円の中心の描画
        if (lineWidth < 2.0f) {//中心の黒い正方形を描く
            canvas.setColor(Colors.get(Color.black));
            canvas.fillRect((int) a.getX() - pointSize, (int) a.getY() - pointSize, 2 * pointSize + 1, 2 * pointSize + 1); //正方形を描く//canvas.fillRect(10, 10, 100, 50);長方形を描く
        }

        if (lineWidth >= 2.0f) {//  太線指定時の中心を示す黒い小円を描く
            g2.setStroke(new BasicStroke(1.0f + lineWidth % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
            if (pointSize != 0) {
                d_width = (double) lineWidth / 2.0 + (double) pointSize;


                canvas.setColor(Colors.get(Color.white));
                g2.fill(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

                canvas.setColor(Colors.get(Color.black));
                g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
            }
        }
    }

    public static void drawAuxLine(Canvas canvas, LineSegment s, Camera camera, float lineWidth, int pointSize, boolean useRoundedEnds) {
        Graphics2D g2 = (Graphics2D) canvas;
        g2.setStroke(new BasicStroke(lineWidth, useRoundedEnds? BasicStroke.CAP_ROUND : BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状

        if (s.getCustomized() == 0) {
            setColor(canvas, s.getColor());
        } else if (s.getCustomized() == 1) {
            canvas.setColor(s.getCustomizedColor());
        }

        LineSegment s_tv = camera.object2TV(s);
        Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);//なぜEpsilon.UNKNOWN_0000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

        canvas.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線

        if (Epsilon.high.eq0(lineWidth) || pointSize == 0) {
            return;
        }
        if (lineWidth < 2.0f) {//頂点の黒い正方形を描く
            drawVertex(g2, a, pointSize);
            drawVertex(g2, b, pointSize);
        }

        if (lineWidth >= 2.0f) {//  太線
            g2.setStroke(new BasicStroke(1.0f + lineWidth % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
            double d_width = (double) lineWidth / 2.0 + (double) pointSize;

            canvas.setColor(Colors.get(Color.white));
            g2.fill(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));


            canvas.setColor(Colors.get(Color.gray));
            g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

            canvas.setColor(Colors.get(Color.white));
            g2.fill(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

            canvas.setColor(Colors.get(Color.gray));
            g2.draw(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
        }
    }

    public static void drawCurve(Canvas canvas, GeneralPath curve, float lineWidth) {
        Graphics2D g2 = (Graphics2D) canvas;
        g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g2.draw(curve);
    }

    public static void drawLineStep(Canvas canvas, LineSegment s, Camera camera, float lineWidth, boolean gridInputAssist) {
        Graphics2D g2 = (Graphics2D) canvas;
        setColor(canvas, s.getColor());
        g2.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状

        LineSegment s_tv = camera.object2TV(s);
        Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);//The reason for adding Epsilon.UNKNOWN_0000001 is to prevent the original fold line from being affected by the new fold line when drawing on the display.


        canvas.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
        int i_width_nyuiiryokuji = 3;
        if (gridInputAssist) {
            i_width_nyuiiryokuji = 2;
        }

        switch (s.getActive()) {
            case ACTIVE_A_1:
                canvas.fillOval((int) a.getX() - i_width_nyuiiryokuji, (int) a.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                break;
            case ACTIVE_B_2:
                canvas.fillOval((int) b.getX() - i_width_nyuiiryokuji, (int) b.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                break;
            case ACTIVE_BOTH_3:
                canvas.fillOval((int) a.getX() - i_width_nyuiiryokuji, (int) a.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                canvas.fillOval((int) b.getX() - i_width_nyuiiryokuji, (int) b.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円
                break;
            default:
                break;
        }
    }

    public static void drawStepVertex(Graphics2D canvas, Point p, LineColor color, Camera camera, boolean gridInputAssist) {
        setColor(canvas, color);
        Point a = camera.object2TV(p);
        int i_width_nyuiiryokuji = 3;
        if (gridInputAssist) {
            i_width_nyuiiryokuji = 2;
        }

        canvas.fillOval((int) a.getX() - i_width_nyuiiryokuji, (int) a.getY() - i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji, 2 * i_width_nyuiiryokuji); //円

    }

    public static void drawLineCandidate(Canvas canvas, LineSegment s, Camera camera, int pointSize) {
        setColor(canvas, s.getColor());

        LineSegment s_tv = camera.object2TV(s);
        Point a = new Point(s_tv.determineAX() + Epsilon.UNKNOWN_1EN6, s_tv.determineAY() + Epsilon.UNKNOWN_1EN6);
        Point b = new Point(s_tv.determineBX() + Epsilon.UNKNOWN_1EN6, s_tv.determineBY() + Epsilon.UNKNOWN_1EN6);//なぜEpsilon.UNKNOWN_0000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

        canvas.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY()); //直線
        int i_width = pointSize + 5;

        switch (s.getActive()) {
            case ACTIVE_A_1:
                canvas.drawLine((int) a.getX() - i_width, (int) a.getY(), (int) a.getX() + i_width, (int) a.getY()); //直線
                canvas.drawLine((int) a.getX(), (int) a.getY() - i_width, (int) a.getX(), (int) a.getY() + i_width); //直線
                break;
            case ACTIVE_B_2:
                canvas.drawLine((int) b.getX() - i_width, (int) b.getY(), (int) b.getX() + i_width, (int) b.getY()); //直線
                canvas.drawLine((int) b.getX(), (int) b.getY() - i_width, (int) b.getX(), (int) b.getY() + i_width); //直線
                break;
            case ACTIVE_BOTH_3:
                canvas.drawLine((int) a.getX() - i_width, (int) a.getY(), (int) a.getX() + i_width, (int) a.getY()); //直線
                canvas.drawLine((int) a.getX(), (int) a.getY() - i_width, (int) a.getX(), (int) a.getY() + i_width); //直線

                canvas.drawLine((int) b.getX() - i_width, (int) b.getY(), (int) b.getX() + i_width, (int) b.getY()); //直線
                canvas.drawLine((int) b.getX(), (int) b.getY() - i_width, (int) b.getX(), (int) b.getY() + i_width); //直線
                break;
            default:
                break;
        }
    }

    public static void drawCircleStep(Canvas canvas, Circle c, Camera camera) {
        Graphics2D g2 = (Graphics2D) canvas;
        setColor(canvas, c.getColor());
        Point a = camera.object2TV(c.determineCenter());//この場合のs_tvは描画座標系での円の中心の位置
        a = new Point(a.getX() + Epsilon.UNKNOWN_1EN6, a.getY() + Epsilon.UNKNOWN_1EN6);//なぜEpsilon.UNKNOWN_0000001を足すかというと,ディスプレイに描画するとき元の折線が新しい折線に影響されて動いてしまうのを防ぐため

        double d_width = c.getR() * camera.getCameraZoomX();//d_habaは描画時の円の半径。なお、camera.get_camera_bairitsu_x()＝camera.get_camera_bairitsu_y()を前提としている。

        g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
    }

    private static final float[] dash_M1 = {10.0f, 3.0f, 3.0f, 3.0f};//一点鎖線
    private static final float[] dash_M2 = {10.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f};//二点鎖線
    private static final float[] dash_V = {8.0f, 8.0f};//破線

    private static final Point defaultMove = new Point(Epsilon.UNKNOWN_1EN6, Epsilon.UNKNOWN_1EN6);

    public static void drawCpLine(Canvas canvas, LineSegment s, Camera camera, LineStyle lineStyle, float lineWidth, int pointSize, int clipX, int clipY, boolean useRoundedEnds) {

        Point a = camera.object2TV(s.getA()).move(defaultMove);
        Point b = camera.object2TV(s.getB()).move(defaultMove);

        int aflag = cohenSutherlandRegion(clipX, clipY, a);
        if (aflag != CENTER) {
            int bflag = cohenSutherlandRegion(clipX, clipY, b);
            if ((aflag & bflag) != CENTER) {
                return;
            }
        }
        int cap = useRoundedEnds? BasicStroke.CAP_ROUND : BasicStroke.CAP_BUTT;
        Graphics2D g2 = (Graphics2D) canvas;
        switch (lineStyle) {
            case COLOR:
                setColor(canvas, s.getColor());
                g2.setStroke(new BasicStroke(lineWidth, cap, BasicStroke.JOIN_MITER));//基本指定A　　線の太さや線の末端の形状
                break;
            case BLACK_WHITE:
                setColor(canvas, s.getColor());
                if (s.getColor() == LineColor.BLACK_0) {
                    g2.setStroke(new BasicStroke(lineWidth, cap, BasicStroke.JOIN_MITER));
                }
                if (s.getColor() == LineColor.RED_1) {
                    setColor(canvas, LineColor.BLACK_0);
                    g2.setStroke(new BasicStroke(lineWidth, cap, BasicStroke.JOIN_MITER));
                }
                if (s.getColor() == LineColor.BLUE_2) {
                    setColor(canvas, LineColor.GREY_10);
                    g2.setStroke(new BasicStroke(lineWidth, cap, BasicStroke.JOIN_MITER));
                }
                break;
            case COLOR_AND_SHAPE:
                setColor(canvas, s.getColor());
                if (s.getColor() == LineColor.BLACK_0) {
                    g2.setStroke(new BasicStroke(lineWidth, cap, BasicStroke.JOIN_MITER));
                }//基本指定A　　線の太さや線の末端の形状
                if (s.getColor() == LineColor.RED_1) {
                    g2.setStroke(new BasicStroke(lineWidth, cap, BasicStroke.JOIN_MITER, 10.0f, dash_M1, 0.0f));
                }//一点鎖線//線の太さや線の末端の形状
                if (s.getColor() == LineColor.BLUE_2) {
                    g2.setStroke(new BasicStroke(lineWidth, cap, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                }//破線//線の太さや線の末端の形状
                break;
            case BLACK_ONE_DOT:
                if (s.getColor() == LineColor.BLACK_0) {
                    g2.setStroke(new BasicStroke(lineWidth, cap, BasicStroke.JOIN_MITER));
                }//基本指定A　　線の太さや線の末端の形状
                if (s.getColor() == LineColor.RED_1) {
                    g2.setStroke(new BasicStroke(lineWidth, cap, BasicStroke.JOIN_MITER, 10.0f, dash_M1, 0.0f));
                }//一点鎖線//線の太さや線の末端の形状
                if (s.getColor() == LineColor.BLUE_2) {
                    g2.setStroke(new BasicStroke(lineWidth, cap, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                }//破線//線の太さや線の末端の形状
                break;
            case BLACK_TWO_DOT:
                if (s.getColor() == LineColor.BLACK_0) {
                    g2.setStroke(new BasicStroke(lineWidth, cap, BasicStroke.JOIN_MITER));
                }//基本指定A　　線の太さや線の末端の形状
                if (s.getColor() == LineColor.RED_1) {
                    g2.setStroke(new BasicStroke(lineWidth, cap, BasicStroke.JOIN_MITER, 10.0f, dash_M2, 0.0f));
                }//二点鎖線//線の太さや線の末端の形状
                if (s.getColor() == LineColor.BLUE_2) {
                    g2.setStroke(new BasicStroke(lineWidth, cap, BasicStroke.JOIN_MITER, 10.0f, dash_V, 0.0f));
                }//破線//線の太さや線の末端の形状
                break;
        }

        g2.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY());

        if (Epsilon.high.eq0(lineWidth) || pointSize == 0) {
            return;
        }
        if (lineWidth < 2.0f) {//頂点の黒い正方形を描く
            drawVertex(g2, a, pointSize);
            if (a.distance(b) > 1) {
                drawVertex(g2, b, pointSize);
            }
        } else if (lineWidth >= 2.0f) {//  太線
            g2.setStroke(new BasicStroke(1.0f + lineWidth % 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状、ここでは折線の端点の線の形状の指定
            double d_width = (double) lineWidth / 2.0 + (double) pointSize;

            canvas.setColor(Colors.get(Color.gray));
            g2.fill(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

            canvas.setColor(Colors.get(Color.black));
            g2.draw(new Ellipse2D.Double(a.getX() - d_width, a.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

            canvas.setColor(Colors.get(Color.gray));
            g2.fill(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));

            canvas.setColor(Colors.get(Color.black));
            g2.draw(new Ellipse2D.Double(b.getX() - d_width, b.getY() - d_width, 2.0 * d_width, 2.0 * d_width));
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
    public static void drawViolation(Graphics2D canvas, Point p, FlatFoldabilityViolation violation, int transparency, boolean useAdvanced) {
        canvas.setColor(Colors.get(new Color(255, 0, 147, transparency)));

        if (!useAdvanced) {
            canvas.setColor(Colors.get(new Color(255, 0, 147, transparency)));
            canvas.fillOval((int) p.getX() - 11, (int) p.getY() - 11, 23, 23);
            return;
        }
        Color c;
        switch (violation.getColor()) {
            case NOT_ENOUGH_MOUNTAIN:
                c = Colors.get(Color.RED);
                break;
            case NOT_ENOUGH_VALLEY:
                c = Colors.get(Color.BLUE);
                break;
            case UNKNOWN:
                c = Colors.get(new Color(255, 0, 147));
                break;
            default:
                c = Colors.get(Color.GRAY);
                break;
        }
        Color actualColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), transparency);
        canvas.setColor(actualColor);
        canvas.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        switch (violation.getViolatedRule()) {
            case NUMBER_OF_FOLDS:
                drawTriangleAroundPoint(canvas, p);
                break;
            case ANGLES:
                if (violation.getColor() == FlatFoldabilityViolation.Color.CORRECT) {
                    canvas.drawOval((int) p.getX() - 11, (int) p.getY() - 11, 23, 23);
                } else {
                    canvas.fillOval((int) p.getX() - 11, (int) p.getY() - 11, 23, 23);
                }
                break;
            case MAEKAWA:
                canvas.fillRect((int) p.getX() - 9, (int) p.getY() - 9, 19, 19);
                break;
            case LITTLE_BIG_LITTLE:
                LittleBigLittleViolation lViolation;
                if (violation instanceof LittleBigLittleViolation) {
                    lViolation = (LittleBigLittleViolation) violation;
                } else {
                    Logger.warn("LITTLE_BIG_LITTLE violation was not of type LittleBigLittleViolation");
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
                    int[] xCoords = new int[]{
                            (int) p.getX(),
                            (int) (p.getX() + current.determineDeltaX()),
                            (int) (p.getX() + next.determineDeltaX()),
                    };
                    int[] yCoords = new int[]{
                            (int) p.getY(),
                            (int) (p.getY() + current.determineDeltaY()),
                            (int) (p.getY() + next.determineDeltaY()),
                    };
                    canvas.drawPolygon(xCoords, yCoords, 3);
                    if (violating[i]) {
                        canvas.fillPolygon(xCoords, yCoords, 3);
                    }
                }
                break;
            case NONE:
                break;
        }
    }

    private static void drawTriangleAroundPoint(Graphics2D canvas, Point p) {
        canvas.fillPolygon(new int[]{
                (int) p.getX(),
                (int) p.getX() - 10,
                (int) p.getX() + 10
        }, new int[]{
                (int) p.getY() - 9,
                (int) p.getY() + 7,
                (int) p.getY() + 7
        }, 3);
    }
}
