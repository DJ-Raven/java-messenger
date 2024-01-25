package raven.messenger.socket.event;

import raven.messenger.models.response.ModelMessage;

public interface SocketEvent {
    public void onReceiveMessage(ModelMessage message);

    public void onUserActiveStatus(int userId, boolean status);

    public void onError(Exception e);
}
