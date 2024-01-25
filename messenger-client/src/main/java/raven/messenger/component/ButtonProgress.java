package raven.messenger.component;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class ButtonProgress extends JButton {

    private float progress;

    public ButtonProgress(Icon icon) {
        super(icon);
        init();
    }

    public ButtonProgress() {
        init();
    }

    private void init() {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFocusable(false);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (progress > 0) {
            Graphics2D g2 = (Graphics2D) g.create();
            FlatUIUtils.setRenderingHints(g2);
            int width = getWidth();
            int height = getHeight();
            int size = UIScale.scale(1);
            Area area = new Area(new Arc2D.Double(0, 0, width, height, 90, 360 * -progress, Arc2D.PIE));
            area.subtract(new Area(new Ellipse2D.Double(size, size, width - size * 2, height - size * 2)));
            g2.setColor(UIManager.getColor("Component.accentColor"));
            g2.fill(area);
            g2.dispose();
        }
    }


    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        progress = Math.round(progress * 100);
        progress /= 100;
        if (this.progress != progress) {
            this.progress = progress;
            repaint();
        }
    }
}
