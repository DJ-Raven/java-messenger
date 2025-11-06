package raven.messenger.api.exception;

import io.restassured.filter.FilterContext;
import io.restassured.filter.OrderedFilter;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import raven.messenger.api.ApiService;
import raven.messenger.login.Login;
import raven.messenger.manager.FormsManager;
import raven.messenger.util.ErrorReporter;

public class GlobalErrorHandler implements OrderedFilter {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Response filter(FilterableRequestSpecification request, FilterableResponseSpecification response, FilterContext context) {
        try {
            Response resp = context.next(request, response);
            int statusCode = resp.getStatusCode();

            // Log all non-200 responses for debugging
            if (statusCode != 200) {
                System.err.println("API Error - Status: " + statusCode +
                        " | URL: " + request.getURI() +
                        " | Method: " + request.getMethod());

                if (statusCode >= 400) {
                    String responseBody = resp.getBody().asString();
                    System.err.println("Response body: " + (responseBody.length() > 500 ?
                            responseBody.substring(0, 500) + "..." : responseBody));
                }
            }

            if (statusCode == 401) {
                handleUnauthorized();
            } else if (statusCode >= 500) {
                ErrorReporter.reportError("Server Error",
                        "Server returned error: " + statusCode + "\nURL: " + request.getURI(), false);
            }

            return resp;
        } catch (Exception e) {
            ErrorReporter.handleException("Error in API request filter", e, false);
            throw e;
        }
    }

    private void handleUnauthorized() {
        System.err.println("Authentication failed (401), redirecting to login...");
        ApiService.getInstance().closeAll();
        FormsManager.getInstance().showForm(new Login(null));
    }
}