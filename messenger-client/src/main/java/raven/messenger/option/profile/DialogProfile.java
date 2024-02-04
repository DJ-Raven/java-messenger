package raven.messenger.option.profile;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;
import raven.messenger.api.exception.ResponseException;
import raven.messenger.component.profile.ProfilePanel;
import raven.messenger.manager.FormsManager;
import raven.messenger.manager.ProfileManager;
import raven.messenger.models.other.ModelGender;
import raven.messenger.models.other.ModelName;
import raven.messenger.models.response.ModelProfile;
import raven.messenger.util.ComponentUtil;
import raven.messenger.util.MethodUtil;
import raven.messenger.util.NetworkDataUtil;
import raven.popup.GlassPanePopup;
import raven.popup.component.SimplePopupBorder;
import raven.popup.component.SimplePopupBorderOption;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DialogProfile extends JPanel {

    public DialogProfile() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap,fillx,insets 0", "center"));
        createProfile();
        createInfo();
        initData();
    }

    private void createProfile() {
        ProfilePanel profilePanel = new ProfilePanel();
        profilePanel.setEventProfileSelected(image -> editProfile(image));
        ModelProfile profile = ProfileManager.getInstance().getProfile();
        Icon icon = NetworkDataUtil.getNetworkIcon(profile.getProfile(), profile.getName().getProfileString(), 100, 100, 999);
        profilePanel.setIcon(icon);
        add(profilePanel);
    }


    private void createInfo() {
        JPanel panel = new JPanel(new MigLayout("wrap,insets 0 0 15 0,fillx", "center", "[]20[][]"));
        buttonUpdateBio = new JButton(MethodUtil.createIcon("raven/messenger/icon/edit.svg", 0.7f));
        buttonUpdateBio.setVisible(false);
        bioLength = new JLabel("40");
        bioLength.putClientProperty(FlatClientProperties.STYLE, "" + "foreground:$Text.lowForeground;" + "border:0,5,0,0");
        labelName = new JLabel("Ra Ven");
        txtBio = new JTextField();
        labelName.putClientProperty(FlatClientProperties.STYLE, "" + "font:+5");
        txtBio.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Bio");
        JToolBar toolBar = new JToolBar();
        toolBar.add(buttonUpdateBio);
        toolBar.add(bioLength);
        txtBio.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, toolBar);
        txtBio.putClientProperty(FlatClientProperties.STYLE, "" + "font:+1;" + "border:5,1,5,1;" + "background:null");

        buttonUpdateBio.addActionListener(e -> {
            try {
                ProfileManager.getInstance().updateProfileBios(txtBio.getText().trim());
                buttonUpdateBio.setVisible(false);
            } catch (ResponseException ex) {

            }
        });
        txtBio.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                bioChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                bioChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                bioChanged();
            }
        });

        panel.add(labelName);
        panel.add(txtBio, "grow 1,gapx 25 25");

        panel.add(ComponentUtil.createInfoText("Provide a short description about yourself.", "Example: Passionate about photography and nature lover."), "grow 1");
        add(panel, "grow 1");
        JPanel panelInfo = new JPanel(new MigLayout("wrap,insets 0,gap 0,fillx", "fill"));
        fieldName = new ButtonField("account.svg", "Name", "Ra Ven");
        fieldGender = new ButtonField("male.svg", "Gender", "Male");
        fieldPhone = new ButtonField("phone.svg", "Phone number", "+855 10 000 000");
        fieldName.addActionListener(actionEvent -> editName());
        fieldGender.addActionListener(actionEvent -> editGender());
        fieldPhone.addActionListener(actionEvent -> editPhone());
        panelInfo.add(fieldName);
        panelInfo.add(fieldGender);
        panelInfo.add(fieldPhone);
        panel.add(panelInfo, "grow 1");
        panel.add(ComponentUtil.createInfoText("Username for display to another people, and also people can", "contact you with phone number if you provide here"), "grow 1");
    }

    private void bioChanged() {
        int length = 40 - txtBio.getText().length();
        bioLength.setText(length + "");
        if (length >= 0) {
            buttonUpdateBio.setVisible(!txtBio.getText().trim().equals(ProfileManager.getInstance().getProfile().getBio()));
        } else {
            buttonUpdateBio.setVisible(false);
        }
    }

    private void initData() {
        ModelProfile profile = ProfileManager.getInstance().getProfile();
        labelName.setText(profile.getName().getFullName());
        fieldName.setDescription(profile.getName().getFullName());
        fieldGender.setDescription(profile.getGender().toString());
        fieldPhone.setDescription(profile.getPhoneNumber());
        txtBio.setText(profile.getBio());
        if (profile.getGender().getGender().equals("M")) {
            fieldGender.setIcon("male.svg");
        } else {
            fieldGender.setIcon("female.svg");
        }
    }

    private void editProfile(BufferedImage image) {
        try {
            ProfileManager.getInstance().updateProfileImage(image);
        } catch (ResponseException | IOException e) {
            e.printStackTrace();
        }
    }

    private void editName() {
        ModelProfile profile = ProfileManager.getInstance().getProfile();
        JPanel panel = new JPanel(new MigLayout("wrap,fill,insets 5 25 5 25", "[fill]"));
        JTextField txtFirstName = new JTextField(profile.getName().getFirstName());
        JTextField txtLastName = new JTextField(profile.getName().getLastName());
        txtFirstName.putClientProperty(FlatClientProperties.STYLE, "" + "background:null");
        txtLastName.putClientProperty(FlatClientProperties.STYLE, "" + "background:null");
        panel.add(new JLabel("First name"));
        panel.add(txtFirstName);
        panel.add(new JLabel("Last name"));
        panel.add(txtLastName);

        String actions[] = {"Cancel", "Save"};
        SimplePopupBorder popupBorder = new SimplePopupBorder(panel, "Edit name", new SimplePopupBorderOption().setWidth(300), actions, (popupController, i) -> {
            if (i == 1) {
                if (FormsManager.getInstance().validateEmpty(txtFirstName, txtLastName)) {
                    try {
                        String firstName = txtFirstName.getText().trim();
                        String lastName = txtLastName.getText().trim();
                        ModelName name = new ModelName(firstName, lastName);
                        ProfileManager.getInstance().updateProfileUser(name);
                        fieldName.setDescription(name.getFullName());
                        labelName.setText(name.getFullName());
                        popupController.closePopup();
                    } catch (ResponseException e) {

                    }
                }
            } else {
                popupController.closePopup();
            }
        });
        GlassPanePopup.showPopup(popupBorder);
    }

    private void editGender() {
        ModelProfile profile = ProfileManager.getInstance().getProfile();
        JPanel panel = new JPanel(new MigLayout("fill,insets 5 25 5 25", "[fill]"));
        ButtonGroup buttonGroup = new ButtonGroup();
        JToggleButton cmdMale = new ButtonGender(MethodUtil.createIcon("raven/messenger/icon/male.svg", 1f));
        JToggleButton cmdFemale = new ButtonGender(MethodUtil.createIcon("raven/messenger/icon/female.svg", 1f));
        buttonGroup.add(cmdMale);
        buttonGroup.add(cmdFemale);
        if (profile.getGender().isMale()) {
            cmdMale.setSelected(true);
        } else {
            cmdFemale.setSelected(true);
        }
        panel.add(cmdMale);
        panel.add(cmdFemale);

        String actions[] = {"Cancel", "Save"};
        SimplePopupBorder popupBorder = new SimplePopupBorder(panel, "Edit gender", new SimplePopupBorderOption().setWidth(300), actions, (popupController, i) -> {
            if (i == 1) {
                ModelGender gender = new ModelGender(cmdMale.isSelected() ? "M" : "F");
                try {
                    ProfileManager.getInstance().updateProfileGender(gender);
                    fieldGender.setDescription(gender.toString());
                    if (gender.isMale()) {
                        fieldGender.setIcon("male.svg");
                    } else {
                        fieldGender.setIcon("female.svg");
                    }
                    popupController.closePopup();
                } catch (ResponseException e) {

                }
            } else {
                popupController.closePopup();
            }
        });
        GlassPanePopup.showPopup(popupBorder);
    }

    private void editPhone() {
        ModelProfile profile = ProfileManager.getInstance().getProfile();
        JPanel panel = new JPanel(new MigLayout("wrap,fill,insets 5 25 5 25", "[fill]"));
        JTextField txtPhone = new JTextField(profile.getPhoneNumber());
        txtPhone.putClientProperty(FlatClientProperties.STYLE, "" + "background:null");
        panel.add(new JLabel("Phone number"));
        panel.add(txtPhone);

        String actions[] = {"Cancel", "Save"};
        SimplePopupBorder popupBorder = new SimplePopupBorder(panel, "Edit phone", new SimplePopupBorderOption().setWidth(300), actions, (popupController, i) -> {
            if (i == 1) {
                if (FormsManager.getInstance().validateEmpty(txtPhone)) {
                    try {
                        String phoneNumber = txtPhone.getText().trim();
                        ProfileManager.getInstance().updateProfilePhoneNumber(phoneNumber);
                        fieldPhone.setDescription(phoneNumber);
                        popupController.closePopup();
                    } catch (ResponseException e) {

                    }
                }
            } else {
                popupController.closePopup();
            }
        });
        GlassPanePopup.showPopup(popupBorder);
    }

    private JLabel labelName;
    private JLabel bioLength;
    private JButton buttonUpdateBio;
    private JTextField txtBio;
    private ButtonField fieldName;
    private ButtonField fieldGender;
    private ButtonField fieldPhone;

    private class ButtonField extends JButton {

        private String icon;
        private String name;
        private String description;

        public void setDescription(String description) {
            this.description = description;
            labelDescription.setText(description);
        }

        public ButtonField(String icon, String name, String description) {
            this.icon = icon;
            this.name = name;
            this.description = description;
            init();
        }

        private void setIcon(String icon) {
            labelIcon.setIcon(MethodUtil.createIcon("raven/messenger/icon/" + icon, 0.5f));
        }

        private void init() {
            setLayout(new MigLayout("fill,insets 5 25 5 25", "[]10[]push[]"));
            putClientProperty(FlatClientProperties.STYLE, "" + "margin:2,0,2,0;" + "borderWidth:0;" + "focusWidth:0;" + "innerFocusWidth:0;" + "background:null;");
            labelIcon = new JLabel(MethodUtil.createIcon("raven/messenger/icon/" + icon, 0.5f));
            add(labelIcon);
            labelName = new JLabel(name);
            labelDescription = new JLabel(description);
            labelName.putClientProperty(FlatClientProperties.STYLE, "" + "font:+1");
            labelDescription.putClientProperty(FlatClientProperties.STYLE, "" + "font:+1");

            add(labelName);
            add(labelDescription);
        }

        private JLabel labelIcon;
        private JLabel labelName;
        private JLabel labelDescription;
    }

    private class ButtonGender extends JToggleButton {

        public ButtonGender(Icon icon) {
            super(icon);
            putClientProperty(FlatClientProperties.STYLE, "" + "arc:10;" + "borderWidth:0;" + "focusWidth:0;" + "innerFocusWidth:0;" + "background:null;");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (isSelected()) {
                int x = 0;
                int y = 0;
                int width = getWidth();
                int height = getHeight();
                Graphics2D g2 = (Graphics2D) g.create();
                FlatUIUtils.setRenderingHints(g2);
                int arc = UIScale.scale(10);
                float size = arc * 0.3f;
                g2.setColor(UIManager.getColor("Component.accentColor"));
                g2.fill(FlatUIUtils.createRoundRectanglePath(x, y + height - size, width, size, 0, 0, arc, arc));
                g2.dispose();
            }
        }
    }
}
