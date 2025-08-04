package com.example.oriedita.editor.factory;

import origami.crease_pattern.FoldLineSet;

/**
 * Provides specific named fold line sets
 */
public class FoldLineSetFactory {
    FoldLineSet auxLinesFoldLineSet() {
        return new FoldLineSet();
    }

    FoldLineSet foldLineSet() {
        return new FoldLineSet();
    }

    FoldLineSet backupAuxLinesFoldLineSet() {
        return new FoldLineSet();
    }

    FoldLineSet backupFoldLineSet() {
        return new FoldLineSet();
    }
}
