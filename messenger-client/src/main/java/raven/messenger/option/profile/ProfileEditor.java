package raven.messenger.option.profile;

import net.miginfocom.swing.MigLayout;
import raven.messenger.component.profile.ProfileImageEditor;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ProfileEditor extends JPanel {

    private final File file;

    public ProfileEditor(File file) {
        this.file = file;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill", "[center]", ""));

        profileImageEditor = new ProfileImageEditor();
        profileImageEditor.setProfile(file.getAbsolutePath());

        add(profileImageEditor);
    }

    public BufferedImage getEditProfile() throws IOException {
        return profileImageEditor.getEditProfile();
    }

    private ProfileImageEditor profileImageEditor;
}
