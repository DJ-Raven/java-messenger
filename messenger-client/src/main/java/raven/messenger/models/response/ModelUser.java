package raven.messenger.models.response;

import org.json.JSONObject;
import raven.messenger.models.other.ModelGender;
import raven.messenger.models.other.ModelImage;
import raven.messenger.models.other.ModelName;

public class ModelUser {

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

    public ModelGender getGender() {
        return gender;
    }

    public void setGender(ModelGender gender) {
        this.gender = gender;
    }

    public boolean isActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public ModelImage getProfile() {
        return profile;
    }

    public void setProfile(ModelImage profile) {
        this.profile = profile;
    }

    public ModelLastMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(ModelLastMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public ModelUser(JSONObject json) {
        userId = json.getInt("user_id");
        userUuid = json.getString("user_uuid");
        name = new ModelName(json.getString("first_name"), json.getString("last_name"));
        gender = new ModelGender(json.getString("gender"));
        activeStatus = json.getBoolean("active");
        if (!json.isNull("profile")) {
            profile = new ModelImage(json.getJSONObject("profile"));
        }
        if (json.has("last_message")) {
            lastMessage = new ModelLastMessage(json.getJSONObject("last_message"));
        }
    }

    private int userId;
    private String userUuid;
    private ModelName name;
    private ModelGender gender;
    private boolean activeStatus;
    private ModelImage profile;
    private ModelLastMessage lastMessage;
}
