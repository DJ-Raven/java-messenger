package raven.messenger.plugin.sound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoundPlayback {

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
                    }
                } else if (event.getType() == LineEvent.Type.STOP) {
                    runEventStop();
                    finished = clip.getFramePosition() >= clip.getFrameLength();
                }
            });
        } catch (LineUnavailableException e) {
            System.err.println(e);
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
            stop();
            this.audioInputStream = audioInputStream;
            clip.open(audioInputStream);
            play();
        } catch (IOException | LineUnavailableException e) {
            System.err.println(e);
        }
    }

    public void skip(float f) {
        if (clip != null) {
            int v = (int) (f * clip.getFrameLength());
            pausePosition = v;
            finished = false;
            clip.setFramePosition(v);
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
                return true;
            }
        }
        return false;
    }

    public boolean stop() {
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
            clip.close();
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
                System.err.println(e);
            }
        }
        events.clear();
        events = null;
    }

    private synchronized void play() {
        clip.start();
    }

    private void sleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
}