package com.example.oriedita_core.origami.crease_pattern.worker;

import android.util.Log;
import com.example.oriedita_core.origami.crease_pattern.FoldingException;
import com.example.oriedita_core.origami.crease_pattern.LineSegmentSet;
import com.example.oriedita_core.origami.crease_pattern.OritaCalc;
import com.example.oriedita_core.origami.crease_pattern.worker.PointSet;
import com.example.oriedita_core.origami.crease_pattern.elements.LineColor;
import com.example.oriedita_core.origami.crease_pattern.elements.Point;
import com.example.oriedita_core.origami.data.ListArray;
import com.example.oriedita_core.origami.data.quadTree.QuadTree;
import com.example.oriedita_core.origami.data.quadTree.adapter.InitialAdapter;
import com.example.oriedita_core.origami.data.quadTree.adapter.PointSetFaceAdapter;
import com.example.oriedita_core.origami.data.quadTree.adapter.PointSetPointAdapter;
import com.example.oriedita_core.origami.data.quadTree.collector.PointCollector;
import com.example.oriedita_core.origami.folding.util.AverageCoordinates;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Рабочий класс для создания каркасной модели оригами.
 * Этот класс ремесленника паттерна складок имеет только один PointSet как паттерн складок.
 * PointSet, полученный в результате складывания и т.д., должен возвращаться наружу и не храниться внутри.
 */
public class WireFrame_Worker {
    /** Критерий для определения радиуса окружностей на обоих концах прямой линии базовой структуры ветвления и близости ветвей к различным точкам */
    double r;
    /** Набор точек - развертка */
    PointSet pointSet = new PointSet();
    
    // Определение переменных, используемых в складывании и рисовании
    
    /** Указывает, насколько далеко поверхность находится от опорной поверхности. Введите значение типа 1 - рядом с опорной плоскостью, 2 - рядом с опорной плоскостью, и 3 рядом с ней */
    int[] facePosition;
    /** ID начальной грани */
    int startingFaceId = -1;
    /** ID поверхности (сторона опорной поверхности) рядом с определенной поверхностью */
    int[] nextFaceId;
    /** ID линии между одной стороной и следующей стороной (сторона опорной плоскости) */
    int[] associatedLineId;
    /** Хранит позицию точки при складывании */
    AverageCoordinates[] tnew;

    /**
     * Конструктор рабочего класса каркасной модели
     * @param r0 критерий радиуса для определения близости точек
     */
    public WireFrame_Worker(double r0) {
        r = r0;
    }

    /**
     * Сбрасывает состояние рабочего класса к значениям по умолчанию
     */
    public void reset() {
        r = 3.0;
        pointSet.reset();
    }

    /**
     * Настраивает массивы для работы с указанным количеством точек, линий и граней
     * @param numPoints количество точек
     * @param numLines количество линий
     * @param numFaces количество граней
     */
    private void configure(int numPoints, int numLines, int numFaces) {
        tnew = new AverageCoordinates[numPoints + 1];
        for (int i = 0; i <= numPoints; i++) {
            tnew[i] = new AverageCoordinates();
        }
        facePosition = new int[numFaces + 1];
        nextFaceId = new int[numFaces + 1];         // ID поверхности (сторона опорной поверхности) рядом с определенной поверхностью
        associatedLineId = new int[numFaces + 1];   // ID линии между одной поверхностью и следующей поверхностью (сторона опорной поверхности)
    }

    /**
     * Устанавливает ID начальной грани для складывания
     * @param i ID грани для установки
     * @return установленный ID начальной грани
     */
    public int setStartingFaceId(int i) {
        startingFaceId = i;

        if (startingFaceId > pointSet.getNumFaces()) {
            startingFaceId = pointSet.getNumFaces();
        }
        if (startingFaceId < 1) {
            startingFaceId = pointSet.inside(new Point(0, 0));
            if (startingFaceId < 1) {
                startingFaceId = 1;
            }
        }

        return startingFaceId;
    }


