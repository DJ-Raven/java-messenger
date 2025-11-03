package raven.messenger.util;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class StyleUtil {

    public static final String INPUT_SIMPLE = "simple";
    public static final String INPUT_MEDIUM = "medium";

    public static final String BUTTON_SIMPLE = "simple";
    public static final String BUTTON_DEFAULT = "default";

    public static final String ICON_SIMPLE = "icon.simple";
    public static final String ICON_MEDIUM = "icon.medium";

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
        text.putClientProperty(FlatClientProperties.STYLE_CLASS, INPUT_SIMPLE);
        text.putClientProperty(FlatClientProperties.STYLE, "showClearButton:true;");
    }

    public static void applyStyleTextField(JTextComponent text) {
        text.putClientProperty(FlatClientProperties.STYLE_CLASS, INPUT_SIMPLE);
    }

    public static void applyStyleTextFieldMedium(JTextComponent text) {
        text.putClientProperty(FlatClientProperties.STYLE_CLASS, INPUT_MEDIUM);
    }

    public static void applyStyleTextFieldMedium(JPasswordField text) {
        text.putClientProperty(FlatClientProperties.STYLE_CLASS, INPUT_MEDIUM);
        text.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true;");
    }

    public static boolean isCoreThemes(Class<? extends LookAndFeel> lafClass) {
        return lafClass == FlatLightLaf.class ||
                lafClass == FlatDarkLaf.class ||
                lafClass == FlatIntelliJLaf.class ||
                lafClass == FlatDarculaLaf.class ||
                lafClass == FlatMacLightLaf.class ||
                lafClass == FlatMacDarkLaf.class;
    }
}
