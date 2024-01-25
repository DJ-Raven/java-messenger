package raven.messenger.component;


import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.UIScale;
import raven.messenger.plugin.blurhash.BlurHash;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class PictureBox extends JPanel {

    private Animator animator;
    private float animate = 1f;

    private Image renderHashImage;
    private String imageHash;
    private String imageUrl;
    private Image render;
    private Icon image;
    private int imageHashWidth;
    private int imageHashHeight;
    private BoxFit boxFit = BoxFit.CONTAIN;
    private int radius;

    private BoxFit oldBoxFit;
    private int oldRadius;
    private int oldWidth;
    private int oldHeight;
    private boolean updateImage;
    private boolean updateImageHash;
    private boolean loadUrl;
    private Dimension controlLayout;
    private LayoutManager oldLayout;

    public PictureBox() {
        init();
    }

    private void init() {
        setOpaque(false);
    }

    private void startAnimator() {
        if (animator == null) {
            animator = new Animator(500, (float f) -> {
                animate = 1f - f;
                repaint();
            });
        }
        if (animator.isRunning()) {
            animator.stop();
        }
        animator.start();
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
            if (loadUrl) {
                if (imageUrl != null) {
                    loadURL(imageUrl);
                }
                loadUrl = false;
            }
            initImage();
            Graphics2D g2 = (Graphics2D) g.create();
            if (render != null) {
                if (imageUrl != null && imageHash == null && animate < 1) {
                    g2.setComposite(AlphaComposite.SrcOver.derive(1f - animate));
                }
                g2.drawImage(render, x, y, null);
            }
            if (animate > 0 && renderHashImage != null) {
                g2.setComposite(AlphaComposite.SrcOver.derive(animate));
                g2.drawImage(renderHashImage, x, y, null);
            }
            g2.dispose();
        }
    }

    private void initImage() {
        if (image != null || imageHash != null) {
            Insets insets = getInsets();
            int width = getWidth() - (insets.left + insets.left);
            int height = getHeight() - (insets.top + insets.bottom);
            if (width > 0 && height > 0) {
                boolean update = oldBoxFit != boxFit || oldWidth != width || oldHeight != height || oldRadius != radius;
                if (image != null && (update || !updateImage)) {
                    render = createImageRender(false);
                    updateImage = true;
                }
                if (animate != 0 && imageHash != null && (update || !updateImageHash)) {
                    renderHashImage = createImageRender(true);
                    updateImageHash = true;
                }
                if (update) {
                    oldBoxFit = boxFit;
                    oldRadius = radius;
                    oldWidth = width;
                    oldHeight = height;
                }
            }
        }
    }

    private Image createImageRender(boolean hash) {
        Insets insets = getInsets();
        int width = getWidth() - (insets.left + insets.left);
        int height = getHeight() - (insets.top + insets.bottom);
        BufferedImage buff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buff.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (radius >= 999) {
            g2.fill(new Ellipse2D.Double(0, 0, width, height));
        } else {
            int arc = UIScale.scale(radius);
            g2.fill(FlatUIUtils.createRoundRectanglePath(0, 0, width, height, arc, arc, arc, arc));
        }
        g2.setComposite(AlphaComposite.SrcIn);
        if (hash) {
            Rectangle rec = scaleSize(imageHashWidth, imageHashHeight);
            BufferedImage imgHash = BlurHash.decodeAndDraw(imageHash, rec.width, rec.height, 1, BufferedImage.TYPE_INT_ARGB);
            g2.drawImage(imgHash, rec.x, rec.y, null);
        } else {
            int imgWidth = image.getIconWidth();
            int imgHeight = image.getIconHeight();
            Rectangle rec = scaleSize(imgWidth, imgHeight);
            ImageIcon icon = (ImageIcon) image;
            g2.drawImage(new ImageIcon(icon.getImage().getScaledInstance(rec.width, rec.height, Image.SCALE_SMOOTH)).getImage(), rec.x, rec.y, null);
        }
        g2.dispose();
        return buff;
    }

    private Rectangle scaleSize(int width, int height) {
        Insets insets = getInsets();
        int comWidth = getWidth() - (insets.left + insets.left);
        int comHeight = getHeight() - (insets.top + insets.bottom);
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

    private synchronized void loadURL(String url) {
        animate = 1f;
        new Thread(() -> {
            try {
                BufferedImage img = ImageIO.read(new URL(url));
                if (img != null) {
                    image = new ImageIcon(img);
                    updateImage = false;
                    initImage();
                    startAnimator();
                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }).start();
    }

    public void setImage(Icon image) {
        this.image = image;
        this.imageHash = null;
        this.imageUrl = null;
        this.updateImage = false;
        repaint();
    }

    public void setImageHash(String imageHash, int width, int height, String url) {
        this.imageHash = imageHash;
        this.imageUrl = url;
        this.imageHashWidth = width;
        this.imageHashHeight = height;
        this.updateImageHash = false;
        this.loadUrl = true;
        repaint();
    }

    public void setImageUrl(String url) {
        this.imageHash = null;
        this.imageUrl = url;
        this.loadUrl = true;
        repaint();
    }

    public Icon getImage() {
        return image;
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

    public Dimension getControlLayout() {
        return controlLayout;
    }

    public void setControlLayout(Dimension controlLayout) {
        this.controlLayout = controlLayout;
        if (controlLayout != null) {
            if (oldLayout == null) {
                oldLayout = getLayout();
            }
            installControlLayout();
        } else {
            setLayout(oldLayout);
            oldLayout = null;
        }
    }

    private void installControlLayout() {

    }

    public static enum BoxFit {
        CONTAIN, COVER
    }
}