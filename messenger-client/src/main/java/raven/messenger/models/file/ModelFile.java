package raven.messenger.models.file;

import org.json.JSONObject;

public class ModelFile {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public ModelFileInfo getInfo() {
        return info;
    }

    public void setInfo(ModelFileInfo info) {
        this.info = info;
    }

    public ModelFile(int id, String name, String originalName, int size, FileType type, ModelFileInfo info) {
        this.id = id;
        this.name = name;
        this.originalName = originalName;
        this.size = size;
        this.type = type;
        this.info = info;
    }

    public ModelFile(JSONObject json) {
        id = json.getInt("id");
        name = json.getString("name");
        originalName = json.getString("original_name");
        size = json.getInt("size");
        type = FileType.toFileType(json.getString("type"));
        if (type == FileType.VOICE) {
            info = new ModelFileVoiceInfo(json.getJSONObject("info"));
        } else if (type == FileType.PHOTO) {
            info = new ModelFilePhotoInfo(json.getJSONObject("info"));
        }
    }

    private int id;
    private String name;
    private String originalName;
    private int size;
    private FileType type;
    private ModelFileInfo info;

    public ModelFileVoiceInfo getVoidInfo() {
        return (ModelFileVoiceInfo) info;
    }

    public ModelFilePhotoInfo getPhotoInfo() {
        return (ModelFilePhotoInfo) info;
    }
}
