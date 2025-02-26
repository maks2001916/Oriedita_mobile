package oriedita.editor.handler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tinylog.Logger;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.canvas.MouseMode;
import oriedita.editor.databinding.CanvasModel;
import origami.Epsilon;
import origami.crease_pattern.OritaCalc;
import origami.crease_pattern.elements.LineColor;
import origami.crease_pattern.elements.LineSegment;
import origami.crease_pattern.elements.Point;
import origami.crease_pattern.elements.StraightLine;
import origami.crease_pattern.util.CreasePattern_Worker_Toolbox;
import origami.folding.util.SortingBox;

@ApplicationScoped
@Handles(MouseMode.CONTINUOUS_SYMMETRIC_DRAW_52)
public class MouseHandlerContinuousSymmetricDraw extends BaseMouseHandlerInputRestricted {

    private final CreasePattern_Worker d;
    private final CanvasModel canvasModel;
    private final CreasePattern_Worker_Toolbox toolbox;

    //マウス操作(ボタンを押したとき)時の作業
    public void mousePressed(Point p0) {
        Logger.info("i_egaki_dankai=" + d.getLineStep().size());

        Point p = d.getCamera().TV2object(p0);
        Point closest_point = d.getClosestPoint(p);

        if (p.distance(closest_point) < d.getSelectionDistance()) {
            d.lineStepAdd(new LineSegment(closest_point, closest_point, d.getLineColor()));
        } else {
            d.lineStepAdd(new LineSegment(p, p, d.getLineColor()));
        }

        Logger.info("i_egaki_dankai=" + d.getLineStep().size());
    }

    //マウス操作(ドラッグしたとき)を行う関数
    public void mouseDragged(Point p0) {
    }

    //マウス操作(ボタンを離したとき)を行う関数
    public void mouseReleased(Point p0) {
        if (d.getLineStep().size() == 2) {
            continuous_folding_new(d.getLineStep().get(0).getA(), d.getLineStep().get(1).getA(), null);

            LineColor lineType = d.getLineColor();
            for (int i = 2; i < d.getLineStep().size(); i++) {
                LineSegment lineSegment = d.getLineStep().get(i).clone();
                lineSegment.setColor(lineType);
                lineType = lineType.changeMV();
                d.addLineSegment(lineSegment);
            }

            d.record();

            d.getLineStep().clear();
        }
    }

    @Inject
    public MouseHandlerContinuousSymmetricDraw(@Named("mainCreasePattern_Worker") CreasePattern_Worker d, CanvasModel canvasModel) {
        this.d = d;
        this.canvasModel = canvasModel;
        this.toolbox = new CreasePattern_Worker_Toolbox(d.getFoldLineSet());
    }

    public void continuous_folding_new(Point a, Point b, Point start) {//An improved version of continuous folding.
        canvasModel.markDirty();

        //ベクトルab(=s0)を点aからb方向に、最初に他の折線(直線に含まれる線分は無視。)と交差するところまで延長する

        //与えられたベクトルabを延長して、それと重ならない折線との、最も近い交点までs_stepとする。
        //補助活線は無視する
        //与えられたベクトルabを延長して、それと重ならない折線との、最も近い交点までs_stepとする


        //「再帰関数における、種の発芽」交点がない場合「種」が成長せずリターン。

        toolbox.lengthenUntilIntersectionCalculateDisregardIncludedLineSegment_new(a, b);//一番近い交差点を見つけて各種情報を記録
        if (toolbox.getLengthenUntilIntersectionFlg_new() == StraightLine.Intersection.NONE_0) {
            return;
        }

        LineSegment s = new LineSegment(toolbox.getLengthenUntilIntersectionLineSegment_new());
        d.lineStepAdd(s);
        s.setActive(LineSegment.ActiveState.ACTIVE_BOTH_3);
        if (start != null && Epsilon.high.eq0(start.distance(s.getB()))) {
            return;
        }
        if (toolbox.getLengthenUntilIntersectionFirstLineSegment_new().getColor() == LineColor.BLACK_0) {
            return; // stop when hitting edge of paper
        }
        if (start == null) {
            start = s.getB();
        }

        Logger.info("20201129 saiki repaint ");

        //「再帰関数における、種の生成」求めた最も近い交点から次のベクトル（＝次の再帰関数に渡す「種」）を発生する。最も近い交点が折線とＸ字型に交差している点か頂点かで、種のでき方が異なる。

        //最も近い交点が折線とＸ字型の場合無条件に種を生成し、散布。
        if (toolbox.getLengthenUntilIntersectionFlg_new() == StraightLine.Intersection.INTERSECT_X_1) {
            LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment(
                    toolbox.getLengthenUntilIntersectionFirstLineSegment_new());
            Point new_a = toolbox.getLengthenUntilIntersectionPoint_new();//Ten new_aは最も近い交点
            Point new_b = OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a);//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

            continuous_folding_new(new_a, new_b, start);//種の散布
            return;
        }

