package raven.messenger.component.left;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;
import raven.messenger.component.PictureBox;
import raven.messenger.component.ProfileStatus;
import raven.messenger.component.StringIcon;
import raven.messenger.models.other.ModelImage;
import raven.messenger.models.response.ModelChatListItem;
import raven.messenger.models.response.ModelLastMessage;
import raven.messenger.socket.MessageType;
import raven.messenger.util.MethodUtil;
import raven.messenger.util.NetworkDataUtil;

import javax.swing.*;
import java.awt.*;

public class Item extends JButton {

    private final ModelChatListItem data;

    public Item(ModelChatListItem data) {
        this.data = data;
        init();
    }

    private void init() {
        putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "selectedBackground:fade($Button.selectedBackground,40%);");
        setLayout(new MigLayout("wrap,fill,insets 3", "[fill]"));
        panelLabel = new PanelLabel();
        if (data.getProfile() != null) {
            PictureBox picture = new PictureBox();
            picture.setRadius(999);
            picture.setBoxFit(PictureBox.BoxFit.COVER);
            ModelImage image = data.getProfile();
            picture.setImageHash(image.getHash(), 180, 180, image.getImage());
            profile = new ProfileStatus(NetworkDataUtil.getNetworkIcon(data.getProfile(), data.getProfileString(), 50, 50, 999));
        } else {
            profile = new ProfileStatus(new StringIcon(data.getProfileString(), UIManager.getColor("Component.accentColor"), 50, 50));
        }
        profile.setActiveStatus(data.isActiveStatus());
        add(profile, "dock west,width 50,height 50,gap 6 10");
        add(panelLabel);
    }

    public ModelChatListItem getData() {
        return data;
    }

    public boolean isActiveStatus() {
        return profile.isActiveStatus();
    }

    public void setActiveStatus(boolean activeStatus) {
        data.setActiveStatus(activeStatus);
        profile.setActiveStatus(activeStatus);
    }

    public void setLastMessage(ModelLastMessage lastMessage) {
        panelLabel.setLastMessage(lastMessage);
    }

    private ProfileStatus profile;
    private PanelLabel panelLabel;

    public class PanelLabel extends JPanel {

        public PanelLabel() {
            init();
        }

        private void init() {
            setOpaque(false);
            setLayout(new LabelLayout());
            lbName = new JLabel(data.getName());
            if (data.isGroup()) {
                lbName.setHorizontalTextPosition(SwingConstants.LEADING);
                lbName.setIcon(MethodUtil.createIcon("raven/messenger/icon/group.svg", 0.33f));
            }
            lbDescriptionName = new JLabel();
            lbDescription = new JLabel();
            lbDescription.setHorizontalTextPosition(SwingConstants.LEADING);
            lbStatus = new JLabel();

            lbName.putClientProperty(FlatClientProperties.STYLE, "" +
                    "font:bold;");
            lbStatus.putClientProperty(FlatClientProperties.STYLE, "" +
                    "foreground:$Text.mediumForeground;");

            lbDescription.putClientProperty(FlatClientProperties.STYLE, "" +
                    "foreground:$Text.mediumForeground;");

            add(lbName);
            add(lbDescriptionName);
            add(lbDescription);
            add(lbStatus);
            setLastMessage(data.getLastMessage());
        }

        private void setLastMessage(ModelLastMessage lastMessage) {
            if (lastMessage != null) {
                lbDescriptionName.setText(lastMessage.isYou() ? "You" : "");
                if (lastMessage.getMessageType() == MessageType.TEXT) {
                    lbDescription.setText(lastMessage.toString());
                    lbDescription.setIcon(null);
                } else if (lastMessage.getMessageType() == MessageType.VOICE) {
                    lbDescription.setText("Voice");
                    lbDescription.setIcon(MethodUtil.createIcon("raven/messenger/icon/voice.svg", 0.3f, lbDescription));
                } else if (lastMessage.getMessageType() == MessageType.PHOTO) {
                    lbDescription.setText("Photo");
                    lbDescription.setIcon(MethodUtil.createIcon("raven/messenger/icon/photo.svg", 0.3f, lbDescription));
                } else if (lastMessage.getMessageType() == MessageType.FILE) {
                    lbDescription.setText("File");
                    lbDescription.setIcon(MethodUtil.createIcon("raven/messenger/icon/file.svg", 0.3f, lbDescription));
                }
            } else {
                if (data.isGroup()) {
                    lbDescription.setText("created");
                } else {
                    lbDescription.setText("joined");
                }
            }
        }

        private JLabel lbName;
        private JLabel lbDescriptionName;
        private JLabel lbDescription;
        private JLabel lbStatus;

        private class LabelLayout implements LayoutManager {

            private final int labelGap = 3;
            private final int gap = 6;

            @Override
            public void addLayoutComponent(String name, Component comp) {
            }

            @Override
            public void removeLayoutComponent(Component comp) {
            }

            @Override
            public Dimension preferredLayoutSize(Container parent) {
                synchronized (parent.getTreeLock()) {
                    Insets insets = parent.getInsets();
                    int g = UIScale.scale(gap);
                    int width = insets.left + insets.right;
                    int height = insets.top + insets.bottom + g;
                    height += lbName.getPreferredSize().height;
                    height += Math.max(lbDescription.getPreferredSize().height, lbStatus.getPreferredSize().height);
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
                    int g = UIScale.scale(gap);
                    int lbg = UIScale.scale(labelGap);
                    int x = insets.left;
                    int y = insets.top;
                    int width = parent.getWidth() - (insets.left + insets.right);
                    int height = parent.getHeight() - (insets.top + insets.bottom);
                    Dimension statusSize = lbStatus.getPreferredSize();
                    lbName.setBounds(x, y, width, lbName.getPreferredSize().height);

                    int descriptionLabelWidth = lbDescriptionName.getPreferredSize().width;
                    if (descriptionLabelWidth == 0) {
                        lbg = 0;
                    }
                    int descriptionWidth = width - statusSize.width - descriptionLabelWidth - g - lbg;

                    lbDescriptionName.setBounds(x, y + height - lbDescriptionName.getPreferredSize().height, descriptionLabelWidth, lbDescriptionName.getPreferredSize().height);
                    lbDescription.setBounds(x + descriptionLabelWidth + lbg, y + height - lbDescription.getPreferredSize().height, descriptionWidth, lbDescription.getPreferredSize().height);

                    lbStatus.setBounds(x + width - statusSize.width, y + height - statusSize.height, statusSize.width, statusSize.height);
                }
            }
        }
    }
}
