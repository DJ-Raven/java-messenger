package raven.messenger.component.left;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.api.exception.ResponseException;
import raven.messenger.manager.ErrorManager;
import raven.messenger.models.response.ModelChatListItem;
import raven.messenger.models.response.ModelLastMessage;
import raven.messenger.models.response.ModelMessage;
import raven.messenger.plugin.swing.scroll.ScrollRefresh;
import raven.messenger.plugin.swing.scroll.ScrollRefreshModel;
import raven.messenger.service.ServiceUser;
import raven.messenger.socket.ChatType;
import raven.messenger.util.Debounce;
import raven.messenger.util.MethodUtil;
import raven.messenger.util.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LeftPanel extends JPanel {

    private final LeftActionListener event;
    private final ServiceUser serviceUser = new ServiceUser();
    private String textSearch;

    public LeftPanel(LeftActionListener event) {
        this.event = event;
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap,fill,insets 15 0 3 0", "[fill,270::]", "[grow 0]0[fill]"));
        panel = new JPanel(new MigLayout("wrap,fillx,gapy 3,insets n 3 n n", "[fill]"));
        scroll = new ScrollRefresh(createScrollRefreshModel(), panel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(10);

        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "width:4;");
        createHeader();
        add(scroll);
    }

    public void initData() {
        panel.removeAll();
        panel.repaint();
        panel.revalidate();
        scroll.getScrollRefreshModel().resetPage();
    }

    private ScrollRefreshModel createScrollRefreshModel() {
        ScrollRefreshModel model = new ScrollRefreshModel(1, SwingConstants.BOTTOM) {
            @Override
            public boolean onRefreshNext() {
                return loadData();
            }

            @Override
            public void onFinishRefresh() {
                repaint();
                revalidate();
            }

            @Override
            public void onFinishData() {
            }

            @Override
            public void onError(Exception e) {
            }
        };
        return model;
    }

    private void createHeader() {
        header = new JPanel(new MigLayout("fill", "[fill]"));
        JTextField text = new JTextField();
        text.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search ...");
        StyleUtil.applyStyleTextFieldWithClear(text);
        text.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, MethodUtil.createIcon("raven/messenger/icon/search.svg", 0.35f));
        Debounce.add(text, (ke, search) -> {
            search = search.trim();
            if (search.isEmpty()) {
                textSearch = null;
            } else {
                textSearch = search;
            }
            initData();
        });
        header.add(text);
        add(header);
    }

    public synchronized void changeUserStatus(int userId, boolean status) {
        int count = panel.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component component = panel.getComponent(i);
            if (component instanceof Item) {
                Item item = (Item) component;
                if (!item.getData().isGroup() && item.getData().getId() == userId) {
                    item.setActiveStatus(status);
                    break;
                }
            }
        }
    }

    public synchronized void userMessage(ModelMessage message) {
        boolean found = false;
        int count = panel.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component component = panel.getComponent(i);
            if (component instanceof Item) {
                Item item = (Item) component;
                if (item.getData().getId() == message.getFromId() && item.getData().getChatType() == message.getChatType()) {
                    found = true;
                    item.setLastMessage(new ModelLastMessage(message));
                    panel.setComponentZOrder(component, 0);
                    panel.revalidate();
                    break;
                }
            }
        }
        if (!found) {
            // Get user from server and add to top
            createNew(message.getChatType(), message.getFromId(), message);
        }
    }

    public void createNew(ChatType chatType, int id) {
        createNew(chatType, id, null);
    }

    public void createNew(ChatType chatType, int id, ModelMessage message) {
        try {
            ModelChatListItem user = serviceUser.findById(chatType, id);
            if (message != null) {
                user.setLastMessage(new ModelLastMessage(message));
            } else {
                if (chatType == ChatType.GROUP) {
                    user.setLastMessage(ModelLastMessage.createAsJoined());
                }
            }
            Item item = new Item(user);
            item.setSelected(true);
            item.addActionListener(e -> event.onUserSelected(user));
            removeItem(chatType, id);
            panel.add(item, 0);
            panel.repaint();
            panel.revalidate();
        } catch (ResponseException ex) {
            ErrorManager.getInstance().showError(ex);
        }
    }

    private void removeItem(ChatType type, int id) {
        for (int i = 0; i < panel.getComponentCount(); i++) {
            Item item = (Item) panel.getComponent(i);
            if (item.getData().getChatType() == type && item.getData().getId() == id) {
                panel.remove(item);
                break;
            }
        }
    }

    public synchronized void userMessage(ChatType chatType, int id, ModelLastMessage lastMessage) {
        int count = panel.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component component = panel.getComponent(i);
            if (component instanceof Item) {
                Item item = (Item) component;
                if (item.getData().getId() == id && item.getData().getChatType() == chatType) {
                    item.setLastMessage(lastMessage);
                    panel.setComponentZOrder(component, 0);
                    panel.revalidate();
                    break;
                }
            }
        }
    }

    public void selectedUser(ModelChatListItem data) {
        int count = panel.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component component = panel.getComponent(i);
            if (component instanceof Item) {
                Item item = (Item) component;
                item.setSelected(item.getData().getId() == data.getId() && item.getData().getChatType() == data.getChatType());
            }
        }
    }

    public boolean loadData() {
        try {
            List<ModelChatListItem> response = serviceUser.findAll(scroll.getScrollRefreshModel().getPage(), textSearch);
            for (ModelChatListItem d : response) {
                if (isNotExist(d)) {
                    Item item = new Item(d);
                    item.addActionListener(e -> event.onUserSelected(d));
                    ModelChatListItem select = event.getSelectedListItem();
                    if (select != null) {
                        if (select.isGroup() == d.isGroup() && select.getId() == d.getId()) {
                            item.setSelected(true);
                        }
                    }
                    panel.add(item);
                }
            }
            return !response.isEmpty();
        } catch (ResponseException e) {
            ErrorManager.getInstance().showError(e);
            return false;
        } finally {
            panel.repaint();
            panel.revalidate();
        }
    }

    private boolean isNotExist(ModelChatListItem data) {
        int count = panel.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component component = panel.getComponent(i);
            if (component instanceof Item) {
                Item item = (Item) component;
                if (item.getData().getId() == data.getId() && item.getData().getChatType() == data.getChatType()) {
                    return false;
                }
            }
        }
        return true;
    }

    private ScrollRefresh scroll;
    private JPanel header;
    private JPanel panel;
}
