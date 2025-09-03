package com.example.oriedita_core.origami.crease_pattern.worker;

import android.util.Log;
import com.example.oriedita_core.origami.folding.HierarchyList;
import com.example.oriedita_core.origami.folding.algorithm.AdditionalEstimationAlgorithm;
import com.example.oriedita_core.origami.folding.algorithm.swapping.SubFaceSwappingAlgorithm;
import com.example.oriedita_core.origami.folding.element.SubFace;
import com.example.oriedita_core.origami.folding.util.EquivalenceCondition;
import com.example.oriedita_core.origami.folding.util.IBulletinBoard;
import com.example.oriedita_core.origami.folding.util.SortingBox;


/**
 * Отвечает за вычисление правильного порядка подграней в сложенной фигуре.
 * Класс для работы с иерархией граней при складывании оригами.
 */
public class FoldedFigure_Worker {
    /** Список иерархии граней */
    public final HierarchyList hierarchyList = new HierarchyList();
    /** Рейтинг граней */
    public double[] face_rating;
    /** Коробка сортировки для пар id грани и рейтинга, отсортированных по возрастанию рейтинга */
    public SortingBox<Integer> nbox = new SortingBox<>();
    /** Общее количество подграней */
    public int SubFaceTotal;
    /** Позиция ошибки для отрисовки */
    public EquivalenceCondition errorPos = null;
    /** Целочисленный рейтинг граней */
    private int[] i_face_rating;
    
    // hierarchyList[][] обрабатывается как таблица, объединяющая все отношения верх-низ между гранями
    // в развертке перед складыванием в одну таблицу
    // hierarchyList[i][j] = 1 означает, что грань i находится выше грани j. 0 означает ниже.
    // hierarchyList[i][j] = -50 означает, что грани i и j перекрываются, но отношение верх-низ не определено.
    // hierarchyList[i][j] = -100 означает, что грани i и j не перекрываются.
    
    /** Количество подграней, необходимое для покрытия иерархических отношений граней до приоритетного порядка */
    int SubFace_valid_number;
    /** Максимальное значение количества id граней, которое имеет каждая подгрань. То есть количество слоев в месте с наибольшим перекрытием бумаги */
    public int FaceIdCount_max;
    /** Подграни, полученные из SubFace_figure */
    public SubFace[] s0;
    /** Уменьшенный список подграней для обработки AEA */
    SubFace[] s1;
    /** s - это s1, отсортированный в порядке убывания приоритета */
    public SubFace[] s;
    /** Доска объявлений для логирования */
    private final IBulletinBoard bb;
    
    // Инициализация таблицы верх-низ. Возвращает 0, если в развертке есть ошибка с нечетным количеством линий сгиба из одной вершины.
    // Возвращает 1000, если такой ошибки нет. Возвращает 2, если есть ошибка расширения линий сгиба горы-долины.
    
    /** Количество граней, которые можно ранжировать без других граней сверху */
    int makesuu0no_menno_amount = 0;
    /** Количество граней, которые можно ранжировать только при наличии одной или более других граней сверху */
    int makesuu1ijyouno_menno_amount = 0;
    /** Суммарное количество граней сверху без ранжированных граней */
    private int top_face_id_ga_maketa_kazu_goukei_without_rated_face = 0;

    /** Алгоритм перестановки подграней */
    private SubFaceSwappingAlgorithm swapper;
    /** Режим дополнительного алгоритма оценки */
    private boolean aeaMode;

    /**
     * Конструктор рабочего класса сложенной фигуры
     * @param bb0 доска объявлений для логирования
     */
    public FoldedFigure_Worker(IBulletinBoard bb0) {
        bb = bb0;
        reset();
    }

    /**
     * Сбрасывает состояние рабочего класса
     */
    public void reset() {
        hierarchyList.reset();
        SubFaceTotal = 0;
        SubFace_valid_number = 0;
        FaceIdCount_max = 0;
    }


    /**
     * Получает количество валидных подграней
     * @return количество валидных подграней
     */
    public int getSubFace_valid_number() {
        return SubFace_valid_number;
    }

    /**
     * Переводит состояние перекрытия подграней в следующее состояние.
     * Если текущее состояние перекрытия граней является последним, возвращает 0
     * и состояние перекрытия граней возвращается к начальному.
     * 
     * @param ss индекс подграни
     * @return id подграни, которая изменилась, или 0 если достигнут конец
     * @throws InterruptedException если поток прерван
     */
    public int next(int ss) throws InterruptedException {
        int isusumu; // Когда = 0, SubFace изменяется (образ изменения цифр)
        int subfaceId; // Номер id SubFace, который изменился
        isusumu = 0;
        // Все SubFaces выше ss + 1 устанавливаются в начальные значения. Ошибка возникает, когда количество граней, включенных в SubFace, равно 0.

        for (int i = ss + 1; i <= SubFace_valid_number; i++) {
            s[i].resetPermutationGenerator();
        }
        // Состояние перекрытия поверхностей изменяется по порядку от того, у которого наибольший номер id SubFace.
        subfaceId = ss;
        for (int i = ss; i >= 1 && isusumu == 0; i--) {
            isusumu = s[i].next(s[i].getFaceIdCount());
            subfaceId = i;
        }
        if (isusumu == 0) {
            return 0;
        }

        return subfaceId;
    }

