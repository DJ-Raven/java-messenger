package raven.messenger;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.util.UIScale;
import raven.messenger.manager.DialogManager;
import raven.messenger.manager.FormsManager;
import raven.messenger.util.ErrorReporter;

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
        setSize(UIScale.scale(new Dimension(1200, 700)));
        setMinimumSize(UIScale.scale(new Dimension(750, 500)));

        // Enhanced window listener with error handling
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                try {
                    FormsManager.getInstance().initApplication(Application.this);
                } catch (Exception ex) {
                    ErrorReporter.handleException("Failed to initialize application", ex, true);
                }
            }

            @Override
            public void windowClosing(WindowEvent e) {
                // Clean up resources on close
                cleanup();
            }
        });

        DialogManager.getInstance().init(this);
        setLocationRelativeTo(null);
    }

    private void cleanup() {
        try {
            // Add any cleanup logic here
            System.out.println("Application cleanup completed");
        } catch (Exception e) {
            ErrorReporter.handleException("Error during application cleanup", e, false);
        }
    }

    public static void main(String[] args) {
        // Set up global exception handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            ErrorReporter.handleException("Uncaught exception in thread: " + thread.getName(),
                    throwable, true);
        });

        try {
            FlatRobotoFont.install();
            FlatLaf.registerCustomDefaultsSource("raven.messenger.themes");
            UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
            FlatMacDarkLaf.setup();
            EventQueue.invokeLater(() -> {
                try {
                    new Application().setVisible(true);
                } catch (Exception e) {
                    ErrorReporter.handleException("Failed to create application window", e, true);
                }
            });
        } catch (Exception e) {
            ErrorReporter.handleException("Failed to initialize application theme", e, true);
        }
    }
}