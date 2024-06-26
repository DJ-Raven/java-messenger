package raven.messenger.component.chat;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.component.layout.ChatViewportLayout;
import raven.messenger.plugin.swing.scroll.ScrollRefresh;
import raven.messenger.plugin.swing.scroll.ScrollRefreshModel;
import raven.messenger.util.ScrollAnimation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;

public class ChatPanel extends JPanel {

    private final ChatActionListener event;
    private EmptyChatData emptyChatData;
    private ScrollAnimation scrollAnimation;
    private ScrollRefreshModel scrollRefreshModel;

    public ChatPanel(ChatActionListener event, ScrollRefreshModel scrollRefreshModel) {
        this.event = event;
        this.scrollRefreshModel = scrollRefreshModel;
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap,fill", "[fill,400::]", "[fill][shrink 0,grow 0]"));
        putClientProperty(FlatClientProperties.STYLE, "" +
                "background:$Chat.background");
        scrollAnimation = new ScrollAnimation();
        panel = new JPanel(new MigLayout("insets 0,wrap,fillx"));
        panelBottom = new JPanel(new BorderLayout());
        createEmptyDataLabel();
        messageInput = new MessageInput(event);
        joinGroupButton = new JoinGroupButton(e -> {
            event.onJoinGroup();
        });

        scroll = new ScrollRefresh(scrollRefreshModel, panel);
        scroll.getViewport().setLayout(new ChatViewportLayout());
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:3,0,3,0;" +
                "background:$Chat.background");
        panelBottom.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:$Chat.background");
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "width:5;" +
                "background:$Chat.background;");
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatComponentBuilder = new DefaultChatComponentBuilder();
        chatModel = new ChatModel(this);
        add(scroll);
        add(panelBottom);
        initScrollEvent();
    }

    protected void add(Recipient recipient) {
        removeEmptyDataLabel();
        chatComponentBuilder.buildRecipient(this, recipient);
    }

    protected void add(Myself myself) {
        removeEmptyDataLabel();
        chatComponentBuilder.buildMyself(this, myself);
    }

    protected void createEmptyDataLabel() {
        removeEmptyDataLabel();
        emptyChatData = new EmptyChatData();
        panel.add(emptyChatData, "pos 0.5al 0.5al");
    }

    private void removeEmptyDataLabel() {
        if (emptyChatData != null) {
            panel.remove(emptyChatData);
            emptyChatData = null;
        }
    }

    private void scrollUpdate() {
        synchronized (panel.getTreeLock()) {
            int count = panel.getComponentCount();
            Rectangle view = scroll.getViewport().getViewRect();
            boolean act = false;
            for (int i = 0; i < count; i++) {
                Component com = panel.getComponent(i);
                if (com instanceof ChatComponent) {
                    if (view.intersects(com.getBounds())) {
                        ChatComponent chatComponent = (ChatComponent) com;
                        chatComponent.checkProfileImageLocation();
                        act = true;
                    } else if (act) {
                        break;
                    }
                }
            }
        }
    }

    private void initScrollEvent() {
        JScrollBar scrollbar = scroll.getVerticalScrollBar();
        scrollbar.addAdjustmentListener((AdjustmentEvent e) -> {
            scrollUpdate();
        });
    }

    public ChatComponentBuilder getChatComponentBuilder() {
        return chatComponentBuilder;
    }

    public void setChatComponentBuilder(ChatComponentBuilder chatComponentBuilder) {
        this.chatComponentBuilder = chatComponentBuilder;
    }

    public void addItem(Component component, boolean top, String constraints) {
        if (top) {
            panel.add(component, constraints, 0);
        } else {
            panel.add(component, constraints);
        }
    }

    public ChatModel getChatModel() {
        return chatModel;
    }

    public void setChatModel(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public JPanel getPanel() {
        return panel;
    }

    public void refreshLayout() {
        panel.repaint();
        panel.revalidate();
    }

    public void scrollToBottom() {
        SwingUtilities.invokeLater(() -> scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum()));
    }

    public void scrollToBottomWithAnimation() {
        SwingUtilities.invokeLater(() -> {
            scrollAnimation.scrollToMax(scroll.getVerticalScrollBar());
        });
    }

    public void useJoinButton() {
        panelBottom.removeAll();
        panelBottom.add(joinGroupButton);
        panelBottom.repaint();
        panelBottom.revalidate();
    }

    public void userMessageInput() {
        panelBottom.removeAll();
        panelBottom.add(messageInput);
        panelBottom.repaint();
        panelBottom.revalidate();
    }

    private ChatComponentBuilder chatComponentBuilder;
    private ChatModel chatModel;
    private ScrollRefresh scroll;
    private JPanel panel;
    private JPanel panelBottom;
    private MessageInput messageInput;
    private JoinGroupButton joinGroupButton;
}
