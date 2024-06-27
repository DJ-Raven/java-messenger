package raven.messenger.connection;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class FormUpdate extends JPanel {

    public FormUpdate() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("al center center"));
        JLabel labelMessage = new JLabel("Client Version Unsupported `Upgrade Required`");
        add(labelMessage);
    }
}
