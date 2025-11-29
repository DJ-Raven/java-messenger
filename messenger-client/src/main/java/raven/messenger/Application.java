package raven.messenger;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.util.UIScale;
import raven.messenger.manager.DialogManager;
import raven.messenger.manager.FormsManager;
import raven.messenger.util.AppPreferences;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application extends JFrame {

    public Application() {
        init();
    }

    private void init() {
        setTitle("Messenger");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
        setSize(UIScale.scale(new Dimension(1280, 700)));
        setMinimumSize(UIScale.scale(new Dimension(750, 500)));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                FormsManager.getInstance().initApplication(Application.this);
            }
        });
        DialogManager.getInstance().init(this);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        AppPreferences.init();
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("raven.messenger.themes");
        AppPreferences.setupLaf();
        EventQueue.invokeLater(() -> new Application().setVisible(true));
    }
}
