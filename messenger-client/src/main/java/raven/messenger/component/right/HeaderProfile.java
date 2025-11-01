package raven.messenger.component.right;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import raven.messenger.manager.DialogManager;
import raven.messenger.models.other.ModelImage;
import raven.messenger.store.StoreManager;
import raven.messenger.util.MethodUtil;
import raven.messenger.util.NetworkDataUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class HeaderProfile extends JPanel {

    private int profileSize = 80;
    private ModelImage profile;

    public HeaderProfile() {
        init();
    }

    private void init() {
        setLayout(new ProfileLayout());
        putClientProperty(FlatClientProperties.STYLE, "" +
                "border:10,10,10,10;");
        labelProfile = new JButton();
        labelProfile.setContentAreaFilled(false);
        labelName = new JLabel();
        labelStatus = new JLabel();

        labelStatus.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:$Text.middleForeground;");

        labelProfile.addActionListener(e -> {
            if (profile != null) {
                File file = StoreManager.getInstance().getFile(profile.getImage());
                if (file != null) {
                    DialogManager.getInstance().showViewPhotoDialog(file.getAbsolutePath());
                }
            }
        });

        labelProfile.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10;" +
                "foreground:$Profile.foreground;");
        labelName.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:+5 bold;");

        add(labelProfile);
        add(labelName);
        add(labelStatus);
    }

    public void setData(ModelImage image, String name, String status) {
        if (image == null) {
            labelProfile.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } else {
            labelProfile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        this.profile = image;
        Icon icon = NetworkDataUtil.getNetworkIcon(image, MethodUtil.getProfileString(name), profileSize, profileSize, 999);
        labelProfile.setIcon(icon);
        labelName.setText(name);
        labelStatus.setText(status);
    }

    private JButton labelProfile;
    private JLabel labelName;
    private JLabel labelStatus;

    private class ProfileLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int height = UIScale.scale(profileSize) + parent.getInsets().top + parent.getInsets().bottom;
                return new Dimension(parent.getWidth(), height);
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
                int ps = UIScale.scale(profileSize);
                int xGap = UIScale.scale(10);
                int yGap = UIScale.scale(5);
                int comWidth = width - (ps + xGap);
                int centerY = height / 2;
                int comX = x + ps + xGap;
                labelProfile.setBounds(x, y, ps, ps);
                labelName.setBounds(comX, centerY - (labelName.getPreferredSize().height), comWidth, labelName.getPreferredSize().height);
                labelStatus.setBounds(comX, centerY + yGap, comWidth, labelStatus.getPreferredSize().height);
            }
        }
    }
}
