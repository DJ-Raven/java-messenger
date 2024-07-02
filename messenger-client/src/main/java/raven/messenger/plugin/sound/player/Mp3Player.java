package raven.messenger.plugin.sound.player;

import com.mpatric.mp3agic.Mp3File;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import javax.swing.*;
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
    private Thread playThread;
    private Mp3File mp3File;
    private boolean isPlaying;
    private boolean isPause;
    private boolean skip;
    private List<PlayerListener> listeners;

    protected double frameRatePerMilliseconds;
    protected int currentFrame;
    protected int position;

    public Mp3Player() {
    }

    public Mp3Player(File path) {
        this.path = path;
        init();
    }

    private void init() {
        try {
            mp3File = new Mp3File(path);
            frameRatePerMilliseconds = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void prepare(File path) {
        this.path = path;
        init();
    }

    public void play() {
        play(null);
    }

    public void play(File path) {
        if (path != null) {
            this.path = path;
            init();
        }
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
                if (currentFrame > 0) {
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

    public boolean isPlayable() {
        return path != null;
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
        started(getEvent());
    }

    private void finished(PlaybackEvent evt) {
        if (isPause) {
            currentFrame += (int) (evt.getFrame() * frameRatePerMilliseconds);
            if (!skip) {
                paused(getEvent());
            } else {
                skip = false;
            }
        } else {
            currentFrame = 0;
            finished(getEvent());
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

    private PlayerEvent getEvent() {
        return new PlayerEvent(this);
    }

    private void lengthChanged(PlayerEvent event) {
        if (listeners != null) {
            for (PlayerListener l : listeners) {
                l.lengthChanged(event);
            }
        }
    }

    private void started(PlayerEvent event) {
        if (listeners != null) {
            for (PlayerListener l : listeners) {
                l.started(event);
            }
        }
    }

    private void finished(PlayerEvent event) {
        if (listeners != null) {
            for (PlayerListener l : listeners) {
                l.finished(event);
            }
        }
    }

    private void paused(PlayerEvent event) {
        if (listeners != null) {
            for (PlayerListener l : listeners) {
                l.paused(event);
            }
        }
    }

    private class PlayerAudioDevice extends JavaSoundAudioDevice {

        private int seconds = -1;

        @Override
        protected void writeImpl(short[] samples, int offs, int len) throws JavaLayerException {
            super.writeImpl(samples, offs, len);
            position = getPosition();
            if (!isPause) {
                int seconds = (int) (currentFrame / frameRatePerMilliseconds + getPosition()) / 1000;
                if (this.seconds != seconds) {
                    lengthChanged(getEvent());
                    this.seconds = seconds;
                }
            }
        }
    }
}