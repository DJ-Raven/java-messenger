package raven.messenger.component.chat;

public class ChatModel {

    private final ChatPanel chatPanel;

    public ChatModel(ChatPanel chatPanel) {
        this.chatPanel = chatPanel;
        init();

    }

    private void init() {

    }

    protected void add(Recipient recipient) {
        chatPanel.add(recipient);
    }

    protected void add(Myself myself) {
        chatPanel.add(myself);
    }

    public Myself myself() {
        return new Myself(this);
    }

    public Recipient recipient() {
        return new Recipient(this);
    }

    public void clear() {
        chatPanel.getPanel().removeAll();
        chatPanel.createEmptyDataLabel();
        chatPanel.getPanel().repaint();
        chatPanel.getPanel().revalidate();
    }
}
