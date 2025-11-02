package raven.messenger.login;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.api.exception.ResponseException;
import raven.messenger.component.PasswordStrengthStatus;
import raven.messenger.connection.ConnectionManager;
import raven.messenger.manager.ErrorManager;
import raven.messenger.manager.FormsManager;
import raven.messenger.models.other.ModelGender;
import raven.messenger.models.request.ModelRegister;
import raven.messenger.service.ServiceAuth;
import raven.messenger.util.StyleUtil;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.net.ConnectException;

public class Register extends JPanel {
    private final ServiceAuth serviceAuth = new ServiceAuth();

    public Register() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));
        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        txtConfirmPassword = new JPasswordField();
        cmdRegister = new JButton("Sign Up") {
            @Override
            public boolean isDefaultButton() {
                return true;
            }
        };
        passwordStrengthStatus = new PasswordStrengthStatus();

        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45", "[fill,360]"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:20;" +
                "[light]background:shade(@background,5%);" +
                "[dark]background:tint(@background,3%);");

        txtFirstName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "First name");
        txtLastName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Last name");
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username or email");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        txtConfirmPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Re-enter your password");

        StyleUtil.applyStyleTextFieldMedium(txtFirstName);
        StyleUtil.applyStyleTextFieldMedium(txtLastName);
        StyleUtil.applyStyleTextFieldMedium(txtUsername);
        StyleUtil.applyStyleTextFieldMedium(txtPassword);
        StyleUtil.applyStyleTextFieldMedium((JTextComponent) txtConfirmPassword);

        txtPassword.addPropertyChangeListener("FlatLaf.internal.FlatPasswordFieldUI.revealSelected", evt -> {
            txtConfirmPassword.setEchoChar(txtPassword.getEchoChar());
        });

        cmdRegister.putClientProperty(FlatClientProperties.STYLE, "" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "default.borderWidth:0;");

        JLabel lbTitle = new JLabel("Welcome to Chat Application");
        JLabel description = new JLabel("Join us to chat, connect, and make new friends.");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10;");
        description.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:$Text.lowForeground;");

        passwordStrengthStatus.initPasswordField(txtPassword);
        Component panelGender = createGenderPanel();
        cmdRegister.addActionListener(e -> register());
        FormsManager.getInstance().autoFocus(o -> register(), txtFirstName, txtLastName, jrMale, jrFemale, txtUsername, txtPassword, txtConfirmPassword);

        panel.add(lbTitle);
        panel.add(description);
        panel.add(new JLabel("Full Name"), "gapy 10");
        panel.add(txtFirstName, "split 2");
        panel.add(txtLastName);
        panel.add(new JLabel("Gender"), "gapy 8");
        panel.add(panelGender);
        panel.add(new JSeparator(), "gapy 5 5");
        panel.add(new JLabel("Username or Email"));
        panel.add(txtUsername);
        panel.add(new JLabel("Password"), "gapy 8");
        panel.add(txtPassword);
        panel.add(passwordStrengthStatus, "gapy 0");
        panel.add(new JLabel("Confirm Password"), "gapy 0");
        panel.add(txtConfirmPassword);
        panel.add(cmdRegister, "gapy 20");
        panel.add(createLoginLabel(), "gapy 10");
        add(panel);
    }

    private Component createGenderPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;");
        jrMale = new JRadioButton("Male");
        jrFemale = new JRadioButton("Female");
        groupGender = new ButtonGroup();
        groupGender.add(jrMale);
        groupGender.add(jrFemale);
        jrMale.setSelected(true);
        panel.add(jrMale);
        panel.add(jrFemale);
        return panel;
    }

    private Component createLoginLabel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;");
        JButton cmdLogin = new JButton("<html><a href=\"#\">Sign in here</a></html>");
        cmdLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:3,3,3,3;");
        cmdLogin.setContentAreaFilled(false);
        cmdLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmdLogin.addActionListener(e -> {
            FormsManager.getInstance().showForm(new Login(null));
        });
        JLabel label = new JLabel("Already have an account ?");
        label.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:$Text.lowForeground;");
        panel.add(label);
        panel.add(cmdLogin);
        return panel;
    }

    private void register() {
        if (FormsManager.getInstance().validateEmpty(txtFirstName, txtLastName, txtUsername, txtPassword, txtConfirmPassword)) {
            if (checkPassword()) {
                String firstName = txtFirstName.getText().trim();
                String lastName = txtLastName.getText().trim();
                ModelGender gender = new ModelGender(jrMale.isSelected() ? "M" : "F");
                String userName = txtUsername.getText().trim();
                String password = String.valueOf(txtPassword.getPassword());
                ModelRegister model = new ModelRegister(firstName, lastName, gender, userName, password);
                try {
                    String response = serviceAuth.register(model);
                    FormsManager.getInstance().showForm(new Login(response));
                } catch (ConnectException e) {
                    ConnectionManager.getInstance().showError(() -> FormsManager.getInstance().showForm(this), true);
                } catch (ResponseException e) {
                    ErrorManager.getInstance().showError(e);
                }
            } else {
                FormsManager.getInstance().applyErrorOutline(txtPassword, txtConfirmPassword);
            }
        }
    }

    private boolean checkPassword() {
        String password = String.valueOf(txtPassword.getPassword());
        String confirmPassword = String.valueOf(txtConfirmPassword.getPassword());
        return password.equals(confirmPassword);
    }

    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JRadioButton jrMale;
    private JRadioButton jrFemale;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private ButtonGroup groupGender;
    private JButton cmdRegister;
    private PasswordStrengthStatus passwordStrengthStatus;
}
