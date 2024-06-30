package raven.messenger.manager;

import raven.messenger.component.chat.item.ItemSound;
import raven.messenger.plugin.sound.SoundPlayback;
import raven.messenger.plugin.sound.SoundPlaybackListener;
import raven.messenger.plugin.sound.player.Mp3Player;
import raven.messenger.plugin.sound.player.PlayerListener;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class SoundManager {

    private static SoundManager instance;
    private SoundPlayback soundPlayback;
    private Mp3Player mp3Player;
    private ItemSound itemSound;
    private boolean isMusic;

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
        mp3Player.addPlayerListener(new PlayerListener() {
            @Override
            public void started() {
                if (itemSound != null) {
                    itemSound.playButton();
                }
            }

            @Override
            public void paused() {
                if (itemSound != null) {
                    itemSound.stopButton();
                }
            }

            @Override
            public void finished() {
                if (itemSound != null) {
                    itemSound.stopButton();
                }
            }
        });
    }

    public void play(File file, boolean isMusic) {
        play(file, isMusic, null);
    }

    public void play(File file, boolean isMusic, ItemSound itemSound) {
        if (file.exists()) {
            try {
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

    private void stop() {
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
}
