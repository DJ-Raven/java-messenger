package raven.messenger.plugin.sound;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoundCapture {

    private List<SoundCaptureListener> events = new ArrayList<>();
    private AudioFormat audioFormat;
    private byte[] audioData;
    private Thread thread;
    private boolean run;
    private double duration;

    public void addSoundCaptureListener(SoundCaptureListener event) {
        events.add(event);
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public byte[] getDataAudioBytes() {
        return audioData;
    }

    public void start() {
        thread = new Thread(() -> run());
        thread.start();
    }

    public CaptureData stop() {
        run = false;
        waitThread();
        return new CaptureData(audioData, audioFormat, duration);
    }

    private void waitThread() {
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
    }

    private void run() {
        if (audioFormat == null) {
            audioFormat = AudioUtil.createDefaultAudioFormat();
        }
        AudioFormat format = audioFormat;
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            throw new RuntimeException("Line matching " + info + " not supported.");
        }
        TargetDataLine line;
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format, line.getBufferSize());
        } catch (LineUnavailableException e) {
            throw new RuntimeException("Unable to open the line " + e);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int frameSizeInBytes = format.getFrameSize();
        int bufferLengthInFrames = line.getBufferSize() / 8;
        int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
        byte[] data = new byte[bufferLengthInBytes];
        int numBytesRead;
        line.start();
        runEventStart();
        run = true;
        initEvent(line);
        while (run) {
            if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
                break;
            }
            out.write(data, 0, numBytesRead);
        }
        runEventStop();
        line.stop();
        line.close();
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        byte audioBytes[] = out.toByteArray();
        audioData = audioBytes;
    }

    private void initEvent(TargetDataLine line) {
        new Thread(new Runnable() {
            private long milliseconds;

            @Override
            public void run() {
                while (line.isActive()) {
                    sleep(5);
                    long milliseconds = line.getMicrosecondPosition() / 1000;
                    if (this.milliseconds != milliseconds) {
                        this.milliseconds = milliseconds;
                        duration = milliseconds / 1000f;
                        runEventCapturing(milliseconds);
                    }
                }
            }
        }).start();
    }

    private void sleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void runEventStart() {
        for (SoundCaptureListener event : events) {
            event.start();
        }
    }

    public void runEventStop() {
        for (SoundCaptureListener event : events) {
            event.stop();
        }
    }

    public void runEventCapturing(long millisecond) {
        for (SoundCaptureListener event : events) {
            event.capturing(millisecond);
        }
    }
}