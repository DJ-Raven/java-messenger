package raven.messenger.component.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.component.LabelRound;
import raven.messenger.component.PictureBox;
import raven.messenger.component.chat.AutoWrapText;
import raven.messenger.component.chat.TextPaneCustom;
import raven.messenger.manager.DialogManager;
import raven.messenger.manager.ErrorManager;
import raven.messenger.models.file.FileType;
import raven.messenger.models.file.ModelFileWithType;
import raven.messenger.util.MethodUtil;
import raven.popup.component.GlassPaneChild;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class DialogSelectFile extends GlassPaneChild {

    private final File[] files;
    private final String message;
    private final int insetRight = 15;

    public DialogSelectFile(File[] files, String message) {
        this.files = files;
        this.message = message;
        init();
        loadFiles();
    }

    private void init() {
        setLayout(new MigLayout("fill,wrap,insets 5 25 5 10", "fill", "[shrinkprio 2,100::]15[][shrinkprio 1]"));
        JPanel panel = new JPanel(new MigLayout("fill,insets 0", "[fill][grow 0]", "fill"));
        panelFiles = new JPanel(new MigLayout("fillx,wrap,insets 0", "fill"));
        panelFiles.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        scrollPane = new JScrollPane(panelFiles);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:0,0,0,0");
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        panel.add(scrollPane);
        panel.add(scrollPane.getVerticalScrollBar());
        add(panel);
        createAction();
        createInput();
    }

    private void createAction() {
        JPanel panel = new JPanel(new MigLayout("insets 0,wrap"));
        chCompressImage = new JCheckBox("Compress the image");
        chCompressImage.setFocusable(false);

        /* Update next
        if (isHaveImage()) {
            panel.add(chCompressImage, "grow 0");
        }
         */

        panel.add(new JLabel("Caption"), "gap 3");
        add(panel);
    }

    private void createInput() {
        txtCaption = new TextPaneCustom();
        txtCaption.setEditorKit(new AutoWrapText());
        txtCaption.setText(message);
        txtCaption.setPlaceholderText("Write a caption...");
        txtCaption.getDocument().addDocumentListener(new DocumentListener() {
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
        txtCaption.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == 10 && e.isControlDown()) {
                    callbackAction.action(createController(), 1);
                }
            }
        });
        JPanel inputPanel = new JPanel(new MigLayout("fill,wrap,insets 3", "fill", "fill,22::110"));
        inputPanel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:10;" +
                "background:$TextField.background");

        JScrollPane scroll = new JScrollPane(txtCaption);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:0,0,0,0");
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "width:3");
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        inputPanel.add(scroll);
        add(inputPanel, "gap 0 " + (insetRight));
    }

    private void loadFiles() {
        for (File file : files) {
            String fileName = file.getName();
            boolean isImage = MethodUtil.isImageFile(fileName);
            if (isImage) {
                ItemPhoto pictureBox = new ItemPhoto(file);
                pictureBox.setImage(new ImageIcon(file.getAbsolutePath()));
                pictureBox.setBoxFit(PictureBox.BoxFit.COVER);
                pictureBox.setRadius(8);
                panelFiles.add(pictureBox, "height 120");
            } else {
                ItemFile itemFile = new ItemFile(file);
                panelFiles.add(itemFile);
            }
        }
    }

    private boolean isHaveImage() {
        for (File file : files) {
            if (MethodUtil.isImageFile(file.getName())) {
                return true;
            }
        }
        return false;
    }

    private JButton createDeleteButton(Component component) {
        JButton cmdDelete = new JButton(MethodUtil.createIcon("raven/messenger/icon/delete.svg", 0.6f));
        cmdDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmdDelete.putClientProperty(FlatClientProperties.STYLE_CLASS, "myButton");
        cmdDelete.putClientProperty(FlatClientProperties.STYLE, "" +
                "[dark]background:lighten(@background,3%);" +
                "[light]background:darken(@background,3%)");
        cmdDelete.addActionListener(e -> {
            if (panelFiles.getComponentCount() > 1) {
                panelFiles.remove(component);
                panelFiles.repaint();
                revalidate();
            } else {
                DialogManager.getInstance().closeLast();
            }
        });
        return cmdDelete;
    }

    public ModelFileWithType[] getSelectedFiles() {
        try {
            int count = panelFiles.getComponentCount();
            ModelFileWithType files[] = new ModelFileWithType[count];
            for (int i = 0; i < count; i++) {
                Component com = panelFiles.getComponent(i);
                if (com instanceof ItemPhoto) {
                    files[i] = new ModelFileWithType(MethodUtil.compressImage(((ItemPhoto) com).getFile()), FileType.PHOTO);
                } else if (com instanceof ItemFile) {
                    files[i] = new ModelFileWithType(((ItemFile) com).getFile(), FileType.FILE);
                }
            }
            return files;
        } catch (IOException e) {
            ErrorManager.getInstance().showError(e);
        }
        return null;
    }

    public String getMessage() {
        return txtCaption.getText().trim();
    }

    private JPanel panelFiles;
    private JScrollPane scrollPane;
    private JCheckBox chCompressImage;
    private TextPaneCustom txtCaption;

    private class ItemFile extends JPanel {

        public File getFile() {
            return file;
        }

        private final File file;

        public ItemFile(File file) {
            this.file = file;
            init();
        }

        private void init() {
            setLayout(new MigLayout("fill,gapy 0", "[grow 0,50,fill][fill][grow 0]", "fill"));

            LabelRound lbIcon = new LabelRound(MethodUtil.createIcon("raven/messenger/icon/file.svg", 1f));
            JLabel lbName = new JLabel(file.getName());
            JLabel lbSize = new JLabel(MethodUtil.formatSize(file.length()));
            lbIcon.putClientProperty(FlatClientProperties.STYLE, "" +
                    "[dark]background:lighten(@background,10%);" +
                    "[light]background:darken(@background,10%)");
            lbSize.putClientProperty(FlatClientProperties.STYLE, "" +
                    "foreground:$Text.lowForeground");
            JButton cmdDelete = createDeleteButton(this);
            add(lbIcon, "span 1 2");
            add(lbName, "cell 1 0,width ::245");
            add(lbSize, "cell 1 1");
            add(cmdDelete, "cell 2 0");
        }
    }

    private class ItemPhoto extends PictureBox {

        public File getFile() {
            return file;
        }

        private final File file;

        public ItemPhoto(File file) {
            this.file = file;
            init();
        }

        private void init() {
            setLayout(new MigLayout("fill", "trailing", "top"));
            JButton cmdDelete = createDeleteButton(this);
            add(cmdDelete);
        }
    }
}
