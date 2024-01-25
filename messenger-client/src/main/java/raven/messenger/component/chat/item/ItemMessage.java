package raven.messenger.component.chat.item;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ItemMessage extends ChatItem {

    private JLayeredPane layeredPane;
    private JPanel timePanel;

    public ItemMessage(Component component, int type) {
        super(5,type);
        init();
        layeredPane.add(component);
    }

    public void addTimePanel(Component component) {
        if (timePanel == null) {
            timePanel = new JPanel(new MigLayout("insets 0"));
            timePanel.setOpaque(false);
            String lc = type == 1 ? "pos 100%-pref-5 100%-pref" : "pos 100%-pref 100%-pref";
            layeredPane.setLayer(timePanel, JLayeredPane.POPUP_LAYER);
            layeredPane.add(timePanel, lc, 0);
        }
        timePanel.add(component);
    }

    private void init() {
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(new MigLayout("insets 0", "[fill,150::]"));
        setLayout(new MigLayout("insets 0,fillx", "fill"));
        add(layeredPane);
    }
}
