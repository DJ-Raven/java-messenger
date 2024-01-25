package raven.messenger.component.chat.item;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.component.ButtonProgress;
import raven.messenger.component.chat.model.ChatVoiceData;
import raven.messenger.manager.SoundManager;
import raven.messenger.plugin.sound.AudioUtil;
import raven.messenger.plugin.sound.WaveFormListener;
import raven.messenger.plugin.sound.WaveFormPanel;
import raven.messenger.store.StoreManager;
import raven.messenger.util.MethodUtil;

import javax.swing.*;
import java.io.File;

public class ItemSound extends JPanel implements ProgressChat {

    private ChatVoiceData data;
    private int type;
    private WaveFormPanel waveFormPanel;
    private ButtonProgress buttonPlay;

    public ItemSound(ChatVoiceData data, int type) {
        this.data = data;
        this.type = type;
        init();
    }

    private void init() {
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
        String backgroundKey = type == 1 ? "$Chat.item.background" : "$Chat.item.myselfBackground";
        String foregroundKey = type == 1 ? "$Chat.item.recipientVoiceForeground" : "$Chat.item.myselfVoiceForeground";

        setLayout(new MigLayout("insets 3 5 3 5,gapy 0", "[]10[" + waveFormPanel.getWaveFormData().getWidth() + ",fill]", ""));
        putClientProperty(FlatClientProperties.STYLE, "" +
                "background:" + backgroundKey);
        waveFormPanel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;" +
                "foreground:" + foregroundKey);
        String icon = StoreManager.getInstance().getFile(data.getName()) != null ? "play.svg" : "download.svg";
        buttonPlay = new ButtonProgress(MethodUtil.createIcon("raven/messenger/icon/" + icon, 0.8f));
        buttonPlay.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:999;" +
                "margin:7,7,7,7;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");
        buttonPlay.addActionListener(e -> {
            if (SoundManager.getInstance().checkSound(this)) {
                if (SoundManager.getInstance().getSoundPlayback().isRunning()) {
                    SoundManager.getInstance().getSoundPlayback().pause();
                } else {
                    SoundManager.getInstance().getSoundPlayback().resumes();
                }
            } else {
                play();
            }
        });
        JLabel lbDuration = new JLabel(MethodUtil.convertSecondsToTime(data.getDuration()) + ", " + MethodUtil.formatSize(data.getSize()));
        lbDuration.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:-1;" +
                "foreground:$Text.lowForeground");

        add(buttonPlay, "span 1 2");
        add(waveFormPanel, "wrap,height " + waveFormPanel.getWaveFormData().getHeight());
        add(lbDuration);
    }

    private void play() {
        File file = StoreManager.getInstance().getFile(data.getName());
        if (file != null) {
            SoundManager.getInstance().play(file, this);
        } else {
            File savePath = StoreManager.getInstance().createFile(data.getName());
            ProgressChat.download(this, data.getName(), savePath);
        }
    }

    public void stopButton() {
        buttonPlay.setIcon(MethodUtil.createIcon("raven/messenger/icon/play.svg", 0.8f));
    }

    public void playButton() {
        buttonPlay.setIcon(MethodUtil.createIcon("raven/messenger/icon/pause.svg", 0.8f));
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
