package raven.messenger.component;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ScrollForTextArea extends JScrollPane {

    private final JTextComponent text;

    public ScrollForTextArea(JTextComponent text) {
        super(text);
        this.text = text;
        init();
    }

    private void init() {
        text.putClientProperty(FlatClientProperties.STYLE, "" +
                "margin:5,5,5,5;" +
                "background:$Item.component.background;");
        putClientProperty(FlatClientProperties.STYLE, "" +
                "borderWidth:0;" +
                "focusWidth:0;");

        getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "trackInsets:0,0,0,3;" +
                "thumbInsets:0,0,0,3;" +
                "width:7;");
        getVerticalScrollBar().setOpaque(false);

        // create text focus listener because scrollPane background paint color as the textarea background
        text.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                text.putClientProperty(FlatClientProperties.STYLE, "" +
                        "margin:5,5,5,5;" +
                        "background:$Item.component.focusedBackground;");
            }

            @Override
            public void focusLost(FocusEvent e) {
                text.putClientProperty(FlatClientProperties.STYLE, "" +
                        "margin:5,5,5,5;" +
                        "background:$Item.component.background;");
            }
        });
    }
}
