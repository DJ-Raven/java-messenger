package raven.messenger.component.profile;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.component.StringIcon;
import raven.messenger.manager.DialogManager;
import raven.messenger.manager.ErrorManager;
import raven.messenger.option.profile.ProfileEditor;
import raven.messenger.util.MethodUtil;
import raven.popup.GlassPanePopup;
import raven.popup.component.SimplePopupBorder;
import raven.popup.component.SimplePopupBorderOption;
import raven.swing.AvatarIcon;

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
        labelProfile.putClientProperty(FlatClientProperties.STYLE, "" + "font:bold +10");
        add(createEditProfile(), "pos 1al 1al");
        add(labelProfile);
    }


    private JButton createEditProfile() {
        JButton button = new JButton(MethodUtil.createIcon("raven/messenger/icon/edit.svg", 0.75f));
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
            String actions[] = {"Cancel", "Save"};
            SimplePopupBorder popupBorder = new SimplePopupBorder(profileEditor, "Edit profile", new SimplePopupBorderOption().setWidth(300), actions, (popupController, i) -> {
                popupController.closePopup();
                if (i == 1) {
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
            GlassPanePopup.showPopup(popupBorder);
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
