package raven.messenger.component.chat.item;

import raven.messenger.api.request.RequestFileMonitor;
import raven.messenger.socket.SocketService;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public interface ProgressChat {
    Map<ProgressChat, RequestFileMonitor> requestMap = new HashMap<>();

    void onDownload(float progress);

    void onFinish(File file);

    void onError(Exception e);

    static void download(ProgressChat progressChat, String fileName, File savePath) {
        final RequestFileMonitor requestFileMonitor;
        if (requestMap.containsKey(progressChat)) {

        } else {
            try {
                requestFileMonitor = new RequestFileMonitor(fileName, savePath) {
                    @Override
                    public void responseMonitor(long length, long bytesWritten) {
                        progressChat.onDownload((bytesWritten / (float) length));
                    }

                    @Override
                    public void done(File file) {
                        requestMap.remove(progressChat);
                        progressChat.onFinish(file);
                    }

                    @Override
                    public void error(Exception e) {
                        requestMap.remove(progressChat);
                        progressChat.onError(e);
                    }
                };
                requestMap.put(progressChat, requestFileMonitor);
                SocketService.getInstance().getServiceMessage().getFile(requestFileMonitor);
            } catch (Exception e) {
                progressChat.onError(e);
            }
        }
    }
}
