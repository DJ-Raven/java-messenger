package raven.messenger.component.right;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.component.chat.AutoWrapText;
import raven.messenger.models.response.ModelUserInfo;

import javax.swing.*;

public class PanelUser extends JPanel {

    public ModelUserInfo getData() {
        return data;
    }

    private ModelUserInfo data;

    public PanelUser() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap,fillx,insets 15 0 15 5,hidemode 3", "[fill]"));
        headerProfile = new HeaderProfile();

        add(headerProfile);
        createDescription();
    }

    private void createDescription() {
        panelDescription = new JPanel(new MigLayout("wrap,fillx,insets 0", "[fill]"));
        createSeparator(panelDescription);
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setEditorKit(new AutoWrapText());

        textPane.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:$Text.upperForeground;");
        panelDescription.add(textPane);
        add(panelDescription);
    }

    public void setUser(ModelUserInfo data) {
        this.data = data;
        headerProfile.setData(data.getProfile(), data.getName().getFullName(), "user");
        textPane.setText(data.getBio());
        panelDescription.setVisible(!data.getBio().isEmpty());
    }

    private void createSeparator(JPanel panel) {
        JPanel separator = new JPanel();
        separator.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%);");
        panel.add(separator, "height 7!");
    }

    private HeaderProfile headerProfile;
    private JPanel panelDescription;
    private JTextPane textPane;
}