    /**
     * Получает общее количество линий в наборе точек
     * @return количество линий
     */
    public int getNumLines() {
        return pointSet.getNumLines();
    }

    /**
     * Получает цвет линии набора точек (когда набор точек рассматривается как развертка, этот цвет представляет гору-долину)
     * @param i индекс линии
     * @return цвет линии
     */
    public LineColor getColor(int i) {
        return pointSet.getColor(i);
    }

    /**
     * Получает позицию грани относительно опорной поверхности
     * @param i индекс грани
     * @return позиция грани
     */
    public int getIFacePosition(int i) {
        return facePosition[i];
    }

    /**
     * Оценка складывания (здесь можно создать каркасную диаграмму, которая не учитывает перекрытие поверхностей)
     * @return набор точек после складывания
     * @throws InterruptedException если поток прерван
     * @throws FoldingException если произошла ошибка при складывании
     */
    public PointSet folding() throws InterruptedException, FoldingException {
        // Код, который был здесь ранее, идентичен getFacePositions
        PointSet pointSet = getFacePositions();

        Log.i("TAG", "Находим позицию точки при складывании.");
        // Находим позицию точки при складывании.
        // Если точка включена в грань im
        // Находим, куда перемещаться, когда паттерн складок складывается путем перемещения грани im.

        QuadTree qt = new QuadTree(new PointSetFaceAdapter(pointSet));
        Log.i("TAG", "Начинаем находить позицию точки при складывании");
        for (int it = 1; it <= this.pointSet.getNumPoints(); it++) {
            tnew[it].reset();
            for (int im : qt.collect(new PointCollector(pointSet.getPoint(it)))) {
                if (pointSet.pointInFaceBorder(im, it)) { // c.Ten_moti_hantei возвращает 1, если граница грани [im] содержит точку [it], 0 если не содержит.
                    tnew[it].addPoint(fold_movement(it, im));
                    pointSet.setPoint(it, tnew[it].getAveragePoint());
                }
            }
        }
        Log.i("TAG", "Завершили поиск позиции точки при складывании");

        return pointSet;
    }

    /**
     * Функция, которая находит позицию назначения, когда точка it складывается как член поверхности im
     * @param it индекс точки
     * @param im индекс грани
     * @return позиция точки после складывания
     */
    private Point fold_movement(int it, int im) {
        Point p = pointSet.getPoint(it);
        int idestination_faceId = im; // Номер id первой грани. Отныне мы будем следовать плоскостям, смежным с опорной плоскостью.
        while (idestination_faceId != startingFaceId) {
            p = lineSymmetry_point_determine(associatedLineId[idestination_faceId], p);
            idestination_faceId = nextFaceId[idestination_faceId];
        }
        return p;
    }

    /**
     * Оценка складывания (здесь можно создать каркасную диаграмму, которая не учитывает перекрытие поверхностей)
     * @return набор точек с позициями граней
     * @throws InterruptedException если поток прерван
     */
    public PointSet getFacePositions() throws InterruptedException {
        PointSet cn = new PointSet();    // Развертка
        cn.configure(pointSet.getNumPoints(), pointSet.getNumLines(), pointSet.getNumFaces());
        cn.set(pointSet);

        ListArray map = pointSet.getPointToLineMap();
        QuadTree qt = new QuadTree(new PointSetFaceAdapter(pointSet));

        for (int i = 0; i <= pointSet.getNumFaces(); i++) {
            nextFaceId[i] = 0;
            associatedLineId[i] = 0;
            facePosition[i] = 0;
        }
        // Понимаем позиционные отношения между гранями в подготовке к складыванию
        Log.i("TAG", "Понимаем позиционные отношения между гранями в подготовке к складыванию");
        facePosition[startingFaceId] = 1;

        int depth = 1;
        int remaining_facesTotal = pointSet.getNumFaces() - 1;

        // Tsai: Я не уверен, имеет ли значение порядок, поэтому просто подстраховываюсь здесь.
        SortedSet<Integer> currentRound = new TreeSet<>();
        currentRound.add(startingFaceId);

        while (remaining_facesTotal > 0) {
            SortedSet<Integer> nextRound = new TreeSet<>();
            for (int i : currentRound) {
                for (int j : qt.getPotentialCollision(i, 0)) {
                    if (facePosition[j] != 0) continue;
                    int mth = pointSet.findAdjacentLine(i, j, map);
                    if (mth > 0) {
                        nextRound.add(j);
                        facePosition[j] = depth + 1;
                        nextFaceId[j] = i;
                        associatedLineId[j] = mth;
                        remaining_facesTotal--;
                    }
                }
            }
            currentRound = nextRound;
            depth++;

            if (Thread.interrupted()) throw new InterruptedException();
        }

        return cn;
    }

