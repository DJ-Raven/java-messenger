package raven.messenger.util;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EventMouseHold {

    private long start;
    private boolean enter;
    private boolean action;
    private boolean run;

    public void applyEventMouseHold(JButton button, long duration, HoleAction callback) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    run = true;
                    start = System.currentTimeMillis();
                    new Thread(() -> {
                        while (run) {
                            sleep(1);
                            if (System.currentTimeMillis() - start >= duration) {
                                action = true;
                                action(callback, HoldStatus.ACTION);
                                break;
                            }
                        }
                    }).start();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    run = false;
                    if (action) {
                        if (enter) {
                            action(callback, HoldStatus.RELEASE_IN);
                        } else {
                            action(callback, HoldStatus.RELEASE_OUT);
                        }
                    }
                    action = false;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                enter = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                enter = false;
            }
        });
    }

    private synchronized void action(HoleAction event, HoldStatus status) {
        event.onAction(status);
    }

    private void sleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    public interface HoleAction {
        void onAction(HoldStatus status);
    }


    public enum HoldStatus {
        ACTION, MOUSE_EXIT, MOUSE_ENTER, RELEASE_IN, RELEASE_OUT
    }
}
