package raven.messenger.component.chat;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionListener;

public class JoinGroupButton extends JPanel {

    private final ActionListener event;

    public JoinGroupButton(ActionListener event) {
        this.event = event;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill", "fill", "fill"));
        JButton button = new JButton("JOIN GROUP");
        button.addActionListener(event);

        putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        button.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;");

        add(button);
    }
}
