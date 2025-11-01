package raven.messenger.option.setting;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.LoggingFacade;
import net.miginfocom.swing.MigLayout;
import raven.color.ColorPicker;
import raven.messenger.component.AccentColorIcon;
import raven.messenger.util.AppPreferences;
import raven.messenger.util.StyleUtil;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AccentColorPanel extends JPanel {

    private final String[] accentColorKeys = {
            "Themes.accent.default", "Themes.accent.blue", "Themes.accent.purple", "Themes.accent.red",
            "Themes.accent.orange", "Themes.accent.yellow", "Themes.accent.green",
    };
    private final String[] accentColorNames = {
            "Default", "Blue", "Purple", "Red", "Orange", "Yellow", "Green",
    };

    private final JToggleButton[] accentColorButtons = new JToggleButton[accentColorKeys.length];
    private JToggleButton accentColorCustomButton;
    private JToggleButton oldSelected;

    public AccentColorPanel() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("insets n 30 n 30"));
        ButtonGroup group = new ButtonGroup();
        JToolBar toolBar = new JToolBar();
        toolBar.putClientProperty(FlatClientProperties.STYLE, "" +
                "hoverButtonGroupBackground:null;");

        boolean selected = false;
        for (int i = 0; i < accentColorButtons.length; i++) {
            accentColorButtons[i] = new JToggleButton(new AccentColorIcon(accentColorKeys[i]));
            accentColorButtons[i].setToolTipText(accentColorNames[i]);
            accentColorButtons[i].addActionListener(this::accentColorChanged);
            toolBar.add(accentColorButtons[i]);
            group.add(accentColorButtons[i]);
            if (!selected) {
                if (AppPreferences.accentColor == null) {
                    if (i == 0) {
                        accentColorButtons[i].setSelected(true);
                        oldSelected = accentColorButtons[i];
                        selected = true;
                    }
                } else {
                    Color color = UIManager.getColor(accentColorKeys[i]);
                    if (AppPreferences.accentColor.equals(color)) {
                        accentColorButtons[i].setSelected(true);
                        oldSelected = accentColorButtons[i];
                        selected = true;
                    }
                }
            }
        }
        accentColorCustomButton = createCustomAccentColor();
        group.add(accentColorCustomButton);
        toolBar.add(accentColorCustomButton);
        if (!selected) {
            accentColorCustomButton.setSelected(true);
        }

        updateAccentColorButtons();
        add(toolBar);
    }

    private void accentColorChanged(ActionEvent e) {
        String accentColorKey = null;
        for (int i = 0; i < accentColorButtons.length; i++) {
            if (accentColorButtons[i].isSelected()) {
                accentColorKey = accentColorKeys[i];
                oldSelected = accentColorButtons[i];
                break;
            }
        }
        AppPreferences.accentColor = (accentColorKey != null && !accentColorKey.equals(accentColorKeys[0]))
                ? UIManager.getColor(accentColorKey)
                : null;
        applyAccentColor();
    }

    private void applyAccentColor() {
        Class<? extends LookAndFeel> lafClass = UIManager.getLookAndFeel().getClass();
        try {
            AppPreferences.updateAccentColor(AppPreferences.accentColor);
            FlatLaf.setup(lafClass.getDeclaredConstructor().newInstance());
            FlatLaf.updateUI();
        } catch (Exception ex) {
            LoggingFacade.INSTANCE.logSevere(null, ex);
        }
    }

    public void updateAccentColorButtons() {
        Class<? extends LookAndFeel> lafClass = UIManager.getLookAndFeel().getClass();
        boolean isAccentColorSupported = StyleUtil.isCoreThemes(lafClass);
        for (int i = 0; i < accentColorButtons.length; i++) {
            accentColorButtons[i].setEnabled(isAccentColorSupported);
        }
        if (accentColorCustomButton != null) {
            accentColorCustomButton.setEnabled(isAccentColorSupported);
        }
    }

    private JToggleButton createCustomAccentColor() {
        JToggleButton button = new JToggleButton(new FlatSVGIcon("raven/messenger/icon/color.svg", 16, 16));
        button.addActionListener(e -> {
            JPanel panel = new JPanel(new MigLayout("insets 0 10 0 10"));
            ColorPicker colorPicker = new ColorPicker(AppPreferences.accentColor);
            colorPicker.setColorPaletteEnabled(false);
            panel.add(colorPicker);
            SimpleModalBorder modalBorder = new SimpleModalBorder(panel, "Select Color", SimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
                if (action == SimpleModalBorder.YES_OPTION) {
                    AppPreferences.accentColor = colorPicker.getSelectedColor();
                    oldSelected = null;
                    applyAccentColor();
                } else if (action != SimpleModalBorder.OPENED) {
                    if (oldSelected != null) {
                        oldSelected.setSelected(true);
                    }
                }
            });
            ModalDialog.showModal(this, modalBorder);
        });
        return button;
    }
}
