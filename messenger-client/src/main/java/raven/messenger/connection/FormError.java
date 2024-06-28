package raven.messenger.connection;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import raven.messenger.manager.FormsManager;
import raven.modal.Toast;

import javax.swing.*;

public class FormError extends JPanel {

    public FormError() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("al center center"));
        JLabel labelMessage = new JLabel("Server Error");
        JLabel labelDescription = new JLabel("Sorry, something went technically wrong");
        labelMessage.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10");
        FlatSVGIcon icon = new FlatSVGIcon("raven/messenger/icon/server_error.svg", 2f);
        add(new JLabel(icon));
        panel = new JPanel(new MigLayout("wrap", "", "[]3[]10[]"));
        panel.add(labelMessage);
        panel.add(labelDescription);
        add(panel);
    }

    private JButton getReconnectButton() {
        if (reconnectButton == null) {
            reconnectButton = new JButton("Try reconnect");
            reconnectButton.addActionListener(e -> reconnect());
            reconnectButton.putClientProperty(FlatClientProperties.STYLE, "" +
                    "[light]background:darken(@background,5%);" +
                    "[dark]background:lighten(@background,5%);" +
                    "font:-1;" +
                    "arc:999;" +
                    "margin:4,10,4,10;" +
                    "borderWidth:0;" +
                    "focusWidth:0;" +
                    "innerFocusWidth:0;");
        }
        return reconnectButton;
    }

    private void reconnect() {
        ConnectionManager.Type type = ConnectionManager.getInstance().checkConnection();
        if (type == ConnectionManager.Type.SUCCESS) {
            ConnectionManager.getInstance().checkOnReconnection();
        } else if (type == ConnectionManager.Type.CLIENT_REQUIRED_UPDATE) {
            FormsManager.getInstance().showForm(new FormUpdate());
        } else {
            Toast.show(FormsManager.getInstance().getMainFrame(), Toast.Type.ERROR, "Connection error");
        }
    }

    public void showReconnectButton(boolean show) {
        if (reconnectButton != null) {
            panel.remove(reconnectButton);
        }
        if (show) {
            panel.add(getReconnectButton());
        }
    }

    private JPanel panel;
    private JButton reconnectButton;
}
