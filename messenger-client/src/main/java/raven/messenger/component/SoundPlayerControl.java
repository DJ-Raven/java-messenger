package raven.messenger.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import raven.messenger.manager.SoundManager;
import raven.messenger.util.MethodUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SoundPlayerControl extends JPanel {

    public SoundPlayerControl() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap 4", "[grow 0][fill,grow][][]", "[center]3[]3"));
        labelText = new JTextField("Player name");
        labelText.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        labelText.setBorder(BorderFactory.createEmptyBorder());
        labelText.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;");
        labelText.setFocusable(false);
        labelDuration = new JLabel("00:00");
        buttonPlay = createPlayButton();
        progressBar = createProgress();

        add(buttonPlay);
        add(labelText);
        add(labelDuration);
        add(createCloseButton());
        add(progressBar, "span 4,grow");
    }

    private JButton createPlayButton() {
        JButton button = new JButton(new FlatSVGIcon("raven/messenger/icon/play.svg", 0.28f));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            if (SoundManager.getInstance().isRunning()) {
                SoundManager.getInstance().pause();
            } else {
                SoundManager.getInstance().resumes();
            }
        });
        button.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:999;" +
                "margin:3,3,3,3;" +
                "background:null;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;");
        return button;
    }

    private JButton createCloseButton() {
        JButton button = new JButton(new FlatSVGIcon("raven/messenger/icon/close.svg", 0.28f));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            SoundManager.getInstance().stop();
        });
        button.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:999;" +
                "margin:3,3,3,3;" +
                "background:null;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;");
        return button;
    }

    private JProgressBar createProgress() {
        JProgressBar progressBar = new JProgressBar();
        progressBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        progressBar.putClientProperty(FlatClientProperties.STYLE, "" +
                "[dark]background:tint(@background,8%);" +
                "[light]background:shade(@background,8%);" +
                "[dark]foreground:tint(@background,25%);" +
                "[light]foreground:shade(@background,25%);");
        progressBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                float f = (float) e.getX() / progressBar.getWidth();
                SoundManager.getInstance().skip(f);
            }
        });
        return progressBar;
    }

    public void setSoundName(String soundName) {
        labelText.setText(soundName);
    }

    public void lengthChanged(float f, int length) {
        progressBar.setValue((int) (f * 100));
        labelDuration.setText(formatDuration(length));
    }

    public void playButton() {
        buttonPlay.setIcon(MethodUtil.createIcon("raven/messenger/icon/pause.svg", 0.28f));
    }

    public void stopButton() {
        buttonPlay.setIcon(MethodUtil.createIcon("raven/messenger/icon/play.svg", 0.28f));
    }

    private String formatDuration(long lengthInSeconds) {
        long minutes = lengthInSeconds / 60;
        long seconds = lengthInSeconds % 60;
        String duration = String.format("%02d:%02d", minutes, seconds);
        return duration;
    }

    private JButton buttonPlay;
    private JTextField labelText;
    private JLabel labelDuration;
    private JProgressBar progressBar;
}
