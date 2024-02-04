package raven.messenger.models.request;

import org.json.JSONObject;
import raven.messenger.models.other.JsonModel;

import java.io.File;

public class ModelCreateGroup implements JsonModel {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ModelCreateGroup(String name, String description, File file) {
        this.name = name;
        this.description = description;
        this.file = file;
    }

    private String name;
    private String description;
    private File file;

    @Override
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("description", description);
        return json;
    }
}
