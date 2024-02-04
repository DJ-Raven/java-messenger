package raven.messenger.util;

import raven.messenger.api.request.RequestFileMonitor;
import raven.messenger.component.NetworkIcon;
import raven.messenger.component.StringIcon;
import raven.messenger.manager.ErrorManager;
import raven.messenger.models.other.ModelImage;
import raven.messenger.socket.SocketService;
import raven.messenger.store.StoreManager;
import raven.swing.AvatarIcon;

import javax.swing.*;
import java.io.File;

public class NetworkDataUtil {

    public static Icon getNetworkIcon(ModelImage image, String string, int width, int height, int round) {
        if (image == null) {
            return new StringIcon(string, UIManager.getColor("Component.accentColor"), width, height);
        } else {
            File file = StoreManager.getInstance().getFile(image.getImage());
            if (file != null) {
                return new AvatarIcon(file.getAbsolutePath(), width, height, round);
            } else {
                NetworkIcon.IconResource resource = new NetworkIcon.IconResource(image.getHash(), width, height, round);
                NetworkIcon icon = new NetworkIcon(resource, width, height, true);
                downloadImage(resource, image.getImage());
                return icon;
            }
        }
    }

    public static void downloadImage(NetworkIcon.IconResource resource, String image) {
        File file = StoreManager.getInstance().createFile(image);
        RequestFileMonitor requestFileMonitor = new RequestFileMonitor(image, file) {
            @Override
            public void responseMonitor(long length, long bytesWritten) {
            }

            @Override
            public void done(File file) {
                if (file != null) {
                    resource.setImage(file.getAbsolutePath());
                }
            }

            @Override
            public void error(Exception e) {
            }
        };
        try {
            SocketService.getInstance().getServiceMessage().getFile(requestFileMonitor);
        } catch (Exception e) {
            ErrorManager.getInstance().showError(e);
        }
    }
}
