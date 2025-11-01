package raven.messenger.util;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.manager.DialogManager;
import raven.messenger.manager.ErrorManager;
import raven.messenger.store.StoreManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;

public class ComponentUtil {

    public static JPanel createInfoText(String... text) {
        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 10 30 10 30,gap 0", "fill"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "[dark]background:tint($Panel.background,5%);" +
                "[light]background:shade($Panel.background,5%);");
        for (String st : text) {
            JLabel lb = new JLabel(st);
            lb.putClientProperty(FlatClientProperties.STYLE, "" +
                    "foreground:$Text.middleForeground;");
            panel.add(lb);
        }
        return panel;
    }

    public static void addSeparatorTo(JPanel panel) {
        JPanel separator = new JPanel();
        separator.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%);");
        panel.add(separator, "height 7!");
    }

    public static JPopupMenu createOpenAndSavePopup(String name, String originalName) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuOpen = new JMenuItem("Open", MethodUtil.createIcon("raven/messenger/icon/view.svg", 0.4f));
        JMenuItem menuSave = new JMenuItem("Save", MethodUtil.createIcon("raven/messenger/icon/save.svg", 0.4f));
        menuOpen.addActionListener(e -> {
            File file = StoreManager.getInstance().getFile(name);
            if (file != null) {
                openFile(file);
            }
        });
        menuSave.addActionListener(e -> {
            File saveFile = DialogManager.getInstance().showSaveDialog(originalName);
            if (saveFile != null) {
                File file = StoreManager.getInstance().getFile(name);
                if (file != null) {
                    try {
                        Files.copy(file.toPath(), saveFile.toPath());
                    } catch (Exception ex) {
                        ErrorManager.getInstance().showError(ex);
                    }
                }
            }
        });
        popupMenu.add(menuOpen);
        popupMenu.add(menuSave);

        return popupMenu;
    }

    public static void openFile(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            ErrorManager.getInstance().showError(e);
        }
    }
}
