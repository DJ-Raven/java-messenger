package raven.messenger.option;

import raven.messenger.api.exception.ResponseException;
import raven.messenger.event.GlobalEvent;
import raven.messenger.manager.ErrorManager;
import raven.messenger.option.group.DialogGroup;
import raven.messenger.option.profile.DialogProfile;
import raven.messenger.option.storage.DialogStorage;
import raven.messenger.socket.SocketService;
import raven.popup.GlassPanePopup;
import raven.popup.component.SimplePopupBorder;
import raven.popup.component.SimplePopupBorderOption;

import java.io.IOException;

public class OptionManager {

    private static OptionManager instance;

    public static OptionManager getInstance() {
        if (instance == null) {
            instance = new OptionManager();
        }
        return instance;
    }

    public void newGroup() {
        DialogGroup dialogGroup = new DialogGroup();
        String actions[] = {"Cancel", "Save"};
        SimplePopupBorder dialogBorder = new SimplePopupBorder(dialogGroup, "New Group", new SimplePopupBorderOption().useScroll(), actions, (popupController, i) -> {
            if (i == 1) {
                if (dialogGroup.validateInput()) {
                    try {
                        int id = SocketService.getInstance().getServiceGroup().create(dialogGroup.getData());
                        GlobalEvent.getInstance().getGroupCreateEvent().onCreate(id);
                        popupController.closePopup();
                    } catch (ResponseException | IOException e) {
                        ErrorManager.getInstance().showError(e);
                    }
                }
            } else {
                popupController.closePopup();
            }
        });
        GlassPanePopup.showPopup(dialogBorder, "group");
    }

    public void showProfile() {
        DialogProfile dialogProfile = new DialogProfile();
        SimplePopupBorder dialogBorder = new SimplePopupBorder(dialogProfile, "Edit Profile", new SimplePopupBorderOption().useScroll());
        GlassPanePopup.showPopup(dialogBorder, "profile");
    }

    public void showStorage() {
        DialogStorage dialogStorage = new DialogStorage();
        SimplePopupBorder dialogBorder = new SimplePopupBorder(dialogStorage, "Local storage", new SimplePopupBorderOption().useScroll());
        GlassPanePopup.showPopup(dialogBorder, "storage");
    }
}
