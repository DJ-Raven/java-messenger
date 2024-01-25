package raven.messenger.api.exception;

import io.restassured.filter.FilterContext;
import io.restassured.filter.OrderedFilter;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import raven.messenger.api.ApiService;
import raven.messenger.login.Login;
import raven.messenger.manager.FormsManager;

public class GlobalErrorHandler implements OrderedFilter {
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Response filter(FilterableRequestSpecification filterableRequestSpecification, FilterableResponseSpecification filterableResponseSpecification, FilterContext filterContext) {
        Response response = filterContext.next(filterableRequestSpecification, filterableResponseSpecification);
        if (response.getStatusCode() == 401) {
            ApiService.getInstance().closeAll();
            FormsManager.getInstance().showForm(new Login(null));
        }
        return response;
    }
}
