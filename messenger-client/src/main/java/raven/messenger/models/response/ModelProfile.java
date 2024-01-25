package raven.messenger.models.response;

import org.json.JSONObject;
import raven.messenger.models.other.ModelGender;
import raven.messenger.models.other.ModelImage;
import raven.messenger.models.other.ModelName;

public class ModelProfile {

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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ModelGender getGender() {
        return gender;
    }

    public void setGender(ModelGender gender) {
        this.gender = gender;
    }

    public ModelImage getProfile() {
        return profile;
    }

    public void setProfile(ModelImage profile) {
        this.profile = profile;
    }

    public ModelProfile(JSONObject json) {
        userId = json.getInt("user_id");
        userUuid = json.getString("user_uuid");
        name = new ModelName(json.getString("first_name"), json.getString("last_name"));
        bio = json.getString("bio");
        phoneNumber = json.getString("phone_number");
        gender = new ModelGender(json.getString("gender"));
        if (!json.isNull("profile")) {
            profile = new ModelImage(json.getJSONObject("profile"));
        }
    }

    private int userId;
    private String userUuid;
    private ModelName name;
    private String bio;
    private String phoneNumber;
    private ModelGender gender;
    private ModelImage profile;
}
