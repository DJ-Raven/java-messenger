package raven.messenger.models.response;

import org.json.JSONObject;
import raven.messenger.models.other.ModelImage;
import raven.messenger.models.other.ModelName;

public class ModelMember {

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public ModelName getName() {
        return name;
    }

    public void setName(ModelName name) {
        this.name = name;
    }

    public ModelImage getProfile() {
        return profile;
    }

    public void setProfile(ModelImage profile) {
        this.profile = profile;
    }

    public ModelMember(JSONObject json) {
        userId = json.getInt("user_id");
        userUuid = json.getString("user_uuid");
        name = new ModelName(json.getString("first_name"), json.getString("last_name"));
        if (!json.isNull("profile")) {
            profile = new ModelImage(json.getJSONObject("profile"));
        }
    }

    public ModelMember(ModelProfile profile) {
        this.userId = profile.getUserId();
        this.userUuid = profile.getUserUuid();
        this.name = profile.getName();
        this.profile = profile.getProfile();
    }

    private int userId;
    private String userUuid;
    private ModelName name;
    private ModelImage profile;
}
