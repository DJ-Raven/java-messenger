package raven.messenger.models.file;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ModelFileVoiceInfo implements ModelFileInfo {

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public List<Float> getWaveData() {
        return waveData;
    }

    public void setWaveData(List<Float> waveData) {
        this.waveData = waveData;
    }

    public ModelFileVoiceInfo(double duration, List<Float> waveData) {
        this.duration = duration;
        this.waveData = waveData;
    }

    public ModelFileVoiceInfo(JSONObject json) {
        duration = json.getDouble("duration");
        waveData = toList(json.getJSONArray("wave_data"));
    }

    private double duration;
    private List<Float> waveData;

    private List<Float> toList(JSONArray data) {
        List<Float> list = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            list.add(data.getFloat(i));
        }
        return list;
    }

    private JSONArray toJson(List<Float> data) {
        JSONArray list = new JSONArray();
        for (Float f : data) {
            list.put(f);
        }
        return list;
    }

    @Override
    public String toJsonString() {
        JSONObject json = new JSONObject();
        json.put("duration", duration);
        json.put("wave_data", toJson(waveData));
        return json.toString();
    }

    @Override
    public FileType getType() {
        return FileType.VOICE;
    }
}