    /**
     * Возвращает строку с количеством перестановок для каждой подграни
     * @param imax максимальный индекс подграни
     * @return строка с количеством перестановок
     */
    public String Permutation_count(int imax) {
        StringBuilder s0 = new StringBuilder();

        for (int ss = 1; ss <= imax; ss++) {
            s0.append(" : ").append(s[ss].getPermutationCount());
        }
        return s0.toString();
    }

    /**
     * Начинает с текущего состояния перестановки и ищет возможные состояния перекрытия.
     * Есть возможности для ускорения здесь.
     * 
     * @param swap использовать ли алгоритм перестановки
     * @return 1000 если найдено решение, 0 если нет возможных состояний перекрытия
     * @throws InterruptedException если поток прерван
     */
    public int possible_overlapping_search(boolean swap) throws InterruptedException {
        bb.write("Initializing search...");
        bb.write(" ");
        bb.write(" ");
        bb.write(" ");
        int ms, Sid;

        AdditionalEstimationAlgorithm AEA = null;
        aeaMode = swap;
        if (swap) {
            swapper = new SubFaceSwappingAlgorithm();

            // Создаем меньший "реалтайм AEA" для помощи в поиске. Поскольку AEA теперь очень
            // быстрый алгоритм, у нас есть возможность использовать его на каждом шаге поиска для
            // вывода дополнительных отношений стекирования из нашего текущего набора выборов перестановок,
            // и это значительно ускорит генерацию перестановок (из-за механизма временного руководства) более поздних SubFaces.
            AEA = new AdditionalEstimationAlgorithm(hierarchyList, s, SubFace_valid_number, 1000);
            AEA.initialize();
        }

        Sid = 1; // Начальное значение Sid может быть любым, кроме 0.
        while (Sid != 0) { // Если Sid == 0, это означает, что даже наименьший номер SubFace был просмотрен.

            ms = inconsistent_subFace_request(AEA);
            if (ms == 1000) {
                return 1000;
            } // Нет противоречий во всех SubFaces.
            Sid = next(ms - 1);

            if (swap) swapper.process(s, SubFace_valid_number);

            if (Thread.interrupted()) throw new InterruptedException();
        }
        return 0; // Нет возможных состояний перекрытия
    }

