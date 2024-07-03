package raven.messenger.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import raven.messenger.api.exception.GlobalErrorHandler;
import raven.messenger.socket.SocketService;
import raven.messenger.store.CookieManager;

import java.io.IOException;

public class ApiService {

    private static ApiService instance;

    public static final String API_VERSION = "6";

    public static final String IP = "http://localhost";

    // Enable this for connect to online server
    // public static final String IP = "http://52.221.189.33";

    public static ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    private ApiService() {
    }

    public boolean init() {
        Cookies cookies = getCookie();
        installConfig(cookies);
        return cookies != null;
    }

    public void installConfig(Cookies cookies) {
        RestAssured.reset();
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(IP)
                .addHeader("VERSION", API_VERSION)
                .setBasePath("api")
                .setPort(5000)
                .setAuth(RestAssured.preemptive().basic("user", "raven-messenger-server"))
                .addFilter(new GlobalErrorHandler())
                .build();
        if (cookies != null) {
            CookieManager.getInstance().setCookieString(cookies.toString());
            RestAssured.requestSpecification.cookies(cookies);
        }
    }

    public void closeAll() {
        CookieManager.getInstance().clearCookie();
        SocketService.getInstance().close();
    }

    public Cookies getCookie() {
        try {
            return CookieManager.getInstance().getCookie();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
