package raven.messenger.component.chat;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.CubicBezierEasing;
import com.formdev.flatlaf.util.UIScale;
import raven.messenger.util.MethodUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class TextPaneCustom extends JTextPane {

    private Animator animator;
    private float animate;
    private String placeholderText;
    private Color placeholderForeground;
    private long soundCaptureMillisecond;
    private boolean input = true;

    @Override
    public void updateUI() {
        super.updateUI();
        placeholderForeground = UIManager.getColor("TextField.placeholderForeground");
    }

    public TextPaneCustom() {
        init();
    }

    private void init() {
        animator = new Animator(200, v -> {
            animate = v;
            repaint();
        });
        animator.setInterpolator(CubicBezierEasing.EASE_IN);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getDocument().getLength() == 0 && placeholderText != null && !placeholderText.isEmpty()) {
            paintPlaceholder(g, 0, placeholderText);
        }
        if (!input) {
            Graphics2D g2 = (Graphics2D) g.create();
            FlatUIUtils.setRenderingHints(g2);
            g2.setColor(getBackground());
            g2.setComposite(AlphaComposite.SrcOver.derive(animate));
            g2.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
            int left = UIScale.scale(30);
            int size = UIScale.scale(10);
            paintPlaceholder(g2, left, MethodUtil.convertMillisToTime(soundCaptureMillisecond));
            int x = (left - size) / 2;
            int y = (getHeight() - size) / 2;
            g2.setComposite(AlphaComposite.SrcOver.derive(MethodUtil.easingMilliseconds(soundCaptureMillisecond)));
            g2.setColor(new Color(206, 40, 40));
            g2.fill(new Ellipse2D.Double(x, y, size, size));
        }
    }

    protected void paintPlaceholder(Graphics g, int left, String text) {
        Insets insets = getInsets();
        FontMetrics fm = getFontMetrics(getFont());
        int height = getHeight() - (insets.top + insets.bottom);
        int x = insets.left;
        int y = insets.top + ((height - fm.getHeight()) / 2) + fm.getAscent();
        g.setColor(placeholderForeground);
        FlatUIUtils.drawString(this, g, text, x + left, y);
    }

    public String getPlaceholderText() {
        return placeholderText;
    }

    public void setPlaceholderText(String placeholderText) {
        this.placeholderText = placeholderText;
    }

    public void setSoundCaptureMillisecond(long millisecond) {
        this.soundCaptureMillisecond = millisecond;
        repaint();
    }

    public void useInput(boolean input) {
        if (this.input != input) {
            this.input = input;
            if (animator.isRunning()) {
                animator.stop();
            }
            animate = 0;
            animator.start();
            setFocusable(input);
            if (input) {
                grabFocus();
            }
        }
    }
}
