package raven.messenger.util;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class StyleUtil {

    public static void applyStyleItemButton(JButton button, int type) {
        String buttonBackgroundKey = type == 1 ? "$Chat.item.button.background" : "$Chat.item.button.myselfBackground";
        button.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:999;" +
                "margin:7,7,7,7;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "background:" + buttonBackgroundKey);
    }

    public static void applyStyleTextFieldWithClear(JTextComponent text) {
        text.putClientProperty(FlatClientProperties.STYLE, "" +
                "margin:5,10,5,10;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "showClearButton:true;" +
                "background:$Item.component.background;" +
                "focusedBackground:$Item.component.focusedBackground;");
    }

    public static void applyStyleTextField(JTextComponent text) {
        text.putClientProperty(FlatClientProperties.STYLE, "" +
                "margin:5,10,5,10;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "background:$Item.component.background;" +
                "focusedBackground:$Item.component.focusedBackground;");
    }
}
