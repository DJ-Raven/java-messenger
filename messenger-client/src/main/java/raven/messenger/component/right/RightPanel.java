package raven.messenger.component.right;

import net.miginfocom.swing.MigLayout;
import raven.messenger.models.response.ModelGroup;
import raven.messenger.models.response.ModelUserInfo;

import javax.swing.*;

public class RightPanel extends JPanel {

    public RightPanel() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill,insets 15 0 3 0", "[fill,270::]", "[fill]"));
    }

    public void setGroup(ModelGroup group) {
        removeAll();
        PanelGroup panelGroup = new PanelGroup();
        panelGroup.setGroup(group);
        add(panelGroup);
        repaint();
        revalidate();
    }

    public void setUser(ModelUserInfo user) {
        removeAll();
        if (user != null) {
            PanelUser panelUser = new PanelUser();
            panelUser.setUser(user);
            add(panelUser);
        }
        repaint();
        revalidate();
    }
}
