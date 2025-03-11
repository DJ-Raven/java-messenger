package raven.messenger.manager;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.ScaledEmptyBorder;
import com.formdev.flatlaf.util.UIScale;
import raven.messenger.Application;
import raven.messenger.component.EmptyModalBorderCustom;
import raven.messenger.component.ModalBorderCustom;
import raven.messenger.component.NetworkIcon;
import raven.messenger.component.chat.model.ChatPhotoData;
import raven.messenger.plugin.swing.scroll.ScrollOverlay;
import raven.messenger.util.MethodUtil;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.SimpleModalBorder;
import raven.modal.listener.ModalCallback;
import raven.modal.option.Option;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.util.prefs.Preferences;

public class DialogManager {

    private static DialogManager instance;
    public JFrame frame;

    public static DialogManager getInstance() {
        if (instance == null) {
            instance = new DialogManager();
        }
        return instance;
    }

    private DialogManager() {
        ModalDialog.getDefaultOption().setAnimationEnabled(false);
        Toast.getDefaultOption().getLayoutOption().setMargin(20, 7, 7, 7);
    }

    public void init(JFrame frame) {
        this.frame = frame;
    }

    public void showDialog(Component component, String title, SimpleModalBorder.Option[] optionsType, ModalCallback callbackAction, String id) {
        ModalBorderCustom dialogBorder = new ModalBorderCustom(component, title, optionsType, callbackAction);
        Option option = ModalDialog.createOption();
        option.getLayoutOption().setSize(430, -1);
        ModalDialog.showModal(frame, dialogBorder, option, id);
    }

    public void showViewPhotoDialog(ChatPhotoData photo) {
        showViewPhotoDialog(photo.getPath());
    }

    public void showViewPhotoDialog(String path) {
        int w = -1;
        int h = -1;
        NetworkIcon.IconResource resource = new NetworkIcon.IconResource(path);
        if (resource.getImageHeight() > 500) {
            h = 500;
        } else if (resource.getImageWidth() > 1000) {
            w = 1000;
        }

        JLabel label = new JLabel(new NetworkIcon(resource, w, h));
        ScrollOverlay scrollPane = new ScrollOverlay(label);
        scrollPane.setBorder(new ScaledEmptyBorder(0, 0, 0, 0));
        JScrollBar verticalScrollbar = scrollPane.getVerticalScrollBar();
        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        verticalScrollbar.setOpaque(false);
        horizontalScrollBar.setOpaque(false);
        verticalScrollbar.putClientProperty(FlatClientProperties.STYLE, "" +
                "width:4;" +
                "trackArc:$ScrollBar.thumbArc;");
        horizontalScrollBar.putClientProperty(FlatClientProperties.STYLE, "" +
                "width:4;" +
                "trackArc:$ScrollBar.thumbArc;");
        verticalScrollbar.setUnitIncrement(10);
        horizontalScrollBar.setUnitIncrement(10);

        // option
        Option option = ModalDialog.createOption();
        option.getBorderOption().setRound(0);
        option.getLayoutOption().setSize(-1, -1);
        ModalDialog.showModal(frame, new EmptyModalBorderCustom(scrollPane), option);
    }

    public File showOpenDialog(ShowOpenType type) {
        String key = type.toString();
        JFileChooser chooser = new JFileChooser(getLastSelectedFile(key));
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String fileName = f.getName();
                if (type == ShowOpenType.PHOTO) {
                    return f.isDirectory() || MethodUtil.isImageFile(fileName);
                } else {
                    return true;
                }
            }

            @Override
            public String getDescription() {
                if (type == ShowOpenType.PHOTO) {
                    return "Photo";
                } else {
                    return "";
                }
            }
        });
        chooser.setPreferredSize(UIScale.scale(new Dimension(900, 500)));
        int act = chooser.showOpenDialog(frame);
        if (act == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            updateLastSelectedFile(key, file);
            return file;
        }
        return null;
    }

    public File[] showOpenDialogMulti() {
        final String key = "select";
        JFileChooser chooser = new JFileChooser(getLastSelectedFile(key));
        chooser.setPreferredSize(UIScale.scale(new Dimension(820, 480)));
        chooser.setMultiSelectionEnabled(true);
        int act = chooser.showOpenDialog(frame);
        if (act == JFileChooser.APPROVE_OPTION) {
            File files[] = chooser.getSelectedFiles();
            updateLastSelectedFile(key, files[0]);
            return files;
        }
        return null;
    }

    public File showSaveDialog(String fileName) {
        final String KEY = "save";
        JFileChooser chooser = new JFileChooser(getLastSelectedFile(KEY));
        if (fileName != null) {
            chooser.setSelectedFile(new File(fileName));
        }
        chooser.setPreferredSize(UIScale.scale(new Dimension(900, 500)));
        int act = chooser.showSaveDialog(frame);
        if (act == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            updateLastSelectedFile(KEY, file);
            return file;
        }
        return null;
    }

    public File getLastSelectedFile(String key) {
        Preferences prefs = Preferences.userNodeForPackage(Application.class);
        String lastUsedDirectory = prefs.get("java_chooser_" + key, FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath());
        return new File(lastUsedDirectory);
    }

    public void updateLastSelectedFile(String key, File file) {
        Preferences prefs = Preferences.userNodeForPackage(Application.class);
        prefs.put("java_chooser_" + key, file.getParent());
    }

    public enum ShowOpenType {
        PHOTO, FILE, ALL
    }
}
