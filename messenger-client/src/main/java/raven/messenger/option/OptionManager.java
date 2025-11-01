package raven.messenger.option;

import raven.messenger.api.exception.ResponseException;
import raven.messenger.component.ModalBorderCustom;
import raven.messenger.event.GlobalEvent;
import raven.messenger.manager.ErrorManager;
import raven.messenger.manager.FormsManager;
import raven.messenger.option.group.DialogGroup;
import raven.messenger.option.profile.DialogProfile;
import raven.messenger.option.setting.DialogSetting;
import raven.messenger.option.storage.DialogStorage;
import raven.messenger.socket.SocketService;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import raven.modal.option.ModalBorderOption;
import raven.modal.option.Option;

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
        SimpleModalBorder.Option[] options = new SimpleModalBorder.Option[]{
                new SimpleModalBorder.Option("Cancel", SimpleModalBorder.CANCEL_OPTION),
                new SimpleModalBorder.Option("Save", SimpleModalBorder.OK_OPTION)
        };
        ModalBorderCustom modalBorder = new ModalBorderCustom(dialogGroup, "New Group", new ModalBorderOption().setUseScroll(true), options, (callback, action) -> {
            if (action == SimpleModalBorder.OK_OPTION) {
                if (dialogGroup.validateInput()) {
                    try {
                        int id = SocketService.getInstance().getServiceGroup().create(dialogGroup.getData());
                        GlobalEvent.getInstance().getGroupCreateEvent().onCreate(id);
                    } catch (ResponseException | IOException e) {
                        ErrorManager.getInstance().showError(e);
                    }
                } else {
                    callback.consume();
                }
            } else if (action == SimpleModalBorder.OPENED) {
                dialogGroup.open();
            }
        });
        Option option = ModalDialog.createOption();
        option.getLayoutOption().setSize(410, -1);
        ModalDialog.showModal(FormsManager.getInstance().getMainFrame(), modalBorder, option, "group");
    }

    public void showProfile() {
        DialogProfile dialogProfile = new DialogProfile();
        SimpleModalBorder modalBorder = new SimpleModalBorder(dialogProfile, "Edit Profile", new ModalBorderOption().setUseScroll(true), null, (controller, action) -> {
            if (action == SimpleModalBorder.OPENED) {
                dialogProfile.open();
            }
        });
        Option option = ModalDialog.createOption();
        option.getLayoutOption().setSize(410, -1);
        ModalDialog.showModal(FormsManager.getInstance().getMainFrame(), modalBorder, option, "profile");
    }

    public void showStorage() {
        DialogStorage dialogStorage = new DialogStorage();
        SimpleModalBorder modalBorder = new SimpleModalBorder(dialogStorage, "Local storage", new ModalBorderOption().setUseScroll(true));
        Option option = ModalDialog.createOption();
        option.getLayoutOption().setSize(410, -1);
        ModalDialog.showModal(FormsManager.getInstance().getMainFrame(), modalBorder, option, "storage");
    }

    public void showSetting() {
        DialogSetting dialogStorage = new DialogSetting();
        SimpleModalBorder modalBorder = new SimpleModalBorder(dialogStorage, "Setting", new ModalBorderOption().setUseScroll(true), null, (controller, action) -> {
            if (action == SimpleModalBorder.OPENED) {
                dialogStorage.open();
            }
        });
        Option option = ModalDialog.createOption();
        ModalDialog.showModal(FormsManager.getInstance().getMainFrame(), modalBorder, option, "setting");
    }
}
