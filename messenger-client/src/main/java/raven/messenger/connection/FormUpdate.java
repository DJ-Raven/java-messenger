package raven.messenger.connection;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class FormUpdate extends JPanel {

    public FormUpdate() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap,al center center", "[center]"));
        JLabel labelMessage = new JLabel("Client Version Unsupported Upgrade Required");
        JLabel button = new JLabel("<html><a href=\"#\">download page</a></html>");
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/DJ-Raven/java-messenger"));
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        });
        add(labelMessage);
        add(button);
    }
}
