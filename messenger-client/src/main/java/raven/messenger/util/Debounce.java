package raven.messenger.util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Debounce {

    private Timer time;
    private KeyEvent lastKeyEvent;

    private Debounce(JTextField txt, Callback callback, int duration) {
        time = new Timer(duration, (e) -> {
            callback.call(lastKeyEvent, txt.getText());
            time.stop();
        });
        time.setRepeats(false);
        txt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent ke) {
                lastKeyEvent = ke;
            }
        });
        txt.getDocument().addDocumentListener(new DocumentListener() {

            private void restartTimer() {
                if (time.isRunning()) {
                    time.restart();
                } else {
                    time.start();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                restartTimer();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                restartTimer();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                restartTimer();
            }
        });
        txt.addActionListener(e -> {
            time.setInitialDelay(0);
            if (time.isRunning()) {
                time.stop();
            }
            time.start();
            time.setInitialDelay(duration);
        });
    }

    public static void add(JTextField text, Callback callback) {
        new Debounce(text, callback, 300);
    }

    public static void add(JTextField text, Callback callback, int duration) {
        new Debounce(text, callback, duration);
    }

    public interface Callback {

        void call(KeyEvent ke, String text);
    }
}
