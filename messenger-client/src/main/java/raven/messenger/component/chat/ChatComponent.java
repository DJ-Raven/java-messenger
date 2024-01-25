package raven.messenger.component.chat;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.component.chat.item.ChatItem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChatComponent extends JPanel {

    private int type;

    private int id;
    private ChatProfile chatProfile;

    public ChatComponent(int type, int id) {
        this.type = type;
        this.id = id;
        init();
    }

    private void init() {
        putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        if (type == 1) {
            setLayout(new MigLayout("wrap,insets 0,gapy 3"));
        } else if (type == 2) {
            setLayout(new MigLayout("wrap,insets 0 0 0 5,gapy 3,al trailing", "trailing"));
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void addItem(Component component, boolean top) {
        if (top) {
            add(component, 0);
        } else {
            add(component);
        }
    }

    protected void setChatProfile(ChatProfile chatProfile) {
        this.chatProfile = chatProfile;
        chatProfile.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        add(chatProfile, "dock west,width 35!,height 35::");
    }

    public void updateLevel() {
        synchronized (getTreeLock()) {
            List<ChatItem> list = new ArrayList<>();
            int count = getComponentCount();
            for (int i = 0; i < count; i++) {
                Component com = getComponent(i);
                if (com instanceof ChatItem) {
                    ChatItem item = (ChatItem) com;
                    list.add(item);
                }
            }
            if (list.size() == 1) {
                list.get(0).setLevel(0);
            } else if (list.size() == 2) {
                list.get(0).setLevel(1);
                list.get(1).setLevel(3);
            } else {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setLevel(i == 0 ? 1 : i == list.size() - 1 ? 3 : 2);
                }
            }
        }
    }

    protected void checkProfileImageLocation() {
        if (chatProfile != null) {
            Rectangle rec = chatProfile.getVisibleRect();
            chatProfile.setImageLocation(rec.y + rec.height);
        }
    }
}
