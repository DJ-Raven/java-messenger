package raven.messenger.manager;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.util.SystemFileChooser;
import raven.messenger.Application;
import raven.messenger.api.ApiService;
import raven.messenger.auth.Login;
import raven.messenger.connection.ConnectionManager;
import raven.messenger.connection.FormUpdate;
import raven.messenger.home.Home;
import raven.messenger.models.response.ModelProfile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

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
        initSystemFileChooserStateStorage();
        boolean signed = ApiService.getInstance().init();
        if (signed) {
            showHome();
        } else {
            ConnectionManager.Type type = ConnectionManager.getInstance().checkConnection();
            if (type == ConnectionManager.Type.SUCCESS) {
                showLogin();
            } else if (type == ConnectionManager.Type.CLIENT_REQUIRED_UPDATE) {
                showForm(new FormUpdate());
            } else {
                ConnectionManager.getInstance().showError(this::showLogin, true);
            }
        }
    }

    private void initSystemFileChooserStateStorage() {
        SystemFileChooser.setStateStore(new SystemFileChooser.StateStore() {
            private static final String KEY_PREFIX = "fileChooser.";

            private final Preferences state = Preferences.userRoot().node("java-messenger-client");

            @Override
            public String get(String key, String def) {
                return state.get(KEY_PREFIX + key, def);
            }

            @Override
            public void put(String key, String value) {
                if (value != null)
                    state.put(KEY_PREFIX + key, value);
                else
                    state.remove(KEY_PREFIX + key);
            }
        });
    }

    private void showLogin() {
        showForm(new Login(null));
    }

    public void updateProfile(ModelProfile profile) {
        home.updateProfile(profile);
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
            checkFormActionForOpen(form);
        });
    }

    public void applyWarning(JComponent... components) {
        applyField("warning", components);
    }

    public void applyError(JComponent... components) {
        applyField("error", components);
    }

    public void clear(JComponent... components) {
        applyField(null, components);
    }

    private void applyField(String keyStyle, JComponent... components) {
        for (int i = 0; i < components.length; i++) {
            JComponent com = components[i];
            Object oldStyle = com.getClientProperty(FlatClientProperties.STYLE_CLASS);
            if (keyStyle == null) {
                String st = oldStyle == null ? null : (oldStyle.toString().replace(" error", "").replace("warning", "")).trim();
                com.putClientProperty(FlatClientProperties.STYLE_CLASS, st);
            } else {
                if (oldStyle != null) {
                    if (!oldStyle.toString().contains(keyStyle)) {
                        com.putClientProperty(FlatClientProperties.STYLE_CLASS, (oldStyle + " " + keyStyle).trim());
                    }
                } else {
                    com.putClientProperty(FlatClientProperties.STYLE_CLASS, keyStyle);
                }
            }
        }
    }

    public boolean validateEmpty(JTextField... textFields) {
        List<JComponent> components = new ArrayList<>();
        for (int i = 0; i < textFields.length; i++) {
            JTextField textField = textFields[i];
            if (textField != null) {
                if (textField.getText().trim().isEmpty()) {
                    components.add(textField);
                } else {
                    clear(textField);
                }
            }
        }
        if (!components.isEmpty()) {
            applyError(components.toArray(new JComponent[0]));
            components.get(0).grabFocus();
        }
        return components.isEmpty();
    }

    public void autoFocus(Consumer<Object> consumer, JComponent... components) {
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

    private void checkFormActionForOpen(JComponent form) {
        if (form instanceof FormAction) {
            SwingUtilities.invokeLater(() -> ((FormAction) form).formOpen());
        }
    }

    public static abstract class FormAction extends JPanel {
        public void formOpen() {
        }
    }
}
