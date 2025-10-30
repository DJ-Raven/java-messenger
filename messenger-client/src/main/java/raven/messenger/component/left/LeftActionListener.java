package raven.messenger.component.left;

import raven.messenger.models.response.ModelChatListItem;

public interface LeftActionListener {
    void onUserSelected(ModelChatListItem user);
}
