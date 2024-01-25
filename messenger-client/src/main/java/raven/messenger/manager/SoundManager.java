package raven.messenger.manager;

import raven.messenger.component.chat.item.ItemSound;
import raven.messenger.plugin.sound.SoundPlayback;
import raven.messenger.plugin.sound.SoundPlaybackListener;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class SoundManager {

    private static SoundManager instance;
    private SoundPlayback soundPlayback;
    private ItemSound itemSound;

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private SoundManager() {
        soundPlayback = new SoundPlayback();
        soundPlayback.addSoundPlaybackListener(new SoundPlaybackListener() {
            @Override
            public void positionChanged(long position, long length) {
                if (itemSound != null) {
                    itemSound.setProgress(position / (float) length);
                }
            }

            @Override
            public void start() {
                if (itemSound != null) {
                    itemSound.playButton();
                }
            }

            @Override
            public void stop() {
                if (itemSound != null) {
                    itemSound.stopButton();
                }
            }
        });
    }

    public void play(File file) {
        play(file, null);
    }

    public void play(File file, ItemSound itemSound) {
        if (file.exists()) {
            try {
                if (itemSound != null) {
                    stopSoundPanel();
                    this.itemSound = itemSound;
                }
                soundPlayback.play(file);
            } catch (UnsupportedAudioFileException | IOException e) {
                System.err.println(e);
            }
        } else {
            if (itemSound != null) {
                this.itemSound = null;
            }
        }
    }

    public void stopSoundPanel() {
        if (itemSound != null) {
            itemSound.stopButton();
        }
    }

    public boolean checkSound(ItemSound itemSound) {
        return this.itemSound == itemSound;
    }

    public SoundPlayback getSoundPlayback() {
        return soundPlayback;
    }
}
