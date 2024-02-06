package raven.messenger.component.right;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.models.response.ModelChatListItem;
import raven.messenger.util.NetworkDataUtil;

import javax.swing.*;

public class RightPanel extends JPanel {

    private ModelChatListItem data;

    public RightPanel() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap,fillx,insets 20 5 5 5", "[center,250::]"));
        labelProfile = new JLabel();
        labelName = new JLabel();
        labelBio = new JLabel();

        labelProfile.putClientProperty(FlatClientProperties.STYLE,"" +
                "font:+10 bold");
        labelName.putClientProperty(FlatClientProperties.STYLE,"" +
                "font:+5 bold");

        add(labelProfile,"width 110!,height 110!");
        add(labelName,"gapy 10");
        add(labelBio);
    }

    public void setData(ModelChatListItem data) {
        labelProfile.setIcon(NetworkDataUtil.getNetworkIcon(data.getProfile(), data.getProfileString(), 110, 110, 999));
        labelName.setText(data.getName());
    }

    private JLabel labelProfile;
    private JLabel labelName;
    private JLabel labelBio;
}
