package raven.messenger.service;

import io.restassured.RestAssured;
import io.restassured.http.Cookies;
import io.restassured.response.Response;

import raven.messenger.api.ApiService;
import raven.messenger.api.exception.ResponseException;
import raven.messenger.models.request.ModelRegister;

import java.net.ConnectException;

public class ServiceAuth {

    public synchronized Cookies login(String user, String password) throws ResponseException, ConnectException {
        Response response = RestAssured.given()
                .queryParam("user", user)
                .queryParam("password", password)
                .post("auth/login");
        if (response.getStatusCode() == 200) {
            Cookies cookies = response.getDetailedCookies();
            ApiService.getInstance().installConfig(cookies);
            return cookies;
        }
        throw new ResponseException(response.getStatusCode(), response.asString());
    }

    public synchronized String register(ModelRegister data) throws ResponseException, ConnectException {
        Response response = RestAssured.given()
                .body(data.toJsonObject().toString())
                .post("auth/register");
        if (response.getStatusCode() == 200) {
            return response.asString();
        }
        throw new ResponseException(response.getStatusCode(), response.asString());
    }
}
