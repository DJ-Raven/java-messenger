package raven.messenger.component.chat;

import raven.messenger.util.MethodUtil;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class ChatItemOption {

    protected Map<String, Component> componentMap;
    protected final ChatModel chatModel;
    private boolean top;
    private boolean autoRefresh = true;
    private boolean error;
    private Date date;

    public void putComponent(String key, Component component) {
        if (componentMap == null) {
            componentMap = new HashMap<>();
        }
        componentMap.put(key, component);
    }

    public Component getComponentMap(String key) {
        return componentMap.get(key);
    }

    public boolean isTop() {
        return top;
    }

    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    public boolean isError() {
        return error;
    }

    public Date getDate() {
        return date;
    }

    public String getTime() {
        if (date != null) {
            DateFormat tf = new SimpleDateFormat("hh:mm aa");
            return tf.format(date);
        } else {
            return "";
        }
    }

    public ChatItemOption(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public ChatItemOption setTop(boolean top) {
        this.top = top;
        return this;
    }

    public ChatItemOption setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
        return this;
    }

    public ChatItemOption setDate(Date date) {
        this.date = date;
        if (componentMap != null) {
            JLabel label = (JLabel) getComponentMap("time");
            label.setText(getTime());
        }
        return this;
    }

    public ChatItemOption setError(boolean error) {
        this.error = error;
        if (componentMap != null) {
            JLabel label = (JLabel) getComponentMap("sent");
            if (error) {
                label.setIcon(MethodUtil.createIcon("raven/messenger/icon/error.svg", 0.7f, Color.decode("#ef4444")));
            } else {
                label.setIcon(null);
            }
        }
        return this;
    }

    public abstract ChatItemOption build();
}
