package com.example.oriedita_core.origami.folding.fold.custom;

public interface Adapter<T, V> {
    V convert(T var1, V var2);

    T convertBack(V var1, T var2);
}
