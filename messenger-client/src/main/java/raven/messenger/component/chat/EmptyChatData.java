package raven.messenger.component.chat;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.util.MethodUtil;

import javax.swing.*;
import java.awt.*;

public class EmptyChatData extends JPanel {

    public EmptyChatData() {
        init();
    }

    private void init() {
        putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;");
        setLayout(new MigLayout("fill,wrap", "center"));
        add(new JLabel(MethodUtil.createIcon("raven/messenger/icon/empty_message.svg", 1.5f, Color.decode("#C8C8C8"), Color.decode("#414141"))));
        JLabel label = new JLabel("Empty Chart");
        label.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:$Text.lowForeground;" +
                "font:+1;");
        add(label);
    }
}
