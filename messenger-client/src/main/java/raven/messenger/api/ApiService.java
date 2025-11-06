package raven.messenger.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import raven.messenger.api.exception.GlobalErrorHandler;
import raven.messenger.socket.SocketService;
import raven.messenger.store.CookieManager;
import raven.messenger.util.ErrorReporter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiService {

    private static ApiService instance;

    public static final String API_VERSION = "6";
    public static final String IP = "http://localhost";
    // public static final String IP = "http://52.221.189.33";

    // Connection testing constants
    private static final int CONNECTION_TIMEOUT = 10;
    private static final int SOCKET_TIMEOUT = 30;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    public static ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    private ApiService() {
        // Configure timeouts
        RestAssured.config = RestAssured.config()
                .httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", CONNECTION_TIMEOUT * 1000)
                        .setParam("http.socket.timeout", SOCKET_TIMEOUT * 1000));
    }

    public boolean init() {
        try {
            Cookies cookies = getCookie();
            installConfig(cookies);

            // Test backend connection
            if (!testBackendConnection()) {
                ErrorReporter.reportError("Backend Connection Failed",
                        "Cannot connect to backend server at: " + IP, false);
                return false;
            }

            return cookies != null;
        } catch (Exception e) {
            ErrorReporter.handleException("Failed to initialize API service", e, false);
            return false;
        }
    }

    public void installConfig(Cookies cookies) {
        try {
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
        } catch (Exception e) {
            ErrorReporter.handleException("Failed to install API configuration", e, false);
        }
    }

    /**
     * Test backend connection with retry mechanism
     */
    public boolean testBackendConnection() {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                System.out.println("Testing backend connection (attempt " + attempt + "/" + MAX_RETRIES + ")...");

                Response response = RestAssured.given()
                        .baseUri(IP)
                        .port(5000)
                        .basePath("api")
                        .when()
                        .get("/health")  // You might want to create a health endpoint
                        .then()
                        .extract()
                        .response();

                if (response.getStatusCode() == 200) {
                    System.out.println("✓ Backend connection successful");
                    return true;
                } else {
                    System.out.println("✗ Backend responded with status: " + response.getStatusCode());
                }
            } catch (Exception e) {
                System.out.println("✗ Connection attempt " + attempt + " failed: " + e.getMessage());

                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Enhanced connection test with detailed diagnostics
     */
    public ConnectionTestResult testConnectionWithDiagnostics() {
        ConnectionTestResult result = new ConnectionTestResult();

        try {
            // Test basic connectivity
            long startTime = System.currentTimeMillis();
            Response response = RestAssured.given()
                    .when()
                    .get("/test")  // Replace with an actual test endpoint
                    .then()
                    .extract()
                    .response();
            long endTime = System.currentTimeMillis();

            result.responseTime = endTime - startTime;
            result.statusCode = response.getStatusCode();
            result.success = response.getStatusCode() == 200;
            result.message = "Connection test completed";

        } catch (Exception e) {
            result.success = false;
            result.message = "Connection failed: " + e.getMessage();
            result.exception = e;
        }

        return result;
    }

    public void closeAll() {
        try {
            CookieManager.getInstance().clearCookie();
            SocketService.getInstance().close();
            System.out.println("API service cleanup completed");
        } catch (Exception e) {
            ErrorReporter.handleException("Error during API service cleanup", e, false);
        }
    }

    public Cookies getCookie() {
        try {
            return CookieManager.getInstance().getCookie();
        } catch (IOException | ClassNotFoundException e) {
            ErrorReporter.handleException("Failed to retrieve cookies", e, false);
            return null;
        }
    }

    /**
     * Inner class for connection test results
     */
    public static class ConnectionTestResult {
        public boolean success;
        public String message;
        public long responseTime;
        public int statusCode;
        public Exception exception;

        @Override
        public String toString() {
            return String.format("Connection Test: %s | Status: %d | Response Time: %dms | Message: %s",
                    success ? "SUCCESS" : "FAILED", statusCode, responseTime, message);
        }
    }
}