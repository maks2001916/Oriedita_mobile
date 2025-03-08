package com.example.oriedita_ui.tools;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;

public class KeyStrokeUtil {
    public static String toString(KeyStroke keyStroke) {
        if (keyStroke == null) {
            return "";
        }

        return keyStroke.toString()
                .replaceAll("pressed ", "")
                .replaceAll(" ", "+");
    }

    public static void resetButton(AbstractButton button) {
        button.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
        button.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
        button.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");
        button.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");
    }

    public static String toStringWithMetaIcon(String str) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac")) {
            return str.replaceFirst("meta", "⌘");
        } else {
            return str.replaceFirst("windows|WINDOWS", "❖");
        }
    }
}
