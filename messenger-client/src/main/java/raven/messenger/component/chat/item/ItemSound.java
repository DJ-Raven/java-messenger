package raven.messenger.component.chat.item;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.component.ButtonProgress;
import raven.messenger.component.chat.model.ChatSoundData;
import raven.messenger.manager.SoundManager;
import raven.messenger.plugin.sound.AudioUtil;
import raven.messenger.plugin.sound.WaveFormListener;
import raven.messenger.plugin.sound.WaveFormPanel;
import raven.messenger.store.StoreManager;
import raven.messenger.util.ComponentUtil;
import raven.messenger.util.MethodUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class ItemSound extends JPanel implements ProgressChat {

    private final ChatSoundData data;
    private final int type;
    private JPopupMenu popupMenu;
    private WaveFormPanel waveFormPanel;
    private ButtonProgress buttonPlay;

    public ItemSound(ChatSoundData data, int type) {
        this.data = data;
        this.type = type;
        init();
    }

    private void init() {
        if (data.getData() != null) {
            waveFormPanel = new WaveFormPanel();
            waveFormPanel.addWaveFormListener(new WaveFormListener() {
                @Override
                public void onClick(float v) {
                    if (!SoundManager.getInstance().checkSound(ItemSound.this)) {
                        play();
                    }
                    if (SoundManager.getInstance().checkSound(ItemSound.this)) {
                        SoundManager.getInstance().getSoundPlayback().skip(v);
                        SoundManager.getInstance().getSoundPlayback().resumes();
                    }
                }

                @Override
                public void onDrag() {
                    if (!SoundManager.getInstance().checkSound(ItemSound.this)) {
                        play();
                    }
                    if (SoundManager.getInstance().checkSound(ItemSound.this)) {
                        SoundManager.getInstance().getSoundPlayback().pause();
                    }
                }
            });
            waveFormPanel.setWaveFormData(AudioUtil.createDefaultWaveFormData(data.getData()));
            String foregroundKey = type == 1 ? "$Chat.item.recipientVoiceForeground" : "$Chat.item.myselfVoiceForeground";
            waveFormPanel.putClientProperty(FlatClientProperties.STYLE, "" +
                    "background:null;" +
                    "foreground:" + foregroundKey);
        }
        String backgroundKey = type == 1 ? "$Chat.item.background" : "$Chat.item.myselfBackground";
        setLayout(new MigLayout("insets 3 5 3 5,gapy 0", "[]10[184,fill]", ""));
        putClientProperty(FlatClientProperties.STYLE, "" +
                "background:" + backgroundKey);

        String icon;
        if (StoreManager.getInstance().getFile(data.getName()) != null) {
            icon = "play.svg";
        } else {
            icon = "download.svg";
            data.setEventFileNameChanged(o -> {
                buttonPlay.setIcon(MethodUtil.createIcon("raven/messenger/icon/play.svg", 0.3f));
            });
        }
        buttonPlay = new ButtonProgress(MethodUtil.createIcon("raven/messenger/icon/" + icon, 0.3f));
        buttonPlay.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:999;" +
                "margin:7,7,7,7;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;");

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    File file = StoreManager.getInstance().getFile(data.getName());
                    if (file != null) {
                        getPopupMenu().show((Component) e.getSource(), e.getX(), e.getY());
                    }
                }
            }
        };

        addMouseListener(mouseAdapter);
        buttonPlay.addMouseListener(mouseAdapter);
        if (waveFormPanel != null) {
            waveFormPanel.addMouseListener(mouseAdapter);
        }
        buttonPlay.addActionListener(e -> {
            if (SoundManager.getInstance().checkSound(this)) {
                if (SoundManager.getInstance().isRunning()) {
                    SoundManager.getInstance().pause();
                } else {
                    SoundManager.getInstance().resumes();
                }
            } else {
                play();
            }
        });

        String duration = data.getData() == null ? MethodUtil.formatSize(data.getSize()) : (MethodUtil.convertSecondsToTime(data.getDuration()) + ", " + MethodUtil.formatSize(data.getSize()));
        JLabel lbDuration = new JLabel(duration);
        lbDuration.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:-1;" +
                "foreground:$Text.lowForeground;");

        add(buttonPlay, "span 1 2");
        if (waveFormPanel != null) {
            add(waveFormPanel, "wrap,height 30");
        } else {
            JLabel lbName = new JLabel(data.getOriginalName());
            add(lbName, "wrap,width 184!,height 30");
        }
        add(lbDuration);
    }

    private JPopupMenu getPopupMenu() {
        if (popupMenu == null) {
            popupMenu = ComponentUtil.createOpenAndSavePopup(data.getName(), data.getOriginalName());
        }
        return popupMenu;
    }

    private void play() {
        File file = StoreManager.getInstance().getFile(data.getName());
        if (file != null) {
            boolean isMusic = data.getData() == null;
            SoundManager.getInstance().play(file, isMusic, this, data.getOriginalName());
        } else {
            File savePath = StoreManager.getInstance().createFile(data.getName());
            ProgressChat.download(this, data.getName(), savePath);
        }
    }

    public void stopButton() {
        buttonPlay.setIcon(MethodUtil.createIcon("raven/messenger/icon/play.svg", 0.3f));
    }

    public void playButton() {
        buttonPlay.setIcon(MethodUtil.createIcon("raven/messenger/icon/pause.svg", 0.3f));
    }

    public void setProgress(float progress) {
        waveFormPanel.setProgress(progress);
    }

    public WaveFormPanel getWaveFormPanel() {
        return waveFormPanel;
    }

    @Override
    public void onDownload(float progress) {
        buttonPlay.setProgress(progress);
    }

    @Override
    public void onFinish(File file) {
        if (file != null) {
            stopButton();
        }
        buttonPlay.setProgress(0);
    }

    @Override
    public void onError(Exception e) {
        buttonPlay.setProgress(0);
    }
}
