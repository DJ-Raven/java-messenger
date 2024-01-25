package raven.messenger.component.chat;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import raven.messenger.component.chat.item.ItemFile;
import raven.messenger.component.chat.item.ItemMessage;
import raven.messenger.component.chat.item.ItemImage;
import raven.messenger.component.chat.item.ItemSound;
import raven.messenger.component.chat.model.ChatFileData;
import raven.messenger.component.chat.model.ChatPhotoData;
import raven.messenger.component.chat.model.ChatVoiceData;
import raven.messenger.util.MethodUtil;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;

public class DefaultChatComponentBuilder implements ChatComponentBuilder {

    @Override
    public void buildRecipient(ChatPanel chatPanel, Recipient recipient) {
        final int type = 1;
        ChatComponent chatComponent = getLastItemRecipient(recipient.getId(), chatPanel.getPanel(), recipient.isTop());
        boolean addNew = chatComponent == null;
        if (chatComponent == null) {
            chatComponent = new ChatComponent(type, recipient.getId());
            if (recipient.getProfile() != null) {
                chatComponent.setChatProfile(buildProfileImage(recipient.getProfile()));
            }
        }
        JLabel lbTime = buildTime(recipient.getTime());
        if (recipient.getMessage() != null && !recipient.getMessage().isEmpty()) {
            ItemMessage itemMessage = buildTextMessage(recipient.getMessage(), type);
            itemMessage.addTimePanel(lbTime);
            chatComponent.addItem(itemMessage, recipient.isTop());
        } else if (recipient.getVoice() != null) {
            ItemMessage itemMessage = buildVoidMessage(recipient.getVoice(), type);
            itemMessage.addTimePanel(lbTime);
            chatComponent.addItem(itemMessage, recipient.isTop());
        } else if (recipient.getPhoto() != null) {
            ItemImage item = buildPhotoMessage(recipient.getPhoto(), type);
            changeLabelTimeStyle(lbTime);
            item.addTimePanel(lbTime);
            chatComponent.addItem(item, recipient.isTop());
        } else if (recipient.getFile() != null) {
            ItemMessage itemMessage = buildFileMessage(recipient.getFile(), type);
            itemMessage.addTimePanel(lbTime);
            chatComponent.addItem(itemMessage, recipient.isTop());
        }
        chatComponent.updateLevel();
        if (addNew) {
            chatPanel.addItem(chatComponent, recipient.isTop(), recipient.getConstraints());
            if (recipient.isAutoRefresh()) {
                updateLayout(chatPanel.getPanel());
            }
        } else {
            if (recipient.isAutoRefresh()) {
                updateLayout(chatComponent);
            }
        }
    }

    @Override
    public void buildMyself(ChatPanel chatPanel, Myself myself) {
        final int type = 2;
        ChatComponent chatComponent = getLastItemMyself(chatPanel.getPanel(), myself.isTop());
        boolean addNew = chatComponent == null;
        if (chatComponent == null) {
            chatComponent = new ChatComponent(type, 0);
        }
        JLabel lbTime = buildTime(myself.getTime());
        JLabel lbSent = buildSent(myself.isSent());
        myself.putComponent("time", lbTime);
        myself.putComponent("sent", lbSent);
        if (myself.getMessage() != null && !myself.getMessage().isEmpty()) {
            ItemMessage itemMessage = buildTextMessage(myself.getMessage(), type);
            itemMessage.addTimePanel(lbTime);
            itemMessage.addTimePanel(lbSent);
            chatComponent.addItem(itemMessage, myself.isTop());
        } else if (myself.getVoice() != null) {
            ItemMessage itemMessage = buildVoidMessage(myself.getVoice(), type);
            itemMessage.addTimePanel(lbTime);
            itemMessage.addTimePanel(lbSent);
            chatComponent.addItem(itemMessage, myself.isTop());
        } else if (myself.getPhoto() != null) {
            ItemImage itemImage = buildPhotoMessage(myself.getPhoto(), type);
            changeLabelTimeStyle(lbTime);
            changeSentIconColor(lbSent.getIcon());
            itemImage.addTimePanel(lbTime);
            itemImage.addTimePanel(lbSent);
            chatComponent.addItem(itemImage, myself.isTop());
        } else if (myself.getFile() != null) {
            ItemMessage itemMessage = buildFileMessage(myself.getFile(), type);
            itemMessage.addTimePanel(lbTime);
            itemMessage.addTimePanel(lbSent);
            chatComponent.addItem(itemMessage, myself.isTop());
        }
        chatComponent.updateLevel();
        if (addNew) {
            chatPanel.addItem(chatComponent, myself.isTop(), myself.getConstraints());
            if (myself.isAutoRefresh()) {
                updateLayout(chatPanel.getPanel());
            }
        } else {
            if (myself.isAutoRefresh()) {
                updateLayout(chatComponent);
            }
        }
    }


