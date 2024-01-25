package raven.messenger.models.request;

import org.json.JSONObject;
import raven.messenger.models.other.JsonModel;
import raven.messenger.models.other.ModelGender;

public class ModelRegister implements JsonModel {

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

    public ModelGender getGender() {
        return gender;
    }

    public void setGender(ModelGender gender) {
        this.gender = gender;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ModelRegister(String firstName, String lastName, ModelGender gender, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.userName = userName;
        this.password = password;
    }

    private String firstName;
    private String lastName;
    private ModelGender gender;
    private String userName;
    private String password;

    @Override
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("first_name", firstName);
        json.put("last_name", lastName);
        json.put("gender", gender.getGender());
        json.put("user_name", userName);
        json.put("password", password);
        return json;
    }
}
