package raven.messenger.socket.event;

import raven.messenger.models.response.ModelMessage;

public interface SocketEvent {
    void onReceiveMessage(ModelMessage message);

    void onUserActiveStatus(int userId, boolean status);

    void onError(Exception e);
}
