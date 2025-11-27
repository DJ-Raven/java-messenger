package raven.messenger.plugin.sound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AudioUtil {

    public static WaveFormData getWaveFormData(File file) throws IOException, UnsupportedAudioFileException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        return generateWaveFormData(audioInputStream);
    }

    public static WaveFormData generateWaveFormData(AudioInputStream audioInputStream) throws IOException {
        AudioFormat format = audioInputStream.getFormat();
        byte[] audioBytes = new byte[(int) (audioInputStream.getFrameLength() * format.getFrameSize())];
        audioInputStream.read(audioBytes);
        audioInputStream.close();
        return getWaveFormData(audioBytes, format);
    }

    public static WaveFormData getWaveFormData(byte[] audioBytes) {
        return getWaveFormData(audioBytes, createDefaultAudioFormat());
    }

    public static WaveFormData getWaveFormData(byte[] audioBytes, AudioFormat format) {
        float[] audioData = null;
        if (format.getSampleSizeInBits() == 16) {
            int lengthInSamples = audioBytes.length / 2;
            audioData = new float[lengthInSamples];
            if (format.isBigEndian()) {
                for (int i = 0; i < lengthInSamples; i++) {
                    int MSB = audioBytes[2 * i];
                    int LSB = audioBytes[2 * i + 1];
                    audioData[i] = MSB << 8 | (255 & LSB);
                }
            } else {
                for (int i = 0; i < lengthInSamples; i++) {
                    int LSB = audioBytes[2 * i];
                    int MSB = audioBytes[2 * i + 1];
                    audioData[i] = MSB << 8 | (255 & LSB);
                }
            }
        } else if (format.getSampleSizeInBits() == 8) {
            int lengthInSamples = audioBytes.length;
            audioData = new float[lengthInSamples];
            if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
                for (int i = 0; i < audioBytes.length; i++) {
                    audioData[i] = audioBytes[i];
                }
            } else {
                for (int i = 0; i < audioBytes.length; i++) {
                    audioData[i] = audioBytes[i] - 128;
                }
            }
        }
        int audioBytesLength = audioBytes.length;
        int frameSize = format.getFrameSize();
        int channels = format.getChannels();
        int sampleSizeInBits = format.getSampleSizeInBits();
        WaveFormData waveFormData = createDefaultWaveFormData(null);
        int framesPerPixel = audioBytesLength / frameSize / waveFormData.getWidth();
        List<Float> data = new ArrayList<>();
        float max = 0;
        for (double i = 0; i < waveFormData.getWidth() && audioData != null; i++) {
            int idx = (int) (framesPerPixel * channels * i);
            float value;
            if (sampleSizeInBits == 8) {
                value = audioData[idx] / (waveFormData.getHeight() * 0.3f);
            } else {
                value = audioData[idx] / (waveFormData.getHeight() * 2.5f);
            }
            max = Math.max(max, value);
            if (i % (waveFormData.getLineSize() + waveFormData.getSpace()) == 0) {
                data.add(Math.abs(value));
            }
        }
        waveFormData.setData(data);
        return waveFormData;
    }

    public static AudioFormat createDefaultAudioFormat() {
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float rate = 44100.0f;
        int sampleSize = 16;
        int channels = 2;
        boolean bigEndian = true;
        return new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate, bigEndian);
    }

    public static WaveFormData createDefaultWaveFormData(List<Float> data) {
        int width = 184;
        int height = 30;
        int lineSize = 2;
        int space = 1;
        return new WaveFormData(width, height, lineSize, space, data);
    }

    public static File write(CaptureData data) throws IOException {
        AudioInputStream audioInputStream = data.createAudioInputStream();
        try {
            Path tempPath = Files.createTempFile("temp_", "_sound.wav");
            File output = tempPath.toFile();
            output.deleteOnExit();
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, output);
            return output;
        } finally {
            audioInputStream.close();
        }
    }
}