    /**
     * Учитывая id линии и любую точку, возвращает точку, которая является осесимметричной данной точке относительно соответствующей линии
     * @param lineId id линии
     * @param point точка для отражения
     * @return отраженная точка
     */
    private Point lineSymmetry_point_determine(int lineId, Point point) {
        return OritaCalc.findLineSymmetryPoint(pointSet.getBeginPointFromLineId(lineId), pointSet.getEndPointFromLineId(lineId), point);
    }

    /**
     * Получает общее количество точек
     * @return количество точек
     */
    public int getPointsTotal() {
        return pointSet.getNumPoints();
    }

    /**
     * Устанавливает набор точек
     * @param ts набор точек для установки
     */
    public void set(PointSet ts) {
        configure(ts.getNumPoints(), ts.getNumLines(), ts.getNumFaces());
        pointSet.configure(ts.getNumPoints(), ts.getNumLines(), ts.getNumFaces());
        pointSet.set(ts);
    }

    /**
     * Получает набор точек
     * @return текущий набор точек
     */
    public PointSet get() {
        return pointSet;
    }

    /**
     * Получает хранилище линий
     * @return экземпляр базовой структуры ветвления
     */
    public LineSegmentSet getLineStore() {
        return new LineSegmentSet(pointSet);
    }

    /**
     * Устанавливает набор сегментов линий без генерации граней
     * @param lineSegmentSet набор сегментов линий
     * @throws InterruptedException если поток прерван
     */
    public void setLineSegmentSetWithoutFaceOccurence(LineSegmentSet lineSegmentSet) throws InterruptedException {
        reset();
        definePointSet(lineSegmentSet);
        defineLines(lineSegmentSet);
    }

    /**
     * Устанавливает набор сегментов линий с генерацией граней
     * @param lineSegmentSet набор сегментов линий
     * @throws InterruptedException если поток прерван
     */
    public void setLineSegmentSet(LineSegmentSet lineSegmentSet) throws InterruptedException {
        reset();

        // Сначала определяем точки в PointSet
        definePointSet(lineSegmentSet);

        // Затем определяем линии в PointSet
        defineLines(lineSegmentSet);

        // Затем генерируем поверхности в PointSet
        pointSet.calculateFaces();
    }

