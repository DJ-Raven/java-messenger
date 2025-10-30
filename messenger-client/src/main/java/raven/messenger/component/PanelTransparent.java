package raven.messenger.component;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import java.awt.*;

public class PanelTransparent extends JPanel {

    private final float alpha;
    private final int round;

    public PanelTransparent(int round, float alpha) {
        this.round = round;
        this.alpha = alpha;
        init();
    }

    private void init() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        FlatUIUtils.setRenderingHints(g2);
        g2.setColor(getBackground());
        g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
        int arc = round == 999 ? getHeight() : UIScale.scale(round);
        FlatUIUtils.paintComponentBackground(g2, 0, 0, getWidth(), getHeight(), 0, arc);
        g2.dispose();
        super.paintComponent(g);
    }
}
