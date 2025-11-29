package raven.messenger.plugin.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayInputStream;

public class CaptureData {

    public byte[] getAudioData() {
        return audioData;
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public double getDuration() {
        return duration;
    }

    private final byte[] audioData;
    private final AudioFormat audioFormat;
    private final double duration;

    protected CaptureData(byte[] audioData, AudioFormat audioFormat, double duration) {
        this.audioData = audioData;
        this.audioFormat = audioFormat;
        this.duration = duration;
    }

    public AudioInputStream createAudioInputStream() {
        int frameSizeInBytes = audioFormat.getFrameSize();
        ByteArrayInputStream byArr = new ByteArrayInputStream(audioData);
        return new AudioInputStream(byArr, audioFormat, audioData.length / frameSizeInBytes);
    }
}