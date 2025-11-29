package raven.messenger.component.layout;

import com.formdev.flatlaf.util.UIScale;
import raven.messenger.component.chat.ChatComponent;

import java.awt.*;

public class ChatLayout implements LayoutManager {

    private final LayoutSize layoutSize = new LayoutSize();

    @Override
    public void addLayoutComponent(String name, Component comp) {

    }

    @Override
    public void removeLayoutComponent(Component comp) {

    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            layoutSize.init();
            Insets insets = parent.getInsets();
            int width = insets.left + insets.right + (layoutSize.space);
            int height = insets.top + insets.bottom;
            int count = parent.getComponentCount();
            int size = 0;
            for (int i = 0; i < count; i++) {
                Component com = parent.getComponent(i);
                if (com.isVisible()) {
                    height += com.getPreferredSize().height;
                    size++;
                }
            }
            if (size > 1) {
                height += layoutSize.gap * (size - 1);
            }
            return new Dimension(width, height);
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
            layoutSize.init();
            int count = parent.getComponentCount();
            for (int i = 0; i < count; i++) {
                Component com = parent.getComponent(i);
                if (com.isVisible() && com instanceof ChatComponent) {
                    ChatComponent chatComponent = (ChatComponent) com;
                    int type = chatComponent.getType();
                    Dimension size = chatComponent.getPreferredSize();
                    int chatWidth = Math.min(size.width, Math.min(layoutSize.maxWidth, width - layoutSize.space));
                    if (type == 1) {
                        chatComponent.setBounds(x, y, chatWidth, size.height);
                    } else if (type == 2) {
                        chatComponent.setBounds(x + width - chatWidth, y, chatWidth, size.height);
                    }
                    y += size.height + layoutSize.gap;
                }
            }
        }
    }


    private static class LayoutSize {

        private int space;
        private int maxWidth;
        private int gap;

        public void init() {
            initWidthScale(100, 450, 8);
        }

        public void initWidthScale(int space, int maxWidth, int gap) {
            this.space = UIScale.scale(space);
            this.maxWidth = UIScale.scale(maxWidth);
            this.gap = UIScale.scale(gap);
        }
    }
}
