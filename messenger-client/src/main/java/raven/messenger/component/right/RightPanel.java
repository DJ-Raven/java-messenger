package raven.messenger.component.right;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class RightPanel extends JPanel {

    public RightPanel() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap,fill", "[fill,250::]"));
    }
}
