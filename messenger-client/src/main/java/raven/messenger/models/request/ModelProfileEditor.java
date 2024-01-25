package raven.messenger.models.request;

import org.json.JSONObject;
import raven.messenger.models.other.JsonModel;
import raven.messenger.models.other.ModelGender;

public class ModelProfileEditor implements JsonModel {

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public ModelProfileEditor(String firstName, String lastName, String bio, String phoneNumber, ModelGender gender, String profile) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.profile = profile;
    }

    private String firstName;
    private String lastName;
    private String bio;
    private String phoneNumber;
    private ModelGender gender;
    private String profile;

    @Override
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("first_name", firstName);
        json.put("last_name", lastName);
        json.put("bio", bio);
        json.put("phone_number", phoneNumber);
        json.put("gender", gender.getGender());
        return json;
    }
}
