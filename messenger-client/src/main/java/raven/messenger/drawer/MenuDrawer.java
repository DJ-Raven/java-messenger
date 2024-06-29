package raven.messenger.drawer;

import raven.messenger.manager.FormsManager;
import raven.messenger.models.response.ModelProfile;
import raven.messenger.util.NetworkDataUtil;
import raven.modal.Drawer;
import raven.modal.drawer.simple.header.SimpleHeader;
import raven.modal.drawer.simple.header.SimpleHeaderData;

import javax.swing.*;

public class MenuDrawer {

    private static MenuDrawer instance;
    private MyDrawerBuilder drawerBuilder;

    public static MenuDrawer getInstance() {
        if (instance == null) {
            instance = new MenuDrawer();
        }
        return instance;
    }

    private MenuDrawer() {
        drawerBuilder = new MyDrawerBuilder();
        Drawer.installDrawer(FormsManager.getInstance().getMainFrame(), drawerBuilder);
    }

    public void showDrawer() {
        SwingUtilities.invokeLater(() -> Drawer.showDrawer());
    }

    public void closeDrawer() {
        Drawer.closeDrawer();
    }

    public void setVisible(boolean v) {
        Drawer.setVisible(v);
    }

    public void setDrawerHeader(ModelProfile profile) {
        SimpleHeader header = (SimpleHeader) drawerBuilder.getHeader();
        SimpleHeaderData data = header.getSimpleHeaderData();
        if (profile != null) {
            data.setIcon(NetworkDataUtil.getNetworkIcon(profile.getProfile(), profile.getName().getProfileString(), 50, 50, 999));
            data.setTitle(profile.getName().getFullName());
            data.setDescription(profile.getBio());
        } else {
            data.setTitle("-");
            data.setDescription("-");
            data.setIcon(null);
        }
        header.setSimpleHeaderData(data);
    }
}
