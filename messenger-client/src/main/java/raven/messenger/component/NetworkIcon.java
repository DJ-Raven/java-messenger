package raven.messenger.component;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.CubicBezierEasing;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.UIScale;
import raven.messenger.plugin.blurhash.BlurHash;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class NetworkIcon implements Icon {

    private final IconResource resource;
    private final int width;
    private final int height;
    private final boolean fill;
    private CreateShape shape;
    private Image image;
    private Image imageHash;
    private boolean updatedHash;
    private boolean updated;
    private int imageWidth;
    private int imageHeight;
    private double lastSystemScaleFactor;
    private float lastUserScaleFactor;

    public NetworkIcon(IconResource resource, int width, int height) {
        this(resource, width, height, false);
    }

    public NetworkIcon(IconResource resource, int width, int height, boolean fill) {
        this.resource = resource;
        this.width = width;
        this.height = height;
        this.fill = fill;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (!resource.updateImage && !resource.component.containsKey(c)) {
            resource.component.put(c, this);
        }

        updateImage();
        if (image != null || imageHash != null) {
            HiDPIUtils.paintAtScale1x((Graphics2D) g.create(), x, y, imageWidth, imageHeight, (g1, x1, y1, width1, height1, scaleFactor) ->
                    paintImpl(g1, c, x1, y1, width1, height1, scaleFactor));
        }
    }

    private void paintImpl(Graphics2D g2, Component c, int x, int y, int width, int height, double scaleFactor) {
        FlatUIUtils.setRenderingHints(g2);
        if (image != null) g2.drawImage(image, x, y, null);
        if (image == null && imageHash != null && resource.animate < 1) {
            g2.setComposite(AlphaComposite.SrcOver.derive(1f - resource.animate));
            g2.drawImage(imageHash, x, y, null);
        }
        g2.dispose();
    }

    private synchronized void updateImage() {
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration();
        double systemScaleFactor = UIScale.getSystemScaleFactor(gc);
        float userScaleFactor = UIScale.getUserScaleFactor();

        if (lastSystemScaleFactor != systemScaleFactor || lastUserScaleFactor != userScaleFactor) {
            updatedHash = false;
            updated = false;
        }
        if (!resource.updateImage || !resource.updateImageHash || !updatedHash) {
            if (!resource.updateImageHash || !updatedHash) {
                if (resource.isImageHashAble()) {
                    imageHash = resizeImage(resource.imageHash, width, height, systemScaleFactor, true);
                    imageWidth = unScale(imageHash.getWidth(null), systemScaleFactor);
                    imageHeight = unScale(imageHash.getHeight(null), systemScaleFactor);
                    resource.updateImageHash = true;
                    updatedHash = true;
                } else {
                    imageHash = null;
                }
            }
            if (!resource.updateImage || !updated) {
                if (resource.isImageAble()) {
                    image = resizeImage(resource.image, width, height, systemScaleFactor, false);
                    imageWidth = unScale(image.getWidth(null), systemScaleFactor);
                    imageHeight = unScale(image.getHeight(null), systemScaleFactor);
                    resource.updateImage = true;
                    updated = true;
                } else {
                    image = null;
                }
            }
            if (imageHash == null && image == null) {
                imageWidth = 0;
                imageHeight = 0;
            }
            lastSystemScaleFactor = systemScaleFactor;
            lastUserScaleFactor = userScaleFactor;
        }
    }

    private Image resizeImage(Image icon, int width, int height, double systemScaleFactor, boolean upScaling) {
        width = scale(width, systemScaleFactor);
        height = scale(height, systemScaleFactor);
        int round = scale(resource.round, systemScaleFactor);
        int w;
        int h;
        if (fill || upScaling) {
            w = Math.max(width, -1);
            h = Math.max(height, -1);
        } else {
            w = width > -1 ? Math.min(width, icon.getWidth(null)) : -1;
            h = height > -1 ? Math.min(height, icon.getHeight(null)) : -1;
        }
        Image img = new ImageIcon(icon.getScaledInstance(w, h, Image.SCALE_SMOOTH)).getImage();
        Shape createShape = shape == null ? null : shape.getShape();
        return round > 0 || createShape != null ? roundImage(img, round, createShape, systemScaleFactor) : img;
    }

    private Image roundImage(Image image, int round, Shape shape, double systemScaleFactor) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        if (width <= 0 || height <= 0) {
            return null;
        }
        BufferedImage buff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = buff.createGraphics();
        FlatUIUtils.setRenderingHints(g);
        if (shape != null) {
            AffineTransform oldTran = g.getTransform();
            if (systemScaleFactor != 0) {
                g.scale(systemScaleFactor, systemScaleFactor);
            }
            g.fill(shape);
            g.setTransform(oldTran);
        } else {
            if (round == 999) {
                g.fill(new Ellipse2D.Double(0, 0, width, height));
            } else {
                int r = UIScale.scale(round);
                g.fill(new RoundRectangle2D.Double(0, 0, width, height, r, r));
            }
        }
        g.setComposite(AlphaComposite.SrcIn);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return buff;
    }

    @Override
    public int getIconWidth() {
        updateImage();
        return imageWidth;
    }

    @Override
    public int getIconHeight() {
        updateImage();
        return imageHeight;
    }

    public CreateShape getShape() {
        return shape;
    }

    public void setShape(CreateShape shape) {
        this.shape = shape;
        updatedHash = false;
        updated = false;
        resource.update();
    }

    public IconResource getResource() {
        return resource;
    }

    private int scale(int value, double scaleFactor) {
        return (int) Math.ceil(UIScale.scale(value) * scaleFactor);
    }

    private int unScale(int value, double scaleFactor) {
        return (int) Math.ceil(value / scaleFactor);
    }

    public static class IconResource {

        private Animator animator;
        protected float animate;
        protected Map<Component, NetworkIcon> component = new HashMap<>();
        protected String path;
        protected String blurHash;
        protected Image image;
        protected Image imageHash;
        protected int round;
        protected boolean updateImage;
        protected boolean updateImageHash;

        public IconResource(String path) {
            this(path, 0);
        }

        public IconResource(String path, int round) {
            this.round = round;
            setImage(path);
        }

        public IconResource(String blurHash, int width, int height) {
            this(blurHash, width, height, 0);
        }

        public IconResource(String blurHash, int width, int height, int round) {
            this.round = round;
            setImageHash(blurHash, width, height);
        }

        private void update() {
            if (component != null) {
                for (Map.Entry<Component, NetworkIcon> entry : component.entrySet()) {
                    entry.getValue().updated = false;
                    entry.getValue().updatedHash = false;
                    entry.getKey().repaint();
                }
                component.clear();
            }
        }

        private void startAnimation() {
            if (animator == null && component != null) {
                animator = new Animator(350, v -> {
                    animate = v;
                    update();
                });
                animator.setInterpolator(CubicBezierEasing.EASE);
            }
            if (animator != null) {
                if (animator.isRunning()) {
                    animator.stop();
                }
                animator.start();
            }
        }

        public void setImage(String path) {
            this.path = path;
            image = new ImageIcon(path).getImage();
            updateImage = false;
            animate = 0;
            startAnimation();
        }

        public void setImageHash(String blurHash, int width, int height) {
            this.blurHash = blurHash;
            imageHash = new ImageIcon(BlurHash.decodeAndDraw(blurHash, width, height, 1, BufferedImage.TYPE_INT_ARGB)).getImage();
            updateImageHash = false;
            update();
        }

        public boolean isImageHashAble() {
            return imageHash != null;
        }

        public boolean isImageAble() {
            return image != null;
        }

        public int getImageWidth() {
            if (image == null) {
                return 0;
            }
            return image.getWidth(null);
        }

        public int getImageHeight() {
            if (image == null) {
                return 0;
            }
            return image.getHeight(null);
        }
    }

    public interface CreateShape {
        Shape getShape();
    }
}
