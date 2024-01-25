package raven.messenger.util;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ComponentUtil {

    public static JPanel createInfoText(String... text) {
        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 10 25 10 25,gap 0", "fill"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "[dark]background:lighten($Panel.background,2%);" +
                "[light]background:darken($Panel.background,4%)");
        for (String st : text) {
            JLabel lb = new JLabel(st);
            lb.putClientProperty(FlatClientProperties.STYLE, "" +
                    "foreground:$Text.lowForeground");
            panel.add(lb);
        }
        return panel;
    }
}
