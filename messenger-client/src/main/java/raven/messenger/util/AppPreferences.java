package raven.messenger.util;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.FontUtils;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.UIScale;
import raven.messenger.manager.FormsManager;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class AppPreferences {

    public static final String PREFERENCES_ROOT_PATH = "java-messenger-client";
    public static final String KEY_LAF = "laf";
    public static final String KEY_ACCENT_COLOR = "accent";
    public static final String KEY_ZOOM_FACTOR = "zoomFactor";

    public static Color accentColor;
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
            String rgbAccentColor = state.get(KEY_ACCENT_COLOR, null);
            if (rgbAccentColor != null) {
                accentColor = new Color(Integer.parseInt(rgbAccentColor), true);
            }
            FlatLaf.setSystemColorGetter(name -> name.equals("accent") ? AppPreferences.accentColor : null);

            String lafClassName = state.get(KEY_LAF, FlatDarculaLaf.class.getName());
            UIManager.put("defaultFont", FontUtils.getCompositeFont(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
            UIManager.setLookAndFeel(lafClassName);
            updateIconColor();
        } catch (Exception e) {
            LoggingFacade.INSTANCE.logSevere(null, e);
            FlatDarculaLaf.setup();
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
        String zoomFactor = state.get(KEY_ZOOM_FACTOR, null);
        if (zoomFactor != null) {
            float f = Float.parseFloat(zoomFactor);
            if (f != 1) {
                UIScale.setZoomFactor(f);
            }
        }
        UIScale.addPropertyChangeListener(e -> {
            if (UIScale.PROP_USER_SCALE_FACTOR.equals(e.getPropertyName())) {
                Window window = FormsManager.getInstance().getMainFrame();
                if (window != null) {
                    zoomWindowBounds(window, (float) e.getOldValue(), (float) e.getNewValue());
                }
            }
        });
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

    public static void updateAccentColor(Color color) {
        if (color != null) {
            String rgb = color.getRGB() + "";
            state.put(KEY_ACCENT_COLOR, rgb);
        } else {
            state.remove(KEY_ACCENT_COLOR);
        }
    }

    public static void updateZoomFactor() {
        float zoom = UIScale.getZoomFactor();
        if (zoom == 1) {
            state.remove(KEY_ZOOM_FACTOR);
        } else {
            state.put(KEY_ZOOM_FACTOR, zoom + "");
        }
    }

    private static void zoomWindowBounds(Window window, float oldZoomFactor, float newZoomFactor) {
        if (window instanceof Frame && ((Frame) window).getExtendedState() != Frame.NORMAL)
            return;

        Rectangle oldBounds = window.getBounds();

        // zoom window bounds
        float factor = (1f / oldZoomFactor) * newZoomFactor;
        int newWidth = (int) (oldBounds.width * factor);
        int newHeight = (int) (oldBounds.height * factor);
        int newX = oldBounds.x - ((newWidth - oldBounds.width) / 2);
        int newY = oldBounds.y - ((newHeight - oldBounds.height) / 2);

        // get maximum window bounds (screen bounds minus screen insets)
        GraphicsConfiguration gc = window.getGraphicsConfiguration();
        Rectangle screenBounds = gc.getBounds();
        Insets screenInsets = FlatUIUtils.getScreenInsets(gc);
        Rectangle maxBounds = FlatUIUtils.subtractInsets(screenBounds, screenInsets);

        // limit new window width/height
        newWidth = Math.min(newWidth, maxBounds.width);
        newHeight = Math.min(newHeight, maxBounds.height);

        // move window into screen bounds
        newX = Math.max(Math.min(newX, maxBounds.width - newWidth), maxBounds.x);
        newY = Math.max(Math.min(newY, maxBounds.height - newHeight), maxBounds.y);

        // set new window bounds
        window.setBounds(newX, newY, newWidth, newHeight);
    }
}
