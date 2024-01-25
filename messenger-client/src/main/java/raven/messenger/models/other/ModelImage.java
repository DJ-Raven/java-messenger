package raven.messenger.models.other;

import org.json.JSONObject;

public class ModelImage {

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

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

    public ModelImage(String image, String hash, int width, int height) {
        this.image = image;
        this.hash = hash;
        this.width = width;
        this.height = height;
    }

    public ModelImage(JSONObject json) {
        image = json.getString("image");
        hash = json.getString("hash");
        width = json.getInt("width");
        height = json.getInt("height");
    }

    private String image;
    private String hash;
    private int width;
    private int height;
}
