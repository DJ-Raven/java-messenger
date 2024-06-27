package raven.messenger.connection;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;

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
        JPanel panelText = new JPanel(new MigLayout("wrap", "", "[]3[]10[]"));
        panelText.add(labelMessage);
        panelText.add(labelDescription);
        panelText.add(createReconnectButton());
        add(panelText);
    }

    private JButton createReconnectButton() {
        JButton button = new JButton("Try reconnect");
        button.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,5%);" +
                "[dark]background:lighten(@background,5%);" +
                "font:-1;" +
                "arc:999;" +
                "margin:4,10,4,10;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;");
        return button;
    }
}
