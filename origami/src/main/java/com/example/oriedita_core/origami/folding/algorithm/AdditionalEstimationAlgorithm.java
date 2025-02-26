package com.example.oriedita_core.origami.crease_pattern.folding.algorithm;

import android.util.Log;
import com.example.oriedita_core.origami.crease_pattern.worker.FoldedFigure_Worker.HierarchyListStatus;
import com.example.oriedita_core.origami.crease_pattern.data.StackArray;
import com.example.oriedita_core.origami.crease_pattern.data.listMatrix.PseudoListMatrix;
import com.example.oriedita_core.origami.crease_pattern.folding.HierarchyList;
import com.example.oriedita_core.origami.crease_pattern.folding.algorithm.italiano.ItalianoAlgorithm;
import com.example.oriedita_core.origami.crease_pattern.folding.algorithm.italiano.ReactiveItalianoAlgorithm;
import com.example.oriedita_core.origami.crease_pattern.folding.element.SubFace;
import com.example.oriedita_core.origami.crease_pattern.folding.util.EquivalenceCondition;
import com.example.oriedita_core.origami.crease_pattern.folding.util.IBulletinBoard;

import java.util.Iterator;

/**
 * This class is the result of refactoring the original additional_estimation().
 * It does basically the same thing, but greatly improves readability. It also
 * improves the performance by removing outer loops that are theoretically
 * redundant.
 *
 * @author Mu-Tsun Tsai
 */
public class AdditionalEstimationAlgorithm {

    /**
     * To prevent memory overflow. Right now this number is hard-coded, but in the
     * future we might want to dynamically determine it based on available memory.
     */
    private static final int MAX_NEW_RELATIONS = 100000;

    private static final int ABOVE = HierarchyList.ABOVE_1;
    private static final int BELOW = HierarchyList.BELOW_0;

    private IBulletinBoard bb = null;
    private final HierarchyList hierarchyList;
    private final SubFace[] subFaces; // indices start from 1
    private final int count;
    private int new_relations;

    private final ReactiveItalianoAlgorithm[] IA;
    private final PseudoListMatrix relationObservers;
    private final StackArray changeList;

    /**
     * During the first time AEA runs, we can actually remove those
     * EquivalenceConditions that are used for inference. Doing so will remove vast
     * majority of the ECs, and thus speed up the search stage like crazy since we
     * will have way less ECs to check. This flag controls that behavior.
     */
    public boolean removeMode = false;

    public int errorIndex;

    public EquivalenceCondition errorPos;

    public AdditionalEstimationAlgorithm(IBulletinBoard bb, HierarchyList hierarchyList, SubFace[] s, int capacity) {
        this(hierarchyList, s, capacity);
        this.bb = bb;
    }

    public AdditionalEstimationAlgorithm(HierarchyList hierarchyList, SubFace[] s, int capacity) {
        this(hierarchyList, s, s.length - 1, capacity);
    }

    public AdditionalEstimationAlgorithm(HierarchyList hierarchyList, SubFace[] s, int count, int capacity) {
        this.hierarchyList = hierarchyList;
        this.count = ++count;
        this.subFaces = new SubFace[count];
        System.arraycopy(s, 0, this.subFaces, 0, count); // We keep a copy, since s might get swapped.
        changeList = new StackArray(count, capacity);
        IA = new ReactiveItalianoAlgorithm[count];
        relationObservers = new PseudoListMatrix(hierarchyList.getFacesTotal());
    }

    public HierarchyListStatus run(int completedSubFaces) throws InterruptedException {
        int total = 0;

        if (bb != null) {
            Log.i("TAG","additional_estimation start---------------------＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊");
            bb.write(" ");
        }

        do {
            new_relations = 0;

            int iS = 0;
            try {
                for (iS = completedSubFaces + 1; iS < count && new_relations < MAX_NEW_RELATIONS; iS++) {
                    if (IA[iS] == null) {
                        // We initialize ItalianoAlgorithm one by one instead of all at once,
                        // so that the changes are flushed immediately after initialization,
                        // saving memory.
                        initializeItalianoAlgorithm(iS);
                    }
                    int changes = checkTransitivity(iS);
                    new_relations += changes;

                    if (Thread.interrupted()) throw new InterruptedException();
                }
            } catch (InferenceFailureException e) {
                errorIndex = iS;
                errorPos = new EquivalenceCondition(e.i, e.j, e.i, e.j);
                return HierarchyListStatus.CONTRADICTED_2;
            }

            EquivalenceCondition currentEC = null;
            try {
                Iterator<EquivalenceCondition> it = hierarchyList.getEquivalenceConditions().iterator();
                while (it.hasNext() && new_relations < MAX_NEW_RELATIONS) {
                    int changes = checkTripleConstraint(currentEC = it.next());
                    if (removeMode && (changes & 1) == 1) it.remove();
                    new_relations += changes >>> 1;

                    if (Thread.interrupted()) throw new InterruptedException();
                }
            } catch (InferenceFailureException e) {
                errorPos = currentEC;
                return HierarchyListStatus.CONTRADICTED_3;
            }

            currentEC = null;
            try {
                Iterator<EquivalenceCondition> it = hierarchyList.getUEquivalenceConditions().iterator();
                while (it.hasNext() && new_relations < MAX_NEW_RELATIONS) {
                    int changes = checkQuadrupleConstraint(currentEC = it.next());
                    if (removeMode && (changes & 1) == 1) it.remove();
                    new_relations += changes >>> 1;

                    if (Thread.interrupted()) throw new InterruptedException();
                }
            } catch (InferenceFailureException e) {
                errorPos = currentEC;
                return HierarchyListStatus.CONTRADICTED_4;
            }

            // ----------------

            if (bb != null) {
                Log.i("TAG","Total number of inferred relations ＝ " + new_relations);
                bb.rewrite(10, "           Total number of inferred relations ＝ " + (total += new_relations));
            }

        } while (new_relations > 0);

        if (bb != null) {
            Log.i("TAG","additional_estimation finished------------------------＊＊＊＊ここまで20150310＊＊＊＊＊＊＊＊＊＊＊");
        }

        return HierarchyListStatus.SUCCESSFUL_1000;
    }

