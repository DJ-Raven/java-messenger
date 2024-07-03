package raven.messenger.component.chat.model;

import raven.messenger.event.FileNameEvent;
import raven.messenger.models.file.FileType;
import raven.messenger.models.file.ModelFile;
import raven.messenger.models.file.ModelFileVoiceInfo;

import java.io.File;
import java.util.List;

public class ChatSoundData extends FileNameEvent {

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        runEvent(name);
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public List<Float> getData() {
        return data;
    }

    public void setData(List<Float> data) {
        this.data = data;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public ChatSoundData(File file) {
        this.originalName = file.getName();
        this.size = file.length();
    }

    public ChatSoundData(List<Float> data, String name, int size, double duration) {
        this.data = data;
        this.name = name;
        this.size = size;
        this.duration = duration;
    }

    public ChatSoundData(ModelFile file) {
        this.name = file.getName();
        this.originalName = file.getOriginalName();
        this.size = file.getSize();
        if (file.getType() == FileType.VOICE) {
            ModelFileVoiceInfo info = file.getVoidInfo();
            this.data = info.getWaveData();
            this.duration = info.getDuration();
        }
    }

    private String name;
    private String originalName;
    private long size;
    private List<Float> data;
    private double duration;
}
