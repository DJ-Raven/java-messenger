package raven.messenger.models.other;

import org.json.JSONObject;

public class ModelGender implements JsonModel {

    public String getGender() {
        return gender;
    }

    public ModelGender(String gender) {
        this.gender = gender.toUpperCase();
    }

    private final String gender;

    public boolean isMale() {
        return gender.equals("M");
    }

    @Override
    public String toString() {
        return gender.equals("M") ? "Male" : "Female";
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("gender", gender.toUpperCase());
        return json;
    }
}
