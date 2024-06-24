package raven.messenger.manager;

import raven.modal.Toast;

public class ErrorManager {

    private static ErrorManager instance;

    public static ErrorManager getInstance() {
        if (instance == null) {
            instance = new ErrorManager();
        }
        return instance;
    }

    public void showError(Exception e) {
        Toast.show(FormsManager.getInstance().getMainFrame(), Toast.Type.ERROR, e.getMessage());
        e.printStackTrace();
    }
}
