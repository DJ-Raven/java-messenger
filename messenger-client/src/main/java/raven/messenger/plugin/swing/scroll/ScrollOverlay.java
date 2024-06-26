package raven.messenger.plugin.swing.scroll;

import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ScrollOverlay extends JScrollPane {

    public ScrollOverlay(Component view) {
        super(view);
        setLayout(new ScrollOverlayLayout());
        setComponentZOrder(getVerticalScrollBar(), 0);
        setComponentZOrder(getHorizontalScrollBar(), 1);
    }

    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false;
    }

    private static class ScrollOverlayLayout extends ScrollPaneLayout {
        @Override
        public void layoutContainer(Container parent) {
            super.layoutContainer(parent);
            if (parent instanceof JScrollPane) {
                int space = UIScale.scale(3);
                JScrollPane scroll = (JScrollPane) parent;
                Rectangle rec = scroll.getViewport().getBounds();
                Insets insets = parent.getInsets();
                int rhHeight = 0;
                if (scroll.getColumnHeader() != null) {
                    Rectangle rh = scroll.getColumnHeader().getBounds();
                    rhHeight = rh.height;
                }
                rec.width = scroll.getBounds().width - (insets.left + insets.right);
                rec.height = scroll.getBounds().height - (insets.top + insets.bottom) - rhHeight;
                if (Objects.nonNull(viewport)) {
                    viewport.setBounds(rec);
                }
                int vw = 0;
                if (!Objects.isNull(vsb)) {
                    Rectangle vrc = vsb.getBounds();
                    vrc.x -= space;
                    vrc.y = space;
                    vrc.height -= space * 2;
                    vw = vrc.width;
                    vsb.setBounds(vrc);
                }
                if (!Objects.isNull(hsb)) {
                    Rectangle hrc = hsb.getBounds();
                    hrc.x += space;
                    hrc.y -= space;
                    hrc.width = rec.width - space * 3 - vw;
                    hsb.setBounds(hrc);
                }
            }
        }
    }
}
