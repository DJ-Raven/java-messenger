package raven.messenger.service;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import raven.messenger.api.exception.ResponseException;
import raven.messenger.models.request.ModelCreateGroup;
import raven.messenger.models.response.ModelGroup;
import raven.messenger.models.response.ModelMember;

import java.util.ArrayList;
import java.util.List;

public class ServiceGroup {

    public synchronized int create(ModelCreateGroup data) throws ResponseException {
        Response response;
        if (data.getFile() == null) {
            response = RestAssured.given()
                    .queryParam("name", data.getName())
                    .queryParam("description", data.getDescription())
                    .post("group/create");
        } else {
            response = RestAssured.given()
                    .contentType(ContentType.MULTIPART)
                    .multiPart(data.getFile())
                    .queryParam("name", data.getName())
                    .queryParam("description", data.getDescription())
                    .post("group/create");
        }
        if (response.getStatusCode() == 200) {
            JSONObject json = new JSONObject(response.getBody().asString());
            return json.getInt("id");
        }
        throw new ResponseException(response.getStatusCode(), response.asString());
    }

    public synchronized ModelGroup checkGroup(String uuid) throws ResponseException {
        String url = "group/check";
        Response response = RestAssured.given()
                .queryParam("group", uuid)
                .get(url);
        if (response.getStatusCode() == 200) {
            return new ModelGroup(new JSONObject(response.getBody().asString()));
        } else {
            throw new ResponseException(response.getStatusCode(), response.asString());
        }
    }

    public synchronized ModelGroup joinGroup(int id) throws ResponseException {
        String url = "group/join";
        Response response = RestAssured.given()
                .queryParam("group", id)
                .post(url);
        if (response.getStatusCode() == 200) {
            return new ModelGroup(new JSONObject(response.getBody().asString()));
        } else {
            throw new ResponseException(response.getStatusCode(), response.asString());
        }
    }

    public synchronized List<ModelMember> getGroupMember(int group, int page) throws ResponseException {
        Response response = RestAssured.given()
                .queryParam("group", group)
                .queryParam("page", page)
                .get("group/member");
        if (response.getStatusCode() == 200) {
            List<ModelMember> list = new ArrayList<>();
            JSONArray data = new JSONArray(response.getBody().asString());
            for (int i = 0; i < data.length(); i++) {
                list.add(new ModelMember(data.getJSONObject(i)));
            }
            return list;
        }
        throw new ResponseException(response.getStatusCode(), response.asString());
    }
}
