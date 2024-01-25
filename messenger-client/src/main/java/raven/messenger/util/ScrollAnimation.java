package raven.messenger.util;

import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.CubicBezierEasing;

import javax.swing.*;

public class ScrollAnimation {

    private Animator animator;
    private JScrollBar scrollBar;
    private int startValue;
    private int endValue;

    public ScrollAnimation() {
        initAnimation();
    }

    private void initAnimation() {
        animator = new Animator(500, v -> {
            int size = endValue - startValue;
            scrollBar.setValue((int) (startValue + size * v));
        });
        animator.setInterpolator(CubicBezierEasing.EASE_IN_OUT);
    }

    public void scroll(JScrollBar scrollBar, int toValue) {
        this.scrollBar = scrollBar;
        if (animator.isRunning()) {
            animator.stop();
        }
        startValue = scrollBar.getValue();
        endValue = toValue;
        animator.start();
    }

    public void scrollToMax(JScrollBar scrollBar) {
        this.scrollBar = scrollBar;
        scroll(scrollBar, scrollBar.getMaximum());
    }
}
