package raven.messenger.manager;

import raven.messenger.component.SoundPlayerControl;
import raven.messenger.component.chat.item.ItemSound;
import raven.messenger.plugin.sound.SoundPlayback;
import raven.messenger.plugin.sound.SoundPlaybackListener;
import raven.messenger.plugin.sound.player.Mp3Player;
import raven.messenger.plugin.sound.player.PlayerEvent;
import raven.messenger.plugin.sound.player.PlayerListener;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class SoundManager {

    private static SoundManager instance;
    private SoundPlayerControl soundPlayerControl;
    private SoundPlayback soundPlayback;
    private Mp3Player mp3Player;
    private ItemSound itemSound;
    private boolean isMusic;
    private String soundName;

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
        mp3Player = new Mp3Player();
        PlayerListener playerListener = new PlayerListener() {
            @Override
            public void lengthChanged(PlayerEvent event) {
                soundPlayerControl.lengthChanged(event.getCurrentInPercent(), event.getCurrentInSeconds());
            }

            @Override
            public void started(PlayerEvent event) {
                if (itemSound != null) {
                    itemSound.playButton();
                    soundPlayerControl.playButton();
                    soundPlayerControl.setSoundName(soundName);
                    soundPlayerControl.setVisible(true);
                }
            }

            @Override
            public void paused(PlayerEvent event) {
                if (itemSound != null) {
                    itemSound.stopButton();
                    soundPlayerControl.stopButton();
                }
            }

            @Override
            public void finished(PlayerEvent event) {
                if (itemSound != null) {
                    itemSound.stopButton();
                    soundPlayerControl.stopButton();
                    soundPlayerControl.lengthChanged(0, 0);
                    soundPlayerControl.setVisible(false);
                }
            }
        };
        mp3Player.addPlayerListener(playerListener);
        soundPlayback.addPlayerListener(playerListener);
    }

    public void setSoundPlayerControl(SoundPlayerControl soundPlayerControl) {
        this.soundPlayerControl = soundPlayerControl;
    }

    public void play(File file, boolean isMusic, ItemSound itemSound, String soundName) {
        if (file.exists()) {
            try {
                this.soundName = soundName;
                stop();
                if (itemSound != null) {
                    this.itemSound = itemSound;
                }
                this.isMusic = isMusic;
                if (isMusic) {
                    mp3Player.play(file);
                } else {
                    soundPlayback.play(file);
                }
            } catch (UnsupportedAudioFileException | IOException e) {
                ErrorManager.getInstance().showError(e);
            }
        } else {
            if (itemSound != null) {
                this.itemSound = null;
            }
        }
    }

    public void stop() {
        if (isMusic) {
            mp3Player.stop();
        } else {
            soundPlayback.stop();
        }
        stopSoundPanel();
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

    public boolean isRunning() {
        if (isMusic) {
            return mp3Player.isPlaying();
        } else {
            return soundPlayback.isRunning();
        }
    }

    public void pause() {
        if (isMusic) {
            mp3Player.pause();
        } else {
            soundPlayback.pause();
        }
    }

    public void resumes() {
        if (isMusic) {
            mp3Player.resumes();
        } else {
            soundPlayback.resumes();
        }
    }

    public void skip(float f) {
        if (isMusic) {
            if (mp3Player != null) {
                mp3Player.skip(f);
            }
        } else {
            if (soundPlayback != null) {
                soundPlayback.skip(f);
            }
        }
    }
}
