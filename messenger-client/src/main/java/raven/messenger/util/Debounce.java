package raven.messenger.util;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Debounce {

    private Timer time;
    private KeyEvent key;

    private Debounce(JTextField txt, Callback callback, int duration) {
        time = new Timer(duration, (e) -> {
            callback.call(key, txt.getText());
            time.stop();
        });
        txt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent ke) {
                key = ke;
                if (time.isRunning()) {
                    time.stop();
                }
                if (ke.getKeyChar() == 10) {
                    time.setInitialDelay(0);
                }
                time.start();
                time.setInitialDelay(duration);
            }
        });
    }

    public static void add(JTextField text, Callback callback, int duration) {
        new Debounce(text, callback, duration);
    }

    public interface Callback {

        void call(KeyEvent ke, String text);
    }
}
