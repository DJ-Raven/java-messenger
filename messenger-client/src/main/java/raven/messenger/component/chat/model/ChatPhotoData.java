package raven.messenger.component.chat.model;

import raven.messenger.models.file.ModelFile;
import raven.messenger.models.file.ModelFilePhotoInfo;
import raven.messenger.store.StoreManager;

import java.io.File;

public class ChatPhotoData {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public ChatPhotoData(ModelFile file) {
        this.name = file.getName();
        ModelFilePhotoInfo info = file.getPhotoInfo();
        this.width = info.getWidth();
        this.height = info.getHeight();
        this.hash = info.getHash();
        File f = StoreManager.getInstance().getFile(name);
        if (f != null) {
            path = f.getAbsolutePath();
        }
    }

    public ChatPhotoData(String path) {
        this.path = path;
    }

    private String name;
    private String path;
    private String hash;
    private int width;
    private int height;
}