    /**
     * Ищет подграни, которые складываются несовместимо, в порядке возрастания номера.
     * Есть возможности для ускорения здесь тоже.
     * 
     * @param AEA алгоритм дополнительной оценки
     * @return 1000 если найдено решение, номер подграни если найдено противоречие
     * @throws InterruptedException если поток прерван
     */
    private int inconsistent_subFace_request(AdditionalEstimationAlgorithm AEA) throws InterruptedException {
        int kks;
        boolean swap = AEA != null;
        hierarchyList.restore(); // Восстанавливаем состояние иерархии
        if (aeaMode) AEA.restore();

        for (int ss = 1; ss <= SubFace_valid_number; ss++) { // Изменено для ускорения. 070417
            if (swap) swapper.visit(s[ss]);

            int count = s[ss].getFaceIdCount(), pair = count * (count - 1) / 2;
            String msg = "Current SubFace( " + ss + " / ";
            if (swap) msg += swapper.getVisitedCount() + " / ";
            bb.rewrite(7, msg + SubFace_valid_number + " ) , face count = " + count + " , face pair = " + pair);
            bb.rewrite(8, "Search progress " + Permutation_count(ss));

            kks = s[ss].possible_overlapping_search(hierarchyList);
            if (kks == 0) { // kks == 0 означает, что нет перестановки, которая может перекрываться
                swapper.record(ss);
                if (ss > SubFace_valid_number / 2 || s[ss].swapCounter > 0) s[ss].swapCounter++;
                return ss;
            }

            s[ss].swapCounter = 0;
            if (aeaMode) {
                // Вводим информацию о стекировании ss-й подграни в hierarchyList.
                s[ss].enterStackingOfSubFace(AEA);

                boolean success = true;
                boolean se = swapper.shouldEstimate(ss); // побочный эффект
                if (se && ss <= Math.sqrt(SubFace_valid_number)) {
                    success = AEA.run(0) == HierarchyListStatus.SUCCESSFUL_1000;
                } else if (ss % (3 + ss * ss / 6400) == 0) {
                    // Нет необходимости выполнять run() или даже fastRun() на каждом шаге (это будет
                    // слишком медленно), поэтому мы используем формулу выше, чтобы решить, когда запускать его.
                    success = AEA.fastRun();
                }
                if (!success) {
                    /*
                     * Для некоторых CP, реалтайм AEA может вернуть результат, отличный от успеха (даже если
                     * мы запускали AEA на каждом шаге и текущая перестановка не имеет никаких
                     * немедленных противоречий, поскольку что-то все еще может пойти не так в процессе
                     * вывода), и в этом случае очень трудно понять ошибку вывода, и ошибка может
                     * остаться до самого финального решения (что сделает решение недействительным).
                     * Лучшее, что мы можем сделать - это отключить реалтайм AEA, если это происходит.
                     */
                    Log.i("TAG", "Disable realtime AEA");
                    aeaMode = false;
                    hierarchyList.restore();
                    ss = 0; // перезапускаем поиск
                }
            } else {
                s[ss].enterStackingOfSubFace(hierarchyList);
            }
        }

        // Решение найдено, выполняем финальную проверку
        bb.rewrite(10, " ");
        bb.rewrite(9, "Possible solution found...");
        AEA = new AdditionalEstimationAlgorithm(hierarchyList, s1, 1000); // нам не нужно много для этого
        if (AEA.run(SubFace_valid_number) != HierarchyListStatus.SUCCESSFUL_1000) {
            bb.rewrite(9, " ");
            // Это редко происходит, но обычно означает, что решение противоречит некоторым
            // подграням, не считавшимся "валидными" ранее. В этом случае добавление их в
            // валидный набор решит проблему.
            if (AEA.errorIndex != 0) {
                // Добавляем дополнительную подгрань в валидный список и продолжаем поиск
                int v = ++SubFace_valid_number, e = AEA.errorIndex;
                Log.i("TAG", "Adding SubFace " + e + " to the valid set index " + v);
                SubFace temp = s[v];
                s[v] = s[e];
                s[e] = temp;

                // Новая подгрань еще не имеет руководства.
                hierarchyList.restore();
                s[v].setGuideMap(hierarchyList);

                // записываем тупик здесь, поскольку эта подгрань уже имеет противоречие
                swapper.record(v);
            }
            return SubFace_valid_number;
        }

        // Решение подтверждено
        return 1000;
    }

    /**
     * Выполняет рейтинг граней и возвращает коробку сортировки
     * @return коробка сортировки с рейтингами граней
     */
    public SortingBox<Integer> rating2() {
        int hierarchyListFacesTotal = hierarchyList.getFacesTotal(); // Находим общее количество граней
        face_rating = new double[hierarchyListFacesTotal + 1];

        i_face_rating = new int[hierarchyListFacesTotal + 1];

        makesuu0no_menno_amount = 0; // Количество граней, которые можно ранжировать без других граней сверху
        makesuu1ijyouno_menno_amount = 0; // Количество граней, которые можно ранжировать только при наличии одной или более других граней сверху

        for (int i = 0; i <= hierarchyListFacesTotal; i++) {
            i_face_rating[i] = 0;
        }

        // Находим самую верхнюю поверхность по порядку от 1 на s поверхности (исключая ранжированную поверхность).
        // Находим количество граней (исключая ранжированные грани) на s плоскости по порядку от 1 и находим общее.
        // Находим поверхность с наименьшим количеством поверхностей на этой поверхности (исключая поверхность с рейтингом) и даем рейтинг
        for (int i = 1; i <= hierarchyListFacesTotal; i++) {
            int i_rate = 1 + hierarchyListFacesTotal - i;

            int top_men_id = get_top_face_id_without_rated_face();

            i_face_rating[top_men_id] = i_rate;
            face_rating[top_men_id] = i_rate;
        }

        Log.i("TAG", "Количество граней, которые можно ранжировать без других граней сверху = " + makesuu0no_menno_amount);
        Log.i("TAG", "Количество граней, которые можно ранжировать только при наличии одной или более других граней сверху = " + makesuu1ijyouno_menno_amount);

        nbox.reset();
        for (int i = 1; i <= hierarchyList.getFacesTotal(); i++) {
            nbox.addByWeight(i, face_rating[i]);
        }

        return nbox;
    }
    // Каждая из следующих функций использует s0[] как FaceStack 20180305

