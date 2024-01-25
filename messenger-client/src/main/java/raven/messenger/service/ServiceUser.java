package raven.messenger.service;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import raven.messenger.api.exception.ResponseException;
import raven.messenger.models.response.ModelUser;

import java.util.ArrayList;
import java.util.List;

public class ServiceUser {

    public synchronized List<ModelUser> findAll(int page, String search) throws ResponseException {
        Response response = RestAssured.given()
                .queryParam("page", page)
                .queryParam("search", search)
                .get("user");
        if (response.getStatusCode() == 200) {
            List<ModelUser> list = new ArrayList<>();
            JSONArray data = new JSONArray(response.getBody().asString());
            for (int i = 0; i < data.length(); i++) {
                list.add(new ModelUser(data.getJSONObject(i)));
            }
            return list;
        }
        throw new ResponseException(response.getStatusCode(), response.asString());
    }

    public synchronized ModelUser findById(int id) throws ResponseException {
        Response response = RestAssured.given()
                .get("user/" + id);
        if (response.getStatusCode() == 200) {
            JSONObject json = new JSONObject(response.getBody().asString());
            return new ModelUser(json);
        }
        throw new ResponseException(response.getStatusCode(), response.asString());
    }
}
