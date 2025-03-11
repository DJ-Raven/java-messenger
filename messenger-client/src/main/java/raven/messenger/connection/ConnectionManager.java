package raven.messenger.connection;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import raven.messenger.manager.FormsManager;
import raven.modal.Toast;

public class ConnectionManager {

    private static ConnectionManager instance;
    private ConnectionCallBack callBack;
    private FormError formError;

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    private ConnectionManager() {
    }

    public void showError(ConnectionCallBack callBack, boolean showReconnectButton) {
        getInstance().callBack = callBack;
        Toast.show(FormsManager.getInstance().getMainFrame(), Toast.Type.ERROR, "Connection error");
        FormsManager.getInstance().showForm(getInstance().getFormError(showReconnectButton));
    }

    private FormError getFormError(boolean showReconnectButton) {
        if (formError == null) {
            formError = new FormError();
        }
        formError.showReconnectButton(showReconnectButton);
        return formError;
    }

    public void checkOnReconnection() {
        if (callBack != null) {
            callBack.onConnected();
            callBack = null;
        }
    }

    public Type checkConnection() {
        try {
            Response response = RestAssured.given()
                    .get("check");
            if (response.getStatusCode() == 200) {
                return Type.SUCCESS;
            } else if (response.getStatusCode() == 426) {
                return Type.CLIENT_REQUIRED_UPDATE;
            }
            return Type.ERROR;
        } catch (Exception e) {
            return Type.ERROR;
        }
    }

    public enum Type {
        ERROR, CLIENT_REQUIRED_UPDATE, SUCCESS
    }
}
