package raven.messenger.models.response;

import org.json.JSONObject;
import raven.messenger.models.other.ModelGender;
import raven.messenger.models.other.ModelImage;
import raven.messenger.models.other.ModelName;
import raven.messenger.util.MethodUtil;

import java.util.Date;

public class ModelUserInfo {

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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public ModelImage getProfile() {
        return profile;
    }

    public void setProfile(ModelImage profile) {
        this.profile = profile;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public ModelUserInfo(JSONObject json) {
        userId = json.getInt("user_id");
        userUuid = json.getString("user_uuid");
        name = new ModelName(json.getString("first_name"), json.getString("last_name"));
        gender = new ModelGender(json.getString("gender"));
        bio = json.getString("bio");
        if (!json.isNull("profile")) {
            profile = new ModelImage(json.getJSONObject("profile"));
        }
        createDate = MethodUtil.stringToDate(json.getString("create_date"));
    }

    private int userId;
    private String userUuid;
    private ModelName name;
    private ModelGender gender;
    private String bio;
    private ModelImage profile;
    private Date createDate;
}
