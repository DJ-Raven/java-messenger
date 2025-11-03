package raven.messenger.util;

import raven.extras.AvatarIcon;
import raven.messenger.api.request.RequestFileMonitor;
import raven.messenger.component.NetworkIcon;
import raven.messenger.component.StringIcon;
import raven.messenger.manager.ErrorManager;
import raven.messenger.models.other.ModelImage;
import raven.messenger.socket.SocketService;
import raven.messenger.store.StoreManager;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class NetworkDataUtil {

    private final static Map<String, NetworkIcon.IconResource> iconMap = new HashMap<>();

    public static Icon getNetworkIcon(ModelImage image, String string, int width, int height, int round) {
        if (image == null) {
            return new StringIcon(string, width, height);
        } else {
            if (iconMap.containsKey(image.getImage())) {
                return new NetworkIcon(iconMap.get(image.getImage()), width, height);
            }
            File file = StoreManager.getInstance().getFile(image.getImage());
            if (file != null) {
                return new AvatarIcon(file.getAbsolutePath(), width, height, round);
            } else {
                NetworkIcon.IconResource resource = new NetworkIcon.IconResource(image.getHash(), image.getWidth(), image.getHeight(), round);
                NetworkIcon icon = new NetworkIcon(resource, width, height, true);
                iconMap.put(image.getImage(), resource);
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
                    iconMap.remove(image);
                }
            }

            @Override
            public void error(Exception e) {
                iconMap.remove(image);
            }
        };
        try {
            SocketService.getInstance().getServiceMessage().getFile(requestFileMonitor);
        } catch (Exception e) {
            ErrorManager.getInstance().showError(e);
        }
    }
}
