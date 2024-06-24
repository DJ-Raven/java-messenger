package raven.messenger.drawer;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import raven.messenger.option.OptionManager;
import raven.modal.drawer.data.Item;
import raven.modal.drawer.data.MenuItem;
import raven.modal.drawer.menu.MenuOption;
import raven.modal.drawer.simple.SimpleDrawerBuilder;
import raven.modal.drawer.simple.footer.SimpleFooterData;
import raven.modal.drawer.simple.header.SimpleHeaderData;
import raven.modal.drawer.simple.header.SimpleHeaderStyle;

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
    public MenuOption getSimpleMenuOption() {

        MenuItem[] menus = new MenuItem[]{
                new Item("New group", "group.svg"),
                new Item("Account", "account.svg"),
                new Item("Local storage", "storage.svg")
        };

        MenuOption menuOption = new MenuOption()
                .setMenus(menus)
                .setBaseIconPath("raven/messenger/icon/drawer")
                .setIconScale(0.5f);
        menuOption.addMenuEvent((menuAction, index) -> {
            if (index.length == 1) {
                if (index[0] == 0) {
                    OptionManager.getInstance().newGroup();
                    MenuDrawer.getInstance().closeDrawer();
                } else if (index[0] == 1) {
                    OptionManager.getInstance().showProfile();
                    MenuDrawer.getInstance().closeDrawer();
                } else if (index[0] == 2) {
                    OptionManager.getInstance().showStorage();
                    MenuDrawer.getInstance().closeDrawer();
                }
            }
        });
        return menuOption;
    }

    @Override
    public int getDrawerWidth() {
        return 275;
    }

    @Override
    public SimpleFooterData getSimpleFooterData() {
        return new SimpleFooterData()
                .setTitle("Java Messenger")
                .setDescription("Version 1.2.3");
    }
}
