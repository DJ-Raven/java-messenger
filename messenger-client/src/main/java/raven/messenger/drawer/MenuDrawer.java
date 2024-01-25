package raven.messenger.drawer;

import raven.drawer.Drawer;
import raven.drawer.component.header.SimpleHeader;
import raven.drawer.component.header.SimpleHeaderData;
import raven.messenger.models.response.ModelProfile;
import raven.messenger.util.NetworkDataUtil;

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
        Drawer.getInstance().setDrawerBuilder(drawerBuilder);
    }

    public void showDrawer() {
        SwingUtilities.invokeLater(() -> Drawer.getInstance().showDrawer());
    }

    public void closeDrawer() {
        Drawer.getInstance().closeDrawer();
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
