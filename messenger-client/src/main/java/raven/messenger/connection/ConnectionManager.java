package raven.messenger.connection;

import raven.messenger.manager.FormsManager;

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

    public void showError(ConnectionCallBack callBack) {
        getInstance().callBack = callBack;
        FormsManager.getInstance().showForm(getInstance().getFormError());
    }

    private FormError getFormError() {
        if (formError == null) {
            formError = new FormError();
        }
        return formError;
    }

    public void checkOnReconnection() {
        if (callBack != null) {
            callBack.onConnected();
            callBack = null;
        }
    }
}
