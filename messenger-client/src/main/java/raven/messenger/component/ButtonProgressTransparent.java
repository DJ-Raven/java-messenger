package raven.messenger.component;

import com.formdev.flatlaf.ui.FlatUIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ButtonProgressTransparent extends ButtonProgress {

    public ButtonProgressTransparent(Icon icon) {
        super(icon);
        init();
    }

    public ButtonProgressTransparent() {
        init();
    }

    private void init() {
        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        FlatUIUtils.setRenderingHints(g2);
        int width = getWidth();
        int height = getHeight();
        g2.setColor(getBackground());
        g2.setComposite(AlphaComposite.SrcOver.derive(0.6f));
        g2.fill(new Ellipse2D.Double(0, 0, width, height));
        g2.dispose();
        super.paintComponent(g);
    }
}
