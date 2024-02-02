package raven.messenger.component.chat;

import raven.messenger.component.StringIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class ChatProfile extends JPanel {

    public void setImageLocation(int imageLocation) {
        if (this.imageLocation != imageLocation) {
            this.imageLocation = imageLocation;
            repaint();
        }
    }

    public Icon getImage() {
        return image;
    }

    public void setImage(Icon image) {
        this.image = image;
        updateImage = false;
        repaint();
    }

    private Image render;
    private Icon image;
    private int imageLocation;

    private int oldSize;
    private boolean updateImage;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            int width = getWidth();
            int height = getHeight();
            int size = Math.min(width, height);
            if (width > 0 && height > 0 && (oldSize != size || !updateImage)) {
                render = createImage(size);
                updateImage = true;
                oldSize = size;
            }
            if (render != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                int x = (width - size) / 2;
                int y = Math.max(imageLocation - size, 0);
                g2.drawImage(render, x, y, this);
                g2.dispose();
            }
        }
    }

    private Image createImage(int size) {
        Image scaleImage = getScaleImage(image, size);
        BufferedImage buff = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buff.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fill(new Ellipse2D.Double(0, 0, size, size));
        g2.setComposite(AlphaComposite.SrcIn);
        int imgWidth = scaleImage.getWidth(null);
        int imgHeight = scaleImage.getHeight(null);
        int x = (size - imgWidth) / 2;
        int y = (size - imgHeight) / 2;
        g2.drawImage(scaleImage, x, y, null);
        g2.dispose();
        return buff;
    }

    private Image getScaleImage(Icon icon, int size) {
        if (icon instanceof StringIcon) {
            return ((StringIcon) icon).getImage(this);
        }
        int iconWidth = icon.getIconWidth();
        int iconHeight = icon.getIconHeight();
        Image image = ((ImageIcon) icon).getImage();
        if (iconWidth > iconHeight) {
            return new ImageIcon(image.getScaledInstance(-1, size, Image.SCALE_SMOOTH)).getImage();
        } else {
            return new ImageIcon(image.getScaledInstance(size, -1, Image.SCALE_SMOOTH)).getImage();
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        imageLocation = getVisibleRect().y + getVisibleRect().height;
    }
}