    /**
     * Получает id самой верхней грани без рейтинга
     * @return id самой верхней грани
     */
    private int get_top_face_id_without_rated_face() {
        int top_men_id = 0;
        top_face_id_ga_maketa_kazu_goukei_without_rated_face = hierarchyList.getFacesTotal() + 100;

        int hierarchyListFacesTotal = hierarchyList.getFacesTotal(); // Находим общее количество граней

        boolean[] i_kentouzumi = new boolean[hierarchyListFacesTotal + 1]; // Рассмотренные id граней помечаем как true
        for (int i = 0; i <= hierarchyListFacesTotal; i++) {
            i_kentouzumi[i] = false;
        }

        for (int i = 1; i <= SubFaceTotal; i++) {
            int s_top_id = get_s_top_id_without_rated_face(i); // Самая верхняя грань каждой s-грани (исключая грани с рейтингом). s_top_id=0 означает, что в этой s-грани нет граней с неопределенным рейтингом

            if (s_top_id != 0) {
                if (!i_kentouzumi[s_top_id]) {
                    int mkg = get_maketa_kazu_goukei_without_rated_face(s_top_id);
                    if (mkg == 0) {
                        makesuu0no_menno_amount++;
                        return s_top_id;
                    } // Здесь нужно проверить, правильно ли это 20180306
                    if (top_face_id_ga_maketa_kazu_goukei_without_rated_face > mkg) {
                        top_face_id_ga_maketa_kazu_goukei_without_rated_face = mkg;
                        top_men_id = s_top_id;
                    }
                }
            }

            i_kentouzumi[s_top_id] = true;
        }

        // makesuu0no_menno_amount=0; // Количество граней, которые можно ранжировать без других граней сверху
        // makesuu1ijyouno_menno_amount=0; // Количество граней, которые можно ранжировать только при наличии одной или более других граней сверху
        if (top_face_id_ga_maketa_kazu_goukei_without_rated_face == 0) {
            makesuu0no_menno_amount++;
        } else if (top_face_id_ga_maketa_kazu_goukei_without_rated_face > 0) {
            makesuu1ijyouno_menno_amount = makesuu1ijyouno_menno_amount + 1;
        }

        return top_men_id;
    }

    /**
     * Получает id самой верхней грани без рейтинга в s-грани
     * @param ism id s-грани
     * @return id самой верхней грани или 0 если нет граней без рейтинга
     */
    private int get_s_top_id_without_rated_face(int ism) { // ism - это id s-грани
        int Mensuu = s0[ism].getFaceIdCount(); // Количество граней в FaceStack // FaceStack s0[]; // FaceStack, полученный из FaceStack_figure
        for (int jyunban = 1; jyunban <= Mensuu; jyunban++) {
            int im = s0[ism].fromTop_count_FaceId(jyunban);
            if (i_face_rating[im] == 0) {
                return im;
            }
        }
        return 0;
    }

    /**
     * Получает общее количество граней сверху без рейтинга для указанной грани
     * @param men_id id грани
     * @return общее количество граней сверху
     */
    private int get_maketa_kazu_goukei_without_rated_face(int men_id) {
        int i_make = 0;
        for (int ism = 1; ism <= SubFaceTotal; ism++) {
            i_make = i_make + get_subFace_de_maketa_kazu_without_rated_Face(ism, men_id);
            if (i_make >= top_face_id_ga_maketa_kazu_goukei_without_rated_face) {
                return i_make;
            } // 20180306 Добавлена эта строка для ускорения, но неясно, действительно ли она эффективна. Даже если закомментировать только эту строку, должно работать нормально.
        }
        return i_make;
    }

    /**
     * Получает количество граней сверху без рейтинга в подграни
     * @param ism id FaceStack
     * @param men_id id грани
     * @return количество граней сверху
     */
    private int get_subFace_de_maketa_kazu_without_rated_Face(int ism, int men_id) { // ism - это id FaceStack
        int FaceCount = s0[ism].getFaceIdCount(); // Количество граней в FaceStack // FaceStack s0[]; // FaceStack, полученный из FaceStack_figure
        int maketa_kazu = 0;

        for (int i = 1; i <= FaceCount; i++) {
            int im = s0[ism].fromTop_count_FaceId(i);
            if (im == men_id) {
                return maketa_kazu;
            }
            if (i_face_rating[im] == 0) {
                maketa_kazu++;
            }
        }
        return 0;
    }


    /**
     * Статусы иерархического списка граней
     */
    public enum HierarchyListStatus {
        /** Неизвестно (-1) */
        UNKNOWN_N1,
        /** Неизвестно (0) */
        UNKNOWN_0,
        /** Неизвестно (1) */
        UNKNOWN_1,
        /** Противоречие (2) */
        CONTRADICTED_2,
        /** Противоречие (3) */
        CONTRADICTED_3,
        /** Противоречие (4) */
        CONTRADICTED_4,
        /** Ограничение (5) */
        CONSTRAINT_5,
        /** Успешно (1000) */
        SUCCESSFUL_1000,
    }
}
