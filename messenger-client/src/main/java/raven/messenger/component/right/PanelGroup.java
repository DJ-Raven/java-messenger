package raven.messenger.component.right;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.messenger.api.exception.ResponseException;
import raven.messenger.component.chat.AutoWrapText;
import raven.messenger.manager.ErrorManager;
import raven.messenger.models.response.ModelGroup;
import raven.messenger.models.response.ModelMember;
import raven.messenger.plugin.swing.scroll.ScrollRefresh;
import raven.messenger.plugin.swing.scroll.ScrollRefreshModel;
import raven.messenger.socket.SocketService;
import raven.messenger.util.ComponentUtil;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class PanelGroup extends JPanel {

    private final NumberFormat numberFormat = new DecimalFormat("#,##0");

    public ModelGroup getData() {
        return data;
    }

    private ModelGroup data;

    public PanelGroup() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("wrap,fillx,insets 15 0 15 5,hidemode 3", "[fill]", "[shrinkprio 1][shrinkprio 2][shrinkprio 3]"));
        headerProfile = new HeaderProfile();

        add(headerProfile);
        createDescription();
        createMember();
    }

    private void createDescription() {
        panelDescription = new JPanel(new MigLayout("wrap,fillx,insets 0", "[fill]"));
        ComponentUtil.addSeparatorTo(panelDescription);
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setEditorKit(new AutoWrapText());

        textPane.putClientProperty(FlatClientProperties.STYLE, "" +
                "foreground:$Text.lowForeground;");
        panelDescription.add(textPane);
        add(panelDescription);
    }

    private void createMember() {
        panelMember = new JPanel(new MigLayout("wrap,fillx,insets 3,gapy 3", "[fill]"));
        ComponentUtil.addSeparatorTo(this);

        scrollRefresh = new ScrollRefresh(getScrollModel(), panelMember);
        scrollRefresh.setBorder(BorderFactory.createEmptyBorder());
        scrollRefresh.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollRefresh.getVerticalScrollBar().setUnitIncrement(10);
        scrollRefresh.getScrollRefreshModel().stop();

        scrollRefresh.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "width:3;");

        add(scrollRefresh);
    }

    private ScrollRefreshModel getScrollModel() {
        return new ScrollRefreshModel(1, SwingConstants.BOTTOM) {
            @Override
            public boolean onRefreshNext() {
                return loadData();
            }

            @Override
            public void onFinishRefresh() {
                repaint();
                revalidate();
            }

            @Override
            public void onFinishData() {

            }

            @Override
            public void onError(Exception e) {

            }
        };
    }

    public void setGroup(ModelGroup data) {
        this.data = data;
        headerProfile.setData(data.getProfile(), data.getName(), numberFormat.format(data.getTotalMember()) + " Members");
        textPane.setText(data.getDescription());
        panelDescription.setVisible(!data.getDescription().isEmpty());
        panelMember.removeAll();
        panelMember.repaint();
        panelMember.revalidate();
        scrollRefresh.getScrollRefreshModel().resetPage();
    }

    public boolean loadData() {
        try {
            List<ModelMember> response = SocketService.getInstance().getServiceGroup().getGroupMember(data.getGroupId(), scrollRefresh.getScrollRefreshModel().getPage());
            for (ModelMember d : response) {
                if (isNotExist(d)) {
                    ItemMember item = new ItemMember(d, d.getUserId() == data.getCreateBy());
                    panelMember.add(item);
                }
            }
            return !response.isEmpty();
        } catch (ResponseException e) {
            ErrorManager.getInstance().showError(e);
            return false;
        } finally {
            panelMember.repaint();
            panelMember.revalidate();
        }
    }

    private boolean isNotExist(ModelMember data) {
        int count = panelMember.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component component = panelMember.getComponent(i);
            if (component instanceof ItemMember) {
                ItemMember item = (ItemMember) component;
                if (item.getData().getUserId() == data.getUserId()) {
                    return false;
                }
            }
        }
        return true;
    }

    private ScrollRefresh scrollRefresh;
    private HeaderProfile headerProfile;
    private JPanel panelDescription;
    private JPanel panelMember;
    private JTextPane textPane;
}
