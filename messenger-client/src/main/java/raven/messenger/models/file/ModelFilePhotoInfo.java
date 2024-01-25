package raven.messenger.models.file;

import org.json.JSONObject;

public class ModelFilePhotoInfo implements ModelFileInfo {

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public ModelFilePhotoInfo() {
    }

    public ModelFilePhotoInfo(JSONObject json) {
        width = json.getInt("width");
        height = json.getInt("height");
        hash = json.getString("hash");
    }

    private int width;
    private int height;
    private String hash;

    @Override
    public String toJsonString() {
        return null;
    }

    @Override
    public FileType getType() {
        return FileType.PHOTO;
    }
}
