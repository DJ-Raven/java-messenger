package raven.messenger.plugin.sound;

import raven.messenger.plugin.sound.player.AbstractPlayer;
import raven.messenger.plugin.sound.player.PlayerEvent;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoundPlayback extends AbstractPlayer {

    private List<SoundPlaybackListener> events = new ArrayList<>();
    private AudioInputStream audioInputStream;
    private Clip clip;
    private int pausePosition;
    private boolean finished;

    public SoundPlayback() {
        init();
    }

    private void init() {
        try {
            clip = AudioSystem.getClip();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.START) {
                    runEventStart();
                    while (clip.isRunning()) {
                        sleep(1);
                        runEventChanged(clip.getFramePosition(), clip.getFrameLength());
                        fireLengthChanged(getEvent());
                    }
                } else if (event.getType() == LineEvent.Type.STOP) {
                    finished = clip.getFramePosition() >= clip.getFrameLength();
                    if (finished) {
                        runEventStop();
                        fireFinished(getEvent());
                    } else {
                        firePaused(getEvent());
                    }
                }
            });
        } catch (LineUnavailableException e) {
            System.err.println(e.getMessage());
        }
    }

    public void play(File file) throws IOException, UnsupportedAudioFileException {
        play(AudioSystem.getAudioInputStream(file));
    }

    public void play(AudioInputStream audioInputStream) {
        try {
            if (this.audioInputStream != null && this.audioInputStream != audioInputStream) {
                this.audioInputStream.close();
            }
            stopImpl(false);
            this.audioInputStream = audioInputStream;
            if (clip.isOpen()) {
                clip.close();
            }
            clip.open(audioInputStream);
            play();
        } catch (IOException | LineUnavailableException e) {
            System.err.println(e.getMessage());
        }
    }

    public void skip(float f) {
        if (clip != null) {
            int v = (int) (f * clip.getFrameLength());
            pausePosition = v;
            finished = false;
            clip.setFramePosition(v);
        }
        if (!isRunning()) {
            resumes();
        }
    }

    public boolean pause() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            pausePosition = clip.getFramePosition();
            return true;
        }
        return false;
    }

    public boolean resumes() {
        if (clip != null) {
            if (!clip.isRunning()) {
                if (finished) {
                    clip.setFramePosition(0);
                } else {
                    clip.setFramePosition(pausePosition);
                }
                clip.start();
                fireStarted(getEvent());
                return true;
            }
        }
        return false;
    }

    public boolean stop() {
        return stopImpl(true);
    }

    private boolean stopImpl(boolean notify) {
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
            pausePosition = 0;
            if (notify && clip.isOpen()) {
                fireFinished(getEvent());
            }
            return true;
        }
        return false;
    }

    public boolean isRunning() {
        return clip != null && clip.isRunning();
    }

    public void close() {
        if (audioInputStream != null) {
            try {
                clip.close();
                audioInputStream.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        events.clear();
        events = null;
    }

    private synchronized void play() {
        clip.start();
        fireStarted(getEvent());
    }

    private void sleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    private void runEventChanged(long position, long max) {
        for (SoundPlaybackListener event : events) {
            event.positionChanged(position, max);
        }
    }

    private void runEventStart() {
        for (SoundPlaybackListener event : events) {
            event.start();
        }
    }

    private void runEventStop() {
        for (SoundPlaybackListener event : events) {
            event.stop();
        }
    }

    public void addSoundPlaybackListener(SoundPlaybackListener event) {
        events.add(event);
    }

    private PlayerEvent getEvent() {
        AudioFormat format = audioInputStream.getFormat();
        int currentInSeconds = (int) (clip.getFramePosition() / format.getFrameRate());
        float currentInPercent = clip.getLongFramePosition() / (float) clip.getFrameLength();
        return new PlayerEvent(this, currentInSeconds, currentInPercent);
    }
}