package raven.messenger.option.group;

import com.formdev.flatlaf.FlatClientProperties;
import net.coobird.thumbnailator.Thumbnails;
import net.miginfocom.swing.MigLayout;
import raven.messenger.component.ScrollForTextArea;
import raven.messenger.component.StringIcon;
import raven.messenger.component.profile.ProfilePanel;
import raven.messenger.manager.FormsManager;
import raven.messenger.models.request.ModelCreateGroup;
import raven.messenger.util.MethodUtil;
import raven.messenger.util.StyleUtil;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DialogGroup extends JPanel {

    public DialogGroup() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap,fillx,insets 0", "center"));
        profilePanel = new ProfilePanel();
        profilePanel.setIcon(new StringIcon(MethodUtil.getProfileString("?"), 100, 100));
        add(profilePanel);
        createDetail();
    }

    public void open() {
        txtName.grabFocus();
    }

    private void createDetail() {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 3 30 3 30", "fill"));
        txtName = new JTextField();
        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                nameChanged();
            }
        });
        txtDescription = new JTextArea();
        txtDescription.setRows(6);
        txtDescription.setColumns(6);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        StyleUtil.applyStyleTextField(txtName);

        txtName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Group name");
        JScrollPane scroll = new ScrollForTextArea(txtDescription);

        panel.add(new JLabel("Name"), "gapy 8");
        panel.add(txtName);
        panel.add(new JLabel("Description"), "gapy 8");
        panel.add(scroll);
        add(panel, "grow 1");
        nameChanged();
    }

    private void nameChanged() {
        if (profilePanel.getSelectedImage() == null) {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                name = "?";
            }
            profilePanel.setIconProfileString(name);
        }
    }

    public boolean validateInput() {
        return FormsManager.getInstance().validateEmpty(txtName);
    }

    public ModelCreateGroup getData() throws IOException {
        String name = txtName.getText().trim();
        String description = txtDescription.getText().trim();
        return new ModelCreateGroup(name, description, getFileImage());
    }

    private File getFileImage() throws IOException {
        if (profilePanel.getSelectedImage() != null) {
            Path tempPath = Files.createTempFile("temp_", "_profile.jpg");
            File output = tempPath.toFile();
            output.deleteOnExit();
            Thumbnails.of(profilePanel.getSelectedImage())
                    .scale(1f)
                    .toFile(output);
            return output;
        } else {
            return null;
        }
    }

    private ProfilePanel profilePanel;
    private JTextField txtName;
    private JTextArea txtDescription;
}
