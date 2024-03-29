package raven.messenger.component.chat.model;

import raven.messenger.models.file.ModelFile;
import raven.messenger.models.file.ModelFileVoiceInfo;

import java.util.List;

public class ChatVoiceData {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
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

    public ChatVoiceData(List<Float> data, String name, int size, double duration) {
        this.data = data;
        this.name = name;
        this.size = size;
        this.duration = duration;
    }

    public ChatVoiceData(ModelFile file) {
        this.name = file.getName();
        this.size = file.getSize();
        ModelFileVoiceInfo info = file.getVoidInfo();
        this.data = info.getWaveData();
        this.duration = info.getDuration();
    }

    private String name;
    private int size;
    private List<Float> data;
    private double duration;
}
