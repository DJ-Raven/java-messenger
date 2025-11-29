package raven.messenger.plugin.sound;

import java.util.List;

public class WaveFormData {

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getLineSize() {
        return lineSize;
    }

    public int getSpace() {
        return space;
    }

    public List<Float> getData() {
        return data;
    }

    public WaveFormData(int width, int height, int lineSize, int space, List<Float> data) {
        this.width = width;
        this.height = height;
        this.lineSize = lineSize;
        this.space = space;
        this.data = data;
    }

    protected void setData(List<Float> data) {
        this.data = data;
    }

    private final int width;
    private final int height;
    private final int lineSize;
    private final int space;
    private List<Float> data;
}