    /**
     * This is a faster version of run().
     */
    public boolean fastRun() {
        try {
            flush();
            for (EquivalenceCondition tg : hierarchyList.getEquivalenceConditions()) {
                checkTripleConstraint(tg);
            }
            for (EquivalenceCondition tg : hierarchyList.getUEquivalenceConditions()) {
                checkQuadrupleConstraint(tg);
            }
            return true;
        } catch (InferenceFailureException e) {
            return false;
        }
    }

    private void initializeItalianoAlgorithm(int s) {
        IA[s] = new ReactiveItalianoAlgorithm(subFaces[s].getFaceIdCount(), s, changeList);
        int count = subFaces[s].getFaceIdCount();
        for (int i = 1; i <= count; i++) {
            for (int j = 1; j <= count; j++) {
                int I = subFaces[s].getFaceId(i);
                int J = subFaces[s].getFaceId(j);
                if (I < J && hierarchyList.isEmpty(I, J)) {
                    // Observing potential changes to the relation
                    relationObservers.add(I, J, s);
                } else if (hierarchyList.get(I, J) == ABOVE) {
                    IA[s].add(i, j);
                }
            }
        }
    }

    /**
     * Originally Mr.Meguro implemented this part using what is essentially the
     * Warshall algorithm, but it is an offline algorithm that is not suitable for
     * the dynamic use case here. I re-implemented it using the Italiano algorithm,
     * which is way faster here.
     */
    private int checkTransitivity(int s) throws InferenceFailureException {
        int changes = 0;
        for (int n : changeList.flush(s)) {
            changes += tryInferAbove(subFaces[s].getFaceId(n >>> 16),
                    subFaces[s].getFaceId(n & ItalianoAlgorithm.mask));
        }
        return changes;
    }

    private int checkTripleConstraint(EquivalenceCondition ec) throws InferenceFailureException {
        int changes = 0;
        int removeFlag = 0; // To mark if this EquivalenceCondition is no longer needed afterwards.
        int a = ec.getA(), b = ec.getB(), d = ec.getD();
        if (hierarchyList.get(a, b) == ABOVE) {
            removeFlag = 1;
            changes += tryInferAbove(a, d);
        } else if (hierarchyList.get(a, b) == BELOW) {
            removeFlag = 1;
            changes += tryInferAbove(d, a);
        } else if (hierarchyList.get(a, d) == ABOVE) {
            removeFlag = 1;
            changes += tryInferAbove(a, b);
        } else if (hierarchyList.get(a, d) == BELOW) {
            removeFlag = 1;
            changes += tryInferAbove(b, a);
        }
        return (changes << 1) | removeFlag;
    }

