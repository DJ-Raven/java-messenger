package raven.messenger.drawer;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import raven.drawer.component.DrawerPanel;
import raven.drawer.component.SimpleDrawerBuilder;
import raven.drawer.component.footer.SimpleFooterData;
import raven.drawer.component.header.SimpleHeaderData;
import raven.drawer.component.header.SimpleHeaderStyle;
import raven.drawer.component.menu.SimpleMenuOption;
import raven.drawer.component.menu.data.Item;
import raven.drawer.component.menu.data.MenuItem;
import raven.messenger.manager.FormsManager;
import raven.messenger.option.OptionManager;

import javax.swing.*;

public class MyDrawerBuilder extends SimpleDrawerBuilder {

    @Override
    public SimpleHeaderData getSimpleHeaderData() {
        FlatSVGIcon icon = new FlatSVGIcon("raven/messenger/icon/account.svg", 50, 50);
        return new SimpleHeaderData()
                .setIcon(icon)
                .setTitle("Ra Ven")
                .setDescription("raven@gmail.com")
                .setHeaderStyle(new SimpleHeaderStyle() {
                    @Override
                    public void styleProfile(JLabel label) {
                        label.putClientProperty(FlatClientProperties.STYLE, "" +
                                "font:bold +5");
                    }
                });
    }

    @Override
    public SimpleMenuOption getSimpleMenuOption() {

        MenuItem[] menus = new MenuItem[]{
                new Item("Account", "account.svg"),
                new Item("Local storage", "storage.svg")
        };

        SimpleMenuOption menuOption = new SimpleMenuOption()
                .setMenus(menus)
                .setBaseIconPath("raven/messenger/icon/drawer")
                .setIconScale(0.5f);
        menuOption.addMenuEvent((menuAction, index) -> {
            if (index.length == 1) {
                if (index[0] == 0) {
                    OptionManager.getInstance().showProfile();
                    MenuDrawer.getInstance().closeDrawer();
                } else if (index[0] == 1) {
                    OptionManager.getInstance().showStorage();
                    MenuDrawer.getInstance().closeDrawer();
                }
            }
        });
        return menuOption;
    }

    @Override
    public SimpleFooterData getSimpleFooterData() {
        return new SimpleFooterData()
                .setTitle("Java Messenger")
                .setDescription("Version 1.0.0");
    }
}
