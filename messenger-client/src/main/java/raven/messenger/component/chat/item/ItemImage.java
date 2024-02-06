package raven.messenger.component.chat.item;

import net.miginfocom.swing.MigLayout;
import raven.messenger.component.ButtonProgressTransparent;
import raven.messenger.component.EmptyIcon;
import raven.messenger.component.NetworkIcon;
import raven.messenger.component.PanelTransparent;
import raven.messenger.component.chat.model.ChatPhotoData;
import raven.messenger.manager.DialogManager;
import raven.messenger.manager.ErrorManager;
import raven.messenger.store.StoreManager;
import raven.messenger.util.GraphicsUtil;
import raven.messenger.util.MethodUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.nio.file.Files;

public class ItemImage extends ChatItem implements ProgressChat {

    private JPopupMenu popupMenu;
    private ChatPhotoData photo;
    private NetworkIcon networkIcon;
    private ButtonProgressTransparent buttonProgress;
    private PanelTransparent timePanel;

    public ItemImage(ChatPhotoData photo, int type) {
        super(0, type);
        this.photo = photo;
        init();
    }

    private void init() {
        JButton button = new JButton(new EmptyIcon(photo.getWidth(), photo.getHeight()));
        button.setContentAreaFilled(false);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.addActionListener(e -> {
            eventClick();
        });
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                timePanel.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                timePanel.setVisible(false);
            }
        });

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    File file = StoreManager.getInstance().getFile(photo.getName());
                    if (file != null) {
                        getPopupMenu().show(ItemImage.this, e.getX(), e.getY());
                    }
                }
            }
        };

        button.addMouseListener(mouseAdapter);

        setLayout(new MigLayout("fill,insets 5,wrap,gapy 3", "fill", "fill"));
        add(button);
        MethodUtil.runWithThread(() -> {
            NetworkIcon.IconResource resource;
            if (photo.getPath() != null) {
                resource = new NetworkIcon.IconResource(photo.getPath());
            } else {
                resource = new NetworkIcon.IconResource(photo.getHash(), photo.getWidth(), photo.getHeight());
            }
            networkIcon = new NetworkIcon(resource, photo.getWidth(), photo.getHeight());
            networkIcon.setShape(createShape());
            button.setIcon(networkIcon);
            if (photo.getPath() == null) {
                createDownloadButton();
            }
        });
    }

    public void addTimePanel(Component component) {
        if (timePanel == null) {
            timePanel = new PanelTransparent(999, 0.5f);
            timePanel.setLayout(new MigLayout("insets 3 8 3 8"));
            timePanel.setOpaque(false);
            timePanel.setVisible(false);
            add(timePanel, "pos 100%-pref-10 100%-pref-10", 0);
        }
        timePanel.add(component);
    }

    private void createDownloadButton() {
        buttonProgress = new ButtonProgressTransparent(MethodUtil.createIcon("raven/messenger/icon/download.svg", 0.8f));
        buttonProgress.addActionListener(e -> {
            if (photo.getPath() == null) {
                eventClick();
            }
        });
        add(buttonProgress, "pos 0.5al 0.5al", 0);
        doLayout();
    }


    private JPopupMenu getPopupMenu() {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();
            JMenuItem menuOpen = new JMenuItem("Open", MethodUtil.createIcon("raven/messenger/icon/view.svg", 0.4f));
            JMenuItem menuSave = new JMenuItem("Save", MethodUtil.createIcon("raven/messenger/icon/save.svg", 0.4f));
            menuOpen.addActionListener(e -> {
                File file = StoreManager.getInstance().getFile(photo.getName());
                if (file != null) {
                    openFile(file);
                }
            });
            menuSave.addActionListener(e -> {
                File saveFile = DialogManager.getInstance().showSaveDialog(photo.getName());
                if (saveFile != null) {
                    File file = StoreManager.getInstance().getFile(photo.getName());
                    if (file != null) {
                        try {
                            Files.copy(file.toPath(), saveFile.toPath());
                        } catch (Exception ex) {
                            ErrorManager.getInstance().showError(ex);
                        }
                    }
                }
            });
            popupMenu.add(menuOpen);
            popupMenu.add(menuSave);
        }
        return popupMenu;
    }

    private void openFile(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            ErrorManager.getInstance().showError(e);
        }
    }

    private void eventClick() {
        if (photo.getPath() != null && new File(photo.getPath()).exists()) {
            DialogManager.getInstance().showViewPhotoDialog(photo);
        } else {
            if (buttonProgress == null) {
                createDownloadButton();
                repaint();
                revalidate();
            }
            buttonProgress.setProgress(0f);
            if (!buttonProgress.isVisible()) {
                photo.setPath(null);
                buttonProgress.setVisible(true);
            }
            File path = StoreManager.getInstance().createFile(photo.getName());
            ProgressChat.download(this, photo.getName(), path);
        }
    }

    @Override
    public void setLevel(int level) {
        super.setLevel(level);
        if (networkIcon != null) {
            networkIcon.setShape(createShape());
        }
    }

    private Shape createShape() {
        int width = photo.getWidth();
        int height = photo.getHeight();
        Area area = new Area(new Rectangle2D.Double(0, 0, width, height));
        area.subtract(new Area(GraphicsUtil.getShape(0, 0, width, height, level, type, true, labelName != null)));
        return area;
    }

    @Override
    public void onDownload(float progress) {
        buttonProgress.setProgress(progress);
    }

    @Override
    public void onFinish(File file) {
        if (file != null) {
            photo.setPath(file.getAbsolutePath());
            networkIcon.getResource().setImage(photo.getPath());
        }
        buttonProgress.setProgress(0);
        buttonProgress.setVisible(false);
    }

    @Override
    public void onError(Exception e) {
        buttonProgress.setProgress(0);
    }
}
