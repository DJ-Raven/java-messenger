package raven.messenger.component.profile;

import com.formdev.flatlaf.ui.FlatUIUtils;
import net.coobird.thumbnailator.Thumbnails;
import raven.messenger.component.NetworkIcon;
import raven.swing.AvatarIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ProfileImageEditor extends JPanel {

    private NetworkIcon.IconResource resource;
    private String path;
    private final int profileSize = 150;
    private JLabel labelIcon;
    private boolean moveWidth;
    private boolean editable;
    private int location = -999;
    private int startMouse;
    private int imagePosition = 0;

    public ProfileImageEditor() {
        init();
    }

    public void setProfile(String path) {
        this.path = path;
        resource = new NetworkIcon.IconResource(path);
        int width = -1;
        int height = -1;
        editable = resource.getImageWidth() >= profileSize || resource.getImageHeight() >= profileSize;
        if (resource.getImageWidth() > resource.getImageHeight()) {
            height = profileSize;
            moveWidth = true;
        } else {
            width = profileSize;
            moveWidth = false;
        }
        System.out.println(width+" "+height);
        NetworkIcon icon = new NetworkIcon(resource, width, height);
        labelIcon.setIcon(icon);
    }

    public BufferedImage getEditProfile() throws IOException {
        return Thumbnails.of(path)
                .size(200, 200)
                .outputQuality(1f)
                .crop((enclosingWidth, enclosingHeight, width, height, insetLeft, insetRight, insetTop, insetBottom) -> {
                    int x = 0;
                    int y = 0;
                    if (moveWidth) {
                        double space = (labelIcon.getPreferredSize().getWidth() - profileSize) / 2;
                        double percent = (imagePosition - space) / labelIcon.getPreferredSize().getWidth();
                        x = (int) (percent * width);
                    } else {
                        double space = (labelIcon.getPreferredSize().getHeight() - profileSize) / 2;
                        double percent = (imagePosition - space) / labelIcon.getPreferredSize().getHeight();
                        y = (int) (percent * height);
                    }
                    return new Point(x, y);
                })
                .asBufferedImage();
    }

    private void init() {
        setLayout(new ProfileEditorLayout());
        labelIcon = new JLabel();
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (editable) {
                    startMouse = moveWidth ? e.getX() : e.getY();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (editable) {
                    int move = moveWidth ? e.getXOnScreen() - getLocationOnScreen().x - startMouse : e.getYOnScreen() - getLocationOnScreen().y - startMouse;
                    location = move;
                    labelIcon.revalidate();
                }
            }
        };
        labelIcon.addMouseListener(mouseAdapter);
        labelIcon.addMouseMotionListener(mouseAdapter);
        add(labelIcon);
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        Graphics2D g2 = (Graphics2D) g.create();
        FlatUIUtils.setRenderingHints(g2);
        g2.setColor(getBackground());
        g2.setComposite(AlphaComposite.SrcOver.derive(0.7f));
        int width = getWidth();
        int height = getHeight();
        float size = 150;
        float x = (width - size) / 2;
        float y = (height - size) / 2;
        Area area = new Area(new Rectangle2D.Float(0, 0, width, height));
        area.subtract(new Area(new Ellipse2D.Float(x, y, size, size)));
        g2.fill(area);
        g2.dispose();
    }

    private class ProfileEditorLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return labelIcon.getPreferredSize();
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(0, 0);
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                int width = parent.getWidth();
                int height = parent.getHeight();
                int cx = (width - profileSize) / 2;
                int cy = (height - profileSize) / 2;
                int x;
                int y;
                int cw = labelIcon.getPreferredSize().width;
                int ch = labelIcon.getPreferredSize().height;
                if (location != -999) {
                    if (moveWidth) {
                        x = Math.max(profileSize - cw + cx, Math.min(location, cx));
                        y = (height - ch) / 2;
                        imagePosition = x;
                    } else {
                        x = (width - cw) / 2;
                        y = Math.max(profileSize - ch + cy, Math.min(location, cy));
                        imagePosition = y;
                    }
                } else {
                    x = (width - cw) / 2;
                    y = (height - ch) / 2;
                    if (moveWidth) {
                        imagePosition = x;
                    } else {
                        imagePosition = y;
                    }
                }
                labelIcon.setBounds(x, y, cw, ch);
            }
        }
    }
}