    private int checkQuadrupleConstraint(EquivalenceCondition ec) throws InferenceFailureException {
        int changes = 0;
        int removeFlag = 0; // To mark if this EquivalenceCondition is no longer needed afterwards.
        int a = ec.getA(), b = ec.getB(), c = ec.getC(), d = ec.getD();

        // If only a> b> c, the position of d cannot be determined

        // a> c && b> d then a> d && b> c
        if (hierarchyList.get(a, c) == ABOVE && hierarchyList.get(b, d) == ABOVE) {
            removeFlag = 1;
            changes += tryInferAbove(a, d) + tryInferAbove(b, c);
        }
        // If a> d && b> c then a> c && b> d
        if (hierarchyList.get(a, d) == ABOVE && hierarchyList.get(b, c) == ABOVE) {
            removeFlag = 1;
            changes += tryInferAbove(a, c) + tryInferAbove(b, d);
        }
        // If a <c && b <d, then a <d && b <c
        if (hierarchyList.get(a, c) == BELOW && hierarchyList.get(b, d) == BELOW) {
            removeFlag = 1;
            changes += tryInferAbove(d, a) + tryInferAbove(c, b);
        }
        // If a <d && b <c then a <c && b <d
        if (hierarchyList.get(a, d) == BELOW && hierarchyList.get(b, c) == BELOW) {
            removeFlag = 1;
            changes += tryInferAbove(c, a) + tryInferAbove(d, b);
        }

        /////////////////////////

        // We already make sure that a > b and c > d when we added the condition.
        // If a> c> b, then a> d> b
        if (hierarchyList.get(a, c) == ABOVE && hierarchyList.get(c, b) == ABOVE) {
            // Noticed that we don't need to infer a > b here, since that part will be done
            // in the transitivity check anyway. The same is true for the rest.
            removeFlag = 1;
            changes += tryInferAbove(a, d) + tryInferAbove(d, b);
        }
        // a>d>b なら a>c>b
        if (hierarchyList.get(a, d) == ABOVE && hierarchyList.get(d, b) == ABOVE) {
            removeFlag = 1;
            changes += tryInferAbove(a, c) + tryInferAbove(c, b);
        }
        // c>a>d なら c>b>d
        if (hierarchyList.get(c, a) == ABOVE && hierarchyList.get(a, d) == ABOVE) {
            removeFlag = 1;
            changes += tryInferAbove(c, b) + tryInferAbove(b, d);
        }
        // c>b>d なら c>a>d
        if (hierarchyList.get(c, b) == ABOVE && hierarchyList.get(b, d) == ABOVE) {
            removeFlag = 1;
            changes += tryInferAbove(c, a) + tryInferAbove(a, d);
        }

        return (changes << 1) | removeFlag;
    }

    /**
     * Make inference that i > j.
     */
    private int tryInferAbove(int i, int j) throws InferenceFailureException {
        if (hierarchyList.get(i, j) == BELOW) {
            throw new InferenceFailureException(i, j);
        }
        return inferAbove(i, j) ? 1 : 0;
    }

    /**
     * Caller of this method must be certain that no error will occur.
     */
    public boolean inferAbove(int i, int j) throws InferenceFailureException {
        if (hierarchyList.isEmpty(i, j)) {
            hierarchyList.set(i, j, ABOVE);

            // Notifying the ItalianoAlgorithm to update.
            Iterable<Integer> it = i < j ? relationObservers.get(i, j) : relationObservers.get(j, i);
            for (int s : it) {
                // Although PseudoListMatrix might return a larger list in general, here the
                // returned list is actually exactly what we need, since a SubFace s is added to
                // List[i][x] only if it contains Face i, and similarly it is added to
                // List[x][j] only if it contains Face j.
                if (!IA[s].tryAdd(subFaces[s].FaceIdIndex(i), subFaces[s].FaceIdIndex(j))) {
                    throw new InferenceFailureException(i, j);
                }
            }
            return true;
        }
        return false;
    }

    public void initialize() {
        for (int i = 1; i < count; i++) {
            initializeItalianoAlgorithm(i);
            changeList.flush(i); // There shouldn't be anything, but just in case.
            IA[i].save();
        }
    }

    public void restore() {
        for (int i = 1; i < count; i++) IA[i].restore();
    }

    /**
     * Create a new 3EC and perform an initial check at the same time.
     */
    public boolean addEquivalenceCondition(int a, int b, int d) {
        if (hierarchyList.get(b, d) == BELOW) {
            int temp = b;
            b = d;
            d = temp;
        }
        EquivalenceCondition ec = new EquivalenceCondition(a, b, a, d);
        try {
            int result = checkTripleConstraint(ec);
            if ((result & 1) == 0) hierarchyList.addEquivalenceCondition(ec);
            else {
                new_relations += result >>> 1;
                if (new_relations > MAX_NEW_RELATIONS) flush();
            }
            return true;
        } catch (InferenceFailureException e) {
            errorPos = ec;
            return false;
        }
    }

    /**
     * Create a new 4EC and perform an initial check at the same time.
     */
    public boolean addUEquivalenceCondition(int a, int b, int c, int d) {
        if (hierarchyList.get(a, b) == HierarchyList.BELOW_0) {
            int temp = a;
            a = b;
            b = temp;
        }
        if (hierarchyList.get(c, d) == HierarchyList.BELOW_0) {
            int temp = c;
            c = d;
            d = temp;
        }
        EquivalenceCondition ec = new EquivalenceCondition(a, b, c, d);
        try {
            int result = checkQuadrupleConstraint(ec);
            if ((result & 1) == 0) hierarchyList.addUEquivalenceCondition(ec);
            else {
                new_relations += result >>> 1;
                if (new_relations > MAX_NEW_RELATIONS) flush();
            }
            return true;
        } catch (InferenceFailureException e) {
            errorPos = ec;
            return false;
        }
    }

    private void flush() throws InferenceFailureException {
        new_relations = 0;
        for (int iS = 1; iS < count; iS++) {
            new_relations += checkTransitivity(iS);
        }
    }
}

