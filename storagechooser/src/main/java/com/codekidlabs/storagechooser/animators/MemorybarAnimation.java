package com.codekidlabs.storagechooser.animators;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

/**
 * display a nice animation when chooser dialog is shown
 */

public class MemorybarAnimation extends Animation {

    private ProgressBar progressBar;
    private float from;
    private float to;


    public MemorybarAnimation(ProgressBar progressBar, int from, int to) {
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float animatedProgress = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) animatedProgress);
    }
}
