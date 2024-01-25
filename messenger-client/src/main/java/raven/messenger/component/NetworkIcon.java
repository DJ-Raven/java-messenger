package raven.messenger.component;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.CubicBezierEasing;
import com.formdev.flatlaf.util.UIScale;
import raven.messenger.plugin.blurhash.BlurHash;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class NetworkIcon implements Icon {

    private Shape shape;
    private IconResource resource;
    private Image image;
    private Image imageHash;
    private int width = -1;
    private int height = -1;
    private int imageWidth;
    private int imageHeight;
    private boolean fill;

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
        if (resource.component != c) {
            resource.component = c;
        }
        updateImage();
        if (image != null || imageHash != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            FlatUIUtils.setRenderingHints(g2);
            if (image != null) g2.drawImage(image, x, y, c);
            if (image == null && imageHash != null && resource.animate < 1) {
                g2.setComposite(AlphaComposite.SrcOver.derive(1f - resource.animate));
                g2.drawImage(imageHash, x, y, c);
            }
            if (shape != null) {
                if (resource.animate > 0) {
                    g2.setComposite(AlphaComposite.SrcOver);
                }
                g2.setColor(c.getParent().getForeground());
                g2.fill(shape);
            }
            g2.dispose();
        }
    }

    private synchronized void updateImage() {
        if (resource.updateImage == false || resource.updateImageHash == false) {
            if (resource.updateImageHash == false) {
                if (resource.isImageHashAble()) {
                    imageHash = resizeImage(resource.imageHash, width, height);
                    imageWidth = imageHash.getWidth(null);
                    imageHeight = imageHash.getHeight(null);
                    resource.updateImageHash = true;
                } else {
                    imageHash = null;
                }
            }
            if (resource.updateImage == false) {
                if (resource.isImageAble()) {
                    image = resizeImage(resource.image, width, height);
                    imageWidth = image.getWidth(null);
                    imageHeight = image.getHeight(null);
                    resource.updateImage = true;
                } else {
                    image = null;
                }
            }
            if (imageHash == null && image == null) {
                imageWidth = 0;
                imageHeight = 0;
            }
        }
    }

    private Image resizeImage(Image icon, int width, int height) {
        int w;
        int h;
        if (fill) {
            w = width > -1 ? width : -1;
            h = height > -1 ? height : -1;
        } else {
            w = width > -1 ? Math.min(width, icon.getWidth(null)) : -1;
            h = height > -1 ? Math.min(height, icon.getHeight(null)) : -1;
        }
        Image img = new ImageIcon(icon.getScaledInstance(w, h, Image.SCALE_SMOOTH)).getImage();
        return resource.round > 0 ? roundImage(img, resource.round) : img;
    }

    private Image roundImage(Image image, int round) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        BufferedImage buff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = buff.createGraphics();
        FlatUIUtils.setRenderingHints(g);
        if (round == 999) {
            g.fill(new Ellipse2D.Double(0, 0, width, height));
        } else {
            int r = UIScale.scale(round);
            g.fill(new RoundRectangle2D.Double(0, 0, width, height, r, r));
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

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        if (this.shape != shape) {
            this.shape = shape;
            resource.update();
        }
    }

    public IconResource getResource() {
        return resource;
    }

    public static class IconResource {

        private Animator animator;
        protected float animate;
        protected Component component;
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
                component.repaint();
            }
        }

        private void startAnimation() {
            if (animator == null && component != null) {
                animator = new Animator(350, new Animator.TimingTarget() {
                    @Override
                    public void timingEvent(float v) {
                        animate = v;
                        component.repaint();
                    }
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
}
