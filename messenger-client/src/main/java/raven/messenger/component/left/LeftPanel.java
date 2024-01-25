package raven.messenger.component.left;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.api.exception.ResponseException;
import raven.messenger.drawer.MenuDrawer;
import raven.messenger.models.response.ModelLastMessage;
import raven.messenger.models.response.ModelMessage;
import raven.messenger.models.response.ModelUser;
import raven.messenger.plugin.swing.scroll.ScrollRefresh;
import raven.messenger.plugin.swing.scroll.ScrollRefreshModel;
import raven.messenger.service.ServiceUser;
import raven.messenger.util.Debounce;
import raven.messenger.util.MethodUtil;

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
        setLayout(new MigLayout("wrap,fill,insets 0 0 3 0", "[fill,270::]", "[grow 0]0[fill]"));
        panel = new JPanel(new MigLayout("wrap,fillx,gapy 3", "[fill]"));
        scroll = new ScrollRefresh(createScrollRefreshModel(), panel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(10);

        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "width:4");
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
        header = new JPanel(new MigLayout("fill", "[grow 0][fill]"));
        JButton button = new JButton(MethodUtil.createIcon("raven/messenger/icon/menu.svg", 1f));
        JTextField text = new JTextField();
        button.addActionListener(e -> {
            MenuDrawer.getInstance().showDrawer();
        });
        button.putClientProperty(FlatClientProperties.STYLE_CLASS, "myButton");
        button.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:999;" +
                "background:null");
        text.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search ...");
        text.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:999;" +
                "margin:5,10,5,10;" +
                "borderWidth:0;" +
                "background:darken($Panel.background,2%);");

        Debounce.add(text, (ke, search) -> {
            search = search.trim();
            if (search.isEmpty()) {
                textSearch = null;
            } else {
                textSearch = search;
            }
            initData();
        }, 300);
        header.add(button);
        header.add(text);
        add(header);
    }

    public synchronized void changeUserStatus(int userId, boolean status) {
        int count = panel.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component component = panel.getComponent(i);
            if (component instanceof Item) {
                Item item = (Item) component;
                if (item.getUser().getUserId() == userId) {
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
                if (item.getUser().getUserId() == message.getFromUser()) {
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
            try {
                ModelUser user = serviceUser.findById(message.getFromUser());
                user.setLastMessage(new ModelLastMessage(message));
                Item item = new Item(user);
                item.addActionListener(e -> event.onUserSelected(user));
                panel.add(item, 0);
                panel.repaint();
                panel.revalidate();
            } catch (ResponseException ex) {
                ex.printStackTrace();
            }
        }
    }

    public synchronized void userMessage(int userId, ModelLastMessage lastMessage) {
        int count = panel.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component component = panel.getComponent(i);
            if (component instanceof Item) {
                Item item = (Item) component;
                if (item.getUser().getUserId() == userId) {
                    item.setLastMessage(lastMessage);
                    panel.setComponentZOrder(component, 0);
                    panel.revalidate();
                    break;
                }
            }
        }
    }

    public void selectedUser(int userId) {
        int count = panel.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component component = panel.getComponent(i);
            if (component instanceof Item) {
                Item item = (Item) component;
                item.setSelected(item.getUser().getUserId() == userId);
            }
        }
    }

    public boolean loadData() {
        try {
            List<ModelUser> response = serviceUser.findAll(scroll.getScrollRefreshModel().getPage(), textSearch);
            for (ModelUser d : response) {
                if (isNotExist(d.getUserId())) {
                    Item item = new Item(d);
                    item.addActionListener(e -> event.onUserSelected(d));
                    panel.add(item);
                }
            }
            return !response.isEmpty();
        } catch (ResponseException e) {
            e.printStackTrace();
            return false;
        } finally {
            panel.repaint();
            panel.revalidate();
        }
    }

    private boolean isNotExist(int userId) {
        int count = panel.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component component = panel.getComponent(i);
            if (component instanceof Item) {
                Item item = (Item) component;
                if (item.getUser().getUserId() == userId) {
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
