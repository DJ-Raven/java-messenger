package raven.messenger.service;

import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import raven.messenger.api.exception.ResponseException;
import raven.messenger.models.other.ModelGender;
import raven.messenger.models.other.ModelName;
import raven.messenger.models.response.ModelProfile;
import raven.messenger.models.other.ModelImage;

import java.io.File;

public class ServiceProfile {

    public synchronized ModelProfile getProfile() throws ResponseException {
        Response response = RestAssured.given()
                .get("profile");
        if (response.getStatusCode() == 200) {
            JSONObject data = new JSONObject(response.getBody().asString());
            return new ModelProfile(data);
        }
        throw new ResponseException(response.getStatusCode(), response.asString());
    }

    public synchronized ModelImage updateProfileImage(File file) throws ResponseException {
        String name = file.getName();
        Response response = RestAssured.given()
                .contentType(ContentType.MULTIPART)
                .multiPart(new MultiPartSpecBuilder(file).fileName(name).build())
                .when()
                .put("profile/image");
        if (response.getStatusCode() == 200) {
            return new ModelImage(new JSONObject(response.getBody().asString()));
        }
        throw new ResponseException(response.getStatusCode(), response.asString());
    }

    public synchronized void updateProfileUser(ModelName name) throws ResponseException {
        Response response = RestAssured.given()
                .body(name.toJsonObject().toString())
                .put("profile/user");
        if (response.getStatusCode() != 200) {
            throw new ResponseException(response.getStatusCode(), response.asString());
        }
    }

    public void updateProfileGender(ModelGender gender) throws ResponseException {
        Response response = RestAssured.given()
                .body(gender.toJsonObject().toString())
                .put("profile/gender");
        if (response.getStatusCode() != 200) {
            throw new ResponseException(response.getStatusCode(), response.asString());
        }
    }

    public void updateProfilePhoneNumber(String phoneNumber) throws ResponseException {
        Response response = RestAssured.given()
                .queryParam("phone_number", phoneNumber)
                .put("profile/phone");
        if (response.getStatusCode() != 200) {
            throw new ResponseException(response.getStatusCode(), response.asString());
        }
    }

    public void updateProfileBio(String bio) throws ResponseException {
        Response response = RestAssured.given()
                .queryParam("bio", bio)
                .put("profile/bio");
        if (response.getStatusCode() != 200) {
            throw new ResponseException(response.getStatusCode(), response.asString());
        }
    }
}
