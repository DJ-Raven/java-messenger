package raven.messenger.component.chat;

public interface ChatComponentBuilder {
    public void buildRecipient(ChatPanel chatPanel, Recipient recipient);

    public void buildMyself(ChatPanel chatPanel, Myself myself);
}
