package raven.messenger.home;

import com.formdev.flatlaf.FlatClientProperties;
import raven.messenger.api.exception.ResponseException;
import raven.messenger.component.StringIcon;
import raven.messenger.component.chat.Myself;
import raven.messenger.component.chat.model.ChatFileData;
import raven.messenger.component.chat.model.ChatPhotoData;
import raven.messenger.component.chat.model.ChatSoundData;
import raven.messenger.connection.ConnectionManager;
import raven.messenger.event.GlobalEvent;
import raven.messenger.event.GroupCreateEvent;
import raven.messenger.manager.ErrorManager;
import raven.messenger.manager.FormsManager;
import raven.messenger.manager.ProfileManager;
import raven.messenger.models.file.*;
import raven.messenger.component.chat.ChatActionListener;
import raven.messenger.component.chat.ChatModel;
import raven.messenger.component.chat.ChatPanel;
import raven.messenger.component.left.LeftActionListener;
import raven.messenger.component.left.LeftPanel;
import raven.messenger.component.right.RightPanel;
import raven.messenger.models.other.ModelImage;
import raven.messenger.models.other.ModelName;
import raven.messenger.models.other.ModelProfileData;
import raven.messenger.models.response.*;
import raven.messenger.plugin.sound.AudioUtil;
import raven.messenger.plugin.sound.CaptureData;
import raven.messenger.plugin.sound.WaveFormData;
import raven.messenger.plugin.swing.scroll.ScrollRefreshModel;
import raven.messenger.socket.ChatType;
import raven.messenger.socket.MessageType;
import raven.messenger.socket.SocketService;
import raven.messenger.socket.event.SocketEvent;
import raven.messenger.socket.models.ModelSendMessage;
import raven.messenger.store.StoreManager;
import raven.messenger.util.MethodUtil;
import raven.messenger.util.NetworkDataUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.ConnectException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends JPanel {

    private ChatActionListener eventChat;
    private ScrollRefreshModel scrollRefreshModel;
    private LeftActionListener eventLeft;
    private ModelChatListItem user;
    private Map<Integer, ModelProfileData> userImages = new HashMap<>();

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
        initChatEvent();
    }

    private void initChatEvent() {
        GlobalEvent.getInstance().setGroupCreateEvent(new GroupCreateEvent() {
            @Override
            public void onCreate(int id) {
                leftPanel.createNew(ChatType.GROUP, id);
            }
        });
    }

    private boolean loadData() {
        try {
            int userId = ProfileManager.getInstance().getProfile().getUserId();
            boolean isGroup = user.isGroup();
            int target = user.getId();
            ChatModel chatModel = chatPanel.getChatModel();
            List<ModelMessage> data = SocketService.getInstance().getServiceMessage().findMessageUser(user.getChatType(), target, scrollRefreshModel.getPage());
            for (ModelMessage d : data) {
                if (userId == d.getFromUser()) {
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
                                .setVoice(new ChatSoundData(d.getFile()))
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
                                .setUsername(getProfileName(d.getFromName(), isGroup))
                                .setProfile(getProfile(d.getFromUser(), d.getFromName(), isGroup))
                                .setMessage(d.getMessage())
                                .setDate(d.getCreateDate())
                                .setTop(true)
                                .setAutoRefresh(false)
                                .build();
                    } else if (d.getType() == MessageType.VOICE) {
                        chatModel.recipient()
                                .setId(d.getFromUser())
                                .setUsername(getProfileName(d.getFromName(), isGroup))
                                .setProfile(getProfile(d.getFromUser(), d.getFromName(), isGroup))
                                .setVoice(new ChatSoundData(d.getFile()))
                                .setDate(d.getCreateDate())
                                .setTop(true)
                                .setAutoRefresh(false)
                                .build();
                    } else if (d.getType() == MessageType.PHOTO) {
                        chatModel.recipient()
                                .setId(d.getFromUser())
                                .setUsername(getProfileName(d.getFromName(), isGroup))
                                .setProfile(getProfile(d.getFromUser(), d.getFromName(), isGroup))
                                .setPhotoData(new ChatPhotoData(d.getFile()))
                                .setDate(d.getCreateDate())
                                .setTop(true)
                                .setAutoRefresh(false)
                                .build();
                    } else if (d.getType() == MessageType.FILE) {
                        chatModel.recipient()
                                .setId(d.getFromUser())
                                .setUsername(getProfileName(d.getFromName(), isGroup))
                                .setProfile(getProfile(d.getFromUser(), d.getFromName(), isGroup))
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
            ErrorManager.getInstance().showError(e);
            chatPanel.refreshLayout();
            chatPanel.scrollToBottom();
            return false;
        }
    }

    public void initHome() {
        SocketService.getInstance().open();
        SocketService.getInstance().setSocketEvent(createSocketEvent());
        try {
            ProfileManager.getInstance().initProfile();
            leftPanel.initData();
        } catch (ConnectException e) {
            // do not show reconnect button because it auto from the socket
            ConnectionManager.getInstance().showError(() -> callBackConnection(), false);
        } catch (ResponseException e) {
            ErrorManager.getInstance().showError(e);
        }
    }

    private void callBackConnection() {
        try {
            FormsManager.getInstance().showForm(this);
            ProfileManager.getInstance().initProfile();
            leftPanel.initData();
        } catch (Exception e) {
            ErrorManager.getInstance().showError(e);
        }
    }

    private Icon getProfile(int id, ModelName name, boolean group) {
        try {
            if (group) {
                if (userImages.containsKey(id)) {
                    return userImages.get(id).getIcon();
                } else {
                    ModelImage image = SocketService.getInstance().getServiceUser().getUserProfile(id);
                    Icon icon = NetworkDataUtil.getNetworkIcon(image, name.getProfileString(), 35, 35, 999);
                    userImages.put(id, new ModelProfileData(image, icon));
                    return icon;
                }
            } else {
                return null;
            }
        } catch (ResponseException e) {
            return new StringIcon(name.getProfileString(), Color.decode("#41AED7"), 35, 35);
        }
    }

    private String getProfileName(ModelName name, boolean group) {
        if (group) {
            return name.getFullName();
        } else {
            return null;
        }
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
                ModelSendMessage message = new ModelSendMessage(user.getChatType(), user.getId(), MessageType.TEXT, text);
                leftPanel.userMessage(user.getChatType(), message.getTarget(), new ModelLastMessage(message));
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
                    leftPanel.userMessage(user.getChatType(), user.getId(), new ModelLastMessage(file.getType()));
                }
                chatPanel.scrollToBottomWithAnimation();
                if (myself != null) {
                    //  send caption message to server by socket
                    ModelSendMessage message = new ModelSendMessage(user.getChatType(), user.getId(), MessageType.TEXT, text);
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
                        ModelSendMessage fileMessage = new ModelSendMessage(user.getChatType(), user.getId(), MessageType.toMessageType(file.getType().toString()), "", fileResponse.getId());
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
                        ErrorManager.getInstance().showError(e);
                    }
                }
            }

            @Override
            public void onMicrophoneCapture(CaptureData captureData) {
                WaveFormData waveFormData = AudioUtil.getWaveFormData(captureData.getAudioData(), captureData.getAudioFormat());
                ChatSoundData chatSoundData = new ChatSoundData(waveFormData.getData(), "", 0, captureData.getDuration());
                Myself myself = chatPanel.getChatModel().myself()
                        .setVoice(chatSoundData)
                        .setSeen(true)
                        .build();
                leftPanel.userMessage(user.getChatType(), user.getId(), new ModelLastMessage(FileType.toFileType("v")));
                chatPanel.scrollToBottomWithAnimation();
                try {
                    File file = AudioUtil.write(captureData);
                    ModelFileInfo fileInfo = new ModelFileVoiceInfo(captureData.getDuration(), waveFormData.getData());
                    //  upload voice file to server
                    ModelFile fileResponse = SocketService.getInstance().getServiceMessage().sendFile(file, fileInfo);
                    chatSoundData.setName(fileResponse.getName());
                    ModelSendMessage message = new ModelSendMessage(user.getChatType(), user.getId(), MessageType.VOICE, "", fileResponse.getId());
                    //  send message to server by socket
                    SocketService.getInstance().sendMessage(message, objects -> {
                        callBack(myself, objects);
                    });
                    //  add file to cache storage
                    StoreManager.getInstance().addFile(file, fileResponse.getName());
                } catch (Exception e) {
                    ErrorManager.getInstance().showError(e);
                }
            }

            @Override
            public void onJoinGroup() {
                try {
                    ModelGroup group = SocketService.getInstance().getServiceGroup().joinGroup(user.getId());
                    chatPanel.userMessageInput();
                    leftPanel.createNew(ChatType.GROUP, group.getGroupId());
                } catch (ResponseException e) {
                    ErrorManager.getInstance().showError(e);
                }
            }
        };
        eventLeft = data -> {
            if (chatBody.getComponentCount() == 0) {
                chatBody.add(chatPanel);
                chatBody.repaint();
                chatBody.revalidate();
            }
            changeUser(data);
        };
        scrollRefreshModel = new ScrollRefreshModel(1, SwingConstants.TOP) {
            @Override
            public boolean onRefreshNext() {
                return loadData();
            }

            @Override
            public void onFinishRefresh() {
                // Need to update this
                if (scrollRefresh.getScrollRefreshModel().getPage() > 1) {
                    FormsManager.getInstance().getMainFrame().repaint();
                    FormsManager.getInstance().getMainFrame().revalidate();
                }
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
    private synchronized void changeUser(ModelChatListItem user) {
        if (this.user == null || (this.user.getChatType() != user.getChatType() || this.user.getId() != user.getId())) {
            this.user = user;
            leftPanel.selectedUser(user);
            scrollRefreshModel.stop();
            chatPanel.getChatModel().clear();
            scrollRefreshModel.resetPage();
            if (user.isGroup()) {
                checkGroup(user.getUuid());
            } else {
                checkUser(user.getUuid());
            }
        } else {
            chatPanel.scrollToBottomWithAnimation();
        }
    }

    private void checkGroup(String uuid) {
        try {
            userImages.clear();
            ModelGroup group = SocketService.getInstance().getServiceGroup().checkGroup(uuid);
            if (group.isJoin()) {
                chatPanel.userMessageInput();
            } else {
                chatPanel.useJoinButton();
            }
            rightPanel.setGroup(group);
        } catch (ResponseException e) {
            chatPanel.useJoinButton();
            rightPanel.setUser(null);
            ErrorManager.getInstance().showError(e);
        }
    }

    private void checkUser(String uuid) {
        try {
            ModelUserInfo userInfo = SocketService.getInstance().getServiceUser().getUser(uuid);
            rightPanel.setUser(userInfo);
            chatPanel.userMessageInput();
        } catch (ResponseException e) {
            chatPanel.userMessageInput();
            ErrorManager.getInstance().showError(e);
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
                if (user != null) {
                    if (message.getFromId() == user.getId() && message.getChatType() == user.getChatType()) {
                        boolean isGroup = user.isGroup();
                        if (message.getType() == MessageType.TEXT) {
                            chatPanel.getChatModel().recipient()
                                    .setId(message.getFromUser())
                                    .setUsername(getProfileName(message.getFromName(), isGroup))
                                    .setProfile(getProfile(message.getFromUser(), message.getFromName(), isGroup))
                                    .setMessage(message.getMessage())
                                    .setDate(message.getCreateDate())
                                    .build();
                        } else if (message.getType() == MessageType.VOICE) {
                            chatPanel.getChatModel().recipient()
                                    .setId(message.getFromUser())
                                    .setUsername(getProfileName(message.getFromName(), isGroup))
                                    .setProfile(getProfile(message.getFromUser(), message.getFromName(), isGroup))
                                    .setVoice(new ChatSoundData(message.getFile()))
                                    .setDate(message.getCreateDate())
                                    .build();
                        } else if (message.getType() == MessageType.PHOTO) {
                            chatPanel.getChatModel().recipient()
                                    .setId(message.getFromUser())
                                    .setUsername(getProfileName(message.getFromName(), isGroup))
                                    .setProfile(getProfile(message.getFromUser(), message.getFromName(), isGroup))
                                    .setPhotoData(new ChatPhotoData(message.getFile()))
                                    .setDate(message.getCreateDate())
                                    .build();
                        } else if (message.getType() == MessageType.FILE) {
                            chatPanel.getChatModel().recipient()
                                    .setId(message.getFromUser())
                                    .setUsername(getProfileName(message.getFromName(), isGroup))
                                    .setProfile(getProfile(message.getFromUser(), message.getFromName(), isGroup))
                                    .setFileData(new ChatFileData(message.getFile()))
                                    .setDate(message.getCreateDate())
                                    .build();
                        }
                        chatPanel.scrollToBottomWithAnimation();
                    }
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