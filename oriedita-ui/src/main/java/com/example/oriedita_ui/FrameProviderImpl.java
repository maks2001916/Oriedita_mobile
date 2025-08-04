package com.example.oriedita_ui;


import javax.swing.JFrame;

/**
 * Provides a pointer to the main JFrame.
 */
public class FrameProviderImpl implements FrameProvider {
    private volatile JFrame frame;

    private static final Object lock = new Object();

    public JFrame get() {
        if (frame == null) {
            synchronized (lock) {
                if (frame == null) {
                    frame = new JFrame();
                }
            }
        }

        return frame;
    }
}
