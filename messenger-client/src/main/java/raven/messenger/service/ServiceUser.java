package raven.messenger.service;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import raven.messenger.api.exception.ResponseException;
import raven.messenger.models.response.ModelChatListItem;
import raven.messenger.socket.ChatType;

import java.util.ArrayList;
import java.util.List;

public class ServiceUser {

    public synchronized List<ModelChatListItem> findAll(int page, String search) throws ResponseException {
        Response response = RestAssured.given()
                .queryParam("page", page)
                .queryParam("search", search)
                .get("user");
        if (response.getStatusCode() == 200) {
            List<ModelChatListItem> list = new ArrayList<>();
            JSONArray data = new JSONArray(response.getBody().asString());
            for (int i = 0; i < data.length(); i++) {
                list.add(new ModelChatListItem(data.getJSONObject(i)));
            }
            return list;
        }
        throw new ResponseException(response.getStatusCode(), response.asString());
    }

    public synchronized ModelChatListItem findById(ChatType chatType, int id) throws ResponseException {
        Response response = RestAssured.given()
                .queryParam("type", chatType.toString())
                .queryParam("id", id)
                .get("user/find");
        if (response.getStatusCode() == 200) {
            JSONObject json = new JSONObject(response.getBody().asString());
            return new ModelChatListItem(json);
        }
        throw new ResponseException(response.getStatusCode(), response.asString());
    }
}
