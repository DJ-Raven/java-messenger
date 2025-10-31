package raven.messenger.component.chat;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.component.dialog.DialogSelectFile;
import raven.messenger.manager.DialogManager;
import raven.messenger.models.file.ModelFileWithType;
import raven.messenger.plugin.sound.CaptureData;
import raven.messenger.plugin.sound.SoundCapture;
import raven.messenger.plugin.sound.SoundCaptureListener;
import raven.messenger.util.EventMouseHold;
import raven.messenger.util.MethodUtil;
import raven.modal.component.SimpleModalBorder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class MessageInput extends JPanel {

    private final ChatActionListener event;
    private final SoundCapture soundCapture = new SoundCapture();

    public MessageInput(ChatActionListener event) {
        this.event = event;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill", "[grow 0]0[fill][grow 0]", "[::200,bottom]"));
        putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:10;");
        input = new TextPaneCustom();
        input.setPlaceholderText("Write a message...");
        input.setEditorKit(new AutoWrapText());
        JScrollPane scroll = new JScrollPane(input);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        input.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;" +
                "margin:4,4,4,4;");
        scroll.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:0,0,0,0;");
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "width:3;");
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        soundCapture.addSoundCaptureListener(new SoundCaptureListener() {
            @Override
            public void capturing(long millisecond) {
                input.setSoundCaptureMillisecond(millisecond);
            }

            @Override
            public void start() {
                input.setSoundCaptureMillisecond(0);
            }

            @Override
            public void stop() {
                input.setSoundCaptureMillisecond(0);
            }
        });
        input.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                revalidate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                revalidate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                revalidate();
            }
        });
        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == 10 && e.isControlDown()) {
                    sendText();
                }
            }
        });
        JButton buttonFile = createActionButton(MethodUtil.createIcon("raven/messenger/icon/attach.svg", 0.35f));
        buttonFile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonFile.addActionListener(e -> sendFile());
        add(buttonFile);
        add(scroll);
        add(createActionButton());
    }

    private Component createActionButton() {
        JPanel panel = new JPanel(new MigLayout("insets 0,gap 3"));
        JButton buttonSend = createActionButton(MethodUtil.createIcon("raven/messenger/icon/sent.svg", 0.35f));
        JButton buttonSound = createActionButton(MethodUtil.createIcon("raven/messenger/icon/microphone.svg", 0.35f));
        buttonSend.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonSound.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonSend.setFocusable(false);
        buttonSound.setFocusable(false);
        buttonSend.addActionListener(e -> sendText());
        EventMouseHold eventMouseHold = new EventMouseHold();
        eventMouseHold.applyEventMouseHold(buttonSound, 500, status -> {
            if (status == EventMouseHold.HoldStatus.ACTION) {
                input.useInput(false);
                soundCapture.start();
            } else if (status == EventMouseHold.HoldStatus.RELEASE_IN) {
                input.useInput(true);
                CaptureData captureData = soundCapture.stop();
                event.onMicrophoneCapture(captureData);
            } else if (status == EventMouseHold.HoldStatus.RELEASE_OUT) {
                input.useInput(true);
                soundCapture.stop();
            } else if (status == EventMouseHold.HoldStatus.MOUSE_ENTER) {
            } else if (status == EventMouseHold.HoldStatus.MOUSE_EXIT) {
            }
        });
        panel.add(buttonSound);
        panel.add(buttonSend);
        return panel;
    }

    private JButton createActionButton(Icon icon) {
        JButton button = new JButton(icon);
        button.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;" +
                "arc:15;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;");
        return button;
    }

    private void sendText() {
        String text = input.getText().trim();
        if (!text.isEmpty()) {
            input.setText("");
            input.grabFocus();
            MethodUtil.runWithThread(() -> event.onSendTextMessage(text));
        }
    }

    private void sendFile() {
        File[] files = DialogManager.getInstance().showOpenDialogMulti();
        if (files != null) {
            String title = getTitle(files);
            DialogSelectFile dialogSelectFile = new DialogSelectFile(files, input.getText());
            SimpleModalBorder.Option[] options = new SimpleModalBorder.Option[]{
                    new SimpleModalBorder.Option("Cancel", SimpleModalBorder.CANCEL_OPTION),
                    new SimpleModalBorder.Option("Send", SimpleModalBorder.OK_OPTION)
            };
            DialogManager.getInstance().showDialog(dialogSelectFile, title, options, (callback, action) -> {
                if (action == SimpleModalBorder.OK_OPTION) {
                    String text = dialogSelectFile.getMessage();
                    input.setText("");
                    input.grabFocus();
                    MethodUtil.runWithThread(() -> {
                        ModelFileWithType[] list = dialogSelectFile.getSelectedFiles();
                        if (list != null) {
                            event.onSendFileMessage(list, text);
                        }
                    });
                }
            }, "select_file");
        }
    }

    private String getTitle(File[] file) {
        return "Send File";
    }

    private TextPaneCustom input;
}
