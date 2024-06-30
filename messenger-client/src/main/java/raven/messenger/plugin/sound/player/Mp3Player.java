package raven.messenger.plugin.sound.player;

import com.mpatric.mp3agic.Mp3File;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Mp3Player {

    private File path;
    private AdvancedPlayer advancedPlayer;
    private InputStream inputStream;
    private double frameRatePerMilliseconds;
    private Thread playThread;
    private int currentFrame;
    private Mp3File mp3File;
    private boolean isPlaying;
    private boolean isPause;
    private boolean skip;
    private List<PlayerListener> listeners;

    public Mp3Player() {
    }

    private void init() {
        try {
            mp3File = new Mp3File(path);
            frameRatePerMilliseconds = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void play(File path) {
        this.path = path;
        init();
        stop();
        currentFrame = 0;
        playImpl();
    }

    private void playImpl() {
        if (advancedPlayer == null) {
            try {
                inputStream = new FileInputStream(path);
                advancedPlayer = new AdvancedPlayer(inputStream, new PlayerAudioDevice());
                advancedPlayer.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackStarted(PlaybackEvent evt) {
                        started(evt);
                    }

                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        finished(evt);
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        playThread = new Thread(() -> {
            try {
                if (isPause) {
                    advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                } else {
                    advancedPlayer.play();
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        playThread.start();
    }

    public void skip(float f) {
        double length = f * (mp3File.getLengthInMilliseconds());
        length *= frameRatePerMilliseconds;
        skip((int) length);
    }

    public void skip(int length) {
        skip = true;
        pause();
        currentFrame = length;
        playImpl();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void pause() {
        if (isPlaying) {
            isPause = true;
            advancedPlayer.stop();
            close();
        }
    }

    public boolean isPause() {
        return isPause;
    }

    public void resumes() {
        if (isPause) {
            playImpl();
        }
    }

    public void stop() {
        if (advancedPlayer != null) {
            advancedPlayer.stop();
        }
        isPause = false;
        close();
    }

    private void started(PlaybackEvent evt) {
        isPlaying = true;
        isPause = false;
        started();
    }

    private void finished(PlaybackEvent evt) {
        if (isPause) {
            currentFrame += (int) (evt.getFrame() * frameRatePerMilliseconds);
            if (!skip) {
                paused();
            } else {
                skip = false;
            }
        } else {
            currentFrame = 0;
            finished();
            close();
        }
        isPlaying = false;
    }

    private void close() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("error while close");
        } finally {
            if (advancedPlayer != null) {
                advancedPlayer.close();
                advancedPlayer = null;
            }
        }
    }

    public void addPlayerListener(PlayerListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    public void removePlayerListener(PlayerListener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public void clearPlayerListener() {
        if (listeners != null) {
            listeners.clear();
        }
    }

    public Mp3File getMp3File() {
        return mp3File;
    }

    private void progressChanged(float f) {
        if (listeners != null) {
            for (PlayerListener l : listeners) {
                l.progressChanged(f);
            }
        }
    }

    private void started() {
        if (listeners != null) {
            for (PlayerListener l : listeners) {
                l.started();
            }
        }
    }

    private void finished() {
        if (listeners != null) {
            for (PlayerListener l : listeners) {
                l.finished();
            }
        }
    }

    private void paused() {
        if (listeners != null) {
            for (PlayerListener l : listeners) {
                l.paused();
            }
        }
    }

    private class PlayerAudioDevice extends JavaSoundAudioDevice {

        private float currentPercent = -1;

        @Override
        protected void writeImpl(short[] samples, int offs, int len) throws JavaLayerException {
            super.writeImpl(samples, offs, len);
            if (!isPause) {
                double current = currentFrame / frameRatePerMilliseconds / mp3File.getLengthInMilliseconds();
                double value = (double) getPosition() / mp3File.getLengthInMilliseconds();
                // to get two decimal places
                float percent = Math.round((current + value) * 100f) / 100f;
                if (currentPercent != percent) {
                    progressChanged(percent);
                    currentPercent = percent;
                }
            }
        }
    }
}