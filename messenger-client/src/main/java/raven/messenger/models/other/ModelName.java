package raven.messenger.models.other;

import org.json.JSONObject;

public class ModelName implements JsonModel {

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

    public ModelName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public ModelName(JSONObject json) {
        this.firstName = json.getString("first_name");
        this.lastName = json.getString("last_name");
    }

    private String firstName;
    private String lastName;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getProfileString() {
        return (String.valueOf(firstName.charAt(0)) + String.valueOf(lastName.charAt(0))).toUpperCase();
    }

    public boolean isSame(ModelName name) {
        return name.getFirstName().equals(firstName) && name.getLastName().equals(lastName);
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("first_name", firstName);
        json.put("last_name", lastName);
        return json;
    }
}
