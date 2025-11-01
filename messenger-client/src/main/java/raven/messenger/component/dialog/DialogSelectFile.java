package raven.messenger.component.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.component.LabelRound;
import raven.messenger.component.PictureBox;
import raven.messenger.component.ScrollForTextArea;
import raven.messenger.component.chat.AutoWrapText;
import raven.messenger.component.chat.TextPaneCustom;
import raven.messenger.manager.ErrorManager;
import raven.messenger.models.file.FileType;
import raven.messenger.models.file.ModelFileWithType;
import raven.messenger.util.MethodUtil;
import raven.modal.ModalDialog;
import raven.modal.component.ModalBorderAction;
import raven.modal.component.SimpleModalBorder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class DialogSelectFile extends JPanel {

    private final File[] files;
    private final String message;
    private final int insetRight = 15;

    public DialogSelectFile(File[] files, String message) {
        this.files = files;
        this.message = message;
        init();
        loadFiles();
    }

    public void open() {
        txtCaption.grabFocus();
    }

    private void init() {
        setLayout(new MigLayout("fill,wrap,insets 5 30 5 15", "[fill]", "[shrinkprio 2,100::]15[][shrinkprio 1]"));
        JPanel panel = new JPanel(new MigLayout("fill,insets 0 0 0 5", "[fill][grow 0]", "fill"));
        panelFiles = new JPanel(new MigLayout("fillx,wrap,insets 0", "fill"));
        panelFiles.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;");
        scrollPane = new JScrollPane(panelFiles);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "width:5;" +
                "background:null;");
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
        txtCaption.putClientProperty(FlatClientProperties.STYLE, "" +
                "margin:5,5,5,5;" +
                "background:$Item.component.background;");
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
                    ModalBorderAction modalBorderAction = ModalBorderAction.getModalBorderAction(DialogSelectFile.this);
                    if (modalBorderAction != null) {
                        modalBorderAction.doAction(SimpleModalBorder.OK_OPTION);
                    }
                }
            }
        });
        JPanel inputPanel = new JPanel(new MigLayout("fill,wrap,insets 0", "fill", "fill,22::110"));
        inputPanel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null;");
        JScrollPane scroll = new ScrollForTextArea(txtCaption);
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
        JButton cmdDelete = new JButton(MethodUtil.createIcon("raven/messenger/icon/close.svg", 0.2f));
        cmdDelete.setFocusable(false);
        cmdDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmdDelete.putClientProperty(FlatClientProperties.STYLE_CLASS, "myButton");
        cmdDelete.putClientProperty(FlatClientProperties.STYLE, "" +
                "[dark]background:tint(@background,5%);" +
                "[light]background:shade(@background,5%);");
        cmdDelete.addActionListener(e -> {
            if (panelFiles.getComponentCount() > 1) {
                panelFiles.remove(component);
                panelFiles.repaint();
                revalidate();
            } else {
                ModalDialog.closeModal("select_file");
            }
        });
        return cmdDelete;
    }

    public ModelFileWithType[] getSelectedFiles() {
        try {
            int count = panelFiles.getComponentCount();
            ModelFileWithType[] files = new ModelFileWithType[count];
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
            setLayout(new MigLayout("fill,gapy 0,insets n 0 n n", "[grow 0,50,fill][fill][grow 0]", "fill"));

            LabelRound lbIcon = new LabelRound(MethodUtil.createIcon("raven/messenger/icon/file.svg", 0.35f));
            JLabel lbName = new JLabel(file.getName());
            JLabel lbSize = new JLabel(MethodUtil.formatSize(file.length()));
            lbIcon.putClientProperty(FlatClientProperties.STYLE, "" +
                    "[dark]background:tint(@background,10%);" +
                    "[light]background:shade(@background,10%);");
            lbSize.putClientProperty(FlatClientProperties.STYLE, "" +
                    "foreground:$Text.middleForeground;");
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
