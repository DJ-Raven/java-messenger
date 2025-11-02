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

public class Mp3Player extends AbstractPlayer {

    private File path;
    private AdvancedPlayer advancedPlayer;
    private InputStream inputStream;
    private Thread playThread;
    private Mp3File mp3File;
    private boolean isPlaying;
    private boolean isPause;
    private boolean skip;

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
        } else {
            play();
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
        fireStarted(getEvent());
    }

    private void finished(PlaybackEvent evt) {
        if (isPause) {
            currentFrame += (int) (evt.getFrame() * frameRatePerMilliseconds);
            if (!skip) {
                firePaused(getEvent());
            } else {
                skip = false;
            }
        } else {
            currentFrame = 0;
            fireFinished(getEvent());
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

    public Mp3File getMp3File() {
        return mp3File;
    }

    private PlayerEvent getEvent() {
        int currentInSeconds = (int) (currentFrame / frameRatePerMilliseconds + position) / 1000;
        float currentInPercent = getCurrentInPercent();
        return new PlayerEvent(this, currentInSeconds, currentInPercent);
    }

    private float getCurrentInPercent() {
        long lengthInMilliseconds = getMp3File().getLengthInMilliseconds();
        double current = currentFrame / frameRatePerMilliseconds / lengthInMilliseconds;
        double value = (double) position / lengthInMilliseconds;
        return Math.round((current + value) * 100f) / 100f;
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
                    fireLengthChanged(getEvent());
                    this.seconds = seconds;
                }
            }
        }
    }

    public static boolean isMp3File(String fileName) {
        return fileName.toLowerCase().endsWith(".mp3");
    }
}