package raven.messenger.option.setting;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.intellijthemes.*;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTGitHubDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTSolarizedLightIJTheme;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.LoggingFacade;
import net.miginfocom.swing.MigLayout;
import raven.messenger.util.ComponentUtil;

import javax.swing.*;
import java.awt.*;

public class DialogSetting extends JPanel {

    private ThemesSelection themesSelection;
    private AccentColorPanel accentColorPanel;

    public DialogSetting() {
        init();
    }

    public void open() {
        themesSelection.scrollToSelected();
    }

    private void init() {
        setLayout(new MigLayout("fill,wrap,insets n 0 0 0", "[fill,450]", "[][]"));

        createAccentColor();
        add(ComponentUtil.createInfoText("Choose the theme accent color to add your personal touch to the interface."));

        createThemes();
        add(ComponentUtil.createInfoText("Change the appearance by selecting from the available themes."));

        createZooming();
        add(ComponentUtil.createInfoText("Change the overall scale of the UI and text to improve", "readability or fit different screen resolutions."));
    }

    private void createThemes() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets n 30 n 30", "[fill]"));
        themesSelection = new ThemesSelection(
                // core themes
                new FlatMacDarkLaf(),
                new FlatMacLightLaf(),
                new FlatDarculaLaf(),
                new FlatIntelliJLaf(),

                // dark themes
                new FlatXcodeDarkIJTheme(),
                new FlatVuesionIJTheme(),
                new FlatSpacegrayIJTheme(),
                new FlatMTGitHubDarkIJTheme(),
                new FlatGruvboxDarkHardIJTheme(),
                new FlatMaterialDesignDarkIJTheme(),
                new FlatArcDarkOrangeIJTheme(),
                new FlatDarkPurpleIJTheme(),
                new FlatCobalt2IJTheme(),

                // light themes
                new FlatMTSolarizedLightIJTheme(),
                new FlatCyanLightIJTheme()
        );
        themesSelection.setCallback(theme -> changeThemes(theme));
        panel.add(createScroll(themesSelection));
        add(panel);
    }

    private void createAccentColor() {
        accentColorPanel = new AccentColorPanel();
        add(accentColorPanel);
    }

    private void createZooming() {
        JPanel panel = new JPanel(new MigLayout("insets n 30 n 30", "[fill]"));
        JComboBox<Object> comboUIScale = new JComboBox<>(new Object[]{"100%", "125%"});

        panel.add(comboUIScale);
        add(panel);
    }

    private JScrollPane createScroll(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;" +
                "width:6;");

        return scrollPane;
    }

    private void changeThemes(FlatLaf theme) {
        EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            try {
                UIManager.setLookAndFeel(theme.getClass().getName());
                accentColorPanel.updateAccentColorButtons();
            } catch (Exception err) {
                LoggingFacade.INSTANCE.logSevere(null, err);
            }
            FlatLaf.updateUI();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }
}
