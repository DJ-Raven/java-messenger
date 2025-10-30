package raven.messenger.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import raven.extras.AvatarIcon;
import raven.messenger.api.ApiService;
import raven.messenger.login.Login;
import raven.messenger.manager.FormsManager;
import raven.messenger.models.response.ModelProfile;
import raven.messenger.option.OptionManager;
import raven.messenger.util.NetworkDataUtil;
import raven.modal.component.DropShadowBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Menu extends JPanel {

    private JLabel labelProfile;

    public Menu() {
        init();
    }

    private void init() {
        setBorder(new DropShadowBorder(new Insets(0, 0, 0, 10), 0));
        setLayout(new MigLayout("wrap", "[center,60]", "15[]push[][][]push[][]15"));

        labelProfile = new JLabel(createAvatar());
        add(labelProfile);

        add(createItem("account.svg", e -> OptionManager.getInstance().showProfile()));
        add(createItem("group.svg", e -> OptionManager.getInstance().newGroup()));
        add(createItem("storage.svg", e -> OptionManager.getInstance().showStorage()));

        add(createItem("settings.svg", e -> showSettings()));
        add(createItem("logout.svg", e -> logout()));
    }

    public void updateProfile(ModelProfile profile) {
        labelProfile.setIcon(NetworkDataUtil.getNetworkIcon(profile.getProfile(), profile.getName().getProfileString(), 50, 50, 999));
    }

    private Icon createAvatar() {
        AvatarIcon icon = new AvatarIcon(new FlatSVGIcon("raven/messenger/profile/avatar_male.svg", 100, 100), 50, 50, 3.5f);
        icon.setType(AvatarIcon.Type.MASK_SQUIRCLE);
        icon.setBorder(2, 2);
        changeAvatarIconBorderColor(icon);
        return icon;
    }

    private void changeAvatarIconBorderColor(AvatarIcon icon) {
        icon.setBorderColor(new AvatarIcon.BorderColor(UIManager.getColor("Component.accentColor"), 0.7f));
    }

    private JButton createItem(String icon, ActionListener action) {
        JButton button = new JButton(new FlatSVGIcon("raven/messenger/icon/drawer/" + icon, 0.45f));
        button.putClientProperty(FlatClientProperties.STYLE, "" +
                "margin:5,8,5,8;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;");
        button.addActionListener(action);
        return button;
    }

    private void logout() {
        ApiService.getInstance().closeAll();
        FormsManager.getInstance().showForm(new Login(null));
    }

    private void showSettings() {
    }
}
