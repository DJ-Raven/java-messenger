package raven.messenger.manager;

import raven.toast.Notifications;

public class ErrorManager {

    private static ErrorManager instance;

    public static ErrorManager getInstance() {
        if (instance == null) {
            instance = new ErrorManager();
        }
        return instance;
    }

    public void showError(Exception e) {
        Notifications.getInstance().show(Notifications.Type.ERROR, e.getMessage());
        e.printStackTrace();
    }
}
