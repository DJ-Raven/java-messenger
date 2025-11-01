package raven.messenger.util;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.FontUtils;
import com.formdev.flatlaf.util.LoggingFacade;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class AppPreferences {

    public static final String PREFERENCES_ROOT_PATH = "java-messenger-client";
    public static final String KEY_LAF = "laf";

    private static Preferences state;

    public static Preferences getState() {
        return state;
    }

    public static void init() {
        state = Preferences.userRoot().node(PREFERENCES_ROOT_PATH);
    }

    public static void setupLaf() {
        // set look and feel
        try {
            String lafClassName = state.get(KEY_LAF, FlatLightLaf.class.getName());
            UIManager.put("defaultFont", FontUtils.getCompositeFont(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
            UIManager.setLookAndFeel(lafClassName);
            updateIconColor();
        } catch (Exception e) {
            LoggingFacade.INSTANCE.logSevere(null, e);
            FlatIntelliJLaf.setup();
        }
        initZoom();
        UIManager.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("lookAndFeel")) {
                updateIconColor();
                state.put(KEY_LAF, UIManager.getLookAndFeel().getClass().getName());
            }
        });
    }

    private static void initZoom() {
    }

    private static void updateIconColor() {
        final Color color;
        if (FlatLaf.isLafDark()) {
            color = ColorFunctions.shade(UIManager.getColor("Label.foreground"), 0.3f);
        } else {
            color = ColorFunctions.tint(UIManager.getColor("Label.foreground"), 0.3f);
        }
        FlatSVGIcon.ColorFilter.getInstance().setMapper(c -> {
            if (c.getRed() == 150 && c.getGreen() == 150 && c.getBlue() == 150) {
                return color;
            }
            return c;
        });
    }
}
