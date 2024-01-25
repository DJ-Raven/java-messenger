package raven.messenger.home;

import com.formdev.flatlaf.FlatClientProperties;
import raven.messenger.component.chat.Myself;
import raven.messenger.component.chat.model.ChatFileData;
import raven.messenger.component.chat.model.ChatPhotoData;
import raven.messenger.component.chat.model.ChatVoiceData;
import raven.messenger.manager.FormsManager;
import raven.messenger.manager.ProfileManager;
import raven.messenger.models.file.*;
import raven.messenger.component.chat.ChatActionListener;
import raven.messenger.component.chat.ChatModel;
import raven.messenger.component.chat.ChatPanel;
import raven.messenger.component.left.LeftActionListener;
import raven.messenger.component.left.LeftPanel;
import raven.messenger.component.right.RightPanel;
import raven.messenger.models.response.ModelLastMessage;
import raven.messenger.models.response.ModelMessage;
import raven.messenger.models.response.ModelUser;
import raven.messenger.plugin.sound.AudioUtil;
import raven.messenger.plugin.sound.CaptureData;
import raven.messenger.plugin.sound.WaveFormData;
import raven.messenger.plugin.swing.scroll.ScrollRefreshModel;
import raven.messenger.socket.MessageType;
import raven.messenger.socket.SocketService;
import raven.messenger.socket.event.SocketEvent;
import raven.messenger.socket.models.ModelSendMessage;
import raven.messenger.store.StoreManager;
import raven.messenger.util.MethodUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends JPanel {

    private ChatActionListener eventChat;
    private ScrollRefreshModel scrollRefreshModel;
    private LeftActionListener eventLeft;
    private ModelUser user;

    public Home() {
        initEvent();
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        mainSplit = new JSplitPane();
        subSplit = new JSplitPane();
        chatBody = new JPanel(new BorderLayout());
        chatPanel = new ChatPanel(eventChat, scrollRefreshModel);
        leftPanel = new LeftPanel(eventLeft);
        rightPanel = new RightPanel();
        mainSplit.putClientProperty(FlatClientProperties.STYLE, "" +
                "style:plain;" +
                "continuousLayout:false");
        subSplit.putClientProperty(FlatClientProperties.STYLE, "" +
                "style:plain;" +
                "continuousLayout:false");
        subSplit.setLeftComponent(chatBody);
        subSplit.setRightComponent(rightPanel);
        mainSplit.setLeftComponent(leftPanel);
        mainSplit.setRightComponent(subSplit);
        subSplit.setResizeWeight(1);
        add(mainSplit);
    }

    private boolean loadData() {
        try {
            int userId = user.getUserId();
            ChatModel chatModel = chatPanel.getChatModel();
            List<ModelMessage> data = SocketService.getInstance().getServiceMessage().findMessageUser(userId, scrollRefreshModel.getPage());
            for (ModelMessage d : data) {
                if (userId != d.getFromUser()) {
                    if (d.getType() == MessageType.TEXT) {
                        chatModel.myself()
                                .setMessage(d.getMessage())
                                .setSent(true)
                                .setSeen(true)
                                .setDate(d.getCreateDate())
                                .setTop(true)
                                .setAutoRefresh(false)
                                .build();
                    } else if (d.getType() == MessageType.VOICE) {
                        chatModel.myself()
                                .setVoice(new ChatVoiceData(d.getFile()))
                                .setSent(true)
                                .setSeen(true)
                                .setDate(d.getCreateDate())
                                .setTop(true)
                                .setAutoRefresh(false)
                                .build();
                    } else if (d.getType() == MessageType.PHOTO) {
                        chatModel.myself()
                                .setPhotoData(new ChatPhotoData(d.getFile()))
                                .setSent(true)
                                .setSeen(true)
                                .setDate(d.getCreateDate())
                                .setTop(true)
                                .setAutoRefresh(false)
                                .build();
                    } else if (d.getType() == MessageType.FILE) {
                        chatModel.myself()
                                .setFileData(new ChatFileData(d.getFile()))
                                .setSent(true)
                                .setSeen(true)
                                .setDate(d.getCreateDate())
                                .setTop(true)
                                .setAutoRefresh(false)
                                .build();
                    }
                } else {
                    if (d.getType() == MessageType.TEXT) {
                        chatModel.recipient()
                                .setId(d.getFromUser())
                                .setUsername(user.getName().getFullName())
                                .setMessage(d.getMessage())
                                .setDate(d.getCreateDate())
                                .setTop(true)
                                .setAutoRefresh(false)
                                .build();
                    } else if (d.getType() == MessageType.VOICE) {
                        chatModel.recipient()
                                .setId(d.getFromUser())
                                .setUsername(user.getName().getFullName())
                                .setVoice(new ChatVoiceData(d.getFile()))
                                .setDate(d.getCreateDate())
                                .setTop(true)
                                .setAutoRefresh(false)
                                .build();
                    } else if (d.getType() == MessageType.PHOTO) {
                        chatModel.recipient()
                                .setId(d.getFromUser())
                                .setUsername(user.getName().getFullName())
                                .setPhotoData(new ChatPhotoData(d.getFile()))
                                .setDate(d.getCreateDate())
                                .setTop(true)
                                .setAutoRefresh(false)
                                .build();
                    } else if (d.getType() == MessageType.FILE) {
                        chatModel.recipient()
                                .setId(d.getFromUser())
                                .setUsername(user.getName().getFullName())
                                .setFileData(new ChatFileData(d.getFile()))
                                .setDate(d.getCreateDate())
                                .setTop(true)
                                .setAutoRefresh(false)
                                .build();
                    }
                }
            }
            return !data.isEmpty();
        } catch (Exception e) {
            chatPanel.refreshLayout();
            chatPanel.scrollToBottom();
            e.printStackTrace();
            return false;
        }
    }

    public void initHome() {
        SocketService.getInstance().open();
        SocketService.getInstance().setSocketEvent(createSocketEvent());
        ProfileManager.getInstance().initProfile();
        leftPanel.initData();
    }

    /*
        Call back for seen status after emit to server by socket
     */
    private void callBack(Myself myself, Object... objects) {
        if (objects.length > 0) {
            Date date = MethodUtil.stringToDate(objects[0].toString());
            if (date != null) {
                myself.setDate(date);
                myself.setSent(true);
            }
        }
    }

    private void initEvent() {
        eventChat = new ChatActionListener() {
            @Override
            public void onSendTextMessage(String text) {
                Myself myself = chatPanel.getChatModel().myself()
                        .setMessage(text)
                        .setSeen(true)
                        .build();
                chatPanel.scrollToBottomWithAnimation();
                ModelSendMessage message = new ModelSendMessage(user.getUserId(), MessageType.TEXT, text);
                leftPanel.userMessage(message.getToUser(), new ModelLastMessage(message));
                SocketService.getInstance().sendMessage(message, objects -> {
                    callBack(myself, objects);
                });
            }

            @Override
            public void onSendFileMessage(ModelFileWithType files[], String text) {
                Myself myself;
                Map<ModelFileWithType, Myself> myselfMap = new HashMap<>();
                if (!text.isEmpty()) {
                    myself = chatPanel.getChatModel().myself()
                            .setMessage(text)
                            .setSeen(true)
                            .build();

                } else {
                    myself = null;
                }
                for (ModelFileWithType file : files) {
                    Myself m;
                    if (file.getType() == FileType.PHOTO) {
                        ChatPhotoData chatPhotoData = new ChatPhotoData(file.getFile().getAbsolutePath());
                        m = chatPanel.getChatModel().myself()
                                .setPhotoData(chatPhotoData)
                                .setSeen(true)
                                .build();
                    } else {
                        m = chatPanel.getChatModel().myself()
                                .setFileData(new ChatFileData(file.getFile()))
                                .setSeen(true)
                                .build();
                    }
                    myselfMap.put(file, m);
                    leftPanel.userMessage(user.getUserId(), new ModelLastMessage(file.getType()));
                }
                chatPanel.scrollToBottomWithAnimation();
                if (myself != null) {
                    //  send caption message to server by socket
                    ModelSendMessage message = new ModelSendMessage(user.getUserId(), MessageType.TEXT, text);
                    SocketService.getInstance().sendMessage(message, objects -> {
                        callBack(myself, objects);
                    });
                }
                for (ModelFileWithType file : files) {
                    try {
                        ModelFileInfo fileInfo = file.getType() == FileType.PHOTO ?
                                new ModelFilePhotoInfo() :
                                new ModelFileDataInfo();
                        //  upload file to server
                        ModelFile fileResponse = SocketService.getInstance().getServiceMessage().sendFile(file.getFile(), fileInfo);
                        ModelSendMessage fileMessage = new ModelSendMessage(user.getUserId(), MessageType.toMessageType(file.getType().toString()), "", fileResponse.getId());
                        //  send message by socket to server
                        SocketService.getInstance().sendMessage(fileMessage, objects -> {
                            Myself ms = myselfMap.get(file);
                            if (ms != null) {
                                Date date = MethodUtil.stringToDate(objects[0].toString());
                                ms.setDate(date);
                                ms.setSent(true);
                            }
                        });
                        //  add file to cache storage
                        StoreManager.getInstance().addFile(file.getFile(), fileResponse.getName());
                    } catch (Exception e) {
                        myselfMap.get(file).setError(true);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onMicrophoneCapture(CaptureData captureData) {
                WaveFormData waveFormData = AudioUtil.getWaveFormData(captureData.getAudioData(), captureData.getAudioFormat());
                ChatVoiceData chatVoiceData = new ChatVoiceData(waveFormData.getData(), "", 0, captureData.getDuration());
                Myself myself = chatPanel.getChatModel().myself()
                        .setVoice(chatVoiceData)
                        .setSeen(true)
                        .build();
                leftPanel.userMessage(user.getUserId(), new ModelLastMessage(FileType.toFileType("v")));
                chatPanel.scrollToBottomWithAnimation();
                try {
                    File file = AudioUtil.write(captureData);
                    ModelFileInfo fileInfo = new ModelFileVoiceInfo(captureData.getDuration(), waveFormData.getData());
                    //  upload voice file to server
                    ModelFile fileResponse = SocketService.getInstance().getServiceMessage().sendFile(file, fileInfo);
                    chatVoiceData.setName(fileResponse.getName());
                    ModelSendMessage message = new ModelSendMessage(user.getUserId(), MessageType.VOICE, "", fileResponse.getId());
                    //  send message to server by socket
                    SocketService.getInstance().sendMessage(message, objects -> {
                        callBack(myself, objects);
                    });
                    //  add file to cache storage
                    StoreManager.getInstance().addFile(file, fileResponse.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        eventLeft = user -> {
            if (chatBody.getComponentCount() == 0) {
                chatBody.add(chatPanel);
                chatBody.repaint();
                chatBody.revalidate();
            }
            changeUser(user);
        };
        scrollRefreshModel = new ScrollRefreshModel(1, SwingConstants.TOP) {
            @Override
            public boolean onRefreshNext() {
                return loadData();
            }

            @Override
            public void onFinishRefresh() {
                // Need to update this
                FormsManager.getInstance().getMainFrame().repaint();
                FormsManager.getInstance().getMainFrame().revalidate();
            }

            @Override
            public void onFinishData() {

            }

            @Override
            public void onError(Exception e) {

            }
        };
    }

    /*
        This method work when select changed user profile left menu
     */
    private synchronized void changeUser(ModelUser user) {
        if (this.user != user) {
            this.user = user;
            leftPanel.selectedUser(user.getUserId());
            scrollRefreshModel.stop();
            chatPanel.getChatModel().clear();
            scrollRefreshModel.resetPage();
        } else {
            chatPanel.scrollToBottomWithAnimation();
        }
    }

    /*
        This method use for socket listener from the server any another client emit to us
     */
    private SocketEvent createSocketEvent() {
        SocketEvent socketEvent = new SocketEvent() {
            @Override
            public void onReceiveMessage(ModelMessage message) {
                leftPanel.userMessage(message);
                if (message.getFromUser() == user.getUserId()) {
                    if (message.getType() == MessageType.TEXT) {
                        chatPanel.getChatModel().recipient()
                                .setId(user.getUserId())
                                .setUsername(user.getName().getFullName())
                                .setMessage(message.getMessage())
                                .setDate(message.getCreateDate())
                                .build();
                    } else if (message.getType() == MessageType.VOICE) {
                        chatPanel.getChatModel().recipient()
                                .setId(user.getUserId())
                                .setUsername(user.getName().getFullName())
                                .setVoice(new ChatVoiceData(message.getFile()))
                                .setDate(message.getCreateDate())
                                .build();
                    } else if (message.getType() == MessageType.PHOTO) {
                        chatPanel.getChatModel().recipient()
                                .setId(user.getUserId())
                                .setUsername(user.getName().getFullName())
                                .setPhotoData(new ChatPhotoData(message.getFile()))
                                .setDate(message.getCreateDate())
                                .build();
                    } else if (message.getType() == MessageType.FILE) {
                        chatPanel.getChatModel().recipient()
                                .setId(user.getUserId())
                                .setUsername(user.getName().getFullName())
                                .setFileData(new ChatFileData(message.getFile()))
                                .setDate(message.getCreateDate())
                                .build();
                    }
                    chatPanel.scrollToBottomWithAnimation();
                }
            }

            @Override
            public void onUserActiveStatus(int userId, boolean status) {
                leftPanel.changeUserStatus(userId, status);
            }

            @Override
            public void onError(Exception e) {

            }
        };
        return socketEvent;
    }

    private JSplitPane mainSplit;
    private JSplitPane subSplit;
    private JPanel chatBody;
    private ChatPanel chatPanel;
    private LeftPanel leftPanel;
    private RightPanel rightPanel;
}