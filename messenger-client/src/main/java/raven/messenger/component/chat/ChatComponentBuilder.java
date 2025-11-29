package raven.messenger.component.chat;

public interface ChatComponentBuilder {
    void buildRecipient(ChatPanel chatPanel, Recipient recipient);

    void buildMyself(ChatPanel chatPanel, Myself myself);
}