        //最も近い交点が頂点（折線端末）の場合、頂点に集まる折線の数で条件分けして、種を生成し散布、
        if ((toolbox.getLengthenUntilIntersectionFlg_new() == StraightLine.Intersection.INTERSECT_T_A_21)
                || (toolbox.getLengthenUntilIntersectionFlg_new() == StraightLine.Intersection.INTERSECT_T_B_22)) {//Logger.info("20201129 21 or 22");

            StraightLine tyoku1 = new StraightLine(a, b);
            StraightLine.Intersection intersection;

            SortingBox<LineSegment> t_m_s_nbox = new SortingBox<>();

            t_m_s_nbox.set(d.getFoldLineSet().get_SortingBox_of_vertex_b_surrounding_foldLine(toolbox.getLengthenUntilIntersectionLineSegment_new().getA(), toolbox.getLengthenUntilIntersectionLineSegment_new().getB()));

            if (t_m_s_nbox.getTotal() == 2) {
                intersection = tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(1));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    return;
                }

                intersection = tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(2));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    return;
                }

                StraightLine tyoku2 = new StraightLine(t_m_s_nbox.getValue(1));
                intersection = tyoku2.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(2));
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment(
                            toolbox.getLengthenUntilIntersectionFirstLineSegment_new());

                    Point new_a = toolbox.getLengthenUntilIntersectionPoint_new();//Ten new_aは最も近い交点
                    Point new_b = OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a);//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                    continuous_folding_new(new_a, new_b, start);//種の散布
                    return;
                }
                return;
            }


            if (t_m_s_nbox.getTotal() == 3) {
                intersection = tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(1));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    StraightLine tyoku2 = new StraightLine(t_m_s_nbox.getValue(2));
                    intersection = tyoku2.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(3));
                    if (intersection == StraightLine.Intersection.INCLUDED_3) {
                        LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment(
                                toolbox.getLengthenUntilIntersectionFirstLineSegment_new());

                        Point new_a = toolbox.getLengthenUntilIntersectionPoint_new();//Ten new_aは最も近い交点
                        Point new_b = OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a);//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        continuous_folding_new(new_a, new_b, start);//種の散布
                        return;
                    }
                }
                //------------------------------------------------
                intersection = tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(2));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    StraightLine tyoku2 = new StraightLine(t_m_s_nbox.getValue(3));
                    intersection = tyoku2.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(1));
                    if (intersection == StraightLine.Intersection.INCLUDED_3) {
                        LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment(
                                toolbox.getLengthenUntilIntersectionFirstLineSegment_new());

                        Point new_a = toolbox.getLengthenUntilIntersectionPoint_new();//Ten new_aは最も近い交点
                        Point new_b = OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a);//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        continuous_folding_new(new_a, new_b, start);//種の散布
                        return;
                    }
                }
                //------------------------------------------------
                intersection = tyoku1.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(3));//0=この直線は与えられた線分と交差しない、1=X型で交差する、2=T型で交差する、3=線分は直線に含まれる。
                if (intersection == StraightLine.Intersection.INCLUDED_3) {
                    StraightLine tyoku2 = new StraightLine(t_m_s_nbox.getValue(1));
                    intersection = tyoku2.lineSegment_intersect_reverse_detail(t_m_s_nbox.getValue(2));
                    if (intersection == StraightLine.Intersection.INCLUDED_3) {
                        LineSegment kousaten_made_nobasi_saisyono_lineSegment = new LineSegment(
                                toolbox.getLengthenUntilIntersectionFirstLineSegment_new());

                        Point new_a = toolbox.getLengthenUntilIntersectionPoint_new();//Ten new_aは最も近い交点
                        Point new_b = OritaCalc.findLineSymmetryPoint(kousaten_made_nobasi_saisyono_lineSegment.getA(), kousaten_made_nobasi_saisyono_lineSegment.getB(), a);//２つの点t1,t2を通る直線に関して、点pの対照位置にある点を求める public Ten oc.sentaisyou_ten_motome(Ten t1,Ten t2,Ten p){

                        continuous_folding_new(new_a, new_b, start);//種の散布
                    }
                }
            }
        }
    }
}
