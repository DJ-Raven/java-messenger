package raven.messenger.manager;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.ScaledEmptyBorder;
import com.formdev.flatlaf.util.SystemFileChooser;
import raven.messenger.component.EmptyModalBorderCustom;
import raven.messenger.component.ModalBorderCustom;
import raven.messenger.component.NetworkIcon;
import raven.messenger.component.chat.model.ChatPhotoData;
import raven.messenger.plugin.swing.scroll.ScrollOverlay;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.SimpleModalBorder;
import raven.modal.listener.ModalCallback;
import raven.modal.option.BorderOption;
import raven.modal.option.Option;

import javax.swing.*;
import java.awt.*;
import java.io.File;

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
        ModalDialog.getDefaultOption().setAnimationEnabled(false)
                .setOpacity(0.3f)
                .getBorderOption().setShadow(BorderOption.Shadow.MEDIUM);

        Toast.getDefaultOption().getLayoutOption().setMargin(20, 15, 7, 15);
        Toast.getDefaultOption().getStyle().setShowIcon(false);
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
        SystemFileChooser fc = new SystemFileChooser();
        fc.setStateStoreID("photo");
        if (type == ShowOpenType.PHOTO) {
            fc.addChoosableFileFilter(new SystemFileChooser.FileNameExtensionFilter("Photo", "png", "jpg", "jpeg", "gif", "bmp"));
        }
        int act = fc.showOpenDialog(frame);
        if (act == SystemFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }
        return null;
    }

    public File[] showOpenDialogMulti() {
        SystemFileChooser fc = new SystemFileChooser();
        fc.setStateStoreID("file");
        fc.setMultiSelectionEnabled(true);
        int act = fc.showOpenDialog(frame);
        if (act == SystemFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFiles();
        }
        return null;
    }

    public File showSaveDialog(String fileName) {
        SystemFileChooser fc = new SystemFileChooser();
        fc.setStateStoreID("file");
        if (fileName != null) {
            fc.setSelectedFile(new File(fileName));
        }
        int act = fc.showSaveDialog(frame);
        if (act == SystemFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }
        return null;
    }

    public enum ShowOpenType {
        PHOTO, FILE, ALL
    }
}
