package raven.messenger.option.storage;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;
import raven.messenger.store.StoreManager;
import raven.messenger.util.ComponentUtil;
import raven.messenger.util.MethodUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.List;

public class DialogStorage extends JPanel {

    private final String[] colors = new String[]{"#60a5fa", "#2dd4bf", "#fbbf24", "#e879f9"};

    public DialogStorage() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill,wrap,insets 10 0 15 0", "fill", "[]20[][]"));
        chart = new Chart();
        panelDetail = new JPanel(new MigLayout("fill,wrap,gapy 0,insets 0 25 0 25", "fill"));
        add(chart, "height 150!");
        add(ComponentUtil.createInfoText("The application stores cached data from the server on your", "computer's storage."));
        add(panelDetail);
        add(ComponentUtil.createInfoText("Here are the total file size and details of the stored data.", "You have the option to clear this data."));
        updateDate();
    }

    private void updateDate() {
        List<StoreManager.ModelFile> data = StoreManager.getInstance().getStorageInfo();
        chart.setData(data);
        panelDetail.removeAll();
        for (int i = 0; i < data.size(); i++) {
            panelDetail.add(new Item(data.get(i), Color.decode(colors[i]), i));
        }
        panelDetail.repaint();
        panelDetail.revalidate();
    }

    private Chart chart;
    private JPanel panelDetail;

    private class Chart extends JPanel {

        private List<StoreManager.ModelFile> data;

        public Chart() {
            init();
        }

        private void init() {
            setLayout(new MigLayout("al center center"));
            putClientProperty(FlatClientProperties.STYLE, "" +
                    "background:null");
            labelName = new JLabel();
            labelName.putClientProperty(FlatClientProperties.STYLE, "" +
                    "font:bold +3;" +
                    "foreground:$Text.upperForeground");
            add(labelName);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            FlatUIUtils.setRenderingHints(g2);
            Insets insets = getInsets();
            int width = getWidth() - (insets.left + insets.right);
            int height = getHeight() - (insets.top + insets.bottom);
            int size = Math.min(width, height);
            int x = insets.left + (getWidth() - size) / 2;
            int y = insets.top + (getHeight() - size) / 2;

            double maxValue = getMaxFileSize();
            Area areaCut = createAreaCut(size, size, size * 0.7f);
            g2.translate(x, y);
            if (maxValue > 0) {
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                float start = 90;
                for (int i = 0; i < data.size(); i++) {
                    float percent = (float) (data.get(i).getSize() / maxValue);
                    float angle = percent * 360f;
                    Area area = new Area(new Arc2D.Double(0, 0, size, size, start, -angle, Arc2D.PIE));
                    area.subtract(areaCut);
                    g2.setColor(Color.decode(colors[i]));
                    g2.fill(area);
                    start -= angle;
                }
            } else {
                Area area = new Area(new Arc2D.Double(0, 0, size, size, 0, 360, Arc2D.PIE));
                area.subtract(areaCut);
                g2.setColor(getEmptyColor());
                g2.fill(area);
            }
            g2.dispose();
        }

        private Area createAreaCut(int width, int height, float size) {
            float x = (width - size) / 2;
            float y = (height - size) / 2;
            return new Area(new Ellipse2D.Double(x, y, size, size));
        }

        private Color getEmptyColor() {
            if (FlatLaf.isLafDark()) {
                return ColorFunctions.lighten(UIManager.getColor("Panel.background"), 0.05f);
            } else {
                return ColorFunctions.darken(UIManager.getColor("Panel.background"), 0.05f);
            }
        }

        public void setData(List<StoreManager.ModelFile> data) {
            this.data = data;
            double maxSize = getMaxFileSize();
            if (maxSize > 0) {
                labelName.setText(MethodUtil.formatSize((long) getMaxFileSize()));
            } else {
                labelName.setText("No data");
            }
            repaint();
        }

        public double getMaxFileSize() {
            double maxValue = 0;
            for (int i = 0; i < data.size(); i++) {
                maxValue += data.get(i).getSize();
            }
            return maxValue;
        }

        private JLabel labelName;
    }

    private class Item extends JPanel {

        private final StoreManager.ModelFile file;
        private final Color color;
        private final int index;

        public Item(StoreManager.ModelFile file, Color color, int index) {
            this.file = file;
            this.color = color;
            this.index = index;
            init();
        }

        private void init() {
            setLayout(new MigLayout("gap 8 0,insets 5"));
            add(new LabelColor(MethodUtil.createIcon("raven/messenger/icon/chart/" + file.getName().toLowerCase() + ".svg", 0.5f, color), color), "span 1 2");
            JLabel labelName = new JLabel(file.getQty() + " " + file.getName());
            JLabel labelSize = new JLabel(MethodUtil.formatSize(file.getSize()));
            JButton buttonClear = new JButton("Clear");
            if (file.getSize() == 0) {
                buttonClear.setEnabled(false);
            }
            buttonClear.addActionListener(e -> {
                StoreManager.getInstance().clearData(index);
                updateDate();
            });
            buttonClear.putClientProperty(FlatClientProperties.STYLE, "" +
                    "background:null;" +
                    "foreground:$Text.upperForeground;" +
                    "margin:5,10,5,10;" +
                    "borderWidth:0;" +
                    "focusWidth:0;" +
                    "innerFocusWidth:0;" +
                    "disabledBackground:null");
            labelName.putClientProperty(FlatClientProperties.STYLE, "" +
                    "foreground:$Text.upperForeground");
            labelSize.putClientProperty(FlatClientProperties.STYLE, "" +
                    "foreground:$Text.lowForeground;" +
                    "font:-2");
            add(labelName, "cell 1 0");
            add(labelSize, "cell 1 1,push");
            add(buttonClear, "al trailing,cell 2 0,span 1 2,gap 8 8 8 8");
        }
    }

    private class LabelColor extends JLabel {

        public LabelColor(Icon icon, Color color) {
            super(icon);
            putClientProperty(FlatClientProperties.STYLE, "" +
                    "border:4,4,4,4");
            setBackground(color);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            FlatUIUtils.setRenderingHints(g2);
            int arc = UIScale.scale(10);
            Color color = FlatLaf.isLafDark() ? ColorFunctions.lighten(getParent().getBackground(), 0.05f) : ColorFunctions.darken(getParent().getBackground(), 0.05f);
            g2.setPaint(new GradientPaint(0, 0, color, 0, getHeight(), getBackground()));
            g2.setComposite(AlphaComposite.SrcOver.derive(0.5f));
            FlatUIUtils.paintComponentBackground(g2, 0, 0, getWidth(), getHeight(), 0, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
