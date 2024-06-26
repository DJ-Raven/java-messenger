package raven.messenger.socket;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONObject;
import raven.messenger.api.ApiService;
import raven.messenger.manager.ErrorManager;
import raven.messenger.models.response.ModelMessage;
import raven.messenger.service.ServiceGroup;
import raven.messenger.service.ServiceMessage;
import raven.messenger.service.ServiceUser;
import raven.messenger.socket.event.ConnectionPromise;
import raven.messenger.socket.event.SocketEvent;
import raven.messenger.socket.models.ModelSendMessage;
import raven.messenger.store.CookieManager;

import java.net.URI;
import java.util.Collections;

public class SocketService {

    private SocketEvent socketEvent;

    private static SocketService instance;
    private final ServiceMessage serviceMessage;
    private final ServiceGroup serviceGroup;
    private final ServiceUser serviceUser;
    private Socket socket;

    public static SocketService getInstance() {
        if (instance == null) {
            instance = new SocketService();
        }
        return instance;
    }

    private SocketService() {
        serviceMessage = new ServiceMessage();
        serviceGroup = new ServiceGroup();
        serviceUser = new ServiceUser();
    }

    private Socket initSocket() {
        URI uri = URI.create(ApiService.IP + ":5000");
        String cookie = CookieManager.getInstance().getCookieString();
        IO.Options option = IO.Options.builder()
                .setExtraHeaders(Collections.singletonMap("cookies", Collections.singletonList(cookie)))
                .build();
        Socket socket = IO.socket(uri, option);
        final ConnectionPromise connectionPromise = new ConnectionPromise();
        socket.on(Socket.EVENT_DISCONNECT, objects -> {
            connectionPromise.discounted();
        });
        socket.on(Socket.EVENT_CONNECT, objects -> {
            connectionPromise.reconnected();
        });
        socket.on(Socket.EVENT_CONNECT_ERROR, objects -> {
            System.out.println(objects[0]);
        });
        socket.on("user_status", objects -> {
            int userId = (int) objects[0];
            boolean status = (boolean) objects[1];
            socketEvent.onUserActiveStatus(userId, status);

        });
        socket.on("message", objects -> {
            if (socketEvent != null) {
                try {
                    socketEvent.onReceiveMessage(new ModelMessage((JSONObject) objects[0]));
                } catch (Exception e) {
                    socketEvent.onError(e);
                }
            }
        });
        return socket;
    }

    public void open() {
        socket = initSocket();
        socket.open();
    }

    public void close() {
        if (socket != null) {
            socket.disconnect();
            socket.close();
            socket = null;
        }
    }

    public void setSocketEvent(SocketEvent event) {
        this.socketEvent = event;
    }

    public ServiceMessage getServiceMessage() {
        return serviceMessage;
    }

    public ServiceGroup getServiceGroup() {
        return serviceGroup;
    }

    public ServiceUser getServiceUser() {
        return serviceUser;
    }

    public void sendMessage(ModelSendMessage message, MessageCallback callback) {
        try {
            socket.emit("message", message.toJsonObject(), (Ack) objects -> callback.onSuccess(objects));
        } catch (Exception e) {
            ErrorManager.getInstance().showError(e);
        }
    }
}
