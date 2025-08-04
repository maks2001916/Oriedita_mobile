package com.example.oriedita.editor.factory;

import oriedita.editor.canvas.animation.EaseOutInterpolation;
import oriedita.editor.canvas.animation.Interpolation;

public class InterpolationFactory {
    public static Interpolation defaultInterpolation() {
        return new EaseOutInterpolation();
    }
}
