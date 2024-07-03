package raven.messenger.component.chat.item;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.component.ButtonProgress;
import raven.messenger.component.chat.model.ChatFileData;
import raven.messenger.store.StoreManager;
import raven.messenger.util.ComponentUtil;
import raven.messenger.util.MethodUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class ItemFile extends JPanel implements ProgressChat {

    private JPopupMenu popupMenu;
    private ChatFileData data;
    private int type;

    public ItemFile(ChatFileData data, int type) {
        this.data = data;
        this.type = type;
        init();
    }

    private void init() {
        setLayout(new MigLayout("insets 3 5 3 5,fill", "[grow 0][fill]"));
        String backgroundKey = type == 1 ? "$Chat.item.background" : "$Chat.item.myselfBackground";
        putClientProperty(FlatClientProperties.STYLE, "" +
                "background:" + backgroundKey);

        String icon;
        if (StoreManager.getInstance().getFile(data.getName()) != null) {
            icon = "file.svg";
        } else {
            icon = "download.svg";
            data.setEventFileNameChanged(o -> {
                buttonProgress.setIcon(MethodUtil.createIcon("raven/messenger/icon/play.svg", 0.8f));
            });
        }
        buttonProgress = new ButtonProgress(MethodUtil.createIcon("raven/messenger/icon/" + icon, 0.8f));
        buttonProgress.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:999;" +
                "margin:7,7,7,7;" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");
        buttonProgress.addActionListener(actionEvent -> {
            eventClick();
        });

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    File file = StoreManager.getInstance().getFile(data.getName());
                    if (file != null) {
                        getPopupMenu().show((Component) e.getSource(), e.getX(), e.getY());
                    }
                }
            }
        };

        addMouseListener(mouseAdapter);
        buttonProgress.addMouseListener(mouseAdapter);

        JLabel lbName = new JLabel(data.getOriginalName());
        JLabel lbSize = new JLabel(MethodUtil.formatSize(data.getSize()));
        lbSize.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:$Text.lowForeground");
        add(buttonProgress, "span 1 2");
        add(lbName, "cell 1 0,width 187!");
        add(lbSize, "cell 1 1");
    }

    private JPopupMenu getPopupMenu() {
        if (popupMenu == null) {
            popupMenu = ComponentUtil.createOpenAndSavePopup(data.getName(), data.getOriginalName());
        }
        return popupMenu;
    }

    private void eventClick() {
        File file = StoreManager.getInstance().getFile(data.getName());
        if (file == null) {
            file = StoreManager.getInstance().createFile(data.getName());
            ProgressChat.download(this, data.getName(), file);
        } else {
            ComponentUtil.openFile(file);
        }
    }

    @Override
    public void onDownload(float progress) {
        buttonProgress.setProgress(progress);
    }

    @Override
    public void onFinish(File file) {
        buttonProgress.setIcon(MethodUtil.createIcon("raven/messenger/icon/file.svg", 0.8f));
        buttonProgress.setProgress(0);
    }

    @Override
    public void onError(Exception e) {
        buttonProgress.setProgress(0);
    }

    private ButtonProgress buttonProgress;
}
