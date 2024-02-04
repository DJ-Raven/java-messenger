package raven.messenger.option.group;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.component.profile.ProfilePanel;

import javax.swing.*;

public class DialogGroup extends JPanel {

    public DialogGroup() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap,fillx,insets 0", "center"));
        profilePanel = new ProfilePanel();
        profilePanel.setEventProfileSelected(image -> {
            System.out.println("Select Image " + profilePanel.getSelectedImage());
        });
        add(profilePanel);
        createDetail();
    }

    private void createDetail() {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 3 30 3 30", "fill"));
        txtName = new JTextField();
        txtDescription = new JTextArea();
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtName.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        txtDescription.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");

        txtName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Group name");
        JScrollPane scroll = new JScrollPane(txtDescription);
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "trackInsets:0,0,0,3;" +
                "thumbInsets:0,0,0,3;" +
                "width:8");

        panel.add(new JLabel("Name"), "gapy 8");
        panel.add(txtName);
        panel.add(new JLabel("Description"), "gapy 8");
        panel.add(scroll, "height 80");
        add(panel, "grow 1");
    }


    private ProfilePanel profilePanel;
    private JTextField txtName;
    private JTextArea txtDescription;
}
