package raven.messenger.util;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
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
        } catch (Exception e) {
            LoggingFacade.INSTANCE.logSevere(null, e);
            FlatIntelliJLaf.setup();
        }
        initZoom();
        UIManager.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("lookAndFeel")) {
                state.put(KEY_LAF, UIManager.getLookAndFeel().getClass().getName());
            }
        });
    }

    private static void initZoom() {
    }
}
