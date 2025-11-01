package raven.messenger.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ProfileStatus extends JLabel {

    private boolean activeStatus;

    public ProfileStatus(Icon icon) {
        super(icon);
        setHorizontalAlignment(SwingConstants.CENTER);
        putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +4;" +
                "foreground:$Profile.foreground;");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (activeStatus) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            int size = Math.min(width, height);
            int x = (width - size) / 2;
            int y = (height - size) / 2;
            g2.setColor(getParent().getBackground());
            g2.translate(x + width * 0.75f, y + height * 0.75f);
            float border = UIScale.scale(11);
            float box = border * 0.7f;
            float sx = (border - box) / 2;
            g2.fill(new Ellipse2D.Double(0, 0, border, border));
            g2.setColor(Color.decode("#33BD4F"));
            g2.fill(new Ellipse2D.Double(sx, sx, box, box));
            g2.dispose();
        }
    }

    public boolean isActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        if (this.activeStatus != activeStatus) {
            this.activeStatus = activeStatus;
            repaint();
        }
    }
}
