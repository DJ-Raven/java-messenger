package raven.messenger.service;

import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import raven.messenger.api.exception.ResponseException;
import raven.messenger.api.request.RequestFileMonitor;
import raven.messenger.models.file.ModelFile;
import raven.messenger.models.response.ModelMessage;
import raven.messenger.models.file.ModelFileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServiceMessage {

    public synchronized List<ModelMessage> findMessageUser(int user, int page) throws ResponseException {
        Response response = RestAssured.given()
                .queryParam("user", user)
                .queryParam("page", page)
                .get("message/message");
        if (response.getStatusCode() == 200) {
            List<ModelMessage> list = new ArrayList<>();
            JSONArray data = new JSONArray(response.body().asString());
            for (int i = 0; i < data.length(); i++) {
                list.add(new ModelMessage(data.getJSONObject(i)));
            }
            return list;
        }
        throw new ResponseException(response.getStatusCode(), response.asString());
    }

    public ModelFile sendFile(File file, ModelFileInfo fileInfo) throws ResponseException {
        String name = file.getName();
        Response response = RestAssured.given()
                .contentType(ContentType.MULTIPART)
                .queryParam("type", fileInfo.getType().toString())
                .queryParam("info", fileInfo.toJsonString())
                .multiPart(new MultiPartSpecBuilder(file).fileName(name).build())
                .when().post("message/upload");
        if (response.getStatusCode() == 200) {
            return new ModelFile(new JSONObject(response.getBody().asString()));
        }
        throw new ResponseException(response.getStatusCode(), response.asString());
    }

    public synchronized void getFile(RequestFileMonitor req) throws ResponseException {
        String url = "message/download";
        Response response = RestAssured.given()
                .queryParam("filename", req.getFileName())
                .expect()
                .when()
                .get(url);
        if (response.getStatusCode() == 200) {
            long length = Long.parseLong(response.getHeaders().get("Content-Length").getValue());
            req.save(response.asInputStream(), length);
        } else {
            throw new ResponseException(response.getStatusCode(), response.asString());
        }
    }
}
