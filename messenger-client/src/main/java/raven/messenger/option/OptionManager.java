package raven.messenger.option;

import raven.messenger.option.profile.DialogProfile;
import raven.messenger.option.storage.DialogStorage;
import raven.popup.GlassPanePopup;
import raven.popup.component.PopupCallbackAction;
import raven.popup.component.SimplePopupBorder;

public class OptionManager {

    private static OptionManager instance;

    public static OptionManager getInstance() {
        if (instance == null) {
            instance = new OptionManager();
        }
        return instance;
    }

    public void showProfile() {
        DialogProfile dialogProfile = new DialogProfile();
        SimplePopupBorder dialogBorder = new SimplePopupBorder(dialogProfile, "Edit Profile");
        GlassPanePopup.showPopup(dialogBorder, "profile");
    }

    public void showStorage() {
        DialogStorage dialogStorage = new DialogStorage();
        SimplePopupBorder dialogBorder = new SimplePopupBorder(dialogStorage, "Local storage");
        GlassPanePopup.showPopup(dialogBorder, "storage");
    }
}
