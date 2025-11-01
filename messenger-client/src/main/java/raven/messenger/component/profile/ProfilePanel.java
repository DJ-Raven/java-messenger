package raven.messenger.component.profile;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.extras.AvatarIcon;
import raven.messenger.component.ModalBorderCustom;
import raven.messenger.component.StringIcon;
import raven.messenger.manager.DialogManager;
import raven.messenger.manager.ErrorManager;
import raven.messenger.manager.FormsManager;
import raven.messenger.option.profile.ProfileEditor;
import raven.messenger.util.MethodUtil;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import raven.modal.option.Option;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ProfilePanel extends JPanel {

    public void setEventProfileSelected(EventProfileSelected eventProfileSelected) {
        this.eventProfileSelected = eventProfileSelected;
    }

    public BufferedImage getSelectedImage() {
        return selectedImage;
    }

    public void setIcon(Icon icon) {
        labelProfile.setIcon(icon);
    }

    public Icon getIcon() {
        return labelProfile.getIcon();
    }

    private EventProfileSelected eventProfileSelected;
    private BufferedImage selectedImage;

    public ProfilePanel() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap,fillx"));
        labelProfile = new JLabel();
        labelProfile.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10;" +
                "foreground:$Profile.foreground;");
        add(createEditProfile(), "pos 1al 1al");
        add(labelProfile);
    }

    private JButton createEditProfile() {
        JButton button = new JButton(MethodUtil.createIcon("raven/messenger/icon/edit.svg", 0.35f));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            editProfile();
        });
        button.putClientProperty(FlatClientProperties.STYLE_CLASS, "emptyButton");
        button.putClientProperty(FlatClientProperties.STYLE, "" + "arc:999;");
        return button;
    }

    private void editProfile() {
        File file = DialogManager.getInstance().showOpenDialog(DialogManager.ShowOpenType.PHOTO);
        if (file != null) {
            ProfileEditor profileEditor = new ProfileEditor(file);
            SimpleModalBorder.Option[] options = new SimpleModalBorder.Option[]{
                    new SimpleModalBorder.Option("Cancel", SimpleModalBorder.CANCEL_OPTION),
                    new SimpleModalBorder.Option("Save", SimpleModalBorder.OK_OPTION)
            };
            ModalBorderCustom modalBorder = new ModalBorderCustom(profileEditor, "Edit profile", options, (callback, action) -> {
                if (action == SimpleModalBorder.OK_OPTION) {
                    Icon oldImage = labelProfile.getIcon();
                    try {
                        selectedImage = profileEditor.getEditProfile();
                        if (eventProfileSelected != null) {
                            eventProfileSelected.selected(selectedImage);
                        }
                        labelProfile.setIcon(new AvatarIcon(new ImageIcon(selectedImage), 100, 100, 999));
                    } catch (IOException e) {
                        ErrorManager.getInstance().showError(e);
                        selectedImage = null;
                        labelProfile.setIcon(oldImage);
                    }
                }
            });
            Option option = ModalDialog.createOption();
            option.getLayoutOption().setSize(300, -1);
            ModalDialog.showModal(FormsManager.getInstance().getMainFrame(), modalBorder, option);
        }
    }

    private String profileString = "";
    private Icon stringIcon;

    public void setIconProfileString(String profileString) {
        String st = MethodUtil.getProfileString(profileString);
        if (!this.profileString.equals(st)) {
            stringIcon = new StringIcon(st, UIManager.getColor("Component.accentColor"), 100, 100);
            this.profileString = st;
        }
        labelProfile.setIcon(stringIcon);
    }

    private JLabel labelProfile;
}
