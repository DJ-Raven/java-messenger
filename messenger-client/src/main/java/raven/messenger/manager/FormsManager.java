package raven.messenger.manager;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import raven.messenger.api.ApiService;
import raven.messenger.connection.ConnectionManager;
import raven.messenger.connection.FormUpdate;
import raven.messenger.home.Home;
import raven.messenger.login.Login;
import raven.messenger.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FormsManager {
    private Application application;
    private Home home;
    private static FormsManager instance;

    public static FormsManager getInstance() {
        if (instance == null) {
            instance = new FormsManager();
        }
        return instance;
    }

    private FormsManager() {
    }

    public void init() {
        boolean signed = ApiService.getInstance().init();
        if (signed) {
            showHome();
        } else {
            ConnectionManager.Type type = ConnectionManager.getInstance().checkConnection();
            if (type == ConnectionManager.Type.SUCCESS) {
                showForm(new Login(null));
            } else if (type == ConnectionManager.Type.CLIENT_REQUIRED_UPDATE) {
                showForm(new FormUpdate());
            } else {
                ConnectionManager.getInstance().showError(() -> {

                });
            }
        }
    }

    public void initApplication(Application application) {
        this.application = application;
        init();
    }

    public void showHome() {
        if (home == null) {
            home = new Home();
        }
        showForm(home);
        home.initHome();
    }

    public void showForm(JComponent form) {
        EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            application.getContentPane().removeAll();
            application.getContentPane().add(form);
            application.repaint();
            application.revalidate();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }

    public void applyWarningOutline(JComponent... components) {
        applyOutline(FlatClientProperties.OUTLINE_WARNING, components);
    }

    public void applyErrorOutline(JComponent... components) {
        applyOutline(FlatClientProperties.OUTLINE_ERROR, components);
    }

    public void clearOutline(JComponent... components) {
        applyOutline(null, components);
    }

    public void applyOutline(String keyStyle, JComponent... components) {
        for (int i = 0; i < components.length; i++) {
            components[i].putClientProperty(FlatClientProperties.OUTLINE, keyStyle);
        }
    }

    public boolean validateEmpty(JTextField... textFields) {
        List<JComponent> components = new ArrayList<>();
        for (int i = 0; i < textFields.length; i++) {
            JTextField textField = textFields[i];
            if (textField != null) {
                if (textField instanceof JTextField) {
                    if (textField.getText().trim().isEmpty()) {
                        components.add(textField);
                    } else {
                        clearOutline(textField);
                    }
                } else if (textField instanceof JPasswordField) {
                    JPasswordField passwordField = (JPasswordField) textField;
                    if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                        components.add(textField);
                    } else {
                        clearOutline(textField);
                    }
                }
            }
        }
        if (!components.isEmpty()) {
            applyErrorOutline(components.toArray(new JComponent[components.size()]));
            components.get(0).grabFocus();
        }
        return components.isEmpty();
    }

    public void autoFocus(Consumer consumer, JComponent... components) {
        for (int i = 0; i < components.length; i++) {
            final int index = i;
            components[i].addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if (e.getKeyChar() == 10) {
                        if (e.isControlDown() || index == components.length - 1) {
                            if (consumer != null) {
                                consumer.accept(components[index]);
                            }
                        } else {
                            if (index < components.length - 1) {
                                components[index + 1].grabFocus();
                            }
                        }
                    }
                }
            });
        }
    }

    public JFrame getMainFrame() {
        return application;
    }
}
