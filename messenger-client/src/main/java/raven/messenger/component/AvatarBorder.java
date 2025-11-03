package raven.messenger.component;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.UIScale;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import static com.formdev.flatlaf.util.UIScale.scale;

public class AvatarBorder extends EmptyBorder {

    private final int arc;
    private final int lineWidth;
    private float alpha = 1f;

    public AvatarBorder() {
        this(2f);
    }

    public AvatarBorder(float size) {
        this(999, (int) size);
    }

    public AvatarBorder(int arc) {
        this(arc, 2);
    }

    public AvatarBorder(int arc, int size) {
        super(size, size, size, size);
        this.arc = arc;
        this.lineWidth = size;
    }

    public AvatarBorder alpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        return scaleInsets(c, insets, top, left, bottom, right);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (alpha > 0) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                FlatUIUtils.setRenderingHints(g2);
                g2.setPaint(new GradientPaint(x, y, c.getBackground(), 0, height, ColorFunctions.darken(c.getBackground(), 0.13f)));
                int arc = this.arc >= 999 ? height : UIScale.scale(this.arc);
                int lineWidth = UIScale.scale(this.lineWidth);
                int inArc = arc - lineWidth;
                Area area = new Area(new RoundRectangle2D.Float(x, y, width, height, arc, arc));
                area.subtract(new Area(new RoundRectangle2D.Float(x + lineWidth, y + lineWidth, width - lineWidth * 2, height - lineWidth * 2, inArc, inArc)));
                g2.setComposite(AlphaComposite.SrcOver.derive(Math.min(alpha, 1f)));
                g2.fill(area);
            } finally {
                g2.dispose();
            }
        }
    }

    protected static Insets scaleInsets(Component c, Insets insets, int top, int left, int bottom, int right) {
        boolean leftToRight = left == right || c == null || c.getComponentOrientation().isLeftToRight();
        insets.left = scale(leftToRight ? left : right);
        insets.top = scale(top);
        insets.right = scale(leftToRight ? right : left);
        insets.bottom = scale(bottom);
        return insets;
    }
}
