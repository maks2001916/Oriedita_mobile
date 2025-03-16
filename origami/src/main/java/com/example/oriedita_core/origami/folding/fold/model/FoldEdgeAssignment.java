package com.example.oriedita_core.origami.folding.fold.model;

public enum FoldEdgeAssignment {
    BORDER("B"),
    MOUNTAIN_FOLD("M"),
    VALLEY_FOLD("V"),
    FLAT_FOLD("F"),
    UNASSIGNED("U");

    private final String letter;

    private FoldEdgeAssignment(String letter) {
        this.letter = letter;
    }

    public static FoldEdgeAssignment of(String letter) {
        FoldEdgeAssignment[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            FoldEdgeAssignment foldEdgeAssignment = var1[var3];
            if (foldEdgeAssignment.getLetter().equals(letter)) {
                return foldEdgeAssignment;
            }
        }

        return null;
    }

    public String getLetter() {
        return this.letter;
    }
}

