package raven.messenger.component;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class StringIcon implements Icon {

    private final String string;
    private final int iconWidth;
    private final int iconHeight;
    private final Color color;
    private final Color colorGradient;

    public StringIcon(String string, Color color, int iconWidth, int iconHeight) {
        this.string = string;
        this.color = color;
        this.colorGradient = ColorFunctions.darken(color, 0.1f);
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        int width = c.getWidth();
        int height = c.getHeight();
        FlatUIUtils.setRenderingHints(g2);
        FontMetrics fm = c.getFontMetrics(c.getFont());
        int tx = x + ((width - fm.stringWidth(string)) / 2);
        int ty = y + fm.getAscent() + ((height - fm.getHeight()) / 2);
        g2.setPaint(new GradientPaint(x, y, color, x, height, colorGradient));
        g2.fill(new Ellipse2D.Double(x, y, width, height));
        g2.dispose();
        FlatUIUtils.drawString((JComponent) c, g, string, tx, ty);
    }

    @Override
    public int getIconWidth() {
        return UIScale.scale(iconWidth);
    }

    @Override
    public int getIconHeight() {
        return UIScale.scale(iconHeight);
    }

    public Image getImage(JComponent c) {
        int width = getIconWidth();
        int height = getIconHeight();
        if (width <= 0 || height <= 0) {
            return null;
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        FlatUIUtils.setRenderingHints(g2);
        g2.setFont(c.getFont());
        FontMetrics fm = c.getFontMetrics(c.getFont());
        int tx = ((width - fm.stringWidth(string)) / 2);
        int ty = fm.getAscent() + ((height - fm.getHeight()) / 2);
        g2.setPaint(new GradientPaint(0, 0, color, 0, height, colorGradient));
        g2.fill(new Ellipse2D.Double(0, 0, width, height));
        g2.setColor(new Color(255, 255, 255));
        FlatUIUtils.drawString(c, g2, string, tx, ty);
        g2.dispose();
        return image;
    }
}
