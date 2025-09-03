package com.example.oriedita_core.origami.folding.algorithm;

public class InferenceFailureException extends Exception {
    final int i;
    final int j;

    public InferenceFailureException(int i, int j) {
        this.i = i;
        this.j = j;
    }
}
