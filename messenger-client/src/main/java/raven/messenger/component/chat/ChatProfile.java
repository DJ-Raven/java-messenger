package raven.messenger.component.chat;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

public class ChatProfile extends JPanel {

    public ChatProfile() {
        init();
    }

    private void init() {
        setLayout(new ChatProfileLayout());
        labelImage = new JLabel();
        labelImage.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:$Component.accentColor;" +
                "foreground:$Profile.foreground;");
        add(labelImage);
    }

    public void setImageLocation(int imageLocation) {
        if (this.imageLocation != imageLocation) {
            this.imageLocation = imageLocation;
            revalidate();
        }
    }

    public void setImage(Icon image) {
        labelImage.setIcon(image);
    }

    private JLabel labelImage;
    private int imageLocation;

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        imageLocation = getVisibleRect().y + getVisibleRect().height;
    }

    private class ChatProfileLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(0, 0);
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
                int with = labelImage.getPreferredSize().width;
                int height = labelImage.getPreferredSize().height;
                int x = 0;
                int y = Math.max(imageLocation - height, 0);
                labelImage.setBounds(x, y, with, height);
            }
        }
    }
}
