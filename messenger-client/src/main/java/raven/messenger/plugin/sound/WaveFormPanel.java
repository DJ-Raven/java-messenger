package raven.messenger.plugin.sound;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class WaveFormPanel extends JPanel {

    private final List<WaveFormListener> events = new ArrayList<>();
    private WaveFormData waveFormData;
    private BufferedImage bufferedImage;
    private BufferedImage progressBufferedImage;

    private float progress;
    private boolean dragged;

    private boolean updated = false;
    private int lastWidth;
    private int lastHeight;
    private double lastSystemScaleFactor;
    private float lastUserScaleFactor;

    public WaveFormPanel() {
        init();
    }

    private void init() {
        setForeground(new Color(56, 76, 93));
        MouseAdapter mouseEvent = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (waveFormData != null) {
                        float v = getClickValue(e.getPoint());
                        runEventWaveClick(v);
                    }
                    dragged = false;
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (!dragged) {
                        dragged = true;
                        runEventWaveDrag();
                    }
                    float v = getClickValue(e.getPoint());
                    if (v < 0) {
                        v = 0;
                    } else if (v > 1) {
                        v = 1;
                    }
                    setProgress(v);
                }
            }
        };
        addMouseListener(mouseEvent);
        addMouseMotionListener(mouseEvent);
    }

    private float getClickValue(Point point) {
        int x = (getWidth() - scale(waveFormData.getWidth())) / 2;
        int width = scale(waveFormData.getWidth());
        float v = point.x - x;
        return v / width;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (waveFormData != null) {
            int x = (getWidth() - scale(waveFormData.getWidth())) / 2;
            int y = (getHeight() - scale(waveFormData.getHeight())) / 2;
            int width = scale(waveFormData.getWidth());
            int height = scale(waveFormData.getHeight());
            if ((width > 0 && height > 0)) {
                Graphics2D g2 = (Graphics2D) g.create();
                float userScaleFactor = UIScale.getUserScaleFactor();
                double systemScaleFactor = UIScale.getSystemScaleFactor(g2);
                if (!updated || width != lastWidth || height != lastHeight
                        || lastSystemScaleFactor != systemScaleFactor || lastUserScaleFactor != userScaleFactor
                ) {
                    bufferedImage = createBufferedImage(width, height, systemScaleFactor);
                    progressBufferedImage = copyBufferedImage(bufferedImage, getProgressColor());
                    lastWidth = width;
                    lastHeight = height;
                    lastSystemScaleFactor = systemScaleFactor;
                    lastUserScaleFactor = userScaleFactor;
                    updated = true;
                }
                HiDPIUtils.paintAtScale1x(g2, x, y, width, height, this::paintImpl);
                g2.dispose();
            }
        }
    }

    private void paintImpl(Graphics2D g2, int x, int y, int width, int height, double systemScaleFactor) {
        g2.drawImage(bufferedImage, x, y, null);
        if (progress > 0) {
            double progressValue = progress * width;
            g2.setClip(new Rectangle2D.Double(x, y, progressValue, height));
            g2.drawImage(progressBufferedImage, x, y, null);
        }
    }

    private BufferedImage createBufferedImage(int width, int height, double systemScaleFactor) {
        width = (int) Math.ceil(width * systemScaleFactor);
        height = (int) Math.ceil(height * systemScaleFactor);
        int lineSize = (int) scale(waveFormData.getLineSize(), systemScaleFactor);
        float lineSizeNoCeil = scaleNoFloor(waveFormData.getLineSize(), systemScaleFactor);
        float space = scaleNoFloor(waveFormData.getSpace(), systemScaleFactor);
        float center = scaleNoFloor(waveFormData.getHeight(), systemScaleFactor) / 2f;
        BufferedImage buffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = buffImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        float x = 0;
        g.setColor(getForeground());
        float h = scale(waveFormData.getHeight(), systemScaleFactor);
        for (float v : waveFormData.getData()) {
            float value = Math.min(Math.max(lineSize, scale(v, systemScaleFactor)), h);
            g.fill(new RoundRectangle2D.Double(x, center - value / 2, lineSize, value, lineSize, lineSize));
            x += (lineSizeNoCeil + space);
        }
        g.dispose();
        return buffImage;
    }

    private BufferedImage copyBufferedImage(BufferedImage bufferedImage, Color color) {
        BufferedImage newBuff = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
        Graphics2D g = newBuff.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(bufferedImage, 0, 0, null);
        g.setComposite(AlphaComposite.SrcIn);
        g.setColor(color);
        g.fill(new Rectangle(0, 0, newBuff.getWidth(), newBuff.getHeight()));
        g.dispose();
        return newBuff;
    }

    public void setWaveFormData(WaveFormData waveFormData) {
        this.waveFormData = waveFormData;
        updated = false;
        repaint();
    }

    public Color getProgressColor() {
        if (FlatLaf.isLafDark()) {
            return ColorFunctions.lighten(getForeground(), 0.15f);
        } else {
            return ColorFunctions.darken(getForeground(), 0.15f);
        }
    }

    public void setProgress(float progress) {
        progress = Math.round(progress * 100);
        progress /= 100;
        if (this.progress != progress) {
            this.progress = progress;
            repaint();
        }
    }

    private void runEventWaveClick(float v) {
        for (WaveFormListener event : events) {
            event.onClick(v);
        }
    }

    private void runEventWaveDrag() {
        for (WaveFormListener event : events) {
            event.onDrag();
        }
    }

    public void addWaveFormListener(WaveFormListener event) {
        events.add(event);
    }

    private int scale(int v) {
        return UIScale.scale(v);
    }

    private float scale(float value, double scaleFactor) {
        return (int) Math.floor(UIScale.scale(value) * scaleFactor);
    }

    private float scaleNoFloor(float value, double scaleFactor) {
        return (float) (UIScale.scale(value) * scaleFactor);
    }
}