    private void definePointSet(LineSegmentSet lineSegmentSet) throws InterruptedException {
        Log.i("TAG","Набор линий -> Набор точек: Определяем точки в наборе точек");
        boolean found;
        Point ti;

        InitialAdapter adapter = new InitialAdapter(lineSegmentSet, lineSegmentSet.getNumLineSegments()*2);
        QuadTree qt = new QuadTree(adapter);

        for (int i = 0; i < lineSegmentSet.getNumLineSegments(); i++) {
            found = false;
            ti = lineSegmentSet.getA(i);
            for (int j : qt.collect(new PointCollector(ti))) {
                if (OritaCalc.equal(ti, adapter.get(j))) found = true;
            }
            if (!found) {
                adapter.add(ti);
                qt.grow(1);
            }

            found = false;
            ti = lineSegmentSet.getB(i);
            for (int j : qt.collect(new PointCollector(ti))) {
                if (OritaCalc.equal(ti, adapter.get(j))) found = true;
            }
            if (!found) {
                adapter.add(ti);
                qt.grow(1);
            }

            if (Thread.interrupted()) throw new InterruptedException();
        }

        int numPoints = adapter.getCount();
        Log.i("TAG","Общее количество точек addPointNum = ");
        Log.i("TAG", String.valueOf(numPoints));

        int numLines = lineSegmentSet.getNumLineSegments();

        // Формула Эйлера говорит F - E + V = 1 (для ограниченных граней)
        int supposedNumFaces = numLines - numPoints + 1;
        /*
         * Однако числа могут быть неточными из-за ошибок округления (см. комментарии в
         * PointSet), поэтому мы добавляем немного больше на всякий случай. Функция "max"
         * здесь частично для совместимости со старыми тестами, но также для обеспечения
         * того, что дополнительного места достаточно.
         */
        int estimatedNumFaces = supposedNumFaces + Math.max(supposedNumFaces / 100, 99);

        configure(numPoints, numLines, estimatedNumFaces);
        pointSet.configure(numPoints, numLines, estimatedNumFaces);

        for (int i = 0; i < numPoints; i++) {
            pointSet.addPoint(adapter.get(i));
        }
    }

    private void defineLines(LineSegmentSet lineSegmentSet) throws InterruptedException {
        Log.i("TAG","Набор линий -> Набор точек: Определяем линию в наборе точек");

        QuadTree qt = new QuadTree(new PointSetPointAdapter(pointSet));
        for (int n = 0; n < lineSegmentSet.getNumLineSegments(); n++) {
            int start = 0, end = 0;
            for (int i : qt.collect(new PointCollector(lineSegmentSet.getA(n)))) {
                if (OritaCalc.equal(lineSegmentSet.getA(n), pointSet.getPoint(i))) {
                    start = i;
                    break;
                }
            }
            for (int i : qt.collect(new PointCollector(lineSegmentSet.getB(n)))) {
                if (OritaCalc.equal(lineSegmentSet.getB(n), pointSet.getPoint(i))) {
                    end = i;
                    break;
                }
            }
            pointSet.addLine(start, end, lineSegmentSet.getColor(n));

            if (Thread.interrupted()) throw new InterruptedException();
        }

        Log.i("TAG", "Общее количество линий = " + pointSet.getNumLines());
    }

    /**
     * Возвращает faceId с меньшим faceId среди граней, содержащих линию lineId как границу (максимум две грани). Возвращает 0, если нет грани, содержащей линию как границу
     */
    public int lineInFaceBorder_min_request(int lineId) {
        return pointSet.lineInFaceBorder_min_lookup(lineId);
    }

    /**
     * Возвращает faceId с большим faceId среди граней, содержащих линию lineId как границу (максимум две грани). Возвращает 0, если нет грани, содержащей линию как границу
     */
    public int lineInFaceBorder_max_request(int lineId) {
        return pointSet.lineInFaceBorder_max_lookup(lineId);
    }


    public int getSelectedPointsNum() {
        return pointSet.getSelectedPointsNum();
    }

    public void setPointStateTrue(int i) {
        pointSet.setPointStateTrue(i);
    }

    public void setAllPointStateFalse() {
        pointSet.setAllPointStateFalse();
    }

    public void changePointState(int i) {
        pointSet.changePointState(i);
    }

    /**
     * Получает, выбрана ли i-я точка как 0 или 1.
     */
    public boolean getPointState(int i) {
        return pointSet.getPointState(i);
    }

    public Point getPoint(int i) {
        return pointSet.getPoint(i);
    }
}
