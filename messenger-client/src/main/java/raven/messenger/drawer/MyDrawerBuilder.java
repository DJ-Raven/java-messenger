package raven.messenger.drawer;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import raven.messenger.api.ApiService;
import raven.messenger.login.Login;
import raven.messenger.manager.FormsManager;
import raven.messenger.option.OptionManager;
import raven.modal.Drawer;
import raven.modal.drawer.item.Item;
import raven.modal.drawer.item.MenuItem;
import raven.modal.drawer.menu.MenuOption;
import raven.modal.drawer.simple.SimpleDrawerBuilder;
import raven.modal.drawer.simple.footer.SimpleFooter;
import raven.modal.drawer.simple.footer.SimpleFooterData;
import raven.modal.drawer.simple.header.SimpleHeader;
import raven.modal.drawer.simple.header.SimpleHeaderData;
import raven.modal.drawer.simple.header.SimpleHeaderStyle;

import javax.swing.*;
import java.awt.*;

public class MyDrawerBuilder extends SimpleDrawerBuilder {

    public MyDrawerBuilder() {
        super(createSimpleMenuOption());
    }

    public static MenuOption createSimpleMenuOption() {
        // create simple menu option
        MenuOption simpleMenuOption = new MenuOption();

        MenuItem[] menus = new MenuItem[]{
                new Item("New group", "group.svg"),
                new Item("Account", "account.svg"),
                new Item("Local storage", "storage.svg"),
                new Item("Log Out", "logout.svg")
        };
        simpleMenuOption.setMenus(menus)
                .setBaseIconPath("raven/messenger/icon/drawer")
                .setIconScale(0.5f);

        simpleMenuOption.addMenuEvent((menuAction, index) -> {
            if (index.length == 1) {
                int i = index[0];
                if (i == 0) {
                    OptionManager.getInstance().newGroup();
                    MenuDrawer.getInstance().closeDrawer();
                } else if (i == 1) {
                    OptionManager.getInstance().showProfile();
                    MenuDrawer.getInstance().closeDrawer();
                } else if (i == 2) {
                    OptionManager.getInstance().showStorage();
                    MenuDrawer.getInstance().closeDrawer();
                } else if (i == 3) {
                    ApiService.getInstance().closeAll();
                    FormsManager.getInstance().showForm(new Login(null));
                    Drawer.setVisible(false);
                }
            }
        });
        return simpleMenuOption;
    }

    @Override
    public SimpleHeaderData getSimpleHeaderData() {
        FlatSVGIcon icon = new FlatSVGIcon("raven/messenger/icon/account.svg", 50, 50);
        return new SimpleHeaderData()
                .setIcon(icon)
                .setTitle("Ra Ven")
                .setDescription("raven@gmail.com")
                .setHeaderStyle(new SimpleHeaderStyle() {
                    @Override
                    public void styleComponent(JComponent component, int styleType) {
                        if (styleType == SimpleHeader.LABEL_TITLE_STYLE) {
                            component.putClientProperty(FlatClientProperties.STYLE, "" +
                                    "font:bold +1");
                        }
                    }
                });
    }

    @Override
    public int getDrawerWidth() {
        return 275;
    }

    @Override
    public Component getFooter() {
        return new SimpleFooter(getSimpleFooterData());
    }

    @Override
    public SimpleFooterData getSimpleFooterData() {
        return new SimpleFooterData()
                .setTitle("Java Messenger")
                .setDescription("Version 1.4.1");
    }
}
