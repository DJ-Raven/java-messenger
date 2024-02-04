package raven.messenger.component.chat.item;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import raven.messenger.util.GraphicsUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;

public class ChatItem extends JPanel {

    protected final int space = 8;
    protected final int borderSize;
    protected final int type;
    protected int level;
    protected JLabel labelName;

    public ChatItem(int borderSize, int type) {
        this.borderSize = borderSize;
        this.type = type;
        init();
    }

    private void init() {
        String backgroundKey = type == 1 ? "$Chat.item.background" : "$Chat.item.myselfBackground";
        String borderKey = type == 1 ? (borderSize + "," + (space + borderSize) + "," + borderSize + "," + borderSize) : (borderSize + "," + borderSize + "," + borderSize + "," + (space + borderSize));
        putClientProperty(FlatClientProperties.STYLE, "" +
                "border:" + borderKey + ";" +
                "background:$Chat.background;" +
                "foreground:" + backgroundKey);
    }

    public void addUserName(String userName) {
        if (userName == null) {
            if (labelName != null) {
                remove(labelName);
                labelName = null;
            }
        } else {
            labelName = new JLabel(userName);
            labelName.putClientProperty(FlatClientProperties.STYLE, "" +
                    "border:0,5,0,5;" +
                    "foreground:$Component.accentColor;" +
                    "font:bold");
            add(labelName, 0);
        }
    }

    @Override
    protected void paintChildren(Graphics g) {
        paintBackground(g, level);
        super.paintChildren(g);
    }

    protected void paintBackground(Graphics g, int level) {
        Graphics2D g2 = (Graphics2D) g.create();
        FlatUIUtils.setRenderingHints(g2);
        int x;
        int y = 0;
        int height = getHeight();
        int width;
        if (type == 1) {
            x = UIScale.scale(space);
            width = getWidth() - x;
        } else {
            x = 0;
            width = getWidth() - UIScale.scale(space);
        }
        Area area = new Area(GraphicsUtil.getShape(x, y, width, height, level, type, false, false));
        if (level == 0 || level == 3) {
            area.add(new Area(getArrow(x, y, width, height, type)));
        }
        g2.setColor(getForeground());
        g2.fill(area);
        g2.dispose();
    }

    private Shape getArrow(int x, int y, int width, int height, int type) {
        Path2D arrow = new Path2D.Double();
        int a = UIScale.scale(1);
        if (type == 1) {
            arrow.moveTo(0, height - a);
            arrow.curveTo(0, height - a, x, height - a, x, height - Math.min(UIScale.scale(15), height - UIScale.scale(15)));
            arrow.lineTo(x + getInsets().left, height);
            arrow.lineTo(0, height);
        } else if (type == 2) {
            int s = UIScale.scale(space);
            arrow.moveTo(width + space, height - a);
            arrow.curveTo(width + space, height - a, width, height - a, width, height - Math.min(UIScale.scale(15), height - UIScale.scale(15)));
            arrow.lineTo(width - getInsets().right, height);
            arrow.lineTo(width + space, height);
        }
        return arrow;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
