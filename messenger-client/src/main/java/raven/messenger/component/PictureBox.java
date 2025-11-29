package raven.messenger.component;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class PictureBox extends JPanel {

    private Image lastImage;
    private Icon icon;
    private BoxFit boxFit = BoxFit.CONTAIN;
    private int radius;

    private BoxFit lastBoxFit;
    private int lastRadius;
    private int lastImageWidth;
    private int lastImageHeight;

    private double lastSystemScaleFactor;
    private float lastUserScaleFactor;

    public PictureBox() {
        init();
    }

    private void init() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Insets insets = getInsets();
        int width = getWidth() - (insets.left + insets.left);
        int height = getHeight() - (insets.top + insets.bottom);
        int x = insets.left;
        int y = insets.top;
        if (width > 0 && height > 0) {
            Graphics2D g2 = (Graphics2D) g.create();
            double systemScaleFactor = UIScale.getSystemScaleFactor(g2);
            initImage(width, height, systemScaleFactor);
            if (lastImage != null) {
                HiDPIUtils.paintAtScale1x(g2, x, y, 0, 0, this::paintImpl);
            }
            g2.dispose();
        }
    }

    private void paintImpl(Graphics2D g2, int x, int y, int width, int height, double systemScaleFactor) {
        g2.drawImage(lastImage, x, y, null);
    }

    private void initImage(int width, int height, double systemScaleFactor) {
        if (icon != null) {
            if (width > 0 && height > 0) {
                float userScaleFactor = UIScale.getUserScaleFactor();
                boolean update = lastBoxFit != boxFit || lastRadius != radius
                        || lastImageWidth != width || lastImageHeight != height
                        || lastSystemScaleFactor != systemScaleFactor || lastUserScaleFactor != userScaleFactor;
                if (update) {
                    lastImage = updateImage(width, height, systemScaleFactor);
                    lastBoxFit = boxFit;
                    lastRadius = radius;
                    lastImageWidth = width;
                    lastImageHeight = height;
                    lastSystemScaleFactor = systemScaleFactor;
                    lastUserScaleFactor = userScaleFactor;
                }
            }
        }
    }

    private Image updateImage(int width, int height, double systemScaleFactor) {
        width = (int) Math.ceil(width * systemScaleFactor);
        height = (int) Math.ceil(height * systemScaleFactor);
        BufferedImage buff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buff.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (radius >= 999) {
            g2.fill(new Ellipse2D.Double(0, 0, width, height));
        } else {
            int arc = scale(radius, systemScaleFactor);
            g2.fill(FlatUIUtils.createRoundRectanglePath(0, 0, width, height, arc, arc, arc, arc));
        }
        g2.setComposite(AlphaComposite.SrcIn);
        int imgWidth = icon.getIconWidth();
        int imgHeight = icon.getIconHeight();
        Rectangle rec = scaleSize(imgWidth, imgHeight, width, height);
        if (icon instanceof ImageIcon) {
            ImageIcon imageIcon = (ImageIcon) icon;
            g2.drawImage(new ImageIcon(imageIcon.getImage().getScaledInstance(rec.width, rec.height, Image.SCALE_SMOOTH)).getImage(), rec.x, rec.y, null);
        }
        g2.dispose();
        return buff;
    }

    private Rectangle scaleSize(int width, int height, int comWidth, int comHeight) {
        double widthRation = (double) comWidth / width;
        double heightRatio = (double) comHeight / height;
        double scale;
        if (boxFit == BoxFit.CONTAIN) {
            scale = Math.min(widthRation, heightRatio);
        } else {
            scale = Math.max(widthRation, heightRatio);
        }
        int scaleWidth = (int) (scale * width);
        int scaleHeight = (int) (scale * height);
        int x = (comWidth - scaleWidth) / 2;
        int y = (comHeight - scaleHeight) / 2;
        return new Rectangle(x, y, scaleWidth, scaleHeight);
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        lastImage = null;
        repaint();
    }

    public Icon getIcon() {
        return icon;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    public BoxFit getBoxFit() {
        return boxFit;
    }

    public void setBoxFit(BoxFit boxFit) {
        this.boxFit = boxFit;
        repaint();
    }

    private int scale(int value, double scaleFactor) {
        return (int) Math.ceil(UIScale.scale(value) * scaleFactor);
    }

    public enum BoxFit {
        CONTAIN, COVER
    }
}