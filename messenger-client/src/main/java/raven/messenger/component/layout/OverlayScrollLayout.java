package raven.messenger.component.layout;

import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import java.awt.*;

public class OverlayScrollLayout implements LayoutManager {

    private JScrollPane scrollPane;

    public OverlayScrollLayout(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            return scrollPane.getPreferredSize();
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
            Insets insets = parent.getInsets();
            int x = insets.left;
            int y = insets.top;
            int width = parent.getWidth() - (insets.left + insets.right);
            int height = parent.getHeight() - (insets.top + insets.bottom);
            int space = UIScale.scale(6);
            scrollPane.setBounds(x, y, width, height);
            JScrollBar vs = scrollPane.getVerticalScrollBar();
            JScrollBar hs = scrollPane.getHorizontalScrollBar();
            Dimension vSize = vs.getPreferredSize();
            Dimension hSize = hs.getPreferredSize();
            vs.setBounds(x + width - vSize.width - space, y + space, vSize.width, height - space * 2);
            hs.setBounds(x + space, y + height - hSize.height - UIScale.scale(2), width - ((int) (vSize.width + space * 2.5f)), hSize.height);
        }
    }
}
