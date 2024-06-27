package raven.messenger.connection;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class FormError extends JPanel {

    public FormError() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("al center center"));
        JLabel labelMessage = new JLabel("Connection error");
        add(labelMessage);
    }
}
