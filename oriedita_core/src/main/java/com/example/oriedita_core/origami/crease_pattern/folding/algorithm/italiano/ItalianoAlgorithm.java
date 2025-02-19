package com.example.oriedita_core.origami.crease_pattern.folding.algorithm.italiano;

import java.util.ArrayDeque;

/**
 * This is the transitive closure algorithm described by G. F. Italiano. See
 * http://dx.doi.org/10.1016/0304-3975%2886%2990098-8. The base class only
 * contains the basic functionality, while additional features are added in the
 * derived classes.
 *
 * @author Mu-Tsun Tsai
 */
public class ItalianoAlgorithm {

    public static final int mask = (1 << 16) - 1;
    public static final int emptyNode = 1 << 16; // so that 0 is reserved as null
    public static final int nodeMask = (1 << 17) - 1;

    protected final int size;

    /**
     * matrix[i][j] is the node of j on the spanning tree of i, of which existence
     * implies that i > j. matrix[i][i] is the root of the spanning tree of i.
     * <p>
     * In order to save memory, we use int array instead of Node array (which can
     * take 3x space). Each node is firstChild in the upper 7 bits follow by 1, and
     * then the nextSibling in the lower 8 bits. By doing so we assume that a
     * subFace will have no more than 32767 faces, which is very reasonable. (No way
     * any one will design a model THAT thick!)
     */
    protected final int[][] matrix; // 1-based

    /**
     * Prevents heap memory overflow. Still, for large projects, it is necessary to
     * allocate larger memory for JVM in the first place. Accordingly, ArrayDeque is
     * more efficient than other classes such as ArrayList, LinkedList, etc. We use
     * int64 to store 4 int16 parameters.
     * <p>
     * We do not use a centralized StackArray to implement this part like what we
     * did for changes, since the way stack operates here is that it pops and stack
     * at the same time, such operations are in fact slower to use StackArray than
     * individual ArrayDeque.
     */
    private final ArrayDeque<Long> stack = new ArrayDeque<>();

    public ItalianoAlgorithm(int size) {
        this.size = size;
        this.matrix = new int[size + 1][size + 1];
        for (int i = 1; i <= size; i++) {
            matrix[i][i] = emptyNode;
        }
    }

    public final boolean tryAdd(int i, int j) {
        if (matrix[j][i] != 0) return false;
        add(i, j);
        return true;
    }

    public final void add(int i, int j) {
        if (matrix[i][j] == 0) {
            for (int x = 1; x <= size; x++) {
                if (matrix[x][i] != 0 && matrix[x][j] == 0) {
                    stackMeld(x, j, i, j);
                }
            }
            while (stack.size() > 0) {
                long r = stack.pop();
                meld((int) (r >>> 48), (int) ((r >>> 32) & mask), (int) ((r >>> 16) & mask), (int) (r & mask));
            }
        }
    }

    protected void meld(int x, int j, int u, int v) {
        // create new node
        matrix[x][v] = emptyNode | (matrix[x][u] >>> 17);

        // add node as child of matrix[x][u]
        matrix[x][u] = matrix[x][u] & nodeMask | (v << 17);

        // copy subtree
        int w = matrix[j][v] >>> 17;
        while (w != 0) {
            if (matrix[x][w] == 0) {
                stackMeld(x, j, v, w);
            }
            w = matrix[j][w] & mask;
        }
    }

    private void stackMeld(long x, long j, long u, long v) {
        stack.add(x << 48 | j << 32 | u << 16 | v);
    }
}

