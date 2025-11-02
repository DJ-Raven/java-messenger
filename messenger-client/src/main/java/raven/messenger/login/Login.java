package raven.messenger.login;

import com.formdev.flatlaf.FlatClientProperties;
import io.restassured.http.Cookies;
import net.miginfocom.swing.MigLayout;
import raven.messenger.api.exception.ResponseException;
import raven.messenger.connection.ConnectionManager;
import raven.messenger.manager.ErrorManager;
import raven.messenger.manager.FormsManager;
import raven.messenger.service.ServiceAuth;
import raven.messenger.store.CookieManager;
import raven.messenger.util.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ConnectException;

public class Login extends JPanel {

    private final ServiceAuth serviceAuth = new ServiceAuth();

    public Login(String initUser) {
        init();
        if (initUser != null) {
            txtUsername.setText(initUser);
        }
    }

    private void init() {
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        chRememberMe = new JCheckBox("Remember me");
        cmdLogin = new JButton("Login") {
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };
        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45", "fill,250:280"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:20;" +
                "[light]background:shade(@background,5%);" +
                "[dark]background:tint(@background,3%);");

        cmdLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "default.borderWidth:0;");

        cmdLogin.addActionListener(e -> login());
        FormsManager.getInstance().autoFocus(o -> login(), txtUsername, txtPassword);
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username or email");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");

        StyleUtil.applyStyleTextFieldMedium(txtUsername);
        StyleUtil.applyStyleTextFieldMedium(txtPassword);
        StyleUtil.applyStyleCheckBox(chRememberMe);

        JLabel lbTitle = new JLabel("Welcome back!");
        JLabel description = new JLabel("Please sign in to access your account");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10;");
        description.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:$Text.lowForeground;");

        panel.add(lbTitle);
        panel.add(description);
        panel.add(new JLabel("Username"), "gapy 15");
        panel.add(txtUsername);
        panel.add(new JLabel("Password"), "gapy 8");
        panel.add(txtPassword);
        panel.add(chRememberMe, "grow 0");
        panel.add(cmdLogin, "gapy 10");
        panel.add(createSignUpLabel(), "gapy 10");
        add(panel);
    }

    private Component createSignUpLabel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;");
        JButton cmdRegister = new JButton("<html><a href=\"#\">Sign up</a></html>");
        cmdRegister.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:3,3,3,3;");
        cmdRegister.setContentAreaFilled(false);
        cmdRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmdRegister.addActionListener(e -> {
            FormsManager.getInstance().showForm(new Register());
        });
        JLabel label = new JLabel("Don't have an account ?");
        label.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:$Text.lowForeground;");
        panel.add(label);
        panel.add(cmdRegister);
        return panel;
    }

    private void login() {
        try {
            if (FormsManager.getInstance().validateEmpty(txtUsername, txtPassword)) {
                String user = txtUsername.getText().trim();
                String password = String.valueOf(txtPassword.getPassword());
                Cookies cookie = serviceAuth.login(user, password);
                if (chRememberMe.isSelected()) {
                    CookieManager.getInstance().storeCookie(cookie);
                } else {
                    CookieManager.getInstance().storeCookie(null);
                }
                CookieManager.getInstance().setCookieString(cookie.toString());
                FormsManager.getInstance().showHome();
            }
        } catch (ConnectException e1) {
            ConnectionManager.getInstance().showError(() -> FormsManager.getInstance().showForm(this), true);
        } catch (ResponseException | IOException e) {
            ErrorManager.getInstance().showError(e);
        }
    }

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chRememberMe;
    private JButton cmdLogin;
}