    private void updateLayout(JPanel panel) {
        panel.repaint();
        panel.revalidate();
    }

    private ChatComponent getLastItemRecipient(int id, JPanel panel, boolean top) {
        int count = panel.getComponentCount();
        if (count == 0) {
            return null;
        }
        Component component = panel.getComponent(top ? 0 : count - 1);
        if (component instanceof ChatComponent) {
            ChatComponent chatComponent = (ChatComponent) component;
            if (chatComponent.getType() == 1 && chatComponent.getId() == id) {
                return chatComponent;
            }
        }
        return null;
    }

    private ChatComponent getLastItemMyself(JPanel panel, boolean top) {
        int count = panel.getComponentCount();
        if (count == 0) {
            return null;
        }
        Component component = panel.getComponent(top ? 0 : count - 1);
        if (component instanceof ChatComponent) {
            ChatComponent chatComponent = (ChatComponent) component;
            if (chatComponent.getType() == 2) {
                return chatComponent;
            }
        }
        return null;
    }

    protected ItemMessage buildTextMessage(String message, int type) {
        JTextPane textPane = new JTextPane();
        ((DefaultCaret) textPane.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        textPane.setEditorKit(new AutoWrapText(type == 1 ? 60 : 100));
        textPane.setEditable(false);
        textPane.setText(message);
        String backgroundKey = type == 1 ? "$Chat.item.background" : "$Chat.item.myselfBackground";
        String foregroundKey = type == 1 ? "@foreground" : "$Chat.item.myselfForeground";
        textPane.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:" + backgroundKey + ";" +
                "foreground:" + foregroundKey);
        ItemMessage itemMessage = new ItemMessage(textPane, type);
        return itemMessage;
    }

    protected JLabel buildTime(String time) {
        JLabel label = new JLabel(time);
        label.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:$Text.lowForeground;" +
                "font:-1");
        return label;
    }

    protected void changeLabelTimeStyle(JLabel label) {
        label.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:$Text.upperForeground;" +
                "font:-1");
    }

    protected JLabel buildSent(boolean act) {
        String icon = act ? "tick.svg" : "loading.svg";
        JLabel label = new JLabel(MethodUtil.createIcon("raven/messenger/icon/" + icon, 0.7f, Color.decode("#969696"), Color.decode("#646464")));
        return label;
    }

    protected void changeSentIconColor(Icon icon) {
        if (icon instanceof FlatSVGIcon) {
            FlatSVGIcon svgIcon = (FlatSVGIcon) icon;
            FlatSVGIcon.ColorFilter colorFilter = new FlatSVGIcon.ColorFilter();
            colorFilter.add(Color.decode("#969696"), Color.decode("#4E4E4E"), Color.decode("#C6C6C6"));
            svgIcon.setColorFilter(colorFilter);
        }
    }

    protected ItemMessage buildVoidMessage(ChatVoiceData data, int type) {
        ItemSound itemSound = new ItemSound(data, type);
        ItemMessage itemMessage = new ItemMessage(itemSound, type);
        return itemMessage;
    }

    protected ItemImage buildPhotoMessage(ChatPhotoData photo, int type) {
        ItemImage item = new ItemImage(photo, type);
        return item;
    }

    protected ItemMessage buildFileMessage(ChatFileData data, int type) {
        ItemFile itemFile = new ItemFile(data, type);
        ItemMessage itemMessage = new ItemMessage(itemFile, type);
        return itemMessage;
    }

    protected ChatProfile buildProfileImage(Icon icon) {
        ChatProfile profile = new ChatProfile();
        profile.setImage(icon);
        return profile;
    }
}
