package raven.messenger.socket.event;

import raven.messenger.manager.FormsManager;
import raven.modal.Toast;
import raven.modal.toast.ToastPromise;
import raven.modal.toast.option.ToastLocation;
import raven.modal.toast.option.ToastOption;

public class ConnectionPromise {
    private ToastPromise.PromiseCallback callback;

    public void discounted() {
        System.out.println("Client discounted");
        ToastPromise toastPromise = new ToastPromise() {
            @Override
            public void execute(PromiseCallback promiseCallback) {
                callback = promiseCallback;
            }

            @Override
            protected String getId() {
                return "connection";
            }
        };
        ToastOption toastOption = Toast.createOption();
        toastOption.setPauseDelayOnHover(false);
        toastOption.getStyle().setShowCloseButton(false);
        Toast.showPromise(FormsManager.getInstance().getMainFrame(), "Disconnected", ToastLocation.BOTTOM_LEADING, toastOption, toastPromise);
    }

    public void reconnected() {
        System.out.println("Server connected");
        if (callback != null) {
            callback.done(Toast.Type.SUCCESS, "Reconnected");
        }
    }
}
