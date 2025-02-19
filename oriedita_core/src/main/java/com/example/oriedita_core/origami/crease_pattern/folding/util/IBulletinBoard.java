package com.example.oriedita_core.origami.crease_pattern.folding.util;

/**
 * Displays multiple lines of strings.
 */
public interface IBulletinBoard {
    void rewrite(int i, String s);

    void write(String s);

    void clear();
}

