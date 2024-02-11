package raven.messenger.component.right;

import net.miginfocom.swing.MigLayout;
import raven.messenger.models.response.ModelGroup;
import raven.messenger.models.response.ModelUserInfo;

import javax.swing.*;
import java.awt.*;

public class RightPanel extends JPanel {

    public RightPanel() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill,insets 0", "[fill,250::]", "[fill]"));
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
        PanelUser panelUser = new PanelUser();
        panelUser.setUser(user);
        add(panelUser);
        repaint();
        revalidate();
    }
}
