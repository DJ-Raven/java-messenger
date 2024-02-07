package raven.messenger.component.right;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.component.chat.AutoWrapText;
import raven.messenger.models.response.ModelGroup;
import raven.messenger.util.MethodUtil;
import raven.messenger.util.NetworkDataUtil;

import javax.swing.*;
import java.awt.*;

public class PanelGroup extends JPanel {

    public ModelGroup getData() {
        return data;
    }

    private ModelGroup data;

    public PanelGroup() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap,fillx,insets 15 0 15 5", "[fill]"));
        headerProfile = new HeaderProfile();
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setEditorKit(new AutoWrapText());
        add(headerProfile);
        add(createSeparator(), "height 4");
        add(textPane);
    }

    public void setGroup(ModelGroup data) {
        headerProfile.setData(data.getProfile(), data.getName(), "Group");
        textPane.setText(data.getDescription());
    }

    private Component createSeparator() {
        JPanel separator = new JPanel();
        separator.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");
        return separator;
    }

    private HeaderProfile headerProfile;
    private JTextPane textPane;